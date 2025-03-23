package com.hunterdowney.hdowneyinventoryapp.domain;

import java.util.Base64;

public class Image {
    private String imageName;
    private byte[] imageData;

    public Image() {}

    public Image(String imageName, byte[] imageData) {
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    // convert Base64 string for images
    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(imageData);
    }
}