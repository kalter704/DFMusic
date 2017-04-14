package com.wiretech.df.dfmusic.Activityes.menuactivities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

import com.wiretech.df.dfmusic.Adapters.ClubsAdapter;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.R;

public class DFClubsActivity extends AppCompatActivity {

    private String[] mClubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfclubs);

        mClubs = getResources().getStringArray(R.array.clubs);

        ClubsAdapter clubsAdapter = new ClubsAdapter(this, mClubs);

        ((ListView) findViewById(R.id.listView)).setAdapter(clubsAdapter);

        findViewById(R.id.rlBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}
