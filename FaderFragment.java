package com.shermdev.will.mpcgo;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaderFragment extends Fragment {
    private static final String TRACK_COUNT_PARAM = "track_count";
    private static final String TRACK_VOLUMES_PARAM = "track_volumes";

    private OnFaderInteraction interactionListener;
    private Context context;
    private int trackCount;
    private ArrayList<Integer> trackVolumes;
    private LinearLayout faderContainer;

    public FaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment FaderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaderFragment newInstance(int trackCount, ArrayList<Integer> trackVolumes) {
        FaderFragment fragment = new FaderFragment();
        Bundle args = new Bundle();
        args.putInt(TRACK_COUNT_PARAM, trackCount);
        args.putIntegerArrayList(TRACK_VOLUMES_PARAM, trackVolumes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            trackCount = getArguments().getInt(TRACK_COUNT_PARAM);
            trackVolumes = getArguments().getIntegerArrayList(TRACK_VOLUMES_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fader, container, false);
        faderContainer = (LinearLayout) view.findViewById(R.id.fader_container);

        for(int i = 0; i < trackCount; i++){
            faderContainer.addView(new TrackFader(context, trackVolumes.get(i), i, interactionListener));
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FaderFragment.OnFaderInteraction) {
            interactionListener = (FaderFragment.OnFaderInteraction) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSoundBankDisplayInteractionListener");
        }
    }

    public void triggerFader(Visualizer visualizer, int trackIndex){
        TrackFader fader = (TrackFader) faderContainer.getChildAt(trackIndex);
        fader.triggerDisplay(visualizer.getMeasurementPeakRms(new Visualizer.MeasurementPeakRms()));
    }

    public interface OnFaderInteraction{
        void onFaderDrag(int index, int volume);
    }
}
