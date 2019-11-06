package com.example.lufthansa.MainFragments;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.lufthansa.MainFragments.Fragments.ArrivalSearchFragment;
import com.example.lufthansa.MainFragments.Fragments.DepartureSearchFragment;
import com.example.lufthansa.MainFragments.Fragments.FlightSearchFragment;

import java.util.ArrayList;

public class FragmentsAdapter extends FragmentStateAdapter {

    ArrayList<Fragment> fragments = new ArrayList<>();
    public final static String TAG = FragmentsAdapter.class.getSimpleName();


    public FragmentsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d(TAG, "Position: " + position);
        Fragment fragmentToReturn = fragments.get(position);

        return  fragmentToReturn;
    }

    public void addFragToList(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
