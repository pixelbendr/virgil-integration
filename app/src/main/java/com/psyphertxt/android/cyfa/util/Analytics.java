package com.psyphertxt.android.cyfa.util;

import android.content.Intent;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;
import com.crashlytics.android.answers.SignUpEvent;
import com.psyphertxt.android.cyfa.Config;

/**
 * This class is responsible for generating analytics and tracking defined usage scenarios
 * for Example how many times a user opened this application
 */
public class Analytics {

    public static void signUpFlow(String title,String section, Boolean success) {

        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("SignUp")
                .putSuccess(success)
                .putCustomAttribute(Config.SIGNUP_FLOW, TextUtils.toTitleCase(section)));

    }

    public static void signUpFlow(String section, Boolean success) {

        signUpFlow(Config.SIGNUP_FLOW, section, success);

    }

    public static void signUpFlowErrors(String section, String error) {

        Answers.getInstance().logSignUp(new SignUpEvent()
                .putMethod("SignUp")
                .putSuccess(false)
                .putCustomAttribute(Config.SIGNUP_FLOW, TextUtils.toTitleCase(section))
                .putCustomAttribute(Config.SIGNUP_FLOW, error));

    }

    public static void loginFlow(String section, Boolean success) {

        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Login")
                .putSuccess(success)
                .putCustomAttribute(Config.LOGIN_FLOW, TextUtils.toTitleCase(section)));

    }

    public static void loginFlowErrors(String section, String error) {

        Answers.getInstance().logLogin(new LoginEvent()
                .putMethod("Login")
                .putSuccess(false)
                .putCustomAttribute(Config.LOGIN_FLOW, TextUtils.toTitleCase(section))
                .putCustomAttribute(Config.LOGIN_FLOW, error));

    }


}
