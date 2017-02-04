package com.wiretech.df.dfmusic.API.Interfaces;

import com.wiretech.df.dfmusic.API.Classes.MusicServerResponse;
import com.wiretech.df.dfmusic.API.Classes.PlayListResponse;

public interface OnResponseAPIListener {
    void onResponse(int action, MusicServerResponse musicServerResponse);

    void onError();
}
