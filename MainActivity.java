 package com.shermdev.will.mpcgo;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

//TODO: use protected functions
 public class MainActivity
        extends AppCompatActivity
        implements PlaybackControlsFragment.OnPlaybackControlsInteract,
        SamplerPadFragment.OnPadPressed,
        MetronomeFragment.MetronomeControlsListener,
        VoiceRecorderFragment.OnVoiceRecordInteractionListener,
        SoundBankDisplayFragment.OnSoundBankDisplayInteractionListener,
        FaderNavigationFragment.OnFaderNavigationInteract {
    private static final String TAG_SAMPLER_FRAGMENT = "sampler_fragment_tag";
    private static final String TAG_SOUNDBANK_NAMES_FRAGMENT = "soundbank_names_fragment_tag";
    private static final String UI_HANDLER_THREAD_NAME = "ui_handler_thread";
    private static final int PERMISSION_STORAGE_REQUEST_CODE = 7718876;
    private static final int PERMISSION_AUDIO_REQUEST_CODE = 7718877;
    private static final int PERMISSION_INITIAL_REQUEST_CODE = 7718878;
    private HandlerThread uiHandlerThread;
    private UIHandler uiHandler;
    private Visualizer visualizer;

    private PlaybackSettings playbackSettings;
    private SamplerPadFragment samplerFragment;
    private FaderFragment faderFragment;
    private PlaybackControlsFragment playbackControlsFragment;
    private TransportFragment transportFragment;
    private VoiceRecorderFragment voiceRecorderFragment;
    private MetronomeFragment metronomeFragment;
    private SoundBankDisplayFragment soundBankDisplayFragment;
    private SamplerPadFragment samplerPadFragment;
    private VisualizerBarFragment visualizerBarFragment;

    private SoundBankManager soundBankManager;
    private AudioService audioService;
    private AudioService.AudioServiceHandler audioServiceHandler;

    private ServiceConnection audioServiceConnection;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uiHandlerThread = new HandlerThread(UI_HANDLER_THREAD_NAME);
        uiHandlerThread.start();
        uiHandler = new UIHandler(uiHandlerThread.getLooper(), this);


       // if(savedInstanceState.getBundle(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA) != null)
         //   soundBankManager.setSoundBank((SoundBank) SoundBank.soundBankFromBundle(Objects.requireNonNull(savedInstanceState.getBundle(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA))));


        playbackSettings = PlaybackSettings.defaultPlaybackSettings();
        soundBankManager = new SoundBankManager(
                getApplicationContext(),
                new Handler(uiHandlerThread.getLooper(),
                        new MainSoundBankManagerHandlerCallback(this))
        );

        audioServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                AudioService.AudioServiceBinder binder = (AudioService.AudioServiceBinder) service;
                audioService = binder.getService();
                audioService.setTimingTaskManager(
                        new TimingTaskManager(playbackSettings, soundBankManager, uiHandler)
                );
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };

        metronomeFragment = MetronomeFragment.newInstance(playbackSettings.getTempo(),
                playbackSettings.getTimeSignature());
        getSupportFragmentManager().beginTransaction().replace(R.id.metronomeFrame, metronomeFragment).commit();

        transportFragment = TransportFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.transportFrame, transportFragment).commit();

        playbackControlsFragment = PlaybackControlsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.playbackControlsFrame, playbackControlsFragment).commit();

        voiceRecorderFragment = VoiceRecorderFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.voiceRecorderFrame, voiceRecorderFragment).commit();


       // visualizerBarFragment = VisualizerBarFragment.newInstance(soundBankManager.getSoundPoolManager().generateAudioSessionID(), playbackSettings.getSampleRate()); //audio session id 0 for main output
       // getSupportFragmentManager().beginTransaction().replace(R.id.visualizer_frame, visualizerBarFragment).commit();


        requestMediaStoragePermissions();
    }




    private void requestMediaStoragePermissions(){
        requestPermissions(
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                },
                PERMISSION_STORAGE_REQUEST_CODE
        );
    }

    private void requestAudioPermissions(){
        requestPermissions(
                new String[] {
                        Manifest.permission.RECORD_AUDIO

                },
                PERMISSION_AUDIO_REQUEST_CODE
        );
    }

     private void samplerLayout(SoundBank soundBank){
         soundBankDisplayFragment = SoundBankDisplayFragment.newInstance(soundBank.getName());
         samplerFragment = SamplerPadFragment.newInstance(soundBank.getSamples());
         getSupportFragmentManager().beginTransaction().replace(R.id.soundBankNameDisplayFrame,  soundBankDisplayFragment).commit();
         getSupportFragmentManager().beginTransaction()
                 .replace(R.id.samplerFrame, samplerFragment, TAG_SAMPLER_FRAGMENT)
                 .setReorderingAllowed(true)
                 .addToBackStack(null)
                 .commit();
     }

     private void showSoundBankList(ArrayList<String> soundBankNames){
         getSupportFragmentManager().beginTransaction()
                 .replace(R.id.samplerFrame, SoundBankMenuFragment.newInstance(soundBankNames), TAG_SOUNDBANK_NAMES_FRAGMENT)
                 .setReorderingAllowed(true)
                 .addToBackStack(null)
                 .commit();
     }

     private void deleteSoundBank(SoundBank sb){
         soundBankManager.getDatabaseHelper().deleteSoundBank(sb);
     }

     //wrapper for soundBankManager.loadSoundBank in order to check permissions
     protected void loadSoundBank(SoundBank sb){
         if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
             soundBankManager.loadSoundBank(sb);
         }else {
             soundBankManager.setSoundBank(sb);
             requestMediaStoragePermissions();
         }
     }

    @Override
    protected void onStart() {
        super.onStart();
        Intent bindIntent = new Intent(getApplicationContext(), AudioService.class);
        bindService(bindIntent, audioServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(audioServiceConnection);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch(requestCode){
                case PERMISSION_STORAGE_REQUEST_CODE:
                    soundBankManager.soundBankLaunch();
                    break;
                case PERMISSION_AUDIO_REQUEST_CODE:
                    //TODO: Start Record
                    break;
                case PERMISSION_INITIAL_REQUEST_CODE:
                    soundBankManager.soundBankLaunch();
                    break;
                default:
                    break;
            }
        }else {
            // Explain to the user that the feature is unavailable because
            // the features requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
            //TODO: Explanations
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SoundBankEditActivity.RESULT_CODE_SOUNDBANK_EDIT && resultCode == RESULT_OK && data != null){
            if(data.getBundleExtra(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA) != null && data.getBundleExtra(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA) != null){
                SoundBank sb = SoundBank.soundBankFromBundle(Objects.requireNonNull(data.getBundleExtra(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA)));
                soundBankManager.getDatabaseHelper().editSoundBank(sb);
            }
        }
    }

    @Override
    public void onRecordButtonPress(boolean recordingOn) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            if(recordingOn){
                // if(visualizerBarFragment != null) visualizerBarFragment.initializeVisualizer(); //initialize visualizer
                audioService.getAudioServiceHandler().sendMessage(audioService.getAudioServiceHandler().obtainMessage(AudioService.MSG_SERVICE_START_RECORD));
            }else{
                audioService.getAudioServiceHandler().sendMessage(audioService.getAudioServiceHandler().obtainMessage(AudioService.MSG_SERVICE_STOP_RECORD));
            }
        }
    }

    @Override
    public void onPlayButtonPress(boolean playbackOn) {
        audioService.getAudioServiceHandler().sendMessage(audioService.getAudioServiceHandler().obtainMessage(AudioService.MSG_SERVICE_START_PLAYBACK));
    }

    @Override
    public void onStopButtonPress() {
        audioService.getAudioServiceHandler().sendMessage(
                audioService.getAudioServiceHandler().obtainMessage(AudioService.MSG_SERVICE_STOP_AUDIO)
        );
    }

    @Override
    public void onSamplerPadPressed(SamplerPad samplerPad, final float pressure) {
        if(!audioService.isRecording()){
            new Thread(new PlaySampleRunnable(
                    soundBankManager.getSoundPoolManager().getSoundPool(),
                    samplerPad.getSample(),
                    samplerPad.getSample().getSampleGain() * pressure)).start();
        }else{
            audioService.getAudioServiceHandler().sendMessage(
                    audioService.getAudioServiceHandler().obtainMessage(
                            AudioService.MSG_SERVICE_PLAY_SAMPLE,
                            (int) pressure * 100,
                            0,
                            samplerPad
                    )
            );
        }
    }

   /* @Override
    public void loopBeginNotifier() {
        //noteInputController.beginLoop();
    }

    @Override
    public void startNoteInput() {
        //  noteInputController.startRecordingNoteInput();
        // playbackControlsFragment.toggleRecordButtonPreCount();
    }

    @Override
    public void loopPlaybackOn() {
        //playbackControlsFragment.playbackOn();
    }

    @Override
    public void recordPreCountNotifier() {
        //playbackControlsFragment.toggleRecordButtonPreCount();
    }

    @Override
    public void clickNotification(int beatCounter) {
        if(beatCounter == TimingTaskManager.SILENT_BEAT){
         //   transportFragment.clickLightToggle();
        }else{
       //     transportFragment.beatNotification(beatCounter);
        }
    }

    @Override
    public void timeNotification() {
        // transportFragment.incrementTimeDisplay();
    }*/

    @Override
    public void onMetronomeButtonPressed(boolean isMetronomeOn) {

    }

    @Override
    public void onVoiceRecord() {

    }

    @Override
    public void onSoundBankNameDisplayInteraction() {
        soundBankManager.getDatabaseHelper().retrieveSoundBanks();
    }

    @Override
    public void onSoundBankEditInteraction() {
        Intent soundBankEditIntent = new Intent();
        soundBankEditIntent.setClass(getApplicationContext(), SoundBankEditActivity.class);
        soundBankEditIntent.putExtra(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA, soundBankManager.getSoundBank().soundBankToBundle());
        startActivityForResult(soundBankEditIntent, SoundBankEditActivity.RESULT_CODE_SOUNDBANK_EDIT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uiHandlerThread.quitSafely();
    }

    @Override
    public void faderNavigation() {
        //TODO: launch fader activity
        /*faderFragment = FaderFragment.newInstance(trackController.getTrackCount(), trackController.getTrackVolumes());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        R.id.samplerFrame,
                        faderFragment
                )
                .commit();*/
    }

    private static class MainSoundBankManagerHandlerCallback implements Handler.Callback {
        private final WeakReference<MainActivity> wActRef;

        public MainSoundBankManagerHandlerCallback(Activity activity) {
            this.wActRef = new WeakReference<>((MainActivity) activity);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case SampleFileTransferTask.DEFAULT_SOUNDBANK_FILES_TRANSFERRED:
                    if(msg.obj != null){
                        final SoundBank sb = (SoundBank) msg.obj;

                        Log.i("MPCGO", String.valueOf("DEFAULT FILES TRANSFERRED MSG: ").concat(msg.obj.toString()));
                        wActRef.get().uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().soundBankManager.getDatabaseHelper().createSoundBank(sb);
                            }
                        });
                    }

                    break;
                case SoundBankManager.MSG_SOUNDBANK_LOADED:
                    if(msg.obj != null) {
                        final SoundBank sb = (SoundBank) msg.obj;
                        Log.i("MPCGO", String.valueOf("SOUNDBANK LOADED MSG: ").concat(sb.toString()));
                        wActRef.get().uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().samplerLayout(sb);
                            }
                        });
                    }
                    break;
                case SoundBankManager.MSG_SOUNDBANK_DELETED:
                    if(msg.obj != null){
                        final SoundBank sb = (SoundBank) msg.obj;
                        wActRef.get().uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().soundBankManager.getDatabaseHelper().deleteSoundBank(sb);
                            }
                        });
                    }
                    break;
                case SoundBankManager.MSG_SOUNDBANK_LIST:
                    final ArrayList<String> soundBankNames = msg.getData().getStringArrayList(MPCDatabaseManager.SOUNDBANK_LIST_NAMES_KEY);
                    wActRef.get().uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            wActRef.get().showSoundBankList(soundBankNames);
                        }
                    });
                    break;
                case SoundBankManager.MSG_SOUNDBANK_CREATED:
                case SoundBankManager.MSG_SOUNDBANK_EDITED:
                    if(msg.obj != null){
                        final SoundBank sb = (SoundBank) msg.obj;
                        Log.i("MPCGO", String.valueOf("SOUNDBANK CREATED/EDITED MSG: ").concat(sb.toString()));
                        wActRef.get().uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().soundBankManager.getDatabaseHelper().retrieveSoundBank(sb.getName());
                            }
                        });
                    }
                    break;
                case SoundBankManager.MSG_SOUNDBANK_RETRIEVED:
                    if(msg.obj != null){
                        final SoundBank sb = (SoundBank) msg.obj;
                        Log.i("MPCGO", "SOUNDBANK RETRIEVED MSG: " + sb.toString());
                        wActRef.get().uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().loadSoundBank(sb);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
    }

    public static class UIHandler extends Handler{
        private final WeakReference<MainActivity> wActRef;

        public UIHandler(Looper looper, MainActivity activityRef) {
            super(looper);
            wActRef = new WeakReference<MainActivity>(activityRef);
        }

        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case TimingTaskHandler.CLOCK_UPDATE_MSG:
                    post(new Runnable() {
                        @Override
                        public void run() {
                           wActRef.get().transportFragment.getClockView().post(new Runnable() {
                               @Override
                               public void run() {
                                   wActRef.get().transportFragment.getClockView().setText(
                                           wActRef.get().transportFragment.getElapsedTimeUnit().incrementTime()
                                   );
                               }
                           });
                        }
                    });
                    break;
                case ClickTrackTask.CLICK_NOTIFICATION_MSG:
                    if(msg.obj != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().transportFragment.clickNotification((int) msg.obj);
                            }
                        });
                    }
                    break;
                case ClickTrackTask.START_PLAYBACK_CLOCK_MSG:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wActRef.get().playbackControlsFragment.clickTrackNotification(msg.what);
                        }
                    });
                   // wActRef.get().transportFragment.startTimer();
                    break;
                case ClickTrackTask.PRE_COUNT_NOTIFICATION_MSG:
                    post(new Runnable() {
                        @Override
                        public void run() {
                            wActRef.get().playbackControlsFragment.clickTrackNotification(msg.what);
                        }
                    });
                    break;
                case TrackPlaybackHandler.SAMPLER_PAD_TRIGGER_MSG:
                    final Note note = (Note) msg.obj;
                    if(note != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().samplerFragment.triggerSamplerPad(note.getButtonId());
                            }
                        });

                        post(new Runnable() {
                            @Override
                            public void run() {
                                if(wActRef.get().faderFragment != null)
                                    wActRef.get().faderFragment.triggerFader(wActRef.get().visualizer, note.getButtonId());
                            }
                        });
                    }

                    break;
                case VisualizerHandlerThread.MSG_FFT:
                    if(msg.obj != null){
                        post(new Runnable() {
                            @Override
                            public void run() {
                                wActRef.get().visualizerBarFragment.fftCaptureBarLayout((byte[]) msg.obj);
                            }
                        });
                    }
                    break;
                case VisualizerHandlerThread.MSG_WAVEFORM:
                    break;
                default:

                    break;
            }
        }
    }
}
