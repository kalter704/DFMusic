package com.wiretech.df.dfmusic.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.wiretech.df.dfmusic.api.classes.Playlist;

import com.wiretech.df.dfmusic.api.classes.Song;
import com.wiretech.df.dfmusic.database.DBScheme.PlaylistTable;
import com.wiretech.df.dfmusic.database.DBScheme.SongTable;

public class MusicCursorWrapper extends CursorWrapper {
    public MusicCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Playlist getPlaylist() {
        return new Playlist(
                getInt(getColumnIndex(PlaylistTable.Cols.ID)),
                getString(getColumnIndex(PlaylistTable.Cols.NAME)),
                getString(getColumnIndex(PlaylistTable.Cols.SCHOOL)),
                getString(getColumnIndex(PlaylistTable.Cols.LAST_UPDATE)),
                getInt(getColumnIndex(PlaylistTable.Cols.POSITION))
        );
    }

    public Song getSong() {
        return new Song(
                getInt(getColumnIndex(SongTable.Cols._ID)),
                getInt(getColumnIndex(SongTable.Cols.ID)),
                getString(getColumnIndex(SongTable.Cols.NAME)),
                getString(getColumnIndex(SongTable.Cols.SINGER)),
                getString(getColumnIndex(SongTable.Cols.LENGTH)),
                getInt(getColumnIndex(SongTable.Cols.POSITION)),
                getString(getColumnIndex(SongTable.Cols.SONG_URL)),
                getString(getColumnIndex(SongTable.Cols.IMG_URL)),
                getInt(getColumnIndex(SongTable.Cols.SAVED)),
                Song.INT_FLAG
        );
    }

    public Song getSong(String path) {
        return new Song(
                getInt(getColumnIndex(SongTable.Cols._ID)),
                getInt(getColumnIndex(SongTable.Cols.ID)),
                getString(getColumnIndex(SongTable.Cols.NAME)),
                getString(getColumnIndex(SongTable.Cols.SINGER)),
                getString(getColumnIndex(SongTable.Cols.LENGTH)),
                getInt(getColumnIndex(SongTable.Cols.POSITION)),
                path,
                getString(getColumnIndex(SongTable.Cols.IMG_URL)),
                getInt(getColumnIndex(SongTable.Cols.SAVED)),
                Song.INT_FLAG
        );
    }
}
