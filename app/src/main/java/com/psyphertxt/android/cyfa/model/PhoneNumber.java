package com.psyphertxt.android.cyfa.model;

public class PhoneNumber {
    private int id;
    private String number;
    private String name;
    private int image;

    public PhoneNumber(String name, String number) {
       // setId(id);
        setNumber(number);
        setName(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


}
