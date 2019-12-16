package com.eduvation.lufthansa.APIObjects.Airlines;

public class Airline {
    private String airlineCode;
    private String airlineName;
    private int logo; // the drawable id

    public Airline(String airlineCode, String airlineName, int logo) {
        this.airlineCode = airlineCode;
        this.airlineName = airlineName;
        this.logo = logo;
    }

    // getter
    public String getAirlineCode() { return airlineCode; }
    public String getAirlineName() { return airlineName; }
    public int getLogo() { return logo; }
}
