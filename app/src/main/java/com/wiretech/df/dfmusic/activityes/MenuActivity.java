package com.wiretech.df.dfmusic.activityes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wiretech.df.dfmusic.activityes.menuactivities.DFClubsActivity;
import com.wiretech.df.dfmusic.activityes.menuactivities.StudioActivity;
import com.wiretech.df.dfmusic.activityes.menuactivities.SupportProjectActivity;
import com.wiretech.df.dfmusic.classes.Share;
import com.wiretech.df.dfmusic.classes.SnackBarCreator;
import com.wiretech.df.dfmusic.database.DBManager;
import com.wiretech.df.dfmusic.R;

import java.util.List;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        findViewById(R.id.rlBack).setOnClickListener(this);
        findViewById(R.id.rlShare).setOnClickListener(this);
        findViewById(R.id.tlAbout).setOnClickListener(this);
        findViewById(R.id.rlRating).setOnClickListener(this);
        findViewById(R.id.rlDonate).setOnClickListener(this);
        findViewById(R.id.rlWrite).setOnClickListener(this);

        findViewById(R.id.rlSavedAudio).setOnClickListener(v -> {
            List<Integer> songsIds = DBManager.get(this).getSavedSongsIds();
            if (songsIds.size() != 0) {
                startActivity(PlaylistActivity.newIntent(this, -1));
            } else {
                SnackBarCreator.show(this, R.string.snack_have_not_saved_songs);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                overridePendingTransition(R.anim.alpha_in, R.anim.left_out);
                break;
            case R.id.rlShare:
                Share.share(MenuActivity.this, getString(R.string.text_for_share_for_friends) + "\n" + getString(R.string.link_to_app));
                break;
            case R.id.tlAbout:
                startActivity(new Intent(MenuActivity.this, DFClubsActivity.class));
                break;
            case R.id.rlRating:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.link_to_app))));
                break;
            case R.id.rlDonate:
                startActivity(new Intent(MenuActivity.this, SupportProjectActivity.class));
                break;
            case R.id.rlWrite:
                startActivity(new Intent(MenuActivity.this, StudioActivity.class));
                break;
        }
    }
}
