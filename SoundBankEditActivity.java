package com.shermdev.will.mpcgo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SoundBankEditActivity extends AppCompatActivity implements SoundBankEditFragment.OnSoundBankEditListener{
    private static final String EDIT_UI_THREAD_NAME = "ui_edit_thread";
    public static final String KEY_EDITOR_SOUNDBANK_NAME = "sb_name";
    public static final String KEY_EDITOR_SOUNDBANK_MODE = "soundbank_editor_mode";
    public static final String MODE_EDITOR_CREATE_SOUNDBANK = "soundbank_mode_create";
    public static final String MODE_EDITOR_EDIT_SOUNDBANK = "soundbank_mode_edit";
    public static final int RESULT_CODE_SOUNDBANK_EDIT = 6767888;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 667886;

    private HandlerThread handlerThread;
    private SoundBankEditFragment soundBankEditFragment;
    private SoundBankManager soundBankManager;
    private Handler handler;
    private EditText nameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_bank_edit);

        nameEditText = findViewById(R.id.soundbank_edit_name);

        handlerThread = new HandlerThread(EDIT_UI_THREAD_NAME);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new EditSoundBankManagerHandlerCallback(this));
        soundBankManager = new SoundBankManager(
                getApplicationContext(),
                handler
        );

        if(getIntent() != null && getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_MODE) != null){
                if(getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_MODE).equals(MODE_EDITOR_CREATE_SOUNDBANK)){
                    soundBankEditFragment = SoundBankEditFragment.newInstance(new SoundBank(SoundBankManager.NEW_SOUNDBANK_NAME, new ArrayList<Sample>()));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_soundbank_editor, soundBankEditFragment).commit();
                }

                if(getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_MODE).equals(MODE_EDITOR_EDIT_SOUNDBANK)){
                    if(getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_NAME) != null){
                        if (ContextCompat.checkSelfPermission(
                                getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_GRANTED) {
                            soundBankManager.getDatabaseHelper().soundBankExists(getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_NAME));
                        }else {
                            requestPermissions(
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    READ_EXTERNAL_STORAGE_REQUEST_CODE
                            );
                        }
                    }

                }
        }
    }

    private void layoutEditor(SoundBank sb){
        soundBankEditFragment = SoundBankEditFragment.newInstance(sb);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_soundbank_editor, soundBankEditFragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
                && requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            soundBankManager.getDatabaseHelper().soundBankExists(getIntent().getStringExtra(KEY_EDITOR_SOUNDBANK_NAME));
        }  else {
            // Explain to the user that the feature is unavailable because
            // the features requires a permission that the user has denied.
            // At the same time, respect the user's decision. Don't link to
            // system settings in an effort to convince the user to change
            // their decision.
            //TODO: Explanations
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }

    @Override
    public void onSoundBankEditSave(SoundBank soundbank) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(SoundBank.KEY_SOUNDBANK_BUNDLE_EXTRA, soundbank.soundBankToBundle());
        setResult(RESULT_CODE_SOUNDBANK_EDIT, resultIntent);
        finish();
    }

    @Override
    public void onSoundBankCancel() {
        setResult(RESULT_CODE_SOUNDBANK_EDIT, null);
        finish();
    }

    @Override
    public void onSamplePreview(Sample sample, int index) {

    }

    @Override
    public void onSampleNameEdit(Sample sample, int index) {

    }

    @Override
    public void onBrowseMusic(Sample sample, int sampleIndex) {
        SoundBank sb = soundBankManager.getSoundBank();
        sb.getSamples().remove(sampleIndex);
        sb.getSamples().add(sampleIndex, sample);
        soundBankManager.loadSoundBank(sb);
    }

    private static class EditSoundBankManagerHandlerCallback implements Handler.Callback{
        private final WeakReference<SoundBankEditActivity> wActRef;

        public EditSoundBankManagerHandlerCallback(Activity sbEditActivity){
            this.wActRef = new WeakReference<>((SoundBankEditActivity) sbEditActivity);
        }

        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what){
                case SoundBankManager.MSG_SAMPLE_UPDATED:
                    break;
                case SoundBankManager.MSG_SOUNDBANK_RETRIEVED:
                    if((SoundBank) msg.obj != null){
                        final SoundBank sb = (SoundBank) msg.obj;
                        wActRef.get().handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        wActRef.get().soundBankManager.loadSoundBank(sb);
                                        wActRef.get()
                                                .getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(
                                                        R.id.frame_soundbank_editor,
                                                        SoundBankEditFragment.newInstance(sb)
                                                )
                                                .commit();
                                    }
                                }
                        );
                    }
                    break;
                case SoundBankManager.MSG_SOUNDBANK_LOADED:
                    final SoundBank sb = (SoundBank) msg.obj;
                    wActRef.get().handler.post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if(sb != null)
                                        wActRef.get().layoutEditor(sb);
                                }
                            }
                    );
                    break;
                case SoundBankManager.MSG_SOUNDBANK_EXISTS:
                    final String name = (String) msg.obj;

                    if(msg.obj != null) {
                        wActRef.get().soundBankManager.getDatabaseHelper().retrieveSoundBank((String) msg.obj);
                        wActRef.get().handler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        wActRef.get().nameEditText.setText(name);
                                    }
                                }
                        );
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }
}