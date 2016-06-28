package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.psyphertxt.android.cyfa.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Codebendr on 30/10/2015.
 */
public class Media extends HashMap<String, Object> {

    //add thumbnail  link
    //add flag
    //add social

    public Media() {
        super();
    }

    public Media(Map map) {
        super(map);
    }

    public String getLink() {
        return (String) get(Config.KEY_LINK);
    }

    public void setLink(String link) {
        put(Config.KEY_LINK, link);
    }

    public String getCaption() {
        return (String) get(Config.KEY_CAPTION);
    }

    public void setCaption(String caption) {
        put(Config.KEY_CAPTION, caption);
    }

    public int getType() {
        return (int) get(Config.KEY_TYPE);
    }

    public void setType(int caption) {
        put(Config.KEY_TYPE, caption);
    }
}
