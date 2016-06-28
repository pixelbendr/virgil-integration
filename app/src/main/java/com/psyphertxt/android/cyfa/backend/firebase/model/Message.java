package com.psyphertxt.android.cyfa.backend.firebase.model;

import com.psyphertxt.android.cyfa.Config;
import com.psyphertxt.android.cyfa.model.Chat;
import com.psyphertxt.android.cyfa.model.ContentType;
import com.psyphertxt.android.cyfa.model.DeliveryStatus;

import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message extends HashMap<String, Object> {

    public static Boolean IS_PRIVATE = false;
    public static Boolean IS_TIMER = false;

    public Message() {
        super();
    }

    public Message(Map map) {
        super(map);
    }

    public String getUserId() {
        return (String) get(Config.KEY_USER_ID);
    }

    public void setUserId(String userId) {
        put(Config.KEY_USER_ID, userId);
    }

    public String getText() {
        return (String) get(Config.KEY_TEXT);
    }

    public void setText(String text) {
        put(Config.KEY_TEXT, text);
    }

    public int getDeliveryStatus() {
        return (int) get(Config.KEY_STATUS);
    }

    public void setDeliveryStatus(int status) {
        put(Config.KEY_STATUS, status);
    }

    public int getMessageType() {
        return (int) get(Config.KEY_TYPE);
    }

    public void setMessageType(int messageType) {
        put(Config.KEY_TYPE, messageType);
    }

    public HashMap<String, Object> getContent() {
        return (HashMap<String, Object>) get(Config.KEY_CONTENT);
    }

    public void setContent(HashMap<String, Object> contentType) {
        put(Config.KEY_CONTENT, contentType);
    }

    public Integer getTimer() {
        return (Integer) get(Config.KEY_TIMER);
    }

    public void setTimer(Integer timer) {
        put(Config.KEY_TIMER, timer);
    }

    public String getSignature() {
        return (String) get(Config.KEY_SIGNATURE);
    }

    public void setSignature(String signature) {
        put(Config.KEY_SIGNATURE, signature);
    }

    public int getContentType() {
        return (int) get(Config.KEY_CONTENT_TYPE);
    }

    public void setContentType(int contentType) {
        put(Config.KEY_CONTENT_TYPE, contentType);
    }

    public Long getDeliveredAt() {
        return (Long) get(Config.KEY_DELIVERED_AT);
    }

    public void setDeliveredAt(long deliveredAt) {
        put(Config.KEY_DELIVERED_AT, deliveredAt);
    }

    public Long getReadAt() {
        return (Long) get(Config.KEY_READ_AT);
    }

    public void setReadAt(long readAt) {
        put(Config.KEY_READ_AT, readAt);
    }

    public static Message fromChat(final Message message, final Chat chat) {

        message.setUserId(chat.getUserId());
        message.setDeliveryStatus(chat.getDeliveryStatus());
        message.setMessageType(chat.getMessageType());
        message.setDeliveredAt(new Date().getTime());
        message.setContentType(chat.getContentType());

        if (chat.getTimer() != null) {
            message.setTimer((int) (long) chat.getTimer());
        }

        if (message.getContentType() != ContentType.TEXT) {
            HashMap<String, Object> content = new HashMap<>();
            content.put(chat.getMessageId(), chat.getContent());
            message.setContent(content);
        }

        return message;

    }

    public static HashMap<String, Object> setAsSent() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_STATUS, DeliveryStatus.SENT);
        return hashMap;
    }

    public static HashMap<String, Object> setAsSent(String message) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_STATUS, DeliveryStatus.SENT);
        hashMap.put(Config.KEY_TEXT, message);
        hashMap.put(Config.KEY_DELIVERED_AT, new Date().getTime());
        return hashMap;
    }

    public static HashMap<String, Object> setAsDeleted(String encryption) throws GeneralSecurityException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_STATUS, DeliveryStatus.DELETED);
       // hashMap.put(Config.KEY_TEXT, SecurityUtils.encrypt(encryption, Config.DELETED_MESSAGE));
        hashMap.put(Config.KEY_TEXT, Config.DELETED_MESSAGE);
        //TODO use firebase server time
        hashMap.put(Config.KEY_DELIVERED_AT, new Date().getTime());
        return hashMap;
    }

    public static HashMap<String, Object> setAsEdited(String encryption, String message) throws GeneralSecurityException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_STATUS, DeliveryStatus.EDITED);
       // hashMap.put(Config.KEY_TEXT, SecurityUtils.encrypt(encryption, message));
        hashMap.put(Config.KEY_TEXT, message);
        hashMap.put(Config.KEY_DELIVERED_AT, new Date().getTime());
        return hashMap;
    }

    public static HashMap<String, Object> setAsRead() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Config.KEY_STATUS, DeliveryStatus.READ);
        hashMap.put(Config.KEY_READ_AT, new Date().getTime());
        return hashMap;
    }
}
