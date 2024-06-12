package com.bypriyan.m24.model;

public class ModelViews {

   private String timestamp, uid, videoID, videoUid,channelId;

    public ModelViews() {
    }

    public ModelViews(String timestamp, String uid, String videoID, String videoUid, String channelId) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.videoID = videoID;
        this.videoUid = videoUid;
        this.channelId = channelId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
