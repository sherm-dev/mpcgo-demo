package com.shermdev.will.mpcgo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSoundbankMenuInteractionListener}
 * interface.
 */
public class SoundBankMenuFragment extends Fragment {

    private static final String SOUNDBANK_NAMES = "soundbanks";

    private ArrayList<String> soundbanks;
    private OnSoundbankMenuInteractionListener soundbankInteractListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SoundBankMenuFragment() {
    }

    public static SoundBankMenuFragment newInstance(ArrayList<String> soundbankNames) {
        SoundBankMenuFragment fragment = new SoundBankMenuFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(SOUNDBANK_NAMES, soundbankNames);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            soundbanks = getArguments().getStringArrayList(SOUNDBANK_NAMES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soundbank_menu, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new SoundBankMenuRecyclerAdapter(soundbanks, soundbankInteractListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSoundbankMenuInteractionListener) {
            soundbankInteractListener = (OnSoundbankMenuInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        soundbankInteractListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSoundbankMenuInteractionListener {
        // TODO: Update argument type and name
        void onSoundbankMenuInteraction(String soundbankName);
    }
}
