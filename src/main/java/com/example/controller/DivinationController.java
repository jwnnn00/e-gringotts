package com.example.controller;

import com.example.controller.HomeController;
import com.example.divinationData;
import com.example.model.Account;
import com.example.model.AccountHolder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DivinationController extends HomeController implements Initializable {

    private Scene previousScene;
    private Long userId;



        @FXML
        private Button backToMain;
        @FXML
        private Button checkBalance;
        @FXML
        private Button transactionHistory;
        @FXML
        private Button transfer;
        @FXML
        private TextField username;
        @FXML
        private TextField totalExpenses;
        @FXML
        private PieChart expenditureSummary;
        @FXML
        private Button FoodCategory;
        @FXML
        private Button TransporataionCategory;
        @FXML
        private Button EntertainmentCategory1;
        @FXML
        private Button UtilitesCategory1;
        @FXML
        private Button ShoppingCategory11;
        @FXML
        private Button Others;
        @FXML
        private Button dateRange;
        @FXML
        private TextField startDate;
        @FXML
        private Text t_username;
        @FXML
        private TextField endDate;
        @FXML
        private Button Credit;
        @FXML
        private Button Debit;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AccountHolder holder = AccountHolder.getInstance();
        Account<?> loggedInAccount = holder.getUser();
        String username = loggedInAccount.getUsername();
        UIUtils.initializeUI(button_userProfile, user_avatar, loggedInAccount, username);
        t_username.setText(username);

    }


        // Method to set the previous scene
        public void setPreviousScene(Scene scene) {

            this.previousScene = scene;
        }

        @FXML
        private void handleBackToMain(MouseEvent event) {
             Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
             stage.setScene(previousScene);
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
        private void showUserNameAndTotalExpenses(){

        }
        @FXML
        private void handleGeneratePieChart(MouseEvent event) {

            try (Connection conn = divinationData.DatabaseManager.getConnection()) {
                Map<String, Double> expenditures = divinationData.calculateExpenditures(conn, userId);

                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

                for (Map.Entry<String, Double> entry : expenditures.entrySet()) {
                    pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }

                expenditureSummary.setData(pieChartData);
                expenditureSummary.setTitle("Expenditure Breakdown");
                expenditureSummary.setLegendSide(Side.RIGHT);
                expenditureSummary.setClockwise(false);

                for(final PieChart.Data data: expenditureSummary.getData()){
                    data.getNode().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            System.out.println("Pie chart clicked: " + data.getName() + " - " + data.getPieValue());
                        }
                    });
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @FXML
        private void handleFoodCategory(MouseEvent event) throws SQLException{
                // Call the method to filter transactions by category "Food" and userId
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Food");
        }

        @FXML
        private void handleTransporataionCategory(MouseEvent event) throws SQLException{
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Transportation");
        }

        @FXML
        private void handleEntertainmentCategory1(MouseEvent event) throws SQLException{
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Entertainment");
        }

        @FXML
        private void handleUtilitesCategory1(MouseEvent event) throws SQLException{
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Utilities");
        }

        @FXML
        private void handleShoppingCategory11(MouseEvent event) throws SQLException{
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Shopping");
        }

        @FXML
        private void handleOthers(MouseEvent event) throws SQLException{
            if (userId != null)
                divinationData.filterTransactionsByCategory(userId, "Others");
        }

        @FXML
        private void handleDateRange(ActionEvent event) {
            String startDateText = startDate.getText();
            String endDateText = endDate.getText();

            if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
                try {
                    LocalDate startDate = LocalDate.parse(startDateText);
                    LocalDate endDate = LocalDate.parse(endDateText);

                    if (userId != null) {
                        divinationData.filterTransactionsByDateRange(userId, startDate, endDate);
                    } else {
                        System.out.println("User ID not available. Please log in.");
                    }

                } catch (Exception e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                }
            } else {
                System.out.println("Please enter both start date and end date.");
            }
        }

        @FXML
        private void handleCredit(MouseEvent event) throws SQLException{

            if (userId != null)
                divinationData.filterTransactionsByPaymentMethod(userId, "Entertainment");
        }

        @FXML
        private void handleDebit(MouseEvent event) throws SQLException{

            if (userId != null)
                divinationData.filterTransactionsByPaymentMethod(userId, "Entertainment");
        }



    }

