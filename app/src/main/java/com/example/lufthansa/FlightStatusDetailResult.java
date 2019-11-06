package com.example.lufthansa;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lufthansa.APIObjects.Aircrafts.AircraftList;
import com.example.lufthansa.APIObjects.Airlines.AirlineList;
import com.example.lufthansa.APIObjects.Airports.AirportList;
import com.example.lufthansa.APIObjects.ApiAirportResult;
import com.example.lufthansa.APIObjects.Cities.CityList;
import com.example.lufthansa.APIObjects.Flight;

import org.json.JSONException;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FlightStatusDetailResult extends AppCompatActivity {

    private final String TAG = FlightStatusDetailResult.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_status_cardview_result);

        Log.d(TAG, "inside FlightStatusDetailResult");

        // the object where all flight data are stored
        Flight flight;
        Bundle bundle = getIntent().getExtras();

        // look if there is an index. this is the activity has been invoked from overview result
        if(bundle.getString("index") != null) {
            int ind =  Integer.parseInt(bundle.getString("index"));
            flight = DataServices.getFlightFromList(ind);
        } else {
            //Intent intent = getIntent();
            flight = DataServices.getFlight();
        }

        /**
         * the following lines set the values of the layout
         */
        TextView flightNumber = findViewById(R.id.flightnumber);
        flightNumber.setText(flight.getFlightNumber());
        //flightNumber.setText(intent.getStringExtra("flightNumber"));

        // find and load logo
        ImageView logo = findViewById(R.id.airlineLogo);
        Drawable drawable = AirlineList.getLogo(flight.getOperator(), this);
        if(drawable != null)
            logo.setImageDrawable(drawable);


        TextView depTimePlanned = findViewById(R.id.plannedDepTimeLayout);
        depTimePlanned.setText(flight.getPlannedDepartureTime());
        //depTimePlanned.setText(intent.getStringExtra("depTimePlanned"));

        TextView depTimeActual = findViewById(R.id.actualDepTimeLayout);
        depTimeActual.setText(flight.getEstimatedDepartureTime());;
        //depTimeActual.setText(intent.getStringExtra("depTimeEst"));;

        TextView depTimeStatus = findViewById(R.id.depTimeStatus);
        depTimeStatus.setText(flight.getDepartureTimeStatus());

        TextView arrTimeStatus = findViewById(R.id.arrTimeStatus);
        arrTimeStatus.setText(flight.getArrivalTimeStatus());

        TextView arrTimePlanned = findViewById(R.id.arrTimePlannedLayout);
        arrTimePlanned.setText(flight.getPlannedArrivalTime());
        //arrTimePlanned.setText(intent.getStringExtra("arrTimePlanned"));

        TextView arrTimeActual = findViewById(R.id.arrTimeActualLayout);
        arrTimeActual.setText(flight.getEstimatedArrivalTime());
        //arrTimeActual.setText(intent.getStringExtra("arrTimeEst"));

        TextView depGate = findViewById(R.id.depGateLayout);
        depGate.setText(flight.getGateDep());
        //depGate.setText(intent.getStringExtra("depGate"));

        TextView arrGate =  findViewById(R.id.arrGateLayout);
        arrGate.setText(flight.getGateArr());
        //arrGate.setText(intent.getStringExtra("arrGate"));

        TextView dep = findViewById(R.id.flightStatusDepartureAirport);
        dep.setText(flight.getDepCode());
        //dep.setText(intent.getStringExtra("depName"));

        TextView depName = findViewById(R.id.flightStatusDepartureAirportName);
        depName.setText(CityList.getCityName(AirportList.getAirportCityCode(flight.getDepCode())));

        TextView arr = findViewById(R.id.flightStatusArrivalAirport);
        arr.setText(flight.getArrCode());
        //arr.setText(intent.getStringExtra("arrName"));

        TextView arrName = findViewById(R.id.flightStatusArrivalAirportName);
        arrName.setText(CityList.getCityName(AirportList.getAirportCityCode(flight.getArrCode())));

        TextView status = findViewById(R.id.informationOfFlight);
        String buf = status.getText() + flight.getStatusOfFlight();
        status.setText(buf);

        TextView airCraftCode = findViewById(R.id.typeOfAircraft);
        buf = AircraftList.getAirCraftName(flight.getAirCraftCode());
        airCraftCode.setText(buf);

        TextView airCraftReg = findViewById(R.id.regOfAircraft);
        buf = airCraftReg.getText() + flight.getAirCraftRegistration();
        airCraftReg.setText(buf);
    }



    /*
    private void extractAirportName(String airportCode, final int type) {
        String authentication = "Bearer " + StartPage.access_token.getAccess_token();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.lufthansa.com/v1/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        Log.d(TAG, "AirportCode: " + airportCode);
        Log.d(TAG, "Authorization: "  + authentication);

        Single<Response<ApiAirportResult>> res = service.getAirport(
                airportCode,
                "DE",
                authentication);

        res.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<ApiAirportResult>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "inside getAirportName, onSubscribe");
                    }

                    @Override
                    public void onSuccess(Response<ApiAirportResult> jsonObjectResponse) {
                        Log.d(TAG, "inside getAirportName, onSuccess");
                        ApiAirportResult obj = jsonObjectResponse.body();
                        try {
                            assignAirportNameToLayout((String) jsonObjectResponse.body().getObject()
                                    .getJSONObject("Airports")
                                    .getJSONObject("Airport")
                                    .getJSONObject("Names")
                                    .getJSONObject("Name")
                                    .get("$"), type);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "inside getAirportName onError");
                    }
                });
    }

    private void assignAirportNameToLayout(String name, int type) {
        Log.d(TAG, "before if query inside assignAirportNameToLayout");
        if(type == 0) {
            Log.d(TAG, "Yes 0 " + name);
            TextView airportName = findViewById(R.id.flightStatusDepartureAirportName);
            airportName.setText(name);
        }
        else if (type == 1) {
            Log.d(TAG, "Yes 1 " + name);
            TextView airportName = findViewById(R.id.flightStatusArrivalAirportName);
            airportName.setText(name);
        }
    }
    */


    // finishs the activity and returns to the last activity in backstack
    public void backToParent(View view) {
        finish();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return callParent();
    }

    private Intent callParent() {

        Intent intent = null;
        String parent = getIntent().getExtras().getString("goto");

        if(parent.equals("overview")) {
            intent = new Intent(this, FlightStatusDetailResult.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent = new Intent(this, StartPage.class);
        }

        return intent;
    }
}
