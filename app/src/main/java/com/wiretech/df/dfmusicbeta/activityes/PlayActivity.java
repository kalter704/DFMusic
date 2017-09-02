package com.wiretech.df.dfmusicbeta.activityes;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wiretech.df.dfmusicbeta.R;
import com.wiretech.df.dfmusicbeta.api.classes.Song;
import com.wiretech.df.dfmusicbeta.classes.MusicDownloadManager;
import com.wiretech.df.dfmusicbeta.classes.Player;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;
import com.wiretech.df.dfmusicbeta.classes.SnackBarCreator;
import com.wiretech.df.dfmusicbeta.interfaces.OnPlayerListener;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        OnPlayerListener {

    private final String LOG_TAG = "PlayActivity";

    public static final String SONG_ID_EXTRA = "song_id";
    public static final String FROM_NOTIFICATION_FLAG_EXTRA = "from_notification";

    private Song mSong;

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

    private boolean isLooping = false;
    private boolean isUserChangeSeek = false;

    private Handler mHandler;

    public static Intent newIntent(Context context, int songID) {
        Intent i = new Intent(context, PlayActivity.class);
        i.putExtra(SONG_ID_EXTRA, songID);
        return i;
    }

    public static PendingIntent newPendingIntent(Context context) {
        Intent i = new Intent(context, PlayActivity.class);
        i.putExtra(FROM_NOTIFICATION_FLAG_EXTRA, true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        MusicDownloadManager.instance.with(this);

        initializeUI();

        boolean isFromNotification = getIntent().getBooleanExtra(FROM_NOTIFICATION_FLAG_EXTRA, false);

        if (isFromNotification) {
            mSong = PlayerManager.get().getPlayingSong();
        } else {
            int id = getIntent().getExtras().getInt(SONG_ID_EXTRA);
            mSong = PlayerManager.get().getSelectedSongByID(id);
        }

        mHandler = new Handler();

        fillUIWithSong();

        secTimer.start();

        Player.get().setOnPlayerListener(this);

    }

    private void initializeUI() {
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);

        tvSchoolTitle = (TextView) findViewById(R.id.tvSchoolTitle);
        tvSongTitle = (TextView) findViewById(R.id.tvSongTitle);
        tvSinger = (TextView) findViewById(R.id.tvSinger);
        tvSongLength = (TextView) findViewById(R.id.tvSongLength);
        tvNowPlay = (TextView) findViewById(R.id.tvNowPlay);

        findViewById(R.id.rlBack).setOnClickListener(this);

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
        tvSchoolTitle.setText(PlayerManager.get().getPlaylist().getSchoolName());
        tvSongTitle.setText(mSong.getName());
        tvSinger.setText(mSong.getSinger());
        tvSongLength.setText(mSong.getLength());
        tvNowPlay.setText("00:00");
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        Picasso.with(PlayActivity.this).load(mSong.getFullAlbumURL()).into(mIvAlbum);

        if (Player.get().getSong() != null
                && Player.get().getSong().getRealID() == mSong.getRealID()) {
            mHandler.post(updateProcessPlaying);
            mSeekBar.setSecondaryProgress(Player.get().getBufferingPercent());
        }

        if (mSong.getIsSaved()) {
            showSaveBtn();
            mSeekBar.setSecondaryProgress(100);
        } else {
            showUnSaveBtn();
        }

        if (!Player.get().getLooping()) {
            showRepeatOffBtn();
        } else {
            showRepeatOnBtn();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.rlSave:
                showUnSaveBtn();
                MusicDownloadManager.instance.deleteSong(mSong.getRealID());
                SnackBarCreator.show(this, R.string.snack_unsave);
                break;
            case R.id.rlUnSave:
                showSaveBtn();
                MusicDownloadManager.instance.downloadSong(mSong.getRealID());
                SnackBarCreator.show(this, R.string.snack_save);
                break;

            case R.id.rlPreviousSong:
                previousSong();
                break;
            case R.id.rlNextSong:
                nextSong();
                break;
            case R.id.rlPause:
                showPlayBtn();
                PlayerManager.get().pause(this);
                break;
            case R.id.rlPlay:
                showPauseBtn();
                PlayerManager.get().play(this, mSong);
                break;

            case R.id.rlRepeatOn:
                showRepeatOffBtn();
                isLooping = false;
                SnackBarCreator.show(this, R.string.snack_repeat_off);
                Player.get().setLooping(isLooping);
                break;
            case R.id.rlRepeatOff:
                showRepeatOnBtn();
                isLooping = true;
                SnackBarCreator.show(this, R.string.snack_repeat_on);
                Player.get().setLooping(isLooping);
                break;
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

//    private void playNewSong() {
//        //mSeekBar.setProgress(0);
//        //mSeekBar.setSecondaryProgress(0);
//        MusicState.instance.setNewSongData(mSongsIds, mCurrentSongIndex);
//        Player.instance.setLooping(isLooping);
//        Player.instance.initNewSong();
//
//        Intent serviceIntent = new Intent(PlayActivity.this, MusicNotificationService.class);
//        serviceIntent.setAction(Const.ACTION.PLAYNEWFOREGROUND_ACTION);
//        startService(serviceIntent);
//    }

//    public void previousSong() {
//        MusicState.instance.setPreviousSong();
//        mCurrentSongIndex = MusicState.instance.getCurrentSongIndex();
//        fillUIWithSong();
//        if (Player.instance.getIsPlaying()) {
//            playNewSong();
//        }
//    }

    public void nextSong() {
        if (Player.get().getSong() != null
                && Player.get().getSong().getRealID() == mSong.getRealID()) {
            PlayerManager.get().next(this);
            mSong = PlayerManager.get().getPlayingSong();
        } else {
            mSong = PlayerManager.get().getNextSelectedSongByID(mSong.getRealID());
        }
        fillUIWithSong();
    }

    public void previousSong() {
        if (Player.get().getSong() != null
                && Player.get().getSong().getRealID() == mSong.getRealID()) {
            PlayerManager.get().previous(this);
            mSong = PlayerManager.get().getPlayingSong();
        } else {
            mSong = PlayerManager.get().getPreviousSelectedSongByID(mSong.getRealID());
        }
        fillUIWithSong();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Player.get().getSong() != null
                    && Player.get().getSong().getRealID() != mSong.getRealID()) {
                tvNowPlay.setText("00:00");
                showPlayBtn();
                mSeekBar.setProgress(0);
                if (mSong.getIsSaved()) {
                    mSeekBar.setSecondaryProgress(100);
                } else {
                    mSeekBar.setSecondaryProgress(0);
                }
            } else if (Player.get().getState() != Player.PlayerState.PLAYING
                    && Player.get().getState() != Player.PlayerState.PREPARING) {
                showPlayBtn();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Player.get().unSetOnPlayerListener(this);
    }

    private Thread secTimer = new Thread() {
        public void run() {
            try {
                while (true) {
                    if (Player.get().getSong() != null
                            && Player.get().getSong().getRealID() == mSong.getRealID()) {
                        if (Player.get().getState() == Player.PlayerState.PLAYING) {
                            mHandler.post(updateProcessPlaying);
                        } else if (Player.get().getState() == Player.PlayerState.PREPARING) {
                            mHandler.post(updatePauseBtn);
                        } else {
                            mHandler.post(updatePlayBtn);
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
            if (!isUserChangeSeek) {
                if (Player.get().getDuration() == 0) {
                    return;
                }
                mSeekBar.setProgress((int) (((float) Player.get().getCurrentPosition()) / ((float) Player.get().getDuration()) * 100));
                int m = 0;
                int s = Player.get().getCurrentPosition() / 1000;
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
                if (Player.get().getState() == Player.PlayerState.PLAYING) {
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

    private Runnable updatePauseBtn = new Runnable() {
        @Override
        public void run() {
            showPauseBtn();
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            String[] splitTime = mSong.getLength().split(":");
            int length = Integer.valueOf(splitTime[0]) * 60 + Integer.valueOf(splitTime[1]);

            int m = 0;
            int s = (int) (((float) progress) * length / 100);

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

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        isUserChangeSeek = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (Player.get().getSong().getRealID() == mSong.getRealID()) {
            String[] splitTime = mSong.getLength().split(":");
            int length = Integer.valueOf(splitTime[0]) * 60 + Integer.valueOf(splitTime[1]);

            int to = (int) (((float) seekBar.getProgress()) * length / 100);
            Player.get().seekTo(to * 1000);
            mHandler.post(updateProcessPlaying);
        }
        isUserChangeSeek = false;
    }

    @Override
    public void OnCompletionListener(Song s) {
        if (s.getRealID() == mSong.getRealID()) {
            mSong = PlayerManager.get().getPlayingSong();
            fillUIWithSong();
        }
    }

    @Override
    public void OnBufferingUpdateListener(int percent) {
        if (Player.get().getSong().getRealID() == mSong.getRealID()) {
            mSeekBar.setSecondaryProgress(percent);
        }
    }

}
