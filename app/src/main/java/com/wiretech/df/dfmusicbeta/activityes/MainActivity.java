package com.wiretech.df.dfmusicbeta.activityes;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.wiretech.df.dfmusicbeta.adapters.PlaylistsAdapter;
import com.wiretech.df.dfmusicbeta.api.classes.Playlist;
import com.wiretech.df.dfmusicbeta.Const;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;
import com.wiretech.df.dfmusicbeta.database.DBManager;
import com.wiretech.df.dfmusicbeta.R;
import com.wiretech.df.dfmusicbeta.services.PlayerService;

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
        mPlayLists = DBManager.get(this).getPlayListsWithNameAndSchool();
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlaylistsAdapter = new PlaylistsAdapter(this, mPlayLists);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mPlaylistsAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MenuActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlaylistsAdapter.notifyDataChanged();
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
            Snackbar snackbar = Snackbar.make(
                    findViewById(R.id.mainCoordLayout),
                    "Нажмите еще раз для выхода",
                    Snackbar.LENGTH_SHORT);
            View snackView = snackbar.getView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snackView.setBackgroundColor(getColor(R.color.snackErrorNetworkColor));
            } else {
                snackView.setBackgroundColor(getResources().getColor(R.color.snackErrorNetworkColor));
            }
            TextView snackTV = snackView.findViewById(android.support.design.R.id.snackbar_text);
            snackTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
