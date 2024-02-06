package com.shermdev.will.mpcgo;

/**
 * Created by Will on 6/3/2017.
 */

public class NoteQuantizer {
    private long measureLengthInMillisecs;
    private int measuresInLoop;
    private int quantizeNote;

    public NoteQuantizer(long measureLengthInMillisecs, int measuresInLoop, int quantizeNote){
        this.measureLengthInMillisecs = measureLengthInMillisecs;
        this.measuresInLoop = measuresInLoop;
        this.quantizeNote = quantizeNote;
    }

    public long quantizeNote(long timeStamp){
        long newTimeStamp = 0;
        int quantizePointsInLoop = measuresInLoop * quantizeNote;
        long quantizeNoteLengthMillisec = Math.round(measureLengthInMillisecs / quantizeNote);

        for(int i = 1; i <= quantizePointsInLoop + 1; i++){
            if(timeStamp < i * quantizeNoteLengthMillisec && timeStamp >= (i - 1) *  quantizeNoteLengthMillisec){
                    if(timeStamp - (i - 1) * quantizeNoteLengthMillisec < i * quantizeNoteLengthMillisec - timeStamp){
                        newTimeStamp = (i - 1) * quantizeNoteLengthMillisec;
                    }else{
                        newTimeStamp = i * quantizeNoteLengthMillisec;
                    }
            }
        }

        return newTimeStamp;
    }
}
