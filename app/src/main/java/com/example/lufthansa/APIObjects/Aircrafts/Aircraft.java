package com.example.lufthansa.APIObjects.Aircrafts;

public class Aircraft {
    private String airCraftCode;
    private String airCraftName;

    public Aircraft(String airCraftCode, String airCraftName) {
        this.airCraftCode = airCraftCode;
        this.airCraftName = airCraftName;
    }

    public String getAirCraftName() { return airCraftName; }
    public String getAirCraftCode() { return airCraftCode; }

}
