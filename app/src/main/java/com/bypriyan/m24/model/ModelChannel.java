package com.bypriyan.m24.model;

public class ModelChannel {

    private String channelProfileImage, channelCoverImage, channelName, channelAbout, channelId, channelTimestamp, uid, subscribers,
            website, fb, twitter, instagram;

    public ModelChannel() {
    }

    public ModelChannel(String channelProfileImage, String channelCoverImage, String channelName, String channelAbout, String channelId, String channelTimestamp, String uid, String subscribers, String website, String fb, String twitter, String instagram) {
        this.channelProfileImage = channelProfileImage;
        this.channelCoverImage = channelCoverImage;
        this.channelName = channelName;
        this.channelAbout = channelAbout;
        this.channelId = channelId;
        this.channelTimestamp = channelTimestamp;
        this.uid = uid;
        this.subscribers = subscribers;
        this.website = website;
        this.fb = fb;
        this.twitter = twitter;
        this.instagram = instagram;
    }

    public String getChannelProfileImage() {
        return channelProfileImage;
    }

    public void setChannelProfileImage(String channelProfileImage) {
        this.channelProfileImage = channelProfileImage;
    }

    public String getChannelCoverImage() {
        return channelCoverImage;
    }

    public void setChannelCoverImage(String channelCoverImage) {
        this.channelCoverImage = channelCoverImage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelAbout() {
        return channelAbout;
    }

    public void setChannelAbout(String channelAbout) {
        this.channelAbout = channelAbout;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTimestamp() {
        return channelTimestamp;
    }

    public void setChannelTimestamp(String channelTimestamp) {
        this.channelTimestamp = channelTimestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
