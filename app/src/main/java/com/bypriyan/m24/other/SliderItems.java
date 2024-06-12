package com.bypriyan.m24.other;

public class SliderItems {

    private String  textTitle, textDescription;
    private int animations;

    public SliderItems(int animations, String textTitle, String textDescription) {
        this.animations = animations;
        this.textTitle = textTitle;
        this.textDescription = textDescription;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public void setTextTitle(String textTitle) {
        this.textTitle = textTitle;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }

    public int getAnimations() {
        return animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
    }
}
