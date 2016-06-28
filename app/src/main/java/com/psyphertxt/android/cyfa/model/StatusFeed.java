package com.psyphertxt.android.cyfa.model;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.util.TimeUtils;

import java.util.Map;

/**
 * This class handles user status updates.
 * badges and channel id's, and recent messages.
 */
//TODO deprecate if not needed

public final class StatusFeed {

    private String userId;
    private int count;
    private String lastMessage;
    private String profileName;
    private Long timestamp;

    public StatusFeed(Map map) {

        updateFeed(map);
    }

    public void updateFeed(Map map) {
        this.userId = (String) map.get(Config.KEY_USER_ID);
        this.lastMessage = (String) map.get(Config.KEY_LAST_MESSAGE);
        this.profileName = (String) map.get(Config.KEY_PROFILE_NAME);
        this.timestamp = (Long) map.get(Config.KEY_TIMESTAMP);
        this.count = (int) map.get(Config.KEY_COUNT);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTimestamp() {
        if (timestamp != null) {
            return TimeUtils.getTimeAgo(timestamp);
        }
        return Config.EMPTY_STRING;
    }

    public Long getRawTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}