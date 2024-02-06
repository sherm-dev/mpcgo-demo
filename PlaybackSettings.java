package com.shermdev.will.mpcgo;

/**
 * Created by Will on 4/5/2017.
 */

public class PlaybackSettings {
    public static final int DEFAULT_SAMPLE_RATE = 44100;
    public static final int DEFAULT_TEMPO = 90;
    public static final int DEFAULT_PRE_COUNT_LENGTH = 2;
    public static final int DEFAULT_LOOP_LENGTH = 2;
    public static final boolean DEFAULT_PRE_COUNT_ON = true;
    public static final boolean DEFAULT_LOOP_ON = true;
    public static final TimeSignature DEFAULT_TIME_SIGNATURE = new TimeSignature(TimeSignature.QUARTER_NOTE, TimeSignature.QUARTER_NOTE);
    public static final boolean DEFAULT_CLICK_SOUND_ON = true;
    public static final boolean DEFAULT_QUANTIZE_ON = true;

    private int sampleRate, tempo, preCountLength, loopLength, quantizeNote;
    private TimeSignature timeSignature;
    private boolean preCountOn, loopOn, clickSoundOn, quantizeOn;

    public PlaybackSettings(int sampleRate, int tempo, TimeSignature timeSignature, boolean preCountOn,
                            boolean loopOn, int preCountLength, int loopLength, boolean clickSoundOn, boolean quantizeOn){
        this.sampleRate = sampleRate;
        this.tempo = tempo;
        this.timeSignature = timeSignature;
        this.preCountOn = preCountOn;
        this.preCountLength = preCountLength;
        this.loopLength = loopLength;
        this.loopOn = loopOn;
        this.clickSoundOn = clickSoundOn;
        this.quantizeOn = quantizeOn;
        quantizeNote = timeSignature.getLower() * 2;
    }

    public static PlaybackSettings defaultPlaybackSettings(){
        return new PlaybackSettings(PlaybackSettings.DEFAULT_SAMPLE_RATE,
                PlaybackSettings.DEFAULT_TEMPO,
                PlaybackSettings.DEFAULT_TIME_SIGNATURE,
                PlaybackSettings.DEFAULT_PRE_COUNT_ON,
                PlaybackSettings.DEFAULT_LOOP_ON,
                PlaybackSettings.DEFAULT_PRE_COUNT_LENGTH,
                PlaybackSettings.DEFAULT_LOOP_LENGTH,
                PlaybackSettings.DEFAULT_CLICK_SOUND_ON,
                PlaybackSettings.DEFAULT_QUANTIZE_ON);
    }

    public boolean isClickSoundOn() {
        return clickSoundOn;
    }

    public int getQuantizeNote() {
        return quantizeNote;
    }

    public void setQuantizeNote(int quantizeNote) {
        this.quantizeNote = quantizeNote;
    }

    public boolean isQuantizeOn() {
        return quantizeOn;
    }

    public void setQuantizeOn(boolean quantizeOn) {
        this.quantizeOn = quantizeOn;
    }

    public void setClickSoundOn(boolean clickSoundOn) {
        this.clickSoundOn = clickSoundOn;
    }

    public int getPreCountLength() {
        return preCountLength;
    }

    public void setPreCountLength(int preCountLength) {
        this.preCountLength = preCountLength;
    }

    public boolean isPreCountOn() {
        return preCountOn;
    }

    public void setPreCountOn(boolean preCountOn) {
        this.preCountOn = preCountOn;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public int getLoopLength() {
        return loopLength;
    }

    public void setLoopLength(int loopLength) {
        this.loopLength = loopLength;
    }

    public TimeSignature getTimeSignature() {
        return timeSignature;
    }

    public void setTimeSignature(TimeSignature timeSignature) {
        this.timeSignature = timeSignature;
    }

    public boolean isLoopOn() {
        return loopOn;
    }

    public void setLoopOn(boolean loopOn) {
        this.loopOn = loopOn;
    }
}
