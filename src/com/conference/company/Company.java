package com.conference.company;

import com.conference.user.Member;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Company {
    private String companyId;
    private String name;
    private int engagements;
    private ObservableList<Product> products;
    private ObservableList<Member> staff;

    public Company(String companyId, String name, int engagements) {
        this.companyId = companyId;
        this.name = name;
        this.engagements = engagements;
        products = FXCollections.observableArrayList();
        staff = FXCollections.observableArrayList();
    }

    public Company(String companyId, String name, ObservableList<Product> products, ObservableList<Member> staff) {
        this.companyId = companyId;
        this.name = name;
        this.products = products;
        this.staff = staff;
        engagements = 0;
    }

    public Company(String companyId, String name, ObservableList<Product> products) {
        this.companyId = companyId;
        this.name = name;
        this.products = products;
        staff = FXCollections.observableArrayList();
        engagements = 0;
    }

    public Company(String companyId, String name) {
        this.companyId = companyId;
        this.name = name;
        products = FXCollections.observableArrayList();
        staff = FXCollections.observableArrayList();
        engagements = 0;
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

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<Member> getStaff() {
        return staff;
    }

    public int getEngagements() {
        return engagements;
    }
}
