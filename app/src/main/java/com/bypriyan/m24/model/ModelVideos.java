package com.bypriyan.m24.model;

public class ModelVideos {

    private String description, like, title, url, videoID, videoThumbnail, videoTimestamp, videoUid, views, videoChannelId;

    public ModelVideos(String description, String like, String title, String url, String videoID, String videoThumbnail, String videoTimestamp, String videoUid, String views, String videoChannelId) {
        this.description = description;
        this.like = like;
        this.title = title;
        this.url = url;
        this.videoID = videoID;
        this.videoThumbnail = videoThumbnail;
        this.videoTimestamp = videoTimestamp;
        this.videoUid = videoUid;
        this.views = views;
        this.videoChannelId = videoChannelId;
    }

    public ModelVideos() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoTimestamp() {
        return videoTimestamp;
    }

    public void setVideoTimestamp(String videoTimestamp) {
        this.videoTimestamp = videoTimestamp;
    }

    public String getVideoUid() {
        return videoUid;
    }

    public void setVideoUid(String videoUid) {
        this.videoUid = videoUid;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getVideoChannelId() {
        return videoChannelId;
    }

    public void setVideoChannelId(String videoChannelId) {
        this.videoChannelId = videoChannelId;
    }
}
