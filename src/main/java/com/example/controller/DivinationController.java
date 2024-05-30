package com.example.controller;
import com.example.Transaction;

import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.example.divinationData;

import static com.example.divinationData.*;

public class DivinationController extends HomeController implements Initializable {

    private Scene previousScene;
    private int userId;



    @FXML
    private Button backToMain;
    @FXML
    private Button checkBalance;
    @FXML
    private Button transactionHistory;
    @FXML
    private Button transfer;
    @FXML
    private Text showUsername;
    @FXML
    private Text showTotal;

    @FXML
    private PieChart expenditureSummary;
    @FXML
    private TableView<Transaction> showDetails;
    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;
    @FXML
    private TableColumn<Transaction,Double> AmountColumn;
    @FXML
    private TableColumn<Transaction,LocalDate> DateColumn;
    @FXML
    private ToggleButton FoodCategory;
    @FXML
    private ToggleButton TransportationCategory;
    @FXML
    private ToggleButton EntertainmentCategory;
    @FXML
    private ToggleButton UtilitiesCategory;
    @FXML
    private ToggleButton ShoppingCategory;
    @FXML
    private ToggleButton Others;
    @FXML
    private ToggleButton dateRange;
    @FXML
    private DatePicker startDate;
    @FXML
    private DatePicker endDate;
    @FXML
    private ToggleButton Credit;
    @FXML
    private ToggleButton Debit;
    @FXML
    private Button refresh;

    private static final String URL = "jdbc:mysql://localhost:3307/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";


