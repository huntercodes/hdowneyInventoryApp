package com.hunterdowney.hdowneyinventoryapp.domain;

import jakarta.persistence.*;
import java.util.Base64;
import java.util.UUID;

@Entity
public class Image {
    @Id
    private String id;

    private String imageName;

    @Lob
    private byte[] imageData;

    public Image() {
        this.id = UUID.randomUUID().toString();
    }

    public Image(String imageName, byte[] imageData) {
        this.id = UUID.randomUUID().toString();
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public String getId() { return id; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    // convert Base64 string for images
    public String getBase64Image() {
        return Base64.getEncoder().encodeToString(imageData);
    }
}