package com.wiretech.df.dfmusicbeta.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.wiretech.df.dfmusicbeta.Const;
import com.wiretech.df.dfmusicbeta.Const.ACTION;
import com.wiretech.df.dfmusicbeta.R;
import com.wiretech.df.dfmusicbeta.activityes.PlayActivity;
import com.wiretech.df.dfmusicbeta.classes.Player;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;

public class PlayerService extends Service {

    private enum NotificationState { PLAY, PAUSE }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action.equals(ACTION.PLAY_ACTION)) {
            Player.get().play(getApplicationContext());
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.PAUSE_ACTION)) {
            Player.get().pause();
            showNotification(NotificationState.PAUSE);

        } else if (action.equals(ACTION.STOP_ACTION)) {
            Player.get().stop();
            //showNotification(NotificationState.PAUSE);
            stopForeground(true);
            stopSelf();

        } else if (action.equals(ACTION.PLAY_NEXT_ACTION)) {
            Player.get().play(getApplicationContext());
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.PLAY_PREVIOUS_ACTION)) {
            Player.get().play(getApplicationContext());
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.NOTIFICATION_PLAY_NEXT_ACTION)) {
            PlayerManager.get().setNextSongForPlayer();
            Player.get().play(getApplicationContext());
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.NOTIFICATION_PLAY_PREVIOUS_ACTION)) {
            PlayerManager.get().setPreviousSongForPlayer();
            Player.get().play(getApplicationContext());
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.INTERRUPT_ACTION)) {
            Player.get().interrupt();
            showNotification(NotificationState.PLAY);

        } else if (action.equals(ACTION.UPDATE_NOTIFICATION_ACTION)) {
            if (Player.get().getState() == Player.PlayerState.PLAYING
                    || Player.get().getState() == Player.PlayerState.PREPARING) {
                showNotification(NotificationState.PLAY);
            } else {
                showNotification(NotificationState.PAUSE);
            }
        }

        return START_STICKY;
    }

    public static PendingIntent newPendingIntentToService(Context context, String action) {
        Intent i = new Intent(context, PlayerService.class);
        i.setAction(action);
        return PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void showNotification(NotificationState state) {

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification);
        RemoteViews bigViews = new RemoteViews(getPackageName(), R.layout.notification_big);

        // надод попроваить!!!!!!!!11
        PendingIntent pendingIntent = PlayActivity.newPendingIntent(this);

        PendingIntent pPlayPauseIntent = null;
        switch (state) {
            case PLAY:
                pPlayPauseIntent = newPendingIntentToService(this, ACTION.PAUSE_ACTION);
                break;
            case PAUSE:
                pPlayPauseIntent = newPendingIntentToService(this, ACTION.PLAY_ACTION);
                break;
        }
        PendingIntent pCloseIntent = newPendingIntentToService(this, ACTION.STOP_ACTION);
        PendingIntent pPreviousIntent = newPendingIntentToService(this, ACTION.NOTIFICATION_PLAY_PREVIOUS_ACTION);
        PendingIntent pNextIntent = newPendingIntentToService(this, ACTION.NOTIFICATION_PLAY_NEXT_ACTION);

        views.setOnClickPendingIntent(R.id.notification_base, pendingIntent);
        bigViews.setOnClickPendingIntent(R.id.notification_base, pendingIntent);

        views.setOnClickPendingIntent(R.id.notification_play, pPlayPauseIntent);
        views.setOnClickPendingIntent(R.id.notification_close, pCloseIntent);
        views.setOnClickPendingIntent(R.id.notification_previous, pPreviousIntent);
        views.setOnClickPendingIntent(R.id.notification_next, pNextIntent);

        bigViews.setOnClickPendingIntent(R.id.notification_play, pPlayPauseIntent);
        bigViews.setOnClickPendingIntent(R.id.notification_close, pCloseIntent);
        bigViews.setOnClickPendingIntent(R.id.notification_previous, pPreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.notification_next, pNextIntent);

        views.setTextViewText(R.id.notification_line_one, PlayerManager.get().getPlayingSong().getName());
        views.setTextViewText(R.id.notification_line_two, PlayerManager.get().getPlayingSong().getSinger());

        bigViews.setTextViewText(R.id.notification_line_one, PlayerManager.get().getPlayingSong().getName());
        bigViews.setTextViewText(R.id.notification_line_two, PlayerManager.get().getPlayingSong().getSinger());

        switch (state) {
            case PLAY:
                views.setImageViewResource(R.id.notification_play, R.drawable.ic_pause);
                bigViews.setImageViewResource(R.id.notification_play, R.drawable.ic_pause);
                break;
            case PAUSE:
                views.setImageViewResource(R.id.notification_play, R.drawable.ic_play);
                bigViews.setImageViewResource(R.id.notification_play, R.drawable.ic_play);
                break;
        }

        Notification notification = new NotificationCompat.Builder(this)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContent(views)
                .setCustomBigContentView(bigViews)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOngoing(true)
                .build();

        startForeground(Const.NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
