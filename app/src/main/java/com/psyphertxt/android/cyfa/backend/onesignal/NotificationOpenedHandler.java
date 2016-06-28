package com.psyphertxt.android.cyfa.backend.onesignal;

import com.onesignal.OneSignal;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.MainApplication;
import com.psyphertxt.android.cyfa.ui.activity.ChatActivity;
import com.psyphertxt.android.cyfa.ui.activity.StatusActivity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;

import de.greenrobot.event.EventBus;

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    /**
     * Callback to implement in your app to handle when a notification is opened from the Android
     * status bar or
     * a new one comes in while the app is running.
     * This method is located in this activity as an example, you may have any class you wish
     * implement NotificationOpenedHandler and define this method.
     *
     * @param message        The message string the user seen/should see in the Android status bar.
     * @param additionalData The additionalData key value pair section you entered in on
     *                       onesignal.com.
     * @param isActive       Was the app in the foreground when the notification was received.
     */
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {


        try {
            if (additionalData != null) {
                if (additionalData.has("userId"))
                    EventBus.getDefault().postSticky(additionalData.getString("userId"));
                    MainApplication.IS_NOTIFICATION = true;


            }
        } catch (JSONException e) {
        }



      /*  Intent notificationIntent = new Intent(MainApplication.context, StatusActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra(Config.KEY_NOTIFICATION, Config.NotificationType.NOTIFICATION);
        MainApplication.context.startActivity(notificationIntent);*/

    }
}