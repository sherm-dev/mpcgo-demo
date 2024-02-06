package com.shermdev.will.mpcgo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.room.Room;

import java.util.ArrayList;

/**
 * Created by Will on 3/5/2017.
 */

public class MPCDatabaseManager{
    public static final String SOUNDBANK_LIST_NAMES_KEY = "soundbank_list_key";
    private static final String DATABASE_NAME = "mpc_database";
    private final Handler soundBankManagerHandler;
    private final MPCSamplerDatabase database;

    public MPCDatabaseManager(Context context, Handler soundBankManagerHandler) {
        this.soundBankManagerHandler = soundBankManagerHandler;
        this.database = Room.databaseBuilder(context, MPCSamplerDatabase.class, DATABASE_NAME).build();
    }

    public void createSoundBank(SoundBank soundBank){
        SoundBankEditorAsync sbEditAsync = new SoundBankEditorAsync(this.database, this.soundBankManagerHandler, false);
        sbEditAsync.execute(soundBank);
    }

    public void editSoundBank(SoundBank soundBank){
        SoundBankEditorAsync sbEditAsync = new SoundBankEditorAsync(this.database, this.soundBankManagerHandler, true);
        sbEditAsync.execute(soundBank);
    }

    public void deleteSoundBank(SoundBank soundBank){
        SoundBankDeleteAsync sbDeleteAsync = new SoundBankDeleteAsync(this.database, this.soundBankManagerHandler);
        sbDeleteAsync.execute(soundBank);
    }

    public void retrieveSoundBank(String soundBankName){
        SoundBankRetrieveAsync sbRetrieveAsync = new SoundBankRetrieveAsync(this.database, this.soundBankManagerHandler);
        sbRetrieveAsync.execute(soundBankName);
    }

    public void retrieveSoundBanks(){
        SoundBankListAsync sbListAsync = new SoundBankListAsync(this.database, this.soundBankManagerHandler);
        sbListAsync.execute();
    }

    public void updateSample(Sample sample, String soundBankName){
        SampleEditorAsync sampleEditorAsync = new SampleEditorAsync(database, soundBankManagerHandler, soundBankName);
        sampleEditorAsync.execute(sample);
    }

    public void soundBankExists(String name){
        SoundBankSearchByNameAsync soundBankSearchByNameAsync = new SoundBankSearchByNameAsync(database, soundBankManagerHandler);
        soundBankSearchByNameAsync.execute(name);
    }

    private static class SoundBankSearchByNameAsync extends AsyncTask<String, Void, String>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;

