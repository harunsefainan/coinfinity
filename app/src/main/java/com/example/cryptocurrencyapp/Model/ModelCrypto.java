package com.example.cryptocurrencyapp.Model;

import java.io.Serializable;

public class ModelCrypto implements Serializable {

    private String id;
    private String name;
    private String symbol;
    private double price;
    private double percent;
    private double marketcap;
    private double volume24h;
    private double dominance;
    private double percentChange30d;
    private double totalSupply;

    public double getMarketcap() {
        return marketcap;
    }

    public void setMarketcap(double marketcap) {
        this.marketcap = marketcap;
    }

    public double getVolume24h() {
        return volume24h;
    }

    public void setVolume24h(double volume24h) {
        this.volume24h = volume24h;
    }

    public double getDominance() {
        return dominance;
    }

    public void setDominance(double dominance) {
        this.dominance = dominance;
    }

    public double getPercentChange30d() {
        return percentChange30d;
    }

    public void setPercentChange30d(double percentChange30d) {
        this.percentChange30d = percentChange30d;
    }

    public double getTotalSupply() {
        return totalSupply;
    }

    public void setTotalSupply(double totalSupply) {
        this.totalSupply = totalSupply;
    }

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


    public ModelCrypto(String id, String name, String symbol, double price,double percent,double marketcap, double volume24h, double dominance, double percentChange30d, double totalSupply) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.price = price;
        this.percent=percent;
        this.marketcap = marketcap;
        this.volume24h = volume24h;
        this.dominance = dominance;
        this.percentChange30d = percentChange30d;
        this.totalSupply = totalSupply;

    }
}
