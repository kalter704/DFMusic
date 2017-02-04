package com.wiretech.df.dfmusic.API.Classes;

import java.util.ArrayList;
import java.util.List;

public class PlayList {
    private int mId = -1;
    private String mName = null;
    private String mSchoolName = null;
    private String mLastUpdate = null;
    private int mPos = 0;
    private List<Song> mSongs = new ArrayList<>();
    private int numbersOfSongs = 0;

    public PlayList(int id, String name, String schoolName) {
        this.mId = id;
        this.mName = name;
        this.mSchoolName = schoolName;
    }

    public PlayList(int id, String name, String schoolName, String lastUpdate, int pos) {
        mId = id;
        mName = name;
        mSchoolName = schoolName;
        mLastUpdate = lastUpdate;
        this.mPos = pos;
    }

    public void addSong(Song s) {
        mSongs.add(s);
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

    public int getPos() {
        return mPos;
    }

    public List<Song> getSongs() {
        return mSongs;
    }

    public int getNumbersOfSongs() {
        return numbersOfSongs;
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

    public void setPos(int pos) {
        this.mPos = pos;
    }

    public void setSongs(List<Song> songs) {
        this.mSongs = songs;
    }

    public void setNumbersOfSongs(int numbersOfSongs) {
        this.numbersOfSongs = numbersOfSongs;
    }
}
