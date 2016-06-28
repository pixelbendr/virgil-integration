package com.psyphertxt.android.cyfa.model;

/**
 * This class is responsible for holding
 * data we use in assisting users when they first sign up
 * and also serves the purpose of status feed notifications
 */
public class Walkthrough {

    //constants representing profile actions
    //use these constants instead of typing the actual strings

    public enum Stage {
        WELCOME,
        PROFILE_PICTURE,
        STATUS_MESSAGE,
        FIRST_MESSAGE,
        //PRIVATE_MESSAGE,
        COMPLETED
    }

    //title of the walk through message
    //for example "profile name"
    private String title;

    //sub title of the walk through message
    //for example "add profile name"
    private String subtitle = "";

    //description accompanying the walk through message
    //for example "add your profile name here"
    private String description;

    //the icon resource indicator
    //default is a right arrow
    private int icon;

    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public String getTitle() {
        return title.toUpperCase();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
