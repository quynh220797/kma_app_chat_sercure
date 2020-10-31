package com.kma.securechatapp.core.api.model;

public class Phonebook {
    private String name;
    private String appName;
    private String urlAvatar;
    private int state;
    public Phonebook(String name, String appName, String urlAvatar, int state ){
        this.name = name;
        this.appName = appName;
        this.urlAvatar = urlAvatar;
        this.state = state;

    }
    public String getName(){
        return name;
    }
    public String getAppName(){
        return appName;

    }
    public String getUrlAvatar(){
        return urlAvatar;
    }
    public int getState(){
        return state;
    }
}
