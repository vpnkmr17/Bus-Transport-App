package com.example.bususerapp.Classes;

public class User {

    private String name, username, id, email, phoneNum;

    public User(){

    }

    public User(String name, String username, String id, String email, String phoneNum) {
        this.name = name;
        this.username = username;
        this.id = id;
        this.email = email;
        this.phoneNum = phoneNum;
    }

    public String getName(){
        return this.name;
    }

    public String getUsername(){
        return this.username;
    }

    public String getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPhoneNum(){
        return this.phoneNum;
    }
}
