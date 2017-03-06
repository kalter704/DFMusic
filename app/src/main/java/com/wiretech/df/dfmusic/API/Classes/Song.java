package com.wiretech.df.dfmusic.API.Classes;

public class Song {
    private int mId;
    private int mPos;
    private String mName;
    private String mSinger;
    private String mLength;
    private String mSongURL;
    private String mAlbumURL;

    public static final int INT_FLAG = 0;
    public static final int STRING_FLAG = 1;


    public Song(int id, String name, int length) {
        mId = id;
        mName = name;
        setLength(length);
    }

    public Song(int id, String name, String singer, String length, int pos, String songURL, String albumURL, int flag) {
        mId = id;
        mPos = pos;
        mName = name;
        mSinger = singer;
        if (flag == INT_FLAG) {
            setLength(Integer.valueOf(length));
        } else {
            mLength = length;
        }
        mSongURL = songURL;
        mAlbumURL = albumURL;
    }

    public void setId(int id) {
        mId = id;
    }

    public void setPos(int pos) {
        mPos = pos;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setLength(int length) {
        String tempLength = null;
        int count = 0;
        while (length >= 60) {
            length -= 60;
            ++count;
        }
        if (count < 10) {
            tempLength = "0" + String.valueOf(count);
        } else {
            tempLength = String.valueOf(count);
        }
        if (length < 10) {
            tempLength += ":0" + String.valueOf(length);
        } else {
            tempLength += ":" + String.valueOf(length);
        }
        mLength = tempLength;
    }

    public void setSongURL(String songURL) {
        mSongURL = songURL;
    }

    public void setAlbumURL(String albumURL) {
        mAlbumURL = albumURL;
    }

    public void setSinger(String singer) {
        mSinger = singer;
    }

    public int getId() {
        return mId;
    }

    public int getPos() {
        return mPos;
    }

    public String getName() {
        return mName;
    }

    public String getLength() {
        return mLength;
    }

    public String getSongURL() {
        return mSongURL;
    }

    public String getAlbumURL() {
        return mAlbumURL;
    }

    public String getSinger() {
        return mSinger;
    }
}
