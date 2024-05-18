package com.example.controller;

import com.example.Database;
import com.example.model.UserType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class NumOfUserController implements Initializable {

    @FXML
    private Text t_totalNumOfUser;

    @FXML
    private BarChart<String, Number> userChart;

    @FXML
    private CategoryAxis x_numberOfUser;

    @FXML
    private NumberAxis y_typeOfUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the number of users by type
        int goblins = Database.getNumberOfUsersByType(UserType.Goblin);
        int sliverSnitches = Database.getNumberOfUsersByType(UserType.Silver_Snitch);
        int goldenGalleons = Database.getNumberOfUsersByType(UserType.Golden_Galleon);
        int platinumPatronuses = Database.getNumberOfUsersByType(UserType.Platinum_Patronus);
        int totalUsersExcludingGoblin = Database.getNumberOfUsers();

        // Update the text element displaying total number of users
        t_totalNumOfUser.setText(String.valueOf(totalUsersExcludingGoblin));

        // Create data series for the BarChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Goblins", goblins));
        series.getData().add(new XYChart.Data<>("Sliver Snitches", sliverSnitches));
        series.getData().add(new XYChart.Data<>("Golden Galleons", goldenGalleons));
        series.getData().add(new XYChart.Data<>("Platinum Patronuses", platinumPatronuses));

        // Clear existing data and add the new series to the BarChart


        userChart.getData().clear();
        userChart.getData().add(series);
    }
}