        public SoundBankSearchByNameAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
        }

        @Override
        protected String doInBackground(String... strings) {
            if(_database.soundBankEntityDao().soundBankCount(strings[0]) > 0){
                return strings[0];
            }else{
                return null;
            }
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            _soundBankManagerHandler.sendMessage(_soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_EXISTS, name));
        }
    }

    private static class SoundBankListAsync extends AsyncTask<Void, Void, String[]>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;

        public SoundBankListAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return _database.soundBankEntityDao().listSoundBanks();
        }

        @Override
        protected void onPostExecute(String[] soundBankNames) {
            super.onPostExecute(soundBankNames);
            Bundle bundle = new Bundle();
            ArrayList<String> soundbanks = new ArrayList<>();

            for(int i = 0; i < soundBankNames.length; i++){
                soundbanks.add(soundBankNames[i]);
            }

            bundle.putStringArrayList(SOUNDBANK_LIST_NAMES_KEY, soundbanks);
            Message msg = _soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_LIST);
            msg.setData(bundle);
            _soundBankManagerHandler.sendMessage(msg);
        }
    }

    private static class SoundBankRetrieveAsync extends AsyncTask<String, Void, SoundBank>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;

        public SoundBankRetrieveAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
        }

        private SoundBank createSoundBankInstance(String soundBankName){
            return new SoundBank(soundBankName, SoundBank.samplesFromSampleEntities(_database.sampleEntityDao().retrieveSoundBankSamplesByName(soundBankName)));
        }

        @Override
        protected SoundBank doInBackground(String... soundBankNames) {
            return createSoundBankInstance(soundBankNames[0]);
        }

        @Override
        protected void onPostExecute(SoundBank soundBank) {
            super.onPostExecute(soundBank);
            _soundBankManagerHandler.sendMessage(_soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_RETRIEVED, soundBank));
        }
    }

    private static class SampleEditorAsync extends AsyncTask<Sample, Void, Sample>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;
        private final String soundBankName;

        public SampleEditorAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler, String soundBankName) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
            this.soundBankName = soundBankName;
        }

        @Override
        protected Sample doInBackground(Sample... samples) {
            int soundBankId = _database.soundBankEntityDao().soundBankIdByName(soundBankName);
            _database.sampleEntityDao().updateSampleEntity(new SampleEntity(samples[0].getSampleName(), samples[0].getSampleFilePath(), samples[0].getSampleOrder(), soundBankId));
            return samples[0];
        }

        @Override
        protected void onPostExecute(Sample sample) {
            super.onPostExecute(sample);
            _soundBankManagerHandler.sendMessage(_soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SAMPLE_UPDATED, sample));
        }
    }

    private static class SoundBankEditorAsync extends AsyncTask<SoundBank, Void, SoundBank>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;
        private final boolean update;

        public SoundBankEditorAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler, boolean update) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
            this.update = update;
        }

        private void editSoundBank(SoundBankEntity sbe){
            if (this.update || _database.soundBankEntityDao().soundBankCount(sbe.getName()) > 0) {
                _database.soundBankEntityDao().updateSoundBank(sbe);
            } else {
                _database.soundBankEntityDao().newSoundBank(sbe);
            }
        }

        private void editSample(SampleEntity se){
            if(this.update){
                _database.sampleEntityDao().updateSampleEntity(se);
            }else{
                _database.sampleEntityDao().insertSampleEntity(se);
            }
        }

        private void deleteSample(SampleEntity se){
            _database.sampleEntityDao().deleteSampleEntity(se);
        }

        private void updateDatabase(SoundBank sb){
            int sampleCounter = 0;
            int soundBankId = _database.soundBankEntityDao().soundBankIdByName(sb.getName());
            SampleEntity[] sampleEntities = _database.sampleEntityDao().retrieveSoundBankSamplesById(soundBankId);

            if(sb.getSamples().size() > 0){
                for(Sample sample : sb.getSamples()){
                    SampleEntity se = new SampleEntity(sample.getSampleName(), sample.getSampleFilePath(), sample.getSampleOrder(), soundBankId);
                    if(sampleEntities.length > 0 && sampleEntities[sampleCounter] != null) deleteSample(se);
                    editSample(se);
                    sampleCounter++;
                }
            }
        }

        @Override
        protected SoundBank doInBackground(SoundBank... soundBank) {
            editSoundBank(new SoundBankEntity(soundBank[0].getName()));
            updateDatabase(soundBank[0]);

            return soundBank[0];
        }

        @Override
        protected void onPostExecute(SoundBank soundBank) {
            super.onPostExecute(soundBank);
            _soundBankManagerHandler.sendMessage(
                    _soundBankManagerHandler.obtainMessage(
                            this.update ?
                                    SoundBankManager.MSG_SOUNDBANK_EDITED :
                                    SoundBankManager.MSG_SOUNDBANK_CREATED,
                            soundBank
                    )
            );
        }
    }

    private static class SoundBankDeleteAsync extends AsyncTask<SoundBank, Void, SoundBank>{
        private final MPCSamplerDatabase _database;
        private final Handler _soundBankManagerHandler;

        public SoundBankDeleteAsync(MPCSamplerDatabase _database, Handler _soundBankManagerHandler) {
            this._database = _database;
            this._soundBankManagerHandler = _soundBankManagerHandler;
        }

        @Override
        protected SoundBank doInBackground(SoundBank... soundBank) {
            int soundBankId = _database.soundBankEntityDao().soundBankIdByName(soundBank[0].getName());
            SampleEntity[] sampleEntities = _database.sampleEntityDao().retrieveSoundBankSamplesById(soundBankId);

            for(int i = 0; i < sampleEntities.length; i++){
                _database.sampleEntityDao().deleteSampleEntity(sampleEntities[i]);
            }

            _database.soundBankEntityDao().deleteSoundBank(new SoundBankEntity(soundBank[0].getName()));

            return soundBank[0];
        }

        @Override
        protected void onPostExecute(SoundBank soundBank) {
            super.onPostExecute(soundBank);
            _soundBankManagerHandler.sendMessage(_soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_DELETED, soundBank));
        }
    }
}
