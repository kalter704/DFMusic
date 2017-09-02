package com.wiretech.df.dfmusicbeta.classes;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.wiretech.df.dfmusicbeta.api.classes.Song;
import com.wiretech.df.dfmusicbeta.Const;
import com.wiretech.df.dfmusicbeta.interfaces.OnPlayerListener;
import com.wiretech.df.dfmusicbeta.receivers.BecomingNoisyReceiver;
import com.wiretech.df.dfmusicbeta.receivers.RemoteControlReceiver;
import com.wiretech.df.dfmusicbeta.services.PlayerService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

public class Player implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    public enum PlayerState { PREPARING,  PLAYING, PAUSE, STOP, INTERRUPT }

    private static Player sPlayer = null;

    private Context mContext = null;

    private MediaPlayer mMediaPlayer;

    private List<OnPlayerListener> mListListeners;

    private Song mSong;

    private BecomingNoisyReceiver mBecomingNoisyReceiver = new BecomingNoisyReceiver();
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

    private PlayerState mPlayerState = PlayerState.STOP;

    private boolean isLooping = false;
    private boolean isTransientCanDuck = false;
    private boolean isPrepared = false;

    private int mBufferingPercent = 0;

    private AudioManager mAudioManager;

    private Player() {
        mListListeners = new ArrayList<>();
    }

    public static Player get() {
        if (sPlayer == null) {
            sPlayer = new Player();
        }
        return sPlayer;
    }

    private boolean registerAFChangeListener() {
        int result = mAudioManager.requestAudioFocus(
                afChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void play(Context context) {
        mContext = context;
        switch (mPlayerState) {
            case PLAYING:
            case STOP:
                playNewSong();
                break;
            case INTERRUPT:
                if (PlayerManager.get().getPlayingSong().getRealID() != mSong.getRealID()) {
                    playNewSong();
                } else {
                    resumePlayerAfterInterrupt();
                }
                break;
            case PREPARING:
                if (PlayerManager.get().getPlayingSong().getRealID() != mSong.getRealID()) {
                    playNewSong();
                }
                break;
            case PAUSE:
                if (PlayerManager.get().getPlayingSong().getRealID() != mSong.getRealID()) {
                    playNewSong();
                } else {
                    resumePlayer();
                }
                break;
        }
    }

    private void playNewSong() {
        isPrepared = false;
        mPlayerState = PlayerState.PREPARING;
        releaseMediaPlayer();

        mSong = PlayerManager.get().getPlayingSong();

        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

        if (registerAFChangeListener()) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(mSong.getFullSongURL());
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                mMediaPlayer.setAudioAttributes(audioAttributes);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();

            mAudioManager.registerMediaButtonEventReceiver(new ComponentName(mContext, RemoteControlReceiver.class));
        }
    }

    public PlayerState getState() {
        return mPlayerState;
    }

    private void resumePlayer() {
        if (isPrepared) {
            if (registerAFChangeListener()) {
                mMediaPlayer.start();
                mPlayerState = PlayerState.PLAYING;
                mContext.registerReceiver(mBecomingNoisyReceiver, intentFilter);
                mAudioManager.registerMediaButtonEventReceiver(new ComponentName(mContext, RemoteControlReceiver.class));
            }
        } else {
            mPlayerState = PlayerState.PREPARING;
        }
    }

    private void resumePlayerAfterInterrupt() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.start();
        mPlayerState = PlayerState.PLAYING;
    }

    private void pausePlayer() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.pause();
        mPlayerState = PlayerState.PAUSE;
        try {
            mContext.unregisterReceiver(mBecomingNoisyReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAudioManager.abandonAudioFocus(afChangeListener);
    }

    public void pause() {
        switch (mPlayerState) {
            case PLAYING:
                pausePlayer();
                break;
            case PREPARING:
                mPlayerState = PlayerState.PAUSE;
                break;
            case PAUSE:
            case STOP:
            case INTERRUPT:
                break;
        }
    }

    private void stopPlayer() {
        releaseMediaPlayer();
        try {
            mContext.unregisterReceiver(mBecomingNoisyReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAudioManager.abandonAudioFocus(afChangeListener);
        mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(mContext, RemoteControlReceiver.class));
        mPlayerState = PlayerState.STOP;
    }

    public void stop() {
        switch (mPlayerState) {
            case PLAYING:
            case PREPARING:
            case PAUSE:
            case INTERRUPT:
                stopPlayer();
                break;
            case STOP:
                break;
        }
    }

    public void interruptPlayer() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.pause();
        mPlayerState = PlayerState.INTERRUPT;
    }

    public void interrupt() {
        switch (mPlayerState) {
            case PLAYING:
            case PREPARING:
                interruptPlayer();
                break;
            case PAUSE:
            case INTERRUPT:
            case STOP:
                break;
        }
    }

    public int getBufferingPercent() {
        return mBufferingPercent;
    }

    public void seekTo(int to) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(to);
        }
    }

    public void setLooping(boolean l) {
        isLooping = l;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(isLooping);
        }
    }

    public boolean getLooping() {
        return isLooping;
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

    public Song getSong() {
        return mSong;
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
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        if ((mp.getDuration() - mp.getCurrentPosition()) < 1000 && mPlayerState != PlayerState.PREPARING) {
            Song tempSong = mSong;
            PlayerManager.get().next(mContext);
            for (OnPlayerListener listener : mListListeners) {
                listener.OnCompletionListener(tempSong);
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mPlayerState == PlayerState.PREPARING) {
            mp.start();
            mPlayerState = PlayerState.PLAYING;
            mContext.registerReceiver(mBecomingNoisyReceiver, intentFilter);
            isPrepared = true;
        }
    }

    public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT) {
                PlayerManager.get().interrupt(mContext);

            } else if (focusChange == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                if (mPlayerState == PlayerState.PLAYING) {
                    mMediaPlayer.setVolume(0.3f, 0.3f);
                    isTransientCanDuck = true;
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (mPlayerState == PlayerState.INTERRUPT) {
                    PlayerManager.get().resume(mContext);
                }

                if (isTransientCanDuck) {
                    isTransientCanDuck = false;
                    mMediaPlayer.setVolume(1, 1);
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                PlayerManager.get().pause(mContext);
                mAudioManager.abandonAudioFocus(afChangeListener);
                mAudioManager.unregisterMediaButtonEventReceiver(new ComponentName(mContext, RemoteControlReceiver.class));
            }
        }
    };

}
