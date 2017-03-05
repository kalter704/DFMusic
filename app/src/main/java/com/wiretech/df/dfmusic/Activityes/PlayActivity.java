package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.DataBase.DBHelper;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SONG_ID_EXTRA_RESULT = "song_id";
    public static final String PLAYLIST_ID_EXTRA = "playlist_id";
    public static final String PLAYLIST_NUMBER_EXTRA = "playlist_number";

    private int mPlayListId;
    //private int mSongId;
    private Song mSong;

    private String mClub;

    private TextView tvSchoolTitle;
    private TextView tvSongTitle;
    private TextView tvSinger;
    private TextView tvSongLength;
    private TextView tvNowPlay;
    private RelativeLayout rlSave;
    private RelativeLayout rlUnSave;
    private RelativeLayout rlRepeatOn;
    private RelativeLayout rlRepeatOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initializeUI();

        mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);
        int tagNum = getIntent().getIntExtra(PLAYLIST_NUMBER_EXTRA, -1);
        mSong = DBManager.getFirstSongByPlayListId(mPlayListId);
        fillUIWithSong();
        Toast.makeText(this, "PlayListId = " + String.valueOf(mPlayListId), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "TagNum = " + String.valueOf(tagNum), Toast.LENGTH_SHORT).show();
        String[] clubs = getResources().getStringArray(R.array.clubs);
        /*
        for (int i = 0; i < clubs.length; ++i) {
            Toast.makeText(this, "Club name = " + clubs[i], Toast.LENGTH_SHORT).show();
        }
        */
        //mClub = clubs[tagNum].substring(0);
        tvSchoolTitle.setText(clubs[tagNum]);

    }

    private void initializeUI() {
        tvSchoolTitle = (TextView) findViewById(R.id.tvSchoolTitle);
        tvSongTitle = (TextView) findViewById(R.id.tvSongTitle);
        tvSinger = (TextView) findViewById(R.id.tvSinger);
        tvSongLength = (TextView) findViewById(R.id.tvSongLength);
        tvNowPlay = (TextView) findViewById(R.id.tvNowPlay);

        findViewById(R.id.rlBack).setOnClickListener(this);
        findViewById(R.id.rlPlaylist).setOnClickListener(this);

        rlSave = (RelativeLayout) findViewById(R.id.rlSave);
        rlSave.setOnClickListener(this);
        rlUnSave = (RelativeLayout) findViewById(R.id.rlUnSave);
        rlUnSave.setOnClickListener(this);
        rlRepeatOn = (RelativeLayout) findViewById(R.id.rlRepeatOn);
        rlRepeatOn.setOnClickListener(this);
        rlRepeatOff = (RelativeLayout) findViewById(R.id.rlRepeatOff);
        rlRepeatOff.setOnClickListener(this);

        findViewById(R.id.rlPreviousSong).setOnClickListener(this);
        findViewById(R.id.rlPause).setOnClickListener(this);
        findViewById(R.id.rlPlay).setOnClickListener(this);
        findViewById(R.id.rlNextSong).setOnClickListener(this);
    }

    public void fillUIWithSong() {
        tvSongTitle.setText(mSong.getName());
        tvSinger.setText("");
        tvSongLength.setText(mSong.getLength());
        tvNowPlay.setText("0:00");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.rlPlaylist:
                Intent intent = new Intent(PlayActivity.this, PlayListActivity.class);
                intent.putExtra(PlayListActivity.PLAYLIST_ID_EXTRA, mPlayListId);
                startActivityForResult(intent, 0);
                break;
            case R.id.rlSave:

                break;
            case R.id.rlUnSave:

                break;
            case R.id.rlPreviousSong:

                break;
            case R.id.rlPause:

                break;
            case R.id.rlPlay:

                break;
            case R.id.rlNextSong:

                break;
            case R.id.rlRepeatOn:

                break;
            case R.id.rlRepeatOff:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(PlayActivity.this, "Song id = " + data.getIntExtra(SONG_ID_EXTRA_RESULT, -1), Toast.LENGTH_SHORT).show();
            int songId = data.getIntExtra(SONG_ID_EXTRA_RESULT, -1);
            mSong = DBManager.getSongById(songId);
            fillUIWithSong();
        }
    }
}
