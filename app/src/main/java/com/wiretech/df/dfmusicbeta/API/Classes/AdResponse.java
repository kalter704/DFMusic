package com.wiretech.df.dfmusicbeta.API.Classes;

import java.util.List;

public class AdResponse {
    private int count;
    private List<Ad> ads;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Ad> getAds() {
        return ads;
    }

    public void setAds(List<Ad> ads) {
        this.ads = ads;
    }
}
