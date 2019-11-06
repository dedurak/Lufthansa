package com.example.lufthansa;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lufthansa.APIObjects.*;
import com.google.gson.Gson;

import java.util.*;

public class ApiRequest extends Application {
    private final static String TAG = ApiRequest.class.getSimpleName();

    private List<Flight> flightResults;

    /**
    private String key = BuildConfig.ApiKey;
    private String sec = BuildConfig.ApiSec;
     */
    private List<String> token;
    //private AccessTokenObject access_token;
    Context context;
    String reqUrl;

    private int locker;

    /**
     * Fetchs an AuthToken from server-side, which is used for each request
     * Later you can use the AccessTokenObject Object instantiated for reqs
     * @param cnt
     */
    /**
    public void setAccess_token(Context cnt) {
        RequestQueue reqQueue = Volley.newRequestQueue(cnt);

        String url = "https://api.lufthansa.com/v1/oauth/token";
        StringRequest req = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response );
                        Gson gson = new Gson();
                        access_token = gson.fromJson(response, AccessTokenObject.class);
                        if(access_token == null)
                            Log.d(TAG, "AuthToken null");
                        else
                            Log.d(TAG, "Accesstoken: " + access_token.getAccess_token());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "StatusCode: " + error.networkResponse.statusCode);
                        Log.d(TAG, "ErrorMessage: " + error.networkResponse.headers);
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", key);
                params.put("client_secret", sec);
                params.put("grant_type", "client_credentials");
                return params;
            }
        };
        reqQueue.add(req);
    }
     */
/**
    private void doGetRequest(String url)
        RequestQueue requestQueue = Volley.newRequestQueue(this);
    }
*/
/**
    public AccessTokenObject getAccessToken() {
        return access_token;
    }
*/
    public void delElemsOfFlightResults() {
        flightResults = new ArrayList<>();
    }

    /**
     * this method is used within in StartPage Asynctask to assign the flights of all flight results
     * to the list. Later, inside flightStatusOverviewResult the flight elems and there are picked to
     * show them inside new layout elem of flight_status_result_overview
     * @param flightToaAdd
     */
    public void addFlightToResults(List<Flight> flightToaAdd) {
        ListIterator<Flight> flightListIterator = flightToaAdd.listIterator();
        while(flightListIterator.hasNext()) {
            flightResults.add(flightListIterator.next());
        }
    }

    public int getCountOfFlights() {
        return flightResults.size();
    }

    public Flight getFlight(int index) {
        ListIterator<Flight> iterator = flightResults.listIterator();
        Flight flightToReturn;

        // iterate through list until we reach the index position
        while(index>0) {
            iterator.next();
            --index;
        }

        return iterator.next();
    }
}
