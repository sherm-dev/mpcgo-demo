package com.shermdev.will.mpcgo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSoundBankDisplayInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoundBankDisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundBankDisplayFragment extends Fragment {
    private static final String SOUNDBANK_DISPLAY_NAME = "soundbank_display_name_arg";
    private static final String INDICATOR_DOWN_IMAGE = "ic_arrow_drop_down_white_24dp.png";
    private static final String INDICATOR_UP_IMAGE = "ic_arrow_drop_up_white_24dp.png";

    private TextView nameDisplayView;
    private String soundBankName;
    private ImageView indicatorArrowView;
    private boolean listOpenState;
    private Context context;

    private OnSoundBankDisplayInteractionListener displayInteractionListener;

    public SoundBankDisplayFragment() {
        // Required empty public constructor
    }


    public static SoundBankDisplayFragment newInstance(String displayName) {
        SoundBankDisplayFragment fragment = new SoundBankDisplayFragment();
        Bundle args = new Bundle();
        args.putString(SOUNDBANK_DISPLAY_NAME, displayName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listOpenState = false;
        if (getArguments() != null) {
            soundBankName = getArguments().getString(SOUNDBANK_DISPLAY_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sound_bank_display, container, false);
        View.OnClickListener nameListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleIndicatorArrow();
                displayInteractionListener.onSoundBankNameDisplayInteraction();
            }
        };

        Button editButton = view.findViewById(R.id.button_edit_soundbank);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInteractionListener.onSoundBankEditInteraction();
            }
        });

        nameDisplayView = (TextView) view.findViewById(R.id.sound_bank_name_display);
        nameDisplayView.setText(soundBankName);
        indicatorArrowView = (ImageView) view.findViewById(R.id.sound_bank_display_indicator);
        indicatorArrowView.setOnClickListener(nameListener);
        nameDisplayView.setOnClickListener(nameListener);

        return view;
    }

    public void toggleIndicatorArrow(){
        if(listOpenState){
            listOpenState = false;
            indicatorArrowView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_down_drawable, null));
        }else{
            listOpenState = true;
            indicatorArrowView.setImageDrawable(getResources().getDrawable(R.drawable.arrow_up_drawable, null));
        }
    }

    public void changeSoundBankName(String name){
        nameDisplayView.setText(name);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnSoundBankDisplayInteractionListener) {
            displayInteractionListener = (OnSoundBankDisplayInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSoundBankDisplayInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        displayInteractionListener = null;
    }


    public interface OnSoundBankDisplayInteractionListener {
        void onSoundBankNameDisplayInteraction();
        void onSoundBankEditInteraction();
    }
}
