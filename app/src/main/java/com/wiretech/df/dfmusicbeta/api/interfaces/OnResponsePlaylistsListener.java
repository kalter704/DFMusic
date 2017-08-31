package com.wiretech.df.dfmusicbeta.api.interfaces;

import com.wiretech.df.dfmusicbeta.api.classes.PlayListResponse;

public interface OnResponsePlaylistsListener {
    void onResponse(PlayListResponse playListResponse);

    void onError();
}
