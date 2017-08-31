package com.wiretech.df.dfmusicbeta.api.interfaces;

import com.wiretech.df.dfmusicbeta.api.classes.MusicServerResponse;

public interface OnResponseAPIListener {
    void onResponse(int action, MusicServerResponse musicServerResponse);

    void onError(int code);
}
