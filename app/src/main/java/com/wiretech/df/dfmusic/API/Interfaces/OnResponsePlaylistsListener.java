package com.wiretech.df.dfmusic.API.Interfaces;

import com.wiretech.df.dfmusic.API.Classes.PlayList;

import java.util.List;

public interface OnResponsePlaylistsListener {
    void onResponse(List<PlayList> playLists);
}
