package com.shermdev.will.mpcgo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Will on 5/14/2017.
 */

public class PlaybackClockHandler extends Handler {
    private PlaybackClockTask playbackClockTask;

    public PlaybackClockHandler(Looper looper) {
        super(looper);
    }

    public PlaybackClockHandler(Looper looper, PlaybackClockTask playbackClockTask) {
        super(looper);
        this.playbackClockTask = playbackClockTask;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case TimingTaskManager.PLAYBACK_START_CLOCK:
                post(new Runnable() {
                    @Override
                    public void run() {
                        playbackClockTask.startClock();
                    }
                });
                break;
            case TimingTaskManager.STOP_PLAYBACK_MSG:
                post(new Runnable() {
                    @Override
                    public void run() {
                        playbackClockTask.stopPlayback();
                    }
                });
                break;
            case PlaybackClockTask.PLAYBACK_CLOCK_WRITE_COMPLETE:
                post(new Runnable() {
                    @Override
                    public void run() {
                        playbackClockTask.writeClockTrack();
                    }
                });
                break;
            default:

                break;
        }
    }
}
