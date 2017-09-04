package com.conference.user;

import com.conference.DialogBox;
import com.conference.MySQL;
import com.conference.company.Company;
import com.conference.company.Product;
import javafx.application.Platform;
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

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.conference.Conference.loginScene;
//import java.util.Date;

public class Retailer extends Member {
    private Connection cn = null;
    private PreparedStatement pst = null;
    private Statement st = null;
    private ResultSet rs = null;

    private Company company;
    private TableView<Product> productTable, sellingTable;
    private ObservableList<Product> products, sellProducts;
    private ComboBox<String> searchType;
    private Label transactionDate, companyId, companyName, total, totalValue,
            productId, productName, price, stock, search, scanProduct, scanId;
    private TextField productIdField, productNameField, priceField, stockField, searchField,
            scanProductField, scanIdField;
    private Button addProductButton, saveProductButton, editProductButton, deleteProductButton, searchProductButton,
            scanProductButton, deleteSellButton, substractSellButton, transactionButton;

    private double totalCount;

    public Retailer(String memberId, String firstName, String lastName, String gender,
                    String contactNumber, String address, Date dob, int position) {
        super(memberId, firstName, lastName, gender, contactNumber, address, dob, position);
    }


    public void view(Stage stage) {
        BorderPane layout = new BorderPane();

        MenuBar menuBar = new MenuBar();

        Menu profile = new Menu("Profile");
//        MenuItem edit = new MenuItem("Edit");
        MenuItem logout = new MenuItem("Log out");
        logout.setOnAction(e -> logout(stage, loginScene));

        profile.getItems().addAll(logout);

        Menu manageProduct = new Menu("Products");
        MenuItem sellProduct = new MenuItem("Sell Products");
        sellProduct.setOnAction(e -> layout.setCenter(sellProductView()));

        MenuItem viewProduct = new MenuItem("View Products");
        viewProduct.setOnAction(e -> layout.setCenter(viewProductView()));

        manageProduct.getItems().addAll(sellProduct, viewProduct);


        menuBar.getMenus().addAll(profile, manageProduct);


        layout.setCenter(mainView());
        layout.setTop(menuBar);

        Scene scene = new Scene(layout, 1024, 768);
        stage.setTitle("Login As : Retailer | Company : " + company.getCompanyId());
        stage.setScene(scene);
    }

    @Override
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

        try {
            cn = MySQL.connect();
            String sql = "SELECT company.companyId, company.name FROM member,company,work WHERE member.memberId = work.memberId AND work.companyId = company.companyId AND member.memberId = " + getMemberId();
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            if(rs.next()) {
                companyId = new Label("Company ID : " + rs.getString(1));
                companyName = new Label("Company Name : " + rs.getString(2));
                company = new Company(rs.getString(1), rs.getString(2));
            } else {
                companyId = new Label("Company ID : ");
                companyName = new Label("Company Name : ");
            }
            GridPane.setConstraints(companyId, 0, 3);
            GridPane.setConstraints(companyName, 0, 4);
        } catch(Exception e) {
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

        body.getChildren().addAll(loginId, loginName, loginLevel, companyId, companyName);

        return body;
    }

    private GridPane sellProductView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        transactionDate = new Label();
        GridPane.setConstraints(transactionDate, 4, 0);
        transactionDate.setText(LocalDate.now() + "");

        scanProduct = new Label("Scan Product : ");
        GridPane.setConstraints(scanProduct, 0, 0);

        scanProductField = new TextField();
        scanProductField.setPromptText("Scan Product ID");
        GridPane.setConstraints(scanProductField, 1, 0);

        scanProductButton = new Button("Add");
        GridPane.setConstraints(scanProductButton, 2, 0);
        scanProductButton.setOnAction(e -> addSelling());

        substractSellButton = new Button("Substract");
        GridPane.setConstraints(substractSellButton, 3, 0);
        substractSellButton.setDisable(true);
        substractSellButton.setOnAction(e -> substractSelling());

        scanId = new Label("Scan ID : ");
        GridPane.setConstraints(scanId, 0, 1);

