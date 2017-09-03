package com.conference;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL {
    public static Connection connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/conferencedb","root","");
            return conn;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Connection error");
            alert.showAndWait();
            return null;
        }
    }
}