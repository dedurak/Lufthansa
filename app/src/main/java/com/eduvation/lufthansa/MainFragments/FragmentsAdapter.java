package com.eduvation.lufthansa.MainFragments;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentsAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    public final static String TAG = FragmentsAdapter.class.getSimpleName();

    public FragmentsAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        Log.d(TAG, "Position: " + position);

        return fragments.get(position);
    }

    public void addFragToList(Fragment fragment) {
        fragments.add(fragment);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }

}
