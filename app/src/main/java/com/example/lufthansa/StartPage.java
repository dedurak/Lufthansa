package com.example.lufthansa;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lufthansa.APIObjects.AccessTokenObject;
import com.example.lufthansa.APIObjects.ApiDepartures;
import com.example.lufthansa.APIObjects.Flight;
import com.example.lufthansa.APIObjects.ApiFlightResults;
import com.example.lufthansa.MainFragments.FragmentsAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import io.reactivex.*;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import org.json.JSONObject;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.*;

public class StartPage extends FragmentActivity implements EventListener {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    public static AccessTokenObject access_token;
    public static String nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        try {
            retrofitAccessToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // AsyncTask --> setAccessToken();
    }

    /***
    public void onFlightSearch(View view) {
        FlightStatusSearchWdw searchWdw = new FlightStatusSearchWdw();
        searchWdw.show(getSupportFragmentManager(), TAG);
    }

    // open departure search window
    public void onDepartureSearch(View view) {
        DeparturesSearchWdw wdw = new DeparturesSearchWdw();
        wdw.show(getSupportFragmentManager(), TAG);
    }
     **/

    // open arrival search window
    public void onArrivalSearch(View view) {

    }

    /* AsyncTask request
    *
    private void setAccessToken() {
        new AccessToken().execute();
    }
*/
/**
    private void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        StartPageFlights fragment = new StartPageFlights();
        transaction.replace(R.id.flightContainer, fragment);
        transaction.commit();
    }
*/
    /**
     *
     * @param typeOfRequest - 0 for search by flightNumber
     *                        1 for search by route
     */
    @Override
    public void receiveData(int typeOfRequest, String[] params) {
       // Log.d(TAG, "Data received: " + url);
       // reqURL = url;
        Integer i = 100;

        // if 0, search for flightnumber
        if(typeOfRequest == 0)
            retrofitFlightStatusByFlightNumber(params);

        // if 1, search for route
        else if(typeOfRequest == 1)
            retrofitFlightStatusByRoute(params);

        // if 2, search for departures
        else if(typeOfRequest == 2)
            retrofitDeparturesSearch(params);
    }

    private void startFlightOverViewActivity() {
       // ((ApiRequest) this.getApplication()).delElemsOfFlightResults();
       // ((ApiRequest) this.getApplication()).addFlightToResults(flightList);
        Intent flightOverview = new Intent(this, FlightStatusOverviewResult.class);
        flightOverview.putExtra("goto", "startpage");
        startActivity(flightOverview);
    }


    /**
    // call the flightDetailResult class to show the flight details inside seperate activity
     **/
    private void startFlightResultActivity() {
        Intent flightStatus = new Intent(this, FlightStatusDetailResult.class);

        flightStatus.putExtra("goto", "main");
        startActivity(flightStatus);
    }

    @Override
    public Context getCtx() {
        return this;
    }

    public AccessTokenObject getToken() {
        return access_token;
    }

    // Retrofit call for access token
    private void retrofitAccessToken() throws IOException {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);

        Single<retrofit2.Response<AccessTokenObject>> retroftiApiToken = service.getAccessToken(
                BuildConfig.ApiKey,
                BuildConfig.ApiSec,
                "client_credentials");

        retroftiApiToken.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<AccessTokenObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: " + d.toString());
                    }

                    @Override
                    public void onSuccess(retrofit2.Response<AccessTokenObject> object) {
                        Log.d(TAG, "onSuccess - AccessToken: " + object.body().getAccess_token());
                        access_token = object.body();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }
                });
    }

    /* Asynctask for access token
    private class AccessToken extends AsyncTask<String, AccessTokenObject, AccessTokenObject> {
        AccessTokenObject object;

        protected AccessTokenObject doInBackground(String... strings) {

            RequestQueue reqQueue = Volley.newRequestQueue(getCtx());

            String url = "https://api.lufthansa.com/v1/oauth/token";
            StringRequest req = new StringRequest
                    (Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, response );
                            Gson gson = new Gson();
                            object = gson.fromJson(response, AccessTokenObject.class);
                            if(object == null)
                                Log.d(TAG, "AuthToken null");
                            else
                                Log.d(TAG, "Accesstoken: " + object.getAccess_token());
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
                    params.put("client_id", BuildConfig.ApiKey);
                    params.put("client_secret", BuildConfig.ApiSec);
                    params.put("grant_type", "client_credentials");
                    return params;
                }
            };
            reqQueue.add(req);

            return object;
        }

        @Override
        protected void onPostExecute(AccessTokenObject object) {
            access_token = object;
        }
    }
*/

    private void retrofitFlightStatusByFlightNumber(String[] params) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "Params 1: " +  params[0] + "\nParams2: " + params[1]);

        Single<Response<ApiFlightResults>> retrofitFlight = service.getFlightByFlightNumber(
                params[0], params[1], "Bearer " + access_token.getAccess_token()
        );

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiFlightResults>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiFlightResults> jsonObjectResponse) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSuccess");
                        //JSONObject object = jsonObjectResponse.body();
                        Log.d(TAG, "response body: " + jsonObjectResponse.body());
                        Log.d(TAG, "response msg: " + jsonObjectResponse.raw());
                        if(DataServices.extractFlight(jsonObjectResponse.body().getFlightStatusResource(), 1) == 1) {
                            startFlightResultActivity();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                        e.printStackTrace();
                    }
                });
    }
