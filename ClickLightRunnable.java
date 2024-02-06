package com.shermdev.will.mpcgo;



import android.widget.ImageView;


/**
 * Created by Will on 6/11/2017.
 */

public class ClickLightRunnable implements Runnable {
    private final int color;
    private final ImageView clickLight;

    public ClickLightRunnable(int color, ImageView clickLight){
        this.color = color;
        this.clickLight = clickLight;
    }
    @Override
    public void run() {
        clickLight.post(new Runnable() {
            @Override
            public void run() {
                clickLight.setBackgroundColor(color);
            }
        });
    }
}
