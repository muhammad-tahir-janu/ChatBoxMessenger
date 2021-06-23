package com.tahir70108.chatboxmessenger;

public class UserProfile {
    private String userName;
    private String userID;

    public UserProfile(String userName, String userID) {
        this.userName = userName;
        this.userID = userID;
    }

    public UserProfile() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "userName='" + userName + '\'' +
                ", userID='" + userID + '\'' +
                '}';
    }
}
