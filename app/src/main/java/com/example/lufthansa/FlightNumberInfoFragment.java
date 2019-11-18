package com.example.lufthansa;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.lufthansa.APIObjects.Aircrafts.AircraftList;
import com.example.lufthansa.APIObjects.Airlines.AirlineList;
import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.Cities.CityList;
import com.example.lufthansa.APIObjects.Flight;
import com.google.android.material.appbar.MaterialToolbar;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DateFormat;

import kotlin.jvm.JvmOverloads;

public class FlightNumberInfoFragment extends Fragment {

    Flight flight;

    private final String TAG = FlightNumberInfoFragment.class.getSimpleName();

    // in onCreate we fetch the flight we want to display the informations in view
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.start_toolbar);
        toolbar.setVisibility(View.GONE);

        View view = (View) getActivity().findViewById(R.id.view);
        view.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, 0);

        View fragment = getActivity().findViewById(R.id.navigation_component);
        fragment.setLayoutParams(params);

        Log.d(TAG,"inside onCreate");
        String index = getArguments().getString("index");

        // look if there is an index. this is the activity has been invoked from overview result
        if(!index.equals(("no"))) {
            flight = DataServices.getFlightFromList(Integer.parseInt(index));
        } else {
            flight = DataServices.getFlight();
        }
    }

    // here the fetched infos are assigned to the textviews
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.flight_info, container, false);
        Log.d(TAG,"inside onCreateView");

        /**
         * *      the following lines set the values of the layout
        */

        TextView flightNumber = view.findViewById(R.id.flightnumber);
        flightNumber.setText(flight.getFlightNumber());

        // find and load logo
        ImageView logo = view.findViewById(R.id.airlineLogo);
        Drawable drawable = AirlineList.getLogo(flight.getOperator(), getContext());
        if(drawable != null)
            logo.setImageDrawable(drawable);

        TextView depTimePlanned = view.findViewById(R.id.flightInfoPlannedDepTime);
        depTimePlanned.setText(flight.getPlannedDepartureTime());

        TextView depTimeActual = view.findViewById(R.id.flightInfoEstDepTime);
        depTimeActual.setText(flight.getEstimatedDepartureTime());;

        TextView arrTimePlanned = view.findViewById(R.id.flightInfoPlannedArrTime);
        arrTimePlanned.setText(flight.getPlannedArrivalTime());

        TextView arrTimeActual = view.findViewById(R.id.flightInfoEstArrTime);
        arrTimeActual.setText(flight.getEstimatedArrivalTime());

        TextView depGate = view.findViewById(R.id.depGateInfo);
        depGate.setText(flight.getGateDep());

        TextView arrGate =  view.findViewById(R.id.arrGateInfo);
        arrGate.setText(flight.getGateArr());

        TextView depTerminal = view.findViewById(R.id.depTerminalInfo);
        depTerminal.setText(flight.getDepTerminal());

        TextView arrTerminal = view.findViewById(R.id.arrTerminalInfo);
        arrTerminal.setText(flight.getArrTerminal());

        TextView dep = view.findViewById(R.id.flightInfoDepCode);
        dep.setText(flight.getDepCode());

        TextView depName = view.findViewById(R.id.flightInfoDepCity);
        String text = CityList.getCityName(AirportList.getAirportCityCode(flight.getDepCode())) + ",";
        depName.setText(text);

        TextView depAirportName = view.findViewById(R.id.flightInfoDepAirport);
        depAirportName.setText(AirportList.getAirportName(flight.getDepCode()));

        TextView arr = view.findViewById(R.id.flightInfoArrCode);
        arr.setText(flight.getArrCode());

        TextView arrName = view.findViewById(R.id.flightInfoArrCity);
        text = CityList.getCityName(AirportList.getAirportCityCode(flight.getArrCode())) + ",";
        arrName.setText(text);

        TextView arrAirportName = view.findViewById(R.id.flightInfoArrAirport);
        arrAirportName.setText(AirportList.getAirportName(flight.getArrCode()));


        // get textview of status vals
        TextView depStatus = view.findViewById(R.id.depStatus);
        TextView arrStatus = view.findViewById(R.id.arrStatus);
        TextView status = view.findViewById(R.id.flightInfoStatusContent);


        // get string code of status vals
        String depStatCode = flight.getDepStatusCode();
        String arrStatCode = flight.getArrivalStatusCode();
        String flightStatCode = flight.getStatusCode();


        // get values for textviews
        String stringRes[] = createStatusStrings(depStatCode, arrStatCode, flightStatCode);

        int green = getResources().getColor(R.color.green);
        int blue = getResources().getColor(R.color.lhDeepBlue);
        int red = getResources().getColor(R.color.red);

        if(depStatCode.equals("FE") || depStatCode.equals("OT")) {
            depStatus.setTextColor(green);
            depStatus.setTypeface(Typeface.DEFAULT_BOLD);
        } else if(depStatCode.equals("DL")) {
            depStatus.setTextColor(red);
            depStatus.setTypeface(Typeface.DEFAULT_BOLD);
        }

        depStatus.setText(stringRes[0]);


        if(arrStatCode.equals("FE")) {
            arrStatus.setTextColor(blue);
            arrStatus.setTypeface(Typeface.DEFAULT_BOLD);
        } else if(arrStatCode.equals("DL")) {
            arrStatus.setTextColor(red);
            arrStatus.setTypeface(Typeface.DEFAULT_BOLD);
        } else if (arrStatCode.equals("OT")) { arrStatus.setTextColor(green); arrStatus.setTypeface(Typeface.DEFAULT_BOLD);}

        arrStatus.setText(stringRes[1]);

        TextView flightInfoOnTop = view.findViewById(R.id.flightInfoTop);

        TextView remainingTimeField = view.findViewById(R.id.remainingTime);
        TextView remainingContnt = view.findViewById(R.id.remainingTimeContent);

        Log.d(TAG, "flightstatCode: " + flightStatCode);


        if(flightStatCode.equals("DP")) {
            status.setTextColor(blue);
            flightInfoOnTop.setTextColor(blue);
            flightInfoOnTop.setText("ON ROUTE");
            remainingTimeField.setVisibility(View.VISIBLE);
            remainingContnt.setVisibility(View.VISIBLE);
            remainingContnt.setText(flight.getRemainingTime());
        } else if(flightStatCode.equals("CD")) {
            status.setTextColor(red);
            status.setTypeface(Typeface.DEFAULT_BOLD);
            flightInfoOnTop.setTextColor(red);
            flightInfoOnTop.setText("CANCELLED");
        } else if(flightStatCode.equals("LD")) {
            status.setTextColor(green);
            status.setTypeface(Typeface.DEFAULT_BOLD);
            flightInfoOnTop.setTextColor(green);
            flightInfoOnTop.setText("LANDED");
        } else if(flightStatCode.equals("RT")) {
            status.setTextColor(blue);
            status.setTypeface(Typeface.DEFAULT_BOLD);
            flightInfoOnTop.setTextColor(blue);
            flightInfoOnTop.setText("REROUTED");
        }

        status.setText(stringRes[2]);


        TextView planFlightDur = view.findViewById(R.id.planFltTimeContent);
        String time = flight.getPlannedFlightDuration();
        planFlightDur.setText(time);

        TextView realFlightDur = view.findViewById(R.id.realFltTimeContent);
        time = flight.getRealFlightDuration();
        realFlightDur.setText(time);


        TextView airCraftCode = view.findViewById(R.id.flightInfoAirCraftContent);
        String buf = AircraftList.getAirCraftName(flight.getAirCraftCode());
        airCraftCode.setText(buf);

        TextView airCraftReg = view.findViewById(R.id.registrationContent);
        airCraftReg.setText(flight.getAirCraftRegistration());


        return view;
    }


    // Fragment destroys view and returns to back stack
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.start_toolbar);
        toolbar.setVisibility(View.VISIBLE);

        View view = (View) getActivity().findViewById(R.id.view);
        view.setVisibility(View.VISIBLE);

        float scale = getResources().getDisplayMetrics().density;
        float dp = 60;
        int pixel = (int) (dp * scale + 0.5f);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, pixel, 0, 0);

        View fragment = getActivity().findViewById(R.id.navigation_component);
        fragment.setLayoutParams(params);

    }

    /**
     *  receives the flight status code and the dep and arr status and creates for all 3 fields the correct strings
     * @param depStatCodeForString - status of departure
     *                              FE - Flight Early
     *                              NI = Next Information
     *                              OT = Flight On Time
     *                              DL = Flight Delayed
     *                              NO = No status
     * @param arrStatCodeForString - status of arrival
     *      *                       FE - Flight Early
     *      *                       NI = Next Information
     *      *                       OT = Flight On Time
     *      *                       DL = Flight Delayed
     *      *                       NO = No status
     * @param statCodeOfFlightForString - status of Flight
     *                              CD = Flight Cancelled
     *                              DP = Flight Departed
     *                              LD = Flight Landed
     *                              RT = Flight Rerouted
     *                              NA = No status
     * @return - return result as String array
     */
    private String[] createStatusStrings (String depStatCodeForString, String arrStatCodeForString, String statCodeOfFlightForString) {
        String[] resultToReturn = new String[3];

        String depString, arrString, statString;

        depString = createDepString(statCodeOfFlightForString, depStatCodeForString);
        arrString = createArrString(statCodeOfFlightForString, arrStatCodeForString);

        if(statCodeOfFlightForString.equals("DP")) {
            statString = "FLIGHT DEPARTED AND ON ROUTE";
        } else if (statCodeOfFlightForString.equals("LD")) {
            statString = "FLIGHT LANDED";
        } else if (statCodeOfFlightForString.equals("CD")) {
            statString = "FLIGHT CANCELLED";
        } else if(statCodeOfFlightForString.equals("RT")) {
            statString = "FLIGHT REROUTED";
        } else
            statString = "NO ACUTAL INFORMATION AVAILABLE";

        resultToReturn[0] = depString;
        resultToReturn[1] = arrString;
        resultToReturn[2] = statString;

        return resultToReturn;
    }

    @NotNull
    private String createDepString (String statCode, String depCode) {
        if(statCode.equals("DP") || statCode.equals("LD") || statCode.equals("RT")) {
            if(depCode.equals("DL")) return "DELAYED DEPARTURE";
            if(depCode.equals("OT")) return "DEPARTED ON TIME";
            if(depCode.equals("FE")) return "EARLY DEPARTURE";
            if(depCode.equals("NO")) return "";
        } else if(statCode.equals("NA")) {
            if(depCode.equals("DL")) return "DEPARTURE DELAYS";
            if(depCode.equals("OT")) return "DEPARTURE ON TIME";
            if(depCode.equals("FE")) return "EARLY DEPARTURE";
            if(depCode.equals("NO")) return "";
        } else
            return "";

        return "";
    }

    @NonNull
    private String createArrString (String statCode, String arrCode) {
        if(statCode.equals("DP") || statCode.equals("NA") || statCode.equals("RT")) {
            if(arrCode.equals("DL")) return "ARRIVAL DELAYS";
            if(arrCode.equals("OT")) return "ARRIVAL ON TIME";
            if(arrCode.equals("FE")) return "EARLY ARRIVAL";
            if(arrCode.equals("NO")) return "";
        } else if(statCode.equals("LD")) {
            if(arrCode.equals("DL")) return "ARRIVED DELAYED";
            if(arrCode.equals("OT")) return "ARRIVED ON TIME";
            if(arrCode.equals("FE")) return "ARRIVED EARLIER";
            if(arrCode.equals("NO")) return "";
        } else
            return "";

        return "";
    }
}
