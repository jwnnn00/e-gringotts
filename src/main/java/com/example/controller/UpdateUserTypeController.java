package com.example.controller;

import com.example.Database;
import com.example.model.Account;
import com.example.model.UserType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UpdateUserTypeController extends HomeController implements Initializable {

    @FXML
    private TableView<Account<?>> userTable;

    @FXML
    private TableColumn<Account, Integer> userIDColumn;

    @FXML
    private TableColumn<Account, String> usernameColumn;

    @FXML
    private TableColumn<Account, String> fullNameColumn;

    @FXML
    private TableColumn<Account, Integer> numOfTransactionColumn;

    @FXML
    private TableColumn<Account, String> userTypeColumn;


    @FXML
    private Text selectedUserText;

    @FXML
    private Label usernameLabel;

    @FXML
    private ChoiceBox<String> userTypeChoiceBox;

    @FXML
    private Button button_saveChange;

    private ObservableList<Account<?>> users;
    private Account selectedUser;


    private void fetchUserData() {
        users = FXCollections.observableArrayList();

        String query = "SELECT u.userId, u.username, u.fullName, COUNT(t.transactionId) AS transactionCount, u.userType "
                + "FROM user u LEFT JOIN transaction t ON u.userID = t.userID "
                + "GROUP BY u.userID";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                long userID = rs.getInt("userId");
                String username = rs.getString("username");
                String fullName = rs.getString("fullName");
                int transactionCount = rs.getInt("transactionCount");
                String userType = rs.getString("userType");

                Account<?> account = new Account(userID, username, fullName, transactionCount, userType);
                users.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void rowClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {
            selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                //System.out.println(selectedUser.getUserId());
                userTypeChoiceBox.setValue(selectedUser.getUserType().toString());
                usernameLabel.setText(selectedUser.getUsername());
            }
        }
    }

    @FXML
    private void saveUserType() {
        if (selectedUser != null) {
            String newUserType = userTypeChoiceBox.getValue();
            selectedUser.setUserType(UserType.valueOf(newUserType));

            String updateQuery = "UPDATE user SET userType = ? WHERE userId = ?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

                pstmt.setString(1, newUserType);
                pstmt.setLong(2, selectedUser.getUserId());
                pstmt.executeUpdate();

                userTable.refresh();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        numOfTransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactionCount"));
        userTypeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));

        userTypeChoiceBox.setItems(FXCollections.observableArrayList("Silver_Snitch", "Golden_Galleon", "Platinum_Patronus"));

        fetchUserData();
        userTable.setItems(users);

        userTable.setOnMouseClicked(this::rowClicked);

        button_saveChange.setOnAction(event -> saveUserType());

    }
}
