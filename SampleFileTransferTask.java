package com.shermdev.will.mpcgo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleFileTransferTask extends Thread{
    private static final String ASSETS_DEFAULT_SAMPLES_DIR = "default";
    public static final int DEFAULT_SOUNDBANK_FILES_TRANSFERRED = 888765;
    public static final String SAMPLES_DIR = "MPCSamples";
    public static final String DEFAULT_SOUNDBANK_DIR = "mpc_default";
    private final Handler soundBankManagerHandler;
    private final Context context;

    public SampleFileTransferTask(Handler soundBankManagerHandler, Context context){
        this.soundBankManagerHandler = soundBankManagerHandler;
        this.context = context;
    }

    private String[] retrieveDefaultFileNames() throws IOException{
        return context.getAssets().list(ASSETS_DEFAULT_SAMPLES_DIR);
    }

    private boolean createDestinationDirectory(){
        File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), SAMPLES_DIR);
        Log.i("MPCGO", String.valueOf("DEST Dirs: ").concat(destDir.getAbsolutePath()));
        if(!destDir.exists()){
            return destDir.mkdir();
        }else{
            return true;
        }
    }

    private ArrayList<Sample> copyFilesToDestination() throws IOException{
        ArrayList<Sample> samples = new ArrayList<>();

            Log.i("MPCGO", "COPY FILES TO DESTINATION");
            final String[] names = retrieveDefaultFileNames();
            final List<String> samplesOrder = SoundBankManager.defaultSoundBankOrder();
            File musicDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), SAMPLES_DIR);

            if(names.length > 0){
                for (String name : names) {
                    File destFile = new File(musicDir, name);

                    if (destFile.createNewFile() && destFile.setWritable(true)) {
                        Log.i("MPCGO", String.valueOf("DEST FILE: ").concat(destFile.getAbsolutePath()));

                        AssetManager.AssetInputStream is = (AssetManager.AssetInputStream) context.getAssets().open("default/" + name);
                        FileOutputStream fos = new FileOutputStream(destFile);

                        if (is.available() != 0) {
                            Log.i("MPCGO", "Bytes Available: " + is.available());

                            while (is.available() != 0) {
                                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(is.available());
                                int bytesRead = is.read(byteBuffer.array());

                                if (bytesRead != -1) {
                                    if (byteBuffer.hasArray()) fos.write(byteBuffer.array());
                                    Log.i("MPCGO", "Bytes Read: " + String.valueOf(bytesRead));
                                }
                            }

                            is.close();
                            fos.close();

                            samples.add(
                                    new Sample(
                                            destFile.getName().substring(
                                                    0,
                                                    destFile.getName().lastIndexOf(".")
                                            ),
                                            destFile.getAbsolutePath(),
                                            samplesOrder.indexOf(name)
                                    )
                            );
                            Log.i("MPCGO", String.valueOf("File Added: ").concat(destFile.getAbsolutePath()));
                        }
                    }
                }
            }



        return samples;
    }

    private SoundBank defaultSoundBank() throws IOException{
        Log.i("MPCGO", "Transfer Sample Files");
        ArrayList<Sample> samples = copyFilesToDestination();
        return new SoundBank(SoundBankManager.DEFAULT_SOUNDBANK_NAME, samples);
    }

    private boolean isExternalMusicDirectoryMounted(){
        return Environment.getExternalStorageState(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        ).equals(Environment.MEDIA_MOUNTED);
    }

    private SoundBank transferSampleFilesToStorage(){

        try{
            if (isExternalMusicDirectoryMounted()
                    && createDestinationDirectory()) {

                return defaultSoundBank();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MPCGO", e.getMessage());
        }

        return null;
    }

    @Override
    public void run() {
        super.run();
        Log.i("MPCGO", "Transfer Sample Files - Create Dest Dir");
        SoundBank sb = transferSampleFilesToStorage();
        soundBankManagerHandler.sendMessage(soundBankManagerHandler.obtainMessage(DEFAULT_SOUNDBANK_FILES_TRANSFERRED, sb));
    }
}