package com.wiretech.df.dfmusic.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiretech.df.dfmusic.Activityes.PlayActivity;
import com.wiretech.df.dfmusic.API.Classes.Song;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends BaseAdapter {

    Activity mActivity;
    Context mContext;
    LayoutInflater mLayoutInflater;
    List<Song> mSongs;

    public SongsAdapter(Activity activity, Context context, List<Song> songs) {
        mActivity = activity;
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
            //Toast.makeText(mContext, "Song id = " + String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
            //mContext.startActivity(new Intent(mContext, PlayActivity.class));
            Intent intent = new Intent();
            intent.putExtra(PlayActivity.SONG_ID_EXTRA_RESULT, view.getId());
            Log.d("SongsAdapter", "Song id = " + view.getId());
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    };
}
