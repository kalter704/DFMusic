package com.wiretech.df.dfmusic.API;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wiretech.df.dfmusic.Classes.AdControl;
import com.wiretech.df.dfmusic.R;

public class AdActivity extends AppCompatActivity {

    public static final String LOG_TAG = "AdActivity";

    public static final String IMG_URL_EXTRA = "img_url_extra";
    public static final String URL_URL_EXTRA = "url_url_extra";

    private String mImgUrl = null;
    private String mUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        mImgUrl = getIntent().getStringExtra(IMG_URL_EXTRA);
        Log.d(LOG_TAG, "mImgUrl = " + mImgUrl);
        mUrl = getIntent().getStringExtra(URL_URL_EXTRA);
        Log.d(LOG_TAG, "mUrl = " + mUrl);


        ImageView imageView = (ImageView) findViewById(R.id.imgView);
        imageView.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mUrl))));

        Picasso.with(this)
                .load(mImgUrl)
                .into(imageView);

        findViewById(R.id.imageButton).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        AdControl.getInstance().startNewAd();
        super.onDestroy();
    }
}
