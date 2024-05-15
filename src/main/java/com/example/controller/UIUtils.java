package com.example.controller;

import com.example.model.Account;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UIUtils {
    public static void initializeUI(MenuButton button_userProfile, ImageView user_avatar, Account<?> loggedInAccount, String username) {
        // Set button text and avatar image
        button_userProfile.setText(username);
        String avatarPath = "file:///"+loggedInAccount.getAvatar().getImagePath();
        if (avatarPath != null) {
            Image image = new Image(avatarPath);
            user_avatar.setImage(image);
        }
    }
}