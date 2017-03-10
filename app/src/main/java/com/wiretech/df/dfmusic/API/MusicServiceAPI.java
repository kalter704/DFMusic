package com.wiretech.df.dfmusic.API;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.MusicServerResponse;
import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.API.Classes.PlayListResponse;
import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponseAPIListener;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponsePlaylistsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicServiceAPI {

    private static final boolean isDEBUG = true;
    private static final String LOG_TAG = "MusicServerApi";

    public static final String SERVER_DOMAIN = "http://192.168.1.5/";
    public static final String SERVER_URL = SERVER_DOMAIN + "musicapi/";

    private static final int PLAYLIST_ACTION_ID = 1;
    private static final int SONG_ACTION_ID = 2;

    public static final int ALL_DATAS = 1;
    public static final int ONLY_PLAYLISTS = 2;
    public static final int UPDATE_PLAYLISTS = 3;

    private static Context sContext;

    private static String screen = null;

    private static MusicServerResponse sMusicServerResponse;

    private static List<OnResponseAPIListener> listeners = new ArrayList<>();

    private static int currentAction = 0;
    private static int globalAction = 0;

    public static void with(Context context) {
        sContext = context;
    }

    public static void requestPlaylistsAndSongs() {
        sMusicServerResponse = new MusicServerResponse();
        currentAction = PLAYLIST_ACTION_ID;
        globalAction = ALL_DATAS;
        new BackgroundRequest().execute(SERVER_URL + "getplaylists/");
    }

    public static void requestPlaylists() {
        sMusicServerResponse = new MusicServerResponse();
        currentAction = PLAYLIST_ACTION_ID;
        globalAction = ONLY_PLAYLISTS;
        new BackgroundRequest().execute(SERVER_URL + "getplaylists/");
    }

    public static void requestForUpdatePlaylists(MusicServerResponse m) {
        sMusicServerResponse = m;
        sMusicServerResponse.moveToFirst();
        globalAction = UPDATE_PLAYLISTS;
        requestSongs();
    }

    public static void requestSongs() {
        currentAction = SONG_ACTION_ID;
        if (screen == null) {
            getScreen();
        }
        if (sMusicServerResponse.getCurrentPlaylist() != null) {
            int id = sMusicServerResponse.getCurrentPlaylist().getId();
            String url = SERVER_URL + "getsongs/?"
                    + "screen=" + screen
                    + "&playlist_id=" + String.valueOf(id);
            new BackgroundRequest().execute(url);
        } else {
            notifMusicServerResponse();
        }
    }

    private static class BackgroundRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setReadTimeout(10000);
                connection.connect();
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder buf = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buf.append(line);
                }
                connection.disconnect();
                return (buf.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(LOG_TAG, "onPostExecute");
            writeToLog(s);
            nextAction(s);
        }
    }

    private static void nextAction(String s) {
        if (currentAction == PLAYLIST_ACTION_ID) {
            new parsePlayListsJsonBackground().execute(s);
        } else if (currentAction == SONG_ACTION_ID) {
            new parseSongsJsonBackground().execute(s);
        }
    }

    /*
    private static void parcePlaylistsJson(String json) {
        Log.d(LOG_TAG, "parceJSONToPlaylistsArray");
        JSONObject resultJson = null;
        try {
            resultJson = new JSONObject(json);
            sMusicServerResponse.setCount(resultJson.getJSONObject("response").getInt("count"));
            JSONArray playlistsJson = resultJson.getJSONObject("response").getJSONArray("playlists");
            for (int i = 0; i < playlistsJson.length(); ++i) {
                JSONObject playlistJson = playlistsJson.getJSONObject(i);
                sMusicServerResponse.addPlaylist(new PlayList(
                        playlistJson.getInt("id"),
                        playlistJson.getString("title"),
                        playlistJson.getString("school_owner"),
                        playlistJson.getString("last_update"),
                        playlistJson.getInt("pos")
                ));
            }
            requestSongs();
        } catch (JSONException e) {
            norifError();
            e.printStackTrace();
        }
    }
    */

    private static class parsePlayListsJsonBackground extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(LOG_TAG, "parceJSONToPlaylistsArray");
            JSONObject resultJson = null;
            try {
                resultJson = new JSONObject(strings[0]);
                sMusicServerResponse.setCount(resultJson.getJSONObject("response").getInt("count"));
                JSONArray playlistsJson = resultJson.getJSONObject("response").getJSONArray("playlists");
                for (int i = 0; i < playlistsJson.length(); ++i) {
                    JSONObject playlistJson = playlistsJson.getJSONObject(i);
                    sMusicServerResponse.addPlaylist(new PlayList(
                            playlistJson.getInt("id"),
                            playlistJson.getString("title"),
                            playlistJson.getString("school_owner"),
                            playlistJson.getString("last_update"),
                            playlistJson.getInt("pos")
                    ));
                }
                return "OK";
            } catch (JSONException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(LOG_TAG, "onPostExecute");
            if (s.equals("OK")) {
                if (globalAction == ALL_DATAS) {
                    requestSongs();
                } else {
                    notifMusicServerResponse();
                }
            } else {
                notifError();
            }
        }
    }

    /*
    private static void parceSongsJson(String json) {
        Log.d(LOG_TAG, "parceJSONToPlaylistsArray");
        JSONObject resultJson = null;
        try {
            resultJson = new JSONObject(json);
            sMusicServerResponse.getCurrentPlaylist().setNumbersOfSongs(resultJson.getJSONObject("response").getInt("count"));
            JSONArray songsJson = resultJson.getJSONObject("response").getJSONArray("songs");
            for (int i = 0; i < songsJson.length(); ++i) {
                JSONObject songJson = songsJson.getJSONObject(i);
                sMusicServerResponse.getCurrentPlaylist().addSong(new Song(
                        songJson.getInt("id"),
                        songJson.getString("title"),
                        songJson.getString("length"),
                        songJson.getInt("pos"),
                        songJson.getString("song_url"),
                        songJson.getString("img_url")
                ));
            }
            requestSongs();
        } catch (JSONException e) {
            //norifError();
            e.printStackTrace();
        }
    }
    */

    private static class parseSongsJsonBackground extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            JSONObject resultJson = null;
            try {
                resultJson = new JSONObject(strings[0]);
                sMusicServerResponse.getCurrentPlaylist().setNumbersOfSongs(resultJson.getJSONObject("response").getInt("count"));
                JSONArray songsJson = resultJson.getJSONObject("response").getJSONArray("songs");
                for (int i = 0; i < songsJson.length(); ++i) {
                    JSONObject songJson = songsJson.getJSONObject(i);
                    sMusicServerResponse.getCurrentPlaylist().addSong(new Song(
                            songJson.getInt("id"),
                            songJson.getString("title"),
                            songJson.getString("singer"),
                            songJson.getString("length"),
                            songJson.getInt("pos"),
                            songJson.getString("song_url"),
                            songJson.getString("img_url"),
                            Song.STRING_FLAG
                    ));
                }
                return "OK";
            } catch (JSONException e) {
                //norifError();
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(LOG_TAG, "onPostExecute");
            if (s.equals("OK")) {
                sMusicServerResponse.nextPlaylist();
                requestSongs();
            } else {
                notifError();
            }
        }
    }


    public static void setOnResponseAPIListener(OnResponseAPIListener newListener) {
        listeners.add(newListener);
    }

    public static void unsetOnResponseAPIListener(OnResponseAPIListener delListener) {
        listeners.remove(delListener);
    }


    private static void notifMusicServerResponse() {
        Log.d(LOG_TAG, "notifMusicServerResponse");
        for (OnResponseAPIListener l: listeners) {
            l.onResponse(globalAction, sMusicServerResponse);
        }
    }

    private static void notifError() {
        Log.d(LOG_TAG, "notifError");
        for (OnResponseAPIListener l: listeners) {
            l.onError();
        }
    }


    private static void writeToLog(String string) {
        if (isDEBUG) {
            Log.d(LOG_TAG, string);
        }
    }

    private static void getScreen() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) sContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch(metrics.densityDpi){
            case DisplayMetrics.DENSITY_MEDIUM:
                screen = "mdpi";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                screen = "hdpi";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                screen = "xhdpi";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                screen = "xxhdpi";
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                screen = "xxhdpi";
                break;
            default:
                screen = "mdpi";
        }
    }

}
