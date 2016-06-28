package com.psyphertxt.android.cyfa.ui.activity.startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.Settings;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.model.ContextUser;
import com.psyphertxt.android.cyfa.model.SignUp;
import com.psyphertxt.android.cyfa.ui.activity.StatusActivity;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.widget.MainActionBarActivity;
import com.psyphertxt.android.cyfa.util.ContactUtils;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.client.model.Identity;
import com.virgilsecurity.sdk.client.model.IdentityType;
import com.virgilsecurity.sdk.client.model.identity.ValidatedIdentity;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCard;
import com.virgilsecurity.sdk.client.model.publickey.VirgilCardTemplate;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

import java.util.HashMap;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class LoaderActivity extends MainActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        ButterKnife.inject(this);



        if (Config.IS_PROTO) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoaderActivity.this, StatusActivity.class);
                    startActivity(intent);
                }
            }, 4000);

        } else {

            final SignUp signUp = EventBus.getDefault().getStickyEvent(SignUp.class);
            settings = new Settings(LoaderActivity.this);

            if (!signUp.isExisting()) {

                newUser(signUp);

            } else {

                syncExistingUser();

            }
        }
    }

    private void syncExistingUser() {

        if (User.getDeviceUser().getProfileName() == null) {

            Settings settings = new Settings(this);
            SignUp signUp = new SignUp();
            signUp.setCallingCode(settings.getCallingCode());
            EventBus.getDefault().postSticky(signUp);

            Intent intent = new Intent(this, ProfileNameActivity.class);
            startActivity(intent);

        } else {


            //TODO use cloud code to retrieve profile and user contacts
            User user = User.getDeviceUser();
            Settings settings = new Settings(this);
            settings.setTheme(user.getTheme());

            //load status activity
            loadStatusActivity(this);

        }
    }

    private void newUser(final SignUp signUp) {
        User.setup(signUp, new CallbackListener.callback() {
            @Override
            public void success() {

                //TODO update text if syncing is taking long
                //TODO using a timer.
                ContactUtils.findContactsAsync(LoaderActivity.this, signUp.getCallingCode(), new CallbackListener.callbackForResults() {
                    @Override
                    public void success(Object object) {
                        ContextUser.create((HashMap<String, Object>) object);
                        loadStatusActivity(LoaderActivity.this);
                    }

                    @Override
                    public void error(String error) {
                        //TODO handle error gracefully
                        loadStatusActivity(LoaderActivity.this);
                    }
                });

            }

            @Override
            public void error(String error) {
                loadStatusActivity(LoaderActivity.this);
            }
        });
    }

    private static void loadStatusActivity(Context context) {
        Intent intent = new Intent(context, StatusActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
