/*
    private class FlightStatusByFlightNumber extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... strings) {

            RequestQueue req = Volley.newRequestQueue(getCtx());

            JsonObjectRequest reqString = new JsonObjectRequest
                    (Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject flightStatusResource = response.getJSONObject("FlightStatusResource");
                                JSONObject flights = flightStatusResource.getJSONObject("Flights");
                                JSONObject flight = flights.getJSONObject("Flight");

                                JSONObject departure = flight.getJSONObject("Departure");
                                JSONObject arrival = flight.getJSONObject("Arrival");
                                depAirportCode = (String) departure.get("AirportCode"); // depCode
                                Log.d(TAG, "inside background operation, departure: " + depAirportCode);
                                arrAirportCode = (String) arrival.get("AirportCode"); // arrCode

                                JSONObject flightNo = flight.getJSONObject("OperatingCarrier");
                                flightNumber = (String) flightNo.get("AirlineID") + flightNo.get("FlightNumber");

                                JSONObject scheduledDepTimeLocal = departure.getJSONObject("ScheduledTimeLocal");
                                depTimePlanned = (String) scheduledDepTimeLocal.get("DateTime"); // planned dep Time
                                depTimePlanned = getTime(depTimePlanned);

                                if(departure.optJSONObject("Terminal") == null)
                                    depGate = "unknown";
                                else {
                                    JSONObject depTerminal = departure.getJSONObject("Terminal");
                                    depGate = depTerminal.optString("Gate", "unknown");
                                }
                                JSONObject arrTerminal = arrival.getJSONObject("Terminal");
                                if(arrTerminal != null)
                                    arrGate = arrTerminal.optString("Gate", "unknown");
                                else
                                    arrGate = "unknown";

                                JSONObject estimatedDepTimeLocal;
                                if(departure.optJSONObject("EstimatedTimeLocal") != null)
                                    estimatedDepTimeLocal = departure.getJSONObject("EstimatedTimeLocal");
                                else
                                    estimatedDepTimeLocal = departure.optJSONObject("ActualTimeLocal");

                                if(estimatedDepTimeLocal != null)
                                    depTimeAct = estimatedDepTimeLocal.optString("DateTime", "unknown");
                                else
                                    depTimeAct = null;

                                if(depTimeAct != null)
                                    depTimeAct = getTime(depTimeAct);

                                JSONObject scheduledArrTimeLocal = arrival.getJSONObject("ScheduledTimeLocal");
                                arrTimePlanned = (String) scheduledArrTimeLocal.get("DateTime"); // planned dep Time
                                arrTimePlanned = getTime(arrTimePlanned);

                                JSONObject estimatedArrTimeLocal;
                                if(arrival.optJSONObject("EstimatedTimeLocal") != null)
                                    estimatedArrTimeLocal = arrival.getJSONObject("EstimatedTimeLocal");
                                else
                                    estimatedArrTimeLocal = arrival.optJSONObject("ActualTimeLocal");

                                if(estimatedArrTimeLocal != null)
                                    arrTimeAct = estimatedArrTimeLocal.optString("DateTime", "unknown");
                                else
                                    arrTimeAct = null;

                                if(arrTimeAct != null)
                                    arrTimeAct = getTime(arrTimeAct);

                                Log.d(TAG, "From: " + depAirportCode + " To: " + arrAirportCode + " DepTime: " + depTimePlanned + " ArrTime: " + arrTimePlanned);
                                new AirportInfo().execute(depAirportCode, arrAirportCode);
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
                    header.put("Authorization", "Bearer " + getToken().getAccess_token());

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

        private String getTime(String depTimePlanned) {
            String[] strTime = depTimePlanned.split("T");
            return strTime[1];
        }
    }

*/

    private void retrofitFlightStatusByRoute(String[] params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Single<retrofit2.Response<ApiFlightResults>> retrofitFlights = service.getFlightsByRoute(
                params[0], params[1], params[2], "Bearer " + access_token.getAccess_token()
        );

        retrofitFlights.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<ApiFlightResults>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "inside FlightStatusbyRoute - onSubscribe");
                    }

                    @Override
                    public void onSuccess(retrofit2.Response<ApiFlightResults> jsonObjectResponse) {
                        Log.d(TAG, "inside FlightStatusByRoute - onSuccess");
                        JSONObject object;
                        if(jsonObjectResponse.body() != null) {
                            object = jsonObjectResponse.body().getFlightStatusResource();
                            Log.d(TAG, "JSONObject: " + object);
                            if (DataServices.extractFlights(object) == 1) {

                                // before starting the overview activity to show the flight results, check if there are any flights
                                // otherwise show the toast messsage

                                if (DataServices.getFlightList().size() > 0)
                                    startFlightOverViewActivity();
                                else
                                    Toast.makeText(StartPage.this, "No flights found!", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(StartPage.this, "No flights found!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "inside FlightStatusByRoute - onError");
                        e.printStackTrace();
                    }
                });
    }


    private void retrofitDeparturesSearch(String[] params) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        final String endTime = params[2];
        final String date = params[1].split("T")[0].split("-")[2];

        final Single<retrofit2.Response<ApiDepartures>> retrofitDepartures = service.getDepartures(
                params[0], params[1], access_token.getAccess_token());

        retrofitDepartures.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiDepartures>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "inside onSubscribe @ departure");
                    }

                    @Override
                    public void onSuccess(Response<ApiDepartures> apiDeparturesResponse) {
                        JSONObject object;
                        if(apiDeparturesResponse.body() != null) {
                            object = apiDeparturesResponse.body().getDeparturesResource();

                            // if 0 the nextpage needs to be fetched
                            if(DataServices.extractDepartures(object, endTime, date)==0) {

                                if(nextPage != null) {

                                    String takeParams[] = nextPage.split("/");
                                    String paramsForStartNextSearch[] = new String[3];

                                    int index = 1;
                                    paramsForStartNextSearch[2] = endTime;
                                    for(int indexAtEnd = takeParams.length-1;
                                        index >= 0;
                                        index--) {

                                        paramsForStartNextSearch[1] = takeParams[indexAtEnd];

                                        --indexAtEnd;
                                    }

                                    retrofitDeparturesSearch(paramsForStartNextSearch);
                                }

                            }
                            else {
                                // all flights are in list and the next activity to display all flights can be started
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "something went wrong @ departures");
                    }
                });
    }

