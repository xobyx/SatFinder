package com.xobyx.satfinder.toolnet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
    public static boolean isConnected(Context arg5) {
        ConnectivityManager v5 = (ConnectivityManager)arg5.getSystemService("connectivity");
        if(v5 != null) {
            NetworkInfo[] v5_1 = v5.getAllNetworkInfo();
            if(v5_1 != null) {
                int v2;
                for(v2 = 0; v2 < v5_1.length; ++v2) {
                    if(v5_1[v2].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}

