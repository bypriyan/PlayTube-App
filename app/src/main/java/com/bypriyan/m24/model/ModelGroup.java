package com.bypriyan.m24.model;

public class ModelGroup {

    String groupId, groupDescription, grooupTitle, grooupIcon, grooupTimeStamp, createdBy;

    public ModelGroup() {
    }

    public ModelGroup(String groupId, String groupDescription, String grooupTitle, String grooupIcon, String grooupTimeStamp, String createdBy) {
        this.groupId = groupId;
        this.groupDescription = groupDescription;
        this.grooupTitle = grooupTitle;
        this.grooupIcon = grooupIcon;
        this.grooupTimeStamp = grooupTimeStamp;
        this.createdBy = createdBy;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGrooupTitle() {
        return grooupTitle;
    }

    public void setGrooupTitle(String grooupTitle) {
        this.grooupTitle = grooupTitle;
    }

    public String getGrooupIcon() {
        return grooupIcon;
    }

    public void setGrooupIcon(String grooupIcon) {
        this.grooupIcon = grooupIcon;
    }

    public String getGrooupTimeStamp() {
        return grooupTimeStamp;
    }

    public void setGrooupTimeStamp(String grooupTimeStamp) {
        this.grooupTimeStamp = grooupTimeStamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
