package com.shermdev.will.mpcgo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RewriteQueriesToDropUnusedColumns;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface SampleEntityDao {
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM samples JOIN soundbanks ON soundbank_id = soundbanks.id WHERE soundbanks.soundbank_name = :soundBankName ORDER BY sample_order")
    SampleEntity[] retrieveSoundBankSamplesByName(String soundBankName);
    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM samples JOIN soundbanks ON soundbank_id = soundbanks.id WHERE soundbank_id = :id ORDER BY sample_order")
    SampleEntity[]  retrieveSoundBankSamplesById(int id);
    @Query("SELECT id FROM samples WHERE sample_name = :sampleName LIMIT 1")
    int retrieveSampleIdByName(String sampleName);
    @Insert
    void insertSampleEntity(SampleEntity sampleEntity);
    @Update
    void updateSampleEntity(SampleEntity sampleEntity);
    @Delete
    void deleteSampleEntity(SampleEntity sampleEntity);
}
