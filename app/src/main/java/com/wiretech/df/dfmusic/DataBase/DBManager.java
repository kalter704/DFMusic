package com.wiretech.df.dfmusic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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

    /*
    public static void addPlaylists(PlayListResponse playListResponse) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        for (PlayList p: playListResponse.getPlayLists()) {
            cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
            cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
            cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, p.getLastUpdate());
            db.insert(DBHelper.PLAYLIST_TABLE_NAME, null, cv);
        }
        writeDataFromTableToLog(DBHelper.PLAYLIST_TABLE_NAME);
    }
    */

    /*
    public static void updatePlaylists(PlayListResponse playListResponse) {

    }
    */

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

    public static void updatePlaylists(MusicServerResponse m) {
        new UpdateDBBackground().execute(m);
    }

    private static class UpdateDBBackground extends AsyncTask<MusicServerResponse, Void, Boolean> {

        @Override
        protected Boolean doInBackground(MusicServerResponse... musicServerResponses) {
            Log.d(LOG_TAG, "FillDBBackground.doInBackground");
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = sDBHelper.getWritableDatabase();
            try {
                for (PlayList p: musicServerResponses[0].getPlayLists()) {
                    String selection = DBHelper.PLAYLIST_ID_FIELD + " = ?";
                    Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, selection, new String[] { String.valueOf(p.getId()) }, null, null, null);
                    logCursor(c);
                    c.moveToFirst();
                    long pId = c.getLong(c.getColumnIndex("id"));
                    cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
                    cv.put(DBHelper.PLAYLIST_ID_FIELD, p.getId());
                    cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, "0");
                    db.update(DBHelper.PLAYLIST_TABLE_NAME, cv, "id = ?", new String[] { String.valueOf(pId) });
                    cv.clear();
                    db.delete(DBHelper.SONG_TABLE_NAME, DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = " + String.valueOf(pId), null);
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
                    cv.clear();
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
            Log.d(LOG_TAG, "UpdateDBBackground.onPostExecute");

            writeDataFromTableToLog(DBHelper.PLAYLIST_TABLE_NAME);
            writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        }
    }

    public static List<Integer> getIndexsOfDifferentPlaylists(MusicServerResponse m) {
        List<Integer> result = new ArrayList<>();
        m.moveToFirst();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int lastUpadateColIndex = c.getColumnIndex(DBHelper.PLAYLIST_LAST_UPDATE_FIELD);
            do {
                String dbStr = c.getString(lastUpadateColIndex);
                String reqStr = m.getCurrentPlaylist().getLastUpdate();
                if (!dbStr.equals(reqStr)) {
                    result.add(m.getIndex());
                }
                m.nextPlaylist();
            } while (c.moveToNext());
        }
        return result;
    }

    public static List<PlayList> getPlayListsNames() {
        List<PlayList> playListsNamesAndIds = new ArrayList<>();

        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, DBHelper.PLAYLIST_POSITION_FIELD);
        if (c.moveToFirst()) {
            int nameIndex = c.getColumnIndex(DBHelper.PLAYLIST_TITLE_FIELD);
            int idIndex = c.getColumnIndex("id");
            do {
                playListsNamesAndIds.add(new PlayList(c.getInt(idIndex), c.getString(nameIndex)));
            } while (c.moveToNext());
        }

        return playListsNamesAndIds;
    }

    public static List<Song> getSongsByPlayListId(int playListId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(playListId) };
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
            do {
                songs.add(new Song(
                        c.getInt(idIndex),
                        c.getString(nameIndex),
                        c.getInt(lengthIndex)
                ));
            } while (c.moveToNext());
        }
        return songs;
    }

    public static Song getSongById(int id) {
        Song song = null;
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[] { String.valueOf(id) };
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int posIndex = c.getColumnIndex(DBHelper.SONG_POSITION_FIELD);
            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
            int songUrlIndex = c.getColumnIndex(DBHelper.SONG_SONG_URL_FIELD);
            int albumUrlIndex = c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD);
            song = new Song(
                    c.getInt(idIndex),
                    c.getString(nameIndex),
                    c.getString(lengthIndex),
                    c.getInt(posIndex),
                    c.getString(songUrlIndex),
                    c.getString(albumUrlIndex),
                    Song.INT_FLAG
            );
        }
        return song;
    }

    public static Song getFirstSongByPlayListId(int playListId) {
        Song song = null;
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
        String[] selectionArgs = new String[] { String.valueOf(playListId) };
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int posIndex = c.getColumnIndex(DBHelper.SONG_POSITION_FIELD);
            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
            int songUrlIndex = c.getColumnIndex(DBHelper.SONG_SONG_URL_FIELD);
            int albumUrlIndex = c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD);
            song = new Song(
                    c.getInt(idIndex),
                    c.getString(nameIndex),
                    c.getString(lengthIndex),
                    c.getInt(posIndex),
                    c.getString(songUrlIndex),
                    c.getString(albumUrlIndex),
                    Song.INT_FLAG
            );
        }
        return song;
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
