package com.wiretech.df.dfmusic.api.interfaces;

import com.wiretech.df.dfmusic.api.classes.MusicServerResponse;

public interface OnResponseAPIListener {
    void onResponse(int action, MusicServerResponse musicServerResponse);

    void onError(int code);
}