    @Override
    public void initialize(java.net.URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        showUsername.setText(username);
        this.userId= Math.toIntExact(loggedInAccount.getUserId());
        try {
            showUserNameAndTotalExpenses();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);
        handlePieChart();
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<Transaction,Integer>("transactionId"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<Transaction,Double>("amount"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<Transaction,LocalDate>("transactionDate"));

        showDetails.setVisible(false);
    }

    public void setUserId(int userId) {
        this.userId = userId;
        if (userId != 0) {
            try {
                showUserNameAndTotalExpenses();
                handlePieChart();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setPreviousScene(Scene scene) throws SQLException {
        this.previousScene = scene;
        showUserNameAndTotalExpenses();
    }


    @FXML
    private void handleCheckBalance(MouseEvent event) {

        System.out.println("Check Balance button clicked");
    }
    @FXML
    private void handleTransactionHistory(MouseEvent event) {
        System.out.println("Transaction History button clicked");
    }
    @FXML
    private void handleTransfer(MouseEvent event) {
        System.out.println("Transfer button clicked");
    }

    @FXML
    private void showUserNameAndTotalExpenses() throws SQLException {
        try (Connection conn = divinationData.DatabaseManager.getConnection()) {

            double totalExpenses = divinationData.calculateTotalExpenditures(conn, userId);
            showTotal.setText(String.format("%.2f", totalExpenses));
        }
    }

    private void displayTransactions(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            System.out.println(transaction); // Print transaction details
        }

        ObservableList<Transaction> transactionList = FXCollections.observableArrayList(transactions);
        expenditureSummary.setVisible(false);
        showDetails.setVisible(true);
        showDetails.setItems(transactionList);
    }

    @FXML
    private void handlePieChart() {
        expenditureSummary.setVisible(true);
        showDetails.setVisible(false);

        try (Connection conn = divinationData.DatabaseManager.getConnection()) {
            Map<String, Double> expenditures = divinationData.calculateExpenditures(conn, userId);

            // Calculate total expenditure
            double totalExpenditure = expenditures.values().stream().mapToDouble(Double::doubleValue).sum();

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            // Create pie chart data
            for (Map.Entry<String, Double> entry : expenditures.entrySet()) {
                double percentage = (entry.getValue() / totalExpenditure) * 100;
                pieChartData.add(new PieChart.Data(entry.getKey() + " (" + String.format("%.2f", percentage) + "%)", entry.getValue()));
            }

            expenditureSummary.setData(pieChartData);
            expenditureSummary.setLegendSide(Side.RIGHT);
            expenditureSummary.setClockwise(false);

            final Tooltip[] activeTooltip = {null};
            for (final PieChart.Data data : expenditureSummary.getData()) {
                Tooltip tooltip = new Tooltip(data.getName()+" - "+data.getPieValue());
                Tooltip.install(data.getNode(),tooltip);
                data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if(activeTooltip[0] != null){
                            activeTooltip[0].hide();
                        }
                        tooltip.show(data.getNode(), mouseEvent.getSceneX(), mouseEvent.getSceneY());
                        activeTooltip[0] = tooltip;
                        System.out.println(data.getName()+" - "+data.getPieValue());
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleFoodCategory(MouseEvent event) throws SQLException {
        FoodCategory.isSelected();
        handleCategoryToggle(event, "Food");
    }
    @FXML
    private void handleTransportationCategory(MouseEvent event) throws SQLException {
        TransportationCategory.isSelected();
        handleCategoryToggle(event, "Transportation");
    }
    @FXML
    private void handleEntertainmentCategory(MouseEvent event) throws SQLException {
        EntertainmentCategory.isSelected();
        handleCategoryToggle(event, "Entertainment");
    }
    @FXML
    private void handleUtilitiesCategory(MouseEvent event) throws SQLException {
        UtilitiesCategory.isSelected();
        handleCategoryToggle(event, "Utilities");
    }
    @FXML
    private void handleShoppingCategory(MouseEvent event) throws SQLException {
        ShoppingCategory.isSelected();
        handleCategoryToggle(event, "Shopping");
    }
    @FXML
    private void handleOthers(MouseEvent event) throws SQLException {
        Others.isSelected();
        handleCategoryToggle(event, "Others");
    }

    private void handleCategoryToggle(MouseEvent event, String category){
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        if (toggleButton.isSelected()) {
            expenditureSummary.setVisible(false);
            showDetails.setVisible(true);
            try{
                List<Transaction> filteredTransactions = filterTransactionsByCategory(userId, category);
                displayTransactions(filteredTransactions);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    private void handleDateRange(MouseEvent event) throws SQLException {
        if (dateRange.isSelected()) {
            expenditureSummary.setVisible(false);
            showDetails.setVisible(true);

            LocalDate start = startDate.getValue();
            LocalDate end = endDate.getValue();

            if (start != null && end != null) {
                try {
                    List<Transaction> filteredTransactions = filterTransactionsByDateRange(userId, start,end);
                    ObservableList<Transaction> tableList = FXCollections.observableArrayList(filteredTransactions);
                    displayTransactions(tableList);
                } catch (DateTimeParseException e) {
                    // Show alert for invalid date format
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Date Format");
                    alert.setHeaderText("Please use yyyy-MM-dd format for dates.");
                    alert.showAndWait();
                }
            } else {
                // Show alert for missing dates
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Dates");
                alert.setHeaderText("Please enter both start date and end date.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleCredit(MouseEvent event) throws SQLException {
        Credit.isSelected();
        handlePaymentMethodToggle(event, "Credit");
    }
    @FXML
    private void handleDebit(MouseEvent event) throws SQLException {
        Debit.isSelected();
        handlePaymentMethodToggle(event, "Debit");
    }

    private void handlePaymentMethodToggle(MouseEvent event, String paymentMethod){
        ToggleButton toggleButton = (ToggleButton) event.getSource();
        if (toggleButton.isSelected()) {
            expenditureSummary.setVisible(false);
            showDetails.setVisible(true);
            try{
                List<Transaction> filteredTransactions = filterTransactionsByPaymentMethod(userId, paymentMethod);
                displayTransactions(filteredTransactions);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void refreshDetails(MouseEvent event){
        handlePieChart();
    }
}