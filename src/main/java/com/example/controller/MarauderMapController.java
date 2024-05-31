package com.example.controller;

import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.UserDAO;
import com.example.controller.DBUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MarauderMapController extends HomeController implements Initializable {
    private static String friendName;

    @FXML
    private AnchorPane MarauderMapAnchorPane;
    @FXML
    private TextField search;
    @FXML
    private Button browse_friends;
    @FXML
    private TableView<Account<?>> friendsTable;
    @FXML
    private TableColumn<Account<?>, String> fullName_col;
    @FXML
    private TableColumn<Account<?>, String> username_col;
    @FXML
    private TableColumn<Account<?>, String> phoneNumber_col;
    @FXML
    private TableColumn<Account<?>, String> email_col;
    @FXML
    private TableColumn<Account<?>, Void> transfer_col;


    private ObservableList<Account<?>> friendsList = FXCollections.observableArrayList();

    private UserDAO userDAO = new UserDAO();
    private Account<?> account;

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);
        account = loggedInAccount;

        fullName_col.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        username_col.setCellValueFactory(new PropertyValueFactory<>("username"));
        phoneNumber_col.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        email_col.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Add a button to the transfer column
        transfer_col.setCellFactory(createTransferButtonCellFactory());

        friendsTable.setItems(friendsList);
    }

    //Generate transfer button at each row
    private Callback<TableColumn<Account<?>, Void>, TableCell<Account<?>, Void>> createTransferButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Account<?>, Void> call(final TableColumn<Account<?>, Void> param) {
                return new TableCell<>() {
                    private final Button transfer_button = new Button("Transfer");

                    {
                        transfer_button.setOnAction(event -> {
                            Account<?> data = getTableView().getItems().get(getIndex());
                            friendName = data.getUsername();
                            DBUtils.changeSceneWithData(event, "/pages/transfer.fxml", "Transfer", account);
                            System.out.println("Transfer to: " + data.toString());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(transfer_button);
                        }
                    }
                };
            }
        };
    }

    public void handleBrowseFriend(ActionEvent event) {
        String searchValue = search.getText();

        if (searchValue.isEmpty()) {
            showAlert("Null Search Value", "Please enter a name or phone number.");
            return;
        } else {
            friendsList.clear();
            List<Account<?>> user = userDAO.getUserList(searchValue, account.getUserId());
            if (user.isEmpty()) {
                showAlert("No user found", "No user was found for the value entered.");
                search.clear();
                return;
            }
            friendsList.addAll(user);
            friendsTable.setItems(friendsList);
        }
        search.clear();
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static String getFriendName() {
        return friendName;
    }
}
