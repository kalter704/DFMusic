package com.wiretech.df.dfmusic.activityes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiretech.df.dfmusic.R;
import com.wiretech.df.dfmusic.adapters.SongsAdapter;
import com.wiretech.df.dfmusic.api.classes.Playlist;
import com.wiretech.df.dfmusic.api.classes.Song;
import com.wiretech.df.dfmusic.classes.Player;
import com.wiretech.df.dfmusic.classes.PlayerManager;
import com.wiretech.df.dfmusic.database.DBManager;
import com.wiretech.df.dfmusic.interfaces.OnPlayerListener;

public class PlaylistActivity extends AppCompatActivity implements View.OnClickListener, OnPlayerListener{

    public static final String PLAYLIST_ID_EXTRA = "playlist_id_extra";

    private SongsAdapter mSongsAdapter;

    private ListView mListView;

    private RelativeLayout mRlShuffleOn;
    private RelativeLayout mRlShuffleOff;

    private int mPlaylistID;

    private boolean isSongShuffled = false;

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
            Playlist playlist = DBManager.get(this).getFullPlaylistByID(mPlaylistID);
            if (PlayerManager.get().getPlayingPlaylist() != null
                    && playlist.getID() == PlayerManager.get().getPlayingPlaylist().getID()) {

                PlayerManager.get().setPlaylist(PlayerManager.get().getPlayingPlaylist());
                if (PlayerManager.get().getPlayingPlaylist().isShuffle()) {
                    isSongShuffled = true;
                    PlayerManager.get().getPlaylist().setSongs(playlist.getSongs());

                } else {
                    isSongShuffled = false;
                }

            } else {
                PlayerManager.get().setPlaylist(playlist);
            }
        } else {
            Playlist playlist = new Playlist(0, "SAVED", "SAVED");
            playlist.setSongs(DBManager.get(this).getAllSavedSongs());
            PlayerManager.get().setPlaylist(playlist);
        }

        initializeUI();

        PlayerManager.get().setShuffle(isSongShuffled);

        Player.get().setOnPlayerListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Player.get().unSetOnPlayerListener(this);
    }

    private void initializeUI() {
        ((TextView) findViewById(R.id.tvTitle)).setText(PlayerManager.get().getPlaylist().getName());

        findViewById(R.id.rlBack).setOnClickListener(view -> finish());

        mRlShuffleOn = (RelativeLayout) findViewById(R.id.rlShuffleOn);
        mRlShuffleOn.setOnClickListener(this);
        mRlShuffleOff = (RelativeLayout) findViewById(R.id.rlShuffleOff);
        mRlShuffleOff.setOnClickListener(this);

        if (isSongShuffled) {
            showShuffleOn();
        }

        mSongsAdapter = new SongsAdapter(this, PlayerManager.get().getSongs());

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(mSongsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlShuffleOff:
                showShuffleOn();
                shuffleSongs();
                isSongShuffled = true;
                break;
            case R.id.rlShuffleOn:
                showShuffleOff();
                resetOrderSongs();
                isSongShuffled = false;
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

    @Override
    public void OnCompletionListener(Song s) {
        mSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public void OnBufferingUpdateListener(int percent) {

    }

    @Override
    public void OnChangeSong(Song song) {
        mSongsAdapter.notifyDataSetChanged();
    }
}
