package com.shermdev.will.mpcgo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SampleFileManagerAsync extends AsyncTask<Context, Void, SoundBank>{
    public static final int DEFAULT_SOUNDBANK_FILES_TRANSFERRED = 888765;
    private static final String ASSETS_DEFAULT_SAMPLES_DIR = "default";
    public static final String SAMPLES_DIR = "MPC Samples";
    public static final String DEFAULT_SOUNDBANK_DIR = "mpc_default";

    private final Handler soundBankManagerHandler;

    public SampleFileManagerAsync(Handler soundBankManagerHandler){
        this.soundBankManagerHandler = soundBankManagerHandler;
    }

    private ArrayList<AssetFileDescriptor> retrieveSampleAssetFiles(final Context context, String[] names) throws IOException {
        ArrayList<AssetFileDescriptor> defaultAssets = new ArrayList<>();

        if(names.length > 0){
            for (String name : names) {
                Log.i("MPCGO", String.valueOf("NAME: ").concat(name));
                defaultAssets.add(context.getAssets().openFd(name));
            }
        }

        return defaultAssets;
    }

    private String[] retrieveDefaultNames(final Context context) throws IOException{
        return context.getAssets().list(ASSETS_DEFAULT_SAMPLES_DIR);
    }

    private boolean createDestinationDirectory(final Context context){
        File destDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), SAMPLES_DIR);

        if(!destDir.exists())
            return destDir.mkdir();

        return true;
    }

    private ArrayList<String> copyFilesToDestination(ArrayList<AssetFileDescriptor> sampleFileDescriptors, final Context context){
        int fileCounter = 0;
        ArrayList<String> paths = new ArrayList<>();

        if(sampleFileDescriptors.size() > 0){
            try{
                String[] names = retrieveDefaultNames(context);
                File musicDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), SAMPLES_DIR);
                Log.i("MPCGO", String.valueOf("Sample Names: ").concat(Arrays.toString(names)));

                for(AssetFileDescriptor afd : sampleFileDescriptors){
                    File destFile = new File(musicDir, names[fileCounter]);
                    Log.i("MPCGO", String.valueOf("DEST FILE: ").concat(destFile.getAbsolutePath()));

                    if(!destFile.exists() && destFile.createNewFile()){
                        FileInputStream fis = afd.createInputStream();
                        FileOutputStream fos = new FileOutputStream(destFile);

                        while(fis.available() != 0){
                            fos.write(fis.read());
                        }

                        fis.close();
                        fos.close();

                        paths.add(destFile.getAbsolutePath());
                        Log.i("MPCGO", String.valueOf("File Added: ").concat(destFile.getAbsolutePath()));
                    }

                    fileCounter++;

                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return paths;
    }

    private SoundBank defaultSoundBank(ArrayList<String> paths){
        if(paths.size() > 0){
            SoundBank sb = new SoundBank(SoundBankManager.DEFAULT_SOUNDBANK_NAME);
            ArrayList<Sample> samples = new ArrayList<>();

            for(String path : paths){
                samples.add(new Sample(path.substring(path.lastIndexOf(File.pathSeparator), path.lastIndexOf(".")), path));
            }

            sb.setSamples(samples);

            return sb;
        }

        return null;
    }

    private SoundBank transferSampleFilesToStorage(final Context context){
        Log.i("MPCGO", "Transfer Sample Files - Create Dest Dir");
        if(Environment.getExternalStorageState(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsoluteFile()).equals(Environment.MEDIA_MOUNTED)){
            try{
                if(createDestinationDirectory(context)){
                    Log.i("MPCGO", "Transfer Sample Files");
                    Log.i("MPCGO", String.valueOf("NAMES: ").concat(Arrays.toString(retrieveDefaultNames(context))));
                    ArrayList<AssetFileDescriptor> assetFileDescriptors = retrieveSampleAssetFiles(context, retrieveDefaultNames(context));
                    Log.i("MPCGO", String.valueOf("FILEDESCRIPTORS: ").concat(Arrays.toString(assetFileDescriptors.toArray())));
                    return defaultSoundBank(copyFilesToDestination(assetFileDescriptors, context));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    @Override
    protected SoundBank doInBackground(Context... context) {
        return transferSampleFilesToStorage(context[0]);
    }

    @Override
    protected void onPostExecute(SoundBank soundBank) {
        super.onPostExecute(soundBank);
        soundBankManagerHandler.sendMessage(soundBankManagerHandler.obtainMessage(DEFAULT_SOUNDBANK_FILES_TRANSFERRED, soundBank));
    }
}
