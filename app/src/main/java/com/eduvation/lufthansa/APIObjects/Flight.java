package com.eduvation.lufthansa.APIObjects;

import android.graphics.drawable.Drawable;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Flight {
    private Airport depAirport, arrAirport;
    private String plannedDepartureTime, estimatedDepartureTime;
    private String plannedArrivalTime, estimatedArrivalTime;
    private String plannedDepUTC, estDepUTC;
    private String plannedArrUTC, estArrUTC;
    private String gateDep, gateArr;
    private String airCraftCode, airCraftRegistration;
    private String statusOfFlight, statusCode;
    private String departureStatusCode, arrivalStatusCode;
    private String operator, marketingFlightNumber[];
    private String depTerminal, arrTerminal;
    private String flightNumber;
    private String departureTimeStatus, arrivalTimeStatus;
    private Drawable operatorLogo;

    public Flight(Airport depAirport, Airport arrAirport,
                  String operator, String flightNumber,
                  String plannedDepartureTime, String estimatedDepartureTime,
                  String plannedArrivalTime, String estimatedArrivalTime,
                  String plannedDepUTC, String estDepUTC,
                  String plannedArrUTC, String estArrUTC,
                  String departureGate, String arrivalGate,
                  String airCraftCode, String airCraftRegistration,
                  String statusOfFlight, String statusCode,
                  String departureStatusCode, String arrivalStatusCode,
                  String departureTimeStatus, String arrivalTimeStatus,
                  String depTerminal, String arrTerminal,
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
        this.gateDep = departureGate;
        this.gateArr = arrivalGate;
        this.airCraftCode = airCraftCode;
        this.airCraftRegistration = airCraftRegistration;
        this.statusOfFlight = statusOfFlight;
        this.statusCode = statusCode;
        this.departureStatusCode = departureStatusCode;
        this.arrivalStatusCode = arrivalStatusCode;
        this.departureTimeStatus = departureTimeStatus;
        this.arrivalTimeStatus = arrivalTimeStatus;
        this.depTerminal = depTerminal;
        this.arrTerminal = arrTerminal;
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
    public String getGateDep() { return gateDep; }
    public String getGateArr() { return gateArr; }
    public String getAirCraftCode() { return airCraftCode; }
    public String getAirCraftRegistration() { return airCraftRegistration; }
    public String getStatusOfFlight() { return statusOfFlight; }
    public String getStatusCode() { return statusCode; }
    public String getDepStatusCode() { return departureStatusCode; }
    public String getArrivalStatusCode() { return arrivalStatusCode; }
    public String getDepartureTimeStatus() { return departureTimeStatus; }
    public String getArrivalTimeStatus() { return arrivalTimeStatus; }
    public String getOperator() { return operator; }
    public String getDepTerminal() { return depTerminal; }
    public String getArrTerminal() { return arrTerminal; }
    public Drawable getLogo() { return operatorLogo; }

    // calculate and return planned flight duration
    public String getPlannedFlightDuration() {
        return calculateDuration(plannedArrUTC, plannedDepUTC);
    }

    // calculate real flight duration and return if both values given
    public String getRealFlightDuration() {
        return calculateDuration(estArrUTC, estDepUTC);
    }

    /**
     *
     * @return - returns the remaining flight time
     */
    public String getRemainingTime() {

        int hour=0, minute=0;

        // get current time
        DateFormat df = DateFormat.getTimeInstance();
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        String[] gmtTime = df.format(new Date()).split(":");

        Log.d(Flight.class.getSimpleName(), "gmt time: " + gmtTime);

        String arr[] = estArrUTC.split(":");
        arr[1] = arr[1].substring(0, 2);

        int arrHour = Integer.parseInt(arr[0]);
        int arrMinute = Integer.parseInt(arr[1]);

        int gmtHour = Integer.parseInt(gmtTime[0]);
        int gmtMinute = Integer.parseInt(gmtTime[1]);

        if(arrHour>gmtHour) {
            hour = arrHour - gmtHour;
            if(arrMinute<gmtMinute) {
                hour -= 1;
                minute = (60 - gmtMinute) + arrMinute;
            } else
                minute = arrMinute - gmtMinute;
        } else if(arrHour<gmtHour) {
            hour = (24 - gmtHour) + arrHour;
            if(arrMinute<gmtMinute) {
                hour -= 1;
                minute = (60 - gmtMinute) + arrMinute;
            } else
                minute = arrMinute - gmtMinute;
        }

        return hour + "h " + minute + "min";

    }

    /**
     * calculates the flight duration and returns it
     * @param arrTime - arrival gmt time
     * @param depTime - departure gmt time
     * @return - return value is the flight duration as string in format hh:MM
     */
    private String calculateDuration(String arrTime, String depTime) {

        Log.d(Flight.class.getSimpleName(), "arrTime: " + arrTime);
        Log.d(Flight.class.getSimpleName(), "depTime: " + depTime);

        String arr[] = arrTime.split(":");
        String dep[] = depTime.split((":"));

        String hour, minute;

        arr[1] = arr[1].substring(0, 2);
        dep[1] = dep[1].substring(0, 2);

        Log.d(Flight.class.getSimpleName(), "arr[1]; " + arr[1]);
        Log.d(Flight.class.getSimpleName(), "dep0: " + dep[0]);
        Log.d(Flight.class.getSimpleName(), "arr0: " + arr[0]);


        int arrMin = Integer.parseInt(arr[1]);
        int depMin = Integer.parseInt(dep[1]);
        int arrHour = Integer.parseInt(arr[0]);
        int depHour = Integer.parseInt(dep[0]);

        // 1st case the hour of dep and arr equals
        if(arrHour > depHour) {
            hour = Integer.toString(arrHour - depHour);
            if(arrMin<depMin) hour = Integer.toString(arrHour - depHour - 1);
            minute = Integer.toString(calcMin(arrMin, depMin));
        }
        // 2nd case arr hour is smaller than dep hour (means that arrival is on next day, not unusual for long haul flights)
        else if(arrHour<depHour) {
            hour = Integer.toString((24 - depHour) + arrHour);
            if(arrMin<depMin) hour = Integer.toString((24 - depHour) + (arrHour-1));
            // calc min the same way
            minute = Integer.toString(calcMin(arrMin, depMin));
        }
        else {
            hour = "00";
            minute = Integer.toString(arrMin - depMin);
        }

        if(Integer.parseInt(minute)<10)
            minute = "0" + minute;

        return hour + ":" + minute + " h";
    }

    private int calcMin(int arrMin, int depMin) {
        int minute;
        if(arrMin>depMin)
            minute = arrMin - depMin;
        else if(arrMin<depMin)
            minute = (60-depMin)+arrMin;
        else
            minute = 0;

        return minute;
    }
}

