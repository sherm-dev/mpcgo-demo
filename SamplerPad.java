package com.shermdev.will.mpcgo;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatImageButton;

import java.util.Arrays;

/**
 * Created by Will on 5/7/2017.
 */

public class SamplerPad extends AppCompatImageButton implements Button.OnTouchListener{
    private static final int[] COLOR_SET_PRESSED = new int[]{android.R.attr.state_pressed};
    private static final int[] COLOR_SET_NOT_PRESSED = new int[]{-android.R.attr.state_pressed};

    private Sample sample;
    private int index;
    private SamplerPadFragment.OnPadPressed listener;

    public SamplerPad(Context context, Sample sample, int index, final SamplerPadFragment.OnPadPressed listener) {
        super(context);
        this.sample = sample;
        this.index = index;
        this.listener = listener;
    }

    private GradientDrawable createBackgroundDrawable(){
        GradientDrawable gradient = new GradientDrawable();

        gradient.setOrientation(GradientDrawable.Orientation.BL_TR);
        gradient.setColor(createColorStateList());

        return gradient;
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnTouchListener(this);
        post(new Runnable() {
            @Override
            public void run() {
                setBackground(createBackgroundDrawable());
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
            listener.onSamplerPadPressed((SamplerPad) v, event.getPressure());

        return true;
    }

    private ColorStateList createColorStateList(){
        return new ColorStateList(
                new int[][]{
                    new int[]{android.R.attr.state_pressed},
                    new int[]{-android.R.attr.state_pressed}
                },
                new int[]{Color.BLUE, Color.DKGRAY}
        );
    }

    public void triggerPad(){
        if(Arrays.equals(getDrawableState(), COLOR_SET_PRESSED)){
            getDrawable().setState(COLOR_SET_NOT_PRESSED);
        }

        if(Arrays.equals(getDrawableState(), COLOR_SET_NOT_PRESSED)){
            getDrawable().setState(COLOR_SET_PRESSED);
        }
    }

    public int getIndex() {
        return index;
    }
    public Sample getSample() {
        return sample;
    }
}
