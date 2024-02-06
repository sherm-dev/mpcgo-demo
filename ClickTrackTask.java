package com.shermdev.will.mpcgo;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by Will on 4/29/2017.
 */

public class ClickTrackTask implements Runnable {
    private static final String STRONG_CLICK = ClickGenerator.STRONG_CLICK;
    private static final String WEAK_CLICK = ClickGenerator.WEAK_CLICK;
    private static final String SILENCE = ClickGenerator.SILENCE;


    public static final int CLICK_NOTIFICATION_MSG = 77700;
    public static final int LOOP_NOTIFICATION_MSG = 77701;
    public static final int START_PLAYBACK_CLOCK_MSG = 77702;
    public static final int PRE_COUNT_NOTIFICATION_MSG = 77703;
    public static final int SILENT_BEAT = 0;

    private MainActivity.UIHandler uiHandler;
    private final AudioTrack clickTrack;
    private boolean clickTrackOn;
    private int bufferSize, beatCounter, measureCounter;
    private ClickGenerator clickGenerator;
    private String clickType;
    private PlaybackSettings playbackSettings;
    private Thread currentThread;
    private TimingTaskHandler timingTaskHandler;
    private Handler transportHandler;

    public ClickTrackTask(MainActivity.UIHandler uiHandler, TimingTaskHandler timingTaskHandler, AudioTrack clickTrack,
                          int bufferSize, PlaybackSettings playbackSettings){
        this.uiHandler = uiHandler;
        this.timingTaskHandler = timingTaskHandler;
        this.clickTrack = clickTrack;
        this.bufferSize = bufferSize;
        this.playbackSettings = playbackSettings;
        clickTrackOn = false;
        beatCounter = 0;
        clickGenerator = new ClickGenerator(playbackSettings);
        clickType = STRONG_CLICK;
    }

    private int calculateBufferLengthInBytes(){
        return AudioTrack.getMinBufferSize(playbackSettings.getSampleRate(),
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT) * 4 <
                AudioUtility.calculateMeasureLengthInFrames(playbackSettings) /
                        (playbackSettings.getTimeSignature().getUpper() / 2) ?
                (int) (AudioUtility.calculateMeasureLengthInFrames(playbackSettings) /
                        (playbackSettings.getTimeSignature().getUpper() / 2)) :
                AudioTrack.getMinBufferSize(playbackSettings.getSampleRate(),
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
    }

    public void stopClickTrack(){
        clickTrackOn = false;
        clickTrack.flush();
        clickTrack.stop();
        clickTrack.release();
    }

    private void beatIncrement(){
        if(beatCounter < playbackSettings.getTimeSignature().getUpper()){
            beatCounter++;
        }else {
            beatCounter = 1;
            measureCounter++;
        }
    }

    private void switchClickType(){
        if(clickType.equals(SILENCE)){
            clickType = beatCounter == 1 ? STRONG_CLICK : WEAK_CLICK;
        }else{
            clickType = SILENCE;
        }
    }

    private void writeComplete(){
        if(!clickType.equals(SILENCE)){
            //playbackListener.clickNotification(beatCounter);
            uiHandler.sendMessage(uiHandler.obtainMessage(CLICK_NOTIFICATION_MSG, beatCounter));

            if(playbackSettings.isPreCountOn()){
                if(measureCounter == playbackSettings.getPreCountLength() && beatCounter == 1) timingTaskHandler.sendEmptyMessage(START_PLAYBACK_CLOCK_MSG);

                if(measureCounter < playbackSettings.getPreCountLength()) uiHandler.sendEmptyMessage(PRE_COUNT_NOTIFICATION_MSG);
            }

            if(playbackSettings.isLoopOn()){
                if(measureCounter % playbackSettings.getLoopLength() == 0
                        && beatCounter == 1){
                    if(playbackSettings.isPreCountOn()){
                        if(measureCounter > playbackSettings.getPreCountLength()) timingTaskHandler.sendEmptyMessage(LOOP_NOTIFICATION_MSG);
                    }else{
                        if(measureCounter > playbackSettings.getLoopLength()) timingTaskHandler.sendEmptyMessage(LOOP_NOTIFICATION_MSG);
                    }
                }
            }
        }else{
            uiHandler.sendMessage(uiHandler.obtainMessage(CLICK_NOTIFICATION_MSG, SILENT_BEAT));
            beatIncrement();
        }


        switchClickType();
    }

    public Thread getCurrentThread() {
        return currentThread;
    }

    @Override
    public void run() {
        currentThread = Thread.currentThread();
        clickType = STRONG_CLICK;
        beatCounter = 1;
        measureCounter = 0;


        if(clickTrack != null){
            clickTrackOn = true;

            if(clickTrack.getState() == AudioTrack.STATE_INITIALIZED){
                clickTrack.play();
            }

            if(clickTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                while (clickTrackOn) {
                    int index = 0;
                    byte[] clickSound = clickGenerator.getClickSounds().get(clickType);
                    byte[] chunk;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        do {
                            chunk = new byte[bufferSize];
                            for (int i = 0; i < bufferSize; i++) {
                                chunk[i] = clickSound[index + i];
                                //TODO: Uncomment below to integrate timer with metronome clock
                                if((index + i) % 44 == 0) uiHandler.sendEmptyMessage(TimingTaskHandler.CLOCK_UPDATE_MSG);
                            }
                            index += clickTrack.write(chunk, 0, chunk.length, AudioTrack.WRITE_BLOCKING);
                        } while (index + chunk.length <= clickSound.length);

                        writeComplete();
                    }else{
                        do {
                            chunk = new byte[bufferSize];
                            for (int i = 0; i < bufferSize; i++) {
                                chunk[i] = clickSound[index + i];
                            }
                            index += clickTrack.write(chunk, 0, chunk.length);
                        } while (index + chunk.length <= clickSound.length);

                        writeComplete();
                    }
                }
            }
        }
    }
}
