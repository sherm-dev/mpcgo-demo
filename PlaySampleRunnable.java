package com.shermdev.will.mpcgo;

import android.media.AudioAttributes;
import android.media.SoundPool;

/**
 * Created by Will on 5/28/2017.
 */

public class PlaySampleRunnable implements Runnable {
    private SoundPool soundPool;
    private Sample sample;
    private float volume;

    public PlaySampleRunnable(SoundPool soundPool, Sample sample, float volume){
        this.soundPool = soundPool;
        this.sample = sample;
        this.volume = volume;
    }

    @Override
    public void run() {
        soundPool.play(sample.getSampleOrder(),
                volume,
                volume,
                AudioAttributes.FLAG_AUDIBILITY_ENFORCED,
                0,
                1.0f);
    }
}
