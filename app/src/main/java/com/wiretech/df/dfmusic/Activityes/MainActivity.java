package com.wiretech.df.dfmusic.Activityes;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.Adapters.PlayListsAdapter;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;
import com.wiretech.df.dfmusic.Services.MusicNotificationService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<PlayList> mPlayLists = new ArrayList<>();
    private PlayListsAdapter mPlayListAdapter;
    private ListView mListView;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBManager.with(this);

        fillDate();

        initializeUI();

        AdControl.getInstance().enableAds();
    }

    private void fillDate() {
        /*
        String[] clubs = getResources().getStringArray(R.array.clubs);
        List<PlayList> playListsNamesAndIds = DBManager.getPlayListsNames();
        for (int i = 0; (i < clubs.length) && (i < playListsNamesAndIds.size()); ++i) {
            mPlayLists.add(new PlayList(playListsNamesAndIds.get(i).getId(), playListsNamesAndIds.get(i).getName(), clubs[i]));
        }
        */
        mPlayLists = DBManager.getPlayListsWithNameAndSchool();
    }

    private void initializeUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayListAdapter = new PlayListsAdapter(this, mPlayLists);

        mListView = (ListView) findViewById(R.id.lvMain);
        mListView.setAdapter(mPlayListAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MenuActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdControl.getInstance().intoActivity();
        mPlayListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AdControl.getInstance().outOfActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent stopIntent = new Intent(this, MusicNotificationService.class);
        stopIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        try {
            pStopIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
        AdControl.getInstance().disableAds();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Snackbar snackbar = Snackbar.make(
                    findViewById(R.id.mainCoordLayout),
                    "Нажмите еще раз для выхода",
                    Snackbar.LENGTH_SHORT);
            View snackView = snackbar.getView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snackView.setBackgroundColor(getColor(R.color.snackErrorNetworkColor));
            } else {
                snackView.setBackgroundColor(getResources().getColor(R.color.snackErrorNetworkColor));
            }
            TextView snackTV = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
            snackTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snackbar.show();
            //Toast.makeText(getBaseContext(), "Нажмите еще раз для выхода", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
