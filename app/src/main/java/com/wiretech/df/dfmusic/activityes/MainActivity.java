package com.wiretech.df.dfmusic.activityes;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.wiretech.df.dfmusic.adapters.PlaylistsAdapter;
import com.wiretech.df.dfmusic.api.classes.Playlist;
import com.wiretech.df.dfmusic.classes.PlayerManager;
import com.wiretech.df.dfmusic.classes.SnackBarCreator;
import com.wiretech.df.dfmusic.database.DBManager;
import com.wiretech.df.dfmusic.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Playlist> mPlayLists;
    private RecyclerView mRecyclerView;
    private PlaylistsAdapter mPlaylistsAdapter;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillDate();

        initializeUI();

        setVolumeControlStream (AudioManager.STREAM_MUSIC);

    }

    private void fillDate() {
        mPlayLists = DBManager.get(this).getPlayLists();
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlaylistsAdapter = new PlaylistsAdapter(this, mPlayLists);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPlaylistsAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, MenuActivity.class));
            overridePendingTransition(R.anim.left_in, R.anim.alpha_out);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlaylistsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerManager.get().stop(this);
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            SnackBarCreator.show(this, "Нажмите еще раз для выхода");
        }
        backPressedTime = System.currentTimeMillis();
    }
}
