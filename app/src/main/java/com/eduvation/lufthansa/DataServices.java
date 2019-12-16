package com.eduvation.lufthansa;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.eduvation.lufthansa.APIObjects.Airlines.AirlineList;
import com.eduvation.lufthansa.APIObjects.Airport;
import com.eduvation.lufthansa.APIObjects.Flight;
import com.eduvation.lufthansa.APIObjects.TtFlight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * this class extracts the necessary data for further usage from JSONObject/-Array received from main thread
 *
 * return value is one of the API Objects implemented
 */

public class DataServices {

    private final static String TAG = DataServices.class.getSimpleName();

    private static String depAirportName, arrAirportName, airportCode;
    private static String airportName;
    private static Flight flight;
    private static ArrayList<Flight> flightList;
    private static ArrayList<TtFlight> searchedFlights;

    private static String nextHour, nextMinute;

    private static String[] valuesForNextFlightSearch;

    /*this method receives the flight as a JSONObject as received from the API and
    * transforms into Flight object. The result is stored inside "flight".
    * The next activities/fragments may call getFlight to get the data
    * for further handling inside UI
    * type is the definition for the structure of the jsonobject - 1 is for Flights structure and 0 is for a handover
    * of Flight structure
    * */
    public static int extractFlight(JSONObject object, int type, Context context) {
        String operator;
        String flightNumber;
        String plannedDepTime, estDepTime;
        String plannedArrTime, estArrTime;
        String plannedDepTimeUTC, estDepTimeUTC;
        String plannedArrTimeUTC, estArrTimeUTC;
        String depGate, arrGate;
        String depTerminal, arrTerminal;
        String depAirportCode, arrAirportCode;
        String airCraftName, airCraftRegistration;
        String statusCode, statusOfFlight;
        Airport departure, arrival;
        String departureTimeStatus, arrivalTimeStatus;
        String depStatCde, arrStatCode;
        try {

            JSONObject flightObject = (type == 1) ?
                    (JSONObject) object.getJSONObject("Flights").getJSONArray("Flight").get(0) : object;

            Log.d(TAG, "Departure?: " + flightObject.toString());

            depAirportCode = (String) flightObject.getJSONObject("Departure")
                    .get("AirportCode");
            arrAirportCode = (String) flightObject.getJSONObject("Arrival")
                    .get("AirportCode");
            operator = (String) flightObject.getJSONObject("OperatingCarrier")
                    .get("AirlineID");
            Object buffer =  flightObject.getJSONObject("OperatingCarrier")
                    .get("FlightNumber");
            flightNumber = buffer.toString();
            plannedDepTime = getTime(flightObject.getJSONObject("Departure")
                    .getJSONObject("ScheduledTimeLocal")
                    .get("DateTime"));
            plannedDepTimeUTC = getTime(flightObject.getJSONObject("Departure")
                    .getJSONObject("ScheduledTimeUTC")
                    .get("DateTime"));
            airCraftName = flightObject.getJSONObject("Equipment")
                    .get("AircraftCode").toString();
            if(flightObject.getJSONObject("Equipment").opt("AircraftRegistration") != null) {
                airCraftRegistration = (String) flightObject.getJSONObject("Equipment")
                        .get("AircraftRegistration");
            } else
                airCraftRegistration = "unknown";
            statusCode = (String) flightObject.getJSONObject("FlightStatus")
                    .get("Code");
            statusOfFlight = (String) flightObject.getJSONObject("FlightStatus")
                    .get("Definition");

            depStatCde = (String) flightObject.getJSONObject("Departure")
                    .getJSONObject("TimeStatus")
                    .get("Code");
            arrStatCode = (String) flightObject.getJSONObject("Arrival")
                    .getJSONObject("TimeStatus")
                    .get("Code");

            Log.d(TAG, "arrstatcode: " + arrStatCode);

            departureTimeStatus = (String) flightObject.getJSONObject("Departure")
                    .getJSONObject("TimeStatus")
                    .get("Definition");
            arrivalTimeStatus = (String) flightObject.getJSONObject("Arrival")
                    .getJSONObject("TimeStatus")
                    .get("Definition");

            // extract estimated/actual time - a bit complicated because of the structure of data
            if(flightObject.getJSONObject("Departure").optJSONObject("EstimatedTimeLocal") != null)
                estDepTime = getTime(flightObject.getJSONObject("Departure")
                    .getJSONObject("EstimatedTimeLocal")
                    .get("DateTime"));
            else if(flightObject.getJSONObject("Departure").optJSONObject("ActualTimeLocal") != null)
                estDepTime = getTime(flightObject.getJSONObject("Departure")
                        .getJSONObject("ActualTimeLocal")
                        .get("DateTime"));

            else
                estDepTime = plannedDepTime;

            // now the same for the utc dep time
            if(flightObject.getJSONObject("Departure").optJSONObject("EstimatedTimeUTC") != null)
                estDepTimeUTC = getTime(flightObject.getJSONObject("Departure")
                        .getJSONObject("EstimatedTimeUTC")
                        .get("DateTime"));
            else if(flightObject.getJSONObject("Departure").optJSONObject("ActualTimeUTC") != null)
                estDepTimeUTC = getTime(flightObject.getJSONObject("Departure")
                        .getJSONObject("ActualTimeUTC")
                        .get("DateTime"));
            else
                estDepTimeUTC = plannedDepTimeUTC;

            plannedArrTime = getTime(flightObject.getJSONObject("Arrival")
                    .getJSONObject("ScheduledTimeLocal")
                    .get("DateTime"));

            plannedArrTimeUTC = getTime(flightObject.getJSONObject("Arrival")
                    .getJSONObject("ScheduledTimeUTC")
                    .get("DateTime"));

            // extract estimated/planned time
            if(flightObject.getJSONObject("Arrival").optJSONObject("EstimatedTimeLocal") != null)
                estArrTime = getTime(flightObject.getJSONObject("Arrival")
                        .getJSONObject("EstimatedTimeLocal")
                        .get("DateTime"));

            else if(flightObject.getJSONObject("Arrival").optJSONObject("ActualTimeLocal") != null)
                estArrTime = getTime(flightObject.getJSONObject("Arrival")
                        .getJSONObject("ActualTimeLocal")
                        .get("DateTime"));
            else
                estArrTime = plannedArrTime;


            // the same for the utc time
            if(flightObject.getJSONObject("Arrival").optJSONObject("EstimatedTimeUTC") != null)
                estArrTimeUTC = getTime(flightObject.getJSONObject("Arrival")
                        .getJSONObject("EstimatedTimeUTC")
                        .get("DateTime"));

            else if(flightObject.getJSONObject("Arrival").optJSONObject("ActualTimeUTC") != null)
                estArrTimeUTC = getTime(flightObject.getJSONObject("Arrival")
                        .getJSONObject("ActualTimeUTC")
                        .get("DateTime"));
            else
                estArrTimeUTC = plannedArrTimeUTC;



            if(flightObject.getJSONObject("Departure").optJSONObject("Terminal") != null) {
                depGate = flightObject.getJSONObject("Departure")
                        .getJSONObject("Terminal")
                        .optString("Gate", "unknown");
                depTerminal = flightObject.getJSONObject("Departure")
                        .getJSONObject("Terminal")
                        .optString("Name", "unknown");
            }
            else {
                depGate = "unknown";
                depTerminal = "unknown";
            }

            if(flightObject.getJSONObject("Arrival").optJSONObject("Terminal") != null) {
                arrGate = flightObject.getJSONObject("Arrival")
                        .getJSONObject("Terminal")
                        .optString("Gate", "unknown");
                arrTerminal = flightObject.getJSONObject("Arrival")
                        .getJSONObject("Terminal")
                        .optString("Name", "unknown");
            } else {
                arrGate = "unknown";
                arrTerminal = "unknown";
            }

            Log.d(TAG, "Airport Name: " + airportName);

            Log.d(TAG, "Airport Name before creation: " + airportName);

            departure = new Airport(depAirportCode, "");

            arrival = new Airport(arrAirportCode, "");

            Drawable operatorLogo = AirlineList.getLogo(operator, context);

            flight = new Flight(departure, arrival, operator, flightNumber,
                    plannedDepTime, estDepTime, plannedArrTime, estArrTime,
                    plannedDepTimeUTC, estDepTimeUTC, plannedArrTimeUTC, estArrTimeUTC,
                    depGate, arrGate, airCraftName, airCraftRegistration, statusOfFlight, statusCode,
                    depStatCde, arrStatCode, departureTimeStatus, arrivalTimeStatus, depTerminal, arrTerminal, operatorLogo);

            return 1;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }


    private static String getTime(Object obj) {
        String time = (String) obj;
        return time.split("T")[1];
    }

    /**
     * this method extracts and creates an according Flight Array in case of route search
     * all results are stored as an JSONArray inside JSONObject.
     * */
    public static int extractFlights(JSONObject object, Context context) {
        flightList = new ArrayList<>();


        // if there is only one flight the returned value is a jsonobject
        // otherwise we receive an array
        // how do i do this? - we check if getJSONObject("Flight") is instanceof JSONObject, the other possible type is JSONArray
        try {
            Log.d(TAG, "FlightStatusResource: " + object.toString());
            JSONObject flightChecker = null;
            JSONArray flights = null;

            // api always returns
            flights = object.getJSONObject("Flights").getJSONArray("Flight");

            int index = 0;

            while(index < flights.length()) {
                if(extractFlight(flights.getJSONObject(index), 0, context) == 1) {
                    flightList.add(flight);
                }
                ++index;
            }

            return 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }


    // mode 0 - there is no endtime
    // mode 1 -there is an endtime
    // return 1 if fragment can call the next one to show the results, return 0 if not
    public static int extractDepartures(JSONObject object, int mode, Context context) {

        if (mode == 0) {
            searchedFlights = new ArrayList<>();
        }

        try {
            JSONArray jsonDepartures = object.getJSONObject("Flights")
                    .getJSONArray("Flight");


            int parseThroughArray = 0;

            // parse through jsonarray
            while(parseThroughArray<jsonDepartures.length()) {

                // get next flight inside list
                JSONObject obj = jsonDepartures.getJSONObject(parseThroughArray);

                String aCode = obj.getJSONObject("Arrival").getString("AirportCode");

                String operator = obj.getJSONObject("OperatingCarrier").getString("AirlineID");
                String flightNo = obj.getJSONObject("OperatingCarrier").getString("FlightNumber");
                String flightNumber = operator + flightNo;

                String pDepTime = getTime(obj.getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").getString("DateTime"));
                String statusCode = obj.getJSONObject("FlightStatus").getString("Code");
                String statusDef = obj.getJSONObject("FlightStatus").getString("Definition");
                String statusCodeDep = obj.getJSONObject("Departure").getJSONObject("TimeStatus").getString("Code");
                String statusCodeArr = obj.getJSONObject("Arrival").getJSONObject("TimeStatus").getString("Code");

                Drawable operatorLogo = AirlineList.getLogo(operator, context);

                TtFlight nOne = new TtFlight(aCode, statusCodeDep, statusCode, statusDef, pDepTime, flightNumber, operatorLogo);

                searchedFlights.add(nOne);

                ++parseThroughArray;
            }

            JSONArray searchForNextLinkValues = object.getJSONObject("Meta").getJSONArray("Link");

            String link = doTheSearchForLink(searchForNextLinkValues);
            if(link != null)
                valuesForNextFlightSearch = getTheValues(link, "departures/");

            return 1;

        } catch(JSONException exc) {
            exc.printStackTrace();
        }

        return 0;
    }


    public static int extractArrivals(JSONObject object, int mode, Context context) {
        if (mode == 0)
            searchedFlights = new ArrayList<>();

        try {
            JSONArray jsonArray = object.getJSONObject("Flights")
                    .getJSONArray("Flight");

            int parsingIndex = 0;
            while (parsingIndex < jsonArray.length()) {
                JSONObject obj = jsonArray.getJSONObject(parsingIndex);

                String operator = obj.getJSONObject("OperatingCarrier").getString("AirlineID");
                String flightNo = obj.getJSONObject("OperatingCarrier").getString("FlightNumber");
                String flightNumber = operator + flightNo;

                String from = obj.getJSONObject("Departure").getString("AirportCode");
                String arrTime = getTime(obj.getJSONObject("Arrival").getJSONObject("ScheduledTimeLocal").getString("DateTime"));
                String statusDependerCode = obj.getJSONObject("Arrival").getJSONObject("TimeStatus").getString("Code");
                String status = obj.getJSONObject("FlightStatus").getString("Definition");
                String statusCode = obj.getJSONObject("FlightStatus").getString("Code");

                Drawable operatorLogo = AirlineList.getLogo(operator, context);

                TtFlight flight = new TtFlight(from, statusDependerCode, statusCode, status, arrTime, flightNumber, operatorLogo);

                ++parsingIndex;

                searchedFlights.add(flight);
            }

            JSONArray searchForNextLinkValues = object.getJSONObject("Meta").getJSONArray("Link");

            String link = doTheSearchForLink(searchForNextLinkValues);
            if(link != null)
                valuesForNextFlightSearch = getTheValues(link, "arrivals/");

            return 1;
        } catch (JSONException exc) {
            exc.printStackTrace();
        }

        return 0;
    }

    private static String doTheSearchForLink(JSONArray array) {
        String link = null;

        try {

            int index = 0;
            String rel;

            // if array is bigger than 4 we search for @Rel = "next" else for @Rel="nextRange
            if (array.length() > 4) {

                while (index < array.length()) {

                    rel = array.getJSONObject(index).getString("@Rel");
                    if (rel.equals("next"))
                        return array.getJSONObject(index).getString("@Href");
                    else
                        ++ index;
                }

            } else {
                index = 0;
                while(index<array.length()) {

                    rel = array.getJSONObject(index).getString("@Rel");
                    if (rel.equals("nextRange"))
                        return array.getJSONObject(index).getString("@Href");
                    else
                        ++ index;
                }
            }
        } catch (JSONException exc) {
            exc.printStackTrace();
        }

        return link;
    }

    private static String[] getTheValues(String link, String regexMode) {
        String[] firstSplit = link.split(regexMode);
        String toUse = firstSplit[1];
        String[] depCodeSplit = toUse.split("/");

        String depCode = depCodeSplit[0];

        String[] dateTimeSplitter = depCodeSplit[1].split("\\?");

        String dateTime = dateTimeSplitter[0];

        String[] lastSplitter = dateTimeSplitter[1].split("&");
        String offsetSplitter = lastSplitter[0];
        String limitSplitter = lastSplitter[1];
        String[] offset = offsetSplitter.split("=");
        String[] limit = limitSplitter.split("=");

        String offSetValue = offset[1];
        String limitValue = limit[1];

        return new String[] {depCode, dateTime, offSetValue, limitValue};
    }

    // return flight from list on position index
    public static Flight getFlightFromList(int index) {
        return flightList.get(index);
    }

    public static String[] getValuesForNextFlightSearch() { return valuesForNextFlightSearch; }

    public static Flight getFlight() { return flight; }

    public static ArrayList<Flight> getFlightList() { return flightList; }

    public static ArrayList<TtFlight> getSearchedFlights() { return searchedFlights; }

}
