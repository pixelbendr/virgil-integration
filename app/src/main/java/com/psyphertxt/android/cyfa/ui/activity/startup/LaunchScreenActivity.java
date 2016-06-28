package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.MainApplication;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.onesignal.Notifications;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.backend.parse.UserAccount;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.activity.StatusActivity;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.StartUpActionBarActivity;

import de.greenrobot.event.EventBus;

/**
 * Activity that shows when the app starts or restarts
 */

public class LaunchScreenActivity extends StartUpActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        //track how many times this application was opened
        //Analytics.trackAppOpens(getIntent());

        //initialize notifications
        Notifications notifications = new Notifications();
        notifications.subscribe(this);

        //lets create a new instance of the user account
        //to access user features
        final UserAccount userAccount = new UserAccount();

        //lets create a delay so we can display our launch screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Config.IS_PROTO) {

                    Intent intent = new Intent(LaunchScreenActivity.this, StartUpActivity.class);
                    startActivity(intent);
                    LaunchScreenActivity.this.finish();

                } else {

                    //lets check to see if a user is already logged in
                    userAccount.currentUser(new CallbackListener.callbackForResults() {
                        @Override
                        public void success(Object result) {

                            //lets assume user didn't finish signing up
                            //lets show the profile name activity to help
                            //them finish the sign up process
                            if (User.getDeviceUser().getProfileName() == null) {

                                //we need the calling code for setting up the user
                                //lets try to retrieve it from stored settings
                                String callingCode;
                                Settings settings = new Settings(LaunchScreenActivity.this);
                                callingCode = settings.getCallingCode();

                                Intent intent = new Intent(LaunchScreenActivity.this, ProfileNameActivity.class);
                                intent.putExtra(Config.KEY_CALLING_CODE, callingCode);
                                SignUp signUp = new SignUp();
                                signUp.setCallingCode(callingCode);
                                signUp.isExisting(false);
                                EventBus.getDefault().postSticky(signUp);
                                startActivity(intent);
                                LaunchScreenActivity.this.finish();

                            } else {

                                //if a user is logged in and is not notifcation
                                //display a UI element via an intent
                                Intent intent = new Intent(LaunchScreenActivity.this, StatusActivity.class);
                                startActivity(intent);
                                LaunchScreenActivity.this.finish();


                            }
                        }

                        @Override
                        public void error(String error) {

                            //if a user is not logged in
                            //display a UI element via an intent
                            Intent intent = new Intent(LaunchScreenActivity.this, StartUpActivity.class);
                            startActivity(intent);
                            LaunchScreenActivity.this.finish();

                        }
                    });
                }
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
