package com.example.controller;

import com.example.Database;
import com.example.model.CurrencyRate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class EditCurrencyController extends HomeController implements Initializable {

    @FXML
    private TableView<CurrencyRate> tableView;

    @FXML
    private TableColumn<CurrencyRate, String> currencyColumn;

    @FXML
    private TableColumn<CurrencyRate, Double> toKnutColumn;
    @FXML
    private TableColumn<CurrencyRate, Double> toSickleColumn;
    @FXML
    private TableColumn<CurrencyRate, Double> toGalleonColumn;

    @FXML
    private Button button_editCurrency;

    private ObservableList<CurrencyRate> currencyRates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize table columns
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        toKnutColumn.setCellValueFactory(new PropertyValueFactory<>("toKnut"));
        toSickleColumn.setCellValueFactory(new PropertyValueFactory<>("toSickle"));
        toGalleonColumn.setCellValueFactory(new PropertyValueFactory<>("toGalleon"));

        // Fetch currency conversion rates from the database
        fetchConversionRates();

        // Populate table with fetched currency values
        tableView.setItems(currencyRates);
    }

    // Method to fetch currency conversion rates from the database
    private void fetchConversionRates() {
        currencyRates = FXCollections.observableArrayList();
        Map<String, Map<String, Double>> conversionRates = Database.fetchConversionRates();
        if (conversionRates != null) {
            for (Map.Entry<String, Map<String, Double>> entry : conversionRates.entrySet()) {
                String currency = entry.getKey();
                Map<String, Double> rates = entry.getValue();
                double toKnut = rates.getOrDefault("to_knut", 0.0);
                double toSickle = rates.getOrDefault("to_sickle", 0.0);
                double toGalleon = rates.getOrDefault("to_galleon", 0.0);
                currencyRates.add(new CurrencyRate(currency, toKnut, toSickle, toGalleon));
            }
        }
    }

    // Method to handle action when "Edit" button is clicked
    @FXML
    private void handleEditCurrency() {
        // Allow editing of the table
        tableView.setEditable(true);
        button_editCurrency.setText("Save");
    }

    // Method to handle action when "Save" button is clicked
    @FXML
    private void handleSaveCurrency() {
        // Update the database with edited currency values
        for (CurrencyRate currencyRate : currencyRates) {
            Database.updateConversionRates(currencyRate.getCurrency(), currencyRate.getToKnut(), currencyRate.getToSickle(), currencyRate.getToGalleon());
        }

        // Disable editing of the table
        tableView.setEditable(false);
        button_editCurrency.setText("Edit");
    }
}
