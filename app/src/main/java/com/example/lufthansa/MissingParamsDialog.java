package com.example.lufthansa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.lufthansa.MainFragments.Fragments.DepartureSearchFragment;


public class MissingParamsDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle svdInstState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        TextView infoText;

        final View mainView = inflater.inflate(R.layout.missing_params_dialog, null);

        switch (DepartureSearchFragment.information) {
            case 1: { infoText = mainView.findViewById(R.id.missingParamsInfoField);
                        infoText.setText(R.string.wrongFormat); }
            case 2: { infoText = mainView.findViewById(R.id.missingParamsInfoField);
                        infoText.setText(R.string.missingParams); }
        }

        Button closeButton = mainView.findViewById(R.id.closeMissingWarning);
        closeButton.setOnClickListener(v -> dismiss());

        dialog.setView(mainView);

        return dialog.create();
    }
}

