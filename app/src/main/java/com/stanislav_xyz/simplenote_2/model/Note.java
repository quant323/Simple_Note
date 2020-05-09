package com.stanislav_xyz.simplenote_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int udi;

    @ColumnInfo
    public String title;

    @ColumnInfo
    public String body;

    @ColumnInfo
    public long date;

    @ColumnInfo
    public String folder;

    public Note(String title, String body, long date, String folder) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.folder = folder;
    }


    protected Note(Parcel in) {
        udi = in.readInt();
        title = in.readString();
        body = in.readString();
        date = in.readLong();
        folder = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(udi);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeLong(date);
        dest.writeString(folder);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
