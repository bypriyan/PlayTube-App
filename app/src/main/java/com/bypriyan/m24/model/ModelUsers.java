package com.bypriyan.m24.model;

public class ModelUsers {

    String email, profileImage, name, uid, onlineStatus, typingStatus;
    boolean isBlocked = false ;

    public ModelUsers() {
    }

    public ModelUsers(String email, String profileImage, String name, String uid, String onlineStatus, String typingStatus, boolean isBlocked) {
        this.email = email;
        this.profileImage = profileImage;
        this.name = name;
        this.uid = uid;
        this.onlineStatus = onlineStatus;
        this.typingStatus = typingStatus;
        this.isBlocked = isBlocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingStatus() {
        return typingStatus;
    }

    public void setTypingStatus(String typingStatus) {
        this.typingStatus = typingStatus;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
