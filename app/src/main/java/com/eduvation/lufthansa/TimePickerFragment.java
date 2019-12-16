package com.eduvation.lufthansa;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import com.eduvation.lufthansa.MainFragments.Fragments.TimeTableSearchFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final String TAG = TimePickerFragment.class.getSimpleName();
    public static String caller;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog tpd = new TimePickerDialog(getActivity(), this, hour, minute, true);

        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker tp, int hour, int minute) {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);

        String sMinute = tp.getMinute() < 10 ? "0" + tp.getMinute() : "" + tp.getMinute();

        Log.d(TAG, "hour: " + hour);
        Log.d(TAG, "minute: " + minute);

        if (caller.equals("DS")) {
            String timeToReturn = tp.getHour() + ":" + sMinute;
            TimeTableSearchFragment.startTimeSelected.setText(timeToReturn);
        }
    }
}
