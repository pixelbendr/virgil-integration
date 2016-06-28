package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.util.TimeUtils;

import java.util.HashMap;
import java.util.Map;

public class Presence extends HashMap<String, Object> {

    public Presence() {
    }

    public Presence(Map map) {
        super(map);
    }

    public String getLastSeen() {
        Long timestamp = (Long) get(Config.KEY_LAST_SEEN);
        if (timestamp != null) {
            return TimeUtils.getTimeAgo(timestamp);
        }

        return Config.EMPTY_STRING;
    }

    public Boolean getOnline() {
        return (Boolean) get(Config.KEY_ONLINE);
    }

    public void setOnline(Boolean online) {
        put(Config.KEY_ONLINE, online);
    }

    public static HashMap<String, Object> setAsOnline() {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_ONLINE, true);
        return hashMap;
    }

}
