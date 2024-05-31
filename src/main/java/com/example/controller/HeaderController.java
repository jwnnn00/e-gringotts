package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HeaderController implements Initializable {

    @FXML
    private Button button_home;
    @FXML
    private Button button_balanceT;
    @FXML
    private Button MarauderMapButton;
    @FXML
    private MenuButton button_userProfile;
    @FXML
    private MenuItem button_openUserProfile;
    @FXML
    private MenuItem button_logout;
    @FXML
    private ImageView user_avatar;
    private Account<?> userAccount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        if (loggedInAccount != null) {
            initializeLoggedInPage(loggedInAccount);
        }
    }

    private void changeScene(String fxmlFileName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFileName));
            Scene scene = button_home.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Unable to load the page: " + fxmlFileName);
        }
    }

    private void initializeLoggedInPage(Account<?> userAccount) {
        this.userAccount = userAccount;
        button_userProfile.setText(userAccount.getUsername());

        String avatarPath = "file:///" + userAccount.getAvatar().getImagePath(); // Adjust method name accordingly

        // Set the avatar image using the fetched path
        if (avatarPath != null && !avatarPath.isEmpty()) {
            Image image = new Image(avatarPath);
            user_avatar.setImage(image);
        }
    }

    @FXML
    private void backHome() {
        changeScene("/pages/home.fxml");
    }

    @FXML
    private void checkBalance(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/userProfile.fxml", "User Profile", userAccount);
    }

    @FXML
    private void getHistory(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/TransactionHistory-view.fxml", "Transaction History", userAccount);
    }

    @FXML
    private void toMarauderMap(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/MarauderMap-view.fxml", "Marauder's Map", userAccount);
    }

    @FXML
    private void openUserProfile(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Node menuButton = menuItem.getParentPopup().getOwnerNode();
        DBUtils.changeSceneWithData(menuButton, "/pages/userProfile.fxml", "User Profile", userAccount);
    }

    @FXML
    private void logout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to confirm, or Cancel to stay logged in.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            AccountHolder.getInstance().setUser(null); // Clear the logged-in user
            changeScene("/pages/login.fxml");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
