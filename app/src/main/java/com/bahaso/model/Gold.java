package com.bahaso.model;


import java.util.Locale;

public class Gold {

    private int gold, price, id;
    private String currency;

    public Gold(){}

    public Gold(int gold, int price, int id, String currency){
        this.gold = gold;
        this.price = price;
        this.id = id;
        this.currency = currency;
    }

    public void setID(int id) { this.id = id; }

    public Integer getID() { return id; }

    public void setGold(int gold) { this.gold = gold; }

    public Integer getGold() { return gold; }

    public void setPrice(int price) { this.price = price; }

    public Integer getPrice() { return price; }

    public void setCurrency(String currency) { this.currency = currency; }

    public String getCurrency() { return currency + " " + String.format(new Locale("en"), "%,d",price); }

    public String getGoldString() { return gold+ " Gold"; }

}
