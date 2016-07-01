package com.psyphertxt.android.cyfa.model;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.backend.firebase.model.Message;
import com.psyphertxt.android.cyfa.util.SecurityUtils;
import com.psyphertxt.android.cyfa.util.Testable;
import com.psyphertxt.android.cyfa.util.TimeUtils;
import com.virgilsecurity.sdk.crypto.PrivateKey;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;

/**
 * Class for creating chat messages, receiving chat messages
 * and populating the the view
 */
public class Chat {

    private String userId;
    private String messageId;
    private String name;
    private String text;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    private String signature;

    private Firebase ref;
    private Long deliveredAt;
    private Long readAt;
    private HashMap<String, Object> content;
    private String contentUri;
    private int contentType = ContentType.TEXT;
    private int messageType = MessageType.REGULAR;
    private int deliveryStatus;
    private int direction;
    private Long timer;
    private Boolean isPinned = false;

    /**
     * @param message     a message object from the server
     * @param messageId   the id of the message which was received
     * @param profileName the name of the user or recipient
     */
    public void incomingMessages(Message message, String messageId, String profileName) {

        setUserId(message.getUserId());
        setMessageId(messageId);
        if (message.getDeliveryStatus() >= 0) {
            setDeliveryStatus(message.getDeliveryStatus());
        }
        if (message.getMessageType() >= 0) {
            setMessageType(message.getMessageType());
        }

        if (message.getContent() != null) {
            setContentType(message.getContentType());
            setContent(message.getContent());
        }
        setDirection(Direction.INCOMING);
        setName(profileName);
        if (message.getDeliveredAt() != null) {

            setDeliveredAt(message.getDeliveredAt());
        }

    }

    //virgil decryption
    public void messageDecryption(Message message, String deviceUserId, PrivateKey privateKey) {


        //  setText(message.getText());
        try {
            setText(SecurityUtils.decrypt(message.getText(), deviceUserId, privateKey));
        } catch (Exception e) {
            setText("error decrypting text");
        }
    }

    //TODO move params to public properties to reduce length of params

    /**
     * update messages when their being loaded since messages contain just userId
     * we need to resolve the id's and differentiate between deviceUser/sender from
     * contextUser/recipient
     *
     * @param message                a message object from the server
     * @param child                  a firebase ref to a single message
     * @param deviceUserId           the device user's id
     * @param contextUserProfileName the context user's profile name
     */
    public void updateMessages(Message message, DataSnapshot child,
                               String deviceUserId, String contextUserProfileName) {

        incomingMessages(message, child.getKey(), Config.KEY_TITLE_ME);
        setDirection(Direction.OUTGOING);
        setRef(child.getRef());

        //if message is from the device user
        //set name as context user profile name
        //and tag the message as incoming
        if (message.getUserId().equals(deviceUserId)) {

            setName(contextUserProfileName);
            setDirection(Chat.Direction.INCOMING);
        }
    }

    /**
     * @param text          the message body
     * @param messageRef    the id of the message being sent
     * @param contextUserId the id of the context user or recipient
     */
    public void outgoingMessages(String text, Firebase messageRef, String contextUserId) {

        setUserId(contextUserId);
        setMessageId(messageRef.getKey());
        setDeliveryStatus(DeliveryStatus.PENDING);
        setDirection(Direction.OUTGOING);
        setRef(messageRef);
        setText(text);
        setDeliveredAt(new Date().getTime());
        setName(Config.KEY_TITLE_ME);

    }

    public Firebase getRef() {
        return ref;
    }

    public void setRef(Firebase ref) {
        this.ref = ref;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String title) {
        this.name = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getContentType() {
        return contentType;
    }

    public HashMap<String, Object> getContent() {
        return content;
    }

    public void setContent(HashMap<String, Object> content) {
        this.content = content;
    }

    public String getContentUri() {
        return contentUri;
    }

    public void setContentUri(String contentUri) {
        this.contentUri = contentUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String id) {
        this.userId = id;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public Long getTimer() {
        return timer;
    }

    public void setTimer(Long timer) {
        this.timer = timer;
    }

    public String getCurrentTime() {

        if (deliveredAt != null) {
            return TimeUtils.getTimeAgo(deliveredAt);
        }

        return Config.EMPTY_STRING;
    }

    public String getDeliveredAt() {

        //outputs example fri, 06:44 pm
        return TimeUtils.formatTime(deliveredAt);
    }

    public void setReadAt(long readAt) {
        this.readAt = readAt;
    }

    public String getReadAt() {

        return TimeUtils.formatTime(readAt);
    }

    public void setDeliveredAt(long deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    //TODO move get label to Utils class or make it static
    //TODO determine how to handle media type string
    public String getLabel() {

        if (messageType == MessageType.PRIVATE) {
            return "Sent a private message";
        }

        switch (contentType) {
            case ContentType.IMAGE:
                return "Sent an image";
            case ContentType.AUDIO:
                return "Sent an audio";
            case ContentType.VIDEO:
                return "Sent a video";
            default:
                return getText();
        }
    }

    public void isPinned(boolean pinned) {
        this.isPinned = pinned;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public static final class MessageType {
        public static final int REGULAR = 0;
        public static final int PRIVATE = 1;
    }

    public static final class Direction {
        public static final int INCOMING = 0;
        public static final int OUTGOING = 1;
        public static final int GROUP = 2;
    }


}
