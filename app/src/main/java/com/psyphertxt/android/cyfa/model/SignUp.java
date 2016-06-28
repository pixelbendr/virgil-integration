package com.psyphertxt.android.cyfa.model;

/**
 * This model handles all sign up values
 * and gradually builds up the sign up using
 * EventBus
 */
public class SignUp {

    private String number;
    private int code;
    private String sessionToken;
    private Boolean isExisting;
    private String username;
    private String callingCode;
    private String theme;
    private String playerId;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPhoneNumber() {
        return callingCode + number;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public Boolean isExisting() {
        return isExisting;
    }

    public void isExisting(Boolean isExisting) {
        this.isExisting = isExisting;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    private String profileName;


}
