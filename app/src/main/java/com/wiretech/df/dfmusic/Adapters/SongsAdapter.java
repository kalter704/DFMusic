package com.wiretech.df.dfmusic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wiretech.df.dfmusic.Activityes.PlayListActivity;
import com.wiretech.df.dfmusic.Classes.PlayList;
import com.wiretech.df.dfmusic.Classes.Song;
import com.wiretech.df.dfmusic.R;


import java.util.ArrayList;

public class SongsAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;
    ArrayList<Song> mSongs;

    public SongsAdapter(Context context, ArrayList<Song> songs) {
        mContext = context;
        mSongs = songs;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mSongs.size();
    }

    @Override
    public Object getItem(int i) {
        return mSongs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.playlist_item_list, viewGroup, false);
        }
        Song song = mSongs.get(i);

        ((TextView) view.findViewById(R.id.tvPos)).setText(String.valueOf(i + 1));
        ((TextView) view.findViewById(R.id.tvName)).setText(song.getName());
        ((TextView) view.findViewById(R.id.tvLength)).setText(song.getLength());

        view.setId(song.getId());

        view.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(mContext, "Song id = " + String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
        }
    };
}
