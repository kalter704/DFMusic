package com.wiretech.df.dfmusicbeta.API.Interfaces;

import com.wiretech.df.dfmusicbeta.API.Classes.MusicServerResponse;

public interface OnResponseAPIListener {
    void onResponse(int action, MusicServerResponse musicServerResponse);

    void onError(int code);
}