/*
    private class FlightStatusByRoute extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... strings) {

            flightList = new ArrayList<>();
            RequestQueue req = Volley.newRequestQueue(getCtx());

            JsonObjectRequest reqString = new JsonObjectRequest
                    (Request.Method.GET, reqURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject flightStatusResource = response.getJSONObject("FlightStatusResource");
                                JSONObject flights = flightStatusResource.getJSONObject("Flights");
                                JSONArray flight = flights.getJSONArray("Flight");

                                for (int indexParser = 0; indexParser<flight.length(); indexParser++) {
                                    Log.d(TAG, "Flight: " + indexParser);
                                    Flight flightPicker;

                                    JSONObject flightPicked = flight.getJSONObject(indexParser);
                                    JSONObject departure = flightPicked.getJSONObject("Departure");
                                    JSONObject equipment = flightPicked.getJSONObject("Equipment");
                                    JSONObject status = flightPicked.getJSONObject("FlightStatus");
                                    JSONObject scheduledDepTimeLocal = departure.getJSONObject("ScheduledTimeLocal");
                                    if(departure.optJSONObject("Terminal") == null)
                                        depGate = null;
                                    else {
                                        JSONObject depTerminal = departure.getJSONObject("Terminal");
                                        depGate = depTerminal.optString("Gate", "unknown");
                                    }
                                    JSONObject arrival = flightPicked.getJSONObject("Arrival");
                                    if(arrival.optJSONObject("Terminal") == null)
                                        arrGate = null;
                                    else {
                                        JSONObject arrTerminal = arrival.getJSONObject("Terminal");
                                        arrGate = arrTerminal.optString("Gate", "unknown");
                                    }
                                    JSONObject scheduledArrTimeLocal = arrival.getJSONObject("ScheduledTimeLocal");
                                    JSONObject flightNo = flightPicked.getJSONObject("OperatingCarrier");

                                    depAirportCode = (String) departure.get("AirportCode"); // depCode
                                    arrAirportCode = (String) arrival.get("AirportCode"); // arrCode
                                    operator = (String) flightNo.get("AirlineID");
                                    number =  flightNo.get("FlightNumber").toString();
                                    depTimePlanned = (String) scheduledDepTimeLocal.get("DateTime"); // planned dep Time
                                    depTimePlanned = getTime(depTimePlanned);
                                    arrTimePlanned = (String) scheduledArrTimeLocal.get("DateTime"); // planned dep Time
                                    arrTimePlanned = getTime(arrTimePlanned);
                                    airCraftCode = equipment.getString("AircraftCode");
                                    statusOfFlight = status.getString("Definition");

                                    flightPicker = new Flight(depAirportCode, arrAirportCode, operator, number);
                                    flightPicker.setGate(depGate, arrGate);
                                    flightPicker.setAirCraftCode(airCraftCode);
                                    flightPicker.setStatus(statusOfFlight);

                                    JSONObject estimatedDepTimeLocal;
                                    if(departure.optJSONObject("EstimatedTimeLocal") != null)
                                        estimatedDepTimeLocal = departure.getJSONObject("EstimatedTimeLocal");
                                    else
                                        estimatedDepTimeLocal = departure.optJSONObject("ActualTimeLocal");

                                    if(estimatedDepTimeLocal != null)
                                        depTimeAct = estimatedDepTimeLocal.optString("DateTime", "unknown");
                                    else
                                        depTimeAct = null;

                                    if(depTimeAct != null)
                                        depTimeAct = getTime(depTimeAct);

                                    JSONObject estimatedArrTimeLocal;
                                    if(arrival.optJSONObject("EstimatedTimeLocal") != null)
                                        estimatedArrTimeLocal = arrival.getJSONObject("EstimatedTimeLocal");
                                    else
                                        estimatedArrTimeLocal = arrival.optJSONObject("ActualTimeLocal");

                                    if(estimatedArrTimeLocal != null)
                                        arrTimeAct = estimatedArrTimeLocal.optString("DateTime", "unknown");
                                    else
                                        arrTimeAct = null;

                                    if(arrTimeAct != null)
                                        arrTimeAct = getTime(arrTimeAct);

                                    if(depTimeAct != null) {
                                        if(arrTimeAct != null)
                                            flightPicker.setTimes(depTimePlanned, arrTimePlanned, depTimeAct, arrTimeAct);
                                        else
                                            flightPicker.setTimes(depTimePlanned, arrTimePlanned, depTimeAct);
                                    } else
                                        flightPicker.setTimes(depTimePlanned, arrTimePlanned);

                                    flightPicker.setTimes(depTimePlanned, arrTimePlanned, depTimeAct, arrTimeAct);

                                    flightList.add(flightPicker);
                                }
                                Log.d(TAG, "All Flights extracted");
                                startFlightOverViewActivity();
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
                    header.put("Authorization", "Bearer " + getToken().getAccess_token());

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

        private String getTime(String depTimePlanned) {
            String[] strTime = depTimePlanned.split("T");
            return strTime[1];
        }
    }
*/


