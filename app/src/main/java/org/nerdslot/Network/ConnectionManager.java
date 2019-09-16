package org.nerdslot.Network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;
import org.nerdslot.Foundation.Nerdslot;

public class ConnectionManager {
    private static final String TAG = "connection-tag";
    private static Context _context = Nerdslot.getContext();

    public ConnectionManager() {
        //
    }

    public static int getConnectionStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        @SuppressLint("MissingPermission") NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return 1;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return 2;
            }
        }
        return 0;
    }

    /**
     * Checks for possible Internet Connections
     * like WiFi, Roaming, WiFi Direct etc.
     */
    public static boolean isConnectionAvailable(@NotNull Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            @SuppressLint("MissingPermission") NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo netInfo : info) {
                    if (netInfo.getState() == NetworkInfo.State.CONNECTED) return true;
                }
            }
        }

        return false;
    }
}
