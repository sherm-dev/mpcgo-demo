package com.shermdev.will.mpcgo;

import android.content.ContentResolver;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

public class SoundBankLoaderAsyncTask extends AsyncTask<SoundBank, Void, SoundBank> {
    private final SoundPool soundPool;
    private final Handler soundBankManagerHandler;
    private final ContentResolver contentResolver;

    public SoundBankLoaderAsyncTask(SoundPool soundPool, ContentResolver contentResolver, Handler soundBankManagerHandler) {
        this.soundPool = soundPool;
        this.contentResolver = contentResolver;
        this.soundBankManagerHandler = soundBankManagerHandler;
    }

    private void loadSampleFile(Sample sample, SampleAudioDecoder sampleAudioDecoder) throws IOException {
        Log.i("FILEDESC", "File Desc");
        ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(Uri.parse(sample.getSampleFilePath()), "r");

        if(pfd != null && pfd.getStatSize() > 0){
            int loadId = soundPool.load(pfd.getFileDescriptor(), 0, pfd.getStatSize(), 1);
            sampleAudioDecoder.decodeSample(sample, pfd.getFileDescriptor());
            sample.setSampleOrder(loadId);
            sample.setLoaded(true);
        }
    }

    @Override
    protected SoundBank doInBackground(SoundBank... soundBanks) {
        SampleAudioDecoder sampleAudioDecoder = new SampleAudioDecoder();

        try {
            for(Sample sample : soundBanks[soundBanks.length - 1].getSamples()){
                loadSampleFile(sample, sampleAudioDecoder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return soundBanks[0];
    }

    @Override
    protected void onPostExecute(SoundBank soundBank) {
        super.onPostExecute(soundBank);
        soundBankManagerHandler.sendMessage(soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_LOADED, soundBank));
    }
}
