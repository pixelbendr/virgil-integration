package com.psyphertxt.android.cyfa.ui.listeners;

import com.psyphertxt.android.cyfa.backend.firebase.channel.UserChannel;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;

/**
 * A simple Callback interface to aid in network calls
 * when using third party libraries
 */

public class CallbackListener {

    public interface callbackForResults {

        void success(Object result);

        void error(String error);

    }

    public interface callback {

        void success();

        void error(String error);

    }

    public interface completion {

        void done();

    }

    public interface timer {

        void done();
        void running(long counter);

    }
}
