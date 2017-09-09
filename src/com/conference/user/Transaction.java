package com.conference.user;

import com.conference.company.Product;
import javafx.collections.ObservableList;

import java.sql.Date;

public class Transaction {
    private String transactionId;
    private ObservableList<Product> products;
    private double total;
    private Date date;

    public Transaction(String transactionId, ObservableList<Product> products, double total, Date date) {
        this.transactionId = transactionId;
        this.products = products;
        this.total = total;
        this.date = date;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }
}
