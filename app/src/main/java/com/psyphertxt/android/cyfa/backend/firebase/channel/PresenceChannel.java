package com.psyphertxt.android.cyfa.backend.firebase.channel;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.MessageToken;
import com.psyphertxt.android.cyfa.backend.firebase.model.Presence;
import com.psyphertxt.android.cyfa.ui.listeners.PresenceListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.TokenListener;
import com.psyphertxt.android.cyfa.util.MessageUtils;
import com.psyphertxt.android.cyfa.util.TextUtils;

import java.util.HashMap;

/**
 * Holds all connections to firebase including
 * when the user has connection, when the user is active
 * and also UI related functionality like when the user is
 * typing something
 */
public class PresenceChannel {

    private final Firebase ref;
    private final ValueEventListener connectionListener;
    private final ValueEventListener userListener;
    private final String userId;
    private final Firebase connectionRef;
    private Boolean online = false;

    private PresenceChannel(Builder builder) {
        this.ref = builder.ref;
        this.connectionListener = builder.connectionListener;
        this.userListener = builder.userListener;
        this.userId = builder.userId;
        this.connectionRef = builder.connectionRef;

    }

    public static class Builder {
        private Firebase ref;
        private Firebase connectionRef;
        private Firebase lastSeen;
        protected ValueEventListener connectionListener;
        private ValueEventListener userListener;
        private String userId;
        private Presence userPresence;

        public Builder init() {
            ref = new Firebase(Config.getFirebaseURL());
            return this;
        }

        //TODO test this for accuracy
        private void getServerTime() {
            Firebase offsetRef = ref.child(Config.REF_SERVER_TIME_OFFSET);
            offsetRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    double offset = snapshot.getValue(Double.class);
                    double estimatedServerTimeMs = System.currentTimeMillis() + offset;
                    //mLastSeen = "LAST SEEN:" + estimatedServerTimeMs;

                }

                @Override
                public void onCancelled(FirebaseError error) {
                    System.err.println("Listener was cancelled");
                }
            });
        }

        public Builder connection(String userId) {
            if (ref == null) {
                throw new NullPointerException(TextUtils.channelErrors("base", "init"));
            }
            // since I can connect from multiple devices, we store each connectionRef instance separately
            // any time that connectionsRef's value is null (i.e. has no children) I am offline
            //TODO add push to this reference to take care of multiple devices
            this.userId = userId;
            Firebase userRef = ref.child(Config.REF_USERS).child(userId);
            userRef.keepSynced(false);
            connectionRef = userRef.child(Config.REF_CHILD_CONNECTED);
            return this;
        }

        public Builder lastSeen() {
            if (connectionRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("connection"));
            }
            lastSeen = connectionRef.child(Config.KEY_LAST_SEEN);
            return this;
        }

        public Builder addConnectionListener(final PresenceListener.OnConnection connectedListener) {

            if (connectionRef == null && lastSeen == null) {
                throw new NullPointerException(TextUtils.channelErrors("connection & last seen", "connection->lastSeen"));
            }

            connectionListener = MessageUtils.onChange(ref.child(Config.REF_CONNECTION_STATUS),
                    new MessageListener.onMessageChange() {
                        @Override
                        public void onDataChange(DataSnapshot child) {
                            boolean connected = child.getValue(Boolean.class);
                            if (connected) {
                                connectionRef.setValue(Presence.setAsOnline());
                                // when this device disconnects, remove the whole value connected:true
                                connectionRef.onDisconnect().removeValue();
                                // when I disconnect, update the last time I was seen online
                                lastSeen.onDisconnect().setValue(ServerValue.TIMESTAMP);
                                connectedListener.onConnected();
                            } else {
                                connectedListener.onDisconnected();
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            connectedListener.onCancelled();
                        }
                    });

            return this;
        }

        public Builder addUserPresenceListener(final PresenceListener.onConnectionChange onConnectionChange) {
            if (connectionRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("connection"));
            }
            userPresence = new Presence();
            userListener = MessageUtils.onChange(connectionRef, new MessageListener.onMessageChange() {

                @Override
                public void onDataChange(DataSnapshot child) {
                    if (child.getValue() instanceof HashMap) {
                        userPresence = new Presence(child.getValue(HashMap.class));
                        // TestUtils.spec(userId, "is context user is online ?", userPresence.getOnline());
                        if (userPresence.getOnline() != null && userPresence.getOnline()) {
                            onConnectionChange.online();
                        } else {
                            if (userPresence.getLastSeen() != null) {
                                String status = Config.LAST_SEEN_STRING + userPresence.getLastSeen();
                                onConnectionChange.lastSeen(status);
                            } else {
                                onConnectionChange.offline();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    onConnectionChange.cancelled();
                }
            });

            return this;
        }

        public Builder addTokenListener(final TokenListener.onTokenChange onTokenChange) {
            MessageToken.onChange(new TokenListener.onTokenChange() {
                @Override
                public void valid() {
                    onTokenChange.valid();
                }

                @Override
                public void invalid() {
                    onTokenChange.invalid();
                }
            });
            return this;
        }

        public PresenceChannel start() {
            PresenceChannel presenceChannel = new PresenceChannel(this);
            if (presenceChannel.connectionRef == null) {
                throw new IllegalStateException(TextUtils.channelErrors("connection")); // thread-safe
            }
            return presenceChannel;
        }
    }

    public void setOnline(Boolean isPresence) {
        this.online = isPresence;
    }

    public void presence(PresenceListener.OnPresence onPresence) {
        if (online == null) {
            throw new NullPointerException("set is presence values in online/offline/lastSeen listeners before calling presence");
        }
        if (online) {
            onPresence.online();
        } else {
            onPresence.offline();
        }
    }

    public void stop() {
        if (ref != null) {
            if (connectionListener != null) {
                ref.child(Config.REF_CONNECTION_STATUS).removeEventListener(connectionListener);
            }
            if (userListener != null && userId != null) {
                ref.child(Config.REF_USERS).child(userId).child(Config.REF_CHILD_CONNECTED).removeEventListener(userListener);
            }
        }
    }
}

