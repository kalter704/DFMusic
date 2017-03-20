package com.wiretech.df.dfmusic.Receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.wiretech.df.dfmusic.Classes.Player;
import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.Services.MusicNotificationService;

public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {

            if (Player.instance.getIsPlaying()) {
                Intent pauseIntent = new Intent(context, MusicNotificationService.class);
                pauseIntent.setAction(Const.ACTION.PLAY_ACTION);
                PendingIntent pPauseIntent = PendingIntent.getService(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pPauseIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else if (Player.instance.getIsInterrupt()) {
                Player.instance.setUnInterrupt();
            }

        }
    }
}
