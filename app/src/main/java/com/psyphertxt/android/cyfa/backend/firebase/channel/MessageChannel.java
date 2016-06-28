package com.psyphertxt.android.cyfa.backend.firebase.channel;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.Media;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.backend.firebase.model.Status;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.model.DeliveryStatus;
import com.psyphertxt.android.cyfa.ui.listeners.CallbackListener;
import com.psyphertxt.android.cyfa.ui.listeners.MessageListener;
import com.psyphertxt.android.cyfa.util.MessageUtils;
import com.psyphertxt.android.cyfa.util.SecurityUtils;
import com.psyphertxt.android.cyfa.util.Testable;
import com.psyphertxt.android.cyfa.util.TextUtils;
import com.virgilsecurity.sdk.client.ClientFactory;
import com.virgilsecurity.sdk.crypto.KeyPair;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * manages the message layer of the application
 */
public class MessageChannel {

    private static Boolean IS_LOADED = false;

    private final Firebase ref;
    private final Firebase message;
    private final String contextUserId;
    private final String deviceUserId;
    private final String conversationId;
    private final ChildEventListener messageEventListener;

    private List<Chat> unreadMessages;
    private ValueEventListener childValueEventListener;
    private Firebase lastChild;
    private Status status;
    private Chat chat;
    private Firebase messageRef;
    private String timer = Config.TIMER_DEFAULT;

    private MessageChannel(Builder builder) {

        this.ref = builder.ref;
        this.message = builder.message;
        this.contextUserId = builder.contextUserId;
        this.deviceUserId = builder.deviceUserId;
        this.conversationId = builder.conversationId;
        this.messageEventListener = builder.messageEventListener;
    }

    public static class Builder {

        private Firebase ref;
        private Firebase message;
        private String deviceUserId;
        private String contextUserId;
        private String conversationId;
        private ChildEventListener messageEventListener;
        private Boolean isLoaded = false;

        /**
         * initialize the chat to firebase realtime service
         *
         * @param conversationId a required string for chatting
         * @return the builder object
         */
        public Builder init(String conversationId) {
            this.conversationId = conversationId;
            ref = new Firebase(Config.getFirebaseURL());
            return this;
        }

        /**
         * @param deviceUserId
         * @param contextUserId
         * @return
         */
        public Builder chatBetween(String deviceUserId, String contextUserId) {

            if (ref == null) {
                throw new NullPointerException(TextUtils.channelErrors("base", "init"));
            }

            // this.deviceUserId = User.getDeviceUserId();
            this.deviceUserId = deviceUserId;
            this.contextUserId = contextUserId;
            message = ref.child(Config.REF_MESSAGES).child(conversationId);
            return this;
        }

