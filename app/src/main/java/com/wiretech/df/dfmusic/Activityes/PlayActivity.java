package com.wiretech.df.dfmusic.Activityes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.wiretech.df.dfmusic.R;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SONG_ID_EXTRA_RESULT = "song_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initializeUI();

    }

    private void initializeUI() {
        findViewById(R.id.rlBack).setOnClickListener(this);
        findViewById(R.id.rlPlaylist).setOnClickListener(this);

        findViewById(R.id.rlSave).setOnClickListener(this);
        findViewById(R.id.rlUnSave).setOnClickListener(this);
        findViewById(R.id.rlPreviousSong).setOnClickListener(this);
        findViewById(R.id.rlPause).setOnClickListener(this);
        findViewById(R.id.rlPlay).setOnClickListener(this);
        findViewById(R.id.rlNextSong).setOnClickListener(this);
        findViewById(R.id.rlRepeatOn).setOnClickListener(this);
        findViewById(R.id.rlRepeatOff).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlBack:
                finish();
                break;
            case R.id.rlPlaylist:
                startActivityForResult(new Intent(PlayActivity.this, PlayListActivity.class), 1);
                break;
            case R.id.rlSave:

                break;
            case R.id.rlUnSave:

                break;
            case R.id.rlPreviousSong:

                break;
            case R.id.rlPause:

                break;
            case R.id.rlPlay:

                break;
            case R.id.rlNextSong:

                break;
            case R.id.rlRepeatOn:

                break;
            case R.id.rlRepeatOff:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Toast.makeText(PlayActivity.this, "Song id = " + data.getIntExtra(SONG_ID_EXTRA_RESULT, -1), Toast.LENGTH_SHORT).show();
        }
    }
}
