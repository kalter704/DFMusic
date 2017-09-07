package com.wiretech.df.dfmusic.database;

import android.provider.BaseColumns;

public class DBScheme {
    public static class PlaylistTable {
        public static String TABLE_NAME = "playlist";

        public static class Cols implements BaseColumns {
            public static String ID = "id";
            public static String NAME = "name";
            public static String POSITION= "position";
            public static String SCHOOL= "school";
            public static String LAST_UPDATE= "last_update";
        }
    }

    public static class SongTable {
        public static String TABLE_NAME = "song";

        public static class Cols implements BaseColumns {
            public static String ID = "id";
            public static String NAME = "name";
            public static String SINGER = "singer";
            public static String POSITION = "position";
            public static String LENGTH = "length";
            public static String SONG_URL = "song_url";
            public static String IMG_URL = "img_url";
            public static String PLAYLIST_ID = "playlist_id";
            public static String SAVED = "saved";
        }
    }

    public static class SavedSongTable {
        public static String TABLE_NAME = "saved_song";

        public static class Cols implements BaseColumns {
            public static String SONG_ID = "song_id";
            public static String PATH = "path";
        }
    }
}
