package com.example.controller;

import com.example.Database;
import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.UserType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
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
    public Button button_balanceT;

    @FXML
    public Button button_historyT;
    @FXML
    public MenuButton button_userProfile;
    @FXML
    public ImageView user_avatar;
    @FXML
    public Button button_home;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        userAccount = loggedInAccount;
        initializeLoggedInPage(loggedInAccount);
    }

    @FXML
    public void backHome(ActionEvent event) {
        if (userAccount.getUserType().equals(UserType.Goblin)) {
            DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Home", userAccount);
        } else {
            DBUtils.changeSceneWithData(event, "/pages/home.fxml", "Home", userAccount);
        }
    }

    public Account<?> userAccount;

    public void setUserAccount(Account<?> userAccount) {
        this.userAccount = userAccount;
        // Use userAccount data as needed in the home scene
    }

    public void initializeLoggedInPage(Account<?> userAccount) {
        this.userAccount = userAccount;
        //System.out.println(userAccount.getUserType());
        button_userProfile.setText(userAccount.getUsername());

        String avatarPath = "file:///" + userAccount.getAvatar().getImagePath(); // Replace 'getAvatarPath' with the actual method name to retrieve the avatar path from your Account class

        // Set the avatar image using the fetched path
        if (avatarPath != null) {
            // Assuming 'user_avatar' is an ImageView
            Image image = new Image(avatarPath);
            user_avatar.setImage(image);
        }
    }

    @FXML
    public void openUserProfile(ActionEvent event) {
        // Implement the functionality to open the user profile page
        System.out.println("User profile button clicked!");

        // Get the parent node of the MenuItem (MenuButton)
        MenuItem menuItem = (MenuItem) event.getSource();
        Node menuButton = menuItem.getParentPopup().getOwnerNode();

        // Pass the MenuButton as the source node
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
            // User clicked OK, clear session and redirect to login page
            AccountHolder.getInstance().setUser(null); // Clear the logged-in user
            // Redirect to the login page
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/pages/login.fxml"));
                Scene scene = button_userProfile.getScene();
                scene.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void toMarauderMap(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/MarauderMap-view.fxml", "Marauder's Map", userAccount);
    }
    @FXML
    private void showSummary(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/divinationFirstPage.fxml", "Show Summary", userAccount);
    }

    @FXML
    public void getHistory(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/TransactionHistory-view.fxml", "Transaction History", userAccount);
    }

    @FXML
    public void checkBalance(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/userProfile.fxml", "User Profile", userAccount);
    }

    @FXML
    private void changeCurrency(ActionEvent event) {
        // Change the scene to EditCurrency.fxml
        DBUtils.changeSceneWithData(event, "/pages/editCurrency.fxml", "Edit Currency", userAccount);
    }

    @FXML
    public void getNumberOfTransaction(ActionEvent event) throws IOException {
        DBUtils.changeSceneWithData(event, "/pages/TransactionHistoryAdmin-view.fxml", "Get number Of Transaction", userAccount);
    }

    @FXML
    private void updateUserType(ActionEvent event) {
        // Change the scene to EditCurrency.fxml
        DBUtils.changeSceneWithData(event, "/pages/editUserType.fxml", "Edit User Type", userAccount);
    }

    @FXML
    void getNumberOfUsers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/getNumOfUser.fxml"));
        Parent root = loader.load();

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("User Details");
        popupStage.setScene(new Scene(root));
        popupStage.showAndWait();

    }
}