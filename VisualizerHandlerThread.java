package com.shermdev.will.mpcgo;

import android.media.audiofx.Visualizer;
import android.os.HandlerThread;
import android.os.Message;

public class VisualizerHandlerThread extends HandlerThread implements Visualizer.OnDataCaptureListener{
    public static final int MSG_FFT = 22291;
    public static final int MSG_WAVEFORM = 22292;

    private MainActivity.UIHandler uiHandler;
    private Visualizer visualizer;
    private int sampleRate;

    public VisualizerHandlerThread(String name) {
        super(name);
    }

    public VisualizerHandlerThread(String name, MainActivity.UIHandler uiHandler, int sampleRate) {
        super(name);
        this.uiHandler = uiHandler;
        this.sampleRate = sampleRate;
        visualizer = new Visualizer(0); //audio session id 0 for main output mix
    }

    @Override
    public synchronized void start() {
        super.start();
        visualizer.setDataCaptureListener(this, sampleRate, true, true);
        visualizer.setEnabled(true);
        visualizer.setCaptureSize(sampleRate / 1000); //millisecond
    }

    @Override
    public boolean quitSafely() {
        visualizer.setEnabled(false);
        return super.quitSafely();
    }

    @Override
    public boolean quit() {
        visualizer.setEnabled(false);
        return super.quit();
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_WAVEFORM, bytes));
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        uiHandler.sendMessage(uiHandler.obtainMessage(MSG_FFT, bytes));
    }
}
