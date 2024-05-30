package com.example.controller;

import com.example.CurrencyConverter;

import com.example.Database;
import com.example.EmailSender;
import com.example.Transaction;
import com.example.controller.MarauderMapController;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.controller.DBUtils.showAlert;

public class TransferController extends HomeController implements Initializable {

    private List<String[]> currencies = new ArrayList<>();
    private CurrencyConverter converter;
    private String loggedInUsername;
    private String friendName;
    private String currencyy1;
    private String currencyy2;
    private double balance1;
    private double balance2;
    private double totalAmount;
    private String category;
    private Random random = new Random();
    private int transactionIdCounter;

    @FXML
    private ChoiceBox<String> Category;

    @FXML
    private ChoiceBox<String> FromCurrency;

    @FXML
    private ChoiceBox<String> ToCurrency;

    @FXML
    private TextField askAmount;

    @FXML
    private Text getTotal;

    @FXML
    private ImageView imageView;

    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView imageView3;
    private double exchangeAmount;
    private Account<?> loggedInAccount;


    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        loggedInAccount = holder.getUser();
        initializeLoggedInPage(loggedInAccount);

        loggedInUsername=loggedInAccount.getUsername();


            String q = "SELECT currency, balance FROM user WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(q)) {

                statement.setString(1, loggedInUsername);

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        currencyy1 = result.getString("currency");
                        balance1 = result.getDouble("balance");

                    } else {
                        System.out.println("User not found.");
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error fetching user data: " + e.getMessage());
            }


            friendName = MarauderMapController.getFriendName();
            String q2 = "SELECT currency, balance FROM user WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(q2)) {

                statement.setString(1, friendName);

                try (ResultSet result2 = statement.executeQuery()) {
                    if (result2.next()) {
                        currencyy2 = result2.getString("currency");
                        balance2 = result2.getDouble("balance");

                    } else {
                        System.out.println("User not found.");
                    }
                }

            } catch (SQLException e) {
                System.err.println("Error fetching user data: " + e.getMessage());
            }



        String[] currency123 = new String[20];
        fetchCurrencyRatesFromDatabase();

        // Initialize the CurrencyConverter with the currencies list
        converter = new CurrencyConverter(currencies);

        String query = "SELECT currency, toCurrency FROM conversion";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            int i = 0;
            while (resultSet.next()) {
                currency123[i] = resultSet.getString("currency");
                currency123[i+1] = resultSet.getString("toCurrency");
                i+=2;
            }

        } catch (SQLException e) {
            System.err.println("Error fetching currency data: " + e.getMessage());
        }

        List<String> currencyList = new ArrayList<>();
        for (String currency : currency123) {
            if (currency != null) {
                currencyList.add(currency);
            }
        }

        // Remove duplicates using a Set
        Set<String> currencySet = new HashSet<>(currencyList);

        // Convert the Set back to a List (if necessary)
        List<String> uniqueCurrencyList = new ArrayList<>(currencySet);

        // If you need to convert back to an array
        String[] uniqueCurrencyArray = uniqueCurrencyList.toArray(new String[0]);

        FromCurrency.getItems().addAll(uniqueCurrencyArray);
        ToCurrency.getItems().addAll(uniqueCurrencyArray);

            Category.getItems().addAll("Food", "Transportation", "Entertainment", "Utilities", "Others");

        }


        private void fetchCurrencyRatesFromDatabase() {
            String query = "SELECT currency, toCurrency, exchangeRate, processingFee FROM conversion";

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String currency = resultSet.getString("currency");
                    String to_currency = resultSet.getString("toCurrency");
                    double exchange_rate = resultSet.getDouble("exchangeRate");
                    double fee = resultSet.getDouble("processingFee");

                    currencies.add(new String[]{currency, to_currency, String.valueOf(exchange_rate), String.valueOf(fee)});

                }
            } catch (SQLException e) {
                System.err.println("Error fetching currency rates from database: " + e.getMessage());
            }
        }


        @FXML
        void getTotalAmount(ActionEvent event) {
            try {
                String fromCurrency = FromCurrency.getSelectionModel().getSelectedItem();
                String toCurrency = ToCurrency.getSelectionModel().getSelectedItem();
                category = Category.getSelectionModel().getSelectedItem();
                double amount = Double.parseDouble(askAmount.getText());

                if (fromCurrency == null || !fromCurrency.equals(currencyy1)  || toCurrency == null || !toCurrency.equals(currencyy2)  || category == null || amount <= 0) {
                    getTotal.setText("Please select right currency, category\nand enter valid amount");
                    return;
                }

                double[] result = converter.exchangeCurrency(fromCurrency, toCurrency, amount);
                exchangeAmount = result[0];
                double processingFee = result[1];
                totalAmount = amount + processingFee;

                getTotal.setText(String.format("Exchange Amount: %.2f %s\nProcessing Fee: %.2f %s\nTotal: %.2f %s",
                        exchangeAmount, toCurrency, processingFee, fromCurrency, totalAmount, fromCurrency));
            } catch (Exception e) {
                getTotal.setText("Please select right currency, category\nand enter valid amount");
            }
        }



        @FXML
        void proceedToPayment(ActionEvent event) {
            boolean pinVerified = promptForPin();

            if(pinVerified) {

                if (balance1 >= totalAmount) {
                    balance1 -= totalAmount;
                    balance2 += exchangeAmount;
                    updateBalanceInDatabase(balance1, loggedInUsername);
                    updateBalanceInDatabase(balance2, friendName);
                    transactionHistory1(loggedInUsername, friendName);
                    transactionHistory2(friendName, loggedInUsername);

                    sendTransferNotification(loggedInAccount.getEmail());

//                Transaction transaction = createTransactionObject(); // You need to implement this method
//                displayReceipt(transaction);


                } else {
                    // Show insufficient balance message
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Insufficient Balance");
                    alert.setHeaderText(null);
                    alert.setContentText("Insufficient balance. Please add funds to your account.");
                    alert.showAndWait();
                }
            }else{
                System.out.println("PIN verification failed. Payment aborted.");
            }
        }

        private void updateBalanceInDatabase(double newBalance, String name) {
            String updateQuery = "UPDATE user SET balance = ? WHERE username = ?";

            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
                 PreparedStatement statement = connection.prepareStatement(updateQuery)) {

                statement.setDouble(1, newBalance);
                statement.setString(2, name);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Balance updated successfully.");
                } else {
                    System.out.println("Failed to update balance.");
                }

            } catch (SQLException e) {
                System.err.println("Error updating balance: " + e.getMessage());
}
}

    private void sendTransferNotification(String recipientEmail) {
        String subject = "Transfer Successful";
        String body = "Dear user,\n\nYour transfer of " + exchangeAmount + " " + currencyy2 + " to " + friendName + " has been successfully completed.\n\nThank you for using Gringotts Bank.";

        // Call the EmailSender's sendEmail method
        EmailSender.sendEmail(recipientEmail, subject, body);
    }

    private void displayReceipt(Transaction transaction) {
        try {
            // Load the Receipt.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pages/Receipt-view.fxml"));
            Parent root = loader.load();

            // Get the controller
            ReceiptController receiptController = loader.getController();

            // Set the transaction details
            receiptController.setTransactionDetails(transaction);

            // Create a new stage for the receipt
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Transaction Receipt");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transactionHistory2(String name1, String name2) {
        String queryUserId = "SELECT userId FROM user WHERE username = ?";
        String insertTransactionQuery = "INSERT INTO transaction (transactionId, userId, tofrom_userID, transactionType, amount, balance, transactionDate, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement getUserIdStatement = connection.prepareStatement(queryUserId);
             PreparedStatement insertTransactionStatement = connection.prepareStatement(insertTransactionQuery)) {

            // Retrieve user ID for name1
            getUserIdStatement.setString(1, name1);
            ResultSet rsName1 = getUserIdStatement.executeQuery();
            int userId1 = rsName1.next() ? rsName1.getInt("userId") : -1;

            // Retrieve user ID for name2
            getUserIdStatement.setString(1, name2);
            ResultSet rsName2 = getUserIdStatement.executeQuery();
            int userId2 = rsName2.next() ? rsName2.getInt("userId") : -1;


            // Insert transaction record
            insertTransactionStatement.setInt(1, transactionIdCounter);
            insertTransactionStatement.setInt(2, userId1);
            insertTransactionStatement.setInt(3, userId2);
            insertTransactionStatement.setString(4, "Debit");
            insertTransactionStatement.setDouble(5, exchangeAmount);
            insertTransactionStatement.setDouble(6, balance2);
            insertTransactionStatement.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            insertTransactionStatement.setString(8, category);

            int rowsAffected = insertTransactionStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction recorded successfully.");
            } else {
                System.out.println("Failed to record transaction.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
    }

    private void transactionHistory1(String name1, String name2) {
        String queryUserId = "SELECT userId FROM user WHERE username = ?";
        String insertTransactionQuery = "INSERT INTO transaction (transactionId, userId, tofrom_userID, transactionType, amount, balance, transactionDate, category) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement getUserIdStatement = connection.prepareStatement(queryUserId);
             PreparedStatement insertTransactionStatement = connection.prepareStatement(insertTransactionQuery)) {

            // Retrieve user ID for name1
            getUserIdStatement.setString(1, name1);
            ResultSet rsName1 = getUserIdStatement.executeQuery();
            int userId1 = rsName1.next() ? rsName1.getInt("userId") : -1;

            // Retrieve user ID for name2
            getUserIdStatement.setString(1, name2);
            ResultSet rsName2 = getUserIdStatement.executeQuery();
            int userId2 = rsName2.next() ? rsName2.getInt("userId") : -1;

            // Increment transaction ID counter
            transactionIdCounter = generateUniqueTransactionId();

            // Insert transaction record
            insertTransactionStatement.setInt(1, transactionIdCounter);
            insertTransactionStatement.setInt(2, userId1);
            insertTransactionStatement.setInt(3, userId2);
            insertTransactionStatement.setString(4, "Credit");
            insertTransactionStatement.setDouble(5, totalAmount);
            insertTransactionStatement.setDouble(6, balance1);
            insertTransactionStatement.setDate(7, java.sql.Date.valueOf(LocalDate.now()));
            insertTransactionStatement.setString(8, category);

            int rowsAffected = insertTransactionStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Transaction recorded successfully.");

                // Create a Transaction object
                Transaction transaction = new Transaction();
                // Set transaction details
                transaction.setTransactionId(transactionIdCounter);
                transaction.setUserId(userId1);
                transaction.setToFromUserID(userId2);
                transaction.setTransactionType(Transaction.TransactionType.Credit);
                transaction.setAmount(BigDecimal.valueOf(totalAmount));
                transaction.setBalance(BigDecimal.valueOf(balance1));
                transaction.setTransactionDate(LocalDateTime.now());
                transaction.setCategory(Transaction.Category.valueOf(category));

                // Display the receipt
                displayReceipt(transaction);
            } else {
                System.out.println("Failed to record transaction.");
            }

        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
        }
    }

    private int generateUniqueTransactionId() {
        int transactionId;
        String query = "SELECT COUNT(*) AS count FROM transaction WHERE transactionId = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            int count;
            do {
                // Generate a new transactionId
                transactionId = random.nextInt(999990000) + 10000;

                // Check if the generated transactionId already exists
                statement.setInt(1, transactionId);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                count = resultSet.getInt("count");

                // If the transactionId already exists, generate a new one
            } while (count > 0);

        } catch (SQLException e) {
            System.err.println("Error generating unique transactionId: " + e.getMessage());
            // Return a default value if an error occurs
            transactionId = -1;
        }

        return transactionId;
}
    private boolean promptForPin() {
        TextInputDialog pinDialog = new TextInputDialog();
        pinDialog.setTitle("PIN Verification");
        pinDialog.setHeaderText("Enter your 6-digit PIN");
        pinDialog.setContentText("PIN:");

        Optional<String> pinResult = pinDialog.showAndWait();
        if (pinResult.isPresent()) {
            String enteredPin = pinResult.get();
            String storedPin = Database.getUserPin(loggedInUsername); // Assuming you have a method to get the stored PIN from the database

            if (enteredPin.equals(storedPin)) {
                return true; // PIN is correct
            } else {
                showAlert("Invalid PIN", "The entered PIN is incorrect. Please try again.");
                return false; // PIN is incorrect
            }
        } else {
            showAlert("PIN Required", "Please enter your PIN.");
            return false; // PIN is not entered
        }
    }

    }