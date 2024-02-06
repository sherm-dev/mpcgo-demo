package com.shermdev.will.mpcgo;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;


@Entity(
    tableName = "samples",
    foreignKeys = {
            @ForeignKey(
                    entity = SoundBankEntity.class,
                    parentColumns = {"id"},
                    childColumns = {"soundbank_id"},
                    deferred = true,
                    onDelete = ForeignKey.NO_ACTION
            )
    },
    indices = {
            @Index(
                    value = {"soundbank_id"}
            ),
            @Index(
                    value = {"id"},
                    unique = true
            )
    }
)
public class SampleEntity {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;
    @ColumnInfo(name = "sample_name")
    private final String name;
    @ColumnInfo(name = "sample_path")
    private final String path;
    @ColumnInfo(name="sample_order")
    private final int order;
    @ColumnInfo(name="soundbank_id")
    private final int soundBankId;

    public SampleEntity(String name, String path, int order, int soundBankId) {
        this.name = name;
        this.path = path;
        this.order = order;
        this.soundBankId = soundBankId;
    }

    public int getSoundBankId() { return soundBankId; }

    public int getOrder() { return order; }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }


}
