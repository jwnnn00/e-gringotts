package com.example.controller;

import com.example.Database;
import com.example.LoginRegisterProgram;
import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.UserType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class Controller {



    @FXML
    private Button button_login;

    @FXML
    private Button button_register;



    @FXML
    private PasswordField pf_password;

    @FXML
    private TextField tf_username;

    private Stage stage;
    private Scene scene;
    private final Database db = new Database();



    @FXML
    void login(javafx.event.ActionEvent event) {
        // Fetch user details from the database
        Account<?> userAccount = Database.getUserByUsername(tf_username.getText());

        if (userAccount != null && userAccount.getPassword().equals(pf_password.getText())) {
            AccountHolder.getInstance().setUser(userAccount);
            // Pass the userAccount to the next scene
            DBUtils.changeSceneWithData(event, "/pages/home.fxml", "User Page", userAccount);
        } else {
            // Display error message for invalid credentials
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid username or password");
            alert.setContentText("Please enter valid credentials.");
            alert.showAndWait();
        }
    }



    @FXML
    void signUp(javafx.event.ActionEvent event){
        DBUtils.changeScene(event,"/pages/register.fxml",null,null);
    }


}