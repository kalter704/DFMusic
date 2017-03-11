package com.wiretech.df.dfmusic.Activityes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.wiretech.df.dfmusic.Adapters.SongsAdapter;
import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayListActivity extends AppCompatActivity {

    public static final String PLAYLIST_ID_EXTRA = "playlist_id_extra";

    private int mPlayListId;

    private List<Song> mSongs = new ArrayList<>();
    private ArrayList<Integer> mSongsIds = new ArrayList<>();
    private SongsAdapter mSongsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);

        //Toast.makeText(this, "PlayListId = " + String.valueOf(mPlayListId), Toast.LENGTH_SHORT).show();

        fillDate();

        initializeUI();

        showList();
    }

    private void fillDate() {
        mSongs = DBManager.getSongsByPlayListId(mPlayListId);
        for (int i = 0; i < mSongs.size(); ++i) {
            mSongsIds.add(mSongs.get(i).getId());
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
                showList();
            }
        });
    }

    private void showList() {
        mSongsAdapter = new SongsAdapter(this, this, mSongs, mSongsIds);

        ((ListView) findViewById(R.id.listView)).setAdapter(mSongsAdapter);
    }

}
