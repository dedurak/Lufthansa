package com.example.lufthansa.MainFragments.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.lufthansa.APIObjects.ApiDepartures;
import com.example.lufthansa.APIObjects.ApiFlightResults;
import com.example.lufthansa.ApiService;
import com.example.lufthansa.DataServices;
import com.example.lufthansa.DatePickerFragment;
import com.example.lufthansa.MissingParamsDialog;
import com.example.lufthansa.R;
import com.example.lufthansa.StartPage;
import com.example.lufthansa.TimePickerFragment;

import java.io.IOException;
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

public class DepartureSearchFragment extends Fragment {

    private final String TAG = DepartureSearchFragment.class.getSimpleName();

    public static TextView startDate;
    public static TextView startTimeSelected;
    public static TextView endTimeSelected;

    private String date;

    private int startHour, startMinute;
    public static int endHour, endMinute; // static because DataServices needs to access to

    // this var decides if the query is dependent from start and end or only start time
    private int setting;

    private int diffHour, diffMin;

    // this int defines which kind of exception the app catchs and shows inside MissingParamsDialog
    public static int information;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svdInstState) {
        View view = inflater.inflate(R.layout.departure_search, container, false);

        assignButtonBehavior(view);

        startDate = view.findViewById(R.id.depSearchDate);
        startTimeSelected = view.findViewById(R.id.depSearchStartTime);
        endTimeSelected = view.findViewById(R.id.depSearchEndTime);

        Button depSearchButton = view.findViewById(R.id.depSearchButton);
        depSearchButton.setOnClickListener(v -> {

            // check if values are null
            if (!startTimeSelected.getText().toString().equals("")
                    && !startDate.getText().toString().equals("")) {

                String accessToken = "Bearer " + StartPage.access_token.getAccess_token();
                date = changeDateToCorrectFormat(startDate.getText().toString());
                String rangeStartTime = startTimeSelected.getText().toString();
                String rangeEndTime = "";
                String rangeEndSplit[];

                String rangeStartSplit[] = rangeStartTime.split(":");

                startHour = Integer.parseInt(rangeStartSplit[0]);
                startMinute = Integer.parseInt(rangeStartSplit[1]);

                // if endTime has a value --> start query and show results dependent from end time -- and set setting to 1
                if (!endTimeSelected.getText().toString().equals("")) {
                    setting = 1; // mode 1

                    rangeEndTime = endTimeSelected.getText().toString();
                    rangeEndSplit = rangeEndTime.split(":");
                    endMinute = Integer.parseInt(rangeStartSplit[1]);
                    endHour = Integer.parseInt(rangeEndSplit[0]);

                    if (endHour < startHour || ((endHour == startHour) && (endMinute < startMinute)))
                        showWrongParamsField(view);
                    else {
                        // start with the startrangetime - check if there is a rangeendtime, otherwise fetch all flights starting from the range time on this day
                        String dateTime = date + "T" + rangeStartTime;
                        AppCompatAutoCompleteTextView depAirportCode = view.findViewById(R.id.depSearchAirport);
                        String[] splittedDepCode = depAirportCode.getText().toString().split(", ");
                        String depCode = splittedDepCode[1];

                        int[] diff = calculateDiffHour(startHour, startMinute, endHour, endMinute);

                        diffHour = diff[0];
                        diffMin = diff[1];

                        // start first query
                        doRetroFitDepSearch(depCode, dateTime);
                    }
                } // if endTime not available set setting to 0, the whole class knows further what mode
                else {
                    setting = 0; // mode 0 - without endTime
                }

            } else
                missingParamField(view);
        });

        return view;
    }

    // sets the behavior for all imagebuttons - date and time picker control
    private void assignButtonBehavior (View view) {
        AppCompatImageButton datePicker = view.findViewById(R.id.depSearchDateButton);
        AppCompatImageButton startTime = view.findViewById(R.id.depSearchstartTimeButton);
        AppCompatImageButton endTime = view.findViewById(R.id.depSearchendTimeButton);

        // call date picker and select date
        datePicker.setOnClickListener(v -> {
            DialogFragment dialog = new DatePickerFragment();
            DatePickerFragment.caller = "TTS";
            dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
        });

        // select start time
        startTime.setOnClickListener(v -> {
            TimePickerFragment dialog = new TimePickerFragment();
            TimePickerFragment.timeCaller = "TS";
            dialog.show(getActivity().getSupportFragmentManager(), "timePicker");
        });

        // select end time
        endTime.setOnClickListener(v -> {
            TimePickerFragment dialog = new TimePickerFragment();
            TimePickerFragment.timeCaller = "TE";
            dialog.show(getActivity().getSupportFragmentManager(), "timePicker");
        });
    }

    // receives in format dd.mm.yyyy and returns yyyy-mm-dd
    private String changeDateToCorrectFormat(String dateFromInpField) {
        String[] splitDate = dateFromInpField.split(".");

        return splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
    }


    // opens the missing values dialog window if necessary
    private void showWrongParamsField(View view) {
        information = 1;
        MissingParamsDialog dialog = new MissingParamsDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "missingDialog");
    }

    // show warning - missing values information for user
    private void missingParamField(View view) {
        information = 2;
        MissingParamsDialog dialog = new MissingParamsDialog();
        dialog.show(getActivity().getSupportFragmentManager(), "missingParams");
    }

    // method calculates the time diff between start and end time and returns an int array with hour and min diff
    private int[] calculateDiffHour(int startHour, int startMinute, int endHour, int endMinute) {
        int[] values = new int[2];

        if(endMinute < startMinute) {
            values[0] = endHour - startHour - 1;
            values[1] = 60 - startMinute + endMinute;
        } else if (endMinute >= startMinute) {
            values[0] = endHour - startHour;
            values[1] = endMinute - startMinute;
        }

        return values;
    }

    // startHour + 4 hours
    private void calculateNewStartTime() {
        startHour += 4;
    }

    private void doRetroFitDepSearch(String retroDepCode, String dateTime) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Single<Response<ApiDepartures>> retrofitFlight = service.getDepartures(
                retroDepCode, dateTime, StartPage.access_token.getAccess_token(), "application/json");

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiDepartures>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiDepartures> apiFlightResultsResponse) {

                        // if receiving httpexception, in ths case there is no "next" link inside meta data to fetch further data
                        if(!apiFlightResultsResponse.isSuccessful()) {

                            // if no flights were found check if the diff of start and end time is more then 4 hours, if so continue with start +4hours
                            if (setting == 0) {

                                if (24-startHour <= 4) {
                                    if (DataServices.getDepartures().size() == 0) {
                                        // show message that no flights were found
                                    } else {
                                        // jump to next fragment and show the results found so far
                                    }
                                } else {
                                    // calculate new startTime and include it into dateTime
                                    calculateNewStartTime();

                                    String nTime = Integer.toString(startHour) + ":" + startMinute;

                                    String nDateTime = date + "T" + nTime;
                                    doRetroFitDepSearch(retroDepCode, nDateTime);
                                }
                            }

                            // we have an endtime
                            else if (setting == 1) {

                                if (endHour-startHour <= 4) {
                                    if (DataServices.getDepartures().size() == 0) {
                                        // show error message
                                    } else {
                                        // jump to next fragment to show the flght results
                                    }
                                } else {
                                    calculateNewStartTime();

                                    String nTime = Integer.toString(startHour) + ":" + Integer.toString(startMinute);

                                    String nDateTime = date + "T" + nTime;
                                    doRetroFitDepSearch(retroDepCode, nDateTime);
                                }
                            }
                        }

                        else  {
                            // no end time

                            int whatToDo;

                            if (setting == 0) {
                                if((whatToDo = DataServices.extractDepartures(apiFlightResultsResponse.body().getDeparturesResource(), 0, getContext())) == 1) {
                                    // call next fragment and show the results
                                } else if (whatToDo == 0) {
                                    // next request needs to be done because the end of the day couldnt be reached
                                    startHour = DataServices.getNextHour();
                                    startMinute = DataServices.getNextMinute(); // assign the values of the last dep hour and minute to start

                                    String nHour = Integer.toString(startHour) + ":" + Integer.toString(startMinute);

                                    String nDateTime = date + "T" + nHour;

                                    doRetroFitDepSearch(retroDepCode, nDateTime);
                                }
                            } else if (setting == 1) {
                                if((whatToDo = DataServices.extractDepartures(apiFlightResultsResponse.body().getDeparturesResource(), 1, getContext())) == 1) {
                                    // call next fragment and show the results
                                } else if (whatToDo == 0) {
                                    // next request needs to be done because the end of the day couldnt be reached
                                    startHour = DataServices.getNextHour();
                                    startMinute = DataServices.getNextMinute(); // assign the values of the last dep hour and minute to start

                                    String nHour = Integer.toString(startHour) + ":" + Integer.toString(startMinute);

                                    String nDateTime = date + "T" + nHour;

                                    doRetroFitDepSearch(retroDepCode, nDateTime);
                                }
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
}
