package com.wiretech.df.dfmusicbeta.API.Interfaces;

import com.wiretech.df.dfmusicbeta.API.Classes.AdResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface AdsMusicServerService {
    @GET("getinterstitialads")
    Observable<Response<AdResponse>> getInterstitialAds();

    @GET("getbannerads")
    Observable<Response<AdResponse>> getBannerAds();
}
