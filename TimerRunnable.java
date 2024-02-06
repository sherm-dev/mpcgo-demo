package com.shermdev.will.mpcgo;


import android.widget.TextView;

/**
 * Created by Will on 6/11/2017.
 */

public class TimerRunnable implements Runnable{
    private TextView timerView;
    private String time;

    public TimerRunnable(TextView timerView, String time){
        this.timerView = timerView;
        this.time = time;
    }

    @Override
    public void run() {
        timerView.post(new Runnable() {
            @Override
            public void run() {
                timerView.setText(time);
            }
        });
    }
}
