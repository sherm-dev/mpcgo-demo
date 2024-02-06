package com.shermdev.will.mpcgo;


import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Will on 7/3/2015.
 */
public class ClickGenerator {

    private static final double OFF_BEAT_FREQ = 1220;
    private static final double DOWN_BEAT_FREQ = 2440;

    public static final String STRONG_CLICK = "STRONG";
    public static final String WEAK_CLICK = "WEAK";
    public static final String SILENCE = "SILENCE";

    private PlaybackSettings playbackSettings;


    private HashMap<String, byte[]> clickSounds;

    public ClickGenerator(PlaybackSettings playbackSettings){
        this.playbackSettings = playbackSettings;
        clickSounds = createClickSounds();
    }

    private HashMap<String, byte[]> createClickSounds(){
        long beatLength = AudioUtility.calculateMeasureLengthInFrames(playbackSettings) /
                playbackSettings.getTimeSignature().getUpper() * 2; //2 corrects for length converting from Frame to byte (maybe 4)
        int clickLength = (int) (beatLength / 2);
        double[] strongBeatNote = getSineWave(clickLength, playbackSettings.getSampleRate(), DOWN_BEAT_FREQ);
        double[] weakBeatNote = getSineWave(clickLength, playbackSettings.getSampleRate(), OFF_BEAT_FREQ);
        HashMap<String, byte[]> clickMap = new HashMap<>(3);
        double[] strongClick = new double[strongBeatNote.length];
        double[] weakClick = new double[strongBeatNote.length];
        double[] silenceClick = new double[(int) (beatLength - strongBeatNote.length)];

        for (int i = 0; i < strongBeatNote.length; i++) {
            strongClick[i] = strongBeatNote[i];
            weakClick[i] = weakBeatNote[i];
        }

        clickMap.put(STRONG_CLICK, get16BitPcm(strongClick));
        clickMap.put(WEAK_CLICK, get16BitPcm(weakClick));

        for (int j = 0; j < silenceClick.length; j++) {
            silenceClick[j] = 0;
        }

        clickMap.put(SILENCE, get16BitPcm(silenceClick));
        Log.i("CLICKS", String.valueOf(strongClick.length));
        Log.i("CLICKS", String.valueOf(weakClick.length));
        Log.i("CLICKS", String.valueOf(silenceClick.length));
        return clickMap;
    }


    public double[] getSineWave(int samples,int sampleRate,double frequencyOfTone) {
        double[] sample = new double[samples];
        for (int i = 0; i < samples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequencyOfTone));
        }
        return sample;
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

    public void writeSound(String clickType, AudioTrack track) {
        byte[] samples = clickSounds.get(clickType);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            track.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
        }else{
            track.write(samples, 0, samples.length);
        }
    }

    public int writeSoundBits(byte[] samples, AudioTrack track){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            track.write(samples, 0, samples.length, AudioTrack.WRITE_BLOCKING);
        }else{
            track.write(samples, 0, samples.length);
        }
        return samples.length;
    }

    public HashMap<String, byte[]> getClickSounds() {
        return clickSounds;
    }
}
