package com.psyphertxt.android.cyfa.backend.parse;

import com.parse.ParseACL;
import com.parse.ParseObject;

import java.util.List;

public class ParseObjectUtils {

    /**
     * Returns {@code o} if non-null, or throws {@code NullPointerException}
     * with the given detail message.
     */
    public static <T> T requireNonNull(T o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
        return o;
    }

    @SuppressWarnings("unchecked")
    public static List<ParseObject> getParseObject (Object object) {
        return  (List<ParseObject>) object;
    }
}
