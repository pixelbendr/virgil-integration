package com.psyphertxt.android.cyfa.model;

/**
 * Model for setting and retrieving country values
 */
public class Country {

    //code is the the format. e.g GH
    private String code;
    //name is in the format. e.g Ghana
    private String name;
    //zipcode is in the format. e.g +233
    private String callingCode;

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return callingCode;
    }


}