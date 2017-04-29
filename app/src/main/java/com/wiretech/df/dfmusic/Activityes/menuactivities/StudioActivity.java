package com.wiretech.df.dfmusic.Activityes.menuactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wiretech.df.dfmusic.Classes.Share;
import com.wiretech.df.dfmusic.R;

public class StudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        findViewById(R.id.rlBack).setOnClickListener(v -> finish());

        findViewById(R.id.rlShare).setOnClickListener(v -> Share.share(StudioActivity.this, getString(R.string.text_for_share_studio)));
    }
}
