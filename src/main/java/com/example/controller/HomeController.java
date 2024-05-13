package com.example.controller;

import com.example.model.Account;
import com.example.model.Currency;
import com.example.model.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Button button_balance;

    @FXML
    private Button button_history;

    @FXML
    private Button button_summary;

    @FXML
    private Button button_transfer;

    @FXML
    private Button button_balanceT;

    @FXML
    private Button button_historyT;

    @FXML
    private Button button_summaryT;

    @FXML
    private Button button_transferT;

    @FXML
    private MenuButton button_userProfile;

    @FXML
    private Button button_next;

    @FXML
    private Button button_back;
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
     

    }



    @FXML
    private void nextPage(ActionEvent event) {
        // Change the scene to home2.fxml
        DBUtils.changeScene(event, "/pages/home2.fxml", "Next Page", null);
    }

    @FXML
    private void backPage(ActionEvent event) {
        // Change the scene to home.fxml
        DBUtils.changeScene(event, "/pages/home.fxml", "Back Page", null);
    }



    public void initializeLoggedInPage(Account<?> userAccount) {

        if (userAccount != null) {
            if (userAccount.getUserType() == UserType.Goblin) {
                // Show the "Next Page" button for admin functions
                button_next.setVisible(true);
            } else {
                // Hide the "Next Page" button for non-admin users
                button_next.setVisible(false);
                button_back.setVisible(false);
            }
        }

    }

    @FXML
    public void openUserProfile(ActionEvent event) {
        // Implement the functionality to open the user profile page
        System.out.println("User profile button clicked!");
    }

    @FXML
    public void logout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setContentText("Click OK to confirm, or Cancel to stay logged in.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User clicked OK, clear session and redirect to login page
            System.out.println("Logged out");
        }

    }




}
