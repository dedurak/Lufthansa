package com.eduvation.lufthansa;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eduvation.lufthansa.APIObjects.Flight;
import com.eduvation.lufthansa.MainFragments.Fragments.FlightInfoItemClickListener;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class FlightInfoFragment extends Fragment implements FlightInfoItemClickListener {

    private final String TAG = FlightInfoFragment.class.getSimpleName();

    // vars later in use @onCreateView then creating recyclerview
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String searchDate;

    // override onCreate to hide the toolbar from the activity and extend the height of the nav component
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        searchDate = getArguments().getString("date");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flight_info, container, false);

        /** *
         *
         * here we declare the recyclerview
         */
        recyclerView = view.findViewById(R.id.recycleFlights);
        recyclerView.setHasFixedSize(true);

        // use layout manager
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // the data to load into the recyclerview adapter
        ArrayList<Flight> data = DataServices.getFlightList();

        // create adapter
        adapter = new FlightInfoAdapter(getContext(), this, data);
        recyclerView.setAdapter(adapter);

        Flight flightForTopBar = data.get(0);
        TextView depCode = view.findViewById(R.id.flightRouteTopBarDepCode);
        depCode.setText(flightForTopBar.getDepCode());

        TextView arrCode = view.findViewById(R.id.flightRouteTopBarArrCode);
        arrCode.setText(flightForTopBar.getArrCode());

        TextView date = view.findViewById(R.id.flightRouteTopBarDate);
        date.setText(searchDate);

        TextView countFlights = view.findViewById(R.id.flightRouteTopBarCountResults);
        String stringForCountOfFlights = "Flights found: " + data.size();
        countFlights.setText(stringForCountOfFlights);

        return view;
    }

    @Override
    public void recyclerClickListener(View v, View transition, int pos) {
        // change view to numberInfoFragment
        Bundle bundle = new Bundle();
        bundle.putString("index", Integer.toString(pos));
        bundle.putString("from", "flightInfo");

        FragmentNavigator.Extras.Builder extras = new FragmentNavigator.Extras.Builder();
        extras.addSharedElement(transition, transition.getTransitionName());
        FragmentNavigator.Extras build = extras.build();

        Log.d(TAG, "inside recyclerclicklistener, transition: " + transition.getId() + " "+ transition.getTransitionName());

        Navigation.findNavController(v).navigate(R.id.action_flightInfo_to_flightNumberInfoFragment, bundle, null, build);
    }
}
