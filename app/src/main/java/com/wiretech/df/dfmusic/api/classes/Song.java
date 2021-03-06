package com.wiretech.df.dfmusic.api.classes;

import com.wiretech.df.dfmusic.api.MusicServiceAPI;

public class Song {
    private int mID;
    private int mRealID;
    private int mPos;
    private String mName;
    private String mSinger;
    private String mLength;
    private String mSongURL;
    private String mAlbumURL;
    private boolean mIsSaved;

    public static final int INT_FLAG = 0;
    public static final int STRING_FLAG = 1;


    public Song(int id, String name, int length) {
        mID = id;
        mName = name;
        setLength(length);
    }

    public Song(int realID, String songURL) {
        mRealID = realID;
        mSongURL = songURL;
    }

    public Song(int id, int realID, String name, String singer, String length, int pos, String songURL, String albumURL, int isSaved, int flag) {
        mID = id;
        mRealID = realID;
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
        mIsSaved = (isSaved != 0);
    }

    public void setID(int id) {
        mID = id;
    }

    public void setRealID(int realId) {
        mRealID = realId;
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

    public void setSaved(boolean saved) {
        mIsSaved = saved;
    }

    public int getID() {
        return mID;
    }

    public int getRealID() {
        return mRealID;
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

    public String getFullSongURL() {
        if (!mIsSaved) {
            return MusicServiceAPI.SERVER_DOMAIN + mSongURL;
        } else {
            return mSongURL;
        }
    }

    public String getAlbumURL() {
        return mAlbumURL;
    }

    public String getFullAlbumURL() {
        return MusicServiceAPI.SERVER_DOMAIN + mAlbumURL;
    }

    public String getSinger() {
        return mSinger;
    }

    public boolean getIsSaved() {
        return mIsSaved;
    }
}
