package com.wiretech.df.dfmusic.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wiretech.df.dfmusic.activityes.PlaylistActivity;
import com.wiretech.df.dfmusic.api.classes.Playlist;
import com.wiretech.df.dfmusic.classes.NetworkConnection;
import com.wiretech.df.dfmusic.classes.Player;
import com.wiretech.df.dfmusic.classes.PlayerManager;
import com.wiretech.df.dfmusic.classes.SnackBarCreator;
import com.wiretech.df.dfmusic.R;

import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<PlaylistsAdapter.PlaylistHolder> {

    private List<Playlist> mPlayLists;
    private Context mContext;
    private PlayListBackground mPlaylistBackground;

    public PlaylistsAdapter(Context context, List<Playlist> playLists) {
        mContext = context;
        mPlayLists = playLists;
        mPlaylistBackground = new PlayListBackground();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder {

        private int mID;
        private TextView mPlaylistName;
        private View mBackgroundView;

        public PlaylistHolder(View itemView) {
            super(itemView);
            mPlaylistName = itemView.findViewById(R.id.tvSchoolName);
            mBackgroundView = itemView.findViewById(R.id.rlBackground);
            itemView.setOnClickListener(view -> {
                if (NetworkConnection.hasConnectionToNetwork(mContext)) {
                    mContext.startActivity(PlaylistActivity.newIntent(mContext, mID));
                } else {
                    SnackBarCreator.show(mContext, R.string.snack_error_network);
                }
            });
        }

        public void bindPlaylist(Playlist p, int drawableID) {
            mID = p.getID();
            mPlaylistName.setText(p.getName());
            mBackgroundView.setBackgroundResource(drawableID);
            if ((Player.get().getState() == Player.PlayerState.PLAYING
                    || Player.get().getState() == Player.PlayerState.PREPARING
                    || Player.get().getState() == Player.PlayerState.PAUSE)
                       && PlayerManager.get().getPlayingPlaylist() != null
                       && PlayerManager.get().getPlayingPlaylist().getID() == mID) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mPlaylistName.setTextColor(mContext.getColor(R.color.textColorRed));
                } else {
                    mPlaylistName.setTextColor(mContext.getResources().getColor(R.color.textColorRed));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mPlaylistName.setTextColor(mContext.getColor(R.color.textColorPrimary));
                } else {
                    mPlaylistName.setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
                }
            }
        }
    }

    @Override
    public PlaylistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PlaylistHolder(LayoutInflater.from(mContext).inflate(R.layout.main_item_list, parent,false));
    }

    @Override
    public void onBindViewHolder(PlaylistHolder holder, int position) {
        holder.bindPlaylist(mPlayLists.get(position), mPlaylistBackground.getBackgroundIdRes(position));
    }

    @Override
    public int getItemCount() {
        return mPlayLists.size();
    }


    private class PlayListBackground {
        private static final String SAVED_INDEX = "saved_index";

        private int index;
        private int backgrounds[] = {
                R.drawable.p_1,
                R.drawable.p_2,
                R.drawable.p_3,
                R.drawable.p_4,
                R.drawable.p_5,
                R.drawable.p_6,
                R.drawable.p_7,
                R.drawable.p_8,
                R.drawable.p_9,
                R.drawable.p_10,
                R.drawable.p_11,
                R.drawable.p_12,
                R.drawable.p_13,
                R.drawable.p_14,
                R.drawable.p_15,
                R.drawable.p_16,
                R.drawable.p_17
        };

        public PlayListBackground() {
            SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(mContext);

            index = sPref.getInt(SAVED_INDEX, 0);

            if (index < 0) {
                index = backgrounds.length - 1;
            }

            sPref.edit()
                    .putInt(SAVED_INDEX, index - 1)
                    .apply();
        }

        public int getBackgroundIdRes(int i) {
            return backgrounds[(index + i) % backgrounds.length];
        }
    }
}

