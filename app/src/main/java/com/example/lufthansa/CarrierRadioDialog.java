package com.example.lufthansa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.lufthansa.MainFragments.Fragments.FlightStatusFragment;

public class CarrierRadioDialog extends DialogFragment {

    AlertDialog.Builder dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle svdInstState) {
        dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        final View mainView = inflater.inflate(R.layout.carrier_radio, null);

        Button closeButton = mainView.findViewById(R.id.closeCarrierSelect);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button selectButton = mainView.findViewById(R.id.selectButton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assignValue(mainView);
            }
        });

        dialog.setView(mainView);

        return dialog.create();
    }


    // if you press "Select" inside carrier_radio, this is the method invoked to assign the value to the main fragment
    private void assignValue(View view) {
        RadioGroup radioGroup = view.findViewById(R.id.radioGroup);
        int id = radioGroup.getCheckedRadioButtonId();

        RadioButton selectedButton = null;

        if(id>0)
            selectedButton = view.findViewById(id);

        if(selectedButton != null)
            FlightStatusFragment.selectedCarrier.setText(selectedButton.getText().toString());

        dismiss();
    }
}
