package com.example.lufthansa;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.Cities.CityList;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FlightStatusSearchWdw extends Fragment implements AdapterView.OnItemSelectedListener{

    private String carrier;
    private EventListener listener;
    private static final String TAG = FlightStatusSearchWdw.class.getSimpleName();
    private Calendar today = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private String date;
    private String[] hintAirportList;
    static TextInputEditText ddate;
    EditText to, from, flightNumber;
    Spinner selectCarrier;
    private int typeOfRequest;
    String[] paramsForFlightSearch;
    View view;

    private AppCompatImageButton flightSearchDate;

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

        //Spinner spinner = view.findViewById(R.id.selectedCarrier);
        MyAdapter adapter = new MyAdapter(getContext(), logos, carriers);
        //spinner.setAdapter(adapter);

        autoCompleteDep = view.findViewById(R.id.flightSearchDeparture);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, hintAirportList);
        autoCompleteDep.setThreshold(2);

        autoCompleteArr = view.findViewById(R.id.flightSearchArrival);
        autoCompleteArr.setThreshold(2);

        autoCompleteDep.setAdapter(arrayAdapter);
        autoCompleteArr.setAdapter(arrayAdapter);

        TextInputEditText flightNoInpField = view.findViewById(R.id.flightSearchFlightNumber);

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

        Button searchButton = view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String requestDate = formatDate(ddate.getText().toString());
                String request = createRequest(carrier, flightNumber.getText().toString(), from.getText().toString(), to.getText().toString(), requestDate);
                //fetch AccessTokenObject for req usage
                String url = "https://api.lufthansa.com/v1/operations/flightstatus" + request;
                Log.d(TAG, "URL: " + url);
                sendDataToActivity(url);
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

        flightSearchDate = view.findViewById(R.id.buttoncalendar);
        flightSearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new DatePickerFragment();
                DatePickerFragment.caller = "FS";
                dialog.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        flightNumber = view.findViewById(R.id.flightSearchFlightNumber);
        from =  view.findViewById(R.id.flightSearchDeparture);
        to =  view.findViewById(R.id.flightSearchArrival);
        //selectCarrier = view.findViewById(R.id.selectedCarrier);
//        selectCarrier.setOnItemSelectedListener(this);
    }

    /**
     * sends the data back to the activity to send the request
     * @param url
     */
    private void sendDataToActivity(String url) {
        listener.receiveData(typeOfRequest, paramsForFlightSearch);
    }

    /**
     *
     * @param flightNumber - Flightnumber inserted
     * @param from - departure airportTextInputEditText
     * @param to - arrival airport
     * @param date - the date of the flight searched
     * @return - returns the end of request url
     */
    private String createRequest(String carrier, String flightNumber, String from, String to, String date) {
        String request;
        boolean checkVals = date.equals("") || ((from.equals("") || to.equals("")) && flightNumber.equals("") || carrier.equals(""));

        if(checkVals)
            return "";
        else if(flightNumber.equals("")) {
            typeOfRequest = 1;

            String depCode = from.split(", ")[1];
            String arrCode = to.split(", ")[1];

            paramsForFlightSearch = new String[] {depCode, arrCode, date};

            return "/route/" + depCode + "/" + arrCode + "/" + date;
        }
        else {
            typeOfRequest = 0;
            paramsForFlightSearch = new String[] {carrier + flightNumber, date};
            return "/" + carrier + flightNumber + "/" + date;
        }
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

    /**
     * assign selected value to String param carrier
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Object obj = parent.getItemAtPosition(pos);
        carrier = obj.toString();
        Log.d(TAG, "obj.toString(): " + carrier);
    }

    /**
     * set what to do if nothing has been selected and the list is decollapsed
     * @param parent
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //do Nothing
    }
}
