package com.example;

import java.sql.*;
import java.util.Scanner;

public class TransactionHistory {
    private static final String URL = "jdbc:mysql://localhost:3306/gringottsbank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int selection = startTransactionHistory(scanner);

        switch (selection) {
            case 1:
                System.out.println("Here is all transaction history:");
                System.out.println(getAllTransactionHistory());
                break;
            case 2:
                System.out.print("Enter your account ID: ");
                int accountID = scanner.nextInt();
                System.out.println("Here is transaction history for account ID " + accountID + ":");
                System.out.println(getTransactionHistoryBy_accountID(accountID));
                break;
            case 3:
                System.out.print("Enter your user ID: ");
                int userID = scanner.nextInt();
                System.out.println(getTransactionHistoryBy_username(userID));
                break;
            case 4:
                System.out.print("Enter start date (yyyy-mm-dd): ");
                String startDateStr = scanner.next();
                System.out.print("Enter end date (yyyy-mm-dd): ");
                String endDateStr = scanner.next();
                System.out.println(getTransactionHistoryBy_date(startDateStr, endDateStr));
                break;
            case 5:
                System.out.print("Enter minimum amount: ");
                double minAmount = scanner.nextDouble();
                System.out.print("Enter maximum amount: ");
                double maxAmount = scanner.nextDouble();
                System.out.println(getTransactionHistoryBy_amount(minAmount, maxAmount));
                break;
            case 6:
                System.out.print("Enter a category: ");
                String category = scanner.next();
                System.out.println(getTransactionHistoryBy_category(category));
                break;
            default:
                System.out.println("Invalid selection");
        }
        scanner.close();
    }

    public static int startTransactionHistory(Scanner scanner) {
        int input = 0;
        boolean validInput = false;

        while (!validInput) {
            System.out.println("\nTransaction History Options:");
            System.out.println("1 - View all transaction history");
            System.out.println("2 - View transaction history by account ID");
            System.out.println("3 - View transaction history by user ID (for admins only)");
            System.out.println("4 - View transaction history by date");
            System.out.println("5 - View transaction history by amount");
            System.out.println("6 - View transaction history by category");
            System.out.print("Please select an option (1-6): ");

            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input >= 1 && input <= 6) {
                    validInput = true;
                } else {
                    System.out.println("Invalid input. Please enter a number from 1 to 6.");
                    scanner.nextLine(); // Consume invalid input
                }
            } else {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Consume invalid input
            }
        }

        return input;
    }

    //Admin can access nia
    public static String getAllTransactionHistory() {
        System.out.println("All Transaction History");
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             Statement statement = connection.createStatement()) {

            String query = "SELECT * FROM allTransactions ORDER BY transaction_date DESC";
            ResultSet resultSet = statement.executeQuery(query);

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }

    public static String getTransactionHistoryBy_accountID(int accountID) {
        System.out.println("Transaction History of Account ID: " + accountID);
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction_history WHERE accountID = ? ORDER BY transaction_date DESC")) {

            statement.setInt(1, accountID);
            ResultSet resultSet = statement.executeQuery();

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }

    public static String getTransactionHistoryBy_username(int userID) {
        System.out.println("Transaction History of User ID: " + userID);
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction_history WHERE userID = ? ORDER BY transaction_date DESC")) {

            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }

    public static String getTransactionHistoryBy_date(String startDateStr, String endDateStr) {
        System.out.printf("Transaction History between %s to %s: \n", startDateStr, endDateStr);
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction_history WHERE transaction_date BETWEEN  ? AND ? ORDER BY transaction_date DESC")) {

            // Convert user input strings to Timestamp objects
            Timestamp startDate = Timestamp.valueOf(startDateStr + " 00:00:00");
            Timestamp endDate = Timestamp.valueOf(endDateStr + " 23:59:59");

            statement.setTimestamp(1, startDate);
            statement.setTimestamp(2, endDate);

            ResultSet resultSet = statement.executeQuery();

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }

    public static String getTransactionHistoryBy_amount(double minAmount, double maxAmount) {
        System.out.printf("Transaction History of amount between %.2f to %.2f: \n", minAmount, maxAmount);
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction_history WHERE amount BETWEEN ? AND ? ORDER BY transaction_date DESC")) {

            statement.setDouble(1, minAmount);
            statement.setDouble(1, maxAmount);
            ResultSet resultSet = statement.executeQuery();

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }
    public static String getTransactionHistoryBy_category(String category) {
        System.out.println("Transaction History of category: " + category);
        StringBuilder tableBuilder = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM transaction_history WHERE category = ? ORDER BY transaction_date DESC")) {

            statement.setString(1, category);
            ResultSet resultSet = statement.executeQuery();

            // Build table header
            buildTableHeader(tableBuilder);

            // Build table rows
            buildTableRows(resultSet, tableBuilder);

            // Close resources
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableBuilder.toString();
    }
    private static void buildTableHeader(StringBuilder tableBuilder) {
        tableBuilder.append("+--------------+------------+----------+------------------+------------+------------------------+-----------------+-----------------+\n");
        tableBuilder.append("| Transaction  | Account    | User     | Transaction      | Amount     | Transaction            | Payment         | Category        |\n");
        tableBuilder.append("| ID           | ID         | ID       | Type             |            | Date                   | Method          |                 |\n");
        tableBuilder.append("+--------------+------------+----------+------------------+------------+------------------------+-----------------+-----------------+\n");
    }

    private static void buildTableRows(ResultSet resultSet, StringBuilder tableBuilder) throws SQLException {
        while (resultSet.next()) {
            int transactionID = resultSet.getInt("transactionID");
            int accountID = resultSet.getInt("accountID");
            int userID = resultSet.getInt("userID");
            String transactionType = resultSet.getString("transaction_type");
            double amount = resultSet.getDouble("amount");
            Timestamp transactionDate = resultSet.getTimestamp("transaction_date");
            String paymentMethod = resultSet.getString("payment_method");
            String category = resultSet.getString("category");

            tableBuilder.append(String.format("| %-12d | %-10d | %-8d | %-16s | %-10.2f | %-22s | %-15s | %-15s |\n",
                    transactionID, accountID, userID, transactionType, amount, transactionDate, paymentMethod, category));
        }
        tableBuilder.append("+--------------+------------+----------+------------------+------------+------------------------+-----------------+-----------------+\n");
    }


}
