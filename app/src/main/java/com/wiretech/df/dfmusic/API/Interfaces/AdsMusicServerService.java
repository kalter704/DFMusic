package com.wiretech.df.dfmusic.API.Interfaces;

import com.wiretech.df.dfmusic.API.Classes.AdResponse;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;

public interface AdsMusicServerService {
    @GET("getinterstitialads")
    Observable<Response<AdResponse>> getInterstitialAds();
}
