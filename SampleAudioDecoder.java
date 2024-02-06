package com.shermdev.will.mpcgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class SampleAudioDecoder {
    public SampleAudioDecoder(){
    }

    protected void decodeSample(Sample sample, FileInputStream fis, Context context) throws IOException{
        MediaExtractor mediaExtractor = new MediaExtractor();
        mediaExtractor.setDataSource(fis.getFD());

        if (Objects.requireNonNull(mediaExtractor).getTrackCount() > 0) {
            for (int i = 0; i < Objects.requireNonNull(mediaExtractor).getTrackCount(); i++) {
                byte[] sampleAudioByte = new byte[(int) calculateSampleByteLength(
                        mediaExtractor.getTrackFormat(i).getInteger(MediaFormat.KEY_SAMPLE_RATE),
                        mediaExtractor.getTrackFormat(i).getLong(MediaFormat.KEY_DURATION)
                )];

                ByteBuffer inputBuffer = ByteBuffer.allocate(
                        (int) calculateSampleByteLength(
                                mediaExtractor.getTrackFormat(i).getInteger(MediaFormat.KEY_SAMPLE_RATE),
                                mediaExtractor.getTrackFormat(i).getLong(MediaFormat.KEY_DURATION)
                        )
                );

                sample.setMimeType(mediaExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME));
                sample.setSampleSamplingRate(mediaExtractor.getTrackFormat(i).getInteger(MediaFormat.KEY_SAMPLE_RATE));
                sample.setChannels(mediaExtractor.getTrackFormat(i).getInteger(MediaFormat.KEY_CHANNEL_COUNT));
                sample.setSampleLength(mediaExtractor.getTrackFormat(i).getLong(MediaFormat.KEY_DURATION));

                while (mediaExtractor.readSampleData(inputBuffer, inputBuffer.arrayOffset()) > 0) {
                    if (inputBuffer.hasArray())
                        System.arraycopy(
                                inputBuffer.array(),
                                inputBuffer.arrayOffset(),
                                sampleAudioByte,
                                inputBuffer.arrayOffset(),
                                inputBuffer.array().length
                        );

                    mediaExtractor.advance();
                }

                mediaExtractor.release();
                mediaExtractor = null;
                Log.i("MPCGO", "SAMPLE AUDIO BYTE");
                Log.i("MPCGO", Arrays.toString(sampleAudioByte));
                sample.setSampleByte(sampleAudioByte); //set the samples Byte[]
                sample.setLoaded(true);
            }
        }

    }

    private long calculateSampleByteLength(int samplingRate, long durationInMicroSecs){
        return samplingRate * durationInMicroSecs / 1000000; //microsecs divided by one million to get seconds
    }
}
