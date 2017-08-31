package com.wiretech.df.dfmusicbeta.interfaces;

import com.wiretech.df.dfmusicbeta.api.classes.Song;

public interface OnPlayerListener {
    void OnCompletionListener(Song s);

    void OnBufferingUpdateListener(int percent);
}
