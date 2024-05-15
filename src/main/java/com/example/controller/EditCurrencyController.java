package com.example.controller;

import com.example.Database;
import com.example.model.Account;
import com.example.model.AccountHolder;
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
    private TextField tf_currency;
    @FXML
    private TextField tf_toKnut;
    @FXML
    private TextField tf_toSickle;
    @FXML
    private TextField tf_toGalleon;


    @FXML
    private Button button_editCurrency;

    private ObservableList<CurrencyRate> currencyRates;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);

        // Initialize table columns
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currency"));
        toKnutColumn.setCellValueFactory(new PropertyValueFactory<>("toKnut"));
        toSickleColumn.setCellValueFactory(new PropertyValueFactory<>("toSickle"));
        toGalleonColumn.setCellValueFactory(new PropertyValueFactory<>("toGalleon"));

        tableView.setEditable(true);
       fetchConversionRates();
    }

    @FXML
    private void submit(javafx.event.ActionEvent event) {
       ObservableList<CurrencyRate> currencyTableData = tableView.getItems();
       String currentCurrency = tf_currency.getText();

       for(CurrencyRate rate:currencyTableData){
           if(rate.getCurrency().equals(currentCurrency)){
               rate.setToKnut(Double.parseDouble(tf_toKnut.getText()));
               rate.setToSickle(Double.parseDouble(tf_toSickle.getText()));
               rate.setToGalleon(Double.parseDouble(tf_toGalleon.getText()));
               Database.updateConversionRates(rate.getCurrency(), rate.getToKnut(), rate.getToSickle(), rate.getToGalleon());

               tableView.setItems(currencyTableData);
               tableView.refresh();
               break;
           }
       }
    }



    private void fetchConversionRates() {
        currencyRates = FXCollections.observableArrayList();

        // Assuming Database.fetchConversionRates() returns a Map<String, Map<String, Double>> where the keys are currency names
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

        // Set the fetched conversion rates in the TableView
        tableView.setItems(currencyRates);
    }

    public void rowClicked(javafx.scene.input.MouseEvent mouseEvent) {
        CurrencyRate clickedCurrency = tableView.getSelectionModel().getSelectedItem();
        tf_currency.setText(clickedCurrency.getCurrency());
        tf_toKnut.setText(String.valueOf(clickedCurrency.getToKnut()));
        tf_toSickle.setText(String.valueOf(clickedCurrency.getToSickle()));
        tf_toGalleon.setText(String.valueOf(clickedCurrency.getToGalleon()));
    }
}
