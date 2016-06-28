package com.psyphertxt.android.cyfa.backend.onesignal;

import com.psyphertxt.android.cyfa.util.Testable;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PushNotificationService extends IntentService {

    public PushNotificationService() {
        super(PushNotificationService.class.getName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PushNotificationService(String name) {
        super(PushNotificationService.class.getName());
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        new Testable.Spec().describe("on handle intent fired").expect("extra").run();

        List<String> strings = new ArrayList<>();
        Set<String> keys = extras.keySet();
        for (String key : keys) {
            strings.add(extras.get(key).toString());
        }

        new Testable.Spec().describe("see all extra values from notification").expect(strings).run();
       /* Intent notificationIntent = new Intent(this, ChatActivity.class);
        notificationIntent.putExtra(Config.KEY_NOTIFICATION, Config.NotificationType.NOTIFICATION);
        //notificationIntent.putExtra(Config.KEY_USER_ID, userId);
        EventBus.getDefault().postSticky(userId);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(notificationIntent);*/

    }

}