        /**
         * Load all previous messages cached or uncached
         */
        public Builder loadAllMessages(final MessageListener.onMessageLoaded onMessageLoaded) {

            if (message == null && messageEventListener == null) {
                throw new NullPointerException(TextUtils.channelErrors("message listener and message", "message and add message listener"));
            }

            if (message != null) {
                MessageUtils.onLoaded(message.limitToLast(Config.MESSAGE_LIMIT), new MessageListener.onMessageChange() {
                    @Override
                    public void onDataChange(DataSnapshot child) {

                        if (!isLoaded) {

                            if (child.exists()) {

                                if (child.hasChildren()) {

                                    for (DataSnapshot snapshot : child.getChildren()) {

                                        if (snapshot.getValue() instanceof HashMap) {

                                            Message message = new Message(snapshot.getValue(HashMap.class));
                                            onMessageLoaded.populateView(message, snapshot);

                                        }
                                    }
                                }

                                isLoaded = true;
                                MessageChannel.IS_LOADED = true;

                                onMessageLoaded.updateView();

                            } else {

                                isLoaded = true;
                                MessageChannel.IS_LOADED = true;

                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }

            return this;
        }

        /**
         * listener for message lifestyle
         *
         * @param onChatMessage callback with details of status of message
         */
        public Builder addMessageListener(final MessageListener.onChatMessage onChatMessage) {

            if (message == null) {
                throw new NullPointerException(TextUtils.channelErrors("message"));
            }

            messageEventListener = MessageUtils.onAdded(message.limitToLast(Config.MESSAGE_LIMIT), new MessageListener.onMessageAdded() {
                @Override
                public void onChildAdded(final DataSnapshot child, String s) {

                    channel(child, new MessageListener.onMessageChannel() {
                        @Override
                        public void get(Message message) {

                            //if it's device user fire a message received event
                            if (message.getUserId().equals(deviceUserId)) {
                                onChatMessage.received(message, child);

                            }
                        }
                    });
                }

                @Override
                public void onChildChanged(final DataSnapshot child, String s) {

                    channel(child, new MessageListener.onMessageChannel() {
                        @Override
                        public void get(Message message) {

                            //only messages with context users id can be marked as read
                            if (message.getUserId().equals(contextUserId)) {

                                //lets check if the message has been read
                                onChatMessage.read(message, child);

                            }

                            //lets check if the message was from the device and the message is private
                            if (message.getUserId().equals(deviceUserId) && Message.IS_PRIVATE) {

                                //lets check if it's a private message
                                onChatMessage.deleted(message, child);

                            }

                            //only messages with device users id can be edited
                            if (message.getUserId().equals(deviceUserId)) {

                                //lets check if it's an edited message
                                onChatMessage.edited(message, child);

                            }
                        }
                    });
                }

                @Override
                public void onChildRemoved(DataSnapshot child) {
                    onChatMessage.removed(child);
                }

                @Override
                public void onChildMoved(DataSnapshot child, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    onChatMessage.failed(firebaseError);
                }
            });

            return this;
        }

        /**
         * converts the message into a readable format
         */
        private void channel(DataSnapshot dataSnapshot, MessageListener.onMessageChannel onMessageChannel) {

            if (isLoaded) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.getValue() instanceof HashMap) {

                        Message message = new Message(dataSnapshot.getValue(HashMap.class));

                        if (message.getUserId() != null) {

                            onMessageChannel.get(message);

                        }
                    }
                }
            }
        }

        /**
         * calling this will start the messaging channel
         */
        public MessageChannel connect() {
            MessageChannel messageChannel = new MessageChannel(this);
            if (messageChannel.message == null && messageEventListener == null) {
                throw new IllegalStateException(TextUtils.channelErrors("message listener and message", "message and add message listener")); // thread-safe
            }
            return messageChannel;
        }
    }

    /**
     * a boolean representation of whether the message has been loaded or not
     */
    public Boolean isLoaded() {
        return MessageChannel.IS_LOADED;

    }

    public Chat getChat() {
        return chat;
    }

    public void createText(String text, int contentType) {

        //create a message id
        messageRef = message.push();

        //create a new chat instance
        //and tag it as outgoing because its being sent by the device user
        chat = new Chat();
        chat.setContentType(contentType);
        chat.outgoingMessages(text, messageRef, contextUserId);

        if (Message.IS_TIMER) {
            chat.setTimer(Long.valueOf(timer));
        }

        if (Message.IS_PRIVATE) {
            chat.setMessageType(Chat.MessageType.PRIVATE);
        } else {
            chat.setMessageType(Chat.MessageType.REGULAR);
        }
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    /**
     *
     * @param media
     */
    public void createMediaLink(Media media) {
        HashMap<String, Object> content = new HashMap<>();
        if (chat.getContent() != null) {
            content = chat.getContent();
        }
        content.put(SecurityUtils.createMediaId(), media);
        chat.setContent(content);
    }


    public void createStatus(String contextUserProfileName) {
        //lets update the device users status channel anytime a new message is sent
        status = new Status();
        status.setProfileName(contextUserProfileName);
        status.setLastMessage(chat.getLabel());
        status.setMessageId(chat.getMessageId());
        status.setTimestamp();
        status.setUserId(contextUserId);
    }

    /**
     * a method for sending messages from the device
     *
     * @param onMessage a callback indicating the status of the message pending|sent|unread
     */
    public void send(String encryption, final MessageListener.onMessage onMessage) {

        if (message == null) {
            throw new NullPointerException(TextUtils.channelErrors("message"));
        }

        if (messageRef == null) {
            throw new NullPointerException(TextUtils.channelErrors("chat message", "call createText method"));
        }

        //first mark it as pending
        onMessage.pending(chat);

        //encrypt message before sending out
        Message message = new Message();
        message.setText(chat.getText());
        try {
         //   message.setText(SecurityUtils.encrypt(encryption, chat.getText()));
//            message.setText(SecurityUtils.encrypt());

        } catch (Exception e) {
            new Testable.Spec().describe("encryption failed").expect("failed").run();
        }
        message.setText(chat.getText());

        //using multi-location update lets create a parent object to hold all other references
        Map<String, Object> updatedUserData = new HashMap<>();

        //path to update
        Firebase statusRef = ref.child(Config.REF_STATUS).child(deviceUserId).child(conversationId);
        updatedUserData.put(messageRef.getPath().toString(), Message.fromChat(message, chat));

        if (status != null) {
            Firebase recentRef = statusRef.child(Config.KEY_RECENT);
            Firebase countRef = statusRef.child(Config.KEY_COUNT);
            updatedUserData.put(recentRef.getPath().toString(), status);
            updatedUserData.put(countRef.getPath().toString(), 0);
        }

        ref.updateChildren(updatedUserData, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                if (firebaseError == null) {

                    //set message as sent for the chat object
                    chat.setDeliveryStatus(DeliveryStatus.SENT);

                    if (!Message.IS_PRIVATE) {

                        //the message was successfully saved lets update it with a status of SENT
                        messageRef.updateChildren(Message.setAsSent(), new Firebase.CompletionListener() {

                            @Override
                            public void onComplete(FirebaseError firebaseError, final Firebase firebase) {

                                if (firebaseError == null) {

                                    //messages have been sent to the server but unread
                                    //lets fire this event so we can take some actions
                                    //like adding the message to a queue
                                    onMessage.unread(chat, firebase);
                                }
                            }
                        });
                    }

                    //message have been sent so lets do something like
                    //send a notification when user is offline
                    //or a status message when user is inactive
                    onMessage.sent(chat);

                }

                //TODO set objects to null if it helps clean up memory
            }
        });
    }

