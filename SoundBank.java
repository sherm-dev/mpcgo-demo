package com.shermdev.will.mpcgo;

import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by Will on 2/19/2017.
 */

public class SoundBank {
    private static final String SOUNDBANK_NAME_LABEL_TO_STRING = "SoundBank Name: ";
    private static final String SOUNDBANK_SAMPLES_LABEL_TO_STRING = " SoundBank Samples: ";
    public static final String KEY_SOUNDBANK_BUNDLE_EXTRA = "soundbank_extra";
    public static final String SOUNDBANK_NAME = "soundbank_name_bundle";
    public static final String SOUNDBANK_SAMPLE_LIST = "soundbank_sample_list_bundle";
    private String name;
    private ArrayList<Sample> samples;

    public SoundBank(){
        this.name = null;
        this.samples = new ArrayList<>();
    }

    public SoundBank(String name){
        this.name = name;
        this.samples = new ArrayList<>();
    }

    public SoundBank(String name, ArrayList<Sample> samples){
        this.name = name;
        this.samples = samples;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Sample> getSamples() {
        return samples;
    }

    public void setSamples(ArrayList<Sample> samples) {
        this.samples = samples;
    }

    public Bundle soundBankToBundle(){
        Bundle b = new Bundle();
        b.putString(SOUNDBANK_NAME, name);
        ArrayList<Bundle> samplesBundleList = new ArrayList<>();
        for(int i = 0; i < samples.size(); i++){
            samplesBundleList.add(i, samples.get(i).sampleToBundle());
        }
        b.putParcelableArrayList(SOUNDBANK_SAMPLE_LIST, samplesBundleList);
        return b;
    }

    public static SoundBank soundBankFromBundle(Bundle bundle){
        String name = bundle.getString(SOUNDBANK_NAME);
        ArrayList<Bundle> sampleBundles = bundle.getParcelableArrayList(SOUNDBANK_SAMPLE_LIST);
        ArrayList<Sample> sampleList = new ArrayList<>();

        if(sampleBundles != null) {
            for (int i = 0; i < sampleBundles.size(); i++) {
                sampleList.add(new Sample(sampleBundles.get(i).getString(Sample.SAMPLE_BUNDLE_NAME),
                        sampleBundles.get(i).getString(Sample.SAMPLE_BUNDLE_PATH)));
            }
        }
        return new SoundBank(name, sampleList);
    }

    public static ArrayList<Sample> samplesFromSampleEntities(SampleEntity[] sampleEntities){
        ArrayList<Sample> samples = new ArrayList<>();

        for(int i = 0; i < sampleEntities.length; i++){
            samples.add(new Sample(sampleEntities[i].getName(), sampleEntities[i].getPath(), sampleEntities[i].getOrder()));
        }

        return samples;
    }

    @NonNull
    @Override
    public String toString() {
        String soundBankString = SOUNDBANK_NAME_LABEL_TO_STRING.concat(getName()).concat(SOUNDBANK_SAMPLES_LABEL_TO_STRING);

        for(Sample sample : getSamples()){
            soundBankString = soundBankString.concat(sample.toString());
        }

        return super.toString().concat(soundBankString);
    }
}
