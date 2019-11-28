package com.example.lufthansa;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.DialogFragment;

import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.Cities.CityList;

public class DeparturesSearchWdw extends DialogFragment {

    private final String TAG = DeparturesSearchWdw.class.getSimpleName();

    private String departureAirport;
    private String dateToSearchDepartures;
    private String[] hintAirportList;
    private AppCompatAutoCompleteTextView airportHints;
    private AppCompatSpinner StartHours, depStartMinutes;
    public static AppCompatTextView startDate, endDate, startTime, endTime;
    View mainView;


    // EventListener calls function inside parent
    private EventListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EventListener)
            listener = (EventListener) activity;
        else
            Log.e(TAG, "Error inside onAttach");
    }


    // create dialog and send back to parent to open
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // creates the hint list, displayed as autocomplete...
        createHintAirportList();


        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflate = requireActivity().getLayoutInflater();

        // the layout display as dialog
        mainView = inflate.inflate(R.layout.fragment_departures_search_wdw, null);

        // assign the hint list to the edittext
        assignTheHintListToEditText(mainView);

        // set the first Line -> Departure - Arrival <- the user can change betweeen the two options
        setFirstLineWithDepAndArrTitle(mainView);

        // declare the layout vars
        setTextViewFields(mainView);

        // set the timetable values -> start and end date/time
        setTheTimeTableValues(mainView);

        dialog.setView(mainView);

        return dialog.create();
    }


    // set the timetable start/end date/time
    private void setTheTimeTableValues(View view) {

        // date
        AppCompatImageButton startDateButton = view.findViewById(R.id.ttStartDateButton);
        startDateButton.setOnClickListener(openDateDialog("TTS"));

        // startTime
        AppCompatImageButton startTimeButton = view.findViewById(R.id.ttStartTimeButton);
        startTimeButton.setOnClickListener(openTimeDialog("TS"));

        // endTime
        AppCompatImageButton endTimeButton = view.findViewById(R.id.ttEndTimeButton);
        endTimeButton.setOnClickListener(openTimeDialog("TE"));
    }

    // assigns the hint list to the edittext
    private void assignTheHintListToEditText(View view) {
        airportHints = view.findViewById(R.id.departuresAirport);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, hintAirportList);
        airportHints.setThreshold(2);
        airportHints.setAdapter(adapter);
    }

    private void setTextViewFields(View view) {
        startDate = view.findViewById(R.id.ttStartDate);
        startTime = view.findViewById(R.id.ttStartTime);
        endTime = view.findViewById(R.id.ttEndTime);
    }

    // sets the first line of dialog displayed
    private void setFirstLineWithDepAndArrTitle(View view) {
        AppCompatTextView depTitle = view.findViewById(R.id.depTitle);
        depTitle.setOnClickListener(titleOnClickListener());

        AppCompatTextView arrTitle = view.findViewById(R.id.arrTitle);
        arrTitle.setOnClickListener(titleOnClickListener());
    }

    // return the datepicker
    private View.OnClickListener openDateDialog(final String caller) {

        return new View.OnClickListener() {
            @NonNull
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new DatePickerFragment();
                DatePickerFragment.caller = caller;
                dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        };
    }

    private View.OnClickListener openTimeDialog(final String caller) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new TimePickerFragment();
                TimePickerFragment.timeCaller = caller;
                dialog.show(getActivity().getSupportFragmentManager(), "timepicker");
            }
        };
    }

    // change between the two options dep and arr the title and so the mode you are searching for
    private View.OnClickListener titleOnClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int white = getResources().getColor(R.color.white, null);
                int blue = getResources().getColor(R.color.lhBlue4, null);

                /**
                 * check the color and set if searching for departure or arrival
                 */
                if(v instanceof AppCompatTextView) {

                    AppCompatTextView textView = (AppCompatTextView) v;
                    if(textView.getText().equals("Departures")) {
                        textView.setTextColor(white);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        AppCompatTextView arrTextView = mainView.findViewById(R.id.arrTitle);
                        arrTextView.setTextColor(blue);
                        arrTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    } else {
                        textView.setTextColor(white);
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        AppCompatTextView depTextView = mainView.findViewById(R.id.depTitle);
                        depTextView.setTextColor(blue);
                        depTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    }

                }
            }
        };

        return clickListener;
    }

    // function is called by searchForDepButton -> starts searching
    public void pressedOnSearchButton(View view) {

        AppCompatAutoCompleteTextView depTextField = view.findViewById(R.id.departuresAirport);
        AppCompatTextView ttStartDate = view.findViewById(R.id.ttStartDate);
        AppCompatTextView ttStartTime = view.findViewById(R.id.ttStartTime);
        AppCompatTextView ttEndTime = view.findViewById(R.id.ttEndTime);

        String depCode = getDepCode( depTextField.getText().toString());
        String startDate = changeDateToCorrectFormat(ttStartDate.getText().toString());
        String startTime = ttStartTime.getText().toString();
        String endTime = ttEndTime.getText().toString();

        String searchQuery = startDate + "T" + startTime;

        String[] sendData = new String[] {
                depCode, // where?
                searchQuery, // from dateTime
                endTime // is not used for the query but signals if stop with fetching data
        };

        /**
         *
         * INSERT ----------> checker, who checks if all data are inserted as expected
         *
         * **/
        listener.receiveData(2, sendData);
    }


    // function is calld by closeDepSearch
    public void pressedOnCancelButton(View view) {

    }

    // creates the array and assigns all values to created String array var hintAirportLiist
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


    // receives the input result from depSearchField and returns the departure code
    private String getDepCode(String result) {
        String[] splitResult = result.split(", ");

        return splitResult[1];
    }


    // receives the date from inp field of search field and changes format for query
    private String changeDateToCorrectFormat(String dateFromInpField) {
        String[] splitDate = dateFromInpField.split(".");

        return splitDate[2] + "-" + splitDate[1] + "-" + splitDate[0];
    }
}
