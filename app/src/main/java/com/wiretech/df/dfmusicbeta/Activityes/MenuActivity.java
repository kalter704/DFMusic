package com.wiretech.df.dfmusicbeta.Activityes;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.wiretech.df.dfmusicbeta.Activityes.menuactivities.DFClubsActivity;
import com.wiretech.df.dfmusicbeta.Activityes.menuactivities.StudioActivity;
import com.wiretech.df.dfmusicbeta.Activityes.menuactivities.SupportProjectActivity;
import com.wiretech.df.dfmusicbeta.Classes.AdControl;
import com.wiretech.df.dfmusicbeta.Classes.Share;
import com.wiretech.df.dfmusicbeta.DataBase.DBManager;
import com.wiretech.df.dfmusicbeta.R;

import java.util.ArrayList;

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
            ArrayList<Integer> songsIds = DBManager.getSavedSongsIds();
            if (songsIds.size() != 0) {
                Intent intent = new Intent(MenuActivity.this, PlayActivity.class);
                intent.putExtra(PlayActivity.SAVED_SONG_EXTRA, true);
                startActivity(intent);
            } else {
                Snackbar snackbar = Snackbar.make(
                        findViewById(R.id.mainCoordLayout),
                        getString(R.string.snack_have_not_saved_songs),
                        Snackbar.LENGTH_SHORT);
                View snackView = snackbar.getView();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    snackView.setBackgroundColor(getColor(R.color.snackNotificationColor));
                } else {
                    snackView.setBackgroundColor(getResources().getColor(R.color.snackNotificationColor));
                }
                TextView snackTV = (TextView) snackView.findViewById(android.support.design.R.id.snackbar_text);
                snackTV.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                snackbar.show();
            }
        });

        DBManager.writeToLogDataFromTables();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
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
