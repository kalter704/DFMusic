package com.wiretech.df.dfmusic.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_db";
    private static final int DATABASE_VERSION = 4;

    public static final String PLAYLIST_TABLE_NAME = "playlist";
    public static final String PLAYLIST_ID_FIELD = "p_id";
    public static final String PLAYLIST_TITLE_FIELD = "title";
    public static final String PLAYLIST_POSITION_FIELD = "pos";
    public static final String PLAYLIST_LAST_UPDATE_FIELD = "last_update";

    public static final String SONG_TABLE_NAME = "song";
    public static final String SONG_TITLE_FIELD = "title";
    public static final String SONG_POSITION_FIELD = "pos";
    public static final String SONG_LENGTH_FIELD = "length";
    public static final String SONG_SONG_URL_FIELD = "song_url";
    public static final String SONG_ALBUM_URL_FIELD = "album_url";
    public static final String SONG_CONNECT_TO_PLAYLIST_FIELD = "playlist_id_form_con";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createPlaylistTable(db);
        createSongTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.beginTransaction();
        try {
            db.execSQL("drop table if exists " + PLAYLIST_TABLE_NAME + ";");
            db.execSQL("drop table if exists " + SONG_TABLE_NAME + ";");
            onCreate(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createPlaylistTable(SQLiteDatabase db) {
        db.execSQL("create table " + PLAYLIST_TABLE_NAME + " ("
                + "id integer primary key autoincrement, "
                + PLAYLIST_ID_FIELD + " integer, "
                + PLAYLIST_TITLE_FIELD + " text, "
                + PLAYLIST_POSITION_FIELD + " integer, "
                + PLAYLIST_LAST_UPDATE_FIELD + " text "
                + ");");
    }

    private void createSongTable(SQLiteDatabase db) {
        db.execSQL("create table " + SONG_TABLE_NAME + " ("
                + "id integer primary key autoincrement, "
                + SONG_TITLE_FIELD + " text, "
                + SONG_POSITION_FIELD + " integer, "
                + SONG_LENGTH_FIELD + " integer, "
                + SONG_SONG_URL_FIELD + " text, "
                + SONG_ALBUM_URL_FIELD + " text, "
                + SONG_CONNECT_TO_PLAYLIST_FIELD + " integer "
                + ");");
    }

}
