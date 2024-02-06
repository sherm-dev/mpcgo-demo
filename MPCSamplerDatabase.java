package com.shermdev.will.mpcgo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {SoundBankEntity.class, SampleEntity.class}, version = 1, exportSchema = false)
public abstract class MPCSamplerDatabase extends RoomDatabase {
    public abstract SoundBankEntityDao soundBankEntityDao();
    public abstract SampleEntityDao sampleEntityDao();
}
