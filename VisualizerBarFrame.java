package com.shermdev.will.mpcgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class VisualizerBarFrame extends FrameLayout {

    public VisualizerBarFrame(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setLayoutParams(new FrameLayout.LayoutParams(20, LayoutParams.MATCH_PARENT));

        post(new Runnable() {
            @Override
            public void run() {
                setBackground(createVisualizerFrameBackground(0, 0));
            }
        });
    }



    private GradientDrawable createVisualizerFrameBackground(int magnitudePercent, int color){
        GradientDrawable drawable = new GradientDrawable();
        int colorHeight = getHeight() * magnitudePercent / 100;
        int[] colors = new int[]{
                color == 0 ? color : R.color.visualizerBarDefault,
                R.color.visualizerBarBackgroundDefault
        };

        drawable.setSize(getWidth(), getHeight());
        drawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setColors(colors);
        drawable.setGradientCenter((float) getWidth() / 2, colorHeight);
        drawable.mutate();

        return drawable;
    }

    public void visualizerFrameBackground(final int magnitude, final int color){
        post(new Runnable() {
            @Override
            public void run() {
                setBackground(createVisualizerFrameBackground(magnitude, 0));
            }
        });
    }
}
