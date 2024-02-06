package com.shermdev.will.mpcgo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnVoiceRecordInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoiceRecorderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoiceRecorderFragment extends Fragment{

    private OnVoiceRecordInteractionListener mListener;

    public VoiceRecorderFragment() {
        // Required empty public constructor
    }


    public static VoiceRecorderFragment newInstance() {
        return new VoiceRecorderFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_recorder, container, false);
        ImageButton recordButton = (ImageButton) view.findViewById(R.id.voice_record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onVoiceRecord();
            }
        });
        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVoiceRecordInteractionListener) {
            mListener = (OnVoiceRecordInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVoiceRecordInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnVoiceRecordInteractionListener {
        // TODO: Update argument type and name
        void onVoiceRecord();
    }
}
