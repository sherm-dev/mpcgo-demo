package com.shermdev.will.mpcgo;

import android.widget.TextView;

/**
 * Created by Will on 6/11/2017.
 */

public class BeatRunnable implements Runnable {
    private final TextView beatView;
    private final int beat;

    public BeatRunnable(TextView beatView, int beat){
        this.beatView = beatView;
        this.beat = beat;
    }

    @Override
    public void run() {
        beatView.post(new Runnable() {
            @Override
            public void run() {
                beatView.setText(beat);
            }
        });
    }
}
