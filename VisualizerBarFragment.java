package com.shermdev.will.mpcgo;


import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualizerBarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualizerBarFragment extends Fragment implements Visualizer.OnDataCaptureListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_AUDIO_SESSION_ID = "audio_session_id";
    private static final String ARG_SAMPLING_RATE = "sampling_rate";

    private int audioSessionID;
    private int samplingRate;

    private int framesCount;
    private LinearLayout barTop, barBottom;
    private View visualizerBar;

    public VisualizerBarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param audioSessionID audio session of audio output.
     * @return A new instance of fragment VisualizerBarFragment.
     */

    public static VisualizerBarFragment newInstance(int audioSessionID, int samplingRate) {
        VisualizerBarFragment fragment = new VisualizerBarFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_AUDIO_SESSION_ID, audioSessionID);
        args.putInt(ARG_SAMPLING_RATE, samplingRate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            audioSessionID = getArguments().getInt(ARG_AUDIO_SESSION_ID);
            samplingRate = getArguments().getInt(ARG_SAMPLING_RATE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        visualizerBar = inflater.inflate(R.layout.fragment_visualizer_bar, container, false);
        barTop = visualizerBar.findViewById(R.id.visualizer_row_top);
        barBottom = visualizerBar.findViewById(R.id.visualizer_row_bottom);

        //layout frame layouts in bar corresponding to sampling rate
        layoutVisualizerFrames(barTop);
        layoutVisualizerFrames(barBottom);
        framesCount = barBottom.getChildCount();

        return visualizerBar;
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {

    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int i) {
        fftCaptureBarLayout(bytes);
    }

    public void initializeVisualizer(){
        Visualizer visualizer = new Visualizer(audioSessionID);
        visualizer.setDataCaptureListener(this, samplingRate, false, true);
    }

    public void fftCaptureBarLayout(final byte[] fftBytes){
        visualizerBar.post(new Runnable() {
            @Override
            public void run() {
                fftCaptureFramesLayout(barBottom, fftBytes);
                fftCaptureFramesLayout(barTop, fftBytes);
            }
        });
    }

    private void fftCaptureFramesLayout(LinearLayout bar, byte[] fftBytes){
        for(int i = 0; i < fftBytes.length; i++){
            if(i < framesCount){
                VisualizerBarFrame barFrame = (VisualizerBarFrame) bar.getChildAt(i);
                barFrame.visualizerFrameBackground(fftBytes[i], 0); //color 0 goes to default
            }
        }
    }

    private void layoutVisualizerFrames(LinearLayout visualizerBar){
        int count = Math.round((float) samplingRate / 100);

        for(int i = 0; i < count; i++){
            VisualizerBarFrame barFrame = new VisualizerBarFrame(getContext());
            visualizerBar.addView(barFrame);
        }
    }
}
