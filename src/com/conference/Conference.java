package com.conference;

import com.conference.user.Administrator;
import com.conference.user.Member;
import com.conference.user.Retailer;
import com.conference.user.Receptionist;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class Conference extends Application {

    private Member loggedIn;

    public static Scene loginScene;

    // db
    private Connection cn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;
    private Statement st = null;

    // view
    private TextField userId;
    private Button loginButton;
    private Label logoText, loginLabel, loginStatus, copyright;
    private Stage stage;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        cn = MySQL.connect();

//        System.out.println(conn);
        stage = primaryStage;
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));

        // header start
        HBox header = new HBox();
        logoText = new Label("Conference Tracker");
        header.getChildren().add(logoText);
        // header end

        // body start
        VBox body = new VBox(10);

        HBox statusContainer = new HBox(10);
        loginLabel = new Label("Status : ");
        loginStatus = new Label();
        if(cn == null) {
            loginStatus.setText("Connection Error");
        } else {
            loginStatus.setText("Ready");
        }
        statusContainer.getChildren().addAll(loginLabel, loginStatus);
        statusContainer.setAlignment(Pos.CENTER);

        userId = new TextField();
        userId.setPromptText("Insert user Id");
        userId.setMaxWidth(200);
        userId.textProperty().addListener(e  -> {
            if(cn == null) {
                Platform.runLater(() -> userId.clear());
                DialogBox.alertBox("Warning", "Connection Error");
            } else {
                if(DialogBox.numberOnly(userId) && userId.getText().length() >= 10) {
                    login();
                }
            }
        });

        loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            if(cn == null) {
                Platform.runLater(() -> userId.clear());
                DialogBox.alertBox("Warning", "Connection Error");
            } else {
                login();
            }
        });
        body.getChildren().addAll(statusContainer, userId, loginButton);
        body.setAlignment(Pos.CENTER);
        // body end

        // footer start
        HBox footer = new HBox(10);
        copyright = new Label("(c) Cheer Solution 2017");
//        copyright.setAlignment(Pos.CENTER);
        footer.getChildren().add(copyright);
        footer.setAlignment(Pos.CENTER);
        // footer end

        layout.setTop(header);
        layout.setCenter(body);
        layout.setBottom(footer);

        scene = new Scene(layout, 1024, 768);

        stage.setTitle("Login");
        stage.setScene(scene);
        loginScene = scene;
        stage.show();

        if(cn == null) {
            userId.setDisable(true);
            loginButton.setDisable(true);
            loginStatus.setText("No Connection to Database, Turn On Database and Restart Application.");
        }
    }

    public void login() {
        try {
            String sql = "SELECT * FROM member WHERE memberid=?";
            pst = cn.prepareStatement(sql);
            pst.setString(1, userId.getText());
            rs = pst.executeQuery();
            if(rs.next()) {
                // remove line after fine another solution
//                loggedId = rs.getString("userid");
                // login based on level
                if(rs.getInt("position") == 3) {
                    loggedIn = new Administrator(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getDate(7),
                            rs.getInt(8));
                    loggedIn.view(stage);
                } else if(rs.getInt("position") == 2) {
                    loggedIn = new Receptionist(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getDate(7),
                            rs.getInt(8));
                    loggedIn.view(stage);
//                    Retailer.view(stage);
                }  else if(rs.getInt("position") == 1) {
                    loggedIn = new Retailer(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getDate(7),
                            rs.getInt(8));
                    loggedIn.view(stage);
                } else {
                    loggedIn = new Member(rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getDate(7),
                            rs.getInt(8));
                    loggedIn.view(stage);
                }
            } else {
//                loginStatus.setText("Invalid ID, ID not found");
                DialogBox.alertBox("Error", "Invalid ID, ID not found");
            }
//            loginStatus.setText("Ready");
            Platform.runLater(() -> userId.clear());
        } catch (SQLException se) {
//            loginStatus.setText("Invalid ID, ID not found");
            DialogBox.alertBox("Error", se + "");
            Platform.runLater(() -> userId.clear());
        }
    }
}
