package com.example.model;

public class UserAvatar {
    private String imagePath;
    private Long userId;

    // Constructor
    public UserAvatar(String imagePath, Long userId) {
        this.imagePath = imagePath;
        this.userId = userId;
    }

    public UserAvatar() {

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
