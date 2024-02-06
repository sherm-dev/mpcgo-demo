package com.shermdev.will.mpcgo;

import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Will on 5/28/2017.
 */

public class TrackPlaybackHandler extends Handler {
    public static final int TRACK_NOTE_PLAYBACK_MSG = 999913;
    public static final int SAMPLER_PAD_TRIGGER_MSG = 999914;

    private SoundPool soundPool;
    private Handler uiHandler;

    public TrackPlaybackHandler(Looper looper) {
        super(looper);
    }

    public TrackPlaybackHandler(Looper looper, SoundPool soundPool, Handler uiHandler) {
        super(looper);
        this.soundPool = soundPool;
        this.uiHandler = uiHandler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if(msg.what == TRACK_NOTE_PLAYBACK_MSG){
            final Note note = (Note) msg.obj;
            post(new PlaySampleRunnable(soundPool, note.getSample(), note.getVolume()));
            uiHandler.sendMessage(uiHandler.obtainMessage(msg.what, msg.obj));
        }
    }
}
