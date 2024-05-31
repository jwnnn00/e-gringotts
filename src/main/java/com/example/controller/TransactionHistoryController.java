package com.example.controller;

import com.example.Transaction;
import com.example.TransactionDAO;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javafx.stage.Stage;


import java.math.BigDecimal;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionHistoryController extends HomeController implements Initializable {
    @FXML
    private Button MarauderMapButton;
    @FXML
    private DatePicker start_date;
    @FXML
    private DatePicker end_date;
    @FXML
    private Button browse_date;
    @FXML
    private TextField max_amount;
    @FXML
    private TextField min_amount;
    @FXML
    private Button browse_amount;
    @FXML
    private Button browse_category;
    @FXML
    private ChoiceBox<String> category;
    @FXML
    private TableView<Transaction> transactionTable;
    @FXML
    private TableColumn<Transaction, Integer> num_col;
    @FXML
    private TableColumn<Transaction, Integer> transactionID_col;
    @FXML
    private TableColumn<Transaction, Integer> toFromUserID_col;
    @FXML
    private TableColumn<Transaction, LocalDateTime> date_col;
    @FXML
    private TableColumn<Transaction, Transaction.TransactionType> type_col;
    @FXML
    private TableColumn<Transaction, BigDecimal> amount_col;
    @FXML
    private TableColumn<Transaction, Transaction.Category> category_col;
    private String[] categoryList = {"Food", "Grocery", "Shopping", "Transportation", "Entertainment", "Utilities", "Other"};
    @FXML
    private TableColumn<Transaction, Void> print_col;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private TransactionDAO TransactionDAO = new TransactionDAO();
    private String username = "";

    // Define date and time format
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);
        username = loggedInAccount.getUsername();
        // Set category choice box list
        category.getItems().addAll(categoryList);

        // Set cell value factory for row numbers
        num_col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, Integer>, ObservableValue<Integer>>() {
            @Override
            public ObservableValue<Integer> call(TableColumn.CellDataFeatures<Transaction, Integer> p) {
                return new ReadOnlyObjectWrapper<>(transactionTable.getItems().indexOf(p.getValue()) + 1);
            }
        });

        /** Initialize the TableView columns */
        transactionID_col.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        toFromUserID_col.setCellValueFactory(new PropertyValueFactory<>("toFromUserID"));
        // Set a custom cell factory to display full name based on userID
        toFromUserID_col.setCellFactory(column -> {
            return new TableCell<Transaction, Integer>() {
                @Override
                protected void updateItem(Integer userID, boolean empty) {
                    super.updateItem(userID, empty);
                    if (empty || userID == null) {
                        setText(null);
                    } else {
                        // Fetch full name based on userID using your TransactionDAO
                        String fullName = TransactionDAO.getUserFullName(userID);
                        setText(fullName);
                    }
                }
            };
        });

        date_col.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        // Set a cell factory to format the LocalDateTime value
        date_col.setCellFactory(column -> {
            return new TableCell<Transaction, LocalDateTime>() {
                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(FORMATTER));
                    }
                }
            };
        });

        type_col.setCellValueFactory(new PropertyValueFactory<>("transactionType"));

        amount_col.setCellValueFactory(new PropertyValueFactory<>("amount"));
        // Set a custom cell factory to display amount with prefix based on transaction type and color
        amount_col.setCellFactory(column -> {
            return new TableCell<Transaction, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                        setStyle(""); // Reset style if cell is empty
                    } else {
                        Transaction.TransactionType type = getTableView().getItems().get(getIndex()).getTransactionType();
                        String formattedAmount;
                        if (type == Transaction.TransactionType.Debit) {
                            formattedAmount = "+ " + amount.toString(); // Add '+' for debit
                            setTextFill(Color.LIGHTGREEN); // Set font color to green for debit
                        } else {
                            formattedAmount = "- " + amount.toString(); // Add '-' for credit
                            setTextFill(Color.RED); // Set font color to red for credit
                        }
                        setText(formattedAmount);
                    }
                }
            };
        });

        category_col.setCellValueFactory(new PropertyValueFactory<>("category"));

        // Print Receipt Button
        print_col.setCellFactory(new Callback<TableColumn<Transaction, Void>, TableCell<Transaction, Void>>() {
            @Override
            public TableCell<Transaction, Void> call(final TableColumn<Transaction, Void> param) {
                final TableCell<Transaction, Void> cell = new TableCell<Transaction, Void>() {
                    private final Button print_button = new Button("Print");
                    {
                        print_button.setOnAction(event -> {
                            Transaction transaction = getTableView().getItems().get(getIndex());
                            loadReceiptView(transaction);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(print_button);
                        }
                    }
                };
                return cell;
            }
        });

        /** By default will print all transaction made by the user */
        loadTransactionsForUsername(username);
    }

    public void loadTransactionsForUsername(String username) {
        ObservableList<Transaction> transactions = TransactionDAO.getTransactionsByUsername(username);
        transactionTable.setItems(transactions);
    }

    private void loadReceiptView(Transaction transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/receipt-view.fxml"));
            AnchorPane receiptRoot = loader.load();

            ReceiptController receiptController = loader.getController();
            receiptController.setTransactionDetails(transaction);

            Stage receiptStage = new Stage();
            receiptStage.setTitle("Transaction Receipt");
            receiptStage.setScene(new Scene(receiptRoot));
            receiptStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Print Error", "An error occurred while printing: " + e.getMessage());
        }
    }

    public void handleBrowseDate(ActionEvent event) {
        LocalDate startDate = start_date.getValue();
        LocalDate endDate = end_date.getValue();

        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                showAlert("Invalid Date Range", "Start date cannot be after end date.");
                start_date.setValue(null);
                end_date.setValue(null);
            } else {
                transactionList.clear();
                List<Transaction> transactions = TransactionDAO.getTransactionsByDateRange(username, startDate, endDate);
                if (transactions.isEmpty()) {
                    showAlert("No Transactions", "No transactions were found for the selected date range.");
                    start_date.setValue(null);
                    end_date.setValue(null);
                    return;
                }
                transactionList.addAll(transactions);
                transactionTable.setItems(transactionList);
            }
        } else {
            // Handle invalid date range
            showAlert("Missing Dates", "Please select both start and end dates.");
        }
    }

    public void handleBrowseAmount(ActionEvent event) {
        String minAmountStr = min_amount.getText();
        String maxAmountStr = max_amount.getText();


        if (minAmountStr.isEmpty() && maxAmountStr.isEmpty()) {
            showAlert("Missing Amounts", "Please enter both minimum and maximum amount.");
            return;
        } else if (minAmountStr.isEmpty()) {
            showAlert("Missing Minimum Amounts", "Please enter minimum amount.");
            return;
        } else if (maxAmountStr.isEmpty()) {
            showAlert("Missing Maximum Amounts", "Please enter maximum amount.");
            return;
        }

        if (!isNumeric(minAmountStr) || !isNumeric(maxAmountStr)) {
            showAlert("Invalid Amounts", "Please enter a numeric value.");
            min_amount.clear();
            max_amount.clear();
            return;
        }

        BigDecimal minAmount = new BigDecimal(minAmountStr);
        BigDecimal maxAmount = new BigDecimal(maxAmountStr);

        if (minAmount.compareTo(maxAmount) == 1) {
            showAlert("Invalid Amount Range", "Minimum amount cannot be greater than Maximum amount.");
            min_amount.clear();
            max_amount.clear();
        } else {
            transactionList.clear();
            List<Transaction> transactions = TransactionDAO.getTransactionsByAmountRange(username, minAmount, maxAmount);
            if (transactions.isEmpty()) {
                showAlert("No Transactions", "No transactions were found for the selected amount range.");
                return;
            }
            transactionList.addAll(transactions);
            transactionTable.setItems(transactionList);
        }
    }

    public void handleBrowseCategory(ActionEvent event) {
        String categoryValue = category.getValue();

        if (categoryValue == null) {
            showAlert("Missing Category", "Please select a category.");
            return;
        }

        Transaction.Category categoryEnum = Transaction.Category.valueOf(categoryValue);

        String categoryStr = categoryEnum.name();

        transactionList.clear();
        List<Transaction> transactions = TransactionDAO.getTransactionsByCategory(username, categoryValue);
        if (transactions.isEmpty()) {
            showAlert("No Transactions", "No transactions were found for the selected category.");
            category.setValue(null);
            return;
        }
        transactionList.addAll(transactions);
        transactionTable.setItems(transactionList);
    }

    // To check whether text field contains numeric value only
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}