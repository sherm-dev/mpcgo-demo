package com.shermdev.will.mpcgo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SamplerPadFragment.OnPadPressed} interface
 * to handle interaction events.
 * Use the {@link SamplerPadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SamplerPadFragment extends Fragment {
    public static final int SAMPLER_PAD_PRESS_MESSAGE = 12111;
    public static final int SAMPLER_PAD_DEPRESS_MESSAGE = 12112;
    private static final String SOUNDBANK_BUNDLE_STRING = "soundbank_bundle";
    private static final String SAMPLE_BUNDLE_KEY = "sample_";
    private static final String SAMPLE_BUNDLE_COUNT = "sample_bundle_count";

    private OnPadPressed padPressedListener;
    private ArrayList<Sample> samples;
    private ArrayList<SamplerPad> samplerPads;
    private View view;


    public SamplerPadFragment(){
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment SamplerPadFragment.
     */

    public static SamplerPadFragment newInstance(ArrayList<Sample> samples){
        SamplerPadFragment fragment = new SamplerPadFragment();
        Bundle args = new Bundle();
        for(int i = 0; i < samples.size(); i++){
            Bundle b = samples.get(i).sampleToBundle();
            args.putInt(SAMPLE_BUNDLE_COUNT, samples.size());
            args.putBundle(SAMPLE_BUNDLE_KEY + String.valueOf(i), b);
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       samples = new ArrayList<>();
        samplerPads = new ArrayList<>();

        if(getArguments() != null){
            int sampleCount = getArguments().getInt(SAMPLE_BUNDLE_COUNT);
                for(int i = 0; i < sampleCount; i++){
                    Sample sample = Sample.sampleFromBundle(getArguments().getBundle(SAMPLE_BUNDLE_KEY + String.valueOf(i)));
                    Log.i("SMPLFROMBNDLE", "NAME: " + sample.getSampleName());
                    samples.add(sample);
                }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sampler_pad, container, false);
        final LinearLayout buttonContainer = (LinearLayout) view.findViewById(R.id.sampler_pad_container);
        LinearLayout buttonRow = null;

        for(int i = samples.size() - 1; i >= 0; i--){
            if(buttonRow == null){
                buttonRow = new LinearLayout(getContext());
                buttonRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
            }

            buttonRow.addView(new SamplerPad(getContext(), samples.get(i), i, padPressedListener));

            if(buttonRow.getChildCount() == 4){
                //TODO: make 4 equal to buttons per row max setting
                buttonContainer.addView(buttonRow);
                buttonRow = null;
            }
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPadPressed) {
            padPressedListener = (OnPadPressed) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPadPressed");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        padPressedListener = null;
    }

    private void orderButtons(View view){

    }

    //TODO: See if I have to post both the message and the view ui trigger
    public void triggerSamplerPad(int buttonId){
        final SamplerPad pad = (SamplerPad) view.findViewById(buttonId);

        pad.post(new Runnable() {
            @Override
            public void run() {
                pad.triggerPad();
            }
        });

        pad.postDelayed(
            new Runnable() {
                @Override
                public void run() {
                    pad.triggerPad();
                }
            },
            (long) pad.getSample().getSampleLength()
        );
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
    public interface OnPadPressed {
        // TODO: Update argument type and name
        void onSamplerPadPressed(SamplerPad samplerPad, float pressure);
    }
}
