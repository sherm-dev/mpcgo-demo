package com.shermdev.will.mpcgo;


import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class SoundBankEditorRecyclerViewAdapter extends RecyclerView.Adapter<SoundBankEditorRecyclerViewAdapter.ViewHolder> {
    private final ArrayList<Sample> samples;
    private final SoundBankEditFragment.OnSoundBankEditListener editListener;

    public SoundBankEditorRecyclerViewAdapter(ArrayList<Sample> samples, SoundBankEditFragment.OnSoundBankEditListener editListener) {
        this.samples = samples;
        this.editListener = editListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.soundbank_editor_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        initHolder(viewHolder, i);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        initHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return samples.size();
    }

    private void initHolder(final ViewHolder holder, int position){
        final Sample sample = (Sample) samples.get(position);
        holder.sampleItem = sample;
        holder.soundBankNameEditorView.setText(sample.getSampleName());
        holder.soundBankPathTextView.setText(sample.getSampleFilePath());

        holder.soundBankNameEditorView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sample.setSampleName(v.getEditableText().toString());
                editListener.onSampleNameEdit(holder.sampleItem, holder.getAdapterPosition());
                return true;
            }
        });

        holder.browseMusicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onBrowseMusic(holder.sampleItem, holder.getAdapterPosition());
            }
        });

        holder.previewSampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListener.onSamplePreview(holder.sampleItem, holder.getAdapterPosition());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText soundBankNameEditorView;
        public final TextView soundBankPathTextView;
        public final ImageButton browseMusicButton;
        public final ImageButton previewSampleButton;
        public Sample sampleItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            soundBankNameEditorView = (EditText) view.findViewById(R.id.sample_name_editor);
            soundBankPathTextView = (TextView) view.findViewById(R.id.sample_path_view);
            browseMusicButton = (ImageButton) view.findViewById(R.id.browse_music_button);
            previewSampleButton = (ImageButton) view.findViewById(R.id.preview_sample_button);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + soundBankNameEditorView.getText() + "'";
        }
        public Sample getSampleItem() {
            return sampleItem;
        }
    }
}
