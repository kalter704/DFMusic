package com.wiretech.df.dfmusic.Activityes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.API.Interfaces.OnResponsePlaylistsListener;
import com.wiretech.df.dfmusic.API.MusicServiceAPI;
import com.wiretech.df.dfmusic.Classes.NetworkConnection;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

import java.util.List;

public class SplashActivity extends AppCompatActivity implements OnResponsePlaylistsListener {

    private boolean isEndTime = false;
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
        MusicServiceAPI.setOnReponsePlaylistsListener(this);

        splashTimer.start();

        boolean h = DBManager.hasPlaylistsInDatabase();
        //isEndDownload = h;
        isDBEmpty = !h;

        if (isDBEmpty && NetworkConnection.hasConnectionToNetwork()) {
            // cкачивание данных
            downloadDatas();
        } else {
            // диалог об отсутствии интренета
            showErrorDialog();
        }

    }

    private void downloadDatas() {
        // !!!!!!!!!!!!!!!!!!!
        MusicServiceAPI.requestPlaylists();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppHasFocus = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppHasFocus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicServiceAPI.unsetOnReponsePlaylistsListener(this);
    }

    private void startMainActivity() {

        // выполнение этого кода происходит после провеки условий
        if (isEndDownload) {
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
    public void onResponse(List<PlayList> playLists) {
        Log.d("SplashActivity", "onResponse");
        //  Добавить результат в базу
        isEndDownload = true;
        startMainActivity();
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
            finally {
                //finish();
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
