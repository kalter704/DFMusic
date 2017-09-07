package com.wiretech.df.dfmusic.classes;

import android.app.Notification;
import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.R;

public class MusicDownloadNotification {

    private Context mContext;

    private String mSongName;

    //private RemoteViews mViews;

    public MusicDownloadNotification(Context context, String songName) {
        mContext = context;
        mSongName = songName;
        showNotification(0);
    }

    public void notifyProgress(int percent) {
        showNotification(percent);
    }

    private void showNotification(int percent) {
        if (percent > 100) {
            percent = 100;
        }
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.notification_music_download);

        views.setTextViewText(R.id.notification_name, "Скачивание " + mSongName);
        views.setProgressBar(R.id.progressBar, 100, percent, false);

        Notification notification = new NotificationCompat.Builder(mContext)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContent(views)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setTicker("Скачиваем...")
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
        nm.notify(Const.NOTIFICATION_DOWNLOAD_ID, notification);
    }

    public void endDownload(String songName) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.notification_music_end_download);

        views.setTextViewText(R.id.notification_name, "Скачана! " + songName + ".");

        Notification notification = new NotificationCompat.Builder(mContext)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContent(views)
                .setAutoCancel(true)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_check)
                .build();

        NotificationManagerCompat nm = NotificationManagerCompat.from(mContext);
        nm.notify(Const.NOTIFICATION_DOWNLOAD_ID, notification);
    }
}
