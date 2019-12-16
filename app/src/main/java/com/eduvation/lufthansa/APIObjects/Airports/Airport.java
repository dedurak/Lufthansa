package com.eduvation.lufthansa.APIObjects.Airports;

public class Airport {
    private String airportCode;
    private String cityCode; // must be value of cityList
    private String countryCode; // must be value of countryList
    private String name;

    public Airport(String airportCode,
                   String cityCode,
                   String countryCode,
                   String name) {
        this.airportCode = airportCode;
        this.cityCode = cityCode;
        this.countryCode = countryCode;
        this.name = name;
    }

    public String getAirportCode() { return airportCode; }
    public String getCityCode() { return cityCode; }
    public String getCountryCode() { return countryCode; }
    public String getName() { return name; }
}
