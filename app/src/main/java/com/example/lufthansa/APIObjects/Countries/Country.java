package com.example.lufthansa.APIObjects.Countries;

public class Country {
    private String countryCode;
    private String name;

    public Country(String countryCode, String name) {
        this.countryCode = countryCode;
        this.name = name;
    }

    public String getCountryCode() { return countryCode; }
    public String getName() {return name; }
}
