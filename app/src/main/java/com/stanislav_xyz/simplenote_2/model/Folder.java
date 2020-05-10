package com.stanislav_xyz.simplenote_2.model;

public class Folder {

    public int id;
    public String name;
    public long date;

    public Folder(String name, long date, int id) {
        this.id = id;
        this.name = name;
        this.date = date;
    }
}
