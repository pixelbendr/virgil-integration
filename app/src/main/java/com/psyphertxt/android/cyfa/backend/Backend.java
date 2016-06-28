package com.psyphertxt.android.cyfa.backend;

import android.content.Context;

import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;

import org.json.JSONObject;

/**
 * This class contains interfaces for managing our backend services using
 * Parse "Backend as a Service"
 * http://parse.com/
 */

public class Backend {

    /**
     * interface for handling sign in and sign ups
     */
    public interface Account {

        /**
         * this method checks to see if the number provided exists and sends a success or error
         *
         * @param phoneNumber        the users phone number
         * @param callbackForResults the callback returned from the server in json format
         */
        void userValidation(String phoneNumber, final CallbackListener.callbackForResults callbackForResults);

        /**
         * this method creates a new user based on a success callback and sends a
         * text message to the user to verify their account
         *
         * @param username the users generated username
         * @param callbackForResults the callback returned from the server in json format
         */
        void newUser(String username, final CallbackListener.callbackForResults callbackForResults);

        /**
         * this method creates a new token and sends a message to the user to verify an existing account
         *
         * @param user               a user object in json format. example username etc..
         * @param callbackForResults the callback returned from the server in json format
         */
        void existingUser(JSONObject user, final CallbackListener.callbackForResults callbackForResults);

        /**
         * check to see if the user has an active account on the device
         *
         * @param callbackForResults returns success or error
         */
        void currentUser(final CallbackListener.callbackForResults callbackForResults);

        /**
         * @param context the activity or fragment
         * @param cls     the activity class
         */
        void logout(Context context, Class<?> cls);

    }

    /**
     * app notification handling
     */
    public interface Notifications {

        /**
         * this method is used to subscribe a device to receive push notifications
         * make sure all needed settings have been setup in the manifest file or gradle for
         * this application before calling subscribe
         *
         * @param context
         */
        void subscribe(Context context);

        /**
         * this method is used to validate if a device
         * has been registered for push notification
         * @param callbackForResults returns success with a playerId or error
         */
        void validate(CallbackListener.callbackForResults callbackForResults);


        /**
         * this method sends info to cloud code to validate and send a push notification to a user or
         * a group
         */
        void send(String playerId, String message);

    }
}
