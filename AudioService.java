package com.shermdev.will.mpcgo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;

public class AudioService extends Service {
    public static final int MSG_SERVICE_START_PLAYBACK = 112222111;
    public static final int MSG_SERVICE_START_RECORD = 112222112;
    public static final int MSG_SERVICE_STOP_AUDIO = 112222113;
    public static final int MSG_SERVICE_STOP_RECORD = 112222114;
    public static final int MSG_SERVICE_PLAY_SAMPLE = 112222115;
    private static final String HANDLER_THREAD_NAME = "audio_service_handler_thread";
    private TimingTaskManager timingTaskManager;
    private HandlerThread handlerThread;
    private boolean isBound;
    private AudioServiceHandler audioServiceHandler;
    private final IBinder binder = new AudioServiceBinder();
    private boolean isPlaying, isRecording;

    public AudioService() {
        this.timingTaskManager = null;
        this.isBound = false;
        this.audioServiceHandler = null;
        this.isRecording = false;
        this.isPlaying = false;
    }

    public class AudioServiceBinder extends Binder {
        AudioService getService(){
            return AudioService.this;
        }
    }

    protected static class AudioServiceHandler extends Handler{
        private final WeakReference<AudioService> wRefAudioService;

        public AudioServiceHandler(Looper looper, AudioService _audioService) {
            super(looper);
            this.wRefAudioService = new WeakReference<>(_audioService);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MSG_SERVICE_START_PLAYBACK:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wRefAudioService.get().startPlayback();
                        }
                    });
                    break;
                case MSG_SERVICE_START_RECORD:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wRefAudioService.get().startRecording();
                        }
                    });
                    break;
                case MSG_SERVICE_STOP_RECORD:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wRefAudioService.get().stopRecording();
                        }
                    });
                    break;
                case MSG_SERVICE_STOP_AUDIO:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wRefAudioService.get().stopAudio();
                        }
                    });
                    break;
                case MSG_SERVICE_PLAY_SAMPLE:
                    if(msg.obj != null){
                        final SamplerPad samplerPad = (SamplerPad) msg.obj;
                        final float pressure = (float) (msg.arg1 / 100);
                        post(new Runnable() {
                            @Override
                            public void run() {
                                wRefAudioService.get().playSample(samplerPad, pressure);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        handlerThread = new HandlerThread(HANDLER_THREAD_NAME);
        handlerThread.start();
        this.audioServiceHandler = new AudioServiceHandler(handlerThread.getLooper(), this);
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handlerThread.isAlive()) handlerThread.quitSafely();
    }

    public void startPlayback(){
        isPlaying = true;
        if(timingTaskManager.getTrackController().getTracks().size() > 0){
              timingTaskManager.startPlayback(timingTaskManager.getTrackController().getTracks());
        }else{
            Toast recordToast = new Toast(getApplicationContext());
            recordToast.setText("You Haven't Recorded Anything Yet");
            recordToast.show();
        }
    }

    public void startRecording(){
        isRecording = true;
        timingTaskManager.onRecordStart();
    }

    public void stopRecording(){
        isRecording = false;
        timingTaskManager.getTrackController().stopRecordingNoteInput();
    }

    public void stopAudio(){
        isPlaying = false;
        if(isRecording) stopRecording();
        timingTaskManager.stopPlayback();
    }

    public void playSample(SamplerPad samplerPad, float pressure){
        PlaybackNotificationMarker notificationMarker = timingTaskManager.getTrackController().recordNoteInput(
                samplerPad.getSample(),
                samplerPad.getSample().getSampleGain() * pressure,
                samplerPad.getId()
        );

        timingTaskManager.getTimingHandler().sendMessage(
                timingTaskManager.getTimingHandler().obtainMessage(TimingTaskHandler.ADD_MARKER_MSG, notificationMarker)
        );
    }

    public AudioServiceHandler getAudioServiceHandler() {
        return audioServiceHandler;
    }

    public boolean isBound() {
        return isBound;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setTimingTaskManager(TimingTaskManager timingTaskManager) {
        this.timingTaskManager = timingTaskManager;
    }
}
