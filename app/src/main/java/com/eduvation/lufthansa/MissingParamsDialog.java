package com.eduvation.lufthansa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.eduvation.lufthansa.MainFragments.Fragments.TimeTableSearchFragment;

public class MissingParamsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle svdInstState) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View mainView = inflater.inflate(R.layout.missing_params_dialog, null);

        TextView infoText = mainView.findViewById(R.id.missingParamsInfoField);

        if (TimeTableSearchFragment.information == 2) {
            infoText.setText(R.string.missingParams);
        } else if (TimeTableSearchFragment.information == 1) {
            infoText.setText("No Flights found! Update your values.");
        }
        Button closeButton = mainView.findViewById(R.id.closeMissingWarning);
        closeButton.setOnClickListener(v -> dismiss());

        dialog.setView(mainView);
        return dialog.create();
    }
}

