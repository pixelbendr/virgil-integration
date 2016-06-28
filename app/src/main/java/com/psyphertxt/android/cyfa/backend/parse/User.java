package com.psyphertxt.android.cyfa.backend.parse;

import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.MessageToken;
import com.psyphertxt.android.cyfa.backend.onesignal.Notifications;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.util.NetworkUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for managing users.
 * example, getting or setting the name of a user
 */

@ParseClassName("_User")
public class User extends ParseUser {

    public void setUsername(String username) {
        put(Config.KEY_USERNAME, username);
    }

    public static User getDeviceUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public static String getDeviceUserId() {
        return getDeviceUser().getObjectId();

    }

    public void setTheme(String theme) {
        put(Config.KEY_THEME, theme);
    }

    public String getTheme() {
        return getString(Config.KEY_THEME);
    }

    public void setProfileName(String profileName) {
        put(Config.KEY_PROFILE_NAME, profileName);
    }

    public String getProfileName() {
        return getString(Config.KEY_PROFILE_NAME);
    }

    public static ParseACL publicReadOnly() {
        ParseACL parseACL = new ParseACL();
        parseACL.setPublicReadAccess(true);
        parseACL.setWriteAccess(User.getDeviceUser(), true);
        return parseACL;
    }

    public static void sync(SignUp signUp, final CallbackListener.callback callback) {

    }

    public static void setup(final SignUp signUp, final CallbackListener.callback callback) {

        Notifications notifications = new Notifications();
        notifications.validate(new CallbackListener.callbackForResults() {
            @Override
            public void success(final Object result) {

                signUp.setPlayerId((String) result);

                Map<String, Object> payload = new HashMap<>();
                payload.put(Config.KEY_USER_ID, getDeviceUserId());
                payload.put(Config.KEY_PROFILE_NAME, signUp.getProfileName());
                payload.put(Config.KEY_THEME, signUp.getTheme());
                payload.put(Config.KEY_CALLING_CODE, signUp.getCallingCode());
                payload.put(Config.KEY_USERNAME, User.getDeviceUser().getUsername());
                payload.put(Config.KEY_PLAYER_ID, signUp.getPlayerId());

                ParseCloud.callFunctionInBackground(Config.DEFINE_SETUP_USER, payload, new FunctionCallback<HashMap<String, Object>>() {

                    @Override
                    public void done(HashMap<String, Object> hashMap, ParseException e) {

                        if (e == null) {

                            int responseCode = (Integer) hashMap.get(NetworkUtils.KEY_RESPONSE_CODE);

                            if (responseCode == NetworkUtils.RESPONSE_OK) {

                                @SuppressWarnings("unchecked")
                                HashMap<String, Object> data = (HashMap<String, Object>) hashMap.get(Config.DATA);

                                if (data.get(Config.KEY_PROFILE) != null) {
                                    Profile profile = (Profile) data.get(Config.KEY_PROFILE);
                                    profile.pinInBackground();

                                    final User user = User.getDeviceUser();
                                    user.setProfileName(signUp.getProfileName());
                                    user.setTheme(signUp.getTheme());
                                    user.pinInBackground();
                                }

                                if (data.get(Config.TOKEN) != null) {
                                    MessageToken.validate((String) data.get(Config.TOKEN));
                                }
                                callback.success();
                            } else {

                                saveEventually(signUp);
                                callback.error(NetworkUtils.SETUP_USER_ERROR + "");

                            }

                        } else {

                            saveEventually(signUp);
                            callback.error(e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void error(String error) {

            }
        });


    }

    private static void saveEventually(SignUp signUp) {

        String callingCode = signUp.getCallingCode();

        //TODO use event to track how many times this happens
        //lets save some profile defaults on the device user
        Profile profile = new Profile();
        profile.setACL(User.publicReadOnly());
        profile.setUserId(User.getDeviceUserId());
        profile.setPlayerId(signUp.getPlayerId());
        profile.setCallingCode(callingCode);
        profile.saveEventually();

        //lets do a first save in our user table
        final User user = User.getDeviceUser();
        user.setProfileName(signUp.getProfileName());
        user.setTheme(signUp.getTheme());
        user.saveEventually();
    }
}
