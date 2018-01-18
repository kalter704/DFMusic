package com.wiretech.df.dfmusic.activityes.menuactivities;

import android.content.*;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wiretech.df.dfmusic.classes.Share;
import com.wiretech.df.dfmusic.R;

public class StudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        findViewById(R.id.rlBack).setOnClickListener(v -> finish());

        findViewById(R.id.rlShare).setOnClickListener(v -> Share.share(StudioActivity.this, getString(R.string.text_for_share_studio) + "\n" + getString(R.string.link_to_app)));

        findViewById(R.id.tv_to_agreement).setOnClickListener(v -> startActivity(new Intent(StudioActivity.this, AgreementActivity.class)));
    }
}
