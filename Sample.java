package com.shermdev.will.mpcgo;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by Will on 1/15/2017.
 */

public class Sample{
    private static final String SAMPLE_NAME_LABEL_TO_STRING = "Sample Name: ";
    private static final String SAMPLE_PATH_LABEL_TO_STRING = " Sample Path: ";
    private static final String SAMPLE_ORDER_LABEL_TO_STRING = " Sample Order: ";
    private static final String SAMPLE_LOADED_LABEL_TO_StRING = " Sample Loaded: ";
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final float DEFAULT_PLAYBACK_RATE = 1;
    public static final float DEFAULT_SAMPLE_GAIN = 1;
    public static final String SAMPLE_BUNDLE_NAME = "samplebundlename";
    public static final String SAMPLE_BUNDLE_PATH = "samplebundlepath";
    public static final String SAMPLE_BUNDLE_SAMPLE_ID = "sample_bundle_sample_id";
    public static final String SAMPLE_BUNDLE_SAMPLE_RATE = "samplebundlesamplerate";
    public static final String DEFAULT_MIME_TYPE = "audio/mp3";
    public static final int DEFUALT_CHANNELS = 2;

    private String sampleName;
    private final String sampleFilePath;
    private float samplePlaybackRate;
    private byte[] sampleByte;
    private int sampleSamplingRate;
    private String mimeType;
    private int channels;
    private float sampleGain;
    private float sampleLength;
    private ArrayList<String> sampleEffects;
    private int sampleOrder;                       //SoundPool soundPoolId returned from SoundPool's load method
    private boolean isLoaded;

    public Sample(String sampleName, String sampleFilePath) {
        this.sampleName = sampleName;
        this.sampleOrder = -1; //a default value that isn't found in the index of a soundpool
        this.sampleFilePath = sampleFilePath;
        this.sampleLength = 0;
        this.sampleByte = null;
        this.samplePlaybackRate = DEFAULT_PLAYBACK_RATE;
       this.sampleSamplingRate = DEFAULT_SAMPLE_RATE;
       this.mimeType = DEFAULT_MIME_TYPE;
       this.sampleGain = DEFAULT_SAMPLE_GAIN;
       this.sampleEffects = new ArrayList<>();
       this.isLoaded = false;
       this.channels = DEFUALT_CHANNELS;
    }

    public Sample(String sampleName, String sampleFilePath, int sampleOrder) {
        this.sampleName = sampleName;
        this.sampleOrder = sampleOrder;
        this.sampleFilePath = sampleFilePath;

        this.sampleLength = 0;
        this.sampleByte = null;
        this.samplePlaybackRate = DEFAULT_PLAYBACK_RATE;
        this.sampleSamplingRate = DEFAULT_SAMPLE_RATE;
        this.mimeType = DEFAULT_MIME_TYPE;
        this.sampleGain = DEFAULT_SAMPLE_GAIN;
        this.sampleEffects = new ArrayList<>();
        this.isLoaded = false;
        this.channels = DEFUALT_CHANNELS;
    }

    public Sample(String sampleName, String sampleFilePath, int sampleOrder, float sampleLength) {
        this.sampleName = sampleName;
        this.sampleOrder = sampleOrder;
        this.sampleFilePath = sampleFilePath;
        this.sampleLength = sampleLength;
        this.sampleByte = null;
        this.samplePlaybackRate = DEFAULT_PLAYBACK_RATE;
        this.sampleSamplingRate = DEFAULT_SAMPLE_RATE;
        this.mimeType = DEFAULT_MIME_TYPE;
        this.sampleGain = DEFAULT_SAMPLE_GAIN;
        this.sampleEffects = new ArrayList<>();
        this.isLoaded = false;
        this.channels = DEFUALT_CHANNELS;
    }

