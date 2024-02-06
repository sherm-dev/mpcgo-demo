package com.shermdev.will.mpcgo;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.shermdev.will.mpcgo.SoundBankMenuFragment.OnSoundbankMenuInteractionListener;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {Sound Bank Option} and makes a call to the
 * specified {@link SoundBankMenuFragment.OnSoundbankMenuInteractionListener}.
 *
 */
public class SoundBankMenuRecyclerAdapter extends RecyclerView.Adapter<SoundBankMenuRecyclerAdapter.ViewHolder> {

    private final ArrayList<String> soundbankOptions;
    private final SoundBankMenuFragment.OnSoundbankMenuInteractionListener optionListener;

    public SoundBankMenuRecyclerAdapter(ArrayList<String> items, SoundBankMenuFragment.OnSoundbankMenuInteractionListener listener) {
        soundbankOptions = items;
        optionListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sound_bank_menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemName = soundbankOptions.get(position);
        holder.optionView.setText(soundbankOptions.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != optionListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    optionListener.onSoundbankMenuInteraction(holder.itemName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return soundbankOptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView optionView;
        public String itemName;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            optionView = (TextView) view.findViewById(R.id.soundbank_option_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + optionView.getText() + "'";
        }
    }
}
