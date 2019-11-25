package com.example.lufthansa;


import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lufthansa.APIObjects.Flight;
import com.example.lufthansa.MainFragments.Fragments.FlightInfoItemClickLisstener;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class FlightInfoFragment extends Fragment implements FlightInfoItemClickLisstener {

    // vars later in use @onCreateView then creating recyclerview
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private String searchDate;

    // override onCreate to hide the toolbar from the activity and extend the height of the nav component
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        hideTopBar();

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
        recyclerView = view.findViewById(R.id.recycleListFlights);
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
    public void onStart() {
        super.onStart();

        // check if top bar is visible - if so, set them again to gone
        if(getActivity().findViewById(R.id.start_toolbar).getVisibility() != View.GONE)
            hideTopBar();
    }

    // hides the elements of the topbar and the topbar itself
    private void hideTopBar() {
        MaterialToolbar toolbar = getActivity().findViewById(R.id.start_toolbar);
        toolbar.setVisibility(View.GONE);

        View view = getActivity().findViewById(R.id.view);
        view.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);

        View fragment = getActivity().findViewById(R.id.navigation_component);
        fragment.setLayoutParams(params);
    }

    @Override
    public void recyclerClickListener(View v, int pos) {
        // change view to numberInfoFragment
        Bundle bundle = new Bundle();
        bundle.putString("index", Integer.toString(pos));
        bundle.putString("from", "flightInfo");
        Navigation.findNavController(v).navigate(R.id.action_flightInfo_to_flightNumberInfoFragment, bundle);
    }
}
