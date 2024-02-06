package com.shermdev.will.mpcgo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.media.audiofx.Visualizer;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class TrackFader extends LinearLayout implements SeekBar.OnSeekBarChangeListener{
    private static int VOLUME_GREEN = 75;
    private static int VOLUME_YELLOW = 90;
    private static int VOLUME_RED = 100;
    private FaderFragment.OnFaderInteraction faderListener;
    private int trackVolume;
    private int faderIndex;
    private SeekBar fader;
    private FrameLayout volumeDisplay;
    private Visualizer visualizer;

    public TrackFader(Context context, int trackVolume, int faderIndex, FaderFragment.OnFaderInteraction faderListener) {
        super(context);
        this.trackVolume = trackVolume;
        this.faderListener = faderListener;
        this.faderIndex = faderIndex;
    }

    public void triggerDisplay(final int visualizerMeasurement){
        post(new Runnable() {
            @Override
            public void run() {
                setVolumeDisplayBackground(visualizerMeasurement);
            }
        });
    }

    private GradientDrawable createFaderBackground(int volume){
        int[] colors = new int[]{
                Color.GREEN,
                Color.YELLOW,
                Color.RED
        };


        GradientDrawable drawable = new GradientDrawable();
        drawable.setSize(20, fader.getProgress());
        drawable.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP);
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawable.setColors(colors);
        drawable.setGradientCenter(10, VOLUME_GREEN);
        drawable.mutate();
        return drawable;
    }

    private void setVolumeDisplayBackground(final int trackVolume){
        post(new Runnable() {
            @Override
            public void run() {
                volumeDisplay.setBackground(createFaderBackground(trackVolume));
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        volumeDisplay = (FrameLayout) findViewById(R.id.volume_display);
        fader = (SeekBar) findViewById(R.id.fader);
        fader.setOnSeekBarChangeListener(this);
        fader.setProgress(trackVolume);
        setVolumeDisplayBackground(trackVolume);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        visualizer.release();
    }

    public void setTrackVolume(int trackVolume) {
        this.trackVolume = trackVolume;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        setTrackVolume(seekBar.getProgress());
        faderListener.onFaderDrag(faderIndex, seekBar.getProgress());
    }
}
