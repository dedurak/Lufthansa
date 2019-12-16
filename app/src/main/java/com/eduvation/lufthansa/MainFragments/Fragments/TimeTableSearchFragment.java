package com.eduvation.lufthansa.MainFragments.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.eduvation.lufthansa.APIObjects.Airports.AirportList;
import com.eduvation.lufthansa.APIObjects.ApiFlights;
import com.eduvation.lufthansa.APIObjects.Cities.CityList;
import com.eduvation.lufthansa.ApiService;
import com.eduvation.lufthansa.DataServices;
import com.eduvation.lufthansa.DatePickerFragment;
import com.eduvation.lufthansa.MainFragments.FragmentCollection;
import com.eduvation.lufthansa.MissingParamsDialog;
import com.eduvation.lufthansa.R;
import com.eduvation.lufthansa.StartPage;
import com.eduvation.lufthansa.TimePickerFragment;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class TimeTableSearchFragment extends Fragment {

    private final String TAG = TimeTableSearchFragment.class.getSimpleName();

    private View view;
    public static TextView startDate;
    public static TextView startTimeSelected;
    private String[] hintAirportList;
    private AutoCompleteTextView ttAirport;
    private RadioGroup tableType;

    private String date;
    private String accessToken;

    private int startHour, startMinute;

    // this int defines which kind of exception the app catchs and shows inside MissingParamsDialog
    public static int information;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svdInstState) {
        view = inflater.inflate(R.layout.timetable_search, container, false);

        assignButtonBehavior(view);

        startDate = view.findViewById(R.id.ttSearchDate);
        startTimeSelected = view.findViewById(R.id.ttSearchStartTime);
        tableType = view.findViewById(R.id.tableType);

        createHintAirportList();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_item, hintAirportList);

        ttAirport = view.findViewById(R.id.ttSearchAirport);
        ttAirport.setThreshold(2);
        ttAirport.setAdapter(arrayAdapter);

        Button ttSearchButton = view.findViewById(R.id.ttSearchButton);
        ttSearchButton.setOnClickListener(v -> {

            view.findViewById(R.id.flightProg).setVisibility(View.VISIBLE);

            // check if values are null
            if (!startTimeSelected.getText().toString().equals("")
                    && !startDate.getText().toString().equals("")
                    && tableType.getCheckedRadioButtonId() != -1
                    && !ttAirport.getText().toString().equals("")) {

                accessToken = "Bearer " + StartPage.access_token.getAccess_token();

                int id = tableType.getCheckedRadioButtonId();
                MaterialRadioButton radioButton = view.findViewById(id);

                String type = null;
                if ( radioButton != null)
                    type = radioButton.getText().toString();

                date = changeDateToCorrectFormat(startDate.getText().toString());

                String rangeStartSplit[] = startTimeSelected.getText().toString().split(":");

                startHour = Integer.parseInt(rangeStartSplit[0]);
                startMinute = Integer.parseInt(rangeStartSplit[1]);

                String ttCode = ttAirport.getText().toString();
                String[] splitter = ttCode.split(", ");
                ttCode = splitter[1];

                String time;

                // create date and time
                if(startHour<10)
                    time = "0" + startHour;
                else
                    time = Integer.toString(startHour);
                if(startMinute<10)
                    time += ":0" + startMinute;
                else
                    time += ":" + startMinute;


                String ovhDateTime = date + "T" + time;

                assert type != null;
                if(type.equals("Departures"))
                    doRetroFitDepSearch(ttCode, ovhDateTime, 0, 100);
                else
                    doRetroFitArrSearch(ttCode, ovhDateTime, 0, 100);
            } else {
                view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                missingParamField();
            }
        });

        return view;
    }

    // sets the behavior for all imagebuttons - date and time picker control
    private void assignButtonBehavior(View view) {
        AppCompatImageButton datePicker = view.findViewById(R.id.ttSearchDateButton);
        AppCompatImageButton startTime = view.findViewById(R.id.ttSearchstartTimeButton);

        // call date picker and select date
        datePicker.setOnClickListener(v -> {
            DialogFragment dialog = new DatePickerFragment();
            DatePickerFragment.caller = "DS";
            dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        // select start time
        startTime.setOnClickListener(v -> {
            TimePickerFragment dialog = new TimePickerFragment();
            TimePickerFragment.caller = "DS";
            dialog.show(getActivity().getSupportFragmentManager(), "timePicker");
        });
    }

    // receives in format dd.mm.yyyy and returns yyyy-mm-dd
    private String changeDateToCorrectFormat(String dateFromInpField) {
        String[] splitDate = dateFromInpField.split("\\.");
        return splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
    }

    private void showHintForNextSearch() {
        information = 1;
        MissingParamsDialog dialog = new MissingParamsDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "showHint");
    }

    // show warning - missing values information for user
    private void missingParamField() {
        information = 2;
        MissingParamsDialog dialog = new MissingParamsDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "missingParams");
    }

    private void doRetroFitDepSearch(String retroDepCode, String dateTime, int retroOffSet, int retroLimit) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "retroDepCode: " + retroDepCode);
        Log.d(TAG, "dateTime: " + dateTime);

        Single<Response<ApiFlights>> retrofitFlight = service.getDepartures(
                retroDepCode, dateTime, retroOffSet, retroLimit, accessToken, "application/json");

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiFlights>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiFlights> apiFlightResultsResponse) {

                        // if receiving httpexception, in ths case there is no "next" link inside meta data to fetch further data
                        if (!apiFlightResultsResponse.isSuccessful()) {
                            // no flights found from start time + 4 hours -> show user dialog to change the start time to + min 4 hours
                            view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                            showHintForNextSearch();
                        } else {
                            if (DataServices.extractDepartures(apiFlightResultsResponse.body().getApiFlightsResource(), 0, getContext()) == 1) {
                                view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                                FragmentCollection parent = (FragmentCollection) getParentFragment();
                                parent.navigateToFlightResults(getView(), date, 0, retroDepCode); // 0 is for departures
                            } else {
                                view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                                // display something went wrong
                                Snackbar snackbar = Snackbar.make(getView(), "Something went wrong!", BaseTransientBottomBar.LENGTH_LONG);
                                snackbar.setAction("Try again", v -> {
                                    doRetroFitDepSearch(retroDepCode, dateTime, retroOffSet, retroLimit);
                                });
                                snackbar.show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                        e.printStackTrace();
                    }
                });
    }

    private void doRetroFitArrSearch(String retroArrCode, String dateTime, int retroOffSet, int retroLimit) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "retroDepCode: " + retroArrCode);
        Log.d(TAG, "dateTime: " + dateTime);

        Single<Response<ApiFlights>> retrofitFlight = service.getArrivals(
                retroArrCode, dateTime, retroOffSet, retroLimit, accessToken, "application/json");

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiFlights>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiFlights> apiFlightResultsResponse) {

                        // if receiving httpexception, in ths case there is no "next" link inside meta data to fetch further data
                        if (!apiFlightResultsResponse.isSuccessful()) {
                            // no flights found from start time + 4 hours -> show user dialog to change the start time to + min 4 hours
                            view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                            showHintForNextSearch();
                        } else {

                            if (DataServices.extractArrivals(apiFlightResultsResponse.body().getApiFlightsResource(), 0, getContext()) == 1) {
                                view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                                FragmentCollection parent = (FragmentCollection) getParentFragment();
                                parent.navigateToFlightResults(getView(), date, 1, retroArrCode); // 1 is for arrivals
                            } else {
                                // display something went wrong
                                view.findViewById(R.id.flightProg).setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar.make(getView(), "Something went wrong!", BaseTransientBottomBar.LENGTH_LONG);
                                snackbar.setAction("Try again", v -> {
                                    doRetroFitArrSearch(retroArrCode, dateTime, retroOffSet, retroLimit);
                                });
                                snackbar.show();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                        e.printStackTrace();
                    }
                });
    }

    private void createHintAirportList() {

        int iterations = AirportList.airports.length;
        hintAirportList = new String[iterations];

        String airportCode, airportCityCode, airportCityName;

        int iterator = 0;

        while (iterator < iterations) {

            airportCode = AirportList.airports[iterator].getAirportCode();
            airportCityCode = AirportList.getAirportCityCode(airportCode);
            airportCityName = CityList.getCityName(airportCityCode);

            hintAirportList[iterator] = airportCityName + ", " + airportCode;

            ++iterator;
        }
    }
}
