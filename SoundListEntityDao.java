package com.shermdev.will.mpcgo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface SoundListEntityDao {
    @Query("SELECT sound_list.sample_order, samples.sample_name, samples.sample_path FROM sound_list JOIN soundbanks ON soundbanks.id = sound_list.soundbank_id JOIN samples ON sound_list.sample_id = samples.id WHERE soundbanks.soundbank_name = :soundBankName ORDER BY sound_list.sample_order")
    ArrayList<Sample> retrieveSoundBankSamplesByName(String soundBankName);
    @Query("SELECT sample_order, samples.sample_name, samples.sample_path FROM sound_list JOIN soundbanks ON soundbanks.id = sound_list.soundbank_id JOIN samples ON sound_list.sample_id = samples.id WHERE sound_list.soundbank_id = :id ORDER BY sound_list.sample_order")
    ArrayList<Sample>  retrieveSoundBankSamplesById(int id);
    @Insert
    void insertSoundListRow(SoundListEntity soundListEntity);
    @Update
    void updateSoundListRow(SoundListEntity soundListEntity);
    @Delete
    void deleteSoundListRow(SoundListEntity soundListEntity);
}