        scanIdField = new TextField();
        scanIdField.setPromptText("Scan Customer ID");
        GridPane.setConstraints(scanIdField, 1, 1);
        scanIdField.textProperty().addListener(e -> {
            if (scanIdField.getText().length() >= 7) {
                addTransaction();
                Platform.runLater(() -> {
                    scanProductField.clear();
                    scanIdField.clear();
                    sellingTable.getItems().remove(0, sellingTable.getItems().size());
                });
                totalCount = 0;
                totalValue.setText(totalCount + "");
                transactionButton.setDisable(true);
            }
        });

        transactionButton = new Button("Finish Transaction");
        transactionButton.setDisable(true);
        transactionButton.setOnAction(e -> addTransaction());
        GridPane.setConstraints(transactionButton, 2, 1);
//        scanIdButton = new Button("Add");
//        GridPane.setConstraints(scanProductButton, 2, 0);

        deleteSellButton = new Button("Delete");
        GridPane.setConstraints(deleteSellButton, 0, 2);
        deleteSellButton.setOnAction(e -> deleteSelling());
        deleteSellButton.setDisable(true);


        TableColumn<Product, String> sellingIdColumn = new TableColumn<>("ID");
        sellingIdColumn.setMinWidth(200);
        sellingIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> sellingNameColumn = new TableColumn<>("Name");
        sellingNameColumn.setMinWidth(200);
        sellingNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));


        TableColumn<Product, Double> sellingPriceColumn = new TableColumn<>("Price");
        sellingPriceColumn.setMinWidth(100);
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setMinWidth(100);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        TableColumn<Product, String> productIdColumn = new TableColumn<>("ID");
        productIdColumn.setMinWidth(200);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setMinWidth(200);
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setMinWidth(100);
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        sellingTable = new TableView<>();
        sellProducts = FXCollections.observableArrayList();
        sellingTable.setItems(sellProducts);
        sellingTable.getColumns().addAll(sellingIdColumn, sellingNameColumn, sellingPriceColumn, quantityColumn);
        sellingTable.setMaxHeight(200);
        sellingTable.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
            if(newValue != null) {
                scanProductField.setText(sellingTable.getSelectionModel().getSelectedItem().getProductId());
                substractSellButton.setDisable(false);
                deleteSellButton.setDisable(false);
                productTable.getSelectionModel().clearSelection();
            }
        });

        GridPane.setConstraints(sellingTable, 0, 3, 4, 1);

        total = new Label("Total : ");
        GridPane.setConstraints(total, 5, 3);

        totalValue = new Label();
        totalCount = 0.00;
        totalValue.setText(totalCount + "");
        GridPane.setConstraints(totalValue, 6,3);

        HBox searchContainer = new HBox();
        searchContainer.setPadding(new Insets(10));
        searchContainer.setSpacing(10);

        search = new Label("Search : ");
        searchField = new TextField();
        searchField.setPromptText("Insert Something");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener(e -> searchProduct());

        searchType = new ComboBox<>();
        searchType.getItems().addAll("ProductID", "Name");
        searchType.getSelectionModel().select(1);
        searchProductButton = new Button("Search");
        searchProductButton.setOnAction(e -> searchProduct());

        searchContainer.getChildren().addAll(search, searchField, searchType, searchProductButton);

        GridPane.setConstraints(searchContainer, 0, 4, 4, 1);

        productTable = new TableView<>();
        productTable.setItems(getProducts());
        productTable.getColumns().addAll(productIdColumn, productNameColumn, priceColumn, stockColumn);
        productTable.setMaxHeight(200);
        productTable.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
                    // if the newValue not null -> get the value and set to the field
                    // if the newValue is null -> do nothing, dont get the value from the previous selected
                    // otherwise it will give the null pointer exception
                    if (newValue != null) {
                        scanProductField.setText(productTable.getSelectionModel().getSelectedItem().getProductId());
//                productIdField.setDisable(true);
//                productNameField.setText(productTable.getSelectionModel().getSelectedItem().getName());
//                priceField.setText(productTable.getSelectionModel().getSelectedItem().getPrice() + "");
//                stockField.setText(productTable.getSelectionModel().getSelectedItem().getStock() + "");
//                editProductButton.setDisable(false);
//                deleteProductButton.setDisable(false);
                        substractSellButton.setDisable(true);
                        deleteSellButton.setDisable(true);
                        sellingTable.getSelectionModel().clearSelection();
                    }
                });
        GridPane.setConstraints(productTable, 0, 5, 4, 1);


        body.getChildren().addAll(scanProduct, transactionDate, scanProductField, scanProductButton,
                scanId, scanIdField, total, totalValue, deleteSellButton,
                searchContainer,
                sellingTable, productTable, substractSellButton, transactionButton);

        return body;
    }

    private GridPane viewProductView() {
        GridPane body = new GridPane();
        body.setVgap(10);
        body.setHgap(10);
        body.setPadding(new Insets(10));

        productName = new Label("Product Name : ");
        GridPane.setConstraints(productName, 0,0);
        productNameField = new TextField();
        productNameField.setPromptText("Insert Product Name");
        GridPane.setConstraints(productNameField, 1, 0);
        productNameField.textProperty().addListener(e -> DialogBox.lengthCheck(productNameField,
                20, "Warning", "Product Name is Too Long"));

        price = new Label("Price : ");
        GridPane.setConstraints(price, 0, 1);
        priceField = new TextField();
        priceField.setPromptText("Insert Price");
        GridPane.setConstraints(priceField, 1, 1);
        priceField.textProperty().addListener(e -> DialogBox.numberOnly(priceField));

        stock = new Label("Stock : ");
        GridPane.setConstraints(stock, 0, 2);
        stockField = new TextField();
        stockField.setPromptText("Insert Stock");
        GridPane.setConstraints(stockField, 1, 2);
        stockField.textProperty().addListener(e -> DialogBox.numberOnly(stockField));

        productId = new Label("Product ID : ");
        GridPane.setConstraints(productId, 0, 3);
        productIdField = new TextField();
        productIdField.setPromptText("Insert Product ID");
        GridPane.setConstraints(productIdField, 1, 3);
        productIdField.textProperty().addListener(e -> DialogBox.lengthCheck(productIdField, 20,
                "Warning", "Product ID is Too Long"));

        addProductButton = new Button("Add Product");
        GridPane.setConstraints(addProductButton, 1, 4);
        addProductButton.setOnAction(e -> addProduct());
        addProductButton.setPrefWidth(150);

        saveProductButton = new Button("Save Product");
        GridPane.setConstraints(saveProductButton, 2, 4);
        saveProductButton.setOnAction(e -> saveProduct());
        saveProductButton.setPrefWidth(150);

        editProductButton = new Button("Edit Product");
        GridPane.setConstraints(editProductButton, 3, 4);
        editProductButton.setPrefWidth(150);
        editProductButton.setOnAction(e -> editProduct());
        editProductButton.setDisable(true);

        deleteProductButton = new Button("Delete Product");
        GridPane.setConstraints(deleteProductButton, 4, 4);
        deleteProductButton.setOnAction(e -> deleteProduct());
        deleteProductButton.setPrefWidth(150);
        deleteProductButton.setDisable(true);

        HBox searchContainer = new HBox();
        searchContainer.setPadding(new Insets(10));
        searchContainer.setSpacing(10);

        search = new Label("Search : ");
        searchField = new TextField();
        searchField.setPromptText("Insert Something");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener(e -> searchProduct());

        searchType = new ComboBox<>();
        searchType.getItems().addAll("ProductID", "Name");
        searchType.getSelectionModel().select(1);
        searchProductButton = new Button("Search");
        searchProductButton.setOnAction(e -> searchProduct());

        searchContainer.getChildren().addAll(search, searchField, searchType, searchProductButton);

        GridPane.setConstraints(searchContainer, 0, 5, 4, 1);

        TableColumn<Product, String> productIdColumn = new TableColumn<>("ID");
        productIdColumn.setMinWidth(200);
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> productNameColumn = new TableColumn<>("Name");
        productNameColumn.setMinWidth(200);
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
        stockColumn.setMinWidth(100);
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));


        productTable = new TableView<>();
        productTable.setItems(getProducts());
        productTable.getSelectionModel().selectedItemProperty().addListener((value, oldValue, newValue) -> {
            // if the newValue not null -> get the value and set to the field
                // if the newValue is null -> do nothing, dont get the value from the previous selected
                // otherwise it will give the null pointer exception
            if(newValue != null) {
                productIdField.setText(productTable.getSelectionModel().getSelectedItem().getProductId());
                productIdField.setDisable(true);
                productNameField.setText(productTable.getSelectionModel().getSelectedItem().getName());
                priceField.setText(productTable.getSelectionModel().getSelectedItem().getPrice() + "");
                stockField.setText(productTable.getSelectionModel().getSelectedItem().getStock() + "");
                editProductButton.setDisable(false);
                deleteProductButton.setDisable(false);
            }
        });

        productTable.getColumns().addAll(productIdColumn, productNameColumn, priceColumn, stockColumn);

        GridPane.setConstraints(productTable, 0, 6, 4, 1);

        body.getChildren().addAll(productName, price, stock, productId, addProductButton, saveProductButton,
                editProductButton, deleteProductButton,
                searchContainer,
                productTable);
        body.getChildren().addAll(productNameField, priceField, stockField, productIdField);

