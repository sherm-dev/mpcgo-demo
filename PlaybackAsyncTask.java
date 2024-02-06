package com.shermdev.will.mpcgo;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Handler;

/**
 * Created by Will on 1/15/2017.
 * AsyncTask for Playback of SoundPool sounds during loop playback
 */

public class PlaybackAsyncTask extends AsyncTask<Sample, Integer, Integer> {
    private Handler handler;
    private final SoundPool soundPool;
    private final AudioManager audioManager;
    private float trackVolume;

    public PlaybackAsyncTask(Handler handler, SoundPool soundPool, AudioManager audioManager, float trackVolume) {
        this.handler = handler;
        this.soundPool = soundPool;
        this.audioManager = audioManager;
        this.trackVolume = trackVolume;
    }

    public PlaybackAsyncTask(SoundPool soundPool, AudioManager audioManager, float trackVolume) {
        this.soundPool = soundPool;
        this.audioManager = audioManager;
       // this.trackVolume = trackVolume;
    }

    public PlaybackAsyncTask(SoundPool soundPool, AudioManager audioManager) {
        this.soundPool = soundPool;
        this.audioManager = audioManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Sample... samples){
        float volume = samples[samples.length - 1].getSampleGain() * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
       // Message message = handler.obtainMessage(SoundPoolManager.PAD_HIT, notes[0].getSample().getSoundPoolId());
       // handler.dispatchMessage(message);
        soundPool.play(samples[samples.length - 1].getSampleOrder(),
                0.3f,
                0.3f,
                AudioAttributes.FLAG_AUDIBILITY_ENFORCED,
                0,
                1.0f);
        return null;
    }
}
