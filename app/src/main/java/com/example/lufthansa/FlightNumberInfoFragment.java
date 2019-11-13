package com.example.lufthansa;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.lufthansa.APIObjects.Aircrafts.AircraftList;
import com.example.lufthansa.APIObjects.Airlines.AirlineList;
import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.Cities.CityList;
import com.example.lufthansa.APIObjects.Flight;

import kotlin.jvm.JvmOverloads;

public class FlightNumberInfoFragment extends Fragment {

    Flight flight;


    // in onCreate we fetch the flight we want to display the informations in view
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        String index = getArguments().getString("index");

        // look if there is an index. this is the activity has been invoked from overview result
        if(index != null) {
            flight = DataServices.getFlightFromList(Integer.parseInt(index));
        } else {
            flight = DataServices.getFlight();
        }
    }

    // here the fetched infos are assigned to the textviews
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.flight_status_cardview_result, container, false);

        /**
         * *      the following lines set the values of the layout
        */

        TextView flightNumber = view.findViewById(R.id.flightnumber);
        flightNumber.setText(flight.getFlightNumber());
        //flightNumber.setText(intent.getStringExtra("flightNumber"));

        // find and load logo
        ImageView logo = view.findViewById(R.id.airlineLogo);
        Drawable drawable = AirlineList.getLogo(flight.getOperator(), getContext());
        if(drawable != null)
            logo.setImageDrawable(drawable);


        TextView depTimePlanned = view.findViewById(R.id.plannedDepTimeLayout);
        depTimePlanned.setText(flight.getPlannedDepartureTime());
        //depTimePlanned.setText(intent.getStringExtra("depTimePlanned"));

        TextView depTimeActual = view.findViewById(R.id.actualDepTimeLayout);
        depTimeActual.setText(flight.getEstimatedDepartureTime());;
        //depTimeActual.setText(intent.getStringExtra("depTimeEst"));;

        TextView depTimeStatus = view.findViewById(R.id.depTimeStatus);
        depTimeStatus.setText(flight.getDepartureTimeStatus());

        TextView arrTimeStatus = view.findViewById(R.id.arrTimeStatus);
        arrTimeStatus.setText(flight.getArrivalTimeStatus());

        TextView arrTimePlanned = view.findViewById(R.id.arrTimePlannedLayout);
        arrTimePlanned.setText(flight.getPlannedArrivalTime());
        //arrTimePlanned.setText(intent.getStringExtra("arrTimePlanned"));

        TextView arrTimeActual = view.findViewById(R.id.arrTimeActualLayout);
        arrTimeActual.setText(flight.getEstimatedArrivalTime());
        //arrTimeActual.setText(intent.getStringExtra("arrTimeEst"));

        TextView depGate = view.findViewById(R.id.depGateLayout);
        depGate.setText(flight.getGateDep());
        //depGate.setText(intent.getStringExtra("depGate"));

        TextView arrGate =  view.findViewById(R.id.arrGateLayout);
        arrGate.setText(flight.getGateArr());
        //arrGate.setText(intent.getStringExtra("arrGate"));

        TextView dep = view.findViewById(R.id.flightStatusDepartureAirport);
        dep.setText(flight.getDepCode());
        //dep.setText(intent.getStringExtra("depName"));

        TextView depName = view.findViewById(R.id.flightStatusDepartureAirportName);
        depName.setText(CityList.getCityName(AirportList.getAirportCityCode(flight.getDepCode())));

        TextView arr = view.findViewById(R.id.flightStatusArrivalAirport);
        arr.setText(flight.getArrCode());
        //arr.setText(intent.getStringExtra("arrName"));

        TextView arrName = view.findViewById(R.id.flightStatusArrivalAirportName);
        arrName.setText(CityList.getCityName(AirportList.getAirportCityCode(flight.getArrCode())));

        TextView status = view.findViewById(R.id.informationOfFlight);
        String buf = status.getText() + flight.getStatusOfFlight();
        status.setText(buf);

        TextView airCraftCode = view.findViewById(R.id.typeOfAircraft);
        buf = AircraftList.getAirCraftName(flight.getAirCraftCode());
        airCraftCode.setText(buf);

        TextView airCraftReg = view.findViewById(R.id.regOfAircraft);
        buf = airCraftReg.getText() + flight.getAirCraftRegistration();
        airCraftReg.setText(buf);


        return view;
    }

}