//        body.getColumnConstraints().add(new ColumnConstraints(150));
//        body.getColumnConstraints().add(new ColumnConstraints(150));
//        body.getColumnConstraints().add(new ColumnConstraints(150));
        return body;
    }

    public void addTransaction() {
        if (sellingTable.getItems().size() <= 0) {
            DialogBox.alertBox("Warning", "No Item found");
        } else {
            boolean confirm = DialogBox.confirmationBox("Warning", "Finish the Transaction ?");
            if(confirm) {

                long ms = 0;
                try {
                    String myDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss"));
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                    java.util.Date date = sdf.parse(myDate);
                    ms = date.getTime();
                } catch (Exception e) {
                    DialogBox.alertBox("Warning", e + "");
                }
                try {
                    cn = MySQL.connect();

                    String sqlCheckId = "SELECT * FROM member WHERE memberId = ?";
                    pst = cn.prepareStatement(sqlCheckId);
                    pst.setString(1, scanIdField.getText());
                    rs = pst.executeQuery();

                    if (rs.next()) {
                        String sqlTransaction = "INSERT INTO transaction VALUES(?,?,?)";
                        pst = cn.prepareStatement(sqlTransaction);
                        pst.setString(1, ms + "");
                        pst.setString(2, totalValue.getText());
                        pst.setDate(3, Date.valueOf(transactionDate.getText()));
                        pst.executeUpdate();

                        String sqlDo = "INSERT INTO do VALUES(?,?)";
                        pst = cn.prepareStatement(sqlDo);
                        pst.setString(1, ms + "");
                        pst.setString(2, scanIdField.getText());
                        pst.executeUpdate();

                        for (int i = 0; i < sellingTable.getItems().size(); i++) {
                            String productId = sellingTable.getItems().get(i).getProductId();
                            int quantity = sellingTable.getItems().get(i).getStock();
                            String sqlSelling = "INSERT INTO have VALUES(?,?,?)";
                            pst = cn.prepareStatement(sqlSelling);
                            pst.setString(1, ms + "");
                            pst.setString(2, productId);
                            pst.setInt(3, quantity);
                            pst.executeUpdate();

                            String sqlUpdateProduct = "UPDATE product SET stock=stock-? WHERE productId = ?";
                            pst = cn.prepareStatement(sqlUpdateProduct);
                            pst.setInt(1, quantity);
                            pst.setString(2, productId);
                            pst.executeUpdate();
                        }
                        productTable.setItems(getProducts());
                        DialogBox.alertBox("Success", "Transaction Complete");
                    } else {
                        DialogBox.alertBox("Warning", "ID not found");
                    }
                } catch (SQLException e) {
                    DialogBox.alertBox("Warning", e.getErrorCode() + " : " + e.getMessage());
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

    private void countTotal() {
        totalCount = 0.00;
        for(int i=0; i<sellingTable.getItems().size(); i++) {
            double subTotal = (sellingTable.getItems().get(i).getPrice() * sellingTable.getItems().get(i).getStock());
            totalCount += subTotal;
        }
        totalValue.setText(totalCount + "");
    }

    public void deleteSelling() {
        int index = sellingTable.getSelectionModel().getSelectedIndex();
        for(int i=0; i<productTable.getItems().size(); i++) {
            if(productTable.getItems().get(i).getProductId().equals(sellingTable.getItems().get(index).getProductId())) {
                productTable.getItems().get(i).setStock(productTable.getItems().get(i).getStock() + sellingTable.getItems().get(index).getStock());
            }
        }
        sellingTable.getItems().remove(index);
        if(sellingTable.getItems().size() <= 0) {
            transactionButton.setDisable(true);
        }
        countTotal();
        productTable.refresh();
        deleteSellButton.setDisable(true);
        substractSellButton.setDisable(true);
        sellingTable.getSelectionModel().clearSelection();
    }

    public void substractSelling() {
        int index = sellingTable.getSelectionModel().getSelectedIndex();
        for(int i=0; i<productTable.getItems().size(); i++) {
            if(productTable.getItems().get(i).getProductId().equals(sellingTable.getItems().get(index).getProductId())) {
                productTable.getItems().get(i).setStock(productTable.getItems().get(i).getStock() + 1);
            }
        }
        sellingTable.getItems().get(index).setStock(sellingTable.getItems().get(index).getStock() - 1);
        if(sellingTable.getItems().get(index).getStock() <= 0) {
            sellingTable.getItems().remove(index);
            if(sellingTable.getItems().size() <= 0) {
                transactionButton.setDisable(true);
            }
            deleteSellButton.setDisable(true);
            substractSellButton.setDisable(true);
            sellingTable.getSelectionModel().clearSelection();
        }
        countTotal();
        productTable.refresh();
        sellingTable.refresh();
    }

    public void addSelling() {
        try {
            cn = MySQL.connect();
            String sql = "SELECT product.productId, product.name, product.price " +
                    "FROM product, company, own " +
                    "WHERE (product.productId = own.productId " +
                    "AND own.companyId = company.companyId) " +
                    "AND (product.productId = ? " +
                    "AND company.companyId = ?)";
            pst = cn.prepareStatement(sql);
            pst.setString(1, scanProductField.getText());
            pst.setString(2, company.getCompanyId());
            rs = pst.executeQuery();
            if(rs.next()) {
                Product product = new Product(rs.getString(1), rs.getString(2),
                        rs.getDouble(3), 1);
                boolean scanned = false;
                int scannedIndex = 0;
                boolean noStock = false;
                int productIndex = 0;
                for (int i = 0; i < sellProducts.size(); i++) {
                    // check if the newly get product object equally the same with the one on the selling view
                    if (sellProducts.get(i).getProductId().equals(product.getProductId())) {
                        scanned = true;
                        scannedIndex = i;
                    }
                }

                for(int i = 0; i < products.size(); i++) {
                    // decrease the product Id that have the same value with the newly created object
                    if(products.get(i).getProductId().equals(product.getProductId())) {
                        // only decrement when stock is more than 0
//                        if(products.get(i).getStock() > 0) {
//                            products.get(i).setStock(products.get(i).getStock() - 1);
//                        }
                        productIndex = i;
                    }
                }

                // scanned and have a stock
                // else -> not scanned /
                if (scanned && products.get(productIndex).getStock() > 0) {
                        sellProducts.get(scannedIndex).setStock(sellProducts.get(scannedIndex).getStock() + 1);
                        products.get(productIndex).setStock(products.get(productIndex).getStock() - 1);

                } else if (!scanned && products.get(productIndex).getStock() > 0) {

                        sellProducts.add(product);
                        products.get(productIndex).setStock(products.get(productIndex).getStock() - 1);
                } else {
                    DialogBox.alertBox("Warning", "Product is Empty");
                }

            } else {
                DialogBox.alertBox("Warning", "No Product Found");
            }
            countTotal();
            productTable.refresh();
            sellingTable.refresh();
            // enable transaction button
            transactionButton.setDisable(false);
            // if selling table is empty
            if(sellingTable.getItems().size() <= 0) {
                // disable transaction button
                transactionButton.setDisable(true);
            }
        } catch (SQLException e) {
            DialogBox.alertBox("Warning", e.getErrorCode() + " : " + e.getMessage());
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

    public void addProduct() {
        editProductButton.setDisable(true);
        deleteProductButton.setDisable(true);
        productIdField.setDisable(false);
        productIdField.clear();
        productNameField.clear();
        priceField.clear();
        stockField.clear();
    }

    public void editProduct() {
        if(isEmpty()) {
            DialogBox.alertBox("Warning", "Empty Field is not Allowed");
        } else {
            boolean confirm = DialogBox.confirmationBox("Warning", "Are you sure you want to edit " +
                    productNameField.getText() + " ? It will affect the related product");
            if(confirm) {
                try {
                    cn = MySQL.connect();
                    String sql = "UPDATE product set name = ?, price = ?, stock = ? WHERE productId = ?";
                    pst = cn.prepareStatement(sql);
                    pst.setString(1, productNameField.getText());
                    pst.setDouble(2, Double.parseDouble(priceField.getText()));
                    pst.setInt(3, Integer.parseInt(stockField.getText()));
                    pst.setString(4, productIdField.getText());
                    pst.executeUpdate();
                    DialogBox.alertBox("Success", "Product " + productIdField.getText() + " Updated");
                    addProduct();
                    productTable.setItems(getProducts());
                } catch (SQLException e) {
                    DialogBox.alertBox("Warning", e.getErrorCode() + " : " + e.getMessage());
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

    public void deleteProduct() {
        if(productIdField.getText().isEmpty()) {
            // this will never happen
            DialogBox.alertBox("Warning", "ID is needed to Delete, cannot be empty");
        } else {
            boolean confirm = DialogBox.confirmationBox("Warning", "Are you sure you want to delete " +
                    productNameField.getText() + " ? Product that already have a record cannot be deleted");
            if (confirm) {
                try {
                    cn = MySQL.connect();
                    String sqlCount = "SELECT COUNT(have.productId) FROM have WHERE have.productId = ?";
                    pst = cn.prepareStatement(sqlCount);
                    pst.setString(1, productIdField.getText());
                    rs = pst.executeQuery();

                    if (rs.next()) {
                        if (rs.getInt(1) > 0) {
                            DialogBox.alertBox("Warning", "Cannot delete " + productNameField.getText() +
                                    ", Product already have a record.");
                        } else {

                            String sqlOwn = "DELETE FROM own WHERE productId = ? AND companyId = ?";
                            pst = cn.prepareStatement(sqlOwn);
                            pst.setString(1, productIdField.getText());
                            pst.setString(2, company.getCompanyId());
                            pst.executeUpdate();

                            String sqlProduct = "DELETE FROM product WHERE productId = ?";
                            pst = cn.prepareStatement(sqlProduct);
                            pst.setString(1, productIdField.getText());
                            pst.executeUpdate();

                            // set the items on productTable view using getProducts method to get the current data from database
                            DialogBox.alertBox("Success", productNameField.getText() + " Deleted Successfully");
                            addProduct();
                            productTable.refresh();
                            productTable.setItems(getProducts());
                        }
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

    public void searchProduct() {
        ObservableList<Product> searchProduct = FXCollections.observableArrayList();
        if(searchType.getSelectionModel().getSelectedItem().equals("Name")){
            String name = searchField.getText().toLowerCase();
            for(int i=0; i<products.size(); i++) {
                if(products.get(i).getName().toLowerCase().contains(name)) {
                    searchProduct.add(products.get(i));
                }
            }
        } else {
            String id = searchField.getText().toLowerCase();
            for(int i=0; i<products.size(); i++) {
                if(products.get(i).getProductId().toLowerCase().contains(id)) {
                    searchProduct.add(products.get(i));
                }
            }
        }
        productTable.setItems(searchProduct);
    }

    // Database search method, no longer needed, too heavy
//    public void searchProduct() {
//        products = FXCollections.observableArrayList();
//        try {
//            String sql;
//            cn = MySqlConnect.connectDB();
//            if(searchType.getSelectionModel().getSelectedItem() == "Name") {
//                sql = "SELECT * " +
//                        "FROM product, company, own " +
//                        "WHERE product.productId = own.productId " +
//                        "AND own.companyId = company.companyId " +
//                        "AND company.companyId = ? " +
//                        "AND product.name LIKE ?";
//            } else {
//                sql = "SELECT * " +
//                        "FROM product, company, own " +
//                        "WHERE product.productId = own.productId " +
//                        "AND own.companyId = company.companyId " +
//                        "AND company.companyId = ? " +
//                        "AND product.productid LIKE ?";
//            }
//                //            if(searchType.getSelectionModel().getSelectedItem() == "productID") {}
////                sql = "SELECT * FROM product, company, own WHERE product.productId = own.productId AND own.companyId = company.companyId AND company.companyId = ? AND product.productid LIKE ?";
////            }
//                pst = cn.prepareStatement(sql);
////            pst.setString(2, searchType.getSelectionModel().getSelectedItem());
//                pst.setString(1, company.getCompanyId());
//                pst.setString(2, "%" + searchField.getText() + "%");
//                rs = pst.executeQuery();
//
////            int i = 0;
////            }
//            while(rs.next()) {
//                products.add(new Product(rs.getString(1), rs.getString(2), rs.getDouble(3), rs.getInt(4)));
////                selectionBox.getItems().add(lectures.get(i).getLectureId() + " - " + lectures.get(i).getTitle());
////                i++;
//            }
//            productTable.setItems(products);
//        } catch (SQLException e) {
//            DialogBox.alertBox("Warning", e.getErrorCode() + " : " + e.getMessage());
//        } catch (Exception e) {
//            DialogBox.alertBox("Warning", e + "");
//        } finally {
//            try {
//                if(rs != null) {
//                    rs.close();
//                }
//            } catch (Exception e) {
//                DialogBox.alertBox("Error", e + "rs");
//            }
//            try {
//                if(st != null) {
//                    st.close();
//                }
//            } catch (Exception e) {
//                DialogBox.alertBox("Error", e + "st");
//            }
//            try {
//                if(pst != null) {
//                    pst.close();
//                }
//            } catch (Exception e) {
//                DialogBox.alertBox("Error", e + "st");
//            }
//            try {
//                if(cn != null) {
//                    cn.close();
//                }
//            } catch (Exception e) {
//                DialogBox.alertBox("Error", e + "cn");
//            }
//        }
//    }

    public void saveProduct() {
        if(isEmpty()) {
            DialogBox.alertBox("Warning", "Empty field is not allowed");
        } else {
            try {
                cn = MySQL.connect();
                String sqlProduct = "INSERT INTO product VALUES(?, ?, ?, ?)";
                pst = cn.prepareStatement(sqlProduct);
                pst.setString(1, productIdField.getText());
                pst.setString(2, productNameField.getText());
                pst.setDouble(3, Double.parseDouble(priceField.getText()));
                pst.setInt(4, Integer.parseInt(stockField.getText()));
                pst.executeUpdate();

                String sqlOwn = "INSERT INTO own VALUES(?,?)";
                pst = cn.prepareStatement(sqlOwn);
                pst.setString(1, company.getCompanyId());
                pst.setString(2, productIdField.getText());
                pst.executeUpdate();
                DialogBox.alertBox("Success", productNameField.getText() + " Inserted Successfuly");
                addProduct();
                productTable.setItems(getProducts());
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

    private boolean isEmpty() {
        if(productNameField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Product Name is Empty");
            return true;
        } else if(priceField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Price is Empty");
            return true;
        } else if(stockField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Stock is Empty");
            return true;
        } else if(productIdField.getText().isEmpty()) {
            DialogBox.alertBox("Warning", "Product ID is Empty");
            return true;
        } else {
            return false;
        }
    }

    private ObservableList<Product> getProducts() {
        products = FXCollections.observableArrayList();
        try {
            cn = MySQL.connect();
            String sql = "SELECT product.* FROM product, company, own WHERE product.productId = own.productId AND own.companyId = company.companyId AND company.companyId = '" + company.getCompanyId() + "'";
            st = cn.createStatement();
            rs = st.executeQuery(sql);
            while(rs.next()) {
                products.add(new Product(rs.getString(1),
                        rs.getString(2),
                        rs.getDouble(3),
                        rs.getInt(4)));
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
        return products;
    }
}
