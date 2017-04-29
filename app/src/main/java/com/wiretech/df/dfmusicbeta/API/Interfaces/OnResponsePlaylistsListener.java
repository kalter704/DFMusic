package com.wiretech.df.dfmusicbeta.API.Interfaces;

import com.wiretech.df.dfmusicbeta.API.Classes.PlayListResponse;

public interface OnResponsePlaylistsListener {
    void onResponse(PlayListResponse playListResponse);

    void onError();
}
