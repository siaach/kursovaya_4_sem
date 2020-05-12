package com.example.currency;

public class Currency {

    private int numCode;
    private String name;
    private double value;


    public Currency(int numCode, String name, double value) {
        this.numCode = numCode;
        this.name = name;
        this.value = value;
    }

    public int getNumCode() {
        return numCode;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public void setNumCode(int numCode) {
        this.numCode = numCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
