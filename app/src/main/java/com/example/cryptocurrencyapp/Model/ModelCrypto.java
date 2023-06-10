package com.example.cryptocurrencyapp.Model;

public class ModelCrypto {

    private String id;
    private String name;
    private String symbol;
    private double price;

    private double percent;

    public  ModelCrypto(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }


    public ModelCrypto(String id, String name, String symbol, double price,double percent) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.percent=percent;

    }
}
