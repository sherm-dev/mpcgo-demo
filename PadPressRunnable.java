package com.shermdev.will.mpcgo;

import android.graphics.Color;
import android.view.View;

/**
 * Created by Will on 2/3/2017.
 */

public class PadPressRunnable implements Runnable {
    private View button;
    private boolean padPressed;
    public PadPressRunnable(View button, boolean padPressed){
        this.button = button;
        this.padPressed = padPressed;
    }

    @Override
    public void run() {
        if(isPadPressed()){
            getButton().setBackgroundColor(Color.DKGRAY);
        }else{
            getButton().setBackgroundColor(Color.BLACK);
        }
    }

    public View getButton() {
        return button;
    }

    public void setButton(View button) {
        this.button = button;
    }

    public boolean isPadPressed() {
        return padPressed;
    }

    public void setPadPressed(boolean padPressed) {
        this.padPressed = padPressed;
    }
}
