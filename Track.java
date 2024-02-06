package com.shermdev.will.mpcgo;

import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

/**
 * Created by Will on 1/15/2017.
 */

public class Track {
    private static final String HANDLER_THREAD_PREFIX = "track_playback_thread_";

    private Vector<Note> notes;
    private Vector<Note> modifiedNotes;
    private ArrayList<String> trackEffects;
    private float trackVolume;
    private TrackPlaybackHandler trackPlaybackHandler;
    private Handler uiHandler;
    private long startTime;
    private HandlerThread handlerThread;

    public Track(SoundPool soundPool, Handler uiHandler) {
        notes = new Vector<>();
        modifiedNotes = null;
        trackEffects = null;
        trackVolume = 0.0f;
        handlerThread = new HandlerThread(HANDLER_THREAD_PREFIX + String.valueOf((Math.random() * 100)));
        handlerThread.start();
        startTime = SystemClock.elapsedRealtime();
        trackPlaybackHandler = new TrackPlaybackHandler(handlerThread.getLooper(), soundPool, uiHandler);
    }

    public void emptyRemove(){
        if(notes.size() == 0){
            handlerThread.quitSafely();
            handlerThread = null;
        }
    }


    public void addNotesToPlaybackToQueue(){
        for(int i = 0; i < notes.size(); i++){
            trackPlaybackHandler.sendMessageDelayed(
                    trackPlaybackHandler.obtainMessage(
                            TrackPlaybackHandler.TRACK_NOTE_PLAYBACK_MSG,
                            notes.get(i)),
                    notes.get(i).getTimeStamp()
            );
        }
    }

    public void addNoteToTrack(Note newNote){
        notes.add(newNote);
        trackPlaybackHandler.sendMessage(trackPlaybackHandler.obtainMessage(TrackPlaybackHandler.TRACK_NOTE_PLAYBACK_MSG, newNote));
    }

    public void deleteNoteFromTrack(int noteIndex){

    }

    public void editNoteVolume(int noteIndex, float newVolume){
        Vector<Note> newNotes = getModifiedNotes();
        newNotes.get(noteIndex).setVolume(newVolume);
        setModifiedNotes(newNotes);
    }

    public Vector<Note> getNotes() {
        return notes;
    }

    private void setNotes(Vector<Note> notes) {
        this.notes = notes;
    }

    public Vector<Note> getModifiedNotes() {
        return modifiedNotes;
    }

    private void setModifiedNotes(Vector<Note> modifiedNotes) {
        this.modifiedNotes = modifiedNotes;
    }

    public ArrayList<String> getTrackEffects() {
        return trackEffects;
    }

    public void setTrackEffects(ArrayList<String> trackEffects) {
        this.trackEffects = trackEffects;
    }

    public float getTrackVolume() {
        return trackVolume;
    }

    public void setTrackVolume(float trackVolume) {
        this.trackVolume = trackVolume;
    }

    public TrackPlaybackHandler getTrackPlaybackHandler() {
        return trackPlaybackHandler;
    }
}
