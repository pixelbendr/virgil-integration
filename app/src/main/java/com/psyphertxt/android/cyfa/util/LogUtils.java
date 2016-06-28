package com.psyphertxt.android.cyfa.util;

import android.util.Log;

import com.orhanobut.logger.Logger;
import com.psyphertxt.android.cyfa.Config;

public class LogUtils {

    public static void debug(String tag, String msg) {
        if (!Config.IS_PROD) {
            Log.d(tag, msg);
        }
    }

    public static void debug(String message, Object... args) {
        if (!Config.IS_PROD) {
            Logger.d(message, args);
        }
    }
}
