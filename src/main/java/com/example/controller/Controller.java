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

    @FXML
    void login(javafx.event.ActionEvent event) {
        DBUtils.loginUser(event, tf_username.getText(), pf_password.getText());


    }

    @FXML
    void signUp(javafx.event.ActionEvent event){
        DBUtils.changeScene(event,"/pages/register.fxml",null,null);
    }


}