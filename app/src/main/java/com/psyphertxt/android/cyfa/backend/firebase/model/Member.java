package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.psyphertxt.android.cyfa.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for handling each member of a group
 */
public final class Member extends HashMap<String, Object> {

    public Member() {
        super();
    }

    public Member(Map map) {
        super(map);
    }

    public String getName() {
        return (String) get(Config.KEY_NAME);
    }

    public void setName(String name) {
        put(Config.KEY_NAME, name);
    }

    public String getUserId() {
        return (String) get(Config.KEY_USER_ID);
    }

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public Member(String name, String userId) {
        setName(name);
        setUserId(userId);
    }

    public void setMember(HashMap<String, Object> channel) {
        setName((String) channel.get(Config.KEY_NAME));
        setUserId((String) channel.get(Config.KEY_USER_ID));
    }
}
