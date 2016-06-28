package com.psyphertxt.android.cyfa.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.parse.Profile;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.backend.parse.UserAccount;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.activity.startup.LoaderActivity;
import com.psyphertxt.android.cyfa.ui.activity.startup.ProfileNameActivity;
import com.psyphertxt.android.cyfa.ui.activity.startup.VerificationCodeActivity;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Class with helper methods for signing up.
 * ps: because of rapid prototyping and faster iteration
 * it makes sense to abstract listener methods this way
 */
public class SignUpUtils {

    public static void validateUser(final Context context) {

        //lets create a new instance of the user account
        //to access user features
        final UserAccount userAccount = new UserAccount();
        final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);

        //TODO check the type of network 4G, 3G, H
        if (NetworkUtils.isAvailable(context)) {

            //send user number to cloud code to verify
            //show loading progress
            //load next Verify Code Activity
            Alerts.progress(context);

            //check if the user exist in our data browser
            userAccount.userValidation(signUp.getPhoneNumber(), new CallbackListener.callbackForResults() {

                @Override
                public void success(Object result) {

                    //close progress dialog
                    Alerts.dismissProgress();

                    try {

                        //suppress the hash map check because cloud code should always return
                        //key as string and value as any object
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> _result = (HashMap<String, Object>) result;

                        int responseCode = (Integer) _result.get(NetworkUtils.KEY_RESPONSE_CODE);

                        if (responseCode == NetworkUtils.RESPONSE_OK) {


                            @SuppressWarnings("unchecked")
                            HashMap<String, Object> _data = (HashMap<String, Object>) _result.get(Config.DATA);

                            //any cloud code call should return a status message
                            signUp.isExisting((Boolean) _data.get(Config.KEY_IS_EXISTING_USER));

                            //create the next activity
                            Intent intent = new Intent(context, VerificationCodeActivity.class);

                            //lets save the code from the server for the next activity
                            signUp.setCode((Integer) _data.get(Config.KEY_CODE));

                            //if we detect an existing user, lets add these extra values
                            //to be sent to the next intent
                            if (signUp.isExisting()) {


                                signUp.setSessionToken((String) _data.get(Config.KEY_SESSION_TOKEN));
                                signUp.setUsername((String) _data.get(Config.KEY_USERNAME));
                            }

                            //update event bus sign up class
                            EventBus.getDefault().postSticky(signUp);

                            //show the next activity
                            context.startActivity(intent);

                        } else {
                            error(responseCode + Config.EMPTY_STRING);
                        }

                    } catch (Exception e) {
                        error(NetworkUtils.UNKNOWN_ERROR + Config.EMPTY_STRING);
                    }
                }

                @Override
                public void error(String error) {

                    Alerts.dismissProgress();

                    switch (error) {
                        case NetworkUtils.TEXT_MESSAGE_ERROR + Config.EMPTY_STRING:
                            Alerts.show(context, context.getString(R.string.phone_number_error_title), context.getString(R.string.phone_number_error_message));
                            break;

                        case NetworkUtils.SERVER_ERROR + Config.EMPTY_STRING:
                            Alerts.show(context, context.getString(R.string.connection_error_title), context.getString(R.string.connection_error_message));
                            break;

                        default:
                            Alerts.show(context, context.getString(R.string.unknown_error_title), context.getString(R.string.unknown_error_message));
                            break;
                    }
                }
            });

        } else {

            Alerts.show(context, context.getString(R.string.internet_error_title), context.getString(R.string.internet_error_message));
        }
    }

    public static void validateCode(final Context context, String verifyCode) {

        //get user data from event bus
        final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);

        //lets create a new instance of the user account class
        //to access user account sign up features
        final UserAccount userAccount = new UserAccount();

        //compare verification code if it matches create a new user
        //and show next activity
        if (verifyCode.length() == Config.CODE_LENGTH && Integer.parseInt(verifyCode) == signUp.getCode()) {

            final LinearLayout linearLayoutAutoVerify = ButterKnife.findById(((Activity) context), R.id.linearLayout_auto_verify);

            //lets make this visible so the user knows the code was auto verified
            linearLayoutAutoVerify.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    linearLayoutAutoVerify.setVisibility(View.GONE);
                }
            }, 2000);

            Alerts.progress(context);

            //store users phone number and calling code locally
            Settings settings = new Settings(context);
            settings.setPhoneNumber(signUp.getPhoneNumber());
            settings.setCallingCode(signUp.getCallingCode());

            if (!signUp.isExisting()) {

                SecurityUtils.generateHash(signUp.getPhoneNumber(), new CallbackListener.callbackForResults() {

                    @Override
                    public void success(Object result) {

                        userAccount.newUser((String) result, new CallbackListener.callbackForResults() {

                            @Override
                            public void success(Object result) {

                                Alerts.dismissProgress();

                                //show the next activity
                                Intent intent = new Intent(context, ProfileNameActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context.startActivity(intent);

                            }

                            @Override
                            public void error(String error) {

                                Alerts.dismissProgress();
                                Alerts.show(context, "Error signing up", error);

                            }
                        });
                    }

                    @Override
                    public void error(String error) {

                        Alerts.show(context, "Hash errors", error);
                    }
                });


            } else {

                JSONObject user = new JSONObject();
                try {
                    user.put(Config.KEY_USERNAME, signUp.getUsername());
                    user.put(Config.KEY_PASSWORD, signUp.getSessionToken());
                    userAccount.existingUser(user, new CallbackListener.callbackForResults() {
                        @Override
                        public void success(Object result) {



                            Alerts.dismissProgress();
                            //show the next activity
                            Intent intent = new Intent(context, LoaderActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);

                        }

                        @Override
                        public void error(String error) {


                            Alerts.dismissProgress();
                            //TODO better error code handling
                            //TODO sign in account error
                            Alerts.show(context, "Error signing up", error);

                        }
                    });
                } catch (JSONException e) {

                    Alerts.dismissProgress();


                    Alerts.show(context, "JSON Error", e.getMessage());
                }

            }
        } else {

            Alerts.show(context, context.getString(R.string.sms_code_error_title), context.getString(R.string.sms_code_error_message));

        }

    }

    public static void setupNewUser(final Context context) {

        //get user data from event bus
        final SignUp signUp = EventBus.getDefault().removeStickyEvent(SignUp.class);

        //lets do a first save in our user table
        final User user = User.getDeviceUser();
        user.setProfileName(signUp.getProfileName());
        user.setTheme(signUp.getTheme());
        user.pinInBackground();

        user.saveInBackground().continueWith(new Continuation<Void, Object>() {
            @Override
            public Object then(Task<Void> task) throws Exception {

                //lets save some profile defaults on the device user
                Profile profile = new Profile();
                profile.setACL(User.publicReadOnly());
                profile.setUserId(User.getDeviceUserId());

                String callingCode = signUp.getCallingCode();

                profile.setCallingCode(callingCode);
                profile.pinInBackground();
                profile.saveInBackground();


                return null;
            }
        });
    }


}
