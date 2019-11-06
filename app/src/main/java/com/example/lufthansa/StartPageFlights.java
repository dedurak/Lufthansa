package com.example.lufthansa;

import android.app.Application;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lufthansa.APIObjects.ApiDepartures;
import com.example.lufthansa.APIObjects.TimeTableFlight;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

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
        // Inflate the layout for this fragment
        getFlights();
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

    private void getFlights() {
        Log.d(TAG, "Date is " + date);
        reqUrl = "https://api.lufthansa.com/v1/operations/flightstatus/departures/FRA/" + date;

        findTimeTableFlights("FRA");
        //new FindFlights().execute(reqUrl);
    }

    private void addFlightsToLayout() {
        LinearLayout mainLayout = getActivity().findViewById(R.id.flightsInsideStartPageLayout);
        View childLayout;

        List<TimeTableFlight> departures = DataServices.getDepartures();
        ListIterator iterateThroughList = departures.listIterator();

        int index = 0;

        ProgressBar progressBar = mainLayout.findViewById(R.id.progression);
        progressBar.setVisibility(View.GONE);

        while(index<10 && iterateThroughList.hasNext()){
            childLayout = this.getLayoutInflater().inflate(R.layout.timetable_flight, null);

            TextView flightNo = childLayout.findViewById(R.id.flightInsideTimeTable_flightNo);
            TextView destination = childLayout.findViewById(R.id.flightInsideTimeTable_destination);
            TextView time = childLayout.findViewById(R.id.flightInsideTimeTable_time);
            TextView status = childLayout.findViewById(R.id.flightInsideTimeTable_status);

            flightNo.setText(departures.get(index).getFlightNo());
            Log.d(TAG, departures.get(index).getFlightNo());
            destination.setText(departures.get(index).getDestination());
            time.setText(departures.get(index).getDepTimePlanned());
            status.setText(departures.get(index).getFlightStatus());

            Log.d(TAG, "Index: " + index);

            mainLayout.addView(childLayout);
            ++index;
        }
    }

    private void findTimeTableFlights(String origin) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "Origin: " + origin);
        Log.d(TAG, "Date: " + date);
        Log.d(TAG, "Bearer " + StartPage.access_token.getAccess_token());

        Single<retrofit2.Response<ApiDepartures>> response = service.getDepartures(
                origin,
                date,
                "Bearer " + StartPage.access_token.getAccess_token());

        response.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<retrofit2.Response<ApiDepartures>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "inside StartPageFlights - onSubscribe");
                    }

                    @Override
                    public void onSuccess(retrofit2.Response<ApiDepartures> jsonObjectResponse) {
                        Log.d(TAG, "inside StartPageFlights - onSuccess");
                        //Log.d(TAG, "received object: " + jsonObjectResponse.body().toString());
                        //if(DataServices.extractFlights(jsonObjectResponse.body()) == 1)
                          //  addFlightsToLayout();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "inside StartPageFlights - onError");
                    }
                });
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
