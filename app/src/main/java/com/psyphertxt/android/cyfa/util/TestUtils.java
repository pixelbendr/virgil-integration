package com.psyphertxt.android.cyfa.util;

import com.firebase.client.Firebase;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.parse.User;

import java.util.HashMap;

@Deprecated
public class TestUtils {

    public interface TestListener {

        void run(Object result);

    }

    private static Firebase serverLogger(final String name, String desc) {
        return new Firebase(Config.getFirebaseURL())
                .child(Config.REF_SPEC)
                .child(name)
                .child("describe")
                .child(desc);
    }

    private static HashMap<String, Object> log(final Object actual) {
        return new HashMap<String, Object>() {
            {
                put("expect", actual);
                put("toBeType", actual.getClass().getSimpleName());
            }
        };
    }

    public static void spec(final String name, String desc, final Object actual) {
        if (!Config.IS_PROD) {
            HashMap<String, Object> hashMap = log(actual);
            serverLogger(name, desc).setValue(hashMap);
        }
    }

    public static void spec(String desc, final Object actual) {
        String name = "User-" + SecurityUtils.createConversationId();
        if (User.getDeviceUser() != null) {
            name = User.getDeviceUserId();
        }
        spec(name, desc, actual);
    }

    public static void spec(String desc, final Object actual, final Object stacktrace) {
        spec(User.getDeviceUserId(), desc, actual, stacktrace);
    }

    public static void spec(final String name, String desc, final Object actual, final Object stacktrace) {
        if (!Config.IS_PROD) {
            HashMap<String, Object> hashMap = log(actual);
            hashMap.put("stacktrace", stacktrace);
            serverLogger(name, desc).setValue(hashMap);
        }
    }

    public static HashMap stacktrace() {
        final StackTraceElement traces = Thread.currentThread().getStackTrace()[3];
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lineNumber", traces.getLineNumber());
        hashMap.put("methodName", traces.getMethodName() + "()");
        hashMap.put("fileName", traces.getFileName());
        return hashMap;
    }

    public static void messageTest(int length, TestListener testListener) {
        if (length > 1000) {
            length = 999;
        }
        for (int i = 0; i < length; i++) {
            testListener.run(SecurityUtils.createConversationId());
        }
    }

    public static void crashReporting() {
        // uncaught exception handler variable
        final Thread.UncaughtExceptionHandler defaultUEH;

        // handler listener
        Thread.UncaughtExceptionHandler _unCaughtExceptionHandler =
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {


                        // re-throw critical exception further to the os (important)
                        //  defaultUEH.uncaughtException(thread, ex);
                    }
                };

        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        // setup handler for uncaught exception
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);

    }
}
