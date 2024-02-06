package com.shermdev.will.mpcgo;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Will on 1/16/2017.
 */
//TODO: DELETE?
public class PlaybackController implements TimingTaskManager.TimingClockListener {
    private final SoundPoolManager soundPoolManager;
    private int notificationMarkerIndex;
    private Handler uiHandler;
    private Handler noteInputHandler;
    private TimingTaskManager timingTaskManager;
    private ClickGenerator clickGenerator;

    private boolean recordOn;
    private boolean playbackOn;


    private SoundBank soundBank;
   // private int loopLength;
    private PlaybackSettings playbackSettings;
    private PlaybackListener playbackListener;





    public PlaybackController(Context context,
                              Handler uiHandler,
                              SoundBank soundBank,
                              PlaybackSettings playbackSettings,
                              PlaybackListener playbackListener){
        this.soundBank = soundBank;
        this.uiHandler = uiHandler;
        this.playbackSettings = playbackSettings;
        this.playbackListener = playbackListener;
        this.soundPoolManager = initializeSoundPoolManager(context, playbackListener);

       // loopLength = 2; //default, change later, will be user setting, probably will have to be passed into the constructor
        recordOn = false; //default, may change later
        playbackOn = false;


    }

    public void startTimingClock(){
        Log.i("PlaybackController", "Recording Playback Start");
        recordOn = true;

       // timingTaskManager.startRecording();
    }

    public void stopRecordingPlayback(){
        recordOn = false;
        timingTaskManager.stop();
        //will have to send notification markers to database
    }

    public void playbackRecordedInput(){
       /* if(notificationMarkers.size() > 0){
            //start timing track with first notificationMarker set as a timing track marker
        }*/
    }



    private void playbackMarkerReachedCallback(AudioTrack at){

        //Log.i("Playback Marker", "MarkerName: " + note.getSample().getSampleName());
        Log.i("Playback Marker", "MarkerTime: " + at.getPlaybackHeadPosition());
        //Log.i("Playback Marker", "MarkerSoundId: " + note.getSample().getSoundPoolId());
        //fireSamplerPad(note);
        //soundPoolManager.playSample(note.getSample().getSoundPoolId(),
        //        note.getVolume() * note.getSample().getSampleGain()); //note input volume multiplies the sample gain modifier

        notificationMarkerIndex++;
         }






    private int millisecToFrames(long timeStamp){
        return (int) (timeStamp * playbackSettings.getSampleRate() / 1000);
    }


    public void playSound(Sample sample, float noteVolume){
        soundPoolManager.playSample(sample, noteVolume);

    }

    private SoundPoolManager initializeSoundPoolManager(Context context, PlaybackListener playbackListener){
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).build();
        return new SoundPoolManager(context);
    }

  //  public void addNotificationMarker(Note note){
      //  timingTaskManager.addMarker(note);
             //increasing the notification marker index by 1. Because I am adding a new note to the Marker list, the next marker has to be index + 2 instead of index++

        //sort notificationMarkers by Note input timestamp
        //sortNotificationMarkers();
   // }

    /*public void prepareTracksForPlayback(ArrayList<Track> tracks){
        loadTracksForPlayback(tracks);
        sortNotificationMarkers();
    }*/



    private void loadTracksForPlayback(ArrayList<Track> tracks){
        for(Track track : tracks){
            for(int i = 0; i < track.getNotes().size(); i++){
               //
            }
        }
    }






    public PlaybackSettings getPlaybackSettings() {
        return playbackSettings;
    }


    public void setRecordOn(boolean recordOn) {
        this.recordOn = recordOn;
    }

    @Override
    public void startLoopPlayback() {
        playbackListener.loopBeginNotifier();
    }

    @Override
    public void markerNotification(Note note) {
        Log.i("MARKERNOTIFICATION", String.valueOf(note.getTimeStamp()));
        playSound(note.getSample(), note.getVolume());
    }

    @Override
    public void sortNotificationMarkers() {

    }

   /* @Override
    public void onMarkerReached(AudioTrack track) {
        playbackMarkerReachedCallback(track);
    }

    @Override
    public void onPeriodicNotification(AudioTrack track) {
        //should be notified on beginning of each loop (starting after first loop)
        Log.i("PERIODIC NOTIF: ", "TIME: " + track.getPlaybackHeadPosition());
        if(track.getPositionNotificationPeriod() == AudioUtility.calculateMeasureLengthInFrames(playbackSettings) * playbackSettings.getLoopLength())
        {
            notificationMarkerIndex = 0;
        }
        track.setNotificationMarkerPosition(millisecToFrames(notificationMarkers.get(notificationMarkerIndex).getTimeStamp()));


    }*/

    public interface PlaybackListener {
        void loopBeginNotifier();
        void startNoteInput();
        void loopPlaybackOn();
        void recordPreCountNotifier();
        void soundPoolLoaded(ArrayList<Sample> loadedSamples);
        void clickNotification(int beatCounter);
        void timeNotification();
    }
}
