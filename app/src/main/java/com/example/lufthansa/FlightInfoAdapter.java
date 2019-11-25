package com.example.lufthansa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lufthansa.APIObjects.Flight;
import com.example.lufthansa.MainFragments.Fragments.FlightInfoItemClickLisstener;

import java.util.ArrayList;

public class FlightInfoAdapter extends RecyclerView.Adapter<FlightInfoAdapter.FlightInfoHolder> {

    private ArrayList<Flight> flights;
    private Context context;
    private static FlightInfoItemClickLisstener listener;

    // load the adapter with the data ---- declare also context and listener for calling later the numberInfoFragment
    public FlightInfoAdapter(Context context, FlightInfoItemClickLisstener listener, ArrayList<Flight> flights) {
        this.context = context;
        FlightInfoAdapter.listener = listener;
        this.flights = flights;
    }

    // viewHolder
    public static class FlightInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public AppCompatImageView logo;
        public TextView flightNumber;
        public TextView flightDepTime;
        public TextView flightItemStatus;

        public FlightInfoHolder(View view) {
            super(view);

            logo = view.findViewById(R.id.flightItemAirlineLogo);
            flightNumber = view.findViewById(R.id.flightItemFlightNumber);
            flightDepTime = view.findViewById(R.id.flightItemDepTime);
            flightItemStatus = view.findViewById(R.id.flightItemStatus);

            view.setOnClickListener(this);
        }

        // we implemented the onclick method for this viewholder to call the numberinfoFragment from the infoFragment for routes
        @Override
        public void onClick(View v) {
            listener.recyclerClickListener(v, this.getLayoutPosition());
        }
    }

    // inflate the layout and return the holder
    @Override
    public FlightInfoAdapter.FlightInfoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        View flightView = inflater.inflate(R.layout.flight_timetable_item, viewGroup, false);

        FlightInfoHolder holder = new FlightInfoHolder(flightView);

        return holder;
    }


    @Override
    public void onBindViewHolder(FlightInfoAdapter.FlightInfoHolder viewHolder, int position) {
        Flight flight = flights.get(position);

        AppCompatImageView mLogo = viewHolder.logo;
        mLogo.setImageDrawable(flight.getLogo());

        // the textviews
        TextView mFlightNumber = viewHolder.flightNumber;
        TextView mFlightDepTime = viewHolder.flightDepTime;
        TextView mFlightStatus = viewHolder.flightItemStatus;

        mFlightNumber.setText(flight.getFlightNumber());
        mFlightDepTime.setText(flight.getPlannedDepartureTime());

        if(flight.getStatusOfFlight().equals("No status")) mFlightStatus.setText("On time");
        else mFlightStatus.setText(flight.getStatusOfFlight());
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }
}
