package com.psyphertxt.android.cyfa.backend.parse;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Query;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.TransactionCount;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.util.SecurityUtils;
import com.psyphertxt.android.cyfa.util.Testable;

/**
 * Class which is responsible for mapping
 * user chat sessions. This class acts
 * like a friend request utility for chat channels
 */

@ParseClassName("People")
public class People extends ParseObject {

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public String getUserId() {
        return getString(Config.KEY_USER_ID);
    }

    public void setContextUserId(String contextUserId) {
        put(Config.KEY_CONTEXT_USER_ID, contextUserId);
    }

    public String getContextUserId() {
        return getString(Config.KEY_CONTEXT_USER_ID);
    }

    public void setConversationId(String conversationId) {
        put(Config.KEY_CONVERSATION_ID, conversationId);
    }

    public String getConversationId() {
        return getString(Config.KEY_CONVERSATION_ID);
    }

    public Boolean isAccepted() {
        return getBoolean(Config.KEY_ACCEPTED);
    }

    public void isAccepted(Boolean accepted) {
        put(Config.KEY_ACCEPTED, accepted);
    }

    /**
     * retrieve or create conversation id for people
     *
     * @param callbackForResults a string with the created or retrieved conversation id
     */
    public static void getConversationId(final String contextUserId, final CallbackListener.callbackForResults callbackForResults) {

        //create a reference to the root location
        final Firebase firebase = new Firebase(Config.getFirebaseURL());

        //create a query to a path consisting of the device user id and context user id as a child
        Query session = firebase.child(Config.REF_SESSION).child(User.getDeviceUserId()).child(contextUserId);
        session.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //if path exist retrieve conversation id
                if (dataSnapshot.exists()) {
                    callbackForResults.success(dataSnapshot.child(Config.REF_CHILD_SESSION).getValue(String.class));
                } else {

                    //if path doesn't exist create another query to context user id first and device user id as a child
                    Query session = firebase.child(Config.REF_SESSION).child(contextUserId).child(User.getDeviceUserId());
                    session.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //if path exist retrieve conversation id
                            if (dataSnapshot.exists()) {
                                callbackForResults.success(dataSnapshot.child(Config.REF_CHILD_SESSION).getValue(String.class));
                            } else {

                                String conversationId = SecurityUtils.createConversationId();
                                //if both queries fail, create a new conversation id
                                Firebase conversationRef = dataSnapshot.getRef();

                                //set values on the context user path with device user as child
                                conversationRef.child(Config.REF_CHILD_SESSION).setValue(conversationId);

                                //set values on the device user path with context user as child
                                firebase.child(Config.REF_SESSION).child(User.getDeviceUserId()).child(contextUserId).child(Config.REF_CHILD_SESSION).setValue(conversationId);

                                callbackForResults.success(conversationId);
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            callbackForResults.error(firebaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                callbackForResults.error(firebaseError.getMessage());
            }
        });
    }

    public static void setUpChannel(final String contextUserId, final CallbackListener.callbackForResults callbackForResults) {

        //create a reference to the root location
        final Firebase firebase = new Firebase(Config.getFirebaseURL());
        Firebase deviceUserPath = firebase.child(Config.REF_SESSION).child(User.getDeviceUserId()).child(contextUserId).child(Config.REF_CHILD_SESSION);
        Firebase contextUserPath = firebase.child(Config.REF_SESSION).child(contextUserId).child(User.getDeviceUserId()).child(Config.REF_CHILD_SESSION);
        final String conversationId = SecurityUtils.createConversationId();

        final TransactionCount transactionCount = new TransactionCount();

        deviceUserPath.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(conversationId);

                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (firebaseError == null) {
                    if (transactionCount.getCount() == -1) {
                        transactionCount.setCount(1);
                        callbackForResults.success(dataSnapshot.getValue(String.class));
                    }
                } else {
                    callbackForResults.error(firebaseError.getMessage());
                }
            }
        });

        contextUserPath.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(conversationId);

                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
                if (firebaseError == null) {
                    if (transactionCount.getCount() == -1) {
                        transactionCount.setCount(1);
                        callbackForResults.success(dataSnapshot.getValue(String.class));
                    }
                } else {
                    callbackForResults.error(firebaseError.getMessage());
                }
            }
        });
    }
}
