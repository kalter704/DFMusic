package com.wiretech.df.dfmusicbeta.Activityes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.wiretech.df.dfmusicbeta.API.Classes.Song;
import com.wiretech.df.dfmusicbeta.Adapters.SongsAdapter;
import com.wiretech.df.dfmusicbeta.Classes.AdControl;
import com.wiretech.df.dfmusicbeta.DataBase.DBManager;
import com.wiretech.df.dfmusicbeta.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayListActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String PLAYLIST_ID_EXTRA = "playlist_id_extra";
    public static final String SONGS_IDS_EXTRA = "songs_ids_extra";

    //private int mPlayListId;

    private List<Song> mSongs;
    private List<Song> mShuffleSongs = new ArrayList<Song>();
    private ArrayList<Integer> mSongsIds;
    private ArrayList<Integer> mShuffleSongsIds = new ArrayList<Integer>();
    private SongsAdapter mSongsAdapter;

    private ListView mListView;

    private RelativeLayout mRlShuffleOn;
    private RelativeLayout mRlShuffleOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        //mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);

        DBManager.with(this);

        mSongsIds = (ArrayList<Integer>) getIntent().getSerializableExtra(SONGS_IDS_EXTRA);

        //mSongsIds.

        //Toast.makeText(this, "PlayListId = " + String.valueOf(mPlayListId), Toast.LENGTH_SHORT).show();

        fillDate();

        initializeUI();

        showList();

        resetOrderSongs();
    }

    private void fillDate() {
        mSongs = DBManager.getSongsBySongsIds(mSongsIds);
    }

    private void initializeUI() {
        findViewById(R.id.rlBack).setOnClickListener(view -> finish());

        //findViewById(R.id.rlShare).setOnClickListener(v -> Share.share(PlayListActivity.this));
        mRlShuffleOn = (RelativeLayout) findViewById(R.id.rlShuffleOn);
        mRlShuffleOn.setOnClickListener(this);
        mRlShuffleOff = (RelativeLayout) findViewById(R.id.rlShuffleOff);
        mRlShuffleOff.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.listView);
    }

    private void showList() {
        mSongsAdapter = new SongsAdapter(this, this, mSongs, mSongsIds);
        mListView.setAdapter(mSongsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdControl.getInstance().intoActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdControl.getInstance().outOfActivity();
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
        updateListView();
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
        Random random = new Random(System.currentTimeMillis());
        /*
        mShuffleSongsIds = new ArrayList<Integer>();
        mShuffleSongs = new ArrayList<Song>();
        */
        for (int i = mSongs.size() - 1; i > 0 ; --i) {
            int ranIndex = random.nextInt(i);
            Collections.swap(mShuffleSongs, ranIndex, i);
            Collections.swap(mShuffleSongsIds, ranIndex, i);
        }
    }

    private void resetOrderSongs() {
        mShuffleSongsIds.clear();
        mShuffleSongsIds.addAll(mSongsIds);
        mShuffleSongs.clear();
        mShuffleSongs.addAll(mSongs);
    }

    private void updateListView() {
        mSongsAdapter = new SongsAdapter(this, this, mShuffleSongs, mShuffleSongsIds);
        mListView.setAdapter(mSongsAdapter);
    }

}
