package com.example.controller;

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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeAdminController implements Initializable {

    @FXML
    private Button MarauderMapButton;
    @FXML
    private Button button_balance;
    @FXML
    private Button button_balanceT;
    @FXML
    private Button button_changeCurrency;
    @FXML
    private Button button_history;
    @FXML
    private Button button_historyT;
    @FXML
    private Button button_home;
    @FXML
    private MenuItem button_logout;
    @FXML
    private Button button_numOfTransaction;
    @FXML
    private Button button_numOfUser;
    @FXML
    private MenuItem button_openUserProfile;
    @FXML
    private Button button_summary;
    @FXML
    private Button button_transfer;
    @FXML
    private MenuButton button_userProfile;
    @FXML
    private Button button_userType;
    @FXML
    private Label label_slogan;
    @FXML
    private Label label_slogan1;
    @FXML
    private ImageView user_avatar;
    public Account<?> userAccount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);
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
    void backHome(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/home3.fxml", "Home", userAccount);
    }

    @FXML
    void changeCurrency(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/editCurrency.fxml", "Edit Currency", userAccount);
    }

    @FXML
    void checkBalance(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/userProfile.fxml", "User Profile", userAccount);
    }

    @FXML
    void getHistory(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/TransactionHistoryAdmin-view.fxml", "Transaction History", this.userAccount);
    }

    @FXML
    void getNumberOfTransaction(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/TransactionHistoryAdmin-view.fxml", "Get number Of Transaction", this.userAccount);
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

    @FXML
    void logout(ActionEvent event) {
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
    void openUserProfile(ActionEvent event) {
        // Implement the functionality to open the user profile page
        System.out.println("User profile button clicked!");

        // Get the parent node of the MenuItem (MenuButton)
        MenuItem menuItem = (MenuItem) event.getSource();
        Node menuButton = menuItem.getParentPopup().getOwnerNode();

        // Pass the MenuButton as the source node
        DBUtils.changeSceneWithData(menuButton, "/pages/userProfile.fxml", "User Profile", userAccount);
    }

    @FXML
    void showSummary(ActionEvent event) {
        DBUtils.changeScene(event, "/pages/divinationFirstPage.fxml", "Show Summary", userAccount.getUsername());
    }

    @FXML
    void toMarauderMap(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/MarauderMap-view.fxml", "Marauder's Map", userAccount);
    }

    @FXML
    void updateUserType(ActionEvent event) {
        DBUtils.changeSceneWithData(event, "/pages/editUserType.fxml", "Edit User Type", userAccount);
    }

    public void setUserAccount(Account<?> userAccount) {
        this.userAccount = userAccount;
    }
}