package com.eduvation.lufthansa.APIObjects;

import android.graphics.drawable.Drawable;

public class TtFlight {
    private String airportCode;
    private String statusDependerCode;
    private String statusCode;
    private String statusOfFlight;
    private String timeToShow;
    private String flightNumber;
    private Drawable operatorLogo;

    public TtFlight(String airportCode,
                    String statusDependerCode,
                    String statusCode, String statusOfFlight,
                  String timeToShow, String flightNumber,
                  Drawable operatorLogo) {
        this.airportCode = airportCode;
        this.statusDependerCode = statusDependerCode;
        this.statusCode = statusCode;
        this.statusOfFlight = statusOfFlight;
        this.timeToShow = timeToShow;
        this.flightNumber = flightNumber;
        this.operatorLogo = operatorLogo;
    }

    /**
     // getter methods
     */
    public String getAirportCode () { return airportCode; }
    public String getStatusDependerCode() { return statusDependerCode; }
    public String getStatusCode() { return statusCode; }
    public String getStatusOfFlight () { return statusOfFlight; }
    public String getTimeToShow () { return timeToShow; }
    public String getFlightNumber () { return flightNumber; }
    public Drawable getLogo() { return operatorLogo; }
}

