package com.example.controller;

import com.example.Transaction;
import com.example.TransactionDAO;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.time.format.DateTimeFormatter;

public class ReceiptController {
    @FXML
    private Text transactionID_text;
    @FXML
    private Text date_text;

    @FXML
    private Text fromFullName_text;
    @FXML
    private Text toFullName_text;
    @FXML
    private Text amount_text;
    // Define date and time format
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

    private final TransactionDAO transactionDAO = new TransactionDAO();

    public void setTransactionDetails(Transaction transaction) {
        // Set the transaction ID
        transactionID_text.setText(String.valueOf(transaction.getTransactionId()));

        // Format and set the transaction date
        String formattedDate = transaction.getTransactionDate().format(FORMATTER);
        date_text.setText(formattedDate);

        // Determine and set the full names based on the transaction type
        String fromFullName, toFullName;
        if ("Debit".equals(transaction.getTransactionType().toString())) {
            // If the transaction is a debit, user2 (toFromUserID) sends money to user1 (userId)
            fromFullName = transactionDAO.getUserFullName(transaction.getToFromUserID());
            toFullName = transactionDAO.getUserFullName(transaction.getUserId());
        } else {
            fromFullName = transactionDAO.getUserFullName(transaction.getUserId());
            toFullName = transactionDAO.getUserFullName(transaction.getToFromUserID());
        }
        fromFullName_text.setText(fromFullName);
        toFullName_text.setText(toFullName);

        // Set the transaction amount
        amount_text.setText(String.valueOf(transaction.getAmount()));
    }


}

