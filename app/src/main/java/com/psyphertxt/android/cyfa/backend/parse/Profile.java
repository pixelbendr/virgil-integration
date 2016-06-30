package com.psyphertxt.android.cyfa.backend.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

/**
 * Class which holds profile to users
 * on the platform this is an extension
 * of the User class and serves as a
 * public profile for each user
 */

@ParseClassName("Profile")
public class Profile extends ParseObject {

    public void setStatus(String status) {
        put(Config.KEY_STATUS, status);
    }

    public String getStatus() {
        return getString(Config.KEY_STATUS);
    }

    public void setCallingCode(String callingCode) {
        put(Config.KEY_CALLING_CODE, callingCode);
    }

    public String getCallingCode() {
        return getString(Config.KEY_CALLING_CODE);
    }

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public String getUserId() {
        return getString(Config.KEY_USER_ID);
    }

    public void setPlayerId(String playerId) {
        put(Config.KEY_PLAYER_ID, playerId);
    }

    public String getPlayerId() {
        return getString(Config.KEY_PLAYER_ID);
    }

    public static ParseQuery<Profile> getProfileByUserId(String userId, Boolean isLocal) {
        ParseQuery<Profile> profile = ParseQuery.getQuery(Profile.class);
        if (isLocal) {
            profile.fromLocalDatastore();
        }
        profile.whereEqualTo(Config.KEY_USER_ID, userId);
        return profile;
    }

    public static void setStatusUI(Activity activity, String message) {
        View view = activity.getWindow().getDecorView();
        TextView txtStatusMessage = (TextView) view.findViewById(android.R.id.summary);
        txtStatusMessage.setText(message);
    }

    public static void updateStatusMessage(final String status, final CallbackListener.callbackForResults callbackForResults) {

        final Profile profile = EventBus.getDefault().removeStickyEvent(Profile.class);

        if (profile != null) {

            profile.setStatus(status);
            profile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        profile.pinInBackground();
                        callbackForResults.success(status);
                    } else {
                        callbackForResults.error(e.getMessage());
                    }
                }
            });

        } else {

            final Profile profile1 = new Profile();
            profile1.setACL(User.publicReadOnly());
            profile1.setStatus(status);
            profile1.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        profile1.pinInBackground();
                        callbackForResults.success(status);
                    } else {
                        callbackForResults.error(e.getMessage());
                    }
                }
            });

        }
    }
}
