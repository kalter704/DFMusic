package com.wiretech.df.dfmusicbeta.api.classes;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private int mID = -1;
    private String mName = null;
    private String mSchoolName = null;
    private String mLastUpdate = null;
    private int mPos = 0;

    private List<Song> mSongs = new ArrayList<>();
    private int mNumbersOfSongs = 0;
    private boolean isShuffle = false;

    public Playlist() {}

    public Playlist(Playlist p) {
        mID = p.getID();
        mName = p.getName();
        mSchoolName = p.getSchoolName();
        mLastUpdate = p.getLastUpdate();
        mPos = p.getPos();
        mSongs = new ArrayList<>(p.getSongs());
        mNumbersOfSongs = p.getNumbersOfSongs();
        isShuffle = p.isShuffle;
    }

    public Playlist(int id, String name) {
        this.mID = id;
        this.mName = name;
    }

    public Playlist(int id, String name, String schoolName) {
        this.mID = id;
        this.mName = name;
        this.mSchoolName = schoolName;
    }

    public Playlist(int id, String name, String schoolName, String lastUpdate, int pos) {
        mID = id;
        mName = name;
        mSchoolName = schoolName;
        mLastUpdate = lastUpdate;
        mPos = pos;
    }

    public void addSong(Song s) {
        mSongs.add(s);
    }

    public int getID() {
        return mID;
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
        return mNumbersOfSongs;
    }

    public void setID(int mID) {
        this.mID = mID;
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
        this.mSongs = new ArrayList<>(songs);
    }

    public void setNumbersOfSongs(int numbersOfSongs) {
        this.mNumbersOfSongs = numbersOfSongs;
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }
}
