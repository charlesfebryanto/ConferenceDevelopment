package com.conference.user;

import com.conference.DialogBox;
import com.conference.MySQL;
import com.conference.Transaction;
import com.conference.company.Product;
import com.sun.xml.internal.bind.v2.model.core.ID;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Observable;

import static com.conference.Conference.loginScene;

public class Member {
    private String memberId;


    private Connection cn = null;
    private PreparedStatement pst = null;
    private ResultSet rs = null;

    private String firstName;
    private String lastName;
    private String gender;
    private String contactNumber;
    private String address;
    private Date dob;
    private int position;

    private Label transactionIdValue, transactionTotalValue, transactionDateValue;
    private ObservableList<Transaction> transactions;
    private TableView<Transaction> transactionTable;
    private TableView<Product> productTable;

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
        getTransactions();

        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        Menu activity = new Menu("Activity");
        MenuItem productPurchased = new MenuItem("Product Purchased");
        productPurchased.setOnAction(e -> layout.setCenter(productPurchasedView()));
        MenuItem engagementHistory = new MenuItem("Engagement History");
        activity.getItems().addAll(productPurchased, engagementHistory);

        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));
        profile.getItems().addAll(activity, logout);

        menuBar.getMenus().addAll(profile);

        layout.setCenter(mainView());
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

    public GridPane productPurchasedView() {

        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        TableColumn<Transaction, String> transactionIdColumn = new TableColumn<>("ID");
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));
        transactionIdColumn.setPrefWidth(1004/3);

        TableColumn<Transaction, Double> transactionTotalColumn = new TableColumn<>("Total");
        transactionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        transactionTotalColumn.setPrefWidth(1004/3);

        TableColumn<Transaction, Date> transactionDateColumn = new TableColumn<>("Date");
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        transactionDateColumn.setPrefWidth(1004/3);

        transactionTable = new TableView<>();
        transactionTable.getColumns().addAll(transactionIdColumn, transactionTotalColumn, transactionDateColumn);
        transactionTable.setItems(transactions);
        transactionTable.getSelectionModel().selectedItemProperty().addListener(e -> {
            showTransactionDetails();
        });
        GridPane.setConstraints(transactionTable, 0, 0, 3, 1);

//        HBox transactionContainer = new HBox(10);
        HBox idContainer = new HBox(10);
        Label transactionIdLabel = new Label("Transaction No. : ");
        transactionIdValue = new Label();
        idContainer.getChildren().addAll(transactionIdLabel, transactionIdValue);
        GridPane.setConstraints(idContainer, 0, 1);

        HBox totalContainer = new HBox(10);
        Label transactionTotalLabel = new Label("Total : ");
        transactionTotalValue = new Label();
        totalContainer.getChildren().addAll(transactionTotalLabel, transactionTotalValue);
        GridPane.setConstraints(totalContainer, 1, 1);

        HBox dateContainer = new HBox(10);
        Label transactionDateLabel = new Label("Date : ");
        transactionDateValue = new Label();
        dateContainer.getChildren().addAll(transactionDateLabel, transactionDateValue);
        GridPane.setConstraints(dateContainer, 2, 1);

//        transactionContainer.getChildren().addAll(transactionIdLabel, transactionIdValue,
//                transactionTotalLabel, transactionTotalValue,
//                transactionDateLabel, transactionDateValue);
//        GridPane.setConstraints(transactionContainer, 0, 1);

        TableColumn<Product, String> productIdColumn = new TableColumn<>("ID");
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productIdColumn.setPrefWidth(1004/4);

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        productNameColumn.setPrefWidth(1004/4);

        TableColumn<Product, Double> productPriceColumn = new TableColumn<>("Price");
        productPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        productPriceColumn.setPrefWidth(1004/4);

        TableColumn<Product, Integer> productQuantityColumn = new TableColumn<>("Quantity");
        productQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productQuantityColumn.setPrefWidth(1004/4);

        productTable = new TableView<>();
        productTable.getColumns().addAll(productIdColumn, productNameColumn, productPriceColumn, productQuantityColumn);

        GridPane.setConstraints(productTable, 0, 2, 3, 1);

        body.getColumnConstraints().add(new ColumnConstraints(1024/3));
        body.getColumnConstraints().add(new ColumnConstraints(1024/3));
        body.getColumnConstraints().add(new ColumnConstraints(1024/3));

        body.getChildren().addAll(transactionTable, idContainer, totalContainer, dateContainer, productTable);
        return body;
    }

    public void showTransactionDetails() {
        int selectedIndex = transactionTable.getSelectionModel().getSelectedIndex();
        ObservableList<Product> selectedTransactionProducts = transactionTable.getItems().get(selectedIndex).getProducts();
        transactionIdValue.setText(transactionTable.getItems().get(selectedIndex).getTransactionId());
        transactionTotalValue.setText(transactionTable.getItems().get(selectedIndex).getTotal() + "");
        transactionDateValue.setText(transactionTable.getItems().get(selectedIndex).getDate() + "");
        ObservableList<Product> products = FXCollections.observableArrayList();
        for(int i=0; i<selectedTransactionProducts.size(); i++) {
            String productId = selectedTransactionProducts.get(i).getProductId();
            String productName = selectedTransactionProducts.get(i).getName();
            double productPrice = selectedTransactionProducts.get(i).getPrice();
            int productQuantity = selectedTransactionProducts.get(i).getStock();

            Product product = new Product(productId, productName, productPrice, productQuantity);

            products.add(product);
        }
        productTable.setItems(products);
        Platform.runLater(() -> transactionTable.getSelectionModel().clearSelection());
    }

    public ObservableList<Transaction> getTransactions() {
        try {
            transactions = FXCollections.observableArrayList();
            cn = MySQL.connect();
            String sqlTransaction = "SELECT t.* " +
                    "FROM transaction t, member, do " +
                    "WHERE (t.transactionId = do.transactionId AND do.memberId = member.memberId) " +
                    "AND member.memberId = ?";
            pst = cn.prepareStatement(sqlTransaction);
            pst.setString(1, getMemberId());
            rs = pst.executeQuery();

            // loop the transaction
            while(rs.next()) {
                String transactionId = rs.getString(1);
                double transactionTotal = rs.getDouble(2);
                Date transactionDate = rs.getDate(3);
                ObservableList<Product> transactionProducts = FXCollections.observableArrayList();
                String sqlProduct = "SELECT product.productId, product.name, product.price, have.quantity " +
                    "FROM transaction, have, product " +
                    "WHERE (transaction.transactionId = have.transactionId AND have.productId = product.productId) " +
                    "AND transaction.transactionId = ?";
                pst = cn.prepareStatement(sqlProduct);
                pst.setString(1, transactionId);
                ResultSet rsProduct = pst.executeQuery();
                // loop the product
                while(rsProduct.next()) {
                    String productId = rsProduct.getString(1);
                    String productName = rsProduct.getString(2);
                    double productPrice = rsProduct.getDouble(3);
                    int quantity = rsProduct.getInt(4);
                    Product product = new Product(productId, productName, productPrice, quantity);
                    transactionProducts.add(product);
                }
                Transaction transaction = new Transaction(transactionId, transactionProducts,transactionTotal, transactionDate);
                transactions.add(transaction);
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
        return transactions;
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
