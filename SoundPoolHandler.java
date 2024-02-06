package com.shermdev.will.mpcgo;

import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by Will on 5/28/2017.
 */

public class SoundPoolHandler extends Handler {
    public static final int PLAY_SAMPLE = 8999;

    private SoundPool soundPool;

    public SoundPoolHandler(Looper looper) {
        super(looper);
    }

    public SoundPoolHandler(Looper looper, SoundPool soundPool) {
        super(looper);
        this.soundPool = soundPool;
    }

    @Override
    public void handleMessage(Message msg) {
        if(msg.what == PLAY_SAMPLE){
            final Sample sample = (Sample) msg.obj;
            final float leftVolume = Float.intBitsToFloat(msg.arg1);
            final float rightVolume = Float.intBitsToFloat(msg.arg2);
            post(new Runnable() {
                @Override
                public void run() {
                    soundPool.play(sample.getSampleOrder(),
                            leftVolume,
                            rightVolume,
                            AudioAttributes.FLAG_AUDIBILITY_ENFORCED,
                            0,
                            1.0f);
                }
            });
        }else{super.handleMessage(msg);}
    }
}
