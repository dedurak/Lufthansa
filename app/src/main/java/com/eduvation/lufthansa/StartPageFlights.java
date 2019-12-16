package com.eduvation.lufthansa;

import android.app.Application;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eduvation.lufthansa.APIObjects.TimeTableFlight;

import java.text.SimpleDateFormat;
import java.util.*;

public class StartPageFlights extends Fragment {

    private final String TAG = StartPageFlights.class.getSimpleName();
    private List<TimeTableFlight> flightsTotal = new ArrayList<>();

    Application application;

    private String reqUrl;
    final Calendar cal = Calendar.getInstance();
    private String date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = getActivity().getApplication();
        date = getDate() + "T" + getTime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_start_page_flights, container, false);
    }

    private String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(cal.getTime());
    }

    private String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(cal.getTime());
    }
    

    /*
    private class FindFlights extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... strings) {

            RequestQueue req = Volley.newRequestQueue(getContext());

            JsonObjectRequest reqString = new JsonObjectRequest
                    (Request.Method.GET, strings[0], null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject flightStatusResource = response.getJSONObject("FlightStatusResource");
                                JSONObject flights = flightStatusResource.getJSONObject("Flights");
                                JSONArray flight = flights.getJSONArray("Flight");

                                for(int index=0; index<flight.length(); index++) {

                                    if(index == 10)
                                        break;

                                    TimeTableFlight flightPicked = new TimeTableFlight();
                                    JSONObject pickedObject = flight.getJSONObject(index);

                                    String buffer = pickedObject.getJSONObject("OperatingCarrier").getString("AirlineID");
                                    buffer += pickedObject.getJSONObject("OperatingCarrier").getString("FlightNumber");

                                    flightPicked.setFlightNo(buffer);
                                    flightPicked.setDestination(pickedObject.getJSONObject("Arrival").getString("AirportCode"));
                                    flightPicked.setDepTimePlanned(getTime((String) pickedObject.getJSONObject("Departure").getJSONObject("ScheduledTimeLocal").get("DateTime")));
                                    flightPicked.setFlightStatus(pickedObject.getJSONObject("Departure").getJSONObject("TimeStatus").getString("Definition"));
                                    flightsTotal.add(flightPicked);
                                }
                                addFlightsToLayout();
                            } catch(JSONException exc) {
                                exc.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError err) {
                            Log.d(TAG, "Error Response: " + err.networkResponse);
                        }
                    }) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    Log.d(TAG, "Auth Token is: " + ((StartPage) getActivity()).getToken().getAccess_token());
                    header.put("Authorization", "Bearer " + ((StartPage) getActivity()).getToken().getAccess_token());

                    return header;
                }
            };

            req.add(reqString);

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        private String getTime(String dateTime) {
            String[] buffer = dateTime.split("T");
            return buffer[1];
        }
    }
     */
}
