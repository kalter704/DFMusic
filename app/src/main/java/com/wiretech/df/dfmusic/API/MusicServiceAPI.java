package com.wiretech.df.dfmusic.API;

import android.os.AsyncTask;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponsePlaylistsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicServiceAPI {

    private static final boolean isDEBUG = true;
    private static final String LOG_TAG = "MusicServerApi";

    private static final String SERVER_DOMAIN = "http://6d22bdb6.ngrok.io";
    private static final String SERVER_URL = SERVER_DOMAIN + "/musicapi/";

    private static final int PLAYLIST_ACTION_ID = 1;

    private static final BackgroundRequest mBackgroundRequest = new BackgroundRequest();

    private static final List<OnResponsePlaylistsListener> playlistsListners = new ArrayList<>();

    private static int CURRENT_ACTION = 0;

    public static void requestPlaylists() {
        CURRENT_ACTION = PLAYLIST_ACTION_ID;
        mBackgroundRequest.execute(SERVER_URL + "getplaylists/");
    }

    private static class BackgroundRequest extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            //HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                /*
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = connection.getInputStream();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException(connection.getResponseMessage() + ": with " + strings[0]);
                }

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                return Arrays.toString(out.toByteArray()) + strings[1];
                */

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
            if (CURRENT_ACTION == PLAYLIST_ACTION_ID) {
                parceJSONToPlaylistsArray(s);
            }
        }
    }

    private static void parceJSONToPlaylistsArray(String json) {
        Log.d(LOG_TAG, "parceJSONToPlaylistsArray");
        List<PlayList> playLists = new ArrayList<>();
        JSONObject resultJson = null;
        try {
            resultJson = new JSONObject(json);
            JSONArray playlistsJson = resultJson.getJSONObject("response").getJSONArray("playlists");
            for (int i = 0; i < playlistsJson.length(); ++i) {
                JSONObject playlistJson = playlistsJson.getJSONObject(i);
                playLists.add(new PlayList(
                        playlistJson.getInt("id"),
                        playlistJson.getString("title"),
                        playlistJson.getString("school_owner"),
                        (long) playlistJson.getInt("last_update"),
                        playlistJson.getInt("pos")
                ));
            }
            notifPlaylists(playLists);
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public static void setOnReponsePlaylistsListener(OnResponsePlaylistsListener newListener) {
        playlistsListners.add(newListener);
    }

    public static void unsetOnReponsePlaylistsListener(OnResponsePlaylistsListener delListener) {
        playlistsListners.remove(delListener);
    }

    private static void notifPlaylists(List<PlayList> playLists) {
        Log.d(LOG_TAG, "notifPlaylists");
        for (OnResponsePlaylistsListener playlistsListner: playlistsListners) {
            playlistsListner.onResponse(playLists);
        }
    }

    private static void writeToLog(String string) {
        if (isDEBUG) {
            Log.d(LOG_TAG, string);
        }
    }

}