    //virgil-integration
    public void send(ClientFactory clientFactory, KeyPair keyPair, final MessageListener.onMessage onMessage) {

        if (message == null) {
            throw new NullPointerException(TextUtils.channelErrors("message"));
        }

        if (messageRef == null) {
            throw new NullPointerException(TextUtils.channelErrors("chat message", "call createText method"));
        }

        //first mark it as pending
        onMessage.pending(chat);

        //encrypt message before sending out
        Message message = new Message();
         message.setText(chat.getText());

        //virgil-integration
        try {
            //   message.setText(SecurityUtils.encrypt(encryption, chat.getText()));
            Map<String,Object> map = SecurityUtils.encrypt(clientFactory,keyPair,chat.getText());
            message.setText((String) map.get("text"));
            message.setSignature((String) map.get("signature"));

        } catch (Exception e) {
            new Testable.Spec().describe("encryption failed").expect("failed").run();
        }
   //     message.setText(chat.getText());

        //using multi-location update lets create a parent object to hold all other references
        Map<String, Object> updatedUserData = new HashMap<>();

        //path to update
        Firebase statusRef = ref.child(Config.REF_STATUS).child(deviceUserId).child(conversationId);
        updatedUserData.put(messageRef.getPath().toString(), Message.fromChat(message, chat));

        if (status != null) {
            Firebase recentRef = statusRef.child(Config.KEY_RECENT);
            Firebase countRef = statusRef.child(Config.KEY_COUNT);
            updatedUserData.put(recentRef.getPath().toString(), status);
            updatedUserData.put(countRef.getPath().toString(), 0);
        }

        ref.updateChildren(updatedUserData, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                if (firebaseError == null) {

                    //set message as sent for the chat object
                    chat.setDeliveryStatus(DeliveryStatus.SENT);

                    if (!Message.IS_PRIVATE) {

                        //the message was successfully saved lets update it with a status of SENT
                        messageRef.updateChildren(Message.setAsSent(), new Firebase.CompletionListener() {

                            @Override
                            public void onComplete(FirebaseError firebaseError, final Firebase firebase) {

                                if (firebaseError == null) {

                                    //messages have been sent to the server but unread
                                    //lets fire this event so we can take some actions
                                    //like adding the message to a queue
                                    onMessage.unread(chat, firebase);
                                }
                            }
                        });
                    }

                    //message have been sent so lets do something like
                    //send a notification when user is offline
                    //or a status message when user is inactive
                    onMessage.sent(chat);

                }

                //TODO set objects to null if it helps clean up memory
            }
        });
    }

    /**
     * method for deleting private messages
     *
     * @param chat     the chat message to be deleted
     * @param callback a callback with success or error for deletion status
     */
    public static void deletePrivateMessage(String encryption, final Chat chat, final CallbackListener.callback callback) throws GeneralSecurityException {
        if (chat != null) {
            if (chat.getRef() != null) {
                chat.getRef().updateChildren(Message.setAsDeleted(encryption), new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            callback.success();
                        } else {
                            callback.error("error");
                        }
                    }
                });
            }
        }
    }

    /**
     * method for editing already sent messages
     *
     * @param chat     the chat object to edit
     * @param message  the edited message object
     * @param callback a callback indicating
     */
    public static void editMessage(String encryption, final Chat chat, String message, final CallbackListener.callback callback) throws GeneralSecurityException {

        if (chat != null) {
            if (chat.getRef() != null) {

                HashMap<String, Object> update = new HashMap<>();

                //check if the message has been delivered or not
                //and mark that appropriately
                if (chat.getDeliveryStatus() == DeliveryStatus.SENT ||
                        chat.getDeliveryStatus() == DeliveryStatus.PENDING) {
                    update = Message.setAsSent(message);
                } else if (chat.getDeliveryStatus() == DeliveryStatus.READ ||
                        chat.getDeliveryStatus() == DeliveryStatus.EDITED) {
                    update = Message.setAsEdited(encryption, message);
                }

                chat.getRef().updateChildren(update, new Firebase.CompletionListener() {
                    @Override
                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                        if (firebaseError == null) {
                            callback.success();
                        } else {
                            callback.error("error");
                        }
                    }
                });
            } else {
                callback.error("error");
            }
        } else {
            callback.error("error");
        }
    }

    public static void updateTimedMessage(final Long counter, Chat chat) {

        chat.getRef().runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                MutableData timer = mutableData.child(Config.KEY_TIMER);

                if (timer.getValue() != null) {

                    if (timer.getValue(Long.class) != Config.NUMBER_ZERO) {
                        timer.setValue(counter);
                    } else {
                        timer.setValue(Config.NUMBER_ZERO);
                    }
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {
            }
        });
    }

    /**
     * a method for marking messages as read on the server
     *
     * @param dataSnapshot  a firebase snapshot of the message
     * @param onMessageRead a callback for when a message has been mark as read
     */
    public void markMessagesAsRead(DataSnapshot dataSnapshot, final MessageListener.onMessageRead onMessageRead) {

        //construct a reference to the location of the exact message
        Firebase updates = dataSnapshot.getRef();

        //update all unread messages on the server as read
        updates.updateChildren(Message.setAsRead(), new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError == null) {
                    onMessageRead.updateView(firebase.getKey());
                }
            }
        });
    }

    /**
     * a method for updating all sent messages to read after they have actually been read
     *
     * @param message       the message that was read
     * @param child         a firebase snapshot of the message that was read
     * @param onMessageRead a callback for when a message has been mark as read
     */
    public void updateMessagesAsRead(Message message, final DataSnapshot child, final MessageListener.onMessageRead onMessageRead) {

        if (message.getUserId().equals(deviceUserId)) {

            //detect all incoming messages with status as sent
            //and set them to read
            if (message.getDeliveryStatus() == DeliveryStatus.SENT) {

                markMessagesAsRead(child, new MessageListener.onMessageRead() {
                    @Override
                    public void updateView(String messageId) {
                        onMessageRead.updateView(messageId);
                    }
                });
            }
        }
    }

    /**
     * contains a list of unread messages from context user or recipient
     *
     * @param message the server message sent as an object
     * @param chat    the chat needed for populating the UI
     */
    public void unreadMessagesQueue(Message message, Chat chat) {

        if (unreadMessages == null) {
            unreadMessages = new ArrayList<>();
        }

        if (message.getUserId().equals(contextUserId)) {

            if (message.getDeliveryStatus() == DeliveryStatus.SENT) {

                //add chat objects for all unread messages
                unreadMessages.add(chat);

            }
        }
    }

    /**
     * contains a list of unread messages from context user or recipient
     *
     * @param firebase a firebase of object of the unread message
     * @param chat     the chat needed for populating the UI
     */
    public void unreadMessagesQueue(Firebase firebase, Chat chat) {

        if (unreadMessages == null) {
            unreadMessages = new ArrayList<>();
        }
        chat.setRef(firebase.getRef());
        unreadMessages.add(chat);

    }

    /**
     * have event listener that listens to the last unread messages
     * and fires have event to alert all other unread messages before it
     *
     * @param onMessageRead the event for when the last message is read
     */
    public void lastUnreadMessageQueueListener(final MessageListener.onMessageRead onMessageRead) {

        if (unreadMessages != null) {

            if (unreadMessages.size() > Config.ZERO_LENGTH) {

                childValueEventListener = null;
                lastChild = unreadMessages.get(unreadMessages.size() - Config.NUMBER_ONE).getRef();
                childValueEventListener = MessageUtils.onChange(lastChild, new MessageListener.onMessageChange() {

                    @Override
                    public void onDataChange(DataSnapshot child) {

                        if (child.exists()) {

                            if (child.getValue() instanceof HashMap) {

                                Message message = new Message(child.getValue(HashMap.class));

                                if (message.getDeliveryStatus() == DeliveryStatus.READ) {

                                    for (Chat unreadChat : unreadMessages) {
                                        onMessageRead.updateView(unreadChat.getMessageId());
                                    }

                                    //remove event
                                    lastChild.removeEventListener(childValueEventListener);
                                    unreadMessages = new ArrayList<>();

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    /**
     * disconnect reference to preserve memory
     */
    public void disconnect() {

        if (ref != null) {

            if (contextUserId != null && messageEventListener != null) {
                ref.child(Config.REF_MESSAGES).child(conversationId).removeEventListener(messageEventListener);
            }

            if (lastChild != null && childValueEventListener != null) {
                lastChild.removeEventListener(childValueEventListener);
            }
        }
    }
}