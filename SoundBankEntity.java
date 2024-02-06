package com.shermdev.will.mpcgo;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(
        tableName = "soundbanks",
        indices = {
                @Index(
                        value = {"soundbank_name"},
                        unique = true
                )
        }
)
public class SoundBankEntity {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @ColumnInfo(name = "soundbank_name")
    private final String name;

    public SoundBankEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
