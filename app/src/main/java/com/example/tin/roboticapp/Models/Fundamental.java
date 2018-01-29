package com.example.tin.roboticapp.Models;

/**
 * Created by Tin on 29/01/2018.
 */

public class Fundamental {

    private String price_date;
    private int company;
    private String price;

    // The Constructor
    public Fundamental(String price_date, int company, String price) {
        this.price_date = price_date;
        this.company = company;
        this.price = price;
    }

    // Getters
    public String getPrice_date() {
        return price_date;
    }

    public int getCompany() {
        return company;
    }

    public String getPrice() {
        return price;
    }
}
