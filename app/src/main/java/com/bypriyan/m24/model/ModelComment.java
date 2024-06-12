package com.bypriyan.m24.model;

public class ModelComment {

    private String commentText, commentUID, timestamp;

    public ModelComment(String commentText, String commentUID, String timestamp) {
        this.commentText = commentText;
        this.commentUID = commentUID;
        this.timestamp = timestamp;
    }

    public ModelComment() {
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentUID() {
        return commentUID;
    }

    public void setCommentUID(String commentUID) {
        this.commentUID = commentUID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
