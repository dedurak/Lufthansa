package com.eduvation.lufthansa;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eduvation.lufthansa.APIObjects.ApiFlights;
import com.eduvation.lufthansa.APIObjects.ApiFlightResults;
import com.eduvation.lufthansa.APIObjects.TtFlight;
import com.eduvation.lufthansa.MainFragments.Fragments.FlightInfoItemClickListener;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FlightResultFragment extends Fragment implements FlightInfoItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button loadButton;
    private String mDate;
    private int mode; // 0 is for deps, 1 is for arrivals
    private ArrayList<TtFlight> flights;
    private ProgressBar prog;

    private final String TAG = FlightResultFragment.class.getSimpleName();
    private final String accessToken = StartPage.access_token.getAccess_token();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle bundle){

        View view = inflater.inflate(R.layout.flight_results, null);

        mode = getArguments().getInt("from");
        prog = view.findViewById(R.id.flightProg);

        flights = DataServices.getSearchedFlights();

        recyclerView = view.findViewById(R.id.recycleFlights);
        recyclerView.setHasFixedSize(true);
        loadButton = view.findViewById(R.id.flightLoadButton);
        loadButton.setOnClickListener(v -> {
            loadButton.setVisibility(View.GONE);
            prog.setVisibility(View.VISIBLE);
            String[] values = DataServices.getValuesForNextFlightSearch();
            doRetroFitFlightSearch(values[0], values[1], Integer.parseInt(values[2]), Integer.parseInt(values[3]));
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    loadButton.setVisibility(View.VISIBLE);
                }
            }
        });

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TimeTableAdapter(getContext(), this, flights, mode);
        recyclerView.setAdapter(adapter);

        TextView airportCode = view.findViewById(R.id.flightResAirportCode);
        String topCodeText;
        if (mode == 1) {
            topCodeText = "Arrivals " + getArguments().getString("airportCode");
            airportCode.setText(topCodeText);
        } else {
            topCodeText = "Departures " + getArguments().getString("airportCode");
            airportCode.setText(topCodeText);
        }
        TextView date = view.findViewById(R.id.flightResTopBarDate);
        mDate = getArguments().getString("date");
        String topDate = formatDate(mDate);
        date.setText(topDate);

        return view;
    }

    @Override
    public void recyclerClickListener(View v, View transition, int pos) {
        String flightNo = flights.get(pos).getFlightNumber();

        doRetrofitSearchFlight(flightNo, mDate);
    }

    private String formatDate ( String dateToFormat) {
        String[] val = dateToFormat.split("-");
        return val[2] + "." + val[1] + "." + val[0];
    }

    private void doRetrofitSearchFlight(String flightNo, String metDate) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Log.d(TAG, "After build");

        ApiService service = retrofit.create(ApiService.class);

        Single<Response<ApiFlightResults>> retrofitFlight;

        retrofitFlight = service.getFlightByFlightNumber(
                flightNo, metDate, "Bearer " + StartPage.access_token.getAccess_token(),  "application/json"
        );

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiFlightResults>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiFlightResults> apiFlightResultsResponse) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSuccess");
                        //JSONObject object = jsonObjectResponse.body();
                        Log.d(TAG, "response body: " + apiFlightResultsResponse.body());
                        Log.d(TAG, "response resp code: " + apiFlightResultsResponse.code());
                        Log.d(TAG, "response header: " + apiFlightResultsResponse.headers());
                        Log.d(TAG, "raw response: " + apiFlightResultsResponse.raw());

                        if (apiFlightResultsResponse.body() != null) {
                            if (DataServices.extractFlight(apiFlightResultsResponse.body().getFlightStatusResource(), 1, getContext()) == 1) {
                                openFlightNumberFragment();
                            }
                        } else {
                            Toast warningNoFlight = Toast.makeText(getContext(), "No Results!", Toast.LENGTH_LONG);
                            warningNoFlight.show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                        e.printStackTrace();
                    }});

    }

    private void doRetroFitFlightSearch(String retroAirportCode, String dateTime, int retroOffSet, int retroLimit) {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "retroAirportCode: " + retroAirportCode);
        Log.d(TAG, "dateTime: " + dateTime);


        Single<Response<ApiFlights>> retrofitFlight;

        if (mode == 0)
            retrofitFlight = service.getDepartures(
                retroAirportCode, dateTime, retroOffSet, retroLimit, "Bearer " + accessToken, "application/json");

        else
            retrofitFlight = service.getArrivals(
                    retroAirportCode, dateTime, retroOffSet, retroLimit, "Bearer " + accessToken, "application/json");

        retrofitFlight.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiFlights>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onSubscribe()");
                    }

                    @Override
                    public void onSuccess(Response<ApiFlights> apiFlightResultsResponse) {

                        // if receiving httpexception, in ths case there is no "next" link inside meta data to fetch further data
                        if (apiFlightResultsResponse.isSuccessful()) {
                            if(mode == 0) {
                                if (DataServices.extractDepartures(apiFlightResultsResponse.body().getApiFlightsResource(), 1, getContext()) == 1) {
                                    // do something after the next flights are loaded and dismiss the visibility of load button
                                    flights = DataServices.getSearchedFlights();
                                    loadButton.setVisibility(View.GONE);
                                    prog.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                if (DataServices.extractArrivals(apiFlightResultsResponse.body().getApiFlightsResource(), 1, getContext()) == 1) {
                                    // do something after the next flights are loaded and dismiss the visibility of load button
                                    flights = DataServices.getSearchedFlights();
                                    loadButton.setVisibility(View.GONE);
                                    prog.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            prog.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "retrofitFlightStatusByFlightNumber - onError");
                        e.printStackTrace();
                    }
                });
    }

    private void openFlightNumberFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("index", "no");
        bundle.putString("from", "flightInfo");
        Navigation.findNavController(getView()).navigate(R.id.action_flightResultFragment_to_flightNumberInfoFragment, bundle);
    }
}
