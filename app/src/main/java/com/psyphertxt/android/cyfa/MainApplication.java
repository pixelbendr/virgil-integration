package com.psyphertxt.android.cyfa;

import com.crashlytics.android.Crashlytics;
import com.psyphertxt.android.cyfa.util.Foreground;
import com.squareup.leakcanary.LeakCanary;

import android.app.Application;
import android.content.Context;

import io.fabric.sdk.android.Fabric;

/**
 * This is the Applications main entrance so
 * most of the stuff done here is initialization
 * and persistent data storage and retrievals
 */

public class MainApplication extends Application {

    public static Context context = null;
    public static Boolean IS_NOTIFICATION = false;

    public void onCreate() {

        super.onCreate();

        context = MainApplication.this;

        //listen for app background event
        Foreground.init(this);

        //setup Crashlytics to log app crashes
        Fabric.with(this, new Crashlytics());

        //memory leaks
       // LeakCanary.install(this);

        //TODO set IS_PROD to true when ready to deploy
        //TODO move env management to gradle
        //lets set to prod when application is ready to go live
        //lets initialize third party libraries using this method
        //set up notification
        //set up crash reporting etc
        Config.setup(this);

        //use stetho debugger.
       // Config.debug(this);

    }
}
