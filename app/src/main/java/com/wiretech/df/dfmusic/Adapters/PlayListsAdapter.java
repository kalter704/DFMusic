package com.wiretech.df.dfmusic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wiretech.df.dfmusic.Activityes.PlayActivity;
import com.wiretech.df.dfmusic.Activityes.PlayListActivity;
import com.wiretech.df.dfmusic.Classes.PlayList;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;

public class PlayListsAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PlayList> playLists;

    public PlayListsAdapter(Context context, ArrayList<PlayList> playLists) {
        this.context = context;
        this.playLists = playLists;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return playLists.size();
    }

    @Override
    public Object getItem(int i) {
        return playLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.main_item_list, viewGroup, false);
        }
        PlayList playList = playLists.get(i);

        ((TextView) view.findViewById(R.id.tvSchoolName)).setText(playList.getSchoolName());
        ((TextView) view.findViewById(R.id.tvPlayList)).setText(playList.getName());

        view.setId(playList.getId());

        view.setOnClickListener(onClickListener);

        // Временные фоны
        int drawableId = 0;
        switch (i % 3) {
            case 0:
                drawableId = R.drawable.tempback2;
                break;
            case 1:
                drawableId = R.drawable.tempback3;
                break;
            case 2:
                drawableId = R.drawable.tempback1;
                break;
        }

        view.setBackground(context.getResources().getDrawable(drawableId));

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, PlayActivity.class);
            intent.putExtra(PlayListActivity.PLAYLIST_ID_EXTRA, view.getId());
            context.startActivity(intent);
        }
    };
}
