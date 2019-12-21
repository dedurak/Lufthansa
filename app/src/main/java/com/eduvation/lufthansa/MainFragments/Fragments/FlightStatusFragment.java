package com.eduvation.lufthansa.MainFragments.Fragments;


import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.eduvation.lufthansa.APIObjects.Airlines.AirlineList;
import com.eduvation.lufthansa.APIObjects.Airports.AirportList;
import com.eduvation.lufthansa.APIObjects.ApiFlightResults;
import com.eduvation.lufthansa.APIObjects.Cities.CityList;
import com.eduvation.lufthansa.ApiService;
import com.eduvation.lufthansa.CarrierRadioDialog;
import com.eduvation.lufthansa.DataServices;
import com.eduvation.lufthansa.DatePickerFragment;
import com.eduvation.lufthansa.MainFragments.FragmentCollection;
import com.eduvation.lufthansa.R;
import com.eduvation.lufthansa.SharedAirportData;
import com.eduvation.lufthansa.StartPage;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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

    private static final String TAG = FlightStatusFragment.class.getSimpleName();
    private Calendar today = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private String date;
    private String[] hintAirportList;
    public static TextInputEditText ddate, selectedCarrier;
    private ImageButton depBtn, arrBtn;
    EditText to, from, flightNumber;
    TextInputEditText flightNoInpField;
    View view;

    private AppCompatImageButton flightSearchDate, carrierSelect;
    private AppCompatAutoCompleteTextView autoCompleteDep, autoCompleteArr;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        SharedAirportData data;
        data = ViewModelProviders.of(getActivity()).get(SharedAirportData.class);

        data.getSelected().observe(this, s -> {
            if(StartPage.mode == 1)
                autoCompleteArr.setText(s);
            else if(StartPage.mode == 0)
                autoCompleteDep.setText(s);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getTodaysDate();

        view = inflater.inflate(R.layout.fragment_flight_status_search_wdw, container,false);

        depBtn = view.findViewById(R.id.depAirportSearchButton);
        depBtn.setOnClickListener(l -> {
            AirportTextInput.field = 0;
            FragmentCollection parent = (FragmentCollection) getParentFragment();
            parent.navigateToAirportTextInput(getView(), view.findViewById(R.id.flightSearchArrival));
        });
        arrBtn = view.findViewById(R.id.arrSearchButton);
        arrBtn.setOnClickListener(l -> {
            AirportTextInput.field = 1;
            FragmentCollection parent = (FragmentCollection) getParentFragment();
            parent.navigateToAirportTextInput(getView(), view.findViewById(R.id.flightSearchArrival));
        });

        autoCompleteDep = view.findViewById(R.id.flightSearchDeparture);
        autoCompleteArr = view.findViewById(R.id.flightSearchArrival);

        flightNoInpField = view.findViewById(R.id.flightSearchFlightNumber);

        // if not null dep and arr field are disabled
        flightNoInpField.addTextChangedListener(new TextWatcher() {
            Drawable colorDisabled = getContext().getDrawable(R.drawable.textbackground_disabled);
            Drawable colorEnabled = getContext().getDrawable(R.drawable.textbackground);

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
            Drawable colorDisabled = getContext().getDrawable(R.drawable.textbackground_disabled);
            Drawable colorEnabled = getContext().getDrawable(R.drawable.textbackground);

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

        autoCompleteDep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // view is focuseed

                } else {
                    // focus is gone
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

            view.findViewById(R.id.fsSearchProg).setVisibility(View.VISIBLE);

            /**
             * 1st check wether flight route or flight nummber is selected
             */
            if((selectedCarrier.getText().toString().equals("") || flightNoInpField.getText().toString().equals(""))
            && (autoCompleteDep.getText().toString().equals("") || autoCompleteArr.getText().toString().equals(""))) {
                view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
                popupErrorMessage();
            }
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
        });


        // behavior for date search button
        flightSearchDate = view.findViewById(R.id.ttSearchDateButton);
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
                            Log.d(TAG, "response body: " + apiFlightResultsResponse.body());
                            Log.d(TAG, "response resp code: " + apiFlightResultsResponse.code());
                            Log.d(TAG, "response header: " + apiFlightResultsResponse.headers());
                            Log.d(TAG, "raw response: " + apiFlightResultsResponse.raw());

                            if (apiFlightResultsResponse.body() != null) {
                                if (DataServices.extractFlight(apiFlightResultsResponse.body().getFlightStatusResource(), 1, getContext()) == 1) {
                                    view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
                                    openFlightNumberFragment();
                                }
                            } else {
                                view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
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
                                view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
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
                                    view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
                                    openFLightRouteFragment();
                                }
                            } else {
                                view.findViewById(R.id.fsSearchProg).setVisibility(View.GONE);
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
