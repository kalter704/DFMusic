package com.wiretech.df.dfmusic.Activityes.menuactivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wiretech.df.dfmusic.R;

public class StudioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studio);

        findViewById(R.id.rlBack).setOnClickListener(v -> finish());
    }
}
