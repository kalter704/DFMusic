package com.wiretech.df.dfmusic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wiretech.df.dfmusic.Activityes.AboutCrazyDreamActivity;
import com.wiretech.df.dfmusic.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ClubsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mClubs;
    private LayoutInflater mLayoutInflater;

    public ClubsAdapter(Context context, ArrayList<String> clubs) {
        mContext = context;
        mClubs = clubs;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mClubs.size();
    }

    @Override
    public Object getItem(int i) {
        return mClubs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.clubs_item_list, viewGroup, false);
        }

        ((TextView) view.findViewById(R.id.tvPos)).setText(String.valueOf(i + 1));
        ((TextView) view.findViewById(R.id.tvName)).setText(mClubs.get(i));
        view.setId(i);
        view.setOnClickListener(onClickListener);

        return view;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(mContext, AboutCrazyDreamActivity.class);
            switch (view.getId()) {
                case 0:

                    break;
            }
            mContext.startActivity(intent);
        }
    };

}
