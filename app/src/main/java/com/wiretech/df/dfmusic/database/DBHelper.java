package com.wiretech.df.dfmusic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wiretech.df.dfmusic.classes.MusicDownloadManager;

import com.wiretech.df.dfmusic.database.DBScheme.PlaylistTable;
import com.wiretech.df.dfmusic.database.DBScheme.SongTable;
import com.wiretech.df.dfmusic.database.DBScheme.SavedSongTable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_db";
    public static final int DATABASE_VERSION = 25;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlaylistTable(db);
        createSongTable(db);
        createSavedSongTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.beginTransaction();
        try {
            MusicDownloadManager.instance.deleteAllSavedSongs(db);
            db.execSQL("drop table if exists " + PlaylistTable.TABLE_NAME + ";");
            db.execSQL("drop table if exists " + SongTable.TABLE_NAME + ";");
            db.execSQL("drop table if exists " + SavedSongTable.TABLE_NAME + ";");

            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createPlaylistTable(SQLiteDatabase db) {
        db.execSQL("create table " + PlaylistTable.TABLE_NAME + " ("
                + PlaylistTable.Cols._ID + " integer primary key autoincrement, "
                + PlaylistTable.Cols.ID + " integer, "
                + PlaylistTable.Cols.NAME + " text, "
                + PlaylistTable.Cols.SCHOOL + " text, "
                + PlaylistTable.Cols.POSITION + " integer, "
                + PlaylistTable.Cols.LAST_UPDATE + " text "
                + ");");
    }

    private void createSongTable(SQLiteDatabase db) {
        db.execSQL("create table " + SongTable.TABLE_NAME + " ("
                + SongTable.Cols._ID + " integer primary key autoincrement, "
                + SongTable.Cols.ID + " integer, "
                + SongTable.Cols.NAME + " text, "
                + SongTable.Cols.SINGER + " text, "
                + SongTable.Cols.POSITION + " integer, "
                + SongTable.Cols.LENGTH + " text, "
                + SongTable.Cols.SONG_URL + " text, "
                + SongTable.Cols.IMG_URL + " text, "
                + SongTable.Cols.PLAYLIST_ID + " integer, "
                + SongTable.Cols.SAVED + " integer "
                + ");");
    }

    private void createSavedSongTable(SQLiteDatabase db) {
        db.execSQL("create table " + SavedSongTable.TABLE_NAME + " ("
                + SavedSongTable.Cols._ID + " integer primary key autoincrement, "
                + SavedSongTable.Cols.SONG_ID + " integer, "
                + SavedSongTable.Cols.PATH + " text "
                + ");");
    }

}
