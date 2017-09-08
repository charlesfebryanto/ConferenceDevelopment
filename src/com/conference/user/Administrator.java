package com.conference.user;

import com.conference.DialogBox;
import com.conference.MySQL;
import com.conference.company.Company;
import com.conference.company.Product;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.conference.Conference.loginScene;

//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

public class Administrator extends Member {

    private Connection cn = null;
    private PreparedStatement pst = null;
//    private Statement st = null;
    private ResultSet rs = null;

    private Label staffFirstName, staffLastName, gender, staffContactNo, staffAddress, staffId, dob, position, search,
            companyId,companyName, searchCompany;

    private TextField staffFirstNameField, staffLastNameField, staffContactField, staffIdField, searchField,
            companyIdField, companyNameField, searchCompanyField;

    private TextArea staffAddressField;
    private Button saveStaffButton, editStaffButton, deleteStaffButton, addStaffButton, searchStaffButton,
        saveCompanyButton, editCompanyButton, deleteCompanyButton, addCompanyButton, searchCompanyButton;
    private ToggleGroup genderGroup;
    private RadioButton maleRadio, femaleRadio;
    private DatePicker dobPicker;
    private ComboBox<String> positionBox, searchType, searchCompanyType, companyBox;

    private ObservableList<Member> members;
    private ObservableList<Company> companies;

    private TableView<Member> memberTable, companyStaffTable;
    private TableView<Company> companyTable;
    private TableView<Product> companyProductTable;

    public Administrator(String memberId, String firstName, String lastName, String gender, String contactNumber, String address, Date dob, int position) {
        super(memberId, firstName, lastName, gender, contactNumber, address, dob, position);
    }

