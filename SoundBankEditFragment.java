package com.shermdev.will.mpcgo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnSoundBankEditListener}
 * interface.
 */
public class SoundBankEditFragment extends Fragment {
    public static final String SOUNDBANK_NAME = "soundbank_name";
    public static final String SOUNDBANK_BUNDLE = "soundbank_bundle";
    public static final int BROWSE_REQUEST_CODE = 6111;

    private int columns = 4;
    private OnSoundBankEditListener soundBankEditListener;
    private Context fragmentContext;
    private SoundBankManager soundBankManager;
    private SoundBank soundBank;
    private RecyclerView.AdapterDataObserver dataObserver;

    private int sampleEditPosition;
    private SoundBankEditorRecyclerViewAdapter soundBankRecyclerAdapter;

    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SoundBankEditFragment() {
    }

    public static SoundBankEditFragment newInstance(SoundBank sb) {
        SoundBankEditFragment sbef = new  SoundBankEditFragment();
        Bundle b = new Bundle();
        b.putBundle(SOUNDBANK_BUNDLE, sb.soundBankToBundle());
        sbef.setArguments(b);
        return sbef;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null && getArguments().getBundle(SOUNDBANK_BUNDLE) != null) {
            this.soundBank = SoundBank.soundBankFromBundle(requireArguments().getBundle(SOUNDBANK_BUNDLE));
        }else{
            this.soundBank = new SoundBank(null, new ArrayList<Sample>());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soundbank_edit, container, false);

        Button cancelSoundBankEdit = (Button) view.findViewById(R.id.cancel_edit_soundbank_button);
        cancelSoundBankEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundBankEditListener.onSoundBankCancel();
            }
        });

        Button saveEditSoundBankButton = (Button) view.findViewById(R.id.save_soundbank_edit_button);
        saveEditSoundBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Validate that fields are filled
                soundBankEditListener.onSoundBankEditSave(soundBank);
            }
        });

        // Set the adapter
        if (view instanceof RecyclerView) {
            soundBankRecyclerAdapter = new SoundBankEditorRecyclerViewAdapter(
                    soundBankManager.getSoundBank().getSamples(),
                    soundBankEditListener
            );

            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(fragmentContext, columns));
            recyclerView.setAdapter(soundBankRecyclerAdapter);

            ImageButton.OnClickListener browseMusicListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sampleEditPosition = recyclerView.getChildViewHolder(v).getAdapterPosition();

                    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                    // browser.
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    // Filter to only show results that can be "opened", such as a
                    // file (as opposed to a list of contacts or timezones)
                    intent.addCategory(Intent.CATEGORY_OPENABLE);


                    // Filter to show only images, using the image MIME data type.
                    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
                    // To search for all documents available via installed storage providers,
                    // it would be "*/*".
                    intent.setType("audio/*");

                    startActivityForResult(intent, BROWSE_REQUEST_CODE);
                }
            };
        }

        return view;
    }

    public void browseMusicCallback(int samplePosition){

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BROWSE_REQUEST_CODE && resultCode == RESULT_OK){
            String path = null;
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.d("Get Ext Sound", "Uri: " + uri.toString());
                path = uri.getPath();



                // Log.i("URI Path to Name", uri.getLastPathSegment());
                Log.d("Path Name", path);

            }

            assert path != null;
            int folderLocation = path.lastIndexOf(":");
            File soundFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath().concat(File.pathSeparator)
                    .concat(path.substring((folderLocation + 1), path.length())));
            String soundFilePath = soundFile.getAbsolutePath();
            Sample newSample = new Sample(soundFilePath.substring(soundFilePath.lastIndexOf("/"), soundFilePath.lastIndexOf(".")),
                    soundFilePath);
            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(sampleEditPosition, newSample);
            soundBankEditListener.onBrowseMusic(newSample, sampleEditPosition);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        fragmentContext = context;
        if (context instanceof OnSoundBankEditListener) {
            soundBankEditListener = (OnSoundBankEditListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        soundBankEditListener = null;
      //  recyclerView.getAdapter().unregisterAdapterDataObserver(soundBankRecyclerAdapter.getDataObserver());
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
    public interface OnSoundBankEditListener {
        void onSoundBankEditSave(SoundBank soundbank);
        void onSoundBankCancel();
        void onSamplePreview(Sample sample, int index);
        void onSampleNameEdit(Sample sample, int index);
        void onBrowseMusic(Sample sample, int index);
    }
}
