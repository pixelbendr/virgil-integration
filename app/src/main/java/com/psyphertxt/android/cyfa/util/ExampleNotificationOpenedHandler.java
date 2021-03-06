package com.psyphertxt.android.cyfa.util;

import android.app.AlertDialog;

import com.onesignal.OneSignal;
import com.psyphertxt.android.cyfa.MainApplication;

import org.json.JSONException;
import org.json.JSONObject;

// NotificationOpenedHandler is implemented in its own class instead of adding implements to MainActivity so we don't hold on to a reference of our first activity if it gets recreated.
public class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    /**
     * Callback to implement in your app to handle when a notification is opened from the Android status bar or
     * a new one comes in while the app is running.
     * This method is located in this activity as an example, you may have any class you wish implement NotificationOpenedHandler and define this method.
     *
     * @param message        The message string the user seen/should see in the Android status bar.
     * @param additionalData The additionalData key value pair section you entered in on onesignal.com.
     * @param isActive       Was the app in the foreground when the notification was received.
     */
    @Override
    public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
        String messageTitle = "OneSignal Example", messageBody = message;

        try {
            if (additionalData != null) {
                if (additionalData.has("title"))
                    messageTitle = additionalData.getString("title");
                if (additionalData.has("actionSelected"))
                    messageBody += "\nPressed ButtonID: " + additionalData.getString("actionSelected");

                messageBody = message + "\n\nFull additionalData:\n" + additionalData.toString();
            }
        } catch (JSONException e) {
        }
    }
}
