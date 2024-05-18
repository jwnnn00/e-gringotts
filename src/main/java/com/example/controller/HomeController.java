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
    public Button button_summaryT;

    @FXML
    private Button button_transferT;

    @FXML
    public MenuButton button_userProfile;

    @FXML
    private Button button_next;

    @FXML
    private Button button_back;

    @FXML
    public ImageView user_avatar;
    @FXML
    private Button button_changeCurrency;

    @FXML
    public Button button_home;
    @FXML
    private Button button_numOfTransaction;
    @FXML
    private Text t_totalNumOfUser;
    @FXML
    private BarChart<Number,String> userChart;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);
        t_totalNumOfUser = new Text();
        CategoryAxis y_typeOfUser = new CategoryAxis();
        NumberAxis x_numberOfUser  = new NumberAxis();
        userChart = new BarChart<>(x_numberOfUser,y_typeOfUser);

    }

    @FXML
    private void backHome() {
        // Call a method to change the scene to home.fxml
        changeScene("/pages/home.fxml");
    }

    protected void changeScene(String fxmlFileName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFileName));
            Scene scene = button_home.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    @FXML
    private void changeCurrency(ActionEvent event) {
        // Change the scene to EditCurrency.fxml
        DBUtils.changeSceneWithData(event, "/pages/editCurrency.fxml", "Edit Currency", userAccount);
    }

    public Account<?> userAccount;

    public void setUserAccount(Account<?> userAccount) {
        this.userAccount = userAccount;
        // Use userAccount data as needed in the home scene
    }

    public void initializeLoggedInPage(Account<?> userAccount) {
        this.userAccount=userAccount;
        System.out.println(userAccount.getUserType());
        button_userProfile.setText(userAccount.getUsername());

        String avatarPath = "file:///"+userAccount.getAvatar().getImagePath(); // Replace 'getAvatarPath' with the actual method name to retrieve the avatar path from your Account class

        // Set the avatar image using the fetched path
        if (avatarPath != null) {
            // Assuming 'user_avatar' is an ImageView
            Image image = new Image(avatarPath);
            user_avatar.setImage(image);
        }

        if (userAccount != null && button_next != null) {
            if (userAccount.getUserType() == UserType.Goblin) {
                // Show the "Next Page" button for admin functions
                button_next.setVisible(true);
            } else {
                // Hide the "Next Page" button for non-admin users
                button_next.setVisible(false);
            }
        }

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


}