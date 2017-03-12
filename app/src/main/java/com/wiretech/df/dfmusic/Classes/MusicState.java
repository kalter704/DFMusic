package com.wiretech.df.dfmusic.Classes;

import java.util.ArrayList;

public class MusicState {
    public static  MusicState instance = new MusicState();

    private ArrayList<Integer> mSongsIds;
    private int mCurrentSongIndex = 0;

    private MusicState() {}

    public void setNewSongDatas(ArrayList ss, int currentIndex) {
        mSongsIds = ss;
        mCurrentSongIndex = currentIndex;
    }

    public int getSongId() {
        return mSongsIds.get(mCurrentSongIndex);
    }

    public void setPreviousSong() {
        --mCurrentSongIndex;
        if (mCurrentSongIndex < 0) {
            mCurrentSongIndex = mSongsIds.size() - 1;
        }
    }

    public void setNextSong() {
        ++mCurrentSongIndex;
        if (mCurrentSongIndex >= mSongsIds.size()) {
            mCurrentSongIndex = 0;
        }
    }

    public int getCurrentSongIndex() {
        return mCurrentSongIndex;
    }

    /*
    public int getPreviousSongId() {
        --mCurrentSongIndex;
        if (mCurrentSongIndex < 0) {
            mCurrentSongIndex = mSongsIds.size() - 1;
        }
        return mSongsIds.get(mCurrentSongIndex);
    }

    public int getNextSongId() {
        ++mCurrentSongIndex;
        if (mCurrentSongIndex >= mSongsIds.size()) {
            mCurrentSongIndex = 0;
        }
        return mSongsIds.get(mCurrentSongIndex);
    }
    */

}
