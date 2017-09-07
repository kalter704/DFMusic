package com.wiretech.df.dfmusic.classes;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.wiretech.df.dfmusic.api.classes.Song;
import com.wiretech.df.dfmusic.database.DBManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MusicDownloadManager {
    private static final String LOG_TAG = "MusicDownloadManager";

    private static final String FOLDER_NAME = "DFMusic";

    private static final int SAVE_SONG = 0;
    private static final int DELETE_SONG = 1;

    class DoAction {
        public int id;
        public int action;
    }

    private Context mContext;
    private List<DoAction> mDoActions  = new ArrayList<>();

    private boolean isDo = false;

    public static MusicDownloadManager instance = new MusicDownloadManager();

    private MusicDownloadManager() {}

    public void with(Context context) {
        mContext = context;
        //mBackgroundDownload = new BackgroundDownload();
    }

    public void downloadSong(int songId) {
        addAction(songId, SAVE_SONG);
    }

    public void deleteSong(int songId) {
        addAction(songId, DELETE_SONG);
    }

    private void addAction(int songId, int action) {
        DoAction doAction = new DoAction();
        doAction.id = songId;
        doAction.action = action;
        boolean isContain = false;
        for (DoAction da : mDoActions) {
            if ((da.id == doAction.id) && (da.action == doAction.action)) {
                isContain = true;
                break;
            }
        }
        if (!isContain){
            mDoActions.add(doAction);
            if (!isDo) {
                new BackgroundDownload().execute();
            }
        }
    }

    class BackgroundDownload extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isDo = true;
        }

        @Override
        protected Void doInBackground(Void... params) {
            DBManager mDBManager = DBManager.get(mContext);
            while(mDoActions.size() > 0) {
                DoAction da = mDoActions.get(0);
                Song song = mDBManager.getSongById(da.id);
                if (da.action == SAVE_SONG) {
                    String path = mContext.getFilesDir().toString() + "/" + FOLDER_NAME;
                    File f1 = new File(path);
                    if (!f1.exists()) {
                        f1.mkdir();
                    }
                    path += "/" + song.getName() + String.valueOf(song.getRealID())+ ".mp3";
                    Log.d(LOG_TAG, "Path = " + path);

                    try {

                        MusicDownloadNotification notification = new MusicDownloadNotification(mContext, song.getName());

                        URL url = new URL(song.getFullSongURL());
                        URLConnection connection = url.openConnection();
                        connection.connect();

                        InputStream input = new BufferedInputStream(url.openStream());
                        OutputStream output = new FileOutputStream(path);

                        int totalSize = connection.getContentLength();
                        int downloadSize = 0;

                        byte data[] = new byte[1024];
                        int count;
                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                            downloadSize += count;
                            notification.notifyProgress((int)(((float)downloadSize)/totalSize * 100));
                        }

                        output.flush();
                        output.close();
                        input.close();

                        notification.endDownload(song.getName());

                        song.setSaved(true);
                        song.setSongURL(path);
                        mDBManager.setSaveSongBySong(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (da.action == DELETE_SONG) {
                    try {
                        //mContext.deleteFile(song.getSongURL());
                        File fd = new File(song.getSongURL());
                        fd.delete();
                        mDBManager.setUnSaveSongBySong(song);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                mDoActions.remove(0);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isDo = false;
        }
    }

    public void deleteAllSavedSongs(SQLiteDatabase db) {
        List<Song> songs = DBManager.get(mContext).getAllSavedSongs(db);
        for (Song song : songs) {
            File fd = new File(song.getSongURL());
            if (fd.delete()) {
                DBManager.get(mContext).setUnSaveSongBySong(db, song);
            }
        }
        DBManager.get(mContext).deleteDataFromSavedTable(db);
    }
}
