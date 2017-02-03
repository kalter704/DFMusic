package com.wiretech.df.dfmusic.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "music_db_test1";
    private static final int DATABASE_VERSION = 1;

    public static final String PLAYLIST_TABLE_NAME = "playlist";

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

    }

    private void createPlaylistTable(SQLiteDatabase db) {
        db.execSQL("create table " + PLAYLIST_TABLE_NAME + " ("
                + "id integer primary key autoincrement,"
                + "title text,"
                + "pos integer"
                + ");");
    }

    private void createSongTable(SQLiteDatabase db) {

    }

    private void deletePlaylistTable(SQLiteDatabase db) {

    }

    private void deleteSongTable(SQLiteDatabase db) {

    }

}
