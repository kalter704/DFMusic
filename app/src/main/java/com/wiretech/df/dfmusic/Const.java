package com.wiretech.df.dfmusic;

public class Const {
    public interface ACTION {
        String MAIN_ACTION = "com.wiretech.df.dfmusic.action.main";
        String PLAY_ACTION = "com.wiretech.df.dfmusic.action.play";
        String PLAYFOREGROUND_ACTION = "com.wiretech.df.dfmusic.action.playforeground";
        String PLAYNEWFOREGROUND_ACTION = "com.wiretech.df.dfmusic.action.playnewforeground";
        String STOPFOREGROUND_ACTION = "com.wiretech.df.dfmusic.action.stopforeground";
        String PREVIOUSFOREGROUND_ACTION = "com.wiretech.df.dfmusic.action.previousforeground";
        String NEXTFOREGROUND_ACTION = "com.wiretech.df.dfmusic.action.nextforeground";
        String UPDATE_NOTIFICATION_ACTION = "com.wiretech.df.dfmusic.action.updatenotification";
        String AUDIOFOCUS_LOSS_ACTION = "com.wiretech.df.dfmusic.action.audiofocuslost";
    }

    public static int FOREGROUND_SERVICE = 101;
}
