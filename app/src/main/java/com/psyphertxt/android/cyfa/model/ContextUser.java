package com.psyphertxt.android.cyfa.model;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.parse.People;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.util.NetworkUtils;
import com.psyphertxt.android.cyfa.util.Testable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.greenrobot.event.EventBus;

/**
 * A context user is dynamically created on the device from
 * the users contact, server details etc...
 * And interactions with other parts
 * of the app like a business, a groups etc
 */

@ParseClassName("ContextUser")
public class ContextUser extends ParseObject {

    public String getUserId() {
        return getString(Config.KEY_USER_ID);
    }

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public void setConversationId(String conversationId) {
        put(Config.KEY_CONVERSATION_ID, conversationId);
    }

    public String getConversationId() {
        return getString(Config.KEY_CONVERSATION_ID);
    }

    public String getUsername() {
        return getString(Config.KEY_USERNAME);
    }

    public void setUsername(String username) {
        put(Config.KEY_USERNAME, username);
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

    public Boolean isBlocked() {
        return getBoolean(Config.KEY_IS_BLOCKED);
    }

    public void isBlocked(Boolean isBlocked) {
        put(Config.KEY_IS_BLOCKED, isBlocked);
    }

    public Boolean isContact() {
        return getBoolean(Config.KEY_IS_CONTACT);
    }

    public void isContact(Boolean isContact) {
        put(Config.KEY_IS_CONTACT, isContact);
    }

    public Boolean isAccepted() {
        return getBoolean(Config.KEY_ACCEPTED);
    }

    public void isAccepted(Boolean accepted) {
        put(Config.KEY_ACCEPTED, accepted);
    }

    public String getNumber() {
        return getString(Config.NUMBER);
    }

    public void setNumber(String number) {
        put(Config.NUMBER, number);
    }

    public Profile getProfile() {
        return (Profile) get(Config.KEY_PROFILE);
    }

    public void setProfile(Profile profile) {
        put(Config.KEY_PROFILE, profile);
    }

    public void setUser(User user) {
        setUserId(user.getObjectId());
        setUsername(user.getUsername());
        if (user.getProfileName() != null) {
            setProfileName(user.getProfileName());
        }
        if (user.getTheme() != null) {
            setTheme(user.getTheme());
        }
    }

    public void setPeople(People people) {
        setConversationId(people.getConversationId());
        isAccepted(people.isAccepted());

    }

    //TODO create a removeAll utils
    public static void removeAll(final CallbackListener.callback callback) {
        ParseQuery<ContextUser> query = ParseQuery.getQuery(ContextUser.class);
        query.fromLocalDatastore().findInBackground(new FindCallback<ContextUser>() {
            @Override
            public void done(List<ContextUser> list, ParseException e) {
                ParseObject.unpinAllInBackground(Config.DB_NAME_CONTEXT_USERS, list, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            callback.success();
                        }else{
                            callback.error("deletion error");
                        }
                    }
                });
            }
        });
    }

    //TODO add spinner :: loading contacts
    public static List<Object> findAll() {
        List<ContextUser> contextUsers = new ArrayList<>();
        try {
            contextUsers = ParseQuery.getQuery(ContextUser.class)
                    .whereNotEqualTo(Config.KEY_USER_ID, User.getDeviceUserId())
                    .fromLocalDatastore()
                    .orderByAscending(Config.KEY_PROFILE_NAME)
                    .find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (List<Object>)(List<?>) contextUsers;
    }

    public static ContextUser findOne(String userId) {
        ContextUser contextUser = null;
        try {
            contextUser = ParseQuery.getQuery(ContextUser.class)
                    .fromLocalDatastore()
                    .whereEqualTo(Config.KEY_USER_ID, userId)
                    .getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return contextUser;
    }

    /**
     * creates a context user from data sent from the server
     * teh data is retrieved from an event bus and contains
     * a curated list of data collected . e.g User, Profile, isBlocked etc...
     */
    public static List<ContextUser> create(HashMap<String, Object> result) {

        @SuppressWarnings("unchecked")
       // HashMap<String, Object> result = EventBus.getDefault().removeStickyEvent(HashMap.class);

        int responseCode = (Integer) result.get(NetworkUtils.KEY_RESPONSE_CODE);
        List<ContextUser> contextUserList = new ArrayList<>();

        if (responseCode == NetworkUtils.RESPONSE_OK) {

            ArrayList users = (ArrayList) result.get(Config.DATA);

            for (int i = 0, len = users.size(); i < len; i++) {

                ContextUser contextUser = new ContextUser();

                @SuppressWarnings("unchecked")
                HashMap<String, Object> _contextUser = (HashMap<String, Object>) users.get(i);

                //lets check if the user object was sent
                //ParseUser doesn't pin well in background
                //so we have to set the values ourselves
                if (_contextUser.get(Config.KEY_USER) != null) {

                    //TODO in chat activity check if profileName is null and try and retrieve from the server,
                    // TODO put user's number in place of profile name
                    //TODO alternatively search address book and retrieve local name if any
                    //TODO create method to retrieve a single contact entry in address book
                    User user = (User) _contextUser.get(Config.KEY_USER);
                    contextUser.setUser(user);
                }

                //lets check if the user's number is not null
                if (_contextUser.get(Config.NUMBER) != null) {
                    contextUser.setNumber((String) _contextUser.get(Config.NUMBER));
                }

                //lets check if the user's profile was sent
                if (_contextUser.get(Config.KEY_PROFILE) != null) {
                    contextUser.setProfile((Profile) _contextUser.get(Config.KEY_PROFILE));
                }

                //lets check if these user has already been blocked
                if (_contextUser.get(Config.KEY_IS_BLOCKED) != null) {
                    contextUser.isBlocked((Boolean) _contextUser.get(Config.KEY_IS_BLOCKED));
                }

                //lets set is contact to true since context user's phone number
                //is in device users phone book
                if (_contextUser.get(Config.KEY_IS_CONTACT) != null) {
                    contextUser.isContact((Boolean) _contextUser.get(Config.KEY_IS_CONTACT));
                }

                //add to list
                contextUserList.add(contextUser);

            }
        }

        ParseObject.pinAllInBackground(Config.DB_NAME_CONTEXT_USERS, contextUserList);

        return contextUserList;
    }
}
