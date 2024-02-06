package com.shermdev.will.mpcgo;

/**
 * Created by Will on 1/15/2017.
 */

public class Quantizer {
    protected static final int MODE_STRAIGHT = 20;
    protected static final int MODE_SWING = 21;
    protected static final int MODE_LIVE = 22;

    private int quantizeMode;
    private boolean quantizeOn;
    private int quantizeNote;

    private int quantizeIndex = 0;

    public Quantizer() {
        this.quantizeNote = TimeSignature.EIGHTH_NOTE; //Set default quantize beat to 8th notes
         this.quantizeMode = MODE_STRAIGHT;  //Set default quantize mode to Straight Time
        this.quantizeOn = true;  //Set default quantize on to true
    }

  /*  protected float quantizeNote(float inputTime, int bpm, int noteIndex, TimeSignature timeSignature){
        float newTimestamp = 0;
        final float quantizeCorrection = calculateCorrection(getQuantizeMode(), inputTime, bpm, timeSignature);
        int millisecondsPerQuantizeUnit = 60000/bpm * timeSignature.getUpper() / getQuantizeNote(); //Millisecons per beat times number of beats in measure, divided by the beat denomination to be quantized

        switch(getQuantizeMode()){
            case MODE_STRAIGHT:
                return inputTime + quantizeCorrection;
                break;
            case MODE_SWING:
                float swingOffset =
                return inputTime
                break;
            case MODE_LIVE:
                break;
            default:
                break;
        }
        //see if the inputed not is within the quantization tolerance, if not, move the note back or up to closest quantize strong beat
        if(inputTimestamp * noteIndex >  millisecPerBeat * noteIndex - quantizeCorrection
                || inputTimestamp * noteIndex <= millisecPerBeat * noteIndex + quantizeCorrection){
        }
        return newTimestamp;
    }*/





    private Note findClosestQuantizePoint(Note note, TimeSignature ts, int bpm){
        int quantizeNoteTarget = getQuantizeNote();
        int quantizeNoteRange = 60000 / bpm * ts.getUpper() / quantizeNoteTarget;
        long originalTime = note.getTimeStamp();

        for(int i = 1; i <= quantizeNoteTarget; i++){
            if(Math.abs(originalTime - (i - 1) * quantizeNoteRange) < Math.abs(originalTime - i * quantizeNoteRange)){
                note.setTimeStamp((i - 1) * quantizeNoteRange);
            }else{
                note.setTimeStamp(i * quantizeNoteRange);
            }
        }
        return note;
    }



    public int getQuantizeNote() {
        return quantizeNote;
    }

    public void setQuantizeNote(int quantizeNote) {
        this.quantizeNote = quantizeNote;
    }

    public int getQuantizeMode() {
        return quantizeMode;
    }

    public void setQuantizeMode(int quantizeMode) {
        this.quantizeMode = quantizeMode;
    }

    public boolean isQuantizeOn() {
        return quantizeOn;
    }

    public void setQuantizeOn(boolean quantizeOn) {
        this.quantizeOn = quantizeOn;
    }
}
