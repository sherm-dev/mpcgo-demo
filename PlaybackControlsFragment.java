package com.shermdev.will.mpcgo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlaybackControlsFragment.OnPlaybackControlsInteract} interface
 * to handle interaction events.
 * Use the {@link PlaybackControlsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaybackControlsFragment extends Fragment {


    private boolean recordingOn;
    private boolean playingOn;
    private boolean recordIndicatorOn;
    private boolean playbackIndicatorOn;

    private ImageButton recordButton;
    private ImageButton playButton;
    private OnPlaybackControlsInteract controlsCallback;

    public PlaybackControlsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment
     * */


    public static PlaybackControlsFragment newInstance() {
        return new PlaybackControlsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // if (getArguments() != null) {
           // mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
       // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        recordIndicatorOn = false;
        View view = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonPressed(v);
            }
        };

        playButton = (ImageButton) view.findViewById(R.id.play_button);
        playButton.setOnClickListener(buttonClickListener);

        recordButton = (ImageButton) view.findViewById(R.id.record_button);
        recordButton.setOnClickListener(buttonClickListener);

        ImageButton stopButton = (ImageButton) view.findViewById(R.id.stop_button);
        stopButton.setOnClickListener(buttonClickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaybackControlsInteract) {
            controlsCallback = (OnPlaybackControlsInteract) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTransportInteract");
        }
    }



    private void toggleRecordButtonPreCount(){
        if(recordIndicatorOn){
            recordIndicatorOn = false;
            recordButton.post(new Runnable() {
                @Override
                public void run() {
                    recordButton.setBackgroundColor(Color.DKGRAY);
                }
            });
        }else{
            recordIndicatorOn = true;
            recordButton.post(new Runnable() {
                @Override
                public void run() {
                    recordButton.setBackgroundColor(Color.RED);
                }
            });
        }
    }

    public void clickTrackNotification(int notificationType){
        if(notificationType == ClickTrackTask.START_PLAYBACK_CLOCK_MSG){
            playbackOn();
        }

        toggleRecordButtonPreCount();
    }

    private void playbackOn(){
        playingOn = true;
        playButton.post(new Runnable() {
            @Override
            public void run() {
                playButton.setBackgroundColor(Color.GREEN);
            }
        });
    }

    private void playButtonPress(View v){
        if(isPlayingOn()){
            playingOn = false;
            v.setBackgroundColor(Color.DKGRAY);
        }else{
            playingOn = true;
            v.setBackgroundColor(Color.GREEN);
        }
        controlsCallback.onPlayButtonPress(playingOn);
    }

    private void onButtonPressed(View view){
        int id = view.getId();
        switch(id){
            case R.id.play_button:
                //if playback is on, the view's background will turn green, otherwise, off
                playButtonPress(view);
                break;
            case R.id.record_button:
                //if recording is on, and the button is then pressed, recording will be toggled off
                //may want to integrate this into togggleRecordButtonPreCount above, but the "context" is a little different
                if(isRecordingOn()){
                    recordingOn = false;
                    view.setBackgroundColor(Color.DKGRAY);
                    //playButtonPress(view); //uncomment out to fully tie playback on to record on, otherwise, playback can continue without recording
                }else{
                    recordingOn = true;
                    view.setBackgroundColor(Color.RED);
                }
                 controlsCallback.onRecordButtonPress(recordingOn);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        controlsCallback = null;
    }

    public boolean isRecordingOn() {
        return recordingOn;
    }

    public void setRecordingOn(boolean recordingOn) {
        this.recordingOn = recordingOn;
    }

    public boolean isPlayingOn() {
        return playingOn;
    }

    public void setPlayingOn(boolean playingOn) {
        this.playingOn = playingOn;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPlaybackControlsInteract {
        void onRecordButtonPress(boolean recordOn);
        void onPlayButtonPress(boolean playOn);
        void onStopButtonPress();
    }
}
