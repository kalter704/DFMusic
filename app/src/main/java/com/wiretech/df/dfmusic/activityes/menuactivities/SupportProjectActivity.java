package com.wiretech.df.dfmusic.activityes.menuactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.wiretech.df.dfmusic.classes.Share;
import com.wiretech.df.dfmusic.R;

public class SupportProjectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_project);

        findViewById(R.id.rlBack).setOnClickListener(v -> finish());

        findViewById(R.id.rlShare).setOnClickListener(v -> Share.share(SupportProjectActivity.this, getString(R.string.text_for_share_support) + "\n" + getString(R.string.link_to_app)));
    }
}
