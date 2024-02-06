package com.shermdev.will.mpcgo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class SoundBankLoaderTask extends Thread {
    private final SoundBankManager soundBankManager;
    private final Handler soundBankManagerHandler;
    private final Context context;

    public SoundBankLoaderTask(SoundBankManager soundBankManager, Handler soundBankManagerHandler, Context context) {
        this.soundBankManager = soundBankManager;
        this.soundBankManagerHandler = soundBankManagerHandler;
        this.context = context;
    }

    private void loadSampleFile(Sample sample, SampleAudioDecoder sampleAudioDecoder) throws IOException {
        Log.i("MPCGO", "Sample File Load Name: " + sample.getSampleName());

        File file = new File(sample.getSampleFilePath());
        FileDescriptor fd;

        if (file.exists() && file.length() > 0) {
            FileInputStream fis = new FileInputStream(file);
            fd = fis.getFD();
            sampleAudioDecoder.decodeSample(sample, fis, context);
            fis.close();
        }
    }

    @Override
    public void run() {
        super.run();
        SampleAudioDecoder sampleAudioDecoder = new SampleAudioDecoder();


                if(soundBankManager.getSoundBank().getSamples().size() > 0){
                    for (Sample sample : soundBankManager.getSoundBank().getSamples()) {

                      /* try {
                            loadSampleFile(sample, sampleAudioDecoder);
                        } catch (IOException e) {
                            Log.i("MPCGO", "SoundBank Loader Exception " + e.getMessage());
                            e.printStackTrace();
                        }*/

                        soundBankManager.getSoundPoolManager().getSoundPool().load(sample.getSampleFilePath(),1);
                    }
                }

                soundBankManagerHandler.sendMessage(soundBankManagerHandler.obtainMessage(SoundBankManager.MSG_SOUNDBANK_LOADED, soundBankManager.getSoundBank()));

    }
}
