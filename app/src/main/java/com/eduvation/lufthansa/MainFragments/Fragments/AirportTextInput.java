package com.eduvation.lufthansa.MainFragments.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.eduvation.lufthansa.APIObjects.Airports.AirportList;
import com.eduvation.lufthansa.APIObjects.Cities.CityList;
import com.eduvation.lufthansa.R;
import com.eduvation.lufthansa.SharedAirportData;
import com.eduvation.lufthansa.StartPage;

public class AirportTextInput extends Fragment {

    private String[] hintAirportList;
    public static int field; // 1 for arrival - 0 for departure
    private final String TAG = AirportTextInput.class.getSimpleName();
    private SharedAirportData airportData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        airportData = ViewModelProviders.of(getActivity()).get(SharedAirportData.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle) {
        View view = inflater.inflate(R.layout.airport_text_input, null);

        Log.d(TAG, "start airporttextinput");

        createHintAirportList();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, hintAirportList);

        AppCompatAutoCompleteTextView editText = view.findViewById(R.id.airporttextedit);
        editText.setAdapter(arrayAdapter); // set adapter
        editText.setThreshold(1); // show hints after first input
        editText.setFocusable(true); // set view focusable
        editText.requestFocus(); // request focus on view - opens keyboard on start of view

        InputMethodManager imn = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Button ok = view.findViewById(R.id.airportSelectedButton);

        ok.setOnClickListener( v -> {
            String clickedItem = editText.getText().toString();
            StartPage.mode = field;
            airportData.select(clickedItem);
            imn.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            getActivity().onBackPressed();
        });

        return view;
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
}
