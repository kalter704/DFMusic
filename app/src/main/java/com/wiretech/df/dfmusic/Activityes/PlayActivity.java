package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.DataBase.DBHelper;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, SeekBar.OnSeekBarChangeListener {

    private final String LOG_TAG = "PlayActivity";

    public static final String SONG_POS_EXTRA_RESULT = "song_pos";
    public static final String SONGS_IDS_EXTRA_RESULT = "songs_ids";
    public static final String PLAYLIST_ID_EXTRA = "playlist_id";
    public static final String PLAYLIST_NUMBER_EXTRA = "playlist_number";

    private int mPlayListId;
    private Song mSong;
    private ArrayList<Integer> mSongsIds;
    private int mCurrentSongIndex = 0;

    private TextView tvSchoolTitle;
    private TextView tvSongTitle;
    private TextView tvSinger;
    private TextView tvSongLength;
    private TextView tvNowPlay;
    private RelativeLayout rlSave;
    private RelativeLayout rlUnSave;
    private RelativeLayout rlRepeatOn;
    private RelativeLayout rlRepeatOff;
    private RelativeLayout rlPlay;
    private RelativeLayout rlPause;
    private SeekBar mSeekBar;

    private MediaPlayer mMediaPlayer;

    private boolean isLooping = false;
    private boolean isUserChangeSeek = false;
    private boolean isPlaying = false;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initializeUI();

        mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);
        int tagNum = getIntent().getIntExtra(PLAYLIST_NUMBER_EXTRA, -1);
        mSongsIds = DBManager.getSongsIdsByPLayListId(mPlayListId);
        //mSong = DBManager.getFirstSongByPlayListId(mPlayListId);
        mSong = DBManager.getSongById(mSongsIds.get(mCurrentSongIndex));
        fillUIWithSong();
        //Toast.makeText(this, "PlayListId = " + String.valueOf(mPlayListId), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "TagNum = " + String.valueOf(tagNum), Toast.LENGTH_SHORT).show();
        String[] clubs = getResources().getStringArray(R.array.clubs);
        tvSchoolTitle.setText(clubs[tagNum]);

        mHandler = new Handler();

        secTimer.start();
    }

    private void initializeUI() {
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        mSeekBar.setOnSeekBarChangeListener(this);

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


        rlPlay = (RelativeLayout) findViewById(R.id.rlPlay);
        rlPlay.setOnClickListener(this);
        rlPause = (RelativeLayout) findViewById(R.id.rlPause);
        rlPause.setOnClickListener(this);

        findViewById(R.id.rlPreviousSong).setOnClickListener(this);
        findViewById(R.id.rlNextSong).setOnClickListener(this);
    }

    public void fillUIWithSong() {
        tvSongTitle.setText(mSong.getName());
        tvSinger.setText(mSong.getSinger());
        tvSongLength.setText(mSong.getLength());
        tvNowPlay.setText("00:00");
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
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
                showUnSaveBtn();
                break;
            case R.id.rlUnSave:
                showSaveBtn();
                break;

            case R.id.rlPreviousSong:
                PreviousSong();
                break;
            case R.id.rlNextSong:
                nextSong();
                break;

            case R.id.rlPause:
                showPlayBtn();
                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                }
                isPlaying = false;
                break;
            case R.id.rlPlay:
                showPauseBtn();
                if (mMediaPlayer == null) {
                    playNewSong();
                } else {
                    mMediaPlayer.start();
                    isPlaying = true;
                }
                break;

            case R.id.rlRepeatOn:
                showRepeatOffBtn();
                isLooping = false;
                break;
            case R.id.rlRepeatOff:
                showRepeatOnBtn();
                isLooping = true;
                break;
        }

        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLooping);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(PlayActivity.this, "Song id = " + data.getIntExtra(SONG_POS_EXTRA_RESULT, -1), Toast.LENGTH_SHORT).show();
            mCurrentSongIndex = data.getIntExtra(SONG_POS_EXTRA_RESULT, -1);
            mSongsIds = (ArrayList<Integer>) data.getSerializableExtra(SONGS_IDS_EXTRA_RESULT);
            mSong = DBManager.getSongById(mSongsIds.get(mCurrentSongIndex));
            fillUIWithSong();
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    playNewSong();
                } else {
                    releaseMediaPlayer();
                }
            }
        }
    }

    private void showPlayBtn() {
        rlPlay.setVisibility(View.VISIBLE);
        rlPause.setVisibility(View.GONE);
    }

    private void showPauseBtn() {
        rlPause.setVisibility(View.VISIBLE);
        rlPlay.setVisibility(View.GONE);
    }

    private void showSaveBtn() {
        rlSave.setVisibility(View.VISIBLE);
        rlUnSave.setVisibility(View.GONE);
    }

    private void showUnSaveBtn() {
        rlUnSave.setVisibility(View.VISIBLE);
        rlSave.setVisibility(View.GONE);
    }

    private void showRepeatOnBtn() {
        rlRepeatOn.setVisibility(View.VISIBLE);
        rlRepeatOff.setVisibility(View.GONE);
    }

    private void showRepeatOffBtn() {
        rlRepeatOff.setVisibility(View.VISIBLE);
        rlRepeatOn.setVisibility(View.GONE);
    }

    private void playNewSong() {
        releaseMediaPlayer();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(isLooping);
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        try {
            mMediaPlayer.setDataSource(mSong.getFullSongURL());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //Log.d(LOG_TAG, "prepareAsync");
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.prepareAsync();
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //Log.d(LOG_TAG, "onPrepared");
        mp.start();
        isPlaying = true;
        //Log.d(LOG_TAG, "onPrepared duration = " + mp.getDuration());
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Log.d(LOG_TAG, "onBufferingUpdate percent = " + percent);
        mSeekBar.setSecondaryProgress(percent);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "onCompletion");
        /*
        Log.d(LOG_TAG, "onCompletion mMediaPlayer.getCurrentPosition() = " + mMediaPlayer.getCurrentPosition());
        Log.d(LOG_TAG, "onCompletion mMediaPlayer.getDuration() = " + mMediaPlayer.getDuration());
        */
        if ((mMediaPlayer.getDuration() - mMediaPlayer.getCurrentPosition()) < 1000) {
            nextSong();
        }

    }

    public void nextSong() {
        ++mCurrentSongIndex;
        if (mCurrentSongIndex >= mSongsIds.size()) {
            mCurrentSongIndex = 0;
        }
        mSong = DBManager.getSongById(mSongsIds.get(mCurrentSongIndex));
        fillUIWithSong();
        playNewSong();
    }

    public void PreviousSong() {
        --mCurrentSongIndex;
        if (mCurrentSongIndex < 0) {
            mCurrentSongIndex = mSongsIds.size() - 1;
        }
        mSong = DBManager.getSongById(mSongsIds.get(mCurrentSongIndex));
        fillUIWithSong();
        playNewSong();
    }

    /*
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onTimedText(MediaPlayer mp, TimedText text) {
        Log.d(LOG_TAG, "onTimedText text =" + text);
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    private Thread secTimer = new Thread() {
        public void run() {
            try {
                while(true) {
                    if (mMediaPlayer != null) {
                        if (isPlaying) {
                            if (!isUserChangeSeek) {
                                mHandler.post(updateProcessPlaying);
                            }
                        }
                    }
                    sleep(1000);
                }
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable updateProcessPlaying = new Runnable() {
        public void run() {
            //Log.d(LOG_TAG, "Thread curpo = " + mMediaPlayer.getCurrentPosition());
            if (!isUserChangeSeek) {
                if (mMediaPlayer == null) {
                    return;
                }
                mSeekBar.setProgress((int) (((float) mMediaPlayer.getCurrentPosition()) / ((float) mMediaPlayer.getDuration()) * 100));
                int m = 0;
                int s = mMediaPlayer.getCurrentPosition() / 1000;
                while (s > 59) {
                    ++m;
                    s -= 60;
                }
                String time = "";
                if (m < 10) {
                    time += "0" + String.valueOf(m);
                } else {
                    time += String.valueOf(m);
                }
                time += ":";
                if (s > 9) {
                    time += String.valueOf(s);
                } else {
                    time += "0" + String.valueOf(s);
                }
                tvNowPlay.setText(time);
                if (mMediaPlayer.isPlaying()) {
                    showPauseBtn();
                }
            }
        }
    };

    /*
    private Runnable showEndOfPlay = new Runnable() {
        @Override
        public void run() {

        }
    };
    */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            if (mMediaPlayer != null) {
                int m = 0;
                int s = (int) (((float) progress) * mMediaPlayer.getDuration() / 100000);
                while ((s - 60) > 0) {
                    ++m;
                    s -= 60;
                }
                String time = "";
                if (m < 10) {
                    time += "0" + String.valueOf(m);
                } else {
                    time += String.valueOf(m);
                }
                time += ":";
                if (s > 9) {
                    time += String.valueOf(s);
                } else {
                    time += "0" + String.valueOf(s);
                }
                tvNowPlay.setText(time);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserChangeSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mMediaPlayer != null) {
            int to = (int) (((float) seekBar.getProgress()) * mMediaPlayer.getDuration() / 100);
            //Log.d(LOG_TAG, "onProgressChanged to = " + to);
            //Log.d(LOG_TAG, "onProgressChanged seekBar.getProgress() = " + seekBar.getProgress());
            //Log.d(LOG_TAG, "onProgressChanged mMediaPlayer.getDuration() = " + mMediaPlayer.getDuration());
            mMediaPlayer.seekTo(to);
            mHandler.post(updateProcessPlaying);
        }
        isUserChangeSeek = false;
    }
}
