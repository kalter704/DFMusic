package com.wiretech.df.dfmusic.Activityes;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.wiretech.df.dfmusic.API.Classes.PlayList;
import com.wiretech.df.dfmusic.Adapters.PlayListsAdapter;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.Const;
import com.wiretech.df.dfmusic.DataBase.DBManager;
import com.wiretech.df.dfmusic.R;
import com.wiretech.df.dfmusic.Services.MusicNotificationService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<PlayList> mPlayLists = new ArrayList<>();
    PlayListsAdapter mPlayListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fillDate();

        initializeUI();
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

        ((ListView) findViewById(R.id.lvMain)).setAdapter(mPlayListAdapter);

        (findViewById(R.id.rlMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MenuActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdControl.getInstance().intoActivity();
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
}
