package com.eduvation.lufthansa.APIObjects;

public class TimeTableFlight {
    String flightNo;
    String destination;
    String depTimePlanned;
    String depTimeEstimated;
    String flightStatus;
    String nextRange; // only used if necessary

    public TimeTableFlight() {}


    /***
     * Getter methods
     * @return
     */

    public String getFlightNo() { return flightNo; }
    public String getDestination() { return destination; }
    public String getDepTimePlanned() { return depTimePlanned; }
    public String getDepTimeEstimated() { return depTimeEstimated; }
    public String getFlightStatus() { return flightStatus; }


    /**
     * Setter methods
     * @param cnt
     */

    public void setFlightNo(String cnt) { flightNo = cnt; }
    public void setDestination(String cnt) { destination = cnt; }
    public void setDepTimePlanned(String cnt) { depTimePlanned = cnt; }
    public void setDepTimeEstimated(String cnt) { depTimeEstimated = cnt; }
    public void setFlightStatus(String cnt) { flightStatus = cnt; }

}
