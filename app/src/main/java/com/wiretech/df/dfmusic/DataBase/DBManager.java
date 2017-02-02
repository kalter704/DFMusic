package com.wiretech.df.dfmusic.DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

    private static DBHelper sDBHelper;

    public static void with(Context context) {
        sDBHelper = new DBHelper(context);
    }

    public static boolean hasPlaylistsInDatabase() {
        SQLiteDatabase db = sDBHelper.getWritableDatabase();
        Cursor c = db.query(DBHelper.PLAYLIST_TABLE_NAME, null, null, null, null, null, null);
        return c.moveToFirst();
    }

}
