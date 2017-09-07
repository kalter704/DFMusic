package com.wiretech.df.dfmusic.api.classes;

import java.util.ArrayList;
import java.util.List;

public class MusicServerResponse {
    private int index = 0;
    private int count;
    private String sounds = null;
    private List<Playlist> mPlaylists = new ArrayList<>();

    public void addSongToPlailist(int i, Song s) {
        mPlaylists.get(i).addSong(s);
    }

    public void addPlaylist(Playlist p) {
        mPlaylists.add(p);
    }

    public Playlist getCurrentPlaylist() {
        if (index < mPlaylists.size()) {
            return mPlaylists.get(index);
        } else {
            return null;
        }
    }

    public void moveToFirst() {
        index = 0;
    }

    public boolean nextPlaylist() {
        ++index;
        if (index >= mPlaylists.size()) {
            return false;
        }
        return true;
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

    public Playlist getPlaylistByIndex(int i) {
        return mPlaylists.get(i);
    }

    public List<Playlist> getPlaylists() {
        return mPlaylists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        mPlaylists = playlists;
    }

    public String getSounds() {
        return sounds;
    }

    public void setSounds(String sounds) {
        this.sounds = sounds;
    }
}
