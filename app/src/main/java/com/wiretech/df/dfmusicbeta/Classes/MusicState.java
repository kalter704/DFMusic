package com.wiretech.df.dfmusicbeta.Classes;

import java.util.ArrayList;

public class MusicState {
    public static  MusicState instance = new MusicState();

    private static ArrayList<Integer> mSongsIds;
    private static int mCurrentSongIndex = 0;
    private static int mCurrentPlayingSongIndex = 0;

    private MusicState() {}

    public void setNewSongData(ArrayList ss, int currentIndex) {
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

    public void savePlayingSongIndex() {
        mCurrentPlayingSongIndex = mCurrentSongIndex;
    }

    public int getCurrentPlayingSongIndex() {
        return mCurrentPlayingSongIndex;
    }

    public int getCurrentSongIndex() {
        return mCurrentSongIndex;
    }

    public ArrayList<Integer> getSongsIds() {
        return mSongsIds;
    }

    public void setSongsIds(ArrayList<Integer> songsIds) {
        mSongsIds = songsIds;
    }
}