//public class PlaylistsAdapter extends BaseAdapter {
//    Context mContext;
//    LayoutInflater mLayoutInflater;
//    ArrayList<Playlist> mPlayLists;
//    PlayListBackground mPlayListBackground;
//
//    public PlaylistsAdapter(Context context, ArrayList<Playlist> playLists) {
//        this.mContext = context;
//        this.mPlayLists = playLists;
//        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mPlayListBackground = new PlayListBackground();
//    }
//
//    @Override
//    public int getCount() {
//        return mPlayLists.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return mPlayLists.get(i);
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
//            view = mLayoutInflater.inflate(R.layout.main_item_list, viewGroup, false);
//        }
//        Playlist playList = mPlayLists.get(i);
//
//        ((TextView) view.findViewById(R.id.tvSchoolName)).setText(playList.getSchoolName());
//        //((TextView) view.findViewById(R.id.tvPlayList)).setText(playList.getName());
//
//        if (Player.instance.getIsPlaying() && (playList.getId() == DBManager.getPlayListBySongId(Player.instance.getPlayingSongId()).getId())) {
//            //((TextView) view.findViewById(R.id.tvSchoolName)).setTextColor(mContext.getResources().getColor(R.color.textColorRed));
//            ((TextView) view.findViewById(R.id.tvPlayList)).setTextColor(mContext.getResources().getColor(R.color.textColorRed));
//        } else {
//            //((TextView) view.findViewById(R.id.tvSchoolName)).setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
//            ((TextView) view.findViewById(R.id.tvPlayList)).setTextColor(mContext.getResources().getColor(R.color.textColorPrimary));
//        }
//
//        view.setId(playList.getId());
//        view.setTag(i);
//
//        view.setOnClickListener(onClickListener);
//
//        view.setBackground(mContext.getResources().getDrawable(mPlayListBackground.getBackgroundIdRes(i)));
//
//        return view;
//    }
//
//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            if (NetworkConnection.hasConnectionToNetwork(mContext)) {
//                Intent intent = new Intent(mContext, PlayActivity.class);
//                intent.putExtra(PlayActivity.PLAYLIST_ID_EXTRA, view.getId());
//                intent.putExtra(PlayActivity.PLAYLIST_NUMBER_EXTRA, Integer.valueOf((int) view.getTag()));
//                mContext.startActivity(intent);
//            } else {
//                Snackbar snackbar = Snackbar.make(
//                        ((Activity) mContext).findViewById(R.id.mainCoordLayout),
//                        mContext.getString(R.string.snack_error_network),
//                        Snackbar.LENGTH_SHORT);
//                View snackView = snackbar.getView();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    snackView.setBackgroundColor(mContext.getColor(R.color.snackErrorNetworkColor));
//                } else {
//                    snackView.setBackgroundColor(mContext.getResources().getColor(R.color.snackErrorNetworkColor));
//                }
//                TextView snackTV = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
//                snackTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                snackbar.show();
//            }
//        }
//    };
//
//    private class PlayListBackground {
//        private static final String BACKGROUND_PREF = "playlists_background_pref";
//        private static final String SAVED_INDEX = "saved_index";
//
//        private int index;
//        private int backgrounds[] = {
//                R.drawable.p_1,
//                R.drawable.p_2,
//                R.drawable.p_3,
//                R.drawable.p_4,
//                R.drawable.p_5,
//                R.drawable.p_6,
//                R.drawable.p_7,
//                R.drawable.p_8,
//                R.drawable.p_9,
//                R.drawable.p_10,
//                R.drawable.p_11,
//                R.drawable.p_12,
//                R.drawable.p_13,
//                R.drawable.p_14,
//                R.drawable.p_15,
//                R.drawable.p_16,
//                R.drawable.p_17
//        };
//
//        public PlayListBackground() {
//            SharedPreferences sPref = mContext.getSharedPreferences(BACKGROUND_PREF, Context.MODE_PRIVATE);
//
//            index = sPref.getInt(SAVED_INDEX, 0);
//
//            if (index < 0) {
//                index = backgrounds.length - 1;
//            }
//
//            SharedPreferences.Editor editor = sPref.edit();
//            editor.putInt(SAVED_INDEX, index - 1);
//            editor.apply();
//        }
//
//        public int getBackgroundIdRes(int i) {
//            return backgrounds[(index + i) % backgrounds.length];
//        }
//    }
//}
