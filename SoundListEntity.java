package com.shermdev.will.mpcgo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "sound_list",
        foreignKeys = {
        @ForeignKey(
                entity = SoundBankEntity.class,
                parentColumns = "id",
                childColumns = "soundbank_id",
                onDelete = ForeignKey.CASCADE,
                deferred = true
        ),
        @ForeignKey(
                entity = SampleEntity.class,
                parentColumns = "id",
                childColumns = "sample_id",
                onDelete = ForeignKey.CASCADE,
                deferred = true
        )
    }
)
public class SoundListEntity {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @ColumnInfo(name="soundbank_id")
    private final int soundbankId;
    @ColumnInfo(name="sample_id")
    private final int sampleId;
    @ColumnInfo(name="sample_order")
    private final int sampleOrder;

    public SoundListEntity(int soundbankId, int sampleId, int sampleOrder) {
        this.soundbankId = soundbankId;
        this.sampleId = sampleId;
        this.sampleOrder = sampleOrder;
    }

    public int getSoundbankId() {
        return soundbankId;
    }

    public int getSampleId() {
        return sampleId;
    }

    public int getSampleOrder() {
        return sampleOrder;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
