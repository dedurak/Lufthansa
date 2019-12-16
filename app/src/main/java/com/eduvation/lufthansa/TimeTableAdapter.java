package com.eduvation.lufthansa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.eduvation.lufthansa.APIObjects.Airports.AirportList;
import com.eduvation.lufthansa.APIObjects.Cities.CityList;
import com.eduvation.lufthansa.APIObjects.TtFlight;
import com.eduvation.lufthansa.MainFragments.Fragments.FlightInfoItemClickListener;

import java.util.ArrayList;

public class TimeTableAdapter extends RecyclerView.Adapter<TimeTableAdapter.TimeTableHolder> {

    private ArrayList<TtFlight> deps;
    private Context context;
    private static FlightInfoItemClickListener listener;
    private int mode; // 0 - dep ** 1 - dep

    // the constructor loads the data
    public TimeTableAdapter (Context context, FlightInfoItemClickListener listener, ArrayList<TtFlight> deps, int mode) {
        this.deps = deps;
        this.context = context;
        TimeTableAdapter.listener = listener;
        this.mode = mode;
    }


    public static class TimeTableHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

         // declare the items
        AppCompatImageView logo;
        TextView flightNo;
        TextView arrCode;
        TextView depTime;
        TextView status;

        public TimeTableHolder (View view) {
            super(view);

            logo = view.findViewById(R.id.depAirlineLogo);
            flightNo = view.findViewById(R.id.depFlightNumber);
            arrCode = view.findViewById(R.id.depArrCode);
            depTime = view.findViewById(R.id.depDepTime);
            status = view.findViewById(R.id.depResultStatus);

            view.setOnClickListener(this);
        }

        // implement onClick to call the numberinfo fragment to show the details of the departure flight
        @Override
        public void onClick (View v) {listener.recyclerClickListener(v, this.getLayoutPosition()); }
    }


    @Override
    public TimeTableAdapter.TimeTableHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View depView = inflater.inflate(R.layout.departure_item, viewGroup, false);

        return new TimeTableHolder(depView);
    }


    @Override
    public void onBindViewHolder(TimeTableAdapter.TimeTableHolder viewHolder, int position) {

        TtFlight flight = deps.get(position);

        AppCompatImageView logo = viewHolder.logo;
        logo.setImageDrawable(flight.getLogo());

        TextView mFlightNo = viewHolder.flightNo;
        TextView mArrCode = viewHolder.arrCode;
        TextView mDepTime = viewHolder.depTime;
        TextView mStatus = viewHolder.status;

        mFlightNo.setText(flight.getFlightNumber());
        mArrCode.setText(CityList.getCityName(AirportList.getAirportCityCode(flight.getAirportCode())));
        mDepTime.setText(flight.getTimeToShow());
        String code = flight.getStatusCode();
        String codeDepender = flight.getStatusDependerCode();
        String def = "";

        if(mode == 0) {
            if (code.equals("CD")) {
                def = "CANCELLED";
            } else if (code.equals("DP") || code.equals("LD")) {
                def = "DEPARTED";
            } else if (code.equals("NA") && codeDepender.equals("OT")) {
                def = "ON TIME";
            } else if(code.equals("NA") && codeDepender.equals("DL")) {
                def = "DELAYED";
            }
        } else if (mode == 1 ) {
            if (code.equals("CD")) {
                def = "CANCELLED";
            } else if (code.equals("DP")) {
                def = "ON ROUTE";
            } else if(code.equals("LD")) {
                def = "LANDED";
            } else if (code.equals("NA") && codeDepender.equals("OT")) {
                def = "ON TIME";
            } else if(code.equals("NA") && codeDepender.equals("DL")) {
                def = "DELAYED";
            }
        }
        
        mStatus.setText(def);
    }


    @Override
    public int getItemCount() { return deps.size(); }
}
