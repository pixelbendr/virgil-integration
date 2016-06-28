package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.parse.ParseObject;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.util.TextUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles inputs and outputs from the
 * status feed model and reads and writes it directly to
 * firebase status/<userId>/<messageId>
 */
public final class Status extends HashMap<String, Object> {

    //break status in small features
    //TypeIndicator getTypeIndicator
    //Recent getRecent
    //int getCount


    public Status() {
        super();
    }

    public Status(Map map) {
        super(map);
    }

    public int getCount() {
        return (int) get(Config.KEY_COUNT);
    }

    public void setCount(int count) {
        put(Config.KEY_COUNT, count);
    }

    public Boolean getActive() {
        return (Boolean) get(Config.KEY_ACTIVE);
    }

    public void setActive(Boolean active) {
        put(Config.KEY_ACTIVE, active);
    }

    public Object getTypingIndicator() {
        return get(Config.KEY_TYPING);
    }

    public void setTypingIndicator(Object typing) {
        put(Config.KEY_TYPING, typing);
    }

    public String getProfileName() {
        return (String) get(Config.KEY_PROFILE_NAME);
    }

    public void setProfileName(String profileName) {
        put(Config.KEY_PROFILE_NAME, profileName);
    }

    public void setMessageType(int messageType) {
        put(Config.KEY_TYPE, messageType);
    }

    public String getMessageType() {
        return (String) get(Config.KEY_TYPE);
    }

    public String getMessageId() {
        return (String) get(Config.KEY_MESSAGE_ID);
    }

    public void setMessageId(String messageId) {
        put(Config.KEY_MESSAGE_ID, messageId);
    }

    public String getLastMessage() {
        return (String) get(Config.KEY_LAST_MESSAGE);
    }

    public void setLastMessage(String lastMessage) {
        put(Config.KEY_LAST_MESSAGE, TextUtils.trim(lastMessage));
    }

    public Long getTimestamp() {
        return (Long) get(Config.KEY_TIMESTAMP);
    }

    public void setTimestamp() {
        put(Config.KEY_TIMESTAMP, new Date().getTime());
    }

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public String getUserId() {
        return (String) get(Config.KEY_USER_ID);
    }

    public static HashMap<String, Object> setStatus(final Map map) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_USER_ID, map.get(Config.KEY_USER_ID));
        hashMap.put(Config.KEY_PROFILE_NAME, map.get(Config.KEY_PROFILE_NAME));
        hashMap.put(Config.KEY_LAST_MESSAGE, map.get(Config.KEY_LAST_MESSAGE));
        hashMap.put(Config.KEY_TIMESTAMP, map.get(Config.KEY_TIMESTAMP));
        hashMap.put(Config.KEY_COUNT, map.get(Config.KEY_COUNT));
        return hashMap;
    }

    public static HashMap<String, Object> setStatus(final ParseObject parseObject) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_USER_ID, parseObject.getString(Config.KEY_USER_ID));
        hashMap.put(Config.KEY_PROFILE_NAME, parseObject.get(Config.KEY_PROFILE_NAME));
        hashMap.put(Config.KEY_LAST_MESSAGE, parseObject.getString(Config.KEY_LAST_MESSAGE));
        hashMap.put(Config.KEY_TIMESTAMP, parseObject.getLong(Config.KEY_TIMESTAMP));
        hashMap.put(Config.KEY_COUNT, parseObject.getInt(Config.KEY_COUNT));
        return hashMap;
    }

    public static ParseObject getStatus(final Status status, final ParseObject parseObject) {
        parseObject.put(Config.KEY_USER_ID, status.getUserId());
        parseObject.put(Config.KEY_PROFILE_NAME, status.getProfileName());
        parseObject.put(Config.KEY_LAST_MESSAGE, status.getLastMessage());
        parseObject.put(Config.KEY_TIMESTAMP, status.getTimestamp());
        parseObject.put(Config.KEY_COUNT, status.getCount());
        return parseObject;
    }
}
