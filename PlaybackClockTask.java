package com.shermdev.will.mpcgo;

import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Will on 4/29/2017.
 */

public class PlaybackClockTask {

    public static final int PLAYBACK_CLOCK_WRITE_COMPLETE = 34345;

    private TimingTaskHandler timingTaskHandler;
    private AudioTrack playbackTrack;
    private HashMap<String, PlaybackNotificationMarker> markers;
    private long elapsedBytes = 0;
    private boolean playbackOn;
    private int lengthAdjustWriteCounter;
    private int byteLength;

    public PlaybackClockTask(AudioTrack playbackTrack, TimingTaskHandler timingTaskHandler)  {
        this.timingTaskHandler = timingTaskHandler;
        this.playbackTrack = playbackTrack;
        markers = new HashMap<>();
    }



    public PlaybackClockTask(AudioTrack playbackTrack, TimingTaskHandler timingTaskHandler,
                             HashMap<String, PlaybackNotificationMarker> markers)  {
        this.playbackTrack = playbackTrack;
        this.markers = markers;
        this.timingTaskHandler = timingTaskHandler;
    }




    public void stopPlayback(){
        playbackOn = false;
        playbackTrack.flush();
        playbackTrack.stop();
        playbackTrack.release();
    }




    private void writeComplete(){
        lengthAdjustWriteCounter++;
        timingTaskHandler.sendMessageAtFrontOfQueue(timingTaskHandler.obtainMessage(PLAYBACK_CLOCK_WRITE_COMPLETE));
        if(lengthAdjustWriteCounter == 10) lengthAdjustWriteCounter = 0;
    }



    private byte[] get16BitPcm(double[] samples) {
        byte[] generatedSound = new byte[2 * samples.length];
        int index = 0;
        for (double sample : samples) {
            // scale to maximum amplitude
            short maxSample = (short) ((sample * Short.MAX_VALUE));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[index++] = (byte) (maxSample & 0x00ff);
            generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

        }
        return generatedSound;
    }



    public void startClock() {
        Log.i("PLAYBACKCLOCK", "PlaybackClock RUN");
        elapsedBytes = 0;
        playbackOn = true;
        lengthAdjustWriteCounter = 0;
        byteLength = 0;


        if(playbackTrack.getState() == AudioTrack.STATE_INITIALIZED){
            playbackTrack.play();
            playbackTrack.setVolume(0);
            writeClockTrack();
        }


    }

    public void writeClockTrack(){


        //the byte length (length of the byte that is the silence written to the audio track for 1 millisecond or (sample rate / 1000)
        //this corrects for the need for an integer length of the written byte, but the samples per millisecond being 44.1
        //thus, every 10th write, on the 10th write, the length of the byte will be 45 instead of 44 (samples/millisecond rounded)
        byteLength = lengthAdjustWriteCounter == 9 ?
                Math.round(playbackTrack.getSampleRate() / 1000 )
                : Math.round(playbackTrack.getSampleRate() / 1000) + 1;
        byte[] chunk;
        byte[] silenceByte = get16BitPcm(new double[byteLength]);
        if(playbackTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    chunk = new byte[silenceByte.length];
                    for (int i = 0; i < silenceByte.length; i++) {
                        chunk[i] = silenceByte[i];
                    }

                    elapsedBytes += playbackTrack.write(chunk, 0, chunk.length, AudioTrack.WRITE_BLOCKING);
                   // Log.i("ELAPSEDBYTES", String.valueOf(elapsedBytes));
                    //index is effectively 1 frame, or 4 bytes each (a silence byte is double the length of the inital silence double)
                    writeComplete();

            }else{

                    chunk = new byte[silenceByte.length];
                    for (int i = 0; i < silenceByte.length; i++) {
                        chunk[i] = silenceByte[i];
                    }

                    elapsedBytes += playbackTrack.write(chunk, 0, chunk.length);

                    // elapsedBytes = convertFrameTimeToMilliSec(index); //index is effectively 1 frame, or 4 bytes each (a silence byte is double the length of the inital silence double)
                    writeComplete();


            }
        }
    }
}
