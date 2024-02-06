package com.shermdev.will.mpcgo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FaderNavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FaderNavigationFragment extends Fragment {
    private OnFaderNavigationInteract interactListener;

    public FaderNavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FaderNavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FaderNavigationFragment newInstance() {
        return new FaderNavigationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fader_navigation, container, false);
        ImageButton faderNavButton = (ImageButton) view.findViewById(R.id.fader_navigation_button);
        faderNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interactListener.faderNavigation();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FaderNavigationFragment.OnFaderNavigationInteract) {
            interactListener = (OnFaderNavigationInteract) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTransportInteract");
        }
    }

    public interface OnFaderNavigationInteract{
        void faderNavigation();
    }
}
