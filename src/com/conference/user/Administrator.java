package com.conference.user;

import com.conference.DialogBox;
import com.conference.MySQL;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.*;

import static com.conference.Conference.loginScene;

//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

public class Administrator extends Member {

    private Connection cn = null;
    private PreparedStatement pst = null;
    private Statement st = null;
    private ResultSet rs = null;

    private Label staffFirstName, staffLastName, gender, staffContactNo, staffAddress, staffId, dob, position;
    private TextField staffFirstNameField, staffLastNameField, staffContactField, staffIdField;
    private TextArea staffAddressField;
    private ToggleGroup genderGroup;
    private RadioButton maleRadio, femaleRadio;
    private DatePicker dobPicker;
    private ComboBox<String> positionBox;

    private ObservableList<Member> members;

    private TableView<Member> memberTable;

    public Administrator(String memberId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        super(memberId, firstName, lastName, gender, contactNumber, address, dob, position);
    }

    @Override
    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));

        profile.getItems().addAll(logout);

        Menu view = new Menu("View");
        MenuItem staff = new MenuItem("Staff");
        staff.setOnAction(e -> layout.setCenter(staffView()));
        MenuItem product = new MenuItem("Company");
        MenuItem stall = new MenuItem("Lecture");
        MenuItem booth = new MenuItem("Visitor");
        MenuItem lecture = new MenuItem("Lecture");
        MenuItem visitor = new MenuItem("Report?");

        view.getItems().addAll(staff, product, stall, booth, lecture, visitor);

        menuBar.getMenus().addAll(profile, view);

        layout.setCenter(mainView());
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1024, 768);
        stage.setTitle("Login As : Administrator");
        stage.setScene(scene);
    }

    private GridPane staffView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        staffFirstName = new Label("First Name : ");
        GridPane.setConstraints(staffFirstName, 0, 0);
        staffFirstNameField = new TextField();
        staffFirstNameField.setPromptText("Insert First Name");
        GridPane.setConstraints(staffFirstNameField, 1, 0);
        staffFirstNameField.textProperty().addListener(e ->
                DialogBox.stringOnly(staffFirstNameField, 20,
                        "Warning", "First Name is Too Long"));

        staffLastName = new Label("Last Name : ");
        GridPane.setConstraints(staffLastName, 0, 1);
        staffLastNameField = new TextField();
        staffLastNameField.setPromptText("Insert Last Name");
        GridPane.setConstraints(staffLastNameField, 1, 1);
        staffLastNameField.textProperty().addListener(e ->
                DialogBox.stringOnly(staffLastNameField, 20,
                        "Warning", "Last Name is Too Long"));

        gender = new Label("Gender : ");
        GridPane.setConstraints(gender, 0, 2);
        genderGroup = new ToggleGroup();
        maleRadio = new RadioButton("Male");
        GridPane.setConstraints(maleRadio, 1, 2);
        maleRadio.setToggleGroup(genderGroup);
        femaleRadio = new RadioButton("Female");
        GridPane.setConstraints(femaleRadio, 2, 2);
        femaleRadio.setToggleGroup(genderGroup);
        maleRadio.setSelected(true);


        staffContactNo = new Label("Contact Number : ");
        GridPane.setConstraints(staffContactNo, 0, 3);
        staffContactField = new TextField();
        staffContactField.setPromptText("Insert Contact Number");
        GridPane.setConstraints(staffContactField, 1, 3);
        staffContactField.textProperty().addListener(e ->
                DialogBox.numberOnly(staffContactField, 20,
                        "Warning", "Contact is Too Long"));

        staffAddress = new Label("Address : ");
        GridPane.setConstraints(staffAddress, 0, 4);
        staffAddressField = new TextArea();
        staffAddressField.setPromptText("Insert Address");
        staffAddressField.setMaxWidth(300);
        staffAddressField.setMaxHeight(100);
        GridPane.setConstraints(staffAddressField, 1, 4);
        staffAddressField.setWrapText(true);
        staffAddressField.textProperty().addListener(e ->
                DialogBox.lengthCheck(staffAddressField, 80,
                        "Warning", "Address is Too Long"));

        dob = new Label("Date of Birth : ");
        GridPane.setConstraints(dob, 0, 5);
        dobPicker = new DatePicker();
        GridPane.setConstraints(dobPicker, 1, 5);

        position = new Label("Position : ");
        GridPane.setConstraints(position, 0, 6);
        positionBox = new ComboBox<>();
        positionBox.getItems().addAll("Visitor", "Retailer", "Receptionist", "Administrator");
        positionBox.setPromptText("Select Position");
        GridPane.setConstraints(positionBox, 1, 6);


        staffId = new Label("ID : ");
        GridPane.setConstraints(staffId, 0, 7);
        staffIdField = new TextField();
        staffIdField.setPromptText("Scan Member ID");
        GridPane.setConstraints(staffIdField, 1, 7);
        staffIdField.textProperty().addListener(e -> {
            if(DialogBox.numberOnly(staffIdField) && staffIdField.getText().length() >= 10) {
                insertStaff();
            }
        });

        Button addStaff = new Button("Add Member");
        GridPane.setConstraints(addStaff, 1, 8);
        addStaff.setOnAction(e -> insertStaff());

        Button deleteStaff = new Button("Delete Member");
        GridPane.setConstraints(deleteStaff, 2, 8);
        deleteStaff.setOnAction(e -> deleteStaff());

        TableColumn<Member, String> staffIdColumn = new TableColumn<>("ID");
        staffIdColumn.setMinWidth(200);
        staffIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));

        TableColumn<Member, String> staffFirstNameColumn = new TableColumn<>("First Name");
        staffFirstNameColumn.setMinWidth(200);
        staffFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Member, String> staffLastNameColumn = new TableColumn<>("Last Name");
        staffLastNameColumn.setMinWidth(200);
        staffLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Member, Character> staffGenderColumn = new TableColumn<>("Gender");
        staffGenderColumn.setMinWidth(200);
        staffGenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Member, Integer> staffContactColumn = new TableColumn<>("Contact");
        staffContactColumn.setMinWidth(200);
        staffContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        TableColumn<Member, String> staffAddressColumn = new TableColumn<>("Address");
        staffAddressColumn.setMinWidth(200);
        staffAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Member, Date> staffDobColumn = new TableColumn<>("Date of Birth");
        staffDobColumn.setMinWidth(200);
        staffDobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));

        TableColumn<Member, Integer> staffPositionColumn = new TableColumn<>("Position");
        staffPositionColumn.setMinWidth(200);
        staffPositionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        memberTable = new TableView<>();
        memberTable.setItems(getMembers());
        memberTable.getColumns().addAll(staffIdColumn, staffFirstNameColumn, staffLastNameColumn, staffGenderColumn,
                staffContactColumn, staffAddressColumn, staffDobColumn, staffPositionColumn);
        memberTable.setMaxHeight(200);
        memberTable.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
            if ( newValue != null ) {
                // put listener action here
            }
        });
        GridPane.setConstraints(memberTable, 0, 9, 5, 1);

        body.getChildren().addAll(staffFirstName, staffLastName, gender, staffContactNo, staffAddress, dob);
        body.getChildren().addAll(staffFirstNameField, staffLastNameField, maleRadio, femaleRadio, staffContactField,
                staffAddressField, dobPicker, staffId, staffIdField, addStaff, deleteStaff,
                position, positionBox, memberTable);

        return body;
    }

    private void insertStaff() {
        if (isStaffFormEmpty()) {
            DialogBox.alertBox("Warning", "No Empty Value is Allowed");
        } else {
            String selectedGender;
            if (genderGroup.getSelectedToggle() == maleRadio) {
                selectedGender = maleRadio.getText().charAt(0) + "";
            } else {
                selectedGender = femaleRadio.getText().charAt(0) + "";
            }
            try {
                cn = MySQL.connect();
                String sql = "INSERT INTO member VALUES(?,?,?,?,?,?,?,?)";
                pst = cn.prepareStatement(sql);
                pst.setString(1, staffIdField.getText());
                pst.setString(2, staffFirstNameField.getText());
                pst.setString(3, staffLastNameField.getText());
                pst.setString(4, selectedGender);
                pst.setString(5, staffContactField.getText());
                pst.setString(6, staffAddressField.getText());
                pst.setDate(7, Date.valueOf(dobPicker.getValue()));
                pst.setInt(8, positionBox.getSelectionModel().getSelectedIndex());
                pst.executeUpdate();
                DialogBox.alertBox("Success", positionBox.getSelectionModel().getSelectedItem() + " " +
                        staffFirstNameField.getText() + " " + staffLastNameField.getText() + " Successfuly added.");
                memberTable.setItems(getMembers());
                Platform.runLater(() -> {
                    staffFirstNameField.clear();
                    staffLastNameField.clear();
                    maleRadio.setSelected(true);
                    staffContactField.clear();
                    staffAddressField.clear();
                    positionBox.getSelectionModel().clearSelection();
                    dobPicker.getEditor().clear();
                    staffIdField.clear();
                });
            } catch (MySQLIntegrityConstraintViolationException e) {
                if (e.getErrorCode() == 1062) {
                    DialogBox.alertBox("Error", staffIdField.getText() + " Already Registered.");
                    Platform.runLater(() -> staffIdField.clear());
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "");
            } finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "rs");
                }
                try {
                    if (st != null) {
                        st.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if (pst != null) {
                        pst.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "st");
                }
                try {
                    if (cn != null) {
                        cn.close();
                    }
                } catch (Exception e) {
                    DialogBox.alertBox("Error", e + "cn");
                }
            }

        }
    }

    public boolean isStaffFormEmpty() {
        if (staffFirstNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "First Name is Empty");
            return true;
        } else if (staffLastNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Last Name is Empty");
            return true;
        } else if (staffContactField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Contact is Empty");
            return true;
        } else if (staffAddressField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Address is Empty");
            return true;
        } else if (dobPicker.getEditor().getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Date is Empty");
            return true;
        } else if(positionBox.getSelectionModel().getSelectedIndex() == -1) {
            DialogBox.alertBox("Warning", "Position is not Selected");
            return true;
        } else if (staffIdField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "ID is Empty");
            return true;
        } else if (staffIdField.getText().length() < 10) {
            DialogBox.alertBox("Warning", "ID is Too Short");
            return true;
        } else {
            return false;
        }
    }

    public void deleteStaff() {
        if(staffIdField.getText().isEmpty()) {
            // this will never happen
            DialogBox.alertBox("Warning", "ID is needed to Delete, cannot be empty");
        } else {
            boolean confirm = DialogBox.confirmationBox("Warning", "Are you sure you want to delete " +
                    staffFirstNameField.getText() + " " + staffLastNameField.getText() + " ? " +
                    "It is not recommended to remove record");
            if (confirm) {
                try {
                    cn = MySQL.connect();
                    String sqlSelect = "SELECT * " +
                            "FROM member " +
                            "WHERE memberId = ? AND position > 0";
                    pst = cn.prepareStatement(sqlSelect);
                    pst.setString(1, staffIdField.getText());
                    rs = pst.executeQuery();
                    if(rs.next()) {
                        String sqlDelete = "DELETE FROM member WHERE memberId = ?";
                        pst = cn.prepareStatement(sqlDelete);
                        pst.setString(1, staffIdField.getText());
                        pst.executeUpdate();

                        for (int i=0; i<members.size(); i++) {
                            if(members.get(i).getMemberId().equals(staffIdField.getText())) {
                                members.remove(i);
                            }
                        }
                        
                        memberTable.refresh();
                    } else {
                        DialogBox.alertBox("Warning", "Delete Fail. " + staffIdField.getText() +
                                " Not a Staff");
                    }
                } catch (SQLException e) {
                    DialogBox.alertBox("Warning", e.getErrorCode() + " :" + e.getMessage());
                } catch (Exception e) {
                    DialogBox.alertBox("Warning", e + "");
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (Exception e) {
                        DialogBox.alertBox("Error", e + "rs");
                    }
                    try {
                        if (st != null) {
                            st.close();
                        }
                    } catch (Exception e) {
                        DialogBox.alertBox("Error", e + "st");
                    }
                    try {
                        if (pst != null) {
                            pst.close();
                        }
                    } catch (Exception e) {
                        DialogBox.alertBox("Error", e + "st");
                    }
                    try {
                        if (cn != null) {
                            cn.close();
                        }
                    } catch (Exception e) {
                        DialogBox.alertBox("Error", e + "cn");
                    }
                }
            }
        }
    }

    private ObservableList<Member> getMembers() {
        members = FXCollections.observableArrayList();
        try {
            cn = MySQL.connect();
            String sql = "SELECT * " +
                    "FROM member " +
                    "WHERE position > 0 " +
                    "ORDER BY position ASC";
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while(rs.next()) {
                members.add(new Member(rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getDate(7),
                        rs.getInt(8)));
            }
        } catch (Exception e) {
            DialogBox.alertBox("Warning", e + "");
        } finally {
            try {
                if(rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "rs");
            }
            try {
                if(st != null) {
                    st.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(pst != null) {
                    pst.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "st");
            }
            try {
                if(cn != null) {
                    cn.close();
                }
            } catch (Exception e) {
                DialogBox.alertBox("Error", e + "cn");
            }
        }
        return members;
    }
}
