package com.conference.user;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Date;

import static com.conference.Conference.loginScene;

public class Member {
    private String memberId;



    private String firstName;
    private String lastName;
    private String gender;
    private String contactNumber;
    private String address;
    private Date dob;
    private int position;

    public Member(String memberId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.address = address;
        this.dob = dob;
        this.position = position;
    }

    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));
        profile.getItems().addAll(logout);

        Menu view = new Menu("View");
        MenuItem productPurchased = new MenuItem("Product Purchased");
        productPurchased.setOnAction(e -> System.out.println("prod"));
        MenuItem boothEngagement = new MenuItem("Booth Engagement");
        view.getItems().addAll(productPurchased, boothEngagement);

        menuBar.getMenus().addAll(profile, view);

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding( new Insets(10));

        Label loginId = new Label("ID : " + getMemberId());
        GridPane.setConstraints(loginId, 0,0);
        Label loginName = new Label("Name : " + getFirstName() + " " + getLastName());
        GridPane.setConstraints(loginName, 0,1);
        body.getChildren().addAll(loginId, loginName);

//        Label lbl_username = new Label("Username");
//        GridPane.setConstraints(lbl_username, 0,0);
//        TextField txt_username = new TextField();
//        txt_username.setPromptText("Username");
//        GridPane.setConstraints(txt_username, 1, 0);



        layout.setCenter(body);
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1024, 768);
        stage.setTitle("Login As : Visitor");
        stage.setScene(scene);
    }

    public GridPane mainView() {

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        Label loginId = new Label("ID : " + getMemberId());
        GridPane.setConstraints(loginId, 0, 0);

        Label loginName = new Label("Name : " + getFirstName() + " " + getLastName());
        GridPane.setConstraints(loginName, 0, 1);

        Label loginLevel = new Label("Login Level : " + getPosition());
        GridPane.setConstraints(loginLevel, 0, 2);

//        Label loginDOB = new Label("Login Level : " + getDob());

        body.getChildren().addAll(loginId, loginName, loginLevel);
        return body;
    }

    public void logout(Stage stage, Scene scene) {
        stage.setScene(scene);
    }

    public String getMemberId() {
        return memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public Date getDob() {
        return dob;
    }

    public int getPosition() {
        return position;
    }
}
