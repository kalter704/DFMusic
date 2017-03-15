package com.wiretech.df.dfmusic.Classes;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.Interfaces.OnPlayerListener;
import com.wiretech.df.dfmusic.Receivers.NoisyAudioStreamReceiver;
import com.wiretech.df.dfmusic.Services.MusicNotificationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

public class Player implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
    public static Player instance = new Player();

    //private NoisyAudioStreamReceiver mNoisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
    //private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private Context mContext = null;

    private MediaPlayer mMediaPlayer;

    private List<OnPlayerListener> mListListeners;

    private Song mOldSong;
    private Song mSong;
    private Song mNewSong;

    private boolean isPlaying = false;
    private boolean isLooping = false;
    private boolean isInterrupt = false;
    private boolean isTransientCanDuck = false;
    private boolean isHasAudioFocus = false;

    private int mCurrentVolume;

    private int mBufferingPercent = 0;

    private AudioManager mAudioManager;

    private Player() {
        mListListeners = new ArrayList<>();
    }

    public void play(Context context) {
        mContext = context;
        if (mMediaPlayer == null) {
            playNewSong();
        } else {
            /*
            if (isPlaying && (mSong.getId() != mNewSong.getId())) {
                playNewSong();
            } else {
                mMediaPlayer.start();
                isPlaying = true;
            }
            */
            if (isPlaying) {
                if (mSong.getId() != mNewSong.getId()) {
                    playNewSong();
                }
            } else {
                if (mSong.getId() != mNewSong.getId()) {
                    playNewSong();
                } else {
                    if (!isHasAudioFocus) {
                        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
                        int result = mAudioManager.requestAudioFocus(instance.afChangeListener,
                                AudioManager.STREAM_MUSIC,
                                AudioManager.AUDIOFOCUS_GAIN);
                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                            mMediaPlayer.start();
                            isPlaying = true;
                        }
                    } else {
                        mMediaPlayer.start();
                        isPlaying = true;
                    }
                }
            }
        }
    }

    public void initNewSong() {
        mNewSong = DBManager.getSongById(MusicState.instance.getSongId());
        MusicState.instance.savePlayingSongIndex();
    }

    public int  getPlayingSongId() {
        if (mSong != null) {
            return mSong.getId();
        } else {
            return -1;
        }
    }

    public int getFinishSongId() {
        if (mOldSong != null) {
            return mOldSong.getId();
        } else {
            return -1;
        }
    }

    public int getBufferingPercent() {
        return mBufferingPercent;
    }

    public String getSongName() {
        return mNewSong.getName();
    }

    public String getSingerName() {
        return mNewSong.getSinger();
    }

    public String getSongsLength() {
        return mNewSong.getLength();
    }

    public String getSongFullAlbumURL() {
        return mNewSong.getFullAlbumURL();
    }

    public boolean getIsSaved() {
        return mNewSong.getIsSaved();
    }

    private void playNewSong() {
        releaseMediaPlayer();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(instance.afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            isHasAudioFocus = true;
            mMediaPlayer = new MediaPlayer();
            mSong = mNewSong;
            try {

                /*
                if ((mSong.getId() % 3) == 0) {
                    mMediaPlayer.setDataSource("http://ol1.mp3party.net/online/4/4323.mp3");
                } else if ((mSong.getId() % 3) == 1) {
                    mMediaPlayer.setDataSource("http://ol2.mp3party.net/online/1214/1214291.mp3");
                } else {
                    mMediaPlayer.setDataSource("http://ol1.mp3party.net/online/2/2807.mp3");
                }
                */

                mMediaPlayer.setDataSource(mSong.getFullSongURL());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(isLooping);
            //Log.d(LOG_TAG, "prepareAsync");
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        isPlaying = false;
    }

    public void stop() {
        isPlaying = false;
        releaseMediaPlayer();
    }

    public void seekTo(int to) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(to);
        }
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setLooping(boolean l) {
        isLooping = l;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLooping);
        }
    }

    public int getDuration() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDuration();
        } else {
            return 0;
        }
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public void setOnPlayerListener(OnPlayerListener listener) {
        mListListeners.add(listener);
    }

    public void unSetOnPlayerListener(OnPlayerListener listener) {
        mListListeners.remove(listener);
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
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(afChangeListener);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mBufferingPercent = percent;
        for (OnPlayerListener listener : mListListeners) {
            listener.OnBufferingUpdateListener(percent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if ((mp.getDuration() - mp.getCurrentPosition()) < 1000) {
            mOldSong = mSong;
            Intent nextIntent = new Intent(mContext, MusicNotificationService.class);
            nextIntent.setAction(Const.ACTION.NEXTFOREGROUND_ACTION);
            PendingIntent pNextIntent = PendingIntent.getService(mContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            try {
                pNextIntent.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
            for (OnPlayerListener listener : mListListeners) {
                listener.OnCompletionListener();
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        isPlaying = true;
    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            //Log.d("PlayerTag", "focusChange = " + String.valueOf(focusChange));
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                //RadioState.state = RadioState.State.INTERRUPTED;
                isInterrupt = true;
                Intent pauseIntent = new Intent(mContext, MusicNotificationService.class);
                pauseIntent.setAction(Const.ACTION.PLAY_ACTION);
                PendingIntent pPauseIntent = PendingIntent.getService(mContext, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pPauseIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                //Log.d("PlayerTag", "AudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT");
            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                if (isPlaying) {
                    mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    int newVolume = (int) (0.3 * mCurrentVolume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_PLAY_SOUND);
                    isTransientCanDuck = true;
                }
                //Log.d("PlayerTag", "mCurrentVolume = " + String.valueOf(mCurrentVolume));
                //Log.d("PlayerTag", "newVolume = " + String.valueOf(newVolume));
                //Log.d("PlayerTag", "AudioFocusChange AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                if (isInterrupt) {
                    Intent playIntent = new Intent(mContext, MusicNotificationService.class);
                    playIntent.setAction(Const.ACTION.PLAY_ACTION);
                    PendingIntent pPlayIntent = PendingIntent.getService(mContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pPlayIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                    isInterrupt = false;
                }
                if (isTransientCanDuck) {
                    isTransientCanDuck = false;
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, AudioManager.FLAG_PLAY_SOUND);
                }
                //Log.d("PlayerTag", "AudioFocusChange AUDIOFOCUS_GAIN");
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                //manager.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                if (isPlaying) {
                    Intent stopIntent = new Intent(mContext, MusicNotificationService.class);
                    stopIntent.setAction(Const.ACTION.AUDIOFOCUS_LOSS_ACTION);
                    PendingIntent pStopIntent = PendingIntent.getService(mContext, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    try {
                        pStopIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                    isPlaying = false;
                }
                isHasAudioFocus = false;
                mAudioManager.abandonAudioFocus(afChangeListener);
                //Log.d("PlayerTag", "AudioFocusChange AUDIOFOCUS_LOSS");
            }
        }
    };

}
