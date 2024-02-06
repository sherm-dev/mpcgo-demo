package com.shermdev.will.mpcgo;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MetronomeFragment.MetronomeControlsListener} interface
 * to handle interaction events.
 * Use the {@link MetronomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MetronomeFragment extends Fragment {
    private static final String BEATS_PER_MEAURE = "time_sig_upper";
    private static final String GROUPING_NOTE = "time_sig_lower";
    private static final String TEMPO = "tempo";

    private static final int MAX_TEMPO = 200;
    private static final int MIN_TEMPO = 50;


    private static final int TIME_SIG_UPPER_MAX = 16;
    private static final int TIME_SIG_UPPER_MIN = 1;
    private static final int TIME_SIG_LOWER_MAX = 16;
    private static final int TIME_SIG_LOWER_MIN = 2;


    private boolean metronomeOn;
    private MetronomeControlsListener metronomeListener;
    private int tempo, beatsPerMeasure, groupingNote;
    private Context context;
    private ImageButton metronomeToggleButton;

    public MetronomeFragment() {
        // Required empty public constructor
    }



    public static MetronomeFragment newInstance(int tempo, TimeSignature timeSignature) {
        MetronomeFragment fragment = new MetronomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TEMPO, tempo);
        bundle.putInt(BEATS_PER_MEAURE, timeSignature.getUpper());
        bundle.putInt(GROUPING_NOTE, timeSignature.getLower());
        fragment.setArguments(bundle);
        return new MetronomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        metronomeOn = false;

        if (getArguments() != null) {
            this.tempo = getArguments().getInt(TEMPO);
            this.beatsPerMeasure = getArguments().getInt(BEATS_PER_MEAURE);
            this.groupingNote = getArguments().getInt(GROUPING_NOTE);
        }else{
            this.tempo = PlaybackSettings.DEFAULT_TEMPO;
            this.beatsPerMeasure = PlaybackSettings.DEFAULT_TIME_SIGNATURE.getUpper();
            this.groupingNote = PlaybackSettings.DEFAULT_TIME_SIGNATURE.getLower();
        }

        Log.i("TEMPO", String.valueOf(this.tempo));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_metronome, container, false);


        TextView tempoDisplay = (TextView) view.findViewById(R.id.tempo_display);
        tempoDisplay.setText(String.valueOf(tempo));
        tempoDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch alert dialog with number picker in it
                openTempoPickerDialog(v);
            }
        });

        LinearLayout timeSignatureDisplayContainer = (LinearLayout) view.findViewById(R.id.time_signature_display_clickable);
        timeSignatureDisplayContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch alert dialog with a number picker for both the time sig numerator and time sig divisor
                openTimeSignaturePickerDialog(v);
            }
        });

        TextView timeSigUpDisplay = (TextView) view.findViewById(R.id.time_sig_upper_display);
        timeSigUpDisplay.setText(String.valueOf(beatsPerMeasure));

        TextView timeSigLowDisplay = (TextView) view.findViewById(R.id.time_sig_lower_display);
        timeSigLowDisplay.setText(String.valueOf(groupingNote));

        metronomeToggleButton = (ImageButton) view.findViewById(R.id.metronome_button);
        metronomeToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                metronomeToggleClick();
            }
        });


        return view;
    }


    private void openTempoPickerDialog(View view){
         final int originalTempo = tempo;

          final NumberPicker tempoPicker = new NumberPicker(getContext());
        tempoPicker.setMaxValue(MAX_TEMPO);
        tempoPicker.setMinValue(MIN_TEMPO);
        tempoPicker.setValue(tempo);

        FrameLayout fl = (FrameLayout) view.findViewById(android.R.id.custom);
        fl.addView(tempoPicker, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext());
        adBuilder.setView(R.layout.tempo_dialog_content).setTitle("Set Tempo").setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(originalTempo != tempoPicker.getValue()) tempo = tempoPicker.getValue();
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(originalTempo != tempoPicker.getValue()) tempo = originalTempo;
                dialog.dismiss();
            }
        });

        AlertDialog tempoDialog = adBuilder.create();
        tempoDialog.show();
    }

    private void openTimeSignaturePickerDialog(View view){
        final int oldTimeSigNumerator = beatsPerMeasure;
        final int oldTimeSigDivisor = groupingNote;

        final NumberPicker beatsPerMeasPicker = new NumberPicker(getContext());
        beatsPerMeasPicker.setMaxValue(TIME_SIG_UPPER_MAX);
        beatsPerMeasPicker.setMinValue(TIME_SIG_UPPER_MIN);
        beatsPerMeasPicker.setValue(beatsPerMeasure);

        final NumberPicker lowerPicker = new NumberPicker(getContext());
        lowerPicker.setMaxValue(TIME_SIG_LOWER_MAX);
        lowerPicker.setMinValue(TIME_SIG_LOWER_MIN);
        lowerPicker.setValue(groupingNote);

        final TextView upperLabel = new TextView(getContext());
        upperLabel.setText("Beats per Measure");

        final TextView lowerLabel = new TextView(getContext());
        lowerLabel.setText("Beat Grouping");

        FrameLayout fl = (FrameLayout) view.findViewById(android.R.id.custom);
        fl.addView(upperLabel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fl.addView(beatsPerMeasPicker, new NumberPicker.LayoutParams(NumberPicker.LayoutParams.MATCH_PARENT, NumberPicker.LayoutParams.WRAP_CONTENT));
        fl.addView(lowerLabel, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        fl.addView(lowerPicker, new NumberPicker.LayoutParams(NumberPicker.LayoutParams.MATCH_PARENT, NumberPicker.LayoutParams.WRAP_CONTENT));

        AlertDialog.Builder adBuilder = new AlertDialog.Builder(getContext());
        adBuilder.setTitle("Set Time Signature").setView(R.layout.time_signature_dialog_content).setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(oldTimeSigNumerator != beatsPerMeasPicker.getValue()) beatsPerMeasure = beatsPerMeasPicker.getValue();
                if(oldTimeSigDivisor != lowerPicker.getValue()) groupingNote = lowerPicker.getValue();
                dialog.dismiss();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(oldTimeSigNumerator != beatsPerMeasPicker.getValue()) beatsPerMeasure = oldTimeSigNumerator;
                if(oldTimeSigDivisor != lowerPicker.getValue()) groupingNote = oldTimeSigDivisor;
                dialog.dismiss();
            }
        });

        AlertDialog tempoDialog = adBuilder.create();
        tempoDialog.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof MetronomeControlsListener) {
            metronomeListener = (MetronomeControlsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVoiceRecordInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        metronomeListener = null;
    }

    private void metronomeToggleClick(){
        if(metronomeOn){
            metronomeOn = false;
            metronomeToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.metronome_click_sound_off, null));
        }else{
            metronomeOn = true;
            metronomeToggleButton.setImageDrawable(getResources().getDrawable(R.drawable.metronome_click_sound_on, null));
        }

        if (metronomeListener != null) {
            metronomeListener.onMetronomeButtonPressed(metronomeOn);
        }else{
            throw new RuntimeException(" must implement MetronomeListener");
        }
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
    public interface MetronomeControlsListener {
        void onMetronomeButtonPressed(boolean isMetronomeOn);
    }
}
