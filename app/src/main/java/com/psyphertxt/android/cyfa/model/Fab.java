package com.psyphertxt.android.cyfa.model;


/**
 * model for managing insertion of fab buttons
 * in view holders, activities etc....
 */
public class Fab {

    private int buttonType = ButtonType.ADD_FRIENDS;
    private String name;
    private int resId = -1;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public int getButtonType() {
        return buttonType;
    }

    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final class ButtonType {
        public static final int ADD_FRIENDS = 0;
        public static final int ADD_GROUPS = 1;
        public static final int RECENT = 2;
    }
}
