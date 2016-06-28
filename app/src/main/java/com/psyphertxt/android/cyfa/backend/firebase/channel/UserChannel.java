package com.psyphertxt.android.cyfa.backend.firebase.channel;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.StatusListener;
import com.psyphertxt.android.cyfa.ui.listeners.TypingIndicatorListener;
import com.psyphertxt.android.cyfa.util.MessageUtils;
import com.psyphertxt.android.cyfa.util.TextUtils;

/**
 * manages all user status connections to firebase
 * including when user is active in a two way chat
 * handles changes to a users last sent message, timestamp, typing indicator
 * etc
 */
public class UserChannel {

    public static int RUN_TRANSACTION = 0;
    public static int UPDATE_CHILDREN = 1;

    private final Firebase ref;
    private final Firebase userStatusRef;
    private final Status userStatus;
    private final Firebase activeRef;
    private final Firebase countRef;
    private final String userId;
    private final String conversationId;
    private final ValueEventListener activeEventListener;
    private final ValueEventListener typingIndicatorEventListener;
    private Firebase typingIndicatorRef;
    private Boolean isLiveTyping = false;


    private UserChannel(Builder builder) {

        this.ref = builder.ref;
        this.userStatusRef = builder.userStatusRef;
        this.userStatus = builder.userStatus;
        this.activeRef = builder.activeRef;
        this.countRef = builder.countRef;
        this.userId = builder.userId;
        this.conversationId = builder.conversationId;
        this.activeEventListener = builder.ActiveEventListener;
        this.typingIndicatorEventListener = builder.typingIndicatorEventListener;
        this.typingIndicatorRef = builder.typingIndicatorRef;

    }

    public static class Builder {

        private Firebase ref;
        private Firebase userStatusRef;
        private Firebase typingIndicatorRef;
        private Status userStatus;
        private Firebase activeRef;
        private Firebase countRef;
        private String userId;
        private String conversationId;
        private ValueEventListener ActiveEventListener;
        private ValueEventListener typingIndicatorEventListener;

        public Builder init() {
            ref = new Firebase(Config.getFirebaseURL());
            return this;
        }

        public Builder channel(String userId, String conversationId) {

            if (ref == null) {
                throw new NullPointerException(TextUtils.channelErrors("base", "init"));
            }

            this.userId = userId;
            this.conversationId = conversationId;
            userStatusRef = ref.child(Config.REF_STATUS).child(userId).child(conversationId);
            return this;
        }

        public Builder active(Boolean isActive) {

            if (userStatusRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("status", "channel"));
            }

            userStatus = new Status();
            userStatus.setActive(isActive);
            activeRef = userStatusRef.child(Config.KEY_ACTIVE);
            activeRef.keepSynced(false);
            return this;
        }

        //convenient for device user
        public Builder active() {

            active(true);
            return this;
        }

        public Builder addActiveListener() {

            if (activeRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("active"));
            }

