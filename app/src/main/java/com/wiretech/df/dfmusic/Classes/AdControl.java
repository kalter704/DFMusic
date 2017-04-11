package com.wiretech.df.dfmusic.Classes;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.wiretech.df.dfmusic.API.AdActivity;
import com.wiretech.df.dfmusic.API.Classes.AdResponse;
import com.wiretech.df.dfmusic.API.MusicServiceAPI;
import com.wiretech.df.dfmusic.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class AdControl {

    private int mAdCount = 0;

    private boolean isOwnAd = true;

    private boolean isAdmobEnd = false;

    private boolean isShowAd = false;

    private boolean isInApp = false;

    private int mCurrentAd = 0;

    private int mSecondsForSplashActivity = 10; // 60 = 1 minute,  replace to 600 = 10 minute

    private int mIterationTime = 100;

    private List<InterstitialAd> mInterstAdList = new ArrayList<>();

    private Context mContext = null;

    private static AdControl instance = new AdControl();

    public static AdControl getInstance() {
        return instance;
    }

    public static void newAdControlInstance() {
        instance = new AdControl();
    }

    private AdControl() {
    }

    public void setContext(Context context) {
        this.mContext = context;

        MobileAds.initialize(context, context.getString(R.string.admob_app_id));

        //mInterstitialAd = new InterstitialAd(context);
        //mInterstitialAd.setAdUnitId(getResources().getStringArray(R.array.interstitial_ad_unit_id_array)[0]);
        AdListener adListener = new AdListener() {
            @Override
            public void onAdClosed() {
                Log.d("IntersAds", "onAdClosed");
                startNewAd();
                isOwnAd = true;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                Log.d("IntersAds", "onAdFailedToLoad");
                Log.d("IntersAds", "onAdFailedToLoad ERRORCODE = " + String.valueOf(errorCode));
                if (errorCode == AdRequest.ERROR_CODE_NO_FILL || errorCode == AdRequest.ERROR_CODE_INVALID_REQUEST
                        || errorCode == AdRequest.ERROR_CODE_INTERNAL_ERROR) {
                    mInterstAdList.remove(mCurrentAd);
                    request();
                } else if (errorCode == AdRequest.ERROR_CODE_NETWORK_ERROR) {
                    startNewAd();
                }
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                Log.d("IntersAds", "onAdLeftApplication");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d("IntersAds", "onAdOpened");
            }

            @Override
            public void onAdLoaded() {
                showAd();
                Log.d("IntersAds", "onAdLoaded");
            }
        };

        String[] str_array = mContext.getResources().getStringArray(R.array.interstitial_ad_unit_id_array);
        for (int i = 0; i < str_array.length; ++i) {
            InterstitialAd interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(str_array[i]);
            interstitialAd.setAdListener(adListener);
            mInterstAdList.add(interstitialAd);
        }
    }

    public void startNewAd() {
        if (isShowAd) {
            new Thread() {
                public void run() {
                    try {
                        int splashTimer = 0;
                        while (splashTimer < (mSecondsForSplashActivity * 1000)) {
                            sleep(mIterationTime);
                            splashTimer += mIterationTime;
                        }
                        request();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void enableAds() {
        isShowAd = true;
        startNewAd();
    }

    public void disableAds() {
        isShowAd = false;
        mInterstAdList.clear();
    }

    private void request() {
        if (!isOwnAd) {
            if (mInterstAdList.size() == 0) {
                isOwnAd = true;
                isAdmobEnd = true;
                request();
                return;
            }
            if (mCurrentAd >= mInterstAdList.size()) {
                mCurrentAd = 0;
            }
            Log.d("IntersAds", "interstitial_ad_unit_id = " + mInterstAdList.get(mCurrentAd).getAdUnitId());
            Log.d("IntersAds", "request, mCurrentAd = " + String.valueOf(mCurrentAd));
            Log.d("IntersAds", "request, mAdList.size() = " + String.valueOf(mInterstAdList.size()));
            //requestNewInterstitial();
        /*
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        */

            final AdRequest adRequest = new AdRequest.Builder().build();

            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message message) {
                    mInterstAdList.get(mCurrentAd).loadAd(adRequest);
                }
            };
            Message message = mHandler.obtainMessage();
            message.sendToTarget();
        } else {
            MusicServiceAPI.interstitialAds()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponse, this::handleError);
        }
    }

    private void handleResponse(Response<AdResponse> response) {
        AdResponse adResponse = response.body();
        Intent intent = new Intent(mContext, AdActivity.class);
        intent.putExtra(AdActivity.IMG_URL_EXTRA, MusicServiceAPI.SERVER_DOMAIN + adResponse.getAds().get(mAdCount % adResponse.getCount()).getImg());
        intent.putExtra(AdActivity.URL_URL_EXTRA, adResponse.getAds().get(mAdCount % adResponse.getCount()).getUrl());
        mContext.startActivity(intent);
        mAdCount++;
    }

    private void handleError(Throwable error) {
        error.printStackTrace();
        if (!isAdmobEnd) {
            isOwnAd = false;
            request();
        } else {
            isOwnAd = true;
            startNewAd();
        }
    }

    private void showAd() {
        if (mCurrentAd >= mInterstAdList.size()) {
            return;
        }
        if (isInApp && mInterstAdList.get(mCurrentAd).isLoaded()) {
            mInterstAdList.get(mCurrentAd).show();
            mCurrentAd++;
        }
    }

    public void intoActivity() {
        isInApp = true;
        showAd();
    }

    public void outOfActivity() {
        isInApp = false;
    }
}
