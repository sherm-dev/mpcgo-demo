package com.shermdev.will.mpcgo;


/**
 * Created by Will on 4/23/2017.
 */

public class PlaybackNotificationMarker {
    private final int trackNumber;
    private final Note note;

    public PlaybackNotificationMarker(Note note, int trackNumber){
        this.trackNumber = trackNumber;
        this.note = note;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public Note getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "Note: " + getNote().toString() + " TimeStamp: " + getNote().getTimeStamp() + " Track: " + getTrackNumber();
    }
}
