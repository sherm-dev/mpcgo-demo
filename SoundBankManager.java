package com.shermdev.will.mpcgo;



import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Will on 3/5/2017.
 */

public class SoundBankManager{
    public static final int MSG_SOUNDBANK_LOADED = 5211;
    public static final int MAX_SAMPLE_COUNT = 12;
    public static final String DEFAULT_SOUNDBANK_NAME = "default";
    public static final String NEW_SOUNDBANK_NAME = "New SoundBank";
    public static final int MSG_SOUNDBANK_CREATED = 989662;
    public static final int MSG_SOUNDBANK_EDITED = 989663;
    public static final int MSG_SOUNDBANK_DELETED = 989664;
    public static final int MSG_SOUNDBANK_RETRIEVED = 989665;
    public static final int MSG_SOUNDBANK_LIST = 989666;
    public static final int MSG_SAMPLE_UPDATED = 989667;
    public static final int MSG_SOUNDBANK_EXISTS = 989668;
    private final MPCDatabaseManager databaseHelper;
    private final Handler soundbankHandler;
    private final SoundPoolManager soundPoolManager;
    private final Context context;
    private SoundBank soundBank;

    public SoundBankManager(Context context, Handler soundbankHandler){
        this.soundBank = null;
        this.soundPoolManager = new SoundPoolManager(context);
        this.soundbankHandler = soundbankHandler;
        this.context = context;
        this.databaseHelper = new MPCDatabaseManager(context, soundbankHandler);
    }

    //TODO: account for fact that initialization may not work, new external storage card loaded, etc. Check for files transferred, existence in database, etc before loading
    public void soundBankLaunch(){
        Log.i("MPCGO", "Init Default LoadSavedorDefault");
        if(getSoundBank() != null){
            loadSoundBank(getSoundBank());
        }else{
            SampleFileTransferTask sampleFileTransferTask = new SampleFileTransferTask(soundbankHandler, context);
            sampleFileTransferTask.start();
        }
    }

    public void loadSoundBank(SoundBank sb){
        setSoundBank(sb);
        SoundBankLoaderTask soundBankLoaderTask = new SoundBankLoaderTask(
                this,
                soundbankHandler,
                context
        );

        soundBankLoaderTask.start();
    }

    public static List<String> defaultSoundBankOrder(){
        return Arrays.asList(
                "deep_kick.mp3",
                "glitch_kick.mp3",
                "hard_snare.mp3",
                "clap.mp3",
                "glitch_cymbal.mp3",
                "glitch_perc.mp3",
                "t8hhclosed.mp3",
                "t8hhopen.mp3",
                "lofi_hh.mp3",
                "crash.mp3",
                "live_tom1.mp3",
                "live_tom2.mp3");
    }

    protected void setSoundBank(SoundBank soundBank) {
        this.soundBank = soundBank;
    }

    public SoundBank getSoundBank() {
        return soundBank;
    }

    public SoundPoolManager getSoundPoolManager() {
        return soundPoolManager;
    }

    public MPCDatabaseManager getDatabaseHelper() {
        return databaseHelper;
    }
}
