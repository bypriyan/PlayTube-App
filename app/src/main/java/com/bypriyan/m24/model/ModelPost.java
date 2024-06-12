package com.bypriyan.m24.model;

public class ModelPost {

    private String like, postID, postTimestamp, postUid, title, url, videoChannelId;

    public ModelPost() {
    }

    public ModelPost(String like, String postID, String postTimestamp, String postUid, String title, String url, String videoChannelId) {
        this.like = like;
        this.postID = postID;
        this.postTimestamp = postTimestamp;
        this.postUid = postUid;
        this.title = title;
        this.url = url;
        this.videoChannelId = videoChannelId;
    }

    public String getLike() {
        return like;
    }

    public String getPostID() {
        return postID;
    }

    public String getPostTimestamp() {
        return postTimestamp;
    }

    public String getPostUid() {
        return postUid;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getVideoChannelId() {
        return videoChannelId;
    }
}
