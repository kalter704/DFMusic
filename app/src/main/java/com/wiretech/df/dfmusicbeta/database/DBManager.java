package com.wiretech.df.dfmusicbeta.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.wiretech.df.dfmusicbeta.api.classes.MusicServerResponse;
import com.wiretech.df.dfmusicbeta.api.classes.Playlist;
import com.wiretech.df.dfmusicbeta.api.classes.Song;

import com.wiretech.df.dfmusicbeta.database.DBScheme.PlaylistTable;
import com.wiretech.df.dfmusicbeta.database.DBScheme.SongTable;
import com.wiretech.df.dfmusicbeta.database.DBScheme.SavedSongTable;

import java.util.ArrayList;
import java.util.List;

public class DBManager {

    private static String LOG_TAG = "DBManager";

    private DBHelper mDBHelper;

    private static DBManager sDBManager = null;

    private DBManager (Context context) {
        mDBHelper = new DBHelper(context);
    }

    public static DBManager get(Context context) {
        if (sDBManager == null) {
            sDBManager = new DBManager(context);
        }
        return sDBManager;
    }

    public boolean hasPlaylistsInDatabase() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = db.query(PlaylistTable.TABLE_NAME, null, null, null, null, null, null);
        boolean has = c.moveToFirst();
        c.close();
        return has;
    }

    public ContentValues createContentValuesFromPlaylist(Playlist p) {
        ContentValues cv = new ContentValues();
        cv.put(PlaylistTable.Cols.NAME, p.getName());
        cv.put(PlaylistTable.Cols.ID, p.getID());
        cv.put(PlaylistTable.Cols.POSITION, p.getPos());
        cv.put(PlaylistTable.Cols.SCHOOL, p.getSchoolName());
        cv.put(PlaylistTable.Cols.LAST_UPDATE, p.getLastUpdate());
        return cv;
    }

    public ContentValues createContentValuesFromSong(Playlist p, Song s) {
        ContentValues cv = new ContentValues();
        cv.put(SongTable.Cols.ID, s.getRealID());
        cv.put(SongTable.Cols.NAME, s.getName());
        cv.put(SongTable.Cols.SINGER, s.getSinger());
        cv.put(SongTable.Cols.POSITION, s.getPos());
        cv.put(SongTable.Cols.LENGTH, s.getLength());
        cv.put(SongTable.Cols.SONG_URL, s.getSongURL());
        cv.put(SongTable.Cols.IMG_URL, s.getAlbumURL());
        cv.put(SongTable.Cols.PLAYLIST_ID, p.getID());
        cv.put(SongTable.Cols.SAVED, s.getIsSaved());
        return cv;
    }

    public ContentValues createContentValuesForSavedSong(Song s) {
        ContentValues cv = new ContentValues();
        cv.put(SavedSongTable.Cols.SONG_ID, s.getRealID());
        cv.put(SavedSongTable.Cols.PATH, s.getSongURL());
        return cv;
    }

    public void fillDB(MusicServerResponse musicServerResponse) {
        new FillDBBackground().execute(musicServerResponse);
    }

    private class FillDBBackground extends AsyncTask<MusicServerResponse, Void, Boolean> {

        @Override
        protected Boolean doInBackground(MusicServerResponse... musicServerResponses) {
            Log.d(LOG_TAG, "FillDBBackground.doInBackground");
            ContentValues cv;
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            try {
                for (Playlist p : musicServerResponses[0].getPlaylists()) {
                    cv = createContentValuesFromPlaylist(p);
                    db.insert(PlaylistTable.TABLE_NAME, null, cv);
                    for (Song s : p.getSongs()) {
                        cv = createContentValuesFromSong(p, s);
                        db.insert(SongTable.TABLE_NAME, null, cv);
                    }
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
        }
    }

    public void updatePlaylists(MusicServerResponse m) {
        new UpdateDBBackground().execute(m);
    }

    private class UpdateDBBackground extends AsyncTask<MusicServerResponse, Void, Boolean> {

        @Override
        protected Boolean doInBackground(MusicServerResponse... musicServerResponses) {
            Log.d(LOG_TAG, "FillDBBackground.doInBackground");
            ContentValues cv;
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            Cursor c = null;
            try {
                List<Integer> idsList = new ArrayList<>();
                c = db.query(SavedSongTable.TABLE_NAME, new String[]{ SavedSongTable.Cols.SONG_ID }, null, null, null, null, null);
                if (c.moveToFirst()) {
                    do {
                        // Зачем этот код???????????????????????????????????????????????????????????????????????????????????
                        idsList.add(c.getInt(c.getColumnIndex(SavedSongTable.Cols.SONG_ID)));
                    } while (c.moveToNext());
                }

                //String selection = PlaylistTable.Cols.ID + " = ?";
                for (Playlist p : musicServerResponses[0].getPlaylists()) {
                    //c = db.query(PlaylistTable.TABLE_NAME, null, selection, new String[]{String.valueOf(p.getID())}, null, null, null);

                    //c.moveToFirst();
                    //long pID = c.getLong(c.getColumnIndex(PlaylistTable.Cols.ID));

                    cv = createContentValuesFromPlaylist(p);
                    db.update(PlaylistTable.TABLE_NAME, cv, "id = ?", new String[]{String.valueOf(p.getID())});

                    db.delete(SongTable.TABLE_NAME, SongTable.Cols.PLAYLIST_ID + " = " + String.valueOf(p.getID()), null);
                    for (Song s : p.getSongs()) {
                        cv = createContentValuesFromSong(p, s);
                        db.insert(SongTable.TABLE_NAME, null, cv);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.d(LOG_TAG, "UpdateDBBackground.onPostExecute");
        }
    }

    public List<Integer> getIndexsOfDifferentPlaylists(MusicServerResponse m) {
        List<Integer> result = new ArrayList<>();
        m.moveToFirst();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        Cursor c = db.query(PlaylistTable.TABLE_NAME, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int lastUpdateColIndex = c.getColumnIndex(PlaylistTable.Cols.LAST_UPDATE);
            do {
                String dbStr = c.getString(lastUpdateColIndex);
                String reqStr = m.getCurrentPlaylist().getLastUpdate();
                if (!dbStr.equals(reqStr)) {
                    result.add(m.getIndex());
                }
                m.nextPlaylist();
            } while (c.moveToNext());
        }
        c.close();
        return result;
    }

//    public List<Playlist> getPlayListsNames() {
//        List<Playlist> playListsNamesAndIds = new ArrayList<>();
//        SQLiteDatabase db = sDBHelper.getWritableDatabase();
//        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, DBHelper.PLAYLIST_POSITION_FIELD);
//        if (c.moveToFirst()) {
//            int nameIndex = c.getColumnIndex(DBHelper.PLAYLIST_TITLE_FIELD);
//            int idIndex = c.getColumnIndex("id");
//            do {
//                playListsNamesAndIds.add(new Playlist(c.getInt(idIndex), c.getString(nameIndex)));
//            } while (c.moveToNext());
//        }
//        return playListsNamesAndIds;
//    }

    // А вто зачем это? можно же использовать запрос getPlaylists() !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public List<Playlist> getPlayListsWithNameAndSchool() {
        List<Playlist> playLists = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        MusicCursorWrapper c = new MusicCursorWrapper(db.query(PlaylistTable.TABLE_NAME, null, null, null, null, null, PlaylistTable.Cols.POSITION));
        if (c.moveToFirst()) {
            do {
                playLists.add(c.getPlaylist());
            } while (c.moveToNext());
        }
        c.close();
        return playLists;
    }

//    public List<Song> getSongsByPlayListId(int playListId) {
//        List<Song> songs = new ArrayList<>();
//        SQLiteDatabase db = sDBHelper.getWritableDatabase();
//        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
//        String[] selectionArgs = new String[]{String.valueOf(playListId)};
//        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
//        logCursor(c);
//        if (c.moveToFirst()) {
//            int idIndex = c.getColumnIndex("id");
//            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
//            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
//            do {
//                songs.add(new Song(
//                        c.getInt(idIndex),
//                        c.getString(nameIndex),
//                        c.getInt(lengthIndex)
//                ));
//            } while (c.moveToNext());
//        }
//        return songs;
//    }

    public Song getSongById(int id) {
        Song song = null;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String selection = SongTable.Cols.ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(id) };
        MusicCursorWrapper c = new MusicCursorWrapper(db.query(SongTable.TABLE_NAME, null, selection, selectionArgs, null, null, null));
        if (c.moveToFirst()) {
            int realId = c.getInt(c.getColumnIndex(SongTable.Cols.ID));
            int isSaved = c.getInt(c.getColumnIndex(SongTable.Cols.SAVED));

            String selection2 = SavedSongTable.Cols.SONG_ID + " = ?";
            String[] selectionArgs2 = new String[]{ String.valueOf(realId) };
            Cursor c2 = db.query(SavedSongTable.TABLE_NAME, null, selection2, selectionArgs2, null, null, null);

            if (c2.moveToFirst() && (isSaved == 1)) {
                song = c.getSong(c2.getString(c2.getColumnIndex(SavedSongTable.Cols.PATH)));
            } else {
                song = c.getSong();
            }
            c.close();
            c2.close();
        }
        return song;
    }

//    public static Song getFirstSongByPlayListId(int playListId) {
//        Song song = null;
//        SQLiteDatabase db = sDBHelper.getWritableDatabase();
//        String selection = DBHelper.SONG_CONNECT_TO_PLAYLIST_FIELD + " = ?";
//        String[] selectionArgs = new String[]{String.valueOf(playListId)};
//        Cursor c = db.query(DBHelper.SONG_TABLE_NAME, null, selection, selectionArgs, null, null, DBHelper.SONG_POSITION_FIELD);
//        logCursor(c);
//        if (c.moveToFirst()) {
//            int idIndex = c.getColumnIndex("id");
//            int realIdIndex = c.getColumnIndex(DBHelper.SONG_ID_FIELD);
//            int posIndex = c.getColumnIndex(DBHelper.SONG_POSITION_FIELD);
//            int nameIndex = c.getColumnIndex(DBHelper.SONG_TITLE_FIELD);
//            int singerIndex = c.getColumnIndex(DBHelper.SONG_SINGER_FIELD);
//            int lengthIndex = c.getColumnIndex(DBHelper.SONG_LENGTH_FIELD);
//            int songUrlIndex = c.getColumnIndex(DBHelper.SONG_SONG_URL_FIELD);
//            int albumUrlIndex = c.getColumnIndex(DBHelper.SONG_ALBUM_URL_FIELD);
//            int isSavedIndex = c.getColumnIndex(DBHelper.SONG_IS_SAVED);
//            song = new Song(
//                    c.getInt(idIndex),
//                    c.getInt(realIdIndex),
//                    c.getString(nameIndex),
//                    c.getString(singerIndex),
//                    c.getString(lengthIndex),
//                    c.getInt(posIndex),
//                    c.getString(songUrlIndex),
//                    c.getString(albumUrlIndex),
//                    c.getInt(isSavedIndex),
//                    Song.INT_FLAG
//            );
//        }
//        return song;
//    }

    public ArrayList<Integer> getSongsIdsByPlaylistID(int playListId) {
        ArrayList<Integer> list = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        String selection = SongTable.Cols.PLAYLIST_ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(playListId) };
        Cursor c = db.query(SongTable.TABLE_NAME, new String[]{ SongTable.Cols.ID }, selection, selectionArgs, null, null, SongTable.Cols.POSITION);
        if (c.moveToFirst()) {
            do {
                list.add(c.getInt(c.getColumnIndex(SongTable.Cols.ID)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Song> getSongsByPlaylist(Playlist p) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selection = SongTable.Cols.PLAYLIST_ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(p.getID()) };

        MusicCursorWrapper c = new MusicCursorWrapper(db.query(SongTable.TABLE_NAME, null, selection, selectionArgs, null, null, SongTable.Cols.POSITION));

        if (c.moveToFirst()) {
            do {
                songs.add(c.getSong());
            } while (c.moveToNext());
        }
        c.close();
        return songs;
    }

    public Playlist getPlaylistBySongID(int songId) {
        Playlist playList = null;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selection = SongTable.Cols.ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(songId) };

        MusicCursorWrapper c = new MusicCursorWrapper(db.query(SongTable.TABLE_NAME, new String[]{ SongTable.Cols.PLAYLIST_ID }, selection, selectionArgs, null, null, null));

        if (c.moveToFirst()) {
            selection = PlaylistTable.Cols.ID + " = ?";
            selectionArgs = new String[]{ c.getString(c.getColumnIndex(SongTable.Cols.PLAYLIST_ID)) };
            c = new MusicCursorWrapper(db.query(PlaylistTable.TABLE_NAME, null, selection, selectionArgs, null, null, null));
            if (c.moveToFirst()) {
                playList = c.getPlaylist();
            }
        }
        c.close();
        return playList;
    }

    public Playlist getPlaylistByID(int playlistID) {
        Playlist playList = null;
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        String selection = PlaylistTable.Cols.ID + " = ?";
        String[] selectionArgs = new String[]{ String.valueOf(playlistID) };

        MusicCursorWrapper c = new MusicCursorWrapper(db.query(PlaylistTable.TABLE_NAME, null, selection, selectionArgs, null, null, null));
        if (c.moveToFirst()) {
            playList = c.getPlaylist();
        }
        c.close();
        return playList;
    }

    public Playlist getFullPlaylistByID(int playlistID) {
        Playlist playlist = getPlaylistByID(playlistID);
        playlist.setSongs(getSongsByPlaylist(playlist));

        String idsStr = "(";
        for (Song s : playlist.getSongs()) {
            if (s.getIsSaved()) {
                idsStr += "\"" + String.valueOf(s.getRealID()) + "\",";
            }
        }
        idsStr += "\"-1\")";

        String sqlQuery = "select "
                + "SS." + SavedSongTable.Cols.SONG_ID
                + ", SS." + SavedSongTable.Cols.PATH
                + " from " + SavedSongTable.TABLE_NAME + " as SS"
                + " where " + SavedSongTable.Cols.SONG_ID + " in " + idsStr;

        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        Cursor c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex(SavedSongTable.Cols.SONG_ID));
                for (Song s : playlist.getSongs()) {
                    if (s.getRealID() == id) {
                        s.setSongURL(c.getString(c.getColumnIndex(SavedSongTable.Cols.PATH)));
                        break;
                    }
                }
            } while (c.moveToNext());
        }
        c.close();
        return playlist;
    }

    public void setSaveSongBySong(Song song) {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        ContentValues cv = createContentValuesForSavedSong(song);
        db.insert(SavedSongTable.TABLE_NAME, null, cv);
        cv.clear();

        cv.put(SongTable.Cols.SAVED, 1);
        db.update(SongTable.TABLE_NAME, cv, SongTable.Cols.ID + " = ?", new String[]{ String.valueOf(song.getRealID()) });
    }

    public void setUnSaveSongBySong(Song song) {
        setUnSaveSongBySong(mDBHelper.getWritableDatabase(), song);
    }

    public void setUnSaveSongBySong(SQLiteDatabase db, Song song) {
        ContentValues cv = new ContentValues();

        cv.put(SongTable.Cols.SAVED, 0);
        db.update(SongTable.TABLE_NAME, cv, SongTable.Cols.ID + " = ?", new String[]{ String.valueOf(song.getRealID()) });
        cv.clear();

        db.delete(SavedSongTable.TABLE_NAME, SavedSongTable.Cols.SONG_ID + " = ?", new String[]{ String.valueOf(song.getRealID()) });
    }

//    public void setUnSaveSongBySong(SQLiteDatabase db, Song song) {
//        ContentValues cv = new ContentValues();

//        cv.put(DBHelper.SONG_IS_SAVED, 0);
//        db.update(SongTable.TABLE_NAME, cv, SongTable.Cols.ID + " = ?", new String[]{ String.valueOf(song.getID()) });
//        cv.clear();
//
//        db.delete(DBHelper.SAVED_SONG_TABLE_NAME, DBHelper.SAVED_SONG_ID_FIELD + " = " + String.valueOf(song.getRealID()), null);
//    }

    public List<Song> getAllSavedSongs() {
        return getAllSavedSongs(mDBHelper.getWritableDatabase());
    }

    public List<Song> getAllSavedSongs(SQLiteDatabase db) {
        List<Song> songs = new ArrayList<>();

        String sqlQuery = "select "
                + " SONG." + SongTable.Cols._ID
                + ", SONG." + SongTable.Cols.ID
                + ", SONG." + SongTable.Cols.NAME
                + ", SONG." + SongTable.Cols.SINGER
                + ", SONG." + SongTable.Cols.POSITION
                + ", SONG." + SongTable.Cols.LENGTH
                + ", SONG." + SongTable.Cols.IMG_URL
                + ", SONG." + SongTable.Cols.SAVED
                + ", SAVED_SONG." + SavedSongTable.Cols.PATH
                + " from " + SongTable.TABLE_NAME + " as SONG "
                + "inner join " + SavedSongTable.TABLE_NAME + " as SAVED_SONG "
                + "on SONG." + SongTable.Cols.ID + " = SAVED_SONG." + SavedSongTable.Cols.SONG_ID + " ";

        MusicCursorWrapper c = null;
        try {
            c = new MusicCursorWrapper(db.rawQuery(sqlQuery, null));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    songs.add(c.getSong(c.getString(c.getColumnIndex(SavedSongTable.Cols.PATH))));
                } while (c.moveToNext());
            }
            c.close();
        }
        return songs;
    }

    public void deleteDataFromSavedTable(SQLiteDatabase db) {
        try {
            db.delete(SavedSongTable.TABLE_NAME, null, null);

            ContentValues cv = new ContentValues();
            cv.put(SongTable.Cols.SAVED, "0");

            db.update(SongTable.TABLE_NAME, cv, SongTable.Cols.SAVED + " = ?", new String[]{ "1" });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Integer> getSavedSongsIds() {
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        ArrayList<Integer> ids = new ArrayList<>();

        String sqlQuery = "select "
                + "SONG." + SongTable.Cols.ID + " "
                + "from " + SavedSongTable.TABLE_NAME + " as SAVED_SONG "
                + "inner join " + SongTable.TABLE_NAME + " as SONG "
                + "on SONG." + SongTable.Cols.ID + " = SAVED_SONG." + SavedSongTable.Cols.SONG_ID + " ";

        Cursor c = db.rawQuery(sqlQuery, null);
        if (c.moveToFirst()) {
            do {
                ids.add(c.getInt(c.getColumnIndex(SongTable.Cols.ID)));
            } while (c.moveToNext());
        }
        c.close();
        return ids;
    }

    public List<Song> getSongsBySongsIds(List<Integer> ids) {
        List<Song> songs = new ArrayList<>();
        SQLiteDatabase db = mDBHelper.getWritableDatabase();

        // Этот вариант работает быстрее, но порядок песен получается как в БД
        String idsStr = "(";
        for (Integer id : ids) {
            idsStr += "\"" + String.valueOf(id) + "\",";
        }
        idsStr += "\"-1\")";

        String sqlQuery = "select "
                + "SONG." + SongTable.Cols._ID
                + ", SONG." + SongTable.Cols.ID
                + ", SONG." + SongTable.Cols.NAME
                + ", SONG." + SongTable.Cols.SINGER
                + ", SONG." + SongTable.Cols.LENGTH
                + ", SONG." + SongTable.Cols.POSITION
                + ", SONG." + SongTable.Cols.SONG_URL
                + ", SONG." + SongTable.Cols.IMG_URL
                + ", SONG." + SongTable.Cols.SAVED
                + " from " + SongTable.TABLE_NAME + " as SONG "
                + "where " + SongTable.Cols.ID + " in " + idsStr;

        MusicCursorWrapper c = new MusicCursorWrapper(db.rawQuery(sqlQuery, null));

        if (c.moveToFirst()) {
            do {
                songs.add(c.getSong());
            } while (c.moveToNext());
        }

        // Алгоритм изменения порядка
        for (int i = 0; i < ids.size(); ++i) {
            int id = ids.get(i);
            for (int j = i; j < songs.size(); ++j) {
                if (songs.get(j).getID() == id) {
                    songs.add(i, songs.remove(j));
                }
            }
        }

        c.close();
        return songs;
    }

//    public void writeToLogDataFromTables() {
//        writeDataFromTableToLog(DBHelper.SAVED_SONG_TABLE_NAME);
//    }
//
//    private void writeDataFromTableToLog(String tableName) {
//        if (isDebug) {
//            SQLiteDatabase db = sDBHelper.getWritableDatabase();
//            Cursor c = db.query(tableName, null, null, null, null, null, null);
//            Log.d(LOG_TAG, "Table name = " + tableName);
//            logCursor(c);
//        }
//    }

//    private static void logCursor(Cursor c) {
//        if (isDebug) {
//            if (c != null) {
//                if (c.moveToFirst()) {
//                    String str;
//                    do {
//                        str = "";
//                        for (String cn : c.getColumnNames()) {
//                            str = str.concat(cn + " = " + c.getString(c.getColumnIndex(cn)) + "; ");
//                        }
//                        Log.d(LOG_TAG, str);
//                    } while (c.moveToNext());
//                }
//            } else {
//                Log.d(LOG_TAG, "Cursor is null");
//            }
//        }
//    }

}
