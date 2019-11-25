package com.example.lufthansa.MainFragments.Fragments;


import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.lufthansa.APIObjects.Airlines.AirlineList;
import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.ApiFlightResults;
import com.example.lufthansa.APIObjects.Cities.CityList;
import com.example.lufthansa.ApiService;
import com.example.lufthansa.CarrierRadioDialog;
import com.example.lufthansa.DataServices;
import com.example.lufthansa.DatePickerFragment;
import com.example.lufthansa.EventListener;
import com.example.lufthansa.MainFragments.FragmentCollection;
import com.example.lufthansa.R;
import com.example.lufthansa.StartPage;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FlightStatusFragment extends Fragment {

    private String carrier;
    private EventListener listener;
    private static final String TAG = FlightStatusFragment.class.getSimpleName();
    private Calendar today = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private String date;
    private String[] hintAirportList;
    public static TextInputEditText ddate, selectedCarrier;
    EditText to, from, flightNumber;
    TextInputEditText flightNoInpField;
    private int typeOfRequest;
    String[] paramsForFlightSearch;
    View view;

    private AppCompatImageButton flightSearchDate, carrierSelect;
    private AppCompatAutoCompleteTextView autoCompleteDep, autoCompleteArr;

    String[] carriers = {"LH", "EW", "LX", "OS"};
    int logos[] = {R.drawable.lh_logo, R.drawable.ew_logo_new, R.drawable.swiss_logo_new, R.drawable.os_logo_new};

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EventListener) {
            listener = (EventListener) activity;
        } else
            Log.e(TAG, "Error");
    }

    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getTodaysDate();
        createHintAirportList();

        view = inflater.inflate(R.layout.fragment_flight_status_search_wdw, container,false);

        /**

         This part is not necessary until there is no spinner anymore

        Spinner spinner = view.findViewById(R.id.selectedCarrier);
        MyAdapter adapter = new MyAdapter(getContext(), logos, carriers);
        spinner.setAdapter(adapter);
        **/

        autoCompleteDep = view.findViewById(R.id.flightSearchDeparture);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, hintAirportList);
        autoCompleteDep.setThreshold(2);

        autoCompleteArr = view.findViewById(R.id.flightSearchArrival);
        autoCompleteArr.setThreshold(2);

        autoCompleteDep.setAdapter(arrayAdapter);
        autoCompleteArr.setAdapter(arrayAdapter);

        flightNoInpField = view.findViewById(R.id.flightSearchFlightNumber);

        // if not null dep and arr field are disabled
        flightNoInpField.addTextChangedListener(new TextWatcher() {
            Drawable colorDisabled = getContext().getDrawable(R.color.lhGray2);
            Drawable colorEnabled = getContext().getDrawable(R.color.lhSilver);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>0) {
                    autoCompleteDep.setEnabled(false);
                    autoCompleteArr.setEnabled(false);
                    autoCompleteDep.setBackground(colorDisabled);
                    autoCompleteArr.setBackground(colorDisabled);
                } else {
                    autoCompleteDep.setEnabled(true);
                    autoCompleteArr.setEnabled(true);
                    autoCompleteDep.setBackground(colorEnabled);
                    autoCompleteArr.setBackground(colorEnabled);
                }
            }
        });


        // listens to changes within dep input field and changes the textview for flightno to enabled or disabled
        // depends on input within dep field
        autoCompleteDep.addTextChangedListener(new TextWatcher() {
            Drawable colorDisabled = getContext().getDrawable(R.color.lhGray2);
            Drawable colorEnabled = getContext().getDrawable(R.color.lhSilver);

            View flightNoView = view.findViewById(R.id.flightSearchFlightNumber);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "inside aftertextchanged");
                if(s.length()>0) {
                    flightNoView.setEnabled(false);
                    flightNoView.setBackground(colorDisabled);
                }
                else {
                    flightNoView.setEnabled(true);
                    flightNoView.setBackground(colorEnabled);
                }
            }
        });

        assignEditTextFields(view);

        return view;
    }

    private void getTodaysDate() {
        date = dateFormat.format(today.getTime());
    }

    private void assignEditTextFields(View view){
        ddate = view.findViewById(R.id.flightSearchDate);
        ddate.setText(date);

        selectedCarrier = view.findViewById(R.id.flightCarrier);

        // declare behavior for onClick on button
        Button searchButton = view.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(v -> {
            Log.d(TAG, "Button is clicked");

            /**
             * 1st check wether flight route or flight nummber is selected
             */
            if((selectedCarrier.getText().toString().equals("") || flightNoInpField.getText().toString().equals(""))
            && (autoCompleteDep.getText().toString().equals("") || autoCompleteArr.getText().toString().equals("")))
                popupErrorMessage();
            else {
                /**
                 * evaluate if route or flightnumber - depending on we call flight info or status
                 * 1 is for route - 0 for flight number
                 **/
                if(flightNoInpField.getText().toString().equals(""))
                    startRouteSearch();
                else
                    startNumberSearch();
            }


            /**    We set the code completely new
            String requestDate = formatDate(ddate.getText().toString());
            String request = createRequest(carrier, flightNumber.getText().toString(), from.getText().toString(), to.getText().toString(), requestDate);
            //fetch AccessTokenObject for req usage
            String url = "https://api.lufthansa.com/v1/operations/flightstatus" + request;
            Log.d(TAG, "URL: " + url);
            sendDataToActivity(url);
             **/
        });


        // behavior for date search button
        flightSearchDate = view.findViewById(R.id.buttoncalendar);
        flightSearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new DatePickerFragment();
                DatePickerFragment.caller = "FS";
                dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        carrierSelect = view.findViewById(R.id.buttoncarrier);
        carrierSelect.setOnClickListener(v -> {
            CarrierRadioDialog dialog = new CarrierRadioDialog();
            dialog.show(getActivity().getSupportFragmentManager(), "carrierPicker");
        });

        flightNumber = view.findViewById(R.id.flightSearchFlightNumber);
        from =  view.findViewById(R.id.flightSearchDeparture);
        to =  view.findViewById(R.id.flightSearchArrival);
        //selectCarrier = view.findViewById(R.id.selectedCarrier);
//        selectCarrier.setOnItemSelectedListener(this);
    }



    private String formatDate(String date2) {
        Log.d(TAG, "date is: " + date2);
        String[] splittedDate = date2.split("\\.");
        Log.d(TAG, "splittedDate is: " + splittedDate[0]);

        return splittedDate[2] + "-" + splittedDate[1] + "-" + splittedDate[0];
    }

    private void createHintAirportList() {

        int iterations = AirportList.airports.length;
        hintAirportList = new String[iterations];

        String airportCode, airportCityCode, airportCityName;

        int iterator = 0;

        while( iterator < iterations ) {

            airportCode = AirportList.airports[iterator].getAirportCode();
            airportCityCode = AirportList.getAirportCityCode(airportCode);
            airportCityName = CityList.getCityName(airportCityCode);

            hintAirportList[iterator] = airportCityName + ", " + airportCode;

            ++iterator;
        }
    }


    // if values dont match after pushed on Search Button
    private void popupErrorMessage() {
        Toast.makeText(getContext(), "", Toast.LENGTH_LONG).show();
    }

    // search for flight number
    private void startNumberSearch() {
        String carrier = AirlineList.getCode(selectedCarrier.getText().toString());
        String flightNo = carrier + flightNoInpField.getText().toString();
        String date = formatDate(ddate.getText().toString());
        String token = "Bearer " + StartPage.access_token.getAccess_token();

        String[] paramsForRetrofit = new String[] {flightNo, date, token};

        doRetroFitFlightSearch(paramsForRetrofit);
    }


    /**
     * receives the query parameter for route or number search and fetches the data
     * @param params - search for flightnumber requires 3 params -> flightNo, date, token
     *                 or search for route -> origin, destination, date, token
     */
    private void doRetroFitFlightSearch(String[] params) {

        Log.d(TAG, "Before retrofit build");

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Log.d(TAG, "After build");

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "FlightNumberParams Params 1: " +  params[0] + "\nParams2: " + params[1]);

        Single<Response<ApiFlightResults>> retrofitFlight;

        if(params.length == 3) {
            retrofitFlight = service.getFlightByFlightNumber(
                    params[0], params[1], params[2], "application/json"
            );


            retrofitFlight.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<ApiFlightResults>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                        }

                        @Override
                        public void onSuccess(Response<ApiFlightResults> apiFlightResultsResponse) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSuccess");
                            //JSONObject object = jsonObjectResponse.body();
                            Log.d(TAG, "response body: " + apiFlightResultsResponse.body());
                            Log.d(TAG, "response resp code: " + apiFlightResultsResponse.code());
                            Log.d(TAG, "response header: " + apiFlightResultsResponse.headers());
                            Log.d(TAG, "raw response: " + apiFlightResultsResponse.raw());
                            if (!apiFlightResultsResponse.isSuccessful()) {
                                try {
                                    String resp = apiFlightResultsResponse.errorBody().string();
                                    Log.d(TAG, "errorBody: " + resp);
                                    Log.d(TAG, "Response: " + apiFlightResultsResponse.raw().networkResponse().toString());
                                } catch (IOException exc) {
                                    exc.printStackTrace();
                                }
                            }
                            if (apiFlightResultsResponse.body() != null) {
                                if (DataServices.extractFlight(apiFlightResultsResponse.body().getFlightStatusResource(), 1, getContext()) == 1) {
                                    openFlightNumberFragment();
                                }
                            } else {
                                Toast warningNoFlight = Toast.makeText(getContext(), "No Results!", Toast.LENGTH_LONG);
                                warningNoFlight.show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                            e.printStackTrace();
                        }
                    });
        }

        else if(params.length == 4) {
            retrofitFlight = service.getFlightsByRoute(
                    params[0], params[1], params[2], params[3], "application/json"
            );

            retrofitFlight.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Response<ApiFlightResults>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                        }

                        @Override
                        public void onSuccess(Response<ApiFlightResults> apiFlightResultsResponse) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSuccess");
                            //JSONObject object = jsonObjectResponse.body();
                            Log.d(TAG, "response body: " + apiFlightResultsResponse.body());
                            Log.d(TAG, "response resp code: " + apiFlightResultsResponse.code());
                            Log.d(TAG, "response header: " + apiFlightResultsResponse.headers());
                            Log.d(TAG, "raw response: " + apiFlightResultsResponse.raw());
                            if (!apiFlightResultsResponse.isSuccessful()) {
                                try {
                                    String resp = apiFlightResultsResponse.errorBody().string();
                                    Log.d(TAG, "errorBody: " + resp);
                                    Log.d(TAG, "Response: " + apiFlightResultsResponse.raw().networkResponse().toString());
                                } catch (IOException exc) {
                                    exc.printStackTrace();
                                }
                            }
                            if (apiFlightResultsResponse.body() != null) {
                                if (DataServices.extractFlights(apiFlightResultsResponse.body().getFlightStatusResource(), getContext()) == 1) {
                                    openFLightRouteFragment();
                                }
                            } else {
                                Toast warningNoFlight = Toast.makeText(getContext(), "No Results!", Toast.LENGTH_LONG);
                                warningNoFlight.show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                            e.printStackTrace();
                        }
                    });
        }
    }

    // open flight search info
    private void openFlightNumberFragment() {
        FragmentCollection parent = (FragmentCollection) FlightStatusFragment.this.getParentFragment();
        parent.navigateToFlightInfoResult(getView());
    }


    // open the view to show the flight route results
    private void openFLightRouteFragment() {
        FragmentCollection parent = (FragmentCollection) FlightStatusFragment.this.getParentFragment();
        parent.navigateToFlightSearchResults(getView(), ddate.getText().toString());
    }


    // open search results
    private void startRouteSearch() {
        String origin = autoCompleteDep.getText().toString();       // departure
        String[] originCode = origin.split(", ");
        origin = originCode[1];

        String destination = autoCompleteArr.getText().toString();  // arrival
        String[] destCode = destination.split(", ");
        destination = destCode[1];

        String date = formatDate(ddate.getText().toString());
        String token = "Bearer " + StartPage.access_token.getAccess_token();

        String[] paramsForRetrofit = new String[] {origin, destination, date, token};

        doRetroFitFlightSearch(paramsForRetrofit);
    }
}