            ActiveEventListener = MessageUtils.onChange(activeRef, new MessageListener.onMessageChange() {
                @Override
                public void onDataChange(DataSnapshot child) {

                    if (child.getValue() instanceof Boolean) {

                        userStatus.setActive(child.getValue(Boolean.class));

                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return this;
        }

        public Builder count() {

            if (userStatusRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("status", "channel"));
            }

            countRef = userStatusRef.child(Config.KEY_COUNT);
            return this;
        }

        public Builder sync() {

            if (userStatusRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("status", "channel"));
            }

            userStatusRef.keepSynced(true);
            return this;
        }

        //TODO ADD POLICIES TO DETERMINE THE STATE OF TYPING "string typing" or "exactly what the user is typing"
        public Builder typingIndicator() {

            if (activeRef == null && userStatus == null) {
                throw new NullPointerException(TextUtils.channelErrors("active and status", "channel->active"));
            }

            userStatus.setTypingIndicator(TypingIndicatorListener.FINISHED);
            typingIndicatorRef = userStatusRef.child(Config.KEY_TYPING);
            return this;
        }

        public Builder addTypingIndicatorListener(final TypingIndicatorListener.onTypingIndicator onTypingIndicator) {

            if (typingIndicatorRef == null) {
                throw new NullPointerException(TextUtils.channelErrors("typing indicator"));
            }

            typingIndicatorEventListener = MessageUtils.onChange(typingIndicatorRef, new MessageListener.onMessageChange() {
                @Override
                public void onDataChange(DataSnapshot child) {

                    if (child.getValue() instanceof Long) {
                        userStatus.setTypingIndicator(child.getValue(int.class));
                        if (userStatus.getTypingIndicator() == TypingIndicatorListener.STARTED) {
                            onTypingIndicator.startedTyping();
                        } else {
                            onTypingIndicator.finishedTyping();
                        }
                    } else if (child.getValue() instanceof String) {
                        String text = child.getValue(String.class);
                        userStatus.setTypingIndicator(text);
                        if (!text.isEmpty()) {
                            onTypingIndicator.liveTyping(text);
                        } else {
                            onTypingIndicator.finishedTyping();
                        }
                    } else {
                        onTypingIndicator.finishedTyping();
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return this;
        }

        public UserChannel connect() {
            UserChannel userChannel = new UserChannel(this);
            if (userChannel.userStatusRef == null) {
                throw new IllegalStateException(TextUtils.channelErrors("status", "channel")); // thread-safe
            }
            return userChannel;
        }
    }

    /**
     *
     * @param bool
     */
    public void isActive(Boolean bool) {

        //#status channel
        //set device user as active
        userStatus.setActive(bool);
        activeRef.setValue(bool);
    }

    /**
     *
     * @return
     */
    public Boolean getActive() {
        return userStatus.getActive();
    }

    /**
     *
     * @param active
     */
    public void setActive(Boolean active) {
        userStatus.setActive(active);
    }

    /**
     *
     * @param status
     * @param type
     */
    public void updateStatus(final Status status, final int type) {

        if (type == UPDATE_CHILDREN) {

            userStatusRef.child(Config.KEY_RECENT).updateChildren(status);

        } else if (type == RUN_TRANSACTION) {

            userStatusRef.runTransaction(new Transaction.Handler() {

                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {

                    MutableData count = mutableData.child(Config.KEY_COUNT);
                    MutableData recent = mutableData.child(Config.KEY_RECENT);

                    if (count.getValue() == null) {

                        count.setValue(Config.NUMBER_ONE);

                    } else {

                        count.setValue((Long) count.getValue() + 1);

                    }

                    recent.setValue(status);
                    return Transaction.success(mutableData);

                }

                @Override
                public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                }
            });
        }
    }

    /**
     * find local status message by user id and explicitly set it to 0
     */
    private void resetLocally() {


        ParseObject parseObject;
        try {
            parseObject = ParseQuery.getQuery(Config.KEY_STATUS)
                    .fromLocalDatastore()
                    .whereEqualTo(Config.KEY_USER_ID, userId).getFirst();

            if (parseObject != null) {
                parseObject.put(Config.KEY_COUNT, Config.NUMBER_ZERO);
                parseObject.pinInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {


                    }
                });
            }

        } catch (ParseException e) {
            //TODO disable stacktrace in production
            //TODO use fabric to log error events
            e.printStackTrace();
        }
    }

    /**
     * reset all status message counts locally/server
     */
    public void reset(StatusListener.onReset onReset) {

        //do any resets or updates in activity
        onReset.reset();

        //reset locally stored status message by user id
        //  resetLocally();

        //reset status count on server
        if (countRef != null) {
            countRef.setValue(Config.NUMBER_ZERO);
        }

        //set type indicator to finished incase the network was hang
        if (typingIndicatorRef != null) {
            setTypingIndicator(TypingIndicatorListener.FINISHED);
        }

        isActive(true);
    }

    /**
     * method for checking if user is active or inactive
     *
     * @param onUser have event listeners to let the client know if user is active or inactive
     */
    public void user(StatusListener.onUser onUser) {

        if (userStatus.getActive() == null || !userStatus.getActive()) {

            onUser.inactive(this);

        } else {

            onUser.active(this);
        }
    }

    public void setTypingIndicator(Object typingIndicator) {

        if (typingIndicatorRef == null && userStatus == null) {
            throw new NullPointerException(TextUtils.channelErrors("active and status", "channel->active"));
        }

        userStatus.setTypingIndicator(typingIndicator);
        typingIndicatorRef.setValue(typingIndicator);
    }

    public void isLiveTyping(Boolean liveTyping) {
        this.isLiveTyping = liveTyping;


    }

    public Boolean isLiveTyping() {
        return this.isLiveTyping;

    }

    public void onDisconnect() {
        activeRef.onDisconnect().setValue(false);

        if (typingIndicatorRef != null) {
            typingIndicatorRef.onDisconnect().removeValue();
        }
    }

    public void disconnect() {

        if (ref != null) {

            if (conversationId != null && userId != null) {

                if (activeEventListener != null) {

                    ref.child(Config.REF_STATUS).child(userId).child(conversationId).child(Config.KEY_ACTIVE).removeEventListener(activeEventListener);
                }

                if (typingIndicatorEventListener != null) {

                    ref.child(Config.REF_STATUS).child(userId).child(conversationId).child(Config.KEY_TYPING).removeEventListener(typingIndicatorEventListener);
                }
            }
        }
    }
}