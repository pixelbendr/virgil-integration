package com.psyphertxt.android.cyfa;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.psyphertxt.android.cyfa.model.Walkthrough;

import bolts.Bolts;

/**
 * This is the Applications Settings Class
 * all application settings are suppose
 * to be retrieved and saved using this class
 */
public class Settings {

    SharedPreferences sharedPreferences;

    //initialize shared preferences by getting the default
    //and passing the context on each initialization
    public Settings(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getTheme() {
        return sharedPreferences.getString(Config.KEY_THEME, Config.THEME_NAME_INDIGO);
    }

    public void setTheme(String themeName) {
        sharedPreferences
                .edit()
                .putString(Config.KEY_THEME, themeName)
                .apply();

    }

    public String getPasscode() {
        return sharedPreferences.getString(Config.KEY_PASSCODE, Config.EMPTY_STRING);
    }

    public void setPasscode(String passcode) {
        sharedPreferences
                .edit()
                .putString(Config.KEY_PASSCODE, passcode)
                .apply();

    }

    public void clearPasscode() {
        if (sharedPreferences.contains(Config.KEY_PASSCODE)) {
            sharedPreferences
                    .edit()
                    .remove(Config.KEY_PASSCODE)
                    .apply();
        }

    }

    //phone number format is callingCode+user entered number
    //we store it after twilio validates it
    //to make sure it's a correct number
    public String getPhoneNumber() {
        return sharedPreferences.getString(Config.KEY_PHONE_NUMBER, null);
    }

    public void setPhoneNumber(String phoneNumber) {
        sharedPreferences
                .edit()
                .putString(Config.KEY_PHONE_NUMBER, phoneNumber)
                .apply();

    }

    public String getCallingCode() {
        return sharedPreferences.getString(Config.KEY_CALLING_CODE, Config.EMPTY_STRING);
    }

    public void setCallingCode(String callingCode) {
        sharedPreferences
                .edit()
                .putString(Config.KEY_CALLING_CODE, callingCode)
                .apply();

    }

    public Walkthrough.Stage getCurrentStage() {
        final String name = sharedPreferences.getString(Config.KEY_STAGE, Config.EMPTY_STRING);
        if (name == null || name.isEmpty()) {
            return Walkthrough.Stage.PROFILE_PICTURE;
        }
        return Walkthrough.Stage.valueOf(name);
    }

    public void setCurrentStage(Walkthrough.Stage stage) {
        String name = Walkthrough.Stage.COMPLETED.name();
        if (stage != null) {
            name = stage.name();
        }
        sharedPreferences
                .edit()
                .putString(Config.KEY_STAGE, name)
                .apply();

    }

    public void updateStage(Context context) {
        Settings settings = new Settings(context);
        Walkthrough.Stage stage = settings.getCurrentStage();
        if (stage != Walkthrough.Stage.COMPLETED) {
            settings.setCurrentStage(Walkthrough.Stage.values()[stage.ordinal() + 1]);
        }
    }

    //TODO create a seperate tips class to handle both
    //TODO server and locally created tips
    public void resetStage() {
        sharedPreferences
                .edit()
                .putString(Config.KEY_STAGE, Config.EMPTY_STRING)
                .apply();
    }


    //Settings for Chat Action Buttons

    public Boolean isLiveTyping() {
        return sharedPreferences.getBoolean(Config.KEY_LIVE_TYPING, false);
    }

    public void isLiveTyping(Boolean typing) {
        sharedPreferences
                .edit()
                .putBoolean(Config.KEY_LIVE_TYPING, typing)
                .apply();

    }

    public Boolean isHideRegular() {
        return sharedPreferences.getBoolean(Config.KEY_HIDE_REGULAR, false);
    }

    public void isHideRegular(Boolean regular) {
        sharedPreferences
                .edit()
                .putBoolean(Config.KEY_HIDE_REGULAR, regular)
                .apply();

    }

    public Boolean isTimerShowing() {
        return sharedPreferences.getBoolean(Config.KEY_SHOW_TIMER, false);
    }

    public void isTimerShowing(Boolean typing) {
        sharedPreferences
                .edit()
                .putBoolean(Config.KEY_SHOW_TIMER, typing)
                .apply();

    }

    public int getTimerIndex() {
        return sharedPreferences.getInt(Config.KEY_TIMER_INDEX, 0);
    }

    public void setTimerIndex(int index) {
        sharedPreferences
                .edit()
                .putInt(Config.KEY_TIMER_INDEX, index)
                .apply();

    }

    public String getTimerValue() {
        return sharedPreferences.getString(Config.KEY_TIMER_VALUE, Config.TIMER_DEFAULT);
    }

    public void setTimerValue(String value) {
        sharedPreferences
                .edit()
                .putString(Config.KEY_TIMER_VALUE, value)
                .apply();

    }

}
