package com.wiretech.df.dfmusicbeta.activityes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wiretech.df.dfmusicbeta.R;
import com.wiretech.df.dfmusicbeta.adapters.SongsAdapter;
import com.wiretech.df.dfmusicbeta.api.classes.Playlist;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;
import com.wiretech.df.dfmusicbeta.database.DBManager;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PLAYLIST_ID_EXTRA = "playlist_id_extra";

    private SongsAdapter mSongsAdapter;

    private ListView mListView;

    private RelativeLayout mRlShuffleOn;
    private RelativeLayout mRlShuffleOff;

    private int mPlaylistID;

    public static Intent newIntent(Context context, int playListId) {
        Intent i = new Intent(context, PlaylistActivity.class);
        i.putExtra(PLAYLIST_ID_EXTRA, playListId);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        mPlaylistID = getIntent().getExtras().getInt(PLAYLIST_ID_EXTRA, -1);

        if (mPlaylistID != -1) {
            PlayerManager.get().setPlaylist(DBManager.get(this).getFullPlaylistByID(mPlaylistID));
        } else {
            Playlist playlist = new Playlist(0, "SAVED", "SAVED");
            playlist.setSongs(DBManager.get(this).getAllSavedSongs());
            PlayerManager.get().setPlaylist(playlist);
        }

        initializeUI();

        resetOrderSongs();
    }

    private void initializeUI() {
        findViewById(R.id.rlBack).setOnClickListener(view -> finish());

        mRlShuffleOn = (RelativeLayout) findViewById(R.id.rlShuffleOn);
        mRlShuffleOn.setOnClickListener(this);
        mRlShuffleOff = (RelativeLayout) findViewById(R.id.rlShuffleOff);
        mRlShuffleOff.setOnClickListener(this);

        mSongsAdapter = new SongsAdapter(this, PlayerManager.get().getSongs());

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mSongsAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSongsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlShuffleOff:
                showShuffleOn();
                shuffleSongs();
                break;
            case R.id.rlShuffleOn:
                showShuffleOff();
                resetOrderSongs();
                break;
        }
    }

    private void showShuffleOn() {
        mRlShuffleOff.setVisibility(View.GONE);
        mRlShuffleOn.setVisibility(View.VISIBLE);
    }

    private void showShuffleOff() {
        mRlShuffleOff.setVisibility(View.VISIBLE);
        mRlShuffleOn.setVisibility(View.GONE);
    }

    private void shuffleSongs() {
        mSongsAdapter.setSongs(PlayerManager.get().shuffleSongs());
        mSongsAdapter.notifyDataSetChanged();
    }

    private void resetOrderSongs() {
        mSongsAdapter.setSongs(PlayerManager.get().resetOrderSongs());
        mSongsAdapter.notifyDataSetChanged();
    }

}
