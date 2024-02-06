package com.shermdev.will.mpcgo;

import android.util.Log;

/**
 * Created by Will on 3/5/2017.
 */

public class AudioUtility {

    public static final int DEFAULT_TEMPO = 100;
    public static final int TIME_SIG_DEFAULT = 4;
    public static final int DEFAULT_BEATS_PER_MEASURE = 4;
    public static final int DEFAULT_SAMPLE_RATE = 44100;


    private AudioUtility() {

    }

    public static byte[] get16BitPcm(double[] samples) {
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

    //1 frame = 1 sample : 44,100 samples per second
    public static long calculateMeasureLengthInFrames(PlaybackSettings playbackSettings){
        //beats per min divided by 60 sec/min
        return playbackSettings.getSampleRate() * 60 / playbackSettings.getTempo() * playbackSettings.getTimeSignature().getUpper();
    }

    public static long calculateMeasureLengthInMilliSec(PlaybackSettings playbackSettings){
        return (60 / playbackSettings.getTempo()) * playbackSettings.getTimeSignature().getUpper() * 1000;
    }
}
