package com.wiretech.df.dfmusic.interfaces;

import com.wiretech.df.dfmusic.api.classes.Song;

public interface OnPlayerListener {
    void OnCompletionListener(Song s);

    void OnBufferingUpdateListener(int percent);

    void OnChangeSong(Song song);
}
