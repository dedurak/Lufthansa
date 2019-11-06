package com.example.lufthansa.MainFragments.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.lufthansa.R;

public class DepartureSearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle svdInstState) {
        return inflater.inflate(R.layout.departure_search, container, false);
    }
}
