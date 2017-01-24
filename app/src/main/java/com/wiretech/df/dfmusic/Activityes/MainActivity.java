package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wiretech.df.dfmusic.Adapters.PlayListAdapter;
import com.wiretech.df.dfmusic.Classes.PlayList;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<PlayList> playLists = new ArrayList<>();
    PlayListAdapter playListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillDate();

        initializeUI();
    }

    private void fillDate(){
        for (int i = 0; i < 10; ++i) {
            playLists.add(new PlayList(i, "PlayList Name", "CRAZY DREAM" + String.valueOf(i)));
        }
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        playListAdapter = new PlayListAdapter(this, playLists);

        ((ListView) findViewById(R.id.lvMain)).setAdapter(playListAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });
    }
}
