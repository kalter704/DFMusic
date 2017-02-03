package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.wiretech.df.dfmusic.Adapters.PlayListsAdapter;
import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<PlayList> mPlayLists = new ArrayList<>();
    PlayListsAdapter mPlayListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillDate();

        initializeUI();
    }

    private void fillDate(){
        String[] clubs = getResources().getStringArray(R.array.clubs);
        for (int i = 0; i < clubs.length; ++i) {
            mPlayLists.add(new PlayList(i, "PlayList Name", clubs[i]));
        }
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayListAdapter = new PlayListsAdapter(this, mPlayLists);

        ((ListView) findViewById(R.id.lvMain)).setAdapter(mPlayListAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });
    }
}
