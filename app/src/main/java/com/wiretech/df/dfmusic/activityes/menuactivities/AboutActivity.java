package com.wiretech.df.dfmusic.activityes.menuactivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wiretech.df.dfmusic.classes.Share;
import com.wiretech.df.dfmusic.R;

public class AboutActivity extends AppCompatActivity {

    private TextView mTvSite;
    private TextView mTvIns;
    private TextView mTvVK;
    //private TextView mTvYoutube;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String clubName = getIntent().getStringExtra("club_name");

        initializeUI(clubName);

        findViewById(R.id.rlBack).setOnClickListener(view -> finish());

        findViewById(R.id.rlShare).setOnClickListener(v -> Share.share(AboutActivity.this, getString(R.string.text_for_share_for_clubs) + "\n" + getString(R.string.link_to_app)));

    }

    private void initializeUI(String clubName) {
        String name = "";
        String since = "";
        String location = "";
        String peoples = "";
        String site = "";
        String instagram = "";
        String vk = "";
        //String youtube = "";
        if ("BLACK VELVET".equals(clubName)) {
            name = getString(R.string.info_page_bv);
            since = getString(R.string.info_page_bv_since);
            location = getString(R.string.info_page_bv_location);
            peoples = getString(R.string.info_page_bv_peoples);
            site = getString(R.string.info_page_bv_site);
            instagram = getString(R.string.info_page_bv_intagram);
            vk = getString(R.string.info_page_bv_vk);
            //youtube = getString(R.string.info_page_bv_youtube);
        } else if ("CRAZY DREAM".equals(clubName)) {
            name = getString(R.string.info_page_cd);
            since = getString(R.string.info_page_cd_since);
            location = getString(R.string.info_page_cd_location);
            peoples = getString(R.string.info_page_cd_peoples);
            site = getString(R.string.info_page_cd_site);
            instagram = getString(R.string.info_page_cd_intagram);
            vk = getString(R.string.info_page_cd_vk);
            //youtube = getString(R.string.info_page_cd_youtube);
        } else if ("DANCE FAMILY LADIES".equals(clubName)) {
            name = getString(R.string.info_page_dfl);
            since = getString(R.string.info_page_dfl_since);
            location = getString(R.string.info_page_dfl_location);
            peoples = getString(R.string.info_page_dfl_peoples);
            site = getString(R.string.info_page_dfl_site);
            instagram = getString(R.string.info_page_dfl_intagram);
            vk = getString(R.string.info_page_dfl_vk);
            //youtube = getString(R.string.info_page_dfl_youtube);
        } else if ("FLASH LIGHT".equals(clubName)) {
            name = getString(R.string.info_page_fl);
            since = getString(R.string.info_page_fl_since);
            location = getString(R.string.info_page_fl_location);
            peoples = getString(R.string.info_page_fl_peoples);
            site = getString(R.string.info_page_fl_site);
            instagram = getString(R.string.info_page_fl_intagram);
            vk = getString(R.string.info_page_fl_vk);
            //youtube = getString(R.string.info_page_fl_youtube);
        } else if ("FREE STEPS".equals(clubName)) {
            name = getString(R.string.info_page_fs);
            since = getString(R.string.info_page_fs_since);
            location = getString(R.string.info_page_fs_location);
            peoples = getString(R.string.info_page_fs_peoples);
            site = getString(R.string.info_page_fs_site);
            instagram = getString(R.string.info_page_fs_intagram);
            vk = getString(R.string.info_page_fs_vk);
            //youtube = getString(R.string.info_page_fs_youtube);
        } else if ("JUST DANCE".equals(clubName)) {
            name = getString(R.string.info_page_jd);
            since = getString(R.string.info_page_jd_since);
            location = getString(R.string.info_page_jd_location);
            peoples = getString(R.string.info_page_jd_peoples);
            site = getString(R.string.info_page_jd_site);
            instagram = getString(R.string.info_page_jd_intagram);
            vk = getString(R.string.info_page_jd_vk);
            //youtube = getString(R.string.info_page_jd_youtube);
        } else if ("Leader Dance".equals(clubName)) {
            name = getString(R.string.info_page_ld);
            since = getString(R.string.info_page_ld_since);
            location = getString(R.string.info_page_ld_location);
            peoples = getString(R.string.info_page_ld_peoples);
            site = getString(R.string.info_page_ld_site);
            instagram = getString(R.string.info_page_ld_intagram);
            vk = getString(R.string.info_page_ld_vk);
            //youtube = getString(R.string.info_page_ld_youtube);
        } else if ("LUCKY JAM".equals(clubName)) {
            name = getString(R.string.info_page_lj);
            since = getString(R.string.info_page_lj_since);
            location = getString(R.string.info_page_lj_location);
            peoples = getString(R.string.info_page_lj_peoples);
            site = getString(R.string.info_page_lj_site);
            instagram = getString(R.string.info_page_lj_intagram);
            vk = getString(R.string.info_page_lj_vk);
            //youtube = getString(R.string.info_page_lj_youtube);
        } else if ("MY COMMUNITY".equals(clubName)) {
            name = getString(R.string.info_page_mc);
            since = getString(R.string.info_page_mc_since);
            location = getString(R.string.info_page_mc_location);
            peoples = getString(R.string.info_page_mc_peoples);
            site = getString(R.string.info_page_mc_site);
            instagram = getString(R.string.info_page_mc_intagram);
            vk = getString(R.string.info_page_mc_vk);
            //youtube = getString(R.string.info_page_mc_youtube);
        } else if ("UNISTREAM".equals(clubName)) {
            name = getString(R.string.info_page_u);
            since = getString(R.string.info_page_u_since);
            location = getString(R.string.info_page_u_location);
            peoples = getString(R.string.info_page_u_peoples);
            site = getString(R.string.info_page_u_site);
            instagram = getString(R.string.info_page_u_intagram);
            vk = getString(R.string.info_page_u_vk);
            //youtube = getString(R.string.info_page_u_youtube);
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(clubName);
        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvSince)).setText(since);
        ((TextView) findViewById(R.id.tvLocation)).setText(location);
        ((TextView) findViewById(R.id.tvPeople)).setText(peoples);

        mTvSite = (TextView) findViewById(R.id.tvSite);
        mTvIns = (TextView) findViewById(R.id.tvInst);
        mTvVK = (TextView) findViewById(R.id.tvVK);
        //mTvYoutube = (TextView) findViewById(R.id.tvYoutube);

        mTvSite.setText(site);
        mTvIns.setText(instagram);
        mTvVK.setText(vk);
        //mTvYoutube.setText(youtube);

        mTvSite.setOnClickListener(v -> {
            if (!"".equals(((TextView) v).getText())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http:/" + ((TextView) v).getText())));
            }
        });

        mTvIns.setOnClickListener(v -> {
            if (!"".equals(((TextView) v).getText())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com" + ((TextView) v).getText())));
            }
        });

        mTvVK.setOnClickListener(v -> {
            if (!"".equals(((TextView) v).getText())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com" + ((TextView) v).getText())));
            }
        });

        /*
        mTvYoutube.setOnClickListener(v -> {
            if (!"".equals(((TextView) v).getText())) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com" + ((TextView) v).getText())));
            }
        });
        */




    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
