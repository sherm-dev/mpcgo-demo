package com.shermdev.will.mpcgo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.TextView;

/**
 * Created by Will on 6/11/2017.
 */

public class TimerHandler extends Handler {
    public static final int TIMER_INCREMENT_MESSAGE = 442939;
    private Handler fragmentHandler;

    public TimerHandler(Looper looper) {
        super(looper);
    }

    public TimerHandler(Looper looper, Handler fragmentHandler){
        super(looper);
        this.fragmentHandler = fragmentHandler;
    }

    @Override
    public void handleMessage(final Message msg) {
        super.handleMessage(msg);
        if(msg.what == TIMER_INCREMENT_MESSAGE && msg.getTarget() == TimerHandler.this){
            final ElapsedTimeUnit etu = (ElapsedTimeUnit) msg.obj;
            post(new Runnable() {
                @Override
                public void run() {
                    fragmentHandler.sendMessageDelayed(
                            fragmentHandler.obtainMessage(TIMER_INCREMENT_MESSAGE, etu.incrementTime()),
                            1
                    );

                }
            });
        }
    }
}
