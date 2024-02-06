package com.shermdev.will.mpcgo;

/**
 * Created by Will on 1/15/2017.
 */

public class TimeSignature {
    protected static final int WHOLE_NOTE = 1;
    protected static final int HALF_NOTE = 2;
    protected static final int QUARTER_NOTE = 4;
    protected static final int EIGHTH_NOTE = 8;
    protected static final int SIXTEENTH_NOTE = 16;
    protected static final int THIRTYSECOND_NOTE = 32;
    protected static final int SIXTYFOURTH_NOTE = 64;

    private int upper;
    private int lower;

    public TimeSignature(int upper, int lower){
        this.upper = upper;
        this.lower = lower;
    }


    public int getUpper() {
        return upper;
    }

    public void setUpper(int upper) {
        this.upper = upper;
    }

    public int getLower() {
        return lower;
    }

    public void setLower(int lower) {
        this.lower = lower;
    }
}
