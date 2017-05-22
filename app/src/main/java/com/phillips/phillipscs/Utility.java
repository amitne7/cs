package com.phillips.phillipscs;

import android.content.Context;
import android.net.ConnectivityManager;

import java.lang.ref.WeakReference;

/**
 * Created by surbhidhingra on 5/9/2016.
 */
public class Utility {

    public static boolean isNetworkAvailable(WeakReference<Context> context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.get().getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
