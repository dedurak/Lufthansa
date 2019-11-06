package com.example.lufthansa.APIObjects.Cities;

public class City {
    private String cityCode;
    private String countryCode;
    private String name;

    public City(String cityCode, String countryCode, String name) {
        this.cityCode = cityCode;
        this.countryCode = countryCode;
        this.name = name;
    }

    public String getCityCode() { return cityCode; }
    public String getName() { return name; }
}
