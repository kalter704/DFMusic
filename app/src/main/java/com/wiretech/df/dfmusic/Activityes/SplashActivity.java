package com.wiretech.df.dfmusic.Activityes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.MusicServerResponse;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponseAPIListener;
import com.wiretech.df.dfmusic.API.MusicServiceAPI;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.Classes.NetworkConnection;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements OnResponseAPIListener {

    private final String LOG_TAG = "SplashActivity";

    private boolean isEndTime = false;
    private boolean isEndTime2 = true;
    private boolean isAppHasFocus = false;
    private boolean isDBEmpty = true;
    private boolean isEndDownload = false;

    private int mSecForSplashActivity = 1;
    private int mIterationTime = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        DBManager.with(this);
        NetworkConnection.with(this);
        MusicServiceAPI.with(this);
        MusicServiceAPI.setOnResponseAPIListener(this);

        AdControl.newAdControlInstance();
        AdControl.getInstance().setContext(this);
        AdControl.getInstance().enableAds();

        splashTimer.start();

        boolean h = DBManager.hasPlaylistsInDatabase();
        //isEndDownload = h;
        isDBEmpty = !h;

        Log.d(LOG_TAG, "isDBEmpty = " + isDBEmpty);

        if (isDBEmpty) {
            if (NetworkConnection.hasConnectionToNetwork()) {
                // cкачивание данных
                downloadDatas();
            } else {
                // диалог об отсутствии интренета
                showErrorDialog();
            }
        } else {
            if (NetworkConnection.hasConnectionToNetwork()) {
                MusicServiceAPI.requestPlaylists();
            } else {
                isEndDownload = true;
                startMainActivity();
            }
        }

    }

    private void downloadDatas() {
        MusicServiceAPI.requestPlaylistsAndSongs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppHasFocus = true;
        startMainActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppHasFocus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicServiceAPI.unsetOnResponseAPIListener(this);
    }

    private void startMainActivity() {
        if (isEndDownload && isEndTime && isAppHasFocus && isEndTime2) {
            try {
                startActivity(new Intent(SplashActivity.this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                finish();
            }
        }
    }

    @Override
    public void onResponse(int action, MusicServerResponse musicServerResponse) {
        Log.d("SplashActivity", "onResponse");
        if (action == MusicServiceAPI.ALL_DATAS) {
            DBManager.fillDB(musicServerResponse);
            isEndTime2 = false;
            isEndDownload = true;
            splashTimer2.start();
        } else if (action == MusicServiceAPI.ONLY_PLAYLISTS) {
            // Сравнить данные по плейлистам с данными БД!!!!!!!!!!!!!!
            List<Integer> diff = DBManager.getIndexsOfDifferentPlaylists(musicServerResponse);
            if (diff.size() > 0) {
                MusicServerResponse m = new MusicServerResponse();
                for (int i = 0; i < diff.size(); ++i) {
                    m.addPlaylist(musicServerResponse.getPlaylistByIndex(diff.get(i)));
                }
                MusicServiceAPI.requestForUpdatePlaylists(m);
            } else {
                isEndDownload = true;
                startMainActivity();
            }
        } else if (action == MusicServiceAPI.UPDATE_PLAYLISTS) {
            DBManager.updatePlaylists(musicServerResponse);
            isEndDownload = true;
            startMainActivity();
        }
    }

    @Override
    public void onError(int code) {
        Log.d("SplashActivity", "Error");
        if (code == MusicServiceAPI.ERROR_NOT_RESPONSE) {
            isEndDownload = true;
            startMainActivity();
        }
    }

    Thread splashTimer = new Thread() {
        public void run() {
            try {
                int splashTimer = 0;
                while(splashTimer < (mSecForSplashActivity * 1000)) {
                    sleep(mIterationTime);
                    splashTimer += mIterationTime;
                }
                isEndTime = true;
                startMainActivity();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    Thread splashTimer2 = new Thread() {
        public void run() {
            try {
                int splashTimer = 0;
                while(splashTimer < (3 * mSecForSplashActivity * 1000)) {
                    sleep(mIterationTime);
                    splashTimer += mIterationTime;
                }
                isEndTime2 = true;
                startMainActivity();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Загрузка!")
                .setMessage("Для первичной загрузки нужен интернет!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setNegativeButton("ОК", myClickListener);
        AlertDialog alert = builder.create();

        alert.show();

        alert.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#D4D4D4"));
        //alert.getButton(DialogInterface.BUTTON_NEGATIVE).setPadding(10, 0, 10, 0);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (NetworkConnection.hasConnectionToNetwork()) {
                downloadDatas();
            } else {
                showErrorDialog();
            }
        }
    };

}
