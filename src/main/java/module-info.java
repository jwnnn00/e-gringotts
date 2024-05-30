module e.gringotts {
    exports com.example;
    exports com.example.controller; opens com.example.controller; exports com.example.model; requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires jakarta.persistence; requires jbcrypt;
    requires spring.boot;
    requires spring.boot.autoconfigure; requires java.mail;
    requires activation;
}