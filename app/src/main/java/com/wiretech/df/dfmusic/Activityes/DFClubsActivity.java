package com.wiretech.df.dfmusic.Activityes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;

public class DFClubsActivity extends AppCompatActivity {

    ArrayList<String> mClubs = new ArrayList<String>() {{
        add("BLACK VELVET");
        add("CRAZY DREAM");
        add("FLASH LIGHT");
        add("FREE STEPS");
        add("JUST DANCE");
        add("LUCKY JAM");
        add("MY COMMUNITY");
        add("UNISTREAM");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfclubs);
    }
}
