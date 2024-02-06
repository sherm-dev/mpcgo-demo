package com.shermdev.will.mpcgo;

/**
 * Created by Will on 1/15/2017.
 */

public class Note implements Comparable<Note>{
    private Sample sample;
    private long timeStamp;
    private float volume;
    private int buttonId;

    public Note(Sample sample, long timeStamp, float volume, int buttonId) {
        this.sample = sample;
        this.timeStamp = timeStamp;
        this.volume = volume;
        this.buttonId = buttonId;
    }

    @Override
    public String toString() {
        return "Sample Name: " + sample.getSampleName() + " Time Stamp: " + timeStamp +
                " Volume: " + volume + " Button ID: " + buttonId;
    }

    //SETTERS ARE USED IN CASE OF USER CHANGES TO SAMPLE GAIN, SOUND EFFECTS, NOTE QUANTIZATION or NOTE VELOCITY EDITS
    //UPON CHANGE OF THE SAMPLE OR NOTE INPUT, EACH NOTE IN THE TRACK WILL HAVE TO HAVE THESE SET

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public int getButtonId() {
        return buttonId;
    }

    @Override
    public int compareTo(Note o) {
        if(getTimeStamp() < o.getTimeStamp()){
            return -1;
        }else if(getTimeStamp() > o.getTimeStamp()){
            return 1;
        }else{
            return 0;
        }
    }
}
