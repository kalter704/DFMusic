package com.wiretech.df.dfmusic.API.Classes;

public class PlayList {
    private int mId;
    private String mName;
    private String mSchoolName;
    private long mLastUpdate;
    private int pos;

    public PlayList(int id, String name, String schoolName) {
        this.mId = id;
        this.mName = name;
        this.mSchoolName = schoolName;
    }

    public PlayList(int id, String name, String schoolName, long lastUpdate, int pos) {
        mId = id;
        mName = name;
        mSchoolName = schoolName;
        mLastUpdate = lastUpdate;
        this.pos = pos;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String getSchoolName() {
        return mSchoolName;
    }

    public long getLastUpdate() {
        return mLastUpdate;
    }

    public int getPos() {
        return pos;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setSchoolName(String mSchool) {
        this.mSchoolName = mSchool;
    }

    public void setLastUpdate(long mLastUpdate) {
        this.mLastUpdate = mLastUpdate;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
