package com.wiretech.df.dfmusic.Classes;

public class PlayList {
    private int mId;
    private String mName;
    private String mSchoolName;
    private String mLastUpdate;

    public PlayList(int id, String name, String schoolName) {
        this.mId = id;
        this.mName = name;
        this.mSchoolName = schoolName;
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

    public String getLastUpdate() {
        return mLastUpdate;
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

    public void setLastUpdate(String mLastUpdate) {
        this.mLastUpdate = mLastUpdate;
    }
}
