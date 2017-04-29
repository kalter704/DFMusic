package com.wiretech.df.dfmusicbeta.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiretech.df.dfmusicbeta.API.Classes.Song;
import com.wiretech.df.dfmusicbeta.Activityes.PlayActivity;
import com.wiretech.df.dfmusicbeta.Classes.Player;
import com.wiretech.df.dfmusicbeta.R;

import java.util.ArrayList;
import java.util.List;

public class SongsAdapter extends BaseAdapter {

    private String LOG_TAG = "song_adapter";

    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Song> mSongs;
    private ArrayList<Integer> mSongsIds;

    public SongsAdapter(Activity activity, Context context, List<Song> songs, ArrayList<Integer> songsIds) {
        mActivity = activity;
        mContext = context;
        mSongs = songs;
        mSongsIds = songsIds;
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

        TextView tvPos = (TextView) view.findViewById(R.id.tvPos);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        TextView tvLength = (TextView) view.findViewById(R.id.tvLength);

        tvPos.setText(String.valueOf(i + 1));
        tvName.setText(song.getName());
        tvLength.setText(song.getLength());

        if (song.getId() != Player.instance.getPlayingSongId()) {
            tvPos.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
            tvName.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
            tvLength.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
        } else {
            Log.d(LOG_TAG, "song.getId() = " + song.getId());
            Log.d(LOG_TAG, "Player.instance.getPlayingSongId() = " + Player.instance.getPlayingSongId());
            tvPos.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
            tvName.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
            tvLength.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
        }

        view.setId(i);

        view.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //Toast.makeText(mContext, "Song id = " + String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
            //mContext.startActivity(new Intent(mContext, PlayActivity.class));
            Intent intent = new Intent();
            intent.putExtra(PlayActivity.SONG_POS_EXTRA_RESULT, view.getId());
            intent.putExtra(PlayActivity.SONGS_IDS_EXTRA_RESULT, mSongsIds);
            Log.d(LOG_TAG, "Song id = " + view.getId());
            mActivity.setResult(Activity.RESULT_OK, intent);
            mActivity.finish();
        }
    };
}
