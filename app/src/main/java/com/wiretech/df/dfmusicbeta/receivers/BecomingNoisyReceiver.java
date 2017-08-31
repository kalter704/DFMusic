package com.wiretech.df.dfmusicbeta.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.wiretech.df.dfmusicbeta.classes.Player;
import com.wiretech.df.dfmusicbeta.Const;
import com.wiretech.df.dfmusicbeta.services.PlayerService;

public class BecomingNoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            if (Player.get().getState() == Player.PlayerState.PLAYING
                    || Player.get().getState() == Player.PlayerState.PREPARING) {
                PendingIntent pPauseIntent = PlayerService.newPendingIntentToService(context, Const.ACTION.PAUSE_ACTION);
                try {
                    pPauseIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
