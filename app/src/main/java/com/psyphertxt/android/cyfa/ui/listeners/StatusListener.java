package com.psyphertxt.android.cyfa.ui.listeners;

import com.psyphertxt.android.cyfa.backend.firebase.channel.UserChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;

import java.util.Map;

public class StatusListener {

    public interface onStatusLoaded {

        void populateView(Map status);

        void updateView(Map status,int type);

        void loaded();

    }

    public interface onStatusReceived {

        void notify(Status status);

    }

    public interface onUser {

        void active(UserChannel userChannel);

        void inactive(UserChannel userChannel);
    }

    public interface onReset {

        void reset();

    }
}
