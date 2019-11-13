package com.example.lufthansa;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.widget.DatePicker;

import com.example.lufthansa.MainFragments.Fragments.FlightStatusFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // referencese to the class the dialog is going to called from
    /**
     * FS = FlightStatusFragment
     * TTS = TimeTableStatusSearchWdw start
     * TTE = TimeTableStatusSearchWdw end
     */
    public static String caller;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker dp, int year, int month, int day) {
        Log.d("class", "inside onDateset");
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        String date = df.format(cal.getTime());

        // assign the date to the calling textview
        if(caller.equals("FS"))
            FlightStatusFragment.ddate.setText(date);
        else if(caller.equals("TTS"))
            DeparturesSearchWdw.startDate.setText(date);
        else if(caller.equals("TTE"))
            DeparturesSearchWdw.endDate.setText(date);
    }
}
