package com.shermdev.will.mpcgo;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Will on 5/30/2017.
 */

public class TimingTaskHandler extends Handler {
    public static final int CLOCK_UPDATE_MSG = 34344;
    public static final int ADD_MARKER_MSG = 33143;

    private PlaybackClockHandler playbackClockHandler;
    private TrackController trackController;
    private TimingTaskManager timingTaskManager;
    private int loopCounter;
    private long ellapsedMilliseconds;
    private long playbackOffset;
    private long loopLengthInMillisec;
    private int currentTrack;

    public TimingTaskHandler(Looper looper, TimingTaskManager timingTaskManager) {
        super(looper);
        this.playbackClockHandler = null;
        this.timingTaskManager = timingTaskManager;

        loopCounter = 1;
        ellapsedMilliseconds = 0;
        playbackOffset = 0;
        loopLengthInMillisec =  Math.round(
                (AudioUtility.calculateMeasureLengthInFrames(timingTaskManager.getTrackController().getPlaybackSettings())
                * timingTaskManager.getTrackController().getPlaybackSettings().getLoopLength()) * 1000 / timingTaskManager.getTrackController().getPlaybackSettings().getSampleRate());
        currentTrack = 0;
    }

    public void setPlaybackClockHandler(PlaybackClockHandler playbackClockHandler) {
        this.playbackClockHandler = playbackClockHandler;
    }

    private void playbackClockWriteComplete(){
        ellapsedMilliseconds++;
       long adjustedTime = ellapsedMilliseconds + playbackOffset;
        if(playbackClockHandler != null) playbackClockHandler.sendMessage(
                playbackClockHandler.obtainMessage(PlaybackClockTask.PLAYBACK_CLOCK_WRITE_COMPLETE));

        if(timingTaskManager.getNotificationMarkers().containsKey((String.valueOf(ellapsedMilliseconds)))){
            PlaybackNotificationMarker notificationMarker = timingTaskManager.getNotificationMarkers()
                    .get(String.valueOf(ellapsedMilliseconds));
            if(notificationMarker.getTrackNumber() != currentTrack){
                trackController.getTracks().get(notificationMarker.getTrackNumber())
                        .getTrackPlaybackHandler().sendMessage(
                        trackController.getTracks().get(notificationMarker.getTrackNumber()).getTrackPlaybackHandler()
                                .obtainMessage(TrackPlaybackHandler.TRACK_NOTE_PLAYBACK_MSG, notificationMarker.getNote()));
            }
        }

          timingTaskManager.getUiHandler().sendEmptyMessage(CLOCK_UPDATE_MSG);
    }

    private void setMarkerNotifiers(){
        playbackOffset = (SystemClock.elapsedRealtime() - trackController.getLoopStartTime())
                - loopLengthInMillisec;

        Set<String> markerSet = timingTaskManager.getNotificationMarkers().keySet();
        Iterator<String> markerIterator = markerSet.iterator();
        do{
            PlaybackNotificationMarker notificationMarker = timingTaskManager.getNotificationMarkers().get(markerIterator.next());
            trackController.getTracks().get(notificationMarker.getTrackNumber())
                    .getTrackPlaybackHandler().sendMessageDelayed(
                    trackController.getTracks().get(notificationMarker.getTrackNumber()).getTrackPlaybackHandler()
                            .obtainMessage(TrackPlaybackHandler.TRACK_NOTE_PLAYBACK_MSG, notificationMarker.getNote()),
                    notificationMarker.getNote().getTimeStamp());
        }while(markerIterator.hasNext());

    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case PlaybackClockTask.PLAYBACK_CLOCK_WRITE_COMPLETE:
                post(new Runnable() {
                    @Override
                    public void run() {
                        playbackClockWriteComplete();
                    }
                });
                break;
            case TimingTaskHandler.CLOCK_UPDATE_MSG:
                post(new Runnable() {
                    @Override
                    public void run() {
                        timingTaskManager.getUiHandler().sendEmptyMessage(TimingTaskHandler.CLOCK_UPDATE_MSG);
                    }
                });
                break;
            case ClickTrackTask.LOOP_NOTIFICATION_MSG:

                post(new Runnable() {
                    @Override
                    public void run() {
                        ellapsedMilliseconds = 0;
                        loopCounter++;
                        trackController.beginLoop();
                    }
                });
                break;
            case ClickTrackTask.START_PLAYBACK_CLOCK_MSG:
                post(new Runnable() {
                    @Override
                    public void run() {
                        //timingTaskManager.startPlayback(null);
                        trackController.startRecordingNoteInput();
                        timingTaskManager.getUiHandler().sendEmptyMessage(ClickTrackTask.START_PLAYBACK_CLOCK_MSG);
                    }
                });
                break;
            case ADD_MARKER_MSG:
                final PlaybackNotificationMarker marker = (PlaybackNotificationMarker) msg.obj;
                if(currentTrack != marker.getTrackNumber()) currentTrack = marker.getTrackNumber();
                post(new Runnable() {
                    @Override
                    public void run() {
                        timingTaskManager.addMarker(marker);
                    }
                });
                break;
            default:

                break;
        }
    }
}