/**
 * instead of AsyncTask "AirportInfo", there is a new implementation inside ApiService "getAirport" method
  */
/*    private class AirportInfo extends AsyncTask<String, Integer, Integer> {
        protected Integer doInBackground(String... strings) {

            RequestQueue req = Volley.newRequestQueue(getCtx());

            String req1 = "https://api.lufthansa.com/v1/mds-references/airports/" +strings[0] + "?lang=DE";
            String req2 = "https://api.lufthansa.com/v1/mds-references/airports/" +strings[1] + "?lang=DE";

            JsonObjectRequest req1String = new JsonObjectRequest
                    (Request.Method.GET, req1, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String res = response.toString();
                                System.out.println(res);
                                JSONObject airportResource = response.getJSONObject("AirportResource");
                                JSONObject airports = airportResource.getJSONObject("Airports");
                                JSONObject airport = airports.getJSONObject("Airport");
                                JSONObject names = airport.getJSONObject("Names");
                                JSONObject name = names.getJSONObject("Name");

                                depAirportName = name.getString("$");
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
                    header.put("Authorization", "Bearer " + getToken().getAccess_token());

                    return header;
                }
            };

            JsonObjectRequest req2String = new JsonObjectRequest
                    (Request.Method.GET, req2, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject airportResource = response.getJSONObject("AirportResource");
                                JSONObject airports = airportResource.getJSONObject("Airports");
                                JSONObject airport = airports.getJSONObject("Airport");
                                JSONObject names = airport.getJSONObject("Names");
                                JSONObject name = names.getJSONObject("Name");
                                arrAirportName = name.getString("$");

                                startFlightResultActivity();
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
                    header.put("Authorization", "Bearer " + getToken().getAccess_token());

                    return header;
                }
            };

            req.add(req1String);
            req.add(req2String);

            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }
    }

     */
}
