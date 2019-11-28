package com.example.lufthansa.APIObjects;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TtFlight {
    private Airport depAirport, arrAirport;
    private String plannedDepartureTime, estimatedDepartureTime;
    private String plannedArrivalTime, estimatedArrivalTime;
    private String plannedDepUTC, estDepUTC;
    private String plannedArrUTC, estArrUTC;
    private String airCraftCode, airCraftRegistration;
    private String statusOfFlight, statusCode;
    private String departureStatusCode, arrivalStatusCode;
    private String operator, marketingFlightNumber[];
    private String flightNumber;
    private String departureTimeStatus, arrivalTimeStatus;
    private Drawable operatorLogo;

    public TtFlight(Airport depAirport, Airport arrAirport,
                  String operator, String flightNumber,
                  String plannedDepartureTime, String estimatedDepartureTime,
                  String plannedArrivalTime, String estimatedArrivalTime,
                  String plannedDepUTC, String estDepUTC,
                  String plannedArrUTC, String estArrUTC,
                  String airCraftCode, String airCraftRegistration,
                  String statusOfFlight, String statusCode,
                  String departureStatusCode, String arrivalStatusCode,
                  String departureTimeStatus, String arrivalTimeStatus,
                  Drawable operatorLogo) {
        this.depAirport = depAirport;
        this.arrAirport = arrAirport;
        this.operator = operator;
        this.flightNumber = flightNumber;
        this.plannedDepartureTime = plannedDepartureTime;
        this.estimatedDepartureTime = estimatedDepartureTime;
        this.plannedArrivalTime = plannedArrivalTime;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.plannedDepUTC = plannedDepUTC;
        this.estDepUTC = estDepUTC;
        this.plannedArrUTC = plannedArrUTC;
        this.estArrUTC = estArrUTC;
        this.airCraftCode = airCraftCode;
        this.airCraftRegistration = airCraftRegistration;
        this.statusOfFlight = statusOfFlight;
        this.statusCode = statusCode;
        this.departureStatusCode = departureStatusCode;
        this.arrivalStatusCode = arrivalStatusCode;
        this.departureTimeStatus = departureTimeStatus;
        this.arrivalTimeStatus = arrivalTimeStatus;
        this.operatorLogo = operatorLogo;
    }

    /**
     // getter methods
     */
    public String getFlightNumber() { return operator + flightNumber; }
    public String getDepCode() { return depAirport.getCode(); }
    public String getArrCode() { return arrAirport.getCode(); }
    public String getPlannedDepartureTime() { return plannedDepartureTime; }
    public String getEstimatedDepartureTime() { return estimatedDepartureTime; }
    public String getPlannedArrivalTime() { return plannedArrivalTime; }
    public String getEstimatedArrivalTime() { return estimatedArrivalTime; }
    public String getAirCraftCode() { return airCraftCode; }
    public String getAirCraftRegistration() { return airCraftRegistration; }
    public String getStatusOfFlight() { return statusOfFlight; }
    public String getStatusCode() { return statusCode; }
    public String getDepStatusCode() { return departureStatusCode; }
    public String getArrivalStatusCode() { return arrivalStatusCode; }
    public String getDepartureTimeStatus() { return departureTimeStatus; }
    public String getArrivalTimeStatus() { return arrivalTimeStatus; }
    public String getOperator() { return operator; }
    public Drawable getLogo() { return operatorLogo; }

}

