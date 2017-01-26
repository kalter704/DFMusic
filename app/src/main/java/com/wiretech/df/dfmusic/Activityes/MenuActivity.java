package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wiretech.df.dfmusic.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.rlBack).setOnClickListener(this);
        findViewById(R.id.rlShare).setOnClickListener(this);
        findViewById(R.id.tlAbout).setOnClickListener(this);
        findViewById(R.id.rlSavedAudio).setOnClickListener(this);
        findViewById(R.id.rlRating).setOnClickListener(this);
        findViewById(R.id.rlDonate).setOnClickListener(this);
        findViewById(R.id.rlWrite).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.rlShare:
                finish();
                break;
            case R.id.tlAbout:
                startActivity(new Intent(MenuActivity.this, AboutActivity.class));
                break;
            case R.id.rlSavedAudio:

                break;
            case R.id.rlRating:

                break;
            case R.id.rlDonate:

                break;
            case R.id.rlWrite:

                break;
        }
    }
}
