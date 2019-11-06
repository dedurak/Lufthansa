package com.example.lufthansa;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lufthansa.APIObjects.Flight;

import java.util.List;


public class FlightStatusOverviewResult extends AppCompatActivity {

    private static final String TAG = FlightStatusOverviewResult.class.getSimpleName();
    ConstraintLayout layoutIncludingFlights;
    static int pages, countOfPages;
    int size, iterElems; // iterElems -> position of next iterate elem --- size -> size of elements inside list
    List<Flight> queriedFlights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.flights);

        // this layout is the main layout of activity containing all queried flights
        layoutIncludingFlights = findViewById(R.id.flightLayout);

        AppCompatImageButton button = findViewById(R.id.flightoverview_close_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        queriedFlights = DataServices.getFlightList();
        size = queriedFlights.size();

        TextView title = findViewById(R.id.flightoverview_title);
        String buf;

        if(size == 1) {
            buf = "1 Flight found";
            title.setText(buf);
        } else if(size>1) {
            buf = size + " Flights found";
            title.setText(buf);
        }

        int iterator;
        iterElems = 0;
        pages = 1;

        // get iterator ready to prevent including flights not visible inside layout
        if( size > 10 )
            iterator = 10;
        else
            iterator = size;


        // declare countOfPages
        countOfPages = size/10;

        if( ((size>10) && (size%10 != 0)) || countOfPages == 0)
            ++countOfPages;

        while (iterElems < iterator) {
            Flight flight = queriedFlights.get(iterElems);

            View layoutFlight = getLayoutInflater().inflate(R.layout.flight_timetable_item, null);

            TextView indexOfFlight = layoutFlight.findViewById(R.id.indexOfFlight);
            TextView flightNumber = layoutFlight.findViewById(R.id.flightOverviewItem_flightNumber);
            TextView departure = layoutFlight.findViewById(R.id.flightOverviewItem_departure);
            TextView arrival = layoutFlight.findViewById(R.id.flightOverviewItem_arrival);
            TextView depTime = layoutFlight.findViewById(R.id.flightOverviewItem_depTimePlanned);

            indexOfFlight.setText(String.valueOf(iterElems));
            flightNumber.setText(flight.getFlightNumber());
            departure.setText(flight.getDepCode());
            arrival.setText(flight.getArrCode());
            depTime.setText(flight.getPlannedDepartureTime());

            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            margin.topMargin = 5;
            layoutFlight.setLayoutParams(margin);

            LinearLayout view = findViewById(R.id.flightItems);
            view.addView(layoutFlight);

            ++iterElems;
        }

         if (countOfPages > 1 ) {
             findViewById(R.id.backArrow).setVisibility(View.GONE);
             findViewById(R.id.forwardArrow).setVisibility(View.VISIBLE);
             TextView bottomText = findViewById(R.id.pageCounter);
             String tip = "1 - " + countOfPages;
             bottomText.setText(tip);
         }

         else {
            findViewById(R.id.backArrow).setVisibility(View.GONE);
             findViewById(R.id.forwardArrow).setVisibility(View.GONE);
             findViewById(R.id.pageCounter).setVisibility(View.GONE);
         }
    }

    /**
     * if click on one of the results, this method is fired and the detail view of the flight is opened
     */
    public void goToDetailView(View view) {

        // get the index of the clicked view
        TextView getIndex = view.findViewById(R.id.indexOfFlight);

        // create intent and change to new activity
        Intent deTailsViewActivity = new Intent(this, FlightStatusDetailResult.class);
        deTailsViewActivity.putExtra("index", getIndex.getText());
        deTailsViewActivity.putExtra("goto", "overview");
        startActivity(deTailsViewActivity);
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return toStartPage();
    }

    private Intent toStartPage() {
        Intent intent = null;

        if (getIntent().getExtras().getString("goto").equals("startpage")) {
            intent = new Intent(this, StartPage.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        else
            intent = new Intent(this, StartPage.class);

        return intent;
    }

    // jump to next page
    public void nextPage(View view) {

        // clear current items
        LinearLayout layout = findViewById(R.id.flightItems);
        layout.removeAllViews();

        Log.d(TAG, "iterElems start nextPage: " + iterElems);

        int stopper;

        if ( size - iterElems > 10)
            stopper = iterElems+10;
        else
            stopper = size;

        // insert next items
        while (iterElems < stopper) {
            Flight flight = queriedFlights.get(iterElems);

            View layoutFlight = getLayoutInflater().inflate(R.layout.flight_timetable_item, null);

            TextView indexOfFlight = layoutFlight.findViewById(R.id.indexOfFlight);
            TextView flightNumber = layoutFlight.findViewById(R.id.flightOverviewItem_flightNumber);
            TextView departure = layoutFlight.findViewById(R.id.flightOverviewItem_departure);
            TextView arrival = layoutFlight.findViewById(R.id.flightOverviewItem_arrival);
            TextView depTime = layoutFlight.findViewById(R.id.flightOverviewItem_depTimePlanned);

            indexOfFlight.setText(String.valueOf(iterElems));
            flightNumber.setText(flight.getFlightNumber());
            departure.setText(flight.getDepCode());
            arrival.setText(flight.getArrCode());
            depTime.setText(flight.getPlannedDepartureTime());

            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            margin.topMargin = 5;
            layoutFlight.setLayoutParams(margin);

            layout.addView(layoutFlight);

            ++iterElems;
        }

        ++pages;

        // if true, show forward arrow
        if ( (iterElems-size)>10 ) {
            findViewById(R.id.forwardArrow).setVisibility(View.VISIBLE);
            findViewById(R.id.backArrow).setVisibility(View.VISIBLE);
            String tip = pages + " - " + countOfPages;
            TextView textView = findViewById(R.id.pageCounter);
            textView.setText(tip);
        }

        // otherwise show only back button
        else {
            findViewById(R.id.forwardArrow).setVisibility(View.INVISIBLE);
            findViewById(R.id.backArrow).setVisibility(View.VISIBLE);
            String tip = pages + " - " + countOfPages;
            TextView textView = findViewById(R.id.pageCounter);
            textView.setText(tip);
        }

        Log.d(TAG, "iterElems end nextPage: " + iterElems);
    }

    // jump to last page
    public void lastPage(View view) {

        LinearLayout layout = findViewById(R.id.flightItems);
        layout.removeAllViews();

        Log.d(TAG, "iterElems start lastPage: " + iterElems);

        // each time we reach this function there are without eception exactly 10 items to insert
        if(iterElems%10 == 9)
            iterElems -= 10;
        else {
            int substr = (iterElems%10)+1;
            iterElems -= substr;
        }

        int stopper = iterElems - 9;

        while(stopper<=iterElems) {
            Flight flight = queriedFlights.get(stopper);

            View layoutFlight = getLayoutInflater().inflate(R.layout.flight_timetable_item, null);

            TextView indexOfFlight = layoutFlight.findViewById(R.id.indexOfFlight);
            TextView flightNumber = layoutFlight.findViewById(R.id.flightOverviewItem_flightNumber);
            TextView departure = layoutFlight.findViewById(R.id.flightOverviewItem_departure);
            TextView arrival = layoutFlight.findViewById(R.id.flightOverviewItem_arrival);
            TextView depTime = layoutFlight.findViewById(R.id.flightOverviewItem_depTimePlanned);

            indexOfFlight.setText(String.valueOf(iterElems));
            flightNumber.setText(flight.getFlightNumber());
            departure.setText(flight.getDepCode());
            arrival.setText(flight.getArrCode());
            depTime.setText(flight.getPlannedDepartureTime());

            LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            margin.topMargin = 5;
            layoutFlight.setLayoutParams(margin);

            layout.addView(layoutFlight);

            ++stopper;
        }

        --pages;

        if(pages > 1) {
            findViewById(R.id.forwardArrow).setVisibility(View.VISIBLE);
            findViewById(R.id.backArrow).setVisibility(View.VISIBLE);
            String tip = pages + " - " + countOfPages;
            TextView textView = findViewById(R.id.pageCounter);
            textView.setText(tip);
        }
        else {
            findViewById(R.id.forwardArrow).setVisibility(View.VISIBLE);
            findViewById(R.id.backArrow).setVisibility(View.GONE);
            String tip = pages + " - " + countOfPages;
            TextView textView = findViewById(R.id.pageCounter);
            textView.setText(tip);
        }

        ++iterElems;

        Log.d(TAG, "iterElems end lastPage: " + iterElems);
    }
}
