package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wiretech.df.dfmusic.DataBase.DBHelper;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

public class SplashActivity extends AppCompatActivity {

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

        splashTimer.start();

        DBManager.with(this);

        boolean h = DBManager.hasPlaylistsInDatabase();
        isEndDownload = h;
        isDBEmpty = h;

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

    private void startMainActivity() {

        // выполнение этого кода происходит после провеки условий
        startActivity(new Intent(SplashActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
                finish();
            }
        }
    };

}
