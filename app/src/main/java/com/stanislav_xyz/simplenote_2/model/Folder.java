package com.stanislav_xyz.simplenote_2.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public class Folder implements Parcelable {

    private int id;
    private String name;
    private long date;

    public Folder(String name, long date, int id) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    protected Folder(Parcel in) {
        id = in.readInt();
        name = in.readString();
        date = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeLong(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Folder> CREATOR = new Creator<Folder>() {
        @Override
        public Folder createFromParcel(Parcel in) {
            return new Folder(in);
        }

        @Override
        public Folder[] newArray(int size) {
            return new Folder[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Folder folder = (Folder) obj;
        return this.getName().equals(folder.getName());
    }
}
