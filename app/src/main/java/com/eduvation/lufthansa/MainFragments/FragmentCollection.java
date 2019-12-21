package com.eduvation.lufthansa.MainFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.viewpager2.widget.ViewPager2;

import com.eduvation.lufthansa.MainFragments.Fragments.FlightStatusFragment;
import com.eduvation.lufthansa.MainFragments.Fragments.TimeTableSearchFragment;
import com.eduvation.lufthansa.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FragmentCollection extends Fragment {

    private final String TAG = FragmentCollection.class.getSimpleName();

    FragmentsAdapter adapter;
    ViewPager2 pager;

    private int[] iconsSelected = new int[] {
            R.drawable.ic_status_selected_24dp,
            R.drawable.ic_timetable_24dp
    };

    private int[] iconsNons = new int[] {
            R.drawable.ic_status_unselected_24dp,
            R.drawable.ic_timetable_nonselected_24dp
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svdInstState) {
        return inflater.inflate(R.layout.main_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle svdInstState) {
        adapter = new FragmentsAdapter(this);

        adapter.addFragToList(new FlightStatusFragment());
        adapter.addFragToList(new TimeTableSearchFragment());

        pager = view.findViewById(R.id.mainPager);
        pager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "inside tabselected");
                int tabPos = tab.getPosition();
                tab.setIcon(iconsSelected[tabPos]);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.d(TAG, "inside unselected");
                int tabPos = tab.getPosition();
                tab.setIcon(iconsNons[tabPos]);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.d(TAG, "inside reselcted");
            }
        });

        new TabLayoutMediator(tabLayout, pager,
                (tab, position) -> {
                    tab.setText(getTxt(position));
                    tab.setIcon(getIcon(position));
                }).attach();
    }

    private String getTxt(int position) {
        switch(position) {
            case 0: return "STATUS";
            case 1: return "TIMETABLE";
        }
        return "DEFAULT";
    }

    private int getIcon(int position) throws ArrayIndexOutOfBoundsException {
        if(position == 0)
            return iconsSelected[position];
        else
            return iconsNons[position];
    }

    // do transaction to flight info center
    public void navigateToFlightSearchResults(View view, String searchedDate) {
        Log.d(TAG, "try switching to new fragment");
        Bundle bundle = new Bundle();
        bundle.putString("date", searchedDate);
        Navigation.findNavController(view).navigate(R.id.action_fragmentCollection_to_flightInfo, bundle);
    }

    // do transaction to flightnumber info fragment
    public void navigateToFlightInfoResult(View view) {
        Log.d(TAG, "transmitting to flight info fragment");
        Bundle bundle = new Bundle();
        bundle.putString("index", "no"); //are there more than 1 flight ?
        bundle.putString("from", "main");
        Navigation.findNavController(view).navigate(R.id.action_fragmentCollection_to_flightNumberInfoFragment, bundle);
    }

    // do transaction to show the departure or arrival results -- from = 0 is dep, = 1 is arr
    public void navigateToFlightResults(View view, String date, int from, String airportCode) {
        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putInt("from", from);
        bundle.putString("airportCode", airportCode);
        Navigation.findNavController(view).navigate(R.id.action_fragmentCollection_to_flightResultFragment, bundle);
    }

    public void navigateToAirportTextInput(View view, View transition) {
        FragmentNavigator.Extras.Builder extras = new FragmentNavigator.Extras.Builder();
        extras.addSharedElement(transition, transition.getTransitionName());
        FragmentNavigator.Extras build = extras.build();
        Navigation.findNavController(view).navigate(R.id.action_fragmentCollection_to_airportTextInput, null, null, build);
    }
}
