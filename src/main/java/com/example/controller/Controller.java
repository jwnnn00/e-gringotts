package com.example.controller;

import com.example.Database;
import com.example.LoginRegisterProgram;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;

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
    private HomeController homeController;


    @FXML
    void login(javafx.event.ActionEvent event) {
        DBUtils.loginUser(event, tf_username.getText(), pf_password.getText());
        DBUtils.changeScene(event,"/pages/home.fxml","Welcome ", tf_username.getText());

        if (homeController != null) {
            homeController.initializeLoggedInPage(db.getUserByUsername(tf_username.getText()));
        }
    }
    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
    @FXML
    void signUp(javafx.event.ActionEvent event){
        DBUtils.changeScene(event,"/pages/register.fxml",null,null);
    }


}