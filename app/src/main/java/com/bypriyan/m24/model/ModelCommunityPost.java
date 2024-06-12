package com.bypriyan.m24.model;

public class ModelCommunityPost {

    String postID, title, postDescription, postLikes, postComments, postImage, postTime, uid, email, image, name;

    public ModelCommunityPost() {
    }

    public ModelCommunityPost(String postID, String title, String postDescription, String postLikes, String postComments, String postImage, String postTime, String uid, String email, String image, String name) {
        this.postID = postID;
        this.title = title;
        this.postDescription = postDescription;
        this.postLikes = postLikes;
        this.postComments = postComments;
        this.postImage = postImage;
        this.postTime = postTime;
        this.uid = uid;
        this.email = email;
        this.image = image;
        this.name = name;
    }

    public String getPostId() {
        return postID;
    }

    public void setPostId(String postID) {
        this.postID = postID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(String postLikes) {
        this.postLikes = postLikes;
    }

    public String getPostComments() {
        return postComments;
    }

    public void setPostComments(String postComments) {
        this.postComments = postComments;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
