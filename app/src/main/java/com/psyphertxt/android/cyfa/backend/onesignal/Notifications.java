package com.psyphertxt.android.cyfa.backend.onesignal;

import android.app.Activity;
import android.content.Context;

import com.onesignal.OneSignal;
import com.parse.ParseCloud;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.Backend;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import java.util.HashMap;

/**
 * This Class is responsive for handling push notifications, in app and on
 * the device's notification center
 * check interface for explanation of how it works
 */
public class Notifications implements Backend.Notifications {

    public void subscribe(Context context) {
        OneSignal.init((Activity) context,
                context.getString(R.string.googleProjectNumber),
                context.getString(R.string.oneSignalAppId),new NotificationOpenedHandler());
    }

    public void validate(final CallbackListener.callbackForResults callbackForResults) {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                if (userId != null && registrationId != null) {
                    callbackForResults.success(userId);
                } else {
                    callbackForResults.success(Config.EMPTY_STRING);
                }
            }
        });
    }

    @Override
    public void send(String playerId, String message) {
        HashMap<String, Object> params = new HashMap<>();
        params.put(Config.KEY_TITLE, User.getDeviceUser().getProfileName());
        params.put(Config.KEY_MESSAGE, message);
        params.put(Config.KEY_PLAYER_ID, playerId);
        params.put(Config.KEY_USER_ID, User.getDeviceUserId());
        ParseCloud.callFunctionInBackground(Config.DEFINE_NOTIFY_USER, params);
    }
}
