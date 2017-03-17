package com.wiretech.df.dfmusic.Activityes;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.Classes.MusicDownloadManager;
import com.wiretech.df.dfmusic.Classes.MusicState;
import com.wiretech.df.dfmusic.Classes.Player;
import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.Interfaces.OnPlayerListener;
import com.wiretech.df.dfmusic.R;
import com.wiretech.df.dfmusic.Services.MusicNotificationService;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        OnPlayerListener {

    private final String LOG_TAG = "PlayActivity";

    public static final String SONG_POS_EXTRA_RESULT = "song_pos";
    public static final String SONGS_IDS_EXTRA_RESULT = "songs_ids";
    public static final String PLAYLIST_ID_EXTRA = "playlist_id";
    public static final String PLAYLIST_NUMBER_EXTRA = "playlist_number";
    public static final String EXTRA_FROM_NOTIFICATION_FLAG = "from_notification";
    public static final String SAVED_SONG_EXTRA = "saved_song";

    private int mPlayListId;
    //private Song mSong;
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
    private ImageView mIvAlbum;

    //private MediaPlayer mMediaPlayer;

    private boolean isLooping = false;
    private boolean isUserChangeSeek = false;
    private boolean isPlaying = false;

    private Handler mHandler;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        DBManager.with(this);
        MusicDownloadManager.instance.with(this);

        initializeUI();

        boolean isFromNotification = getIntent().getBooleanExtra(EXTRA_FROM_NOTIFICATION_FLAG, false);

        PlayList playList;
        if (isFromNotification) {
            playList = DBManager.getPlayListBySongId(Player.instance.getPlayingSongId());
            mPlayListId = playList.getId();
            mCurrentSongIndex = MusicState.instance.getCurrentPlayingSongIndex();
            mSongsIds = DBManager.getSongsIdsByPLayListId(mPlayListId);
        } else {
            boolean isSavedSongs = getIntent().getBooleanExtra(SAVED_SONG_EXTRA, false);
            if (isSavedSongs) {
                mPlayListId = -1;
                playList = new PlayList(-1, null, "Saved songs");
                mSongsIds = DBManager.getSavedSongsIds();
                if (mSongsIds.size() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlayActivity.this);
                    builder.setTitle("Предупреждение!")
                            .setMessage("Вы не сохранили ни одной песни!")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setCancelable(false)
                            .setNegativeButton("ОК", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();

                    alert.show();

                    alert.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundColor(Color.parseColor("#D4D4D4"));
                    return;
                }
            } else {
                mPlayListId = getIntent().getIntExtra(PLAYLIST_ID_EXTRA, -1);
                playList = DBManager.getPLayListById(mPlayListId);
                mSongsIds = DBManager.getSongsIdsByPLayListId(mPlayListId);
            }
        }

        MusicState.instance.setNewSongData(mSongsIds, mCurrentSongIndex);

        mHandler = new Handler();
        fillUIWithSong();

        tvSchoolTitle.setText(playList.getSchoolName());

        secTimer.start();

        Player.instance.setOnPlayerListener(this);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
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

    private void initializeUI() {
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        //mSeekBar.setProgress(0);
        //mSeekBar.setSecondaryProgress(0);
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

        mIvAlbum = (ImageView) findViewById(R.id.ivAlbum);
    }

    public void fillUIWithSong() {
        Player.instance.initNewSong();
        tvSongTitle.setText(Player.instance.getSongName());
        tvSinger.setText(Player.instance.getSingerName());
        tvSongLength.setText(Player.instance.getSongsLength());
        tvNowPlay.setText("00:00");
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        Picasso.with(PlayActivity.this).load(Player.instance.getSongFullAlbumURL()).into(mIvAlbum);

        if (Player.instance.getPlayingSongId() == mSongsIds.get(mCurrentSongIndex)) {
            mHandler.post(updateProcessPlaying);
            mSeekBar.setSecondaryProgress(Player.instance.getBufferingPercent());
        }

        if (Player.instance.getIsSaved()) {
            showSaveBtn();
            mSeekBar.setSecondaryProgress(100);
        } else {
            showUnSaveBtn();
        }
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
                intent.putExtra(PlayListActivity.SONGS_IDS_EXTRA, mSongsIds);
                startActivityForResult(intent, 0);
                break;

            case R.id.rlSave:
                showUnSaveBtn();
                MusicDownloadManager.instance.deleteSong(mSongsIds.get(mCurrentSongIndex));
                break;
            case R.id.rlUnSave:
                showSaveBtn();
                MusicDownloadManager.instance.downloadSong(mSongsIds.get(mCurrentSongIndex));
                break;

            case R.id.rlPreviousSong:
                previousSong();
                break;
            case R.id.rlNextSong:
                nextSong();
                break;

            case R.id.rlPause:
                showPlayBtn();
                Intent pauseIntent = new Intent(this, MusicNotificationService.class);
                pauseIntent.setAction(Const.ACTION.PLAY_ACTION);
                PendingIntent ppauseIntent = PendingIntent.getService(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    ppauseIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rlPlay:
                showPauseBtn();
                playNewSong();
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

        Player.instance.setLooping(isLooping);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Toast.makeText(PlayActivity.this, "Song id = " + data.getIntExtra(SONG_POS_EXTRA_RESULT, -1), Toast.LENGTH_SHORT).show();
            mCurrentSongIndex = data.getIntExtra(SONG_POS_EXTRA_RESULT, -1);
            mSongsIds = (ArrayList<Integer>) data.getSerializableExtra(SONGS_IDS_EXTRA_RESULT);
            MusicState.instance.setNewSongData(mSongsIds, mCurrentSongIndex);
            //mSong = DBManager.getSongById(mSongsIds.get(mCurrentSongIndex));
            fillUIWithSong();
            if (Player.instance.getIsPlaying()) {
                playNewSong();
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
        //mSeekBar.setProgress(0);
        //mSeekBar.setSecondaryProgress(0);
        MusicState.instance.setNewSongData(mSongsIds, mCurrentSongIndex);
        Player.instance.setLooping(isLooping);
        Player.instance.initNewSong();

        Intent serviceIntent = new Intent(PlayActivity.this, MusicNotificationService.class);
        serviceIntent.setAction(Const.ACTION.PLAYNEWFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    public void previousSong() {
        MusicState.instance.setPreviousSong();
        mCurrentSongIndex = MusicState.instance.getCurrentSongIndex();
        fillUIWithSong();
        if (Player.instance.getIsPlaying()) {
            playNewSong();
        }
    }

    public void nextSong() {
        MusicState.instance.setNextSong();
        mCurrentSongIndex = MusicState.instance.getCurrentSongIndex();
        fillUIWithSong();
        if (Player.instance.getIsPlaying()) {
            playNewSong();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Player.instance.getPlayingSongId() != mSongsIds.get(mCurrentSongIndex)) {
                tvNowPlay.setText("00:00");
                showPlayBtn();
                mSeekBar.setProgress(0);
                if (Player.instance.getIsSaved()) {
                    mSeekBar.setSecondaryProgress(100);
                } else {
                    mSeekBar.setSecondaryProgress(0);
                }
            } else if (!Player.instance.getIsPlaying()) {
                showPlayBtn();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Player.instance.unSetOnPlayerListener(this);
    }

    private Thread secTimer = new Thread() {
        public void run() {
            try {
                while (true) {
                    if (Player.instance.getPlayingSongId() == mSongsIds.get(mCurrentSongIndex)) {
                        if (Player.instance.getIsPlaying()) {
                            if (!isUserChangeSeek) {
                                mHandler.post(updateProcessPlaying);
                            }
                        }
                    } else {
                        mHandler.post(updatePlayBtn);
                    }
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable updateProcessPlaying = new Runnable() {
        public void run() {
            //Log.d(LOG_TAG, "Thread curpo = " + mMediaPlayer.getCurrentPosition());
            if (!isUserChangeSeek) {
                if (Player.instance.getDuration() == 0) {
                    return;
                }
                mSeekBar.setProgress((int) (((float) Player.instance.getCurrentPosition()) / ((float) Player.instance.getDuration()) * 100));
                int m = 0;
                int s = Player.instance.getCurrentPosition() / 1000;
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
                if (Player.instance.getIsPlaying()) {
                    showPauseBtn();
                }
            }
        }
    };

    private Runnable updatePlayBtn = new Runnable() {
        @Override
        public void run() {
            showPlayBtn();
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (Player.instance.getPlayingSongId() == mSongsIds.get(mCurrentSongIndex)) {
            if (fromUser) {
                int m = 0;
                int s = (int) (((float) progress) * Player.instance.getDuration() / 100000);
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
        if (Player.instance.getPlayingSongId() == mSongsIds.get(mCurrentSongIndex)) {
            int to = (int) (((float) seekBar.getProgress()) * Player.instance.getDuration() / 100);
            //Log.d(LOG_TAG, "onProgressChanged to = " + to);
            //Log.d(LOG_TAG, "onProgressChanged seekBar.getProgress() = " + seekBar.getProgress());
            //Log.d(LOG_TAG, "onProgressChanged mMediaPlayer.getDuration() = " + mMediaPlayer.getDuration());
            Player.instance.seekTo(to);
            mHandler.post(updateProcessPlaying);
        }
        isUserChangeSeek = false;
    }

    @Override
    public void OnCompletionListener() {
        if (Player.instance.getFinishSongId() == mSongsIds.get(mCurrentSongIndex)) {
            fillUIWithSong();
            mCurrentSongIndex = MusicState.instance.getCurrentSongIndex();
        }
    }

    @Override
    public void OnBufferingUpdateListener(int percent) {
        if (Player.instance.getPlayingSongId() == mSongsIds.get(mCurrentSongIndex)) {
            mSeekBar.setSecondaryProgress(percent);
        }
    }
}
