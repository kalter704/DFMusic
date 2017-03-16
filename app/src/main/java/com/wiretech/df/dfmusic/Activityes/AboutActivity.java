package com.wiretech.df.dfmusic.Activityes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wiretech.df.dfmusic.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String clubName = getIntent().getStringExtra("club_name");

        initializeUI(clubName);

        findViewById(R.id.rlBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.rlShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void initializeUI(String clubName) {
        String name = "";
        String since = "";
        String location = "";
        String peoples = "";
        String site = "";
        String instagram = "";
        String vk = "";
        String youtube = "";
        if ("BLACK VELVET".equals(clubName)) {
            name = getString(R.string.info_page_bv);
            since = getString(R.string.info_page_bv_since);
            location = getString(R.string.info_page_bv_location);
            peoples = getString(R.string.info_page_bv_peoples);
            site = getString(R.string.info_page_bv_site);
            instagram = getString(R.string.info_page_bv_intagram);
            vk = getString(R.string.info_page_bv_vk);
            youtube = getString(R.string.info_page_bv_youtube);
        } else if ("CRAZY DREAM".equals(clubName)) {
            name = getString(R.string.info_page_cd);
            since = getString(R.string.info_page_cd_since);
            location = getString(R.string.info_page_cd_location);
            peoples = getString(R.string.info_page_cd_peoples);
            site = getString(R.string.info_page_cd_site);
            instagram = getString(R.string.info_page_cd_intagram);
            vk = getString(R.string.info_page_cd_vk);
            youtube = getString(R.string.info_page_cd_youtube);
        } else if ("DANCE FAMILY LADIES".equals(clubName)) {
            name = getString(R.string.info_page_dfl);
            since = getString(R.string.info_page_dfl_since);
            location = getString(R.string.info_page_dfl_location);
            peoples = getString(R.string.info_page_dfl_peoples);
            site = getString(R.string.info_page_dfl_site);
            instagram = getString(R.string.info_page_dfl_intagram);
            vk = getString(R.string.info_page_dfl_vk);
            youtube = getString(R.string.info_page_dfl_youtube);
        } else if ("FLASH LIGHT".equals(clubName)) {
            name = getString(R.string.info_page_fl);
            since = getString(R.string.info_page_fl_since);
            location = getString(R.string.info_page_fl_location);
            peoples = getString(R.string.info_page_fl_peoples);
            site = getString(R.string.info_page_fl_site);
            instagram = getString(R.string.info_page_fl_intagram);
            vk = getString(R.string.info_page_fl_vk);
            youtube = getString(R.string.info_page_fl_youtube);
        } else if ("FREE STEPS".equals(clubName)) {
            name = getString(R.string.info_page_fs);
            since = getString(R.string.info_page_fs_since);
            location = getString(R.string.info_page_fs_location);
            peoples = getString(R.string.info_page_fs_peoples);
            site = getString(R.string.info_page_fs_site);
            instagram = getString(R.string.info_page_fs_intagram);
            vk = getString(R.string.info_page_fs_vk);
            youtube = getString(R.string.info_page_fs_youtube);
        } else if ("JUST DANCE".equals(clubName)) {
            name = getString(R.string.info_page_jd);
            since = getString(R.string.info_page_jd_since);
            location = getString(R.string.info_page_jd_location);
            peoples = getString(R.string.info_page_jd_peoples);
            site = getString(R.string.info_page_jd_site);
            instagram = getString(R.string.info_page_jd_intagram);
            vk = getString(R.string.info_page_jd_vk);
            youtube = getString(R.string.info_page_jd_youtube);
        } else if ("LUCKY JAM".equals(clubName)) {
            name = getString(R.string.info_page_lj);
            since = getString(R.string.info_page_lj_since);
            location = getString(R.string.info_page_lj_location);
            peoples = getString(R.string.info_page_lj_peoples);
            site = getString(R.string.info_page_lj_site);
            instagram = getString(R.string.info_page_lj_intagram);
            vk = getString(R.string.info_page_lj_vk);
            youtube = getString(R.string.info_page_lj_youtube);
        } else if ("MY COMMUNITY".equals(clubName)) {
            name = getString(R.string.info_page_mc);
            since = getString(R.string.info_page_mc_since);
            location = getString(R.string.info_page_mc_location);
            peoples = getString(R.string.info_page_mc_peoples);
            site = getString(R.string.info_page_mc_site);
            instagram = getString(R.string.info_page_mc_intagram);
            vk = getString(R.string.info_page_mc_vk);
            youtube = getString(R.string.info_page_mc_youtube);
        } else if ("UNISTREAM".equals(clubName)) {
            name = getString(R.string.info_page_u);
            since = getString(R.string.info_page_u_since);
            location = getString(R.string.info_page_u_location);
            peoples = getString(R.string.info_page_u_peoples);
            site = getString(R.string.info_page_u_site);
            instagram = getString(R.string.info_page_u_intagram);
            vk = getString(R.string.info_page_u_vk);
            youtube = getString(R.string.info_page_u_youtube);
        }

        ((TextView) findViewById(R.id.tvTitle)).setText(clubName);
        ((TextView) findViewById(R.id.tvName)).setText(name);
        ((TextView) findViewById(R.id.tvSince)).setText(since);
        ((TextView) findViewById(R.id.tvLocation)).setText(location);
        ((TextView) findViewById(R.id.tvPeople)).setText(peoples);
        ((TextView) findViewById(R.id.tvSite)).setText(site);
        ((TextView) findViewById(R.id.tvInst)).setText(instagram);
        ((TextView) findViewById(R.id.tvVK)).setText(vk);
        ((TextView) findViewById(R.id.tvYoutube)).setText(youtube);

    }
}
