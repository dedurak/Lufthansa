package com.eduvation.lufthansa.APIObjects;

public class Airport {

    private String airportCode;
    private String airportName;

    public Airport (String code) {
        airportCode = code;
    }

    public Airport(String code, String name) {
        airportCode = code;
        airportName = name;
    }


    public String getCode() { return airportCode; }
    public String getName() { return airportName; }
}
