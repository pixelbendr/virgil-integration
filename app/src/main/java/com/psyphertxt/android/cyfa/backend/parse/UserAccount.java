package com.psyphertxt.android.cyfa.backend.parse;

import android.content.Context;
import android.content.Intent;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.Backend;
import com.psyphertxt.android.cyfa.backend.firebase.channel.StatusChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.util.SecurityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Contains methods for manipulating user accounts,
 * creating sessions, deleting sessions etc
 * Refer to implementation interface for comments
 */

public class UserAccount implements Backend.Account {

    public void userValidation(String phoneNumber, final CallbackListener.callbackForResults callbackForResults) {

        HashMap<String, Object> params = new HashMap<>();
        params.put(Config.KEY_PHONE_NUMBER, phoneNumber);

        //get users number from edit text field
        //send users number to cloud code
        //to check if a user exist with that number
        ParseCloud.callFunctionInBackground(Config.DEFINE_USER_VALIDATION, params, new FunctionCallback<Object>() {

            public void done(Object result, ParseException e) {

                if (e == null) {

                    callbackForResults.success(result);

                } else {

                    callbackForResults.error(e.getMessage());

                }
            }
        });
    }

    public void newUser(String username, final CallbackListener.callbackForResults callbackForResults) {

        final User user = new User();
        user.setUsername(username);
        user.setPassword(SecurityUtils.createHash());
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callbackForResults.success(user);
                } else {
                    callbackForResults.error(e.getMessage());
                }
            }
        });
    }

    public void existingUser(JSONObject user, final CallbackListener.callbackForResults callbackForResults) {

        try {
            ParseUser.logInInBackground(user.getString(Config.KEY_USERNAME), user.getString(Config.KEY_PASSWORD), new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    if (e == null) {
                        //success
                        callbackForResults.success(parseUser);
                    } else {
                        callbackForResults.error(e.getMessage());
                    }
                }
            });

        } catch (JSONException e) {

            callbackForResults.error(e.getMessage());

        }
    }

    public void currentUser(final CallbackListener.callbackForResults callbackForResults) {

        User currentUser = User.getDeviceUser();
        if (currentUser != null) {
            callbackForResults.success(currentUser);

        } else {
            callbackForResults.error("current user not found");
        }
    }

    @Override
    public void logout(Context context, Class<?> cls) {

        User.logOut();
        User user = User.getDeviceUser();
        if (user == null) {

            Intent loginIntent = new Intent(context, cls);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

            ParseObject.unpinAllInBackground();
            ContextUser.removeAll(new CallbackListener.callback() {
                @Override
                public void success() {

                }

                @Override
                public void error(String error) {

                }
            });
            StatusChannel.removeAll();

            //TODO show loader when logging out
            //TODO investigate extra de-caching
            //TODO set theme to default

            context.startActivity(loginIntent);

        }
    }
}
