package com.wiretech.df.dfmusicbeta.api.classes;

import java.util.ArrayList;
import java.util.List;

public class PlayListResponse {
    private int count;
    private List<Playlist> mPlaylists;

    public PlayListResponse() {
        mPlaylists = new ArrayList<>();
    }

    public PlayListResponse(int count) {
        this.count = count;
        mPlaylists = new ArrayList<>();
    }

    public void addPlaylist(Playlist p) {
        mPlaylists.add(p);
    }

    public void removePlaylist(int i) {
        mPlaylists.remove(i);
    }
    public void removePlaylist(Playlist p) {
        mPlaylists.remove(p);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        mPlaylists = playlists;
    }
}
