package com.psyphertxt.android.cyfa.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This Convenient class is responsible for checking and validation network connectivity and
 * connectivity problems.
 */
public class NetworkUtils {

    public static final String KEY_RESPONSE_CODE = "responseCode";
    public static final String KEY_RESPONSE_MESSAGE = "responseMessage";
    public static final int RESPONSE_OK = 0;
    public static final int SERVER_ERROR = 1;
    public static final int UNKNOWN_ERROR = -1;
    public static final int TEXT_MESSAGE_ERROR = 2;
    public static final int TOKEN_ERROR = 3;
    public static final int HASH_ERROR = 4;
    public static final int CONTACTS_ERROR = 5;
    public static final int SETUP_USER_ERROR = 6;


    //lets check if the user has an active network and if the network is available
    public static boolean isAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //TODO write a method to return type of network and also if the user has data enabled
}
