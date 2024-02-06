package com.shermdev.will.mpcgo;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.HandlerThread;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by Will on 3/5/2017.
 */

public class TimingTaskManager {
    public static final int SILENT_BEAT = 0;
    public static final float DEFAULT_CLICK_VOLUME = (float) (AudioTrack.getMaxVolume() * 0.1);
    private static final String PLAYBACK_CLOCK_THREAD_NAME = "Playback_Clock_HT";
    private static final String TIMING_NOTIFICATION_THREAD_NAME = "Timing_Notification_HT";
    public static final int STOP_PLAYBACK_MSG = 33144;
    public static final int PLAYBACK_LOOP_START_MSG = 33145;
    public static final int PLAYBACK_START_CLOCK = 33146;
    public static final int MUTE_CLICK_MSG = 33146;
    public static final int STOP_CLICK_MSG = 33147;
    private static final String PLAYBACK_TASK_KEY = "playback_task";
    private static final String CLICK_TRACK_TASK_KEY = "click_track_task";
    
    private final HashMap<String, Thread> runningTasks;
    private final float clickVolume;
    private final int bufferSizeInBytes;
    private PlaybackClockTask playbackClockTask;
    private ClickTrackTask clickTrackTask;
    private PlaybackClockHandler playbackClockHandler;
    private final TrackController trackController;
    private TimingTaskHandler timingHandler;
    private final HashMap<String, PlaybackNotificationMarker> notificationMarkers;
    private final MainActivity.UIHandler uiHandler;
    private boolean playbackOn, recordingOn;
    private int audioSessionID;

    public TimingTaskManager(PlaybackSettings playbackSettings, SoundBankManager soundBankManager, MainActivity.UIHandler uiHandler){
        this.trackController = new TrackController(playbackSettings, soundBankManager, uiHandler);
        this.uiHandler = uiHandler;
        this.audioSessionID = AudioManager.AUDIO_SESSION_ID_GENERATE;
        notificationMarkers = new HashMap<>();
        runningTasks = new HashMap<>();
        playbackClockHandler = null;
        bufferSizeInBytes = AudioTrack.getMinBufferSize(trackController.getPlaybackSettings().getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 4 <
                AudioUtility.calculateMeasureLengthInFrames(trackController.getPlaybackSettings()) /
                        (trackController.getPlaybackSettings().getTimeSignature().getUpper() / 2) ?
                (int) (AudioUtility.calculateMeasureLengthInFrames(trackController.getPlaybackSettings()) /
                        (trackController.getPlaybackSettings().getTimeSignature().getUpper() / 2)) :
                AudioTrack.getMinBufferSize(trackController.getPlaybackSettings().getSampleRate(),
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

        playbackOn = false;
        recordingOn = false;
        clickVolume = DEFAULT_CLICK_VOLUME;
    }

    public void addMarker(PlaybackNotificationMarker marker){
        if(notificationMarkers.containsKey(String.valueOf(marker.getNote().getTimeStamp()))){
            String newTimeStampKey = null;
            for(int i = 0; i < trackController.getTracks().size(); i++){ //number of tracks times 2 (+1 and -1) number of tracks should be enough in case of overlapped input times per track
                if(!notificationMarkers.containsKey((String.valueOf(marker.getNote().getTimeStamp() + i)))){
                    notificationMarkers.put(String.valueOf(marker.getNote().getTimeStamp() + i), marker);
                }else{
                    if(!notificationMarkers.containsKey(String.valueOf(marker.getNote().getTimeStamp() - i))){
                        notificationMarkers.put(String.valueOf(marker.getNote().getTimeStamp() - i), marker);
                    }
                }
            }
        }else{
            notificationMarkers.put(String.valueOf(marker.getNote().getTimeStamp()), marker);
        }
    }

    private Thread startPlaybackClockTask(HashMap<String, PlaybackNotificationMarker> playbackMarkers){
        HandlerThread playbackThread = new HandlerThread(PLAYBACK_CLOCK_THREAD_NAME);
        playbackThread.start();

        AudioTrack playbackClockTrack = createTimingTrack();
        playbackClockTask = playbackMarkers == null ?
                new PlaybackClockTask(playbackClockTrack, timingHandler)
                : new PlaybackClockTask(playbackClockTrack, timingHandler, playbackMarkers);


        playbackClockHandler = new PlaybackClockHandler(playbackThread.getLooper(), playbackClockTask);
        timingHandler.setPlaybackClockHandler(playbackClockHandler);
        return playbackThread;
    }

    public void startPlayback(ArrayList<Track> recordedTracks){
        playbackOn = true;
       if(recordedTracks == null){
           runningTasks.put(PLAYBACK_TASK_KEY, startPlaybackClockTask(null));
           playbackClockHandler.sendEmptyMessage(PLAYBACK_START_CLOCK);
       }else{
           runningTasks.put(PLAYBACK_TASK_KEY, startPlaybackClockTask(tracksToMarkers(recordedTracks)));
           playbackClockHandler.sendEmptyMessage(PLAYBACK_START_CLOCK);
       }
    }

    private HashMap<String, PlaybackNotificationMarker> tracksToMarkers(ArrayList<Track> tracks){
        HashMap<String, PlaybackNotificationMarker> trackMarkers = new HashMap<>();
        for(int i = 0; i < tracks.size(); i++){
            for(int j = 0; j < tracks.get(i).getNotes().size(); j++){
                trackMarkers.put(String.valueOf(tracks.get(i).getNotes().get(j).getTimeStamp()),
                        new PlaybackNotificationMarker(tracks.get(i).getNotes().get(j), i));
            }
        }
        return trackMarkers;
    }

    private Thread startClickTrackTask(){
        HandlerThread timingNotificationThread = new HandlerThread(TIMING_NOTIFICATION_THREAD_NAME);
        timingNotificationThread.start();
        AudioTrack clickTrack = createTimingTrack();
        timingHandler = new TimingTaskHandler(timingNotificationThread.getLooper(), this);
        clickTrackTask = new ClickTrackTask(uiHandler, timingHandler, clickTrack, bufferSizeInBytes, trackController.getPlaybackSettings());
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(clickTrackTask);
        return clickTrackTask.getCurrentThread();
    }

    private void startClickTrack(){
        runningTasks.put(CLICK_TRACK_TASK_KEY, startClickTrackTask());
    }

    public void onRecordStart(){
        recordingOn = true;
        if(trackController.getPlaybackSettings().isPreCountOn()){
            startClickTrack();
        }else{
            startClickTrack();
            startPlayback(null);
        }
    }

    public AudioTrack createTimingTrack(){
        AudioTrack audioTrack = null;
        AudioTrack.Builder atBuilder = new AudioTrack.Builder();

        try{
            atBuilder
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                            .build()
                )
                .setAudioFormat(
                        new AudioFormat.Builder()
                            .setSampleRate(trackController.getPlaybackSettings().getSampleRate())
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_DEFAULT)
                            .build()
                )
                .setSessionId(audioSessionID)
                .setTransferMode(AudioTrack.MODE_STREAM);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) //if version 29
                atBuilder
                        .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
                        .setOffloadedPlayback(false);

            //TODO: Add setEncapsulationMode for Android 30
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }

