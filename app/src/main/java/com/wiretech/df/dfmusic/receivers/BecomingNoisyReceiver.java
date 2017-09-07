package com.wiretech.df.dfmusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.wiretech.df.dfmusic.classes.Player;
import com.wiretech.df.dfmusic.classes.PlayerManager;

public class BecomingNoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            if (Player.get().getState() == Player.PlayerState.PLAYING
                    || Player.get().getState() == Player.PlayerState.PREPARING) {
                PlayerManager.get().pause(context);
            }
        }
    }
}
