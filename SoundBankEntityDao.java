package com.shermdev.will.mpcgo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;

@Dao
public interface SoundBankEntityDao {
    @Query("SELECT COUNT(*) FROM soundbanks WHERE soundbank_name = :soundBankName")
    int soundBankCount(String soundBankName);
    @Query("SELECT soundbank_name FROM soundbanks")
    String[] listSoundBanks();
    @Query("SELECT id FROM soundbanks WHERE soundbank_name = :soundBankName LIMIT 1")
    int soundBankIdByName(String soundBankName);
    @Query("SELECT soundbank_name FROM soundbanks WHERE id = :soundBankId LIMIT 1")
    String soundBankNameById(int soundBankId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void newSoundBank(SoundBankEntity soundBankEntity);
    @Update
    void updateSoundBank(SoundBankEntity soundBankEntity);
    @Delete
    void deleteSoundBank(SoundBankEntity soundBankEntity);
}
