package com.wiretech.df.dfmusic.classes;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkConnection {

    public static boolean hasConnectionToNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

}
