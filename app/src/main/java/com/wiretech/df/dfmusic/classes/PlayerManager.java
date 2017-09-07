package com.wiretech.df.dfmusic.classes;

import android.app.PendingIntent;
import android.content.Context;

import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.api.classes.Playlist;
import com.wiretech.df.dfmusic.api.classes.Song;
import com.wiretech.df.dfmusic.services.PlayerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerManager {

    private static PlayerManager sPlayerManager;

    private PlayerManager() {}

    public static PlayerManager get() {
        if (sPlayerManager == null) {
            sPlayerManager = new PlayerManager();
        }
        return sPlayerManager;
    }

    private Song mPlayingSong = null;

    private Playlist mPlayingPlaylist = null;
    private Playlist mFocusPlaylist = null;
    private List<Song> mShuffledSongs = null;

    private boolean isShuffle = false;

    public Song getPlayingSong() {
        return mPlayingSong;
    }

    public Playlist getPlayingPlaylist() {
        return mPlayingPlaylist;
    }

    public void setPlaylist(Playlist p) {
        mFocusPlaylist = new Playlist(p);
        mShuffledSongs = new ArrayList<>(mFocusPlaylist.getSongs());
    }

    public Playlist getPlaylist() {
        return mFocusPlaylist;
    }

    public List<Song> getSongs() {
        return mShuffledSongs;
    }

    public List<Song> shuffleSongs() {
        Collections.shuffle(mShuffledSongs);
        isShuffle = !isShuffle;
        if (mPlayingPlaylist != null
                && mPlayingPlaylist.getID() == mFocusPlaylist.getID()) {
            mPlayingPlaylist.setSongs(mShuffledSongs);
            mPlayingPlaylist.setShuffle(true);
        }
        return mShuffledSongs;
    }

    public List<Song> resetOrderSongs() {
        mShuffledSongs = new ArrayList<>(mFocusPlaylist.getSongs());
        if (mPlayingPlaylist != null
                && mPlayingPlaylist.getID() == mFocusPlaylist.getID()) {
            mPlayingPlaylist.setSongs(mShuffledSongs);
            mPlayingPlaylist.setShuffle(false);
        }
        return mShuffledSongs;
    }

    public void play(Context context, Song song) {
        mPlayingPlaylist = new Playlist(mFocusPlaylist);
        mPlayingPlaylist.setSongs(mShuffledSongs);
        mPlayingPlaylist.setShuffle(isShuffle);
        for (Song s : mPlayingPlaylist.getSongs()) {
            if (s.getRealID() == song.getRealID()) {
                mPlayingSong = s;
                break;
            }
        }
        sendPendingIntent(context, Const.ACTION.PLAY_ACTION);
    }

    public void resume(Context context) {
        sendPendingIntent(context, Const.ACTION.PLAY_ACTION);
    }

    public void pause(Context context) {
        sendPendingIntent(context, Const.ACTION.PAUSE_ACTION);
    }

    public void stop(Context context) {
        sendPendingIntent(context, Const.ACTION.STOP_ACTION);
    }

    public void next(Context context) {
        setNextSongForPlayer();
        sendPendingIntent(context, Const.ACTION.PLAY_NEXT_ACTION);
    }

    public void previous(Context context) {
        setPreviousSongForPlayer();
        sendPendingIntent(context, Const.ACTION.PLAY_PREVIOUS_ACTION);
    }

    public void interrupt(Context context) {
        sendPendingIntent(context, Const.ACTION.INTERRUPT_ACTION);
    }

    public void setNextSongForPlayer() {
        Song oldSong = mPlayingSong;
        int i;
        if ((i = indexOfSong(mPlayingSong, mPlayingPlaylist)) != -1) {
            i++;
            if (i >= mPlayingPlaylist.getSongs().size()) {
                i = 0;
            }
            mPlayingSong = mPlayingPlaylist.getSongs().get(i);
        }
        Player.get().onChangeSong(oldSong);
    }

    public void setPreviousSongForPlayer() {
        Song oldSong = mPlayingSong;
        int i;
        if ((i = indexOfSong(mPlayingSong, mPlayingPlaylist)) != -1) {
            i--;
            if (i < 0) {
                i = mPlayingPlaylist.getSongs().size() - 1;
            }
            mPlayingSong = mPlayingPlaylist.getSongs().get(i);
        }
        Player.get().onChangeSong(oldSong);
    }

    private int indexOfSong(Song song, Playlist playlist) {
        int i = 0;
        for (Song s : playlist.getSongs()) {
            if (s.getRealID() == song.getRealID()) {
                break;
            }
            ++i;
        }
        return (playlist.getSongs().size() != i) ? i : -1;
    }

    public Song getSelectedSongByID(int id) {
        for (Song s : mShuffledSongs) {
            if (s.getRealID() == id) {
                return s;
            }
        }
        return null;
    }

    public Song getNextSelectedSongByID(int id) {
        int i;
        for (i = 0; i < mShuffledSongs.size(); ++i) {
            if (mShuffledSongs.get(i).getRealID() == id) {
                break;
            }
        }
        i++;
        if (i >= mShuffledSongs.size()) {
            i = 0;
        }
        return mShuffledSongs.get(i);
    }

    public Song getPreviousSelectedSongByID(int id) {
        int i;
        for (i = 0; i < mShuffledSongs.size(); ++i) {
            if (mShuffledSongs.get(i).getRealID() == id) {
                break;
            }
        }
        i--;
        if (i < 0) {
            i = mShuffledSongs.size() - 1;
        }
        return mShuffledSongs.get(i);
    }

    private void sendPendingIntent(Context context, String action) {
        PendingIntent pi = PlayerService.newPendingIntentToService(context, action);
        try {
            pi.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public boolean isShuffle() {
        return isShuffle;
    }

    public void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }
}
