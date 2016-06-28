package com.psyphertxt.android.cyfa.ui.manager;

import android.app.Activity;
import android.view.WindowManager;

/**
 * Created by pie on 10/23/15.
 */
public class AppSecurityManager {

    public static void setScreenCaptureAllowed(Activity activity, boolean allowed) {
        if (!allowed) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
    }
}
