package com.wiretech.df.dfmusic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.MusicServerResponse;
import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.API.Classes.PlayListResponse;
import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponsePlaylistsListener;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static String LOG_TAG = "DBManager";
    private static boolean isDebug = true;

    private static DBHelper sDBHelper;

    public static void with(Context context) {
        sDBHelper = new DBHelper(context);
    }

    public static boolean hasPlaylistsInDatabase() {
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, null);
        logCursor(c);
        return c.moveToFirst();
    }

    public static void addPlaylists(PlayListResponse playListResponse) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        for (PlayList p: playListResponse.getPlayLists()) {
            cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
            cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
            cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, p.getLastUpdate());
            db.insert(DBHelper.PLAYLIST_TABLE_NAME, null, cv);
        }
        sDBHelper.close();
        writeDataFromTableToLog(DBHelper.PLAYLIST_TABLE_NAME);
    }

    public static void updatePlaylists(PlayListResponse playListResponse) {

    }

    public static void fillDB(MusicServerResponse musicServerResponse) {
        new FillDBBackground().execute(musicServerResponse);
    }

    private static class FillDBBackground extends AsyncTask<MusicServerResponse, Void, Boolean> {

        @Override
        protected Boolean doInBackground(MusicServerResponse... musicServerResponses) {
            Log.d(LOG_TAG, "FillDBBackground.doInBackground");
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = sDBHelper.getWritableDatabase();
            try {
                for (PlayList p: musicServerResponses[0].getPlayLists()) {
                    cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
                    cv.put(DBHelper.PLAYLIST_ID_FIELD, p.getId());
                    cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, "0");
                    long pId = db.insert(DBHelper.PLAYLIST_TABLE_NAME, null, cv);
                    cv.clear();
                    for (Song s: p.getSongs()) {
                        cv.put(DBHelper.SONG_TITLE_FIELD, s.getName());
                        cv.put(DBHelper.SONG_POSITION_FIELD, s.getPos());
                        cv.put(DBHelper.SONG_LENGTH_FIELD, s.getLength());
                        cv.put(DBHelper.SONG_SONG_URL_FIELD, s.getSongURL());
                        cv.put(DBHelper.SONG_ALBUM_URL_FIELD, s.getAlbumURL());
                        cv.put(DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD, pId);
                        db.insert(DBHelper.SONG_TABLE_NAME, null, cv);
                    }
                    cv.clear();
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, p.getLastUpdate());
                    db.update(DBHelper.PLAYLIST_TABLE_NAME, cv, "id = ?", new String[] { String.valueOf(pId) });
                    //sDBHelper.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(LOG_TAG, "FillDBBackground.onPostExecute");

            writeDataFromTableToLog(DBHelper.PLAYLIST_TABLE_NAME);
            writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        }
    }

    private static void writeDataFromTableToLog(String tableName) {
        if (isDebug) {
            SQLiteDatabase db = sDBHelper.getWritableDatabase();
            Cursor c = db.query(tableName, null, null, null, null, null, null);
            logCursor(c);
        }
    }

    private static void logCursor(Cursor c) {
        if (isDebug) {
            if (c != null) {
                if (c.moveToFirst()) {
                    String str;
                    do {
                        str = "";
                        for (String cn : c.getColumnNames()) {
                            str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
                        }
                        Log.d(LOG_TAG, str);
                    } while (c.moveToNext());
                }
            } else {
                Log.d(LOG_TAG, "Cursor is null");
            }
        }
    }

}
