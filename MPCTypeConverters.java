package com.shermdev.will.mpcgo;

import androidx.room.TypeConverter;

import java.util.ArrayList;


public class MPCTypeConverters {
    @TypeConverter
    public static SampleEntity soundListQueryToSampleEntity(String sampleName, String samplePath, int sampleOrder, int soundBankId){
        return new SampleEntity(sampleName, samplePath, sampleOrder, soundBankId);
    }

    @TypeConverter
    public static ArrayList<Sample> sampleEntitiesToSamples(ArrayList<SampleEntity> sampleEntities){
        ArrayList<Sample> samples = new ArrayList<>(sampleEntities.size());

        for(SampleEntity sampleEntity : sampleEntities){
            samples.add(sampleEntity.getOrder(), new Sample(sampleEntity.getName(), sampleEntity.getPath(), sampleEntity.getOrder()));
        }

        return samples;
    }
}
