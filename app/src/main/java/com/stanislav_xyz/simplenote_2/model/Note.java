package com.stanislav_xyz.simplenote_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int udi;

    @ColumnInfo
    private String body;

    @ColumnInfo
    private String title;

    @ColumnInfo
    private long date;

    @ColumnInfo
    private String folderName;

    public Note(String body, String title, long date, String folderName) {
        this.body = body;
        this.title = title;
        this.date = date;
        this.folderName = folderName;
    }

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }

    public long getDate() {
        return date;
    }

    public String getFolderName() {
        return folderName;
    }

    public int getUdi() {
        return udi;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public void setUdi(int udi) {
        this.udi = udi;
    }


    protected Note(Parcel in) {
        udi = in.readInt();
        title = in.readString();
        body = in.readString();
        date = in.readLong();
        folderName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(udi);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeLong(date);
        dest.writeString(folderName);
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Note note = (Note) obj;
        return this.getUdi() == note.getUdi();
    }
}
