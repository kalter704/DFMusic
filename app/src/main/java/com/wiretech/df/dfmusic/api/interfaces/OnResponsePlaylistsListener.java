package com.wiretech.df.dfmusic.api.interfaces;

import com.wiretech.df.dfmusic.api.classes.PlayListResponse;

public interface OnResponsePlaylistsListener {
    void onResponse(PlayListResponse playListResponse);

    void onError();
}
