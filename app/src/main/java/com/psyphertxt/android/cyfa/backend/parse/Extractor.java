package com.psyphertxt.android.cyfa.backend.parse;

import com.parse.ParseObject;
import com.psyphertxt.android.cyfa.model.ContextUser;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class for making parse objects percelable for passing
 * around in UI activities
 */


public class Extractor implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<String, Object> values = new HashMap<>();

    public HashMap<String, Object> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Object> values) {
        this.values = values;
    }

    public Extractor(Object _object) {

        ParseObject object = (ParseObject) _object;
        // Loop the keys in the ParseObject
        for (String key : object.keySet()) {
            @SuppressWarnings("rawtypes")
            Class classType = object.get(key).getClass();
            if (classType == byte[].class || classType == String.class ||
                    classType == Integer.class || classType == Boolean.class) {
                values.put(key, object.get(key));
            } else if (classType == Profile.class ||
                    classType == People.class || classType == ContextUser.class) {
                Extractor parseObject = new Extractor(object.get(key));
                values.put(key, parseObject);
            } //else {
            // You might want to add more conditions here, for embedded ParseObject, ParseFile, etc.
            // }
        }
    }

    public String getString(String key) {
        if (has(key)) {
            return (String) values.get(key);
        } else {
            return "";
        }
    }

    public int getInt(String key) {
        if (has(key)) {
            return (Integer) values.get(key);
        } else {
            return 0;
        }
    }

    public Boolean getBoolean(String key) {
        if (has(key)) {
            return (Boolean) values.get(key);
        } else {
            return false;
        }
    }

    public byte[] getBytes(String key) {
        if (has(key)) {
            return (byte[]) values.get(key);
        } else {
            return new byte[0];
        }
    }

    public Extractor getObject(String key) {
        if (has(key)) {
            return (Extractor) values.get(key);
        } else {
            return null;
        }
    }

    public People getPeople(String key) {
        if (has(key)) {
            return (People) values.get(key);
        } else {
            return null;
        }
    }

    public ContextUser getContextUser(String key) {
        if (has(key)) {
            return (ContextUser) values.get(key);
        } else {
            return null;
        }
    }

    public Profile getProfile(String key) {
        if (has(key)) {
            return (Profile) values.get(key);
        } else {
            return null;
        }
    }

    public Boolean has(String key) {
        return values.containsKey(key);
    }
}

