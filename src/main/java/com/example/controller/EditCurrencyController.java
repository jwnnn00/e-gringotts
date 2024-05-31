package com.example.controller;

import com.example.Database;
import com.example.model.Account;
import com.example.model.AccountHolder;
import com.example.model.Currency;
import com.example.model.CurrencyRate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.util.converter.DoubleStringConverter;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import static com.example.Database.fetchConversionRates;
import static com.example.controller.DBUtils.showAlert;

public class EditCurrencyController extends HomeController implements Initializable {

    @FXML
    private TableView<CurrencyRate> tableView;
    @FXML
    private TableColumn<CurrencyRate, String> currencyColumn;
    @FXML
    private TableColumn<CurrencyRate, String> toCurrencyColumn;
    @FXML
    private TableColumn<CurrencyRate, Double> exchangeRateColumn;
    @FXML
    private TableColumn<CurrencyRate, Double> processingFeeColumn;
    @FXML
    private TextField tf_currency;
    @FXML
    private TextField tf_toCurrency;
    @FXML
    private TextField tf_exchangeRate;
    @FXML
    private TextField tf_processingFee;
    @FXML
    private Button button_saveChange;
    private ObservableList<CurrencyRate> currencyRates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);

        // Initialize table columns
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        toCurrencyColumn.setCellValueFactory(new PropertyValueFactory<>("toCurrency"));
        exchangeRateColumn.setCellValueFactory(new PropertyValueFactory<>("exchangeRate"));
        processingFeeColumn.setCellValueFactory(new PropertyValueFactory<>("processingFee"));

        tableView.setEditable(true);
        fetchConversionRates();

        // Add mouse click listener to the tableView
        tableView.setOnMouseClicked(event -> {
            if (tableView.getSelectionModel().getSelectedItem() != null) {
                CurrencyRate selectedRate = tableView.getSelectionModel().getSelectedItem();
                fillTextFields(selectedRate);
                button_saveChange.setText("Edit Currency");
                button_saveChange.setOnAction(this::submit);
            } else {
                clearTextFields();
                button_saveChange.setText("Add Currency");
                button_saveChange.setOnAction(this::addNewCurrency);
            }
        });

        // Add listener to handle clicks on empty space
        tableView.setRowFactory(tv -> {
            TableRow<CurrencyRate> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.isEmpty()) {
                    clearTextFields();
                    button_saveChange.setText("Add Currency");
                    button_saveChange.setOnAction(this::addNewCurrency);
                    tableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
    }

    private void fillTextFields(CurrencyRate rate) {
        tf_currency.setText(rate.getCurrency());
        tf_toCurrency.setText(rate.getToCurrency());
        tf_exchangeRate.setText(String.valueOf(rate.getExchangeRate()));
        tf_processingFee.setText(String.valueOf(rate.getProcessingFee()));
    }

    private void clearTextFields() {
        tf_currency.clear();
        tf_toCurrency.clear();
        tf_exchangeRate.clear();
        tf_processingFee.clear();
    }

    @FXML
    private void submit(javafx.event.ActionEvent event) {
        ObservableList<CurrencyRate> currencyTableData = tableView.getItems();
        String currentCurrency = tf_currency.getText();

        for (CurrencyRate rate : currencyTableData) {
            if (rate.getCurrency().equals(currentCurrency)) {
                rate.setToCurrency(tf_toCurrency.getText());
                rate.setExchangeRate(Double.parseDouble(tf_exchangeRate.getText()));
                rate.setProcessingFee(Double.parseDouble(tf_processingFee.getText()));
                Database.updateConversionRates(rate.getCurrency(), rate.getToCurrency(), rate.getExchangeRate(), rate.getProcessingFee());

                tableView.setItems(currencyTableData);
                tableView.refresh();
                break;
            }
        }
    }


    private void fetchConversionRates() {
        currencyRates = FXCollections.observableArrayList();

        // Fetch conversion rates from the database
        Map<String, Map<String, Object>> conversionRates = Database.fetchConversionRates();

        if (conversionRates != null) {
            for (Map.Entry<String, Map<String, Object>> entry : conversionRates.entrySet()) {
                String currency = entry.getKey();
                Map<String, Object> rates = entry.getValue();

                // Retrieve and parse the values
                String toCurrency = (String) rates.getOrDefault("toCurrency", "N/A");
                double exchangeRate = (double) rates.getOrDefault("exchangeRate", 0.0);
                double processingFee = (double) rates.getOrDefault("processingFee", 0.0);

                currencyRates.add(new CurrencyRate(currency, toCurrency, exchangeRate, processingFee));
            }
        }

        // Set the fetched conversion rates in the TableView
        tableView.setItems(currencyRates);
    }

    @FXML
    private void addNewCurrency(javafx.event.ActionEvent event) {
        String currency = tf_currency.getText();
        String toCurrency = tf_toCurrency.getText();
        double exchangeRate;
        double processingFee;

        try {
            exchangeRate = Double.parseDouble(tf_exchangeRate.getText());
            processingFee = Double.parseDouble(tf_processingFee.getText());
        } catch (NumberFormatException e) {
            // Handle invalid number format
            showAlert("Invalid input", "Please enter valid numbers for exchange rate and processing fee.");
            return;
        }

        // Add the new currency to the database
        if (addCurrencyToDatabase(currency, toCurrency, exchangeRate, processingFee)) {
            showAlert("New Currency Added", "The new currency has been added to the database.");
        } else {
            showAlert("Error", "Failed to add the new currency to the database.");
        }
    }

    private boolean addCurrencyToDatabase(String currency, String toCurrency, double exchangeRate, double processingFee) {
        // Add the new currency details to the database
        return Database.addConversionRate(currency, toCurrency, exchangeRate, processingFee);
    }


    public void rowClicked(javafx.scene.input.MouseEvent mouseEvent) {
        CurrencyRate clickedCurrency = tableView.getSelectionModel().getSelectedItem();
        if (clickedCurrency != null) {
            tf_currency.setText(clickedCurrency.getCurrency());
            tf_toCurrency.setText(clickedCurrency.getToCurrency());
            tf_exchangeRate.setText(String.valueOf(clickedCurrency.getExchangeRate()));
            tf_processingFee.setText(String.valueOf(clickedCurrency.getProcessingFee()));
        }
    }
}
