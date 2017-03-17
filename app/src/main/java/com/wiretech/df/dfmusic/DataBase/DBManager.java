package com.wiretech.df.dfmusic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.MusicServerResponse;
import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.API.Classes.Song;

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
                for (PlayList p : musicServerResponses[0].getPlayLists()) {
                    cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
                    cv.put(DBHelper.PLAYLIST_ID_FIELD, p.getId());
                    cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
                    cv.put(DBHelper.PLAYLIST_SCHOOL_FIELD, p.getSchoolName());
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, "0");
                    long pId = db.insert(DBHelper.PLAYLIST_TABLE_NAME, null, cv);
                    cv.clear();
                    for (Song s : p.getSongs()) {
                        cv.put(DBHelper.SONG_ID_FIELD, s.getRealId());
                        cv.put(DBHelper.SONG_TITLE_FIELD, s.getName());
                        cv.put(DBHelper.SONG_SINGER_FIELD, s.getSinger());
                        cv.put(DBHelper.SONG_POSITION_FIELD, s.getPos());
                        cv.put(DBHelper.SONG_LENGTH_FIELD, s.getLength());
                        cv.put(DBHelper.SONG_SONG_URL_FIELD, s.getSongURL());
                        cv.put(DBHelper.SONG_ALBUM_URL_FIELD, s.getAlbumURL());
                        cv.put(DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD, pId);
                        cv.put(DBHelper.SONG_IS_SAVED, s.getIsSaved());
                        db.insert(DBHelper.SONG_TABLE_NAME, null, cv);
                    }
                    cv.clear();
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, p.getLastUpdate());
                    db.update(DBHelper.PLAYLIST_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(pId)});
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

                List<Integer> idsList = new ArrayList<>();
                Cursor c = db.query(DBHelper.SAVED_SONG_TABLE_NAME, new String[]{DBHelper.SAVED_SONG_ID_FIELD}, null, null, null, null, null);
                logCursor(c);
                if (c.moveToFirst()) {
                    do {
                        idsList.add(c.getInt(c.getColumnIndex(DBHelper.SAVED_SONG_ID_FIELD)));
                    } while (c.moveToNext());
                }

                for (PlayList p : musicServerResponses[0].getPlayLists()) {
                    String selection = DBHelper.PLAYLIST_ID_FIELD + " = ?";
                    c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, selection, new String[]{String.valueOf(p.getId())}, null, null, null);
                    logCursor(c);
                    c.moveToFirst();
                    long pId = c.getLong(c.getColumnIndex("id"));
                    cv.put(DBHelper.PLAYLIST_TITLE_FIELD, p.getName());
                    cv.put(DBHelper.PLAYLIST_ID_FIELD, p.getId());
                    cv.put(DBHelper.PLAYLIST_POSITION_FIELD, p.getPos());
                    cv.put(DBHelper.PLAYLIST_SCHOOL_FIELD, p.getSchoolName());
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, "0");
                    db.update(DBHelper.PLAYLIST_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(pId)});
                    cv.clear();
                    db.delete(DBHelper.SONG_TABLE_NAME, DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = " + String.valueOf(pId), null);
                    for (Song s : p.getSongs()) {
                        cv.put(DBHelper.SONG_ID_FIELD, s.getRealId());
                        cv.put(DBHelper.SONG_TITLE_FIELD, s.getName());
                        cv.put(DBHelper.SONG_SINGER_FIELD, s.getSinger());
                        cv.put(DBHelper.SONG_POSITION_FIELD, s.getPos());
                        cv.put(DBHelper.SONG_LENGTH_FIELD, s.getLength());
                        cv.put(DBHelper.SONG_SONG_URL_FIELD, s.getSongURL());
                        cv.put(DBHelper.SONG_ALBUM_URL_FIELD, s.getAlbumURL());
                        if (idsList.contains(s.getRealId())) {
                            cv.put(DBHelper.SONG_IS_SAVED, 1);
                        } else {
                            cv.put(DBHelper.SONG_IS_SAVED, 0);
                        }
                        cv.put(DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD, pId);
                        db.insert(DBHelper.SONG_TABLE_NAME, null, cv);
                    }
                    cv.clear();
                    cv.put(DBHelper.PLAYLIST_LAST_UPDATE_FIELD, p.getLastUpdate());
                    db.update(DBHelper.PLAYLIST_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(pId)});
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

    public static ArrayList<PlayList> getPlayListsWithNameAndSchool() {
        ArrayList<PlayList> playLists = new ArrayList<>();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, DBHelper.PLAYLIST_POSITION_FIELD);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int nameIndex = c.getColumnIndex(DBHelper.PLAYLIST_TITLE_FIELD);
            int schoolIndex = c.getColumnIndex(DBHelper.PLAYLIST_SCHOOL_FIELD);
            do {
                playLists.add(new PlayList(
                        c.getInt(idIndex),
                        c.getString(nameIndex),
                        c.getString(schoolIndex)
                ));
            } while (c.moveToNext());
        }
        return playLists;
    }

    public static List<Song> getSongsByPlayListId(int playListId) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(playListId)};
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
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int realIdIndex = c.getColumnIndex(DBHelper.SONG_ID_FIELD);
            int posIndex = c.getColumnIndex(DBHelper.SONG_POSITION_FIELD);
            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
            int singerIndex = c.getColumnIndex(DBHelper.SONG_SINGER_FIELD);
            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
            int songUrlIndex = c.getColumnIndex(DBHelper.SONG_SONG_URL_FIELD);
            int albumUrlIndex = c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD);
            int isSavedIndex = c.getColumnIndex(DBHelper.SONG_IS_SAVED);

            int realId = c.getInt(realIdIndex);
            int isSaved = c.getInt(isSavedIndex);

            String selection2 = DBHelper.SAVED_SONG_ID_FIELD + " = ?";
            String[] selectionArgs2 = new String[]{String.valueOf(realId)};
            Cursor c2 = db.query(DBHelper.SAVED_SONG_TABLE_NAME, null, selection2, selectionArgs2, null, null, null);
            logCursor(c2);
            String path;
            if (c2.moveToFirst() && (isSaved == 1)) {
                path = c2.getString(c2.getColumnIndex(DBHelper.SAVED_SONG_PATH_FIELD));
            } else {
                path = c.getString(songUrlIndex);
            }

            song = new Song(
                    c.getInt(idIndex),
                    c.getInt(realIdIndex),
                    c.getString(nameIndex),
                    c.getString(singerIndex),
                    c.getString(lengthIndex),
                    c.getInt(posIndex),
                    path,
                    c.getString(albumUrlIndex),
                    isSaved,
                    Song.INT_FLAG
            );
        }
        return song;
    }

    public static Song getFirstSongByPlayListId(int playListId) {
        Song song = null;
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(playListId)};
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            int realIdIndex = c.getColumnIndex(DBHelper.SONG_ID_FIELD);
            int posIndex = c.getColumnIndex(DBHelper.SONG_POSITION_FIELD);
            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
            int singerIndex = c.getColumnIndex(DBHelper.SONG_SINGER_FIELD);
            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
            int songUrlIndex = c.getColumnIndex(DBHelper.SONG_SONG_URL_FIELD);
            int albumUrlIndex = c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD);
            int isSavedIndex = c.getColumnIndex(DBHelper.SONG_IS_SAVED);
            song = new Song(
                    c.getInt(idIndex),
                    c.getInt(realIdIndex),
                    c.getString(nameIndex),
                    c.getString(singerIndex),
                    c.getString(lengthIndex),
                    c.getInt(posIndex),
                    c.getString(songUrlIndex),
                    c.getString(albumUrlIndex),
                    c.getInt(isSavedIndex),
                    Song.INT_FLAG
            );
        }
        return song;
    }

    public static ArrayList<Integer> getSongsIdsByPLayListId(int playListId) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(playListId)};
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, new String[]{"id"}, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
        logCursor(c);
        if (c.moveToFirst()) {
            int idIndex = c.getColumnIndex("id");
            do {
                list.add(c.getInt(idIndex));
            } while (c.moveToNext());
        }
        return list;
    }

    public static PlayList getPlayListBySongId(int songId) {
        PlayList playList = null;
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[]{String.valueOf(songId)};
        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, new String[]{DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD}, selection, selectionArgs, null, null, null);
        logCursor(c);
        if (c.moveToFirst()) {
            selection = "id = ?";
            selectionArgs = new String[]{c.getString(c.getColumnIndex(DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD))};
            c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, selection, selectionArgs, null, null, null);
            if (c.moveToFirst()) {
                playList = new PlayList(
                        c.getInt(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex(DBHelper.PLAYLIST_TITLE_FIELD)),
                        c.getString(c.getColumnIndex(DBHelper.PLAYLIST_SCHOOL_FIELD)),
                        c.getString(c.getColumnIndex(DBHelper.PLAYLIST_LAST_UPDATE_FIELD)),
                        c.getInt(c.getColumnIndex(DBHelper.PLAYLIST_POSITION_FIELD))
                );
            }
        }
        return playList;
    }

    public static PlayList getPLayListById(int playListId) {
        PlayList playList = null;
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        String selection = "id = ?";
        String[] selectionArgs = new String[]{String.valueOf(playListId)};
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if (c.moveToFirst()) {
            playList = new PlayList(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex(DBHelper.PLAYLIST_TITLE_FIELD)),
                    c.getString(c.getColumnIndex(DBHelper.PLAYLIST_SCHOOL_FIELD)),
                    c.getString(c.getColumnIndex(DBHelper.PLAYLIST_LAST_UPDATE_FIELD)),
                    c.getInt(c.getColumnIndex(DBHelper.PLAYLIST_POSITION_FIELD))
            );
        }
        return playList;
    }

    public static void setSaveSongBySong(Song song) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        cv.put(DBHelper.SAVED_SONG_ID_FIELD, song.getRealId());
        cv.put(DBHelper.SAVED_SONG_PATH_FIELD, song.getSongURL());
        db.insert(DBHelper.SAVED_SONG_TABLE_NAME, null, cv);
        cv.clear();

        cv.put(DBHelper.SONG_IS_SAVED, 1);
        db.update(DBHelper.SONG_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(song.getId())});
        cv.clear();
        writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
    }

    public static void setUnSaveSongBySong(Song song) {
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        cv.put(DBHelper.SONG_IS_SAVED, 0);
        db.update(DBHelper.SONG_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(song.getId())});
        cv.clear();

        db.delete(DBHelper.SAVED_SONG_TABLE_NAME, DBHelper.SAVED_SONG_ID_FIELD + " = " + String.valueOf(song.getRealId()), null);

        writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
    }

    public static void setUnSaveSongBySong(SQLiteDatabase db, Song song) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.SONG_IS_SAVED, 0);
        db.update(DBHelper.SONG_TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(song.getId())});
        cv.clear();

        db.delete(DBHelper.SAVED_SONG_TABLE_NAME, DBHelper.SAVED_SONG_ID_FIELD + " = " + String.valueOf(song.getRealId()), null);

        //writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        //writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
    }

    public static List<Song> getAllSavedSongs(SQLiteDatabase db) {
        List<Song> songs = new ArrayList<>();
        /*
        Cursor c = db.query(DBHelper.SAVED_SONG_TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            c.getColumnIndex(DBHelper.SAVED_SONG_ID_FIELD);
            c.getColumnIndex(DBHelper.SAVED_SONG_PATH_FIELD);
            do {

            } while (c.moveToNext());
        }
        */
        String sqlQuery = "select "
                + " SONG.id"
                + ", SONG." + DBHelper.SONG_ID_FIELD
                + ", SONG." + DBHelper.SONG_TITLE_FIELD
                + ", SONG." + DBHelper.SONG_SINGER_FIELD
                + ", SONG." + DBHelper.SONG_POSITION_FIELD
                + ", SONG." + DBHelper.SONG_LENGTH_FIELD
                + ", SONG." + DBHelper.SONG_ALBUM_URL_FIELD
                + ", SONG." + DBHelper.SONG_IS_SAVED
                + ", SAVED_SONG." + DBHelper.SAVED_SONG_PATH_FIELD
                + " from " + DBHelper.SONG_TABLE_NAME + " as SONG "
                + "inner join " + DBHelper.SAVED_SONG_TABLE_NAME + " as SAVED_SONG "
                + "on SONG." + DBHelper.SONG_ID_FIELD + " = SAVED_SONG." + DBHelper.SAVED_SONG_ID_FIELD + " ";
        Cursor c = null;
        try {
            c = db.rawQuery(sqlQuery, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG, "Request result!!!");
        logCursor(c);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    songs.add(new Song(
                            c.getInt(c.getColumnIndex("id")),
                            c.getInt(c.getColumnIndex(DBHelper.SONG_ID_FIELD)),
                            c.getString(c.getColumnIndex(DBHelper.SONG_TITLE_FIELD)),
                            c.getString(c.getColumnIndex(DBHelper.SONG_SINGER_FIELD)),
                            c.getString(c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD)),
                            c.getInt(c.getColumnIndex(DBHelper.SONG_POSITION_FIELD)),
                            c.getString(c.getColumnIndex(DBHelper.SAVED_SONG_PATH_FIELD)),
                            c.getString(c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD)),
                            c.getInt(c.getColumnIndex(DBHelper.SONG_IS_SAVED)),
                            Song.INT_FLAG
                    ));
                } while (c.moveToNext());
            }
        }
        return songs;
    }

    public static void deleteDataFromSavedTable(SQLiteDatabase db) {
        try {
            db.delete(DBHelper.SAVED_SONG_TABLE_NAME, null, null);
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.SONG_IS_SAVED, "0");
            db.update(DBHelper.SONG_TABLE_NAME, cv, DBHelper.SONG_IS_SAVED + " = ?", new String[]{String.valueOf("1")});
        } catch (Exception e) {
            e.printStackTrace();
        }

        //writeDataFromTableToLog(DBHelper.PLAYLIST_TABLE_NAME);
        //writeDataFromTableToLog(DBHelper.SONG_TABLE_NAME);
        //writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
    }

    public static ArrayList<Integer> getSavedSongsIds() {
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        ArrayList<Integer> ids = new ArrayList<>();
        String sqlQuery = "select "
                + "SONG.id "
                + "from " + DBHelper.SAVED_SONG_TABLE_NAME + " as SAVED_SONG "
                + "inner join " + DBHelper.SONG_TABLE_NAME + " as SONG "
                + "on SONG." + DBHelper.SONG_ID_FIELD + " = SAVED_SONG." + DBHelper.SAVED_SONG_ID_FIELD + " ";
        Cursor c = db.rawQuery(sqlQuery, null);
        logCursor(c);
        if (c.moveToFirst()) {
            do {
                ids.add(c.getInt(c.getColumnIndex("id")));
            } while (c.moveToNext());
        }
        return ids;
    }

    public static List<Song> getSongsBySongsIds(ArrayList<Integer> ids) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = sDBHelper.getWritableDatabase();

        // Этот вариант работает быстрее, но порядок песен получается как в БД
        String idsStr = "(";
        for (Integer id : ids) {
            idsStr += "\"" + String.valueOf(id) + "\",";
        }
        idsStr += "\"-1\")";

        String sqlQuery = "select "
                + "SONG.id"
                + ", SONG." + DBHelper.SONG_TITLE_FIELD
                + ", SONG." + DBHelper.SONG_LENGTH_FIELD
                + " from " + DBHelper.SONG_TABLE_NAME + " as SONG "
                + "where id in " + idsStr;
        Cursor c = db.rawQuery(sqlQuery, null);
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

        /*
        String tempStr = "ids: ";
        for (Integer id : ids) {
            tempStr += id + " ";
        }
        Log.d(LOG_TAG, tempStr);
        tempStr = "before sort: ";
        for (Song s : songs) {
            tempStr += s.getId() + " ";
        }
        Log.d(LOG_TAG, tempStr);
        */
        // Алгоритм изменения порядка
        for (int i = 0; i < ids.size(); ++i) {
            int id = ids.get(i);
            for (int j = i; j < songs.size(); ++j) {
                if (songs.get(j).getId() == id) {
                    songs.add(i, songs.remove(j));
                }
            }
        }
        /*
        tempStr = "after sort: ";
        for (Song s : songs) {
            tempStr += s.getId() + " ";
        }
        Log.d(LOG_TAG, tempStr);
        */

        return songs;
    }

    public static void writeToLogDataFromTables() {
        writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
    }

    private static void writeDataFromTableToLog(String tableName) {
        if (isDebug) {
            SQLiteDatabase db = sDBHelper.getWritableDatabase();
            Cursor c = db.query(tableName, null, null, null, null, null, null);
            Log.d(LOG_TAG, "Table name = " + tableName);
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
