package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.image.Image;


import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController extends HomeController  implements Initializable {

    @FXML
    private Button button_logoutUP;
    @FXML
    private Text t_username, t_fullName, t_email, t_phoneNumber, t_address, t_dob, t_currency;

    @FXML
    private ImageView i_userAvatar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);

        // Retrieve the avatar image path from the account
        String avatarPath = loggedInAccount.getAvatar().getImagePath();
        System.out.println(avatarPath);
        // Load the avatar image into the ImageView
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Image image = new Image("file:///" + avatarPath);
            i_userAvatar.setImage(image);
        } else {

            Image image = new Image("/img/USER_ICON.png");
            i_userAvatar.setImage(image);
        }

        if (loggedInAccount != null) {
            t_username.setText(loggedInAccount.getUsername());
            t_fullName.setText(loggedInAccount.getFullName());
            t_email.setText(loggedInAccount.getEmail());
            t_phoneNumber.setText(loggedInAccount.getPhoneNumber().toString());
            t_address.setText(loggedInAccount.getAddress());
            t_dob.setText(loggedInAccount.getDateOfBirth().toString());
            t_currency.setText(loggedInAccount.getCurrency().toString());
        } else {
            // Handle the case where user data is not found
            System.out.println("User data not found.");
        }


    }
}


