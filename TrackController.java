package com.shermdev.will.mpcgo;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Will on 1/29/2017.
 */

public class TrackController {
    public static final int LOOP_BEGIN = 33333;
    public static final int START_NOTE_INPUT = 33334;

    private final ArrayList<Track> tracks;
    private ArrayList<Note> holdoverNotes = null;

    private long systemRecordStartTime;
    private long loopStartTime;
    private int loopCounter;
    private boolean recordingNoteInput;
    private final long loopLengthMillisec;
    private final PlaybackSettings playbackSettings;
    private final NoteQuantizer noteQuantizer;
    private final MainActivity.UIHandler uiHandler;
    private final SoundBankManager soundBankManager;

    public TrackController(PlaybackSettings playbackSettings, SoundBankManager soundBankManager, MainActivity.UIHandler uiHandler){
        this.playbackSettings = playbackSettings;
        this.soundBankManager = soundBankManager;
        this.uiHandler = uiHandler;
        tracks = new ArrayList<>();
        systemRecordStartTime = 0;
        loopCounter = 0;
        loopLengthMillisec = 60000 * playbackSettings.getTimeSignature().getUpper()
                * playbackSettings.getLoopLength() / playbackSettings.getTempo(); // 60000 is milliseconds per minute
        noteQuantizer = new NoteQuantizer((loopLengthMillisec / playbackSettings.getLoopLength()), playbackSettings.getLoopLength(), playbackSettings.getQuantizeNote());
    }


    public PlaybackNotificationMarker recordNoteInput(Sample sample, float noteVolume, int buttonId){
            long inputTime = calculateInputTime(SystemClock.elapsedRealtime());
            Note note = null;
            if(playbackSettings.isQuantizeOn()){
                long quantizeTime = noteQuantizer.quantizeNote(inputTime);
                if(playbackSettings.isLoopOn() && quantizeTime >= loopLengthMillisec){
                    note = new Note(sample, (quantizeTime - loopLengthMillisec), noteVolume, buttonId);
                    holdoverNotes.add(note); // holdover notes add the note to the next track if in loop mode, nothing changes in adding the note to the notification markers, because it only matters which track it's on for multitracking mixdown
                }else{
                    note = new Note(sample, quantizeTime, noteVolume, buttonId);
                    tracks.get(tracks.size() - 1).addNoteToTrack(note);
                }
            }else{
                if(playbackSettings.isLoopOn() && inputTime >= loopLengthMillisec){
                    note = new Note(sample, inputTime - loopLengthMillisec, noteVolume, buttonId);
                    holdoverNotes.add(note); // holdover notes add the note to the next track if in loop mode, nothing changes in adding the note to the notification markers, because it only matters which track it's on for multitracking mixdown
                }else{
                    note = new Note(sample, inputTime, noteVolume, buttonId);
                    tracks.get(tracks.size() - 1).addNoteToTrack(note);
                }
            }

        return new PlaybackNotificationMarker(note, tracks.size() - 1);
    }

    private long calculateInputTime(long elapsedTime){
        return playbackSettings.isLoopOn() ?
                elapsedTime - loopStartTime : elapsedTime - systemRecordStartTime;
    }

    /*private long adjustInputTime(){
        Log.i("INPUTTIME", "adjust time");
        return SystemClock.elapsedRealtime() - systemRecordStartTime -
                AudioUtility.calculateMeasureLengthInMilliSec(playbackController.getPlaybackSettings())
                * playbackController.getPlaybackSettings().getLoopLength() * loopCounter;
    }*/

    private void removeEmptyTrack(int trackNumber){
        tracks.get(trackNumber).emptyRemove();
        tracks.remove(trackNumber);
    }

    private void addNewTrack(){
        tracks.add(new Track(soundBankManager.getSoundPoolManager().getSoundPool(), uiHandler));

        for(int i = 0; i < tracks.size() - 1; i++){
            // the last track in tracks is currently being input, so I don't want to add it's notification markers here
            if(tracks.get(i).getNotes().size() > 0){
                tracks.get(i).addNotesToPlaybackToQueue();
            }else{
                removeEmptyTrack(i);
            }
        }

        if(holdoverNotes == null){
            holdoverNotes = new ArrayList<>();
        }else{
            if(holdoverNotes.size() > 0){
                for(int i = 0; i < holdoverNotes.size(); i++){
                    tracks.get(tracks.size() - 1).addNoteToTrack(holdoverNotes.get(i));
                }
                holdoverNotes = new ArrayList<>();
            }
        }
    }

    public void beginLoop(){
        if(playbackSettings.isLoopOn() && recordingNoteInput) {
            loopStartTime = SystemClock.elapsedRealtime();
            addNewTrack();
        }
    }

    public void startRecordingNoteInput(){
        Log.i("NOTEINPUT", "Start Recording Note Input");
        recordingNoteInput = true;
        systemRecordStartTime = SystemClock.elapsedRealtime();
        if(!playbackSettings.isLoopOn()) { //if not looping, create a new track and add it to tracks, so I don't add two tracks here and in begin loop
            addNewTrack();
        }else{
            beginLoop();
        }
    }

    public void stopRecordingNoteInput(){
        recordingNoteInput = false;
        for(int i = 0; i < getTracks().size(); i++){
            Log.i("TRACK", "Track " + i);
            for(int j = 0; j < getTracks().get(i).getNotes().size(); j++){
                String logString = "Name: " + getTracks().get(i).getNotes().get(j).getSample().getSampleName() +
                        " Time: " + getTracks().get(i).getNotes().get(j).getTimeStamp();
                Log.i("TRACKNOTE", logString);
            }
        }
       // playbackController.stopRecordingPlayback();
    }

    public long getLoopStartTime() {
        return loopStartTime;
    }

    public ArrayList<Track> getTracks(){
        return tracks;
    }

    public int getTrackCount(){
        return tracks.size();
    }

    public ArrayList<Integer> getTrackVolumes(){
        ArrayList<Integer> volumes = new ArrayList<>();

        for(int i = 0; i < tracks.size(); i++){
           volumes.add((int) tracks.get(i).getTrackVolume());
        }

        return volumes;
    }

    public int getAudioSessionID(){
        return this.soundBankManager.getSoundPoolManager().generateAudioSessionID();
    }

    public PlaybackSettings getPlaybackSettings() {
        return this.playbackSettings;
    }

    private void setSystemRecordStartTime(long systemRecordStartTime) {
        this.systemRecordStartTime = systemRecordStartTime;
    }
}
