package com.conference.company;

public class Product {
    private String productId;
    private String name;
    private double price;
    private int stock;
    private int sold;



    public Product(String productId, String name, int sold) {
        this.productId = productId;
        this.name = name;
        this.sold = sold;
        price = 0;
        stock = 0;

    }

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
        sold = 0;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSold() {
        return sold;
    }
}