    @Override
    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        Menu activity = new Menu("Activity");
        MenuItem productPurchased = new MenuItem("Product Purchased");
        productPurchased.setOnAction(e -> layout.setCenter(productPurchasedView()));
        MenuItem engagementHistory = new MenuItem("Engagement History");
        engagementHistory.setOnAction(e -> layout.setCenter(engagementHistoryView()));
        activity.getItems().addAll(productPurchased, engagementHistory);

        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));
        profile.getItems().addAll(activity, logout);

        Menu view = new Menu("View");
        MenuItem staff = new MenuItem("Staff");
        staff.setOnAction(e -> layout.setCenter(staffView()));
        MenuItem company = new MenuItem("Company");
        company.setOnAction(e -> layout.setCenter(companyView()));
        MenuItem stall = new MenuItem("Lecture");
        MenuItem booth = new MenuItem("Visitor");
        MenuItem lecture = new MenuItem("Lecture");
        MenuItem visitor = new MenuItem("Report?");

        view.getItems().addAll(staff, company, stall, booth, lecture, visitor);

        menuBar.getMenus().addAll(profile, view);

        layout.setCenter(mainView());
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1024, 768);
        stage.setTitle("Login As : " + getFirstName() + " " + getLastName() + " | Administrator");
        stage.setScene(scene);
    }

    private GridPane staffView() {
        getMembers();
        getCompanies();
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
        StringConverter converter = new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd");
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        dobPicker.setConverter(converter);

        GridPane.setConstraints(dobPicker, 1, 5);

        position = new Label("Position : ");
        GridPane.setConstraints(position, 0, 6);
        positionBox = new ComboBox<>();
        positionBox.getItems().addAll("Visitor", "Retailer", "Receptionist", "Administrator");
        positionBox.setPromptText("Select Position");
        positionBox.getSelectionModel().selectedItemProperty().addListener(e -> {
            if(positionBox.getSelectionModel().getSelectedIndex() == 1) {
                companyBox.setDisable(false);
            } else {
                companyBox.setDisable(true);
            }
        });
        GridPane.setConstraints(positionBox, 1, 6);


        Label companyBoxLabel = new Label("Company : ");
        GridPane.setConstraints(companyBoxLabel, 2, 6);
        companyBox = new ComboBox<>();

        for(int i=0; i<companies.size(); i++) {
            companyBox.getItems().add(companies.get(i).getCompanyId() + " - " + companies.get(i).getName());
        }
//        companyBox.getItems().get(i).getName();
//        companyBox.getItems().addAll("Visitor", "Retailer", "Receptionist", "Administrator");
        companyBox.setPromptText("Select Company");
        companyBox.setDisable(true);
        GridPane.setConstraints(companyBox, 3, 6);

        staffId = new Label("ID : ");
        GridPane.setConstraints(staffId, 0, 7);
        staffIdField = new TextField();
        staffIdField.setPromptText("Scan Member ID");
        GridPane.setConstraints(staffIdField, 1, 7);
        staffIdField.textProperty().addListener(e -> {
            if(DialogBox.numberOnly(staffIdField) && staffIdField.getText().length() >= 10) {

            }
        });

        addStaffButton = new Button("Add Member");
        GridPane.setConstraints(addStaffButton, 1, 8);
        addStaffButton.setOnAction(e -> addStaff());

        saveStaffButton = new Button("Save Member");
        GridPane.setConstraints(saveStaffButton, 2, 8);
        saveStaffButton.setOnAction(e -> saveStaff());

        deleteStaffButton = new Button("Delete Member");
        GridPane.setConstraints(deleteStaffButton, 3, 8);
        deleteStaffButton.setOnAction(e -> deleteStaff());
        deleteStaffButton.setDisable(true);

        editStaffButton = new Button("Edit Member");
        GridPane.setConstraints(editStaffButton, 4, 8);
        editStaffButton.setOnAction(e -> editStaff());
        editStaffButton.setDisable(true);

        HBox searchContainer = new HBox();
        searchContainer.setPadding(new Insets(10));
        searchContainer.setSpacing(10);

        search = new Label("Search : ");
        searchField = new TextField();
        searchField.setPromptText("Insert Something");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener(e -> searchStaff());

        searchType = new ComboBox<>();
        searchType.getItems().addAll("memberID", "Name");
        searchType.getSelectionModel().select(1);
        searchStaffButton = new Button("Search");
        searchStaffButton.setOnAction(e -> searchStaff());

        searchContainer.getChildren().addAll(search, searchField, searchType, searchStaffButton);

        GridPane.setConstraints(searchContainer, 0, 9, 4, 1);


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
        memberTable.setItems(members);
        memberTable.getColumns().addAll(staffIdColumn, staffFirstNameColumn, staffLastNameColumn, staffGenderColumn,
                staffContactColumn, staffAddressColumn, staffDobColumn, staffPositionColumn);
        memberTable.setMaxHeight(200);
        memberTable.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
            if ( newValue != null ) {

                staffIdField.setDisable(true);
                saveStaffButton.setDisable(true);
                editStaffButton.setDisable(false);
                deleteStaffButton.setDisable(false);

                staffIdField.setText(memberTable.getSelectionModel().getSelectedItem().getMemberId());
                staffFirstNameField.setText(memberTable.getSelectionModel().getSelectedItem().getFirstName());
                staffLastNameField.setText(memberTable.getSelectionModel().getSelectedItem().getLastName());
                if(memberTable.getSelectionModel().getSelectedItem().getGender().equals("M")) {
                    maleRadio.setSelected(true);
                } else {
                    femaleRadio.setSelected(true);
                }

                staffContactField.setText(memberTable.getSelectionModel().getSelectedItem().getContactNumber());
                staffAddressField.setText(memberTable.getSelectionModel().getSelectedItem().getAddress());
                dobPicker.setValue(memberTable.getSelectionModel().getSelectedItem().getDob().toLocalDate());
                positionBox.getSelectionModel().select(memberTable.getSelectionModel().getSelectedItem().getPosition());

                if(memberTable.getSelectionModel().getSelectedItem().getPosition() == 1) {
                    companyBox.setDisable(false);
                    try {
                        cn = MySQL.connect();
                        String sql = "SELECT work.* " +
                                "FROM member, work, company " +
                                "WHERE (member.memberId = work.memberId AND work.companyId = company.companyId) " +
                                "AND member.memberId = ?";
                        pst = cn.prepareStatement(sql);
                        pst.setString(1, staffIdField.getText());
                        rs = pst.executeQuery();
                        if(rs.next()) {
                            String relatedCompanyId = rs.getString(2);
                            for(int i=0; i<companies.size(); i++) {
                                if(companies.get(i).getCompanyId().equals(relatedCompanyId)) {
                                    companyBox.getSelectionModel().select(i);
                                }
                            }
                        } else {
                            DialogBox.alertBox("Warning", "Retailer does not related to any company");
                            companyBox.getSelectionModel().clearSelection();
                        }
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
                } else {
                    companyBox.setDisable(true);
                    companyBox.getSelectionModel().clearSelection();
                }
            }
        });
        GridPane.setConstraints(memberTable, 0, 10, 5, 1);

        body.getChildren().addAll(staffFirstName, staffLastName, gender, staffContactNo, staffAddress, dob);
        body.getChildren().addAll(staffFirstNameField, staffLastNameField, maleRadio, femaleRadio, staffContactField,
                staffAddressField, dobPicker, staffId, staffIdField, addStaffButton, deleteStaffButton, saveStaffButton, editStaffButton,
                position, positionBox, memberTable, searchContainer, companyBoxLabel, companyBox);

        return body;
    }

    public GridPane companyView() {
        getCompanies();

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        GridPane companyFormContainer = new GridPane();
        companyFormContainer.setVgap(10);
        companyFormContainer.setHgap(10);
//        HBox companyNameContainer = new HBox(10);
        companyName = new Label("Company Name : ");
        companyFormContainer.add(companyName, 0, 0);
//        GridPane.setConstraints(companyName, 0, 0);
        companyNameField = new TextField();
        companyNameField.setPromptText("Insert Company Name");
        companyNameField.textProperty().addListener(e ->
                DialogBox.lengthCheck(companyNameField, 20,
                        "Warning", "Company Name is Too Long"));
        companyFormContainer.add(companyNameField, 1, 0);

//        HBox companyIdContainer = new HBox(10);
        companyId = new Label("Company ID : ");
        companyFormContainer.add(companyId, 0, 1);
        companyIdField = new TextField();
        companyIdField.setPromptText("Insert Company ID");
        companyIdField.textProperty().addListener(e ->
                DialogBox.lengthCheck(companyIdField, 20,
                        "Warning", "Company ID is Too Long"));
        companyFormContainer.add(companyIdField, 1, 1);

        HBox companyButtonContainer = new HBox(10);
        addCompanyButton = new Button("Add Company");
        addCompanyButton.setOnAction(e -> addCompany());

        saveCompanyButton = new Button("Save Company");
        saveCompanyButton.setOnAction(e -> saveCompany());

        deleteCompanyButton = new Button("Delete Company");
        deleteCompanyButton.setOnAction(e -> deleteCompany());
        deleteCompanyButton.setDisable(true);

        editCompanyButton = new Button("Edit Company");
        editCompanyButton.setOnAction(e -> editCompany());
        editCompanyButton.setDisable(true);

        companyButtonContainer.getChildren().addAll(addCompanyButton, saveCompanyButton,
                deleteCompanyButton, editCompanyButton);
        GridPane.setConstraints(companyButtonContainer, 0, 2, 4, 1);

        HBox searchContainer = new HBox();
        searchContainer.setPadding(new Insets(10));
        searchContainer.setSpacing(10);

        searchCompany = new Label("Search : ");
        searchCompanyField = new TextField();
        searchCompanyField.setPromptText("Insert Something");
        searchCompanyField.setPrefWidth(200);
        searchCompanyField.textProperty().addListener(e -> searchCompany());

        searchCompanyType = new ComboBox<>();
        searchCompanyType.getItems().addAll("companyID", "Name");
        searchCompanyType.getSelectionModel().select(1);
        searchCompanyButton = new Button("Search");
        searchCompanyButton.setOnAction(e -> searchCompany());

        searchContainer.getChildren().addAll(searchCompany, searchCompanyField,
                searchCompanyType, searchCompanyButton);

        GridPane.setConstraints(searchContainer, 0, 3, 4, 1);

        GridPane tableContainer = new GridPane();
        tableContainer.setHgap(10);

//        HBox tableContainer = new HBox(10);
        TableColumn<Company, String> companyIdColumn = new TableColumn<>("Company ID");
        companyIdColumn.setCellValueFactory(new PropertyValueFactory<>("companyId"));
        companyIdColumn.setResizable(false);
        companyIdColumn.setPrefWidth(((984.00/100.00)*33.33)/2.00);

        TableColumn<Company, String> companyNameColumn = new TableColumn<>("Name");
        companyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        companyNameColumn.setResizable(false);
        companyNameColumn.setPrefWidth(((984.00/100.00)*33.33)/2.00);

        companyTable = new TableView<>();
        companyTable.getColumns().addAll(companyIdColumn, companyNameColumn);
        companyTable.setItems(companies);
        companyTable.getSelectionModel().selectedItemProperty().addListener(e -> {
            saveCompanyButton.setDisable(true);
            editCompanyButton.setDisable(false);
            deleteCompanyButton.setDisable(false);
            companyIdField.setDisable(true);
            ObservableList<Product> selectedCompanyProducts = companyTable.getSelectionModel().getSelectedItem().getProducts();
            ObservableList<Member> selectedCompanyStaff = companyTable.getSelectionModel().getSelectedItem().getStaff();
            companyIdField.setText(companyTable.getSelectionModel().getSelectedItem().getCompanyId());
            companyNameField.setText(companyTable.getSelectionModel().getSelectedItem().getName());
            companyProductTable.setItems(selectedCompanyProducts);
            companyStaffTable.setItems(selectedCompanyStaff);
        });
        // ((1024 - 20(insets)) / 100) * 30
        companyTable.setMinWidth((984.00/100.00)*33.33);


        TableColumn<Product, String> productIdColumn = new TableColumn<>("Product ID");
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productIdColumn.setPrefWidth(100);

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productNameColumn.setPrefWidth(100);

        TableColumn<Product, Double> productPriceColumn = new TableColumn<>("Price");
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceColumn.setPrefWidth(100);

        TableColumn<Product, Integer> productStockColumn = new TableColumn<>("Stock");
        productStockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productStockColumn.setPrefWidth(100);

        companyProductTable = new TableView<>();
        companyProductTable.getColumns().addAll(productIdColumn, productNameColumn,
                productPriceColumn, productStockColumn);
        companyProductTable.setMinWidth((984.00/100.00)*33.33);

        TableColumn<Member, String> staffIdColumn = new TableColumn<>("Staff ID");
        staffIdColumn.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        staffIdColumn.setMinWidth(100);

        TableColumn<Member, String> staffFirstNameColumn = new TableColumn<>("First Name");
        staffFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        staffFirstNameColumn.setMinWidth(150);

        TableColumn<Member, String> staffLastNameColumn = new TableColumn<>("Last Name");
        staffLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        staffLastNameColumn.setMinWidth(150);

        companyStaffTable = new TableView<>();
        companyStaffTable.getColumns().addAll(staffIdColumn, staffFirstNameColumn, staffLastNameColumn);
        companyStaffTable.setMinWidth((984.00/100.00)*33.33);

        tableContainer.add(companyTable, 0, 0);
        tableContainer.add(companyProductTable, 1, 0);
        tableContainer.add(companyStaffTable, 2, 0);

        GridPane.setConstraints(tableContainer, 0, 4);




        body.getChildren().addAll(companyFormContainer,
                companyButtonContainer, searchContainer, tableContainer);
//                companyTable, companyProductTable, companyStaffTable);
        return body;
    }

    private void saveStaff() {
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
                // back end process
                if(positionBox.getSelectionModel().getSelectedIndex() == 1 &&
                        companyBox.getSelectionModel().getSelectedIndex() < 0) {
                        DialogBox.alertBox("Warning", "Assign Company for Retailer");
                } else {
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

                    if(positionBox.getSelectionModel().getSelectedIndex() == 1) {
                        int companyIndex = companyBox.getSelectionModel().getSelectedIndex();
                        String selectedCompanyId = companies.get(companyIndex).getCompanyId();

                        String sqlWork = "INSERT INTO work " +
                                "VALUES(?,?)";
                        pst = cn.prepareStatement(sqlWork);
                        pst.setString(1, staffIdField.getText());
                        pst.setString(2, selectedCompanyId);
                        pst.executeUpdate();
                    }


                    DialogBox.alertBox("Success", positionBox.getSelectionModel().getSelectedItem() + " " +
                            staffFirstNameField.getText() + " " + staffLastNameField.getText() + " Successfully added.");


                    // front end process
                    Member member = new Member(staffIdField.getText(),
                            staffFirstNameField.getText(),
                            staffLastNameField.getText(),
                            selectedGender,
                            staffContactField.getText(),
                            staffAddressField.getText(),
                            Date.valueOf(dobPicker.getValue()),
                            positionBox.getSelectionModel().getSelectedIndex());


                    memberTable.getItems().add(member);

                    addStaff();

                    memberTable.refresh();
                }
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

    public void searchStaff() {
        // declare local scope observable product
        ObservableList<Member> searchStaff = FXCollections.observableArrayList();
        if(searchType.getSelectionModel().getSelectedItem().equals("Name")){
            String name = searchField.getText().toLowerCase();
            for(int i=0; i<members.size(); i++) {
                if(members.get(i).getFirstName().toLowerCase().contains(name) ||
                        members.get(i).getLastName().toLowerCase().contains(name)) {
                    searchStaff.add(members.get(i));
                }
            }
        } else {
            // search for Id
            String id = searchField.getText().toLowerCase();
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getMemberId().toLowerCase().contains(id)) {
                    searchStaff.add(members.get(i));
                }
            }
        }
        // set product table with item from search product observable
        memberTable.setItems(searchStaff);
    }

    public void addStaff() {
        editStaffButton.setDisable(true);
        deleteStaffButton.setDisable(true);
        saveStaffButton.setDisable(false);
        staffIdField.setDisable(false);
        Platform.runLater(() -> {
            staffFirstNameField.clear();
            staffLastNameField.clear();
            maleRadio.setSelected(true);
            staffContactField.clear();
            staffAddressField.clear();
            positionBox.getSelectionModel().clearSelection();
            companyBox.getSelectionModel().clearSelection();
            dobPicker.getEditor().clear();
            staffIdField.clear();
        });
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

    public void editStaff() {
        if(isStaffFormEmpty()) {
            DialogBox.alertBox("Warning", "Empty Field is not Allowed");
        } else {
            try {
                if(positionBox.getSelectionModel().getSelectedIndex() == 1 &&
                        companyBox.getSelectionModel().getSelectedIndex() < 0) {
                    DialogBox.alertBox("Warning", "Assign Company for Retailer");
                } else {
                    cn = MySQL.connect();
//                String sql = "UPDATE product set name = ?, price = ?, stock = ? WHERE productId = ?";
                    String sql = "UPDATE member " +
                            "SET firstName = ?, lastName = ?, gender = ?, contactNo = ?, " +
                            "address = ?, dob = ?, position = ? " +
                            "WHERE memberId = ?";
                    pst = cn.prepareStatement(sql);
                    pst.setString(1, staffFirstNameField.getText());
                    pst.setString(2, staffLastNameField.getText());
                    String selectedGender = "M";
                    if (genderGroup.getSelectedToggle() == femaleRadio) {
                        selectedGender = "F";
                    }
                    pst.setString(3, selectedGender);
                    pst.setString(4, staffContactField.getText());
                    pst.setString(5, staffAddressField.getText());
                    pst.setDate(6, Date.valueOf(dobPicker.getValue()));
                    pst.setInt(7, positionBox.getSelectionModel().getSelectedIndex());
                    pst.setString(8, staffIdField.getText());
                    pst.executeUpdate();

                    if(positionBox.getSelectionModel().getSelectedIndex() == 1) {
                        int companyIndex = companyBox.getSelectionModel().getSelectedIndex();
                        String selectedCompanyId = companies.get(companyIndex).getCompanyId();

                        String sqlWork = "UPDATE work " +
                                "SET companyId = ? " +
                                "WHERE memberId = ?";
                        pst = cn.prepareStatement(sqlWork);

                        pst.setString(1, selectedCompanyId);
                        pst.setString(2, staffIdField.getText());
                        pst.executeUpdate();
                    }

                    DialogBox.alertBox("Success", "Member " + staffFirstNameField.getText() + " " +
                            staffLastNameField.getText() + " Updated");

                    for (int i = 0; i < memberTable.getItems().size(); i++) {
                        if (memberTable.getItems().get(i).getMemberId().equals(staffIdField.getText())) {
                            memberTable.getItems().get(i).setFirstName(staffFirstNameField.getText());
                            memberTable.getItems().get(i).setLastName(staffLastNameField.getText());
                            memberTable.getItems().get(i).setGender(selectedGender);
                            memberTable.getItems().get(i).setContactNumber(staffContactField.getText());
                            memberTable.getItems().get(i).setAddress(staffAddressField.getText());
                            memberTable.getItems().get(i).setDob(Date.valueOf(dobPicker.getValue()));
                            memberTable.getItems().get(i).setPosition(positionBox.getSelectionModel().getSelectedIndex());
                        }
                    }

                    addStaff();
                    memberTable.refresh();
                }
            } catch(Exception e) {
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
                    // back end process
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

                        DialogBox.alertBox("Success", staffFirstNameField.getText() + " " +
                        staffLastNameField.getText() + " Deleted Successfully");
                        // front end process
                        for (int i=0; i<memberTable.getItems().size(); i++) {
                            if(memberTable.getItems().get(i).getMemberId().equals(staffIdField.getText())) {
                                memberTable.getItems().remove(i);
                            }
                        }

                        // using refresh instead of repopulate using database
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

    public void searchCompany() {
        // declare local scope observable product
        ObservableList<Company> searchCompany = FXCollections.observableArrayList();
        String searchCompanyValue = searchCompanyField.getText().toLowerCase();
        if(searchCompanyType.getSelectionModel().getSelectedItem().equals("Name")){
//            String name = searchCompanyField.getText().toLowerCase();
            for(int i=0; i<companies.size(); i++) {
                if(companies.get(i).getName().toLowerCase().contains(searchCompanyValue)) {
                    searchCompany.add(companies.get(i));
                }
            }
        } else {
            // search for Id
//            String id = searchCompanyField.getText().toLowerCase();
            for(int i=0; i<companies.size(); i++) {
                if(companies.get(i).getCompanyId().toLowerCase().contains(searchCompanyValue)) {
                    searchCompany.add(companies.get(i));
                }
            }
        }
        // set product table with item from search product observable
        companyTable.setItems(searchCompany);
    }

    public void addCompany() {
        saveCompanyButton.setDisable(false);
        editCompanyButton.setDisable(true);
        deleteCompanyButton.setDisable(true);
        companyIdField.setDisable(false);
        Platform.runLater(() -> {
           companyIdField.clear();
           companyNameField.clear();
        });
    }

    public void editCompany() {
        if(isCompanyFormEmpty()) {
            DialogBox.alertBox("Warning", "Empty Field is not Allowed");
        } else {
            try {
                cn = MySQL.connect();
                String sql = "UPDATE company " +
                        "SET name = ? " +
                        "WHERE companyId = ?";
                pst = cn.prepareStatement(sql);
                pst.setString(1, companyNameField.getText());
                pst.setString(2, companyIdField.getText());
                pst.executeUpdate();

                DialogBox.alertBox("Warning", companyNameField.getText() + " Successfully Updated");

                for(int i=0; i<companyTable.getItems().size(); i++) {
                    if(companyTable.getItems().get(i).getCompanyId().equals(companyIdField.getText())) {
                        companyTable.getItems().get(i).setName(companyNameField.getText());
                    }
                }

                addCompany();

                companyTable.refresh();

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
    public void deleteCompany() {
        boolean confirm = DialogBox.confirmationBox("Warning", "Are you sure you want to delete " +
                companyNameField.getText() + " ? Company that already have a record cannot be deleted");
        if(confirm) {
            try {
                cn = MySQL.connect();
                String sql = "DELETE FROM company " +
                        "WHERE companyId = ?";
                pst = cn.prepareStatement(sql);
                pst.setString(1, companyIdField.getText());
                pst.executeUpdate();

                DialogBox.alertBox("Success", companyNameField.getText() + " Successfully Deleted");

                // front end
                for (int i=0; i<companyTable.getItems().size(); i++) {
                    if(companyTable.getItems().get(i).getCompanyId().equals(companyIdField.getText())) {
                        companyTable.getItems().remove(i);
                    }
                }

                addCompany();
                // using refresh instead of repopulate using database
                companyTable.refresh();

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

    public void saveCompany() {
        if(isCompanyFormEmpty()) {
            DialogBox.alertBox("Warning", "Empty Field is not Allowed");
        } else {
            try {
                cn = MySQL.connect();
                String sql = "INSERT INTO company " +
                        "VALUES(?,?)";
                pst = cn.prepareStatement(sql);
                pst.setString(1, companyIdField.getText());
                pst.setString(2, companyNameField.getText());
                pst.executeUpdate();

                DialogBox.alertBox("Success", companyNameField.getText() + " Successfully Added");

                Company company = new Company(companyIdField.getText(), companyNameField.getText());

                companyTable.getItems().add(company);

                addCompany();

                companyTable.refresh();

            } catch(Exception e) {
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

    private boolean isCompanyFormEmpty() {
        if(companyIdField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Company ID is Empty");
            return true;
        } else if(companyNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Company Name is Empty");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ObservableList<Company> getCompanies() {
        companies = FXCollections.observableArrayList();
        try {
            cn = MySQL.connect();
            String sql = "SELECT company.* " +
                    "FROM company " +
                    "ORDER BY company.name ASC";
            pst = cn.prepareStatement(sql);
            rs = pst.executeQuery();
            while(rs.next()) {
                ObservableList<Product> companyProducts = FXCollections.observableArrayList();
                String companyId = rs.getString(1);
                String companyName = rs.getString(2);
                String sqlProduct = "SELECT product.* " +
                        "FROM company, own, product " +
                        "WHERE (company.companyId = own.companyId AND own.productId = product.productId) " +
                        "AND company.companyId = ? " +
                        "ORDER BY product.name ASC";
                pst = cn.prepareStatement(sqlProduct);
                pst.setString(1, companyId);
                ResultSet rsProduct = pst.executeQuery();
                while(rsProduct.next()) {
                    Product product = new Product(rsProduct.getString(1),
                            rsProduct.getString(2),
                            rsProduct.getDouble(3),
                            rsProduct.getInt(4));
                    companyProducts.add(product);
                }

                ObservableList<Member> companyStaff = FXCollections.observableArrayList();
                String sqlStaff = "SELECT member.* " +
                        "FROM member, work, company " +
                        "WHERE (member.memberId = work.memberId AND work.companyId = company.companyId) " +
                        "AND company.companyId = ?";
                pst = cn.prepareStatement(sqlStaff);
                pst.setString(1, companyId);
                ResultSet rsStaff = pst.executeQuery();
                while(rsStaff.next()) {
                    Member member = new Member(rsStaff.getString(1),
                            rsStaff.getString(2),
                            rsStaff.getString(3),
                            rsStaff.getString(4),
                            rsStaff.getString(5),
                            rsStaff.getString(6),
                            rsStaff.getDate(7),
                            rsStaff.getInt(8));
                    companyStaff.add(member);
                }
                Company company = new Company(companyId, companyName, companyProducts, companyStaff);
                companies.add(company);
            }
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
        return companies;
    }

//    private ObservableList<Employee> getEmployees() {
//        employees = FXCollections.observableArrayList();
//
//    }

    private ObservableList<Member> getMembers() {
        members = FXCollections.observableArrayList();
        try {
            cn = MySQL.connect();
            String sql = "SELECT * " +
                    "FROM member " +
                    "WHERE position > 0 " +
                    "ORDER BY position ASC";
            pst = cn.prepareStatement(sql);
            rs = pst.executeQuery();
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
