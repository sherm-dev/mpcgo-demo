package com.shermdev.will.mpcgo;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Will on 1/15/2017.
 */

public class SoundPoolManager{
    public static final int SAMPLE_LOADED = 100; //Message What for sample loaded
    public static final int SAMPLE_ID = 101;  //Message what for loaded sample id
    public static final int PAD_HIT = 102; //Message what for sampler pad hit
    private static final String SAMPLE_PLAYBACK_THREAD_NAME = "sample_playback_thread_name";
    private final AudioManager audioManager;
    private final AudioAttributes audioAttributes;
    private SoundPool soundPool;

    public SoundPoolManager(Context context){
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

         audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(SoundBankManager.MAX_SAMPLE_COUNT)
                .build();
    }



    public void playSample(final Sample sample, final float noteVolume){
        float outputVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * noteVolume;
        soundPool.play(sample.getSampleOrder(),
                outputVolume,
                outputVolume,
                AudioAttributes.FLAG_AUDIBILITY_ENFORCED,
                0,
                1.0f);
    }

    public void clearSoundPool(){
        soundPool.autoPause();
        soundPool.release();
        soundPool =  new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .setMaxStreams(SoundBankManager.MAX_SAMPLE_COUNT)
                .build();
    }

    public SoundPool getSoundPool() {
        return soundPool;
    }

    public void stopPlayback(){
        soundPool.autoPause();
    }

    public int generateAudioSessionID(){
        return audioManager.generateAudioSessionId();
    }
}
