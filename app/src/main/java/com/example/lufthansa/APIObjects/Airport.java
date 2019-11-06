package com.example.lufthansa.APIObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Airport {

    private String airportCode;
    private String airportName;


    public Airport(String code, String name) {
        airportCode = code;
        airportName = name;
    }


    public String getCode() { return airportCode; }
    public String getName() { return airportName; }
}