        try{
            audioTrack = atBuilder.build();
        }catch(UnsupportedOperationException e){
            e.printStackTrace();
        }

      //  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


       /* }else {
            audioTrack = new AudioTrack(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setFlags(AudioAttributes.FLAG_HW_AV_SYNC)
                    .build(),
                    new AudioFormat.Builder()
                            .setSampleRate(trackController.getPlaybackSettings().getSampleRate())
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build(),
                    AudioTrack.getMinBufferSize(trackController.getPlaybackSettings().getSampleRate(),
                            AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT),
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE);
        }*/




        return audioTrack;
    }

    public void stopPlayback(){
        playbackOn = false;
        playbackClockTask.stopPlayback();
    }

    public void stop(){
        recordingOn = false;
        playbackOn = false;
      /*  if(runningTasks.size() > 0){
          for(int i = 0; i < runningTasks.size(); i++){
                  runningTasks.get(i).interrupt();
          }
        }*/

        playbackClockTask.stopPlayback();
        clickTrackTask.stopClickTrack();

    }



   /* private void toggleClickSound(){
        if(trackController.getPlaybackSettings().isClickSoundOn()){
            trackController.getPlaybackSettings().setClickSoundOn(false);
            clickTrack.setVolume(0);
        }else{
            trackController.getPlaybackSettings().setClickSoundOn(true);
            clickTrack.setVolume(clickVolume);
        }
    }*/

    public TimingTaskHandler getTimingHandler() {
        return timingHandler;
    }

    public TrackController getTrackController() {
        return trackController;
    }

    public HashMap<String, PlaybackNotificationMarker> getNotificationMarkers() {
        return notificationMarkers;
    }

    public MainActivity.UIHandler getUiHandler() {
        return uiHandler;
    }

    public interface TimingClockListener {
        void startLoopPlayback();
        void markerNotification(Note note);
        void sortNotificationMarkers(); //called before last silence of loop is written to prepare next loop's markers
    }





}
