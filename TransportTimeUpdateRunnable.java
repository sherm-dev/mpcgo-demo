package com.shermdev.will.mpcgo;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Will on 6/11/2017.
 */

public class TransportTimeUpdateRunnable implements Runnable {
    private Handler fragmentHandler;
    private ElapsedTimeUnit elapsedTimeUnit;
    private boolean timerOn;
    private HandlerThread handlerThread;
    private TimerHandler timerHandler;

    public TransportTimeUpdateRunnable(Handler fragmentHandler) {
        this.fragmentHandler = fragmentHandler;
        elapsedTimeUnit = new ElapsedTimeUnit();
        timerOn = true;
    }

    public void timerOff() {
        timerOn = false;
        handlerThread.quitSafely();
        timerHandler = null;
    }

    @Override
    public void run() {
        handlerThread = new HandlerThread("TIME_HANDLER_THREAD");
        handlerThread.start();
        timerHandler = new TimerHandler(handlerThread.getLooper(), fragmentHandler);


        while(timerOn){
            fragmentHandler.sendMessageDelayed(
                    fragmentHandler.obtainMessage(
                            TimerHandler.TIMER_INCREMENT_MESSAGE,
                            elapsedTimeUnit.incrementTime()),
                    1
            );
        }
    }
}
