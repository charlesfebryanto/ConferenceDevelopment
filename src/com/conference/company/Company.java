package com.conference.company;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Company {
    private String companyId;
    private String name;
    private ObservableList<Product> products;

    public Company(String companyId, String name, ObservableList<Product> products) {
        this.companyId = companyId;
        this.name = name;
        this.products = products;
    }

    public Company(String companyId, String name) {
        this.companyId = companyId;
        this.name = name;
        products = FXCollections.observableArrayList();
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }
}
