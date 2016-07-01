package com.psyphertxt.android.cyfa.ui.activity;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.MainApplication;
import com.psyphertxt.android.cyfa.R;
import com.psyphertxt.android.cyfa.backend.firebase.channel.PresenceChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.MessageToken;
import com.psyphertxt.android.cyfa.backend.parse.User;
import com.psyphertxt.android.cyfa.backend.parse.UserAccount;
import com.psyphertxt.android.cyfa.ui.activity.startup.StartUpActivity;
import com.psyphertxt.android.cyfa.ui.adapters.StatusPagerAdapter;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.PresenceListener;
import com.psyphertxt.android.cyfa.ui.listeners.TokenListener;
import com.psyphertxt.android.cyfa.ui.widget.Alerts;
import com.psyphertxt.android.cyfa.ui.widget.MainActionBarActivity;
import com.psyphertxt.android.cyfa.util.FileUtils;
import com.psyphertxt.android.cyfa.util.Foreground;
import com.psyphertxt.android.cyfa.util.SecurityUtils;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.crypto.KeyPair;
import com.virgilsecurity.sdk.crypto.KeyPairGenerator;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Activity for displaying status messages and update information related to the app
 * or from a user location. This activity is assumed to be the most viewed activity in
 * an applications lifetime
 */

public class StatusActivity extends MainActionBarActivity implements PresenceListener.OnConnection,
        TokenListener.onTokenChange,Foreground.Listener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.tabs)
    TabLayout tabLayout;
    
/*    @InjectView(R.id.floatingButtonMenu)
    FloatingActionsMenu mFloatingActionsMenu;*/

    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    @InjectView(android.R.id.title)
    TextView txtProfileName;

    @InjectView(R.id.text_connection_status)
    TextView txtConnectionStatus;

    private PresenceChannel presence;
    private Boolean isToken = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.inject(this);
        setSupportActionBar(toolbar);

        String deviceUserId = User.getDeviceUserId();
        txtProfileName.setText(User.getDeviceUser().getProfileName());

        viewPager.setAdapter(new StatusPagerAdapter(this, getSupportFragmentManager()));

        tabLayout.setupWithViewPager(viewPager);

        //checks to see if there is an active web socket connection
        presence = new PresenceChannel.Builder()
                .init()
                .connection(deviceUserId)
                .lastSeen()
                .addConnectionListener(this)
                .addTokenListener(this)
                .start();

        //lunch chat activity if is notification is true
        if (MainApplication.IS_NOTIFICATION) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Config.KEY_NOTIFICATION, Config.NotificationType.NOTIFICATION);
            startActivity(intent);
            MainApplication.IS_NOTIFICATION = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case FileUtils.PICK_PHOTO_REQUEST:
                /*Intent cameraIntent = new Intent(this, CameraActivity.class);
                cameraIntent.setData(data.getData());
                startActivity(cameraIntent);*/
                break;
        }

       /* if (requestCode == 0) {
            Uri photoUri = intent.getData();
            // Get the bitmap in according to the width of the device
         //   Bitmap bitmap = ImageUtility.decodeSampledBitmapFromPath(photoUri.getPath(), size.x, size.x);
            // ((ImageView) findViewById(R.id.image)).setImageBitmap(bitmap);
            Alerts.show(StatusActivity.this, "bitmap", intent.toString());
        }
*/
        /*final Uri mMediaUri;

        if (resultCode == RESULT_OK) {
            //add it to the gallery
            if (requestCode == FileUtils.PICK_PHOTO_REQUEST) {
                if (data != null) {
                    mMediaUri = data.getData();
                } else {
                    Alerts.show(StatusActivity.this, "Error", "something bad bad happened");
                }

            } else {

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

        } else if (resultCode != RESULT_CANCELED) {

            Alerts.show(StatusActivity.this, "Error", "something bad happened");
        }*/
    }

    @Override
    public void onBecameForeground() {
        if (MainApplication.IS_NOTIFICATION) {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(Config.KEY_NOTIFICATION, Config.NotificationType.NOTIFICATION);
            startActivity(intent);
            MainApplication.IS_NOTIFICATION = false;
        }
    }

    @Override
    public void onBecameBackground() {
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presence.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                UserAccount userAccount = new UserAccount();
                userAccount.logout(this, StartUpActivity.class);
                break;

            case R.id.action_settings:
                Intent intent = new Intent(StatusActivity.this, SettingsActivity.class);
                startActivity(intent);

            case R.id.action_collapse:
                Intent intent2 = new Intent(StatusActivity.this, ScrollingActivity.class);
                startActivity(intent2);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected() {
        Alerts.progress(this, true);
    }

    @Override
    public void onDisconnected() {
        Alerts.progress(this, false);
    }

    @Override
    public void onCancelled() {
        txtConnectionStatus.setText(R.string.offline_text);
    }

    @Override
    public void valid() {
        //  Alerts.progress(this, false);
    }

    @Override
    public void invalid() {
        if (!isToken) {
            isToken = true;
            // Alerts.progress(this, true);
            SecurityUtils.generateToken(new CallbackListener.callbackForResults() {

                @Override
                public void success(Object result) {
                    MessageToken.validate((String) result);
                    isToken = false;
                    //  Alerts.progress(StatusActivity.this, false);
                }

                @Override
                public void error(String error) {
                    isToken = false;
                    //  Alerts.progress(StatusActivity.this, false);
                }
            });
        }
    }
}
