package com.stanislav_xyz.simplenote_2.model;

public class Folder {

    private int id;
    private String name;
    private long date;

    public Folder(String name, long date, int id) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

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
}
