package com.example.controller;

import com.example.Transaction;
import com.example.TransactionAdminDAO;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class TransactionHistoryAdminController extends HomeController implements Initializable {
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
    private TableColumn<Transaction, Integer> name_col;
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
    private Label totalTransaction;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private TransactionAdminDAO TransactionDAO = new TransactionAdminDAO();


    // Define date and time format
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);

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
        name_col.setCellValueFactory(new PropertyValueFactory<>("userId"));
        name_col.setCellFactory(createFullNameCellFactory(TransactionDAO));

        transactionID_col.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        toFromUserID_col.setCellValueFactory(new PropertyValueFactory<>("toFromUserID"));
        toFromUserID_col.setCellFactory(createFullNameCellFactory(TransactionDAO));

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

        /** Add listener to transaction list to update total transaction count */
        transactionList.addListener((ListChangeListener<Transaction>) change -> updateTotalTransactionCount());

        /** By default will print all transaction made by all user */
        loadTransactions();

    }


    public void loadTransactions() {
        ObservableList<Transaction> transactions = TransactionDAO.getTransactions();
        transactionTable.setItems(transactions);
        totalTransaction.setText("" + transactions.size());
    }

    private void updateTotalTransactionCount() {
        totalTransaction.setText("" + transactionList.size());
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
                List<Transaction> transactions = TransactionDAO.getTransactionsByDateRange(startDate, endDate);
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
            List<Transaction> transactions = TransactionDAO.getTransactionsByAmountRange(minAmount, maxAmount);
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
        List<Transaction> transactions = TransactionDAO.getTransactionsByCategory(categoryValue);
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


    // Set a custom cell factory to display full name based on userID
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> createFullNameCellFactory(TransactionAdminDAO dao) {
        return column -> new TableCell<>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String fullName = dao.getUserFullName((Integer) item);
                    setText(fullName);
                }
            }
        };
    }
}