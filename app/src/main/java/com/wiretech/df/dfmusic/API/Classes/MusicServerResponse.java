package com.wiretech.df.dfmusic.API.Classes;

import java.util.ArrayList;
import java.util.List;

public class MusicServerResponse {
    private int index = 0;
    private int count;
    private List<PlayList> mPlayLists = new ArrayList<>();

    public void addSongToPlailist(int i, Song s) {
        mPlayLists.get(i).addSong(s);
    }

    public void addPlaylist(PlayList p) {
        mPlayLists.add(p);
    }

    public PlayList getCurrentPlaylist() {
        if (index < mPlayLists.size()) {
            return mPlayLists.get(index);
        } else {
            return null;
        }
    }

    public void moveToFirst() {
        index = 0;
    }

    public void nextPlaylist() {
        ++index;
    }

    public int getIndex() {
        return index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public PlayList getPlaylistByIndex(int i) {
        return mPlayLists.get(i);
    }

    public List<PlayList> getPlayLists() {
        return mPlayLists;
    }

    public void setPlayLists(List<PlayList> playLists) {
        mPlayLists = playLists;
    }
}
