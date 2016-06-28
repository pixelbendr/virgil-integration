package com.psyphertxt.android.cyfa.ui.listeners;

/**
 * Listeners for connection changes on firebase
 * or any websocket service
 */
public class PresenceListener {

    public interface onConnectionChange {

        void online();

        void lastSeen(String lastSeen);

        void offline();

        void cancelled();

    }

    public interface OnConnection {

        void onConnected();

        void onDisconnected();

        void onCancelled();

    }

    public interface OnPresence {

        void online();

        void offline();
    }

}
