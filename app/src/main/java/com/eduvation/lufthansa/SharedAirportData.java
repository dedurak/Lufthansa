package com.eduvation.lufthansa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedAirportData extends ViewModel {
    private final MutableLiveData<String> selected = new MutableLiveData<>();

    public void select(String text) {
        selected.setValue(text);
    }

    public LiveData<String> getSelected() { return selected; }
}