    public Sample(String sampleName, String sampleFilePath, int sampleOrder, float sampleLength, byte[] sampleByte) {
        this.sampleName = sampleName;
        this.sampleOrder = sampleOrder;
        this.sampleFilePath = sampleFilePath;
        this.sampleLength = sampleLength;
        this.sampleByte = sampleByte;
        this.sampleLength = 0;
        this.sampleByte = null;
        this.samplePlaybackRate = DEFAULT_PLAYBACK_RATE;
        this.sampleSamplingRate = DEFAULT_SAMPLE_RATE;
        this.mimeType = DEFAULT_MIME_TYPE;
        this.sampleGain = DEFAULT_SAMPLE_GAIN;
        this.sampleEffects = new ArrayList<>();
        this.isLoaded = false;
        this.channels = DEFUALT_CHANNELS;
    }

    public Sample(String sampleName, int sampleOrder, String sampleFilePath, float samplePlaybackRate, int sampleSamplingRate, String mimeType,
                  float sampleGain, ArrayList<String> sampleEffects) {
            this.sampleName = sampleName;
            this.sampleOrder = sampleOrder;
            this.sampleFilePath = sampleFilePath;
            this.samplePlaybackRate = samplePlaybackRate;
        this.sampleByte = null;
           this.sampleSamplingRate = sampleSamplingRate;
         this.mimeType = mimeType;
        this.sampleByte = null;
        this.samplePlaybackRate = DEFAULT_PLAYBACK_RATE;
        this.sampleGain = DEFAULT_SAMPLE_GAIN;
        this.sampleEffects = new ArrayList<>();
        this.isLoaded = false;
        this.channels = DEFUALT_CHANNELS;
    }

    public float getSampleLength() {
        return sampleLength;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public int getSampleOrder() {
        return sampleOrder;
    }

    public void setSampleOrder(int sampleOrder) {
        this.sampleOrder = sampleOrder;
    }

    public ArrayList<String> getSampleEffects() {
        return sampleEffects;
    }

    public byte[] getSampleByte() {
        return sampleByte;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getSampleSamplingRate() {
        return sampleSamplingRate;
    }

    public void setSampleSamplingRate(int sampleSamplingRate) {
        this.sampleSamplingRate = sampleSamplingRate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setSampleByte(byte[] sampleByte) {
        this.sampleByte = sampleByte;
    }

    public void setSampleEffects(ArrayList<String> sampleEffects) {
        this.sampleEffects = sampleEffects;
    }

    public float getSampleGain() {
        return sampleGain;
    }

    public void setSampleGain(float sampleGain) {
        this.sampleGain = sampleGain;
    }

    public float getSamplePlaybackRate() {
        return samplePlaybackRate;
    }

    public void setSamplePlaybackRate(float samplePlaybackRate) {
        this.samplePlaybackRate = samplePlaybackRate;
    }

    public String getSampleFilePath() {
        return sampleFilePath;
    }

   /* public float getSampleSamplingRate() {
        return sampleSamplingRate;
    }*/

   /* public String getMimeType() {
        return mimeType;
    }*/

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        this.isLoaded = loaded;
    }

    public void setSampleLength(float sampleLength) {
        this.sampleLength = sampleLength;
    }

    public Bundle sampleToBundle(){
        Bundle b = new Bundle();
        b.putString(SAMPLE_BUNDLE_NAME, sampleName);
        b.putString(SAMPLE_BUNDLE_PATH, sampleFilePath);
        b.putInt(SAMPLE_BUNDLE_SAMPLE_ID, sampleOrder);
        return b;
    }

    public static Sample sampleFromBundle(Bundle bundle){
        return new Sample(bundle.getString(SAMPLE_BUNDLE_NAME),
                bundle.getString(SAMPLE_BUNDLE_PATH), bundle.getInt(SAMPLE_BUNDLE_SAMPLE_ID));
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString().concat(getSampleName())
                .concat(SAMPLE_PATH_LABEL_TO_STRING)
                .concat(getSampleFilePath())
                .concat(SAMPLE_ORDER_LABEL_TO_STRING)
                .concat(String.valueOf(getSampleOrder()))
                .concat((isLoaded() ? SAMPLE_LOADED_LABEL_TO_StRING.concat("True") : ""));
    }
}
