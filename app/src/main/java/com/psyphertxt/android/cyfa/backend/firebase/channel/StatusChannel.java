package com.psyphertxt.android.cyfa.backend.firebase.channel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.ui.listeners.StatusListener;
import com.psyphertxt.android.cyfa.util.MessageUtils;
import com.psyphertxt.android.cyfa.util.TextUtils;

import android.os.Handler;

import java.util.HashMap;
import java.util.List;

public class StatusChannel {

    private final Firebase ref;
    private final String userId;
    private final ChildEventListener statusEventListener;
    private final Firebase status;
    public final static int UPDATE = 0;
    public final static int RESET = 1;

    private StatusChannel(Builder builder) {

        this.ref = builder.ref;
        this.userId = builder.userId;
        this.statusEventListener = builder.statusEventListener;
        this.status = builder.status;

    }

    public static class Builder {

        private Firebase ref;
        private Firebase status;
        private String userId;
        private Boolean isLoaded = false;
        private ChildEventListener statusEventListener;
        private final static int POPULATE_VIEW = 0;
        private final static int UPDATE_VIEW = 1;

        public Builder init() {
            ref = new Firebase(Config.getFirebaseURL());
            return this;
        }

        public Builder status(String userId) {

            if (ref == null) {
                throw new NullPointerException(TextUtils.channelErrors("base", "init"));
            }

            this.userId = userId;
            status = ref.child(Config.REF_STATUS).child(userId);
            return this;
        }

        public Builder loadAllStatusMessages(final StatusListener.onStatusLoaded onStatusLoaded) {

            if (status == null) {
                throw new NullPointerException(TextUtils.channelErrors("status"));
            }

            //retrieve messages from the server
            //for example if an existing user uninstalls and re-installs the app
            MessageUtils.onLoaded(status, new MessageListener.onMessageChange() {
                @Override
                public void onDataChange(final DataSnapshot child) {

                    if (!isLoaded) {

                        if (child.exists()) {

                            if (child.hasChildren()) {

                                for (DataSnapshot snapshot : child.getChildren()) {

                                    channel(snapshot, POPULATE_VIEW, onStatusLoaded);
                                }

                                onStatusLoaded.loaded();
                            }

                            isLoaded = true;

                        } else {

                            isLoaded = true;

                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                }
            });
            return this;
        }

        public Builder addStatusListener(final StatusListener.onStatusLoaded onStatusLoaded) {

            if (status == null) {
                throw new NullPointerException(TextUtils.channelErrors("status"));
            }

            statusEventListener = MessageUtils.onAdded(status, new MessageListener.onMessageAdded() {

                @Override
                public void onChildAdded(DataSnapshot child, String s) {
                    if (isLoaded) {
                        channel(child, UPDATE_VIEW, onStatusLoaded);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot child, String s) {
                    if (isLoaded) {
                        channel(child, UPDATE_VIEW, onStatusLoaded);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot child) {

                }

                @Override
                public void onChildMoved(DataSnapshot child, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            return this;
        }

        public Builder load() {

            if (statusEventListener == null) {
                throw new NullPointerException(TextUtils.listenerErrors("add status listener"));
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isLoaded = true;
                }
            }, 1000);
            return this;
        }

        /**
         * adds a status message locally or create
         * a slot when the user id doesn't exist
         *
         * @param status the message to be saved locally
         */
        private void addStatus(final Status status) {

            //find a parse object by user id
            findByUserId(status.getUserId(), new CallbackListener.callbackForResults() {
                @Override
                public void success(Object result) {
                    ParseObject parseObject = (ParseObject) result;
                    Status.getStatus(status, parseObject);
                    parseObject.pinInBackground();
                }

                @Override
                public void error(String error) {
                    ParseObject parseObject = new ParseObject(Config.KEY_STATUS);
                    Status.getStatus(status, parseObject);
                    parseObject.pinInBackground();
                }
            });


        }

        /**
         * method responsive for receiving data from the server and making it readable for UI
         * consumption
         *
         * @param snapshot       a firebase snapshot of the data
         * @param type           the type of data to determine how it's handling UPDATE_VIEW ||
         *                       POPULATE_VIEW
         * @param onStatusLoaded an event fired for UI consumption
         */
        private void channel(DataSnapshot snapshot, int type, StatusListener.onStatusLoaded onStatusLoaded) {

            DataSnapshot recent = snapshot.child(Config.KEY_RECENT);
            DataSnapshot count = snapshot.child(Config.KEY_COUNT);

            if (count.exists()) {

                if (recent.exists()) {

                    Status status = new Status(recent.getValue(HashMap.class));
                    status.setCount(count.getValue(int.class));

                    if (type == UPDATE_VIEW) {
                        if (status.getCount() == Config.NUMBER_ZERO) {
                            onStatusLoaded.updateView(status, StatusChannel.RESET);
                        } else {
                            onStatusLoaded.updateView(status, StatusChannel.UPDATE);
                            //  addStatus(status);
                        }
                    } else {
                        onStatusLoaded.populateView(status);
                        // addStatus(status);
                    }

                    //as soon as a status message is received
                    //download updates from firebase by constructing a path
                    //to the actual message
                    if (status.getMessageId() != null) {
                        sync(recent, status.getMessageId());
                    }
                }
            }
        }

        /**
         * retrieve the actual message from firebase by
         * constructing a message path
         *
         * @param snapshot  a recent channel snapshot of the received status message
         * @param messageId the gessage id of the last sent message
         */
        private void sync(DataSnapshot snapshot, String messageId) {

            //extract path of the conversationId
            String path = TextUtils.splitURI(snapshot.getRef().getParent().toString());

            //if path is not empty
            if (!path.equals(Config.EMPTY_STRING)) {

                //construct path to the last message
                Firebase inbox = ref.child(Config.REF_MESSAGES).child(path).child(messageId);

                //retrieve message from firebase
                MessageUtils.onLoaded(inbox, new MessageListener.onMessageChange() {
                    @Override
                    public void onDataChange(DataSnapshot child) {
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }

        public StatusChannel connect() {
            StatusChannel statusChannel = new StatusChannel(this);
            if (statusChannel.status == null) {
                throw new IllegalStateException(TextUtils.channelErrors("status")); // thread-safe
            }
            return statusChannel;
        }
    }

    /**
     * finds a locally stored status message by user id
     *
     * @param userId the users id
     */
    private static void findByUserId(String userId, final CallbackListener.callbackForResults callbackForResults) {
        ParseQuery.getQuery(Config.KEY_STATUS)
                .fromLocalDatastore()
                .whereEqualTo(Config.KEY_USER_ID, userId).getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e == null) {
                    callbackForResults.success(parseObject);
                } else {
                    callbackForResults.error(e.getMessage());
                }
            }
        });
    }

    public static void removeAll() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Config.KEY_STATUS);
        query.fromLocalDatastore().findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                try {
                    ParseObject.unpinAll(list);
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void disconnect() {
        if (ref != null) {
            if (statusEventListener != null && userId != null) {
                ref.child(Config.REF_STATUS).child(userId).removeEventListener(statusEventListener);
            }
        }
    }
}