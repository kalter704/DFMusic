package com.wiretech.df.dfmusic.API.Classes;

import java.util.ArrayList;
import java.util.List;

public class PlayListResponse {
    private int count;
    private List<PlayList> mPlayLists;

    public PlayListResponse() {
        mPlayLists = new ArrayList<>();
    }

    public PlayListResponse(int count) {
        this.count = count;
        mPlayLists = new ArrayList<>();
    }

    public void addPlaylist(PlayList p) {
        mPlayLists.add(p);
    }

    public void removePlaylist(int i) {
        mPlayLists.remove(i);
    }
    public void removePlaylist(PlayList p) {
        mPlayLists.remove(p);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PlayList> getPlayLists() {
        return mPlayLists;
    }

    public void setPlayLists(List<PlayList> playLists) {
        mPlayLists = playLists;
    }
}
