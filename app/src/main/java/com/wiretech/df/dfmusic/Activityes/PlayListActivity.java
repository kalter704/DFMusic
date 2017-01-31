package com.wiretech.df.dfmusic.Activityes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.wiretech.df.dfmusic.Adapters.SongsAdapter;
import com.wiretech.df.dfmusic.Classes.Song;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;

public class PlayListActivity extends AppCompatActivity {

    public static final String PLAYLIST_ID_EXTRA = "playlist_id_extra";

    private int mPlayListId;

    private ArrayList<Song> mSongs = new ArrayList<>();
    private SongsAdapter mSongsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);

        Toast.makeText(this, "PlayListId = " + String.valueOf(mPlayListId), Toast.LENGTH_SHORT).show();

        fillDate();

        initializeUI();

    }

    private void fillDate() {
        int length = getResources().getStringArray(R.array.temp_songs_ids).length;
        for (int i = 0; i < length; ++i) {
            mSongs.add(new Song(
                    Integer.valueOf(getResources().getStringArray(R.array.temp_songs_ids)[i]),
                    getResources().getStringArray(R.array.temp_songs_names)[i],
                    Integer.valueOf(getResources().getStringArray(R.array.temp_songs_lengths)[i])
            ));
        }
    }

    private void initializeUI() {
        findViewById(R.id.rlBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.rlShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(PlayListActivity.this, "Share!!!", Toast.LENGTH_SHORT).show();
            }
        });

        mSongsAdapter = new SongsAdapter(this, this, mSongs);

        ((ListView) findViewById(R.id.listView)).setAdapter(mSongsAdapter);

    }

}
