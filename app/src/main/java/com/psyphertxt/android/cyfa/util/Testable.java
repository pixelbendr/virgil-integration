package com.psyphertxt.android.cyfa.util;

import com.firebase.client.Firebase;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.parse.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Class for logging unit tests on the server
 */
public class Testable {

    /**
     * new Testable.Spec("name") name of the test mostly user id in our case
     * .describe("description") description of the type of test
     * .stacktrace(Testable.trace())
     * .type() type of class of the returned data
     * .timer() add a timer to a test, like how long an api took to bring data
     * .toBeTruthy() we expect the results to be true
     * .toBeFalsy() we expect the results to be false
     * .toContain() we expect the result[] to contain a curtain object most likely string
     * .toBeString() we expect the results to be a string
     * .testListener(new TestListener.onTest) event fires to let as know if test failed or passed
     * .toBeType(class) we expect the results to be of a curtain type
     * .toMatch(object) we expect the results to match the object provided
     * .toBeNull() we expect the object to be null
     * .toNotBeNull() we expect the object not to be null
     * .run() runs the test
     */

    //TODO add error exception
    //TODO add comments
    //TODO define toBeTruthy and toBeFalsy well.
    //TODO check out com.orhanobut:logger:1.8 on github for advance logging implementation
    private Testable(Spec spec) {
    }

    public static class Spec {

        private String description;
        private String name;
        private Object actual;
        private HashMap<String, Object> value;

        public Spec(String name) {
            this.name = name;
        }

        public Spec() {
            this.name = "spec-" + SecurityUtils.createConversationId();
            if (User.getDeviceUser() != null) {
                this.name = User.getDeviceUserId() + "-" + User.getDeviceUser().getProfileName();
            }
        }

        private Firebase serverLogger() {
            return new Firebase(Config.getFirebaseURL())
                    .child(Config.REF_SPEC)
                    .child(TextUtils.removeSpecialChars(this.name))
                    .child("describe")
                    .child(TextUtils.removeSpecialChars(this.description));
        }

        public Spec describe(String description) {
            this.description = description;
            return this;
        }

        public Spec expect(Object actual) {
            value = new HashMap<>();
            this.actual = actual;
            if (this.actual == null) {
                this.actual = "object might be null";
            }
            value.put("expect", this.actual);
            if (value.get("type") == null) {
                value.put("toBeType", this.actual.getClass().getSimpleName());
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            value.put("timestamp", dateFormat.format(new Date().getTime()));
            return this;
        }

        public Spec stacktrace(HashMap trace) {
            value.put("stacktrace", trace);
            return this;
        }

        public Spec type() {
            value.put("type", actual.getClass().getSimpleName());
            return this;
        }

        public Spec toBeTruthy(Object object) {
            Boolean truthy = false;
            if (actual == object) {
                truthy = true;
            }
            value.put("compared", object);
            value.put("toBeTruthy", truthy);
            return this;
        }

        public Spec toBeFalsy(Object object) {
            Boolean falsy = true;
            if (actual != object) {
                falsy = false;
            }
            value.put("compared", object);
            value.put("toBeFalsy", falsy);
            return this;
        }

        public Spec toBeEqual(String object) {
            Boolean truthy = false;
            if (actual.equals(object)) {
                truthy = true;
            }
            HashMap<String, Object> equals = new HashMap<>();
            equals.put("equals", object);
            equals.put("toBeTruthy", truthy);
            value.put("toBeEqual", equals);
            return this;
        }

        public Spec toContain(final String object) {
            Boolean truthy = false;
            if (actual.toString().toLowerCase().contains(object)) {
                truthy = true;
            }
            HashMap<String, Object> contains = new HashMap<>();
            contains.put("contains", object);
            contains.put("toBeTruthy", truthy);
            value.put("toContain", contains);
            return this;
        }

        public Spec toBeString(Object object) {
            Boolean truthy = false;
            if (actual instanceof String) {
                truthy = true;
            }
            value.put("toBeString", truthy);
            return this;
        }

        public Spec toBeType(Class<?> cls) {
            Boolean truthy = false;
            if (actual.getClass() == cls) {
                truthy = true;
            }
            HashMap<String, Object> type = new HashMap<>();
            type.put("className", cls.getSimpleName());
            type.put("toBeTruthy", truthy);
            value.put("toBeType", type);
            return this;
        }

        public Spec toBeNull() {
            Boolean truthy = false;
            if (actual == null) {
                truthy = true;
            }
            value.put("toBeNull", truthy);
            return this;
        }

        public Spec toNotBeNull() {
            Boolean falsy = false;
            if (actual != null) {
                falsy = true;
            }
            value.put("toNotBeNull", falsy);
            return this;
        }

        public Testable run() {
            if (!Config.IS_PROD) {
                Testable testable = new Testable(this);
                serverLogger().setValue(value);
           /* if (ref == null) {
                throw new IllegalStateException("illegal state"); // thread-safe
            }*/
                return testable;
            }
            return null;
        }

    }

    public static HashMap trace() {
        final StackTraceElement traces = Thread.currentThread().getStackTrace()[3];
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lineNumber", traces.getLineNumber());
        hashMap.put("methodName", traces.getMethodName() + "()");
        hashMap.put("fileName", traces.getFileName());
        return hashMap;
    }
}

