package com.example.lufthansa.APIObjects;

public class Flight {
    private Airport depAirport, arrAirport;
    private String plannedDepartureTime, estimatedDepartureTime;
    private String plannedArrivalTime, estimatedArrivalTime;
    private String gateDep, gateArr;
    private String airCraftCode, airCraftRegistration;
    private String statusOfFlight;
    private String operator, marketingFlightNumber[];
    private String flightNumber;
    private String departureTimeStatus, arrivalTimeStatus;

    public Flight(Airport depAirport, Airport arrAirport,
                  String operator, String flightNumber,
                  String plannedDepartureTime, String estimatedDepartureTime,
                  String plannedArrivalTime, String estimatedArrivalTime,
                  String departureGate, String arrivalGate,
                  String airCraftCode, String airCraftRegistration,
                  String statusOfFlight,
                  String departureTimeStatus, String arrivalTimeStatus) {
        this.depAirport = depAirport;
        this.arrAirport = arrAirport;
        this.operator = operator;
        this.flightNumber = flightNumber;
        this.plannedDepartureTime = plannedDepartureTime;
        this.estimatedDepartureTime = estimatedDepartureTime;
        this.plannedArrivalTime = plannedArrivalTime;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.gateDep = departureGate;
        this.gateArr = arrivalGate;
        this.airCraftCode = airCraftCode;
        this.airCraftRegistration = airCraftRegistration;
        this.statusOfFlight = statusOfFlight;
        this.departureTimeStatus = departureTimeStatus;
        this.arrivalTimeStatus = arrivalTimeStatus;
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
    public String getGateDep() { return gateDep; }
    public String getGateArr() { return gateArr; }
    public String getAirCraftCode() { return airCraftCode; }
    public String getAirCraftRegistration() { return airCraftRegistration; }
    public String getStatusOfFlight() { return statusOfFlight; }
    public String getDepartureTimeStatus() { return departureTimeStatus; }
    public String getArrivalTimeStatus() { return arrivalTimeStatus; }
    public String getOperator() { return operator; }
    public String getDepName() { return depAirport.getName(); }
    public String getArrName() { return arrAirport.getName(); }
}

