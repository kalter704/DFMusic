package com.wiretech.df.dfmusicbeta.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wiretech.df.dfmusicbeta.R;
import com.wiretech.df.dfmusicbeta.activityes.PlayActivity;
import com.wiretech.df.dfmusicbeta.api.classes.Song;
import com.wiretech.df.dfmusicbeta.classes.Player;
import com.wiretech.df.dfmusicbeta.classes.PlayerManager;

import java.util.List;

public class SongsAdapter extends ArrayAdapter<Song> implements View.OnClickListener {

    private class SongHolder {
        int ID;
        TextView mTvPos;
        TextView mTvName;
        TextView mTvLength;
    }

    private String LOG_TAG = "song_adapter";

    private Context mContext;
    private List<Song> mSongs;

    public SongsAdapter(Context context, List<Song> songs) {
        super(context, -1, songs);
        mContext = context;
        mSongs = songs;
    }

    public void setSongs(List<Song> songs) {
        mSongs = songs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SongHolder songHolder;

        Song song = mSongs.get(position);

        if (convertView == null) {
            songHolder= new SongHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.playlist_item_list, parent, false);

            songHolder.mTvPos = convertView.findViewById(R.id.tvPos);
            songHolder.mTvName = convertView.findViewById(R.id.tvName);
            songHolder.mTvLength = convertView.findViewById(R.id.tvLength);

            convertView.setTag(songHolder);

            convertView.setOnClickListener(this);
        } else {
            songHolder = (SongHolder) convertView.getTag();
        }

        songHolder.ID = song.getRealID();
        songHolder.mTvPos.setText(String.valueOf(position + 1));
        songHolder.mTvName.setText(song.getName());
        songHolder.mTvLength.setText(song.getLength());

        if (Player.get().getSong() != null
                && PlayerManager.get().getPlayingSong() != null
                && PlayerManager.get().getPlayingSong().getRealID() == songHolder.ID
                && (Player.get().getState() == Player.PlayerState.PLAYING
                    || Player.get().getState() == Player.PlayerState.PREPARING)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                songHolder.mTvPos.setTextColor(mContext.getColor(R.color.textColorRed));
                songHolder.mTvName.setTextColor(mContext.getColor(R.color.textColorRed));
                songHolder.mTvLength.setTextColor(mContext.getColor(R.color.textColorRed));
            } else {
                songHolder.mTvPos.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
                songHolder.mTvName.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
                songHolder.mTvLength.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                songHolder.mTvPos.setTextColor(mContext.getColor(R.color.textColorPrimary));
                songHolder.mTvName.setTextColor(mContext.getColor(R.color.textColorPrimary));
                songHolder.mTvLength.setTextColor(mContext.getColor(R.color.textColorPrimary));
            } else {
                songHolder.mTvPos.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
                songHolder.mTvName.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
                songHolder.mTvLength.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
            }
        }

        return convertView;
    }

    @Override
    public void onClick(View view) {
        int ID = ((SongHolder) view.getTag()).ID;
        mContext.startActivity(PlayActivity.newIntent(mContext, ID));
    }

    //    public class SongHolder extends RecyclerView.ViewHolder {
//
//        private int ID;
//        private TextView mTvPos;
//        private TextView mTvName;
//        private TextView mTvLength;
//
//        public SongHolder(View itemView) {
//            super(itemView);
//            mTvPos = itemView.findViewById(R.id.tvPos);
//            mTvName = itemView.findViewById(R.id.tvName);
//            mTvLength = itemView.findViewById(R.id.tvLength);
//            itemView.setOnClickListener(v -> mContext.startActivity(PlayActivity.newIntent(mContext, ID)));
//        }
//
//        public void bindSong(int pos, Song s) {
//            ID = s.getID();
//            mTvPos.setText(String.valueOf(pos));
//            mTvName.setText(s.getName());
//            mTvLength.setText(s.getLength());
//        }
//    }
//
//    private Context mContext;
//    private List<Song> mSongs;
//
//    public SongsAdapter(Context context, List<Song> songs) {
//        mContext = context;
//        mSongs = songs;
//    }
//
//    @Override
//    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        return new SongHolder(LayoutInflater.from(mContext).inflate(R.layout.playlist_item_list, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(SongHolder holder, int position) {
//        holder.bindSong(position + 1, mSongs.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return mSongs.size();
//    }





//    @Override
//    public int getCount() {
//        return mSongs.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return mSongs.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (view == null) {
//            view = mLayoutInflater.inflate(R.layout.playlist_item_list, viewGroup, false);
//        }
//        Song song = mSongs.get(i);
//
//        TextView tvPos = (TextView) view.findViewById(R.id.tvPos);
//        TextView tvName = (TextView) view.findViewById(R.id.tvName);
//        TextView tvLength = (TextView) view.findViewById(R.id.tvLength);
//
//        tvPos.setText(String.valueOf(i + 1));
//        tvName.setText(song.getName());
//        tvLength.setText(song.getLength());
//
//        if (song.getID() != Player.instance.getPlayingSongId()) {
//            tvPos.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
//            tvName.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
//            tvLength.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
//        } else {
//            Log.d(LOG_TAG, "song.getId() = " + song.getID());
//            Log.d(LOG_TAG, "Player.instance.getPlayingSongId() = " + Player.instance.getPlayingSongId());
//            tvPos.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
//            tvName.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
//            tvLength.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
//        }
//
//        view.setId(i);
//
//        view.setOnClickListener(onClickListener);
//
//        return view;
//    }
//
//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            //Toast.makeText(mContext, "Song id = " + String.valueOf(view.getId()), Toast.LENGTH_SHORT).show();
//            //mContext.startActivity(new Intent(mContext, PlayActivity.class));
//            Intent intent = new Intent();
//            intent.putExtra(PlayActivity.SONG_POS_EXTRA_RESULT, view.getId());
//            intent.putExtra(PlayActivity.SONGS_IDS_EXTRA_RESULT, mSongsIds);
//            Log.d(LOG_TAG, "Song id = " + view.getId());
//            mActivity.setResult(Activity.RESULT_OK, intent);
//            mActivity.finish();
//        }
//    };
}
