/*
 * Copyright 2015 EPFL. All rights reserved.
 *
 * Created by simon.schuetz on 01 Nov 2015
 * based on SwEngHomework3 DefaultNetworkProvider class
 */

package ch.epfl.sweng.team7.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A default implementation of the {@link NetworkProvider} interface that uses
 * the mechanism available in the {@link URL} object to create
 * {@link HttpURLConnection} objects.
 */
public class DefaultNetworkProvider implements NetworkProvider {

    private final static String LOG_FLAG = "Network_DefaultNetworkProvider";
    private Context mContext = null;

    /**
     * The default constructor.
     */
    public DefaultNetworkProvider() {
    }

    /**
     * To check network availability, we need a valid context
     */
    public DefaultNetworkProvider(Context context) {
        mContext = context;
    }

    /**
     * Returns a connection to a specified URL.
     *
     * @param url a valid HTTP or HTTPS URL.
     * @return a valid {@link HttpURLConnection}
     * @throws IOException
     */
    @Override
    public HttpURLConnection getConnection(URL url) throws IOException {
        if(mContext != null && !isNetworkAvailable()) {
            throw new IOException("Network unavailable");
        }
        return (HttpURLConnection) url.openConnection();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}