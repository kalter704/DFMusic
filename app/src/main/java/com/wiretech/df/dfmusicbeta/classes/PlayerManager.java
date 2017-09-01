package com.wiretech.df.dfmusicbeta.classes;

import android.app.PendingIntent;
import android.content.Context;

import com.wiretech.df.dfmusicbeta.Const;
import com.wiretech.df.dfmusicbeta.api.classes.Playlist;
import com.wiretech.df.dfmusicbeta.api.classes.Song;
import com.wiretech.df.dfmusicbeta.services.PlayerService;

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

    public Song getPlayingSong() {
        return mPlayingSong;
    }

    public Playlist getPlayingPlaylist() {
        return mPlayingPlaylist;
    }

    public void setPlaylist(Playlist p) {
        mFocusPlaylist = p;
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
        return mShuffledSongs;
    }

    public List<Song> resetOrderSongs() {
        mShuffledSongs = new ArrayList<>(mFocusPlaylist.getSongs());
        return mShuffledSongs;
    }

    public void play(Context context, Song song) {
        mPlayingPlaylist = new Playlist(mFocusPlaylist);
        mPlayingPlaylist.setSongs(mShuffledSongs);
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
        int i;
        if ((i = indexOfSong(mPlayingSong, mPlayingPlaylist)) != -1) {
            i++;
            if (i >= mPlayingPlaylist.getSongs().size()) {
                i = 0;
            }
            mPlayingSong = mPlayingPlaylist.getSongs().get(i);
        }
    }

    public void setPreviousSongForPlayer() {
        int i;
        if ((i = indexOfSong(mPlayingSong, mPlayingPlaylist)) != -1) {
            i--;
            if (i < 0) {
                i = mPlayingPlaylist.getSongs().size() - 1;
            }
            mPlayingSong = mPlayingPlaylist.getSongs().get(i);
        }
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

}
