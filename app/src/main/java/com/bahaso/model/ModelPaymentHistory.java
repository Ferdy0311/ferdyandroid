package com.bahaso.model;

/**
 * Created by ASUS on 3/16/2017.
 */

public class ModelPaymentHistory {

    private String date, paymentStatus;
    private int amountGold, totalPrice, id;

    public ModelPaymentHistory() {}

    public ModelPaymentHistory(String date, String paymentStatus, int amountGold, int totalPrice, int id){
        this.paymentStatus = paymentStatus;
        this.amountGold = amountGold;
        this.date = date;
        this.totalPrice = totalPrice;
        this.id = id;
    }

    public void setDateHistoryPayment(String date) { this.date = date; }

    public String getDateHistoryPayment() { return date; }

    public void setHistoryPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getHistoryPaymentStatus() { return paymentStatus; }

    public void setHistoryAmountGold(int amountGold) { this.amountGold = amountGold; }

    public int getHistoryAmountGold() { return amountGold; }

    public void setHistoryTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public int getHistoryTotalPrice() { return totalPrice; }

    public void setHistoryTransactionID(int id) { this.id = id; }

    public int getHistoryTransactionID() { return id; }

}
