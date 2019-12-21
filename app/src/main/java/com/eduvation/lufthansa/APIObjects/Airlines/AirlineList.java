package com.eduvation.lufthansa.APIObjects.Airlines;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.eduvation.lufthansa.R;

public class AirlineList {
    static Airline[] airlines = {
            new Airline("JP", "Adria Airways", R.drawable.adria),
            new Airline("A3", "Aegean Airlines", R.drawable.aegean),
            new Airline("AC", "Air Canada", R.drawable.aircanada),
            new Airline("CA", "Air China", R.drawable.airchina),
            new Airline("AI", "Air India", R.drawable.airindia),
            new Airline("NZ", "Air New Zealand", R.drawable.airnewzealand),
            new Airline("NH", "All Nippon Airways", R.drawable.ana),
            new Airline("OZ", "Asiana Airlines", R.drawable.asiana),
            new Airline("OS", "Austrian Airlines", R.drawable.austrian),
            new Airline("AV", "Avianca", R.drawable.avianca),
            new Airline("SN", "Brussels Airlines", R.drawable.brussels),
            new Airline("DE", "Condor", R.drawable.condor),
            new Airline("CM", "Copa Airlines", R.drawable.copa),
            new Airline("OU", "Croatia Airlines", R.drawable.croatia),
            new Airline("MS", "Egyptair", R.drawable.egyptair),
            new Airline("ET", "Ethipian Airlines", R.drawable.ethiopian),
            new Airline("EW", "Eurowings", R.drawable.eurowings),
            new Airline("BR", "Eva Air", R.drawable.eva),
            new Airline("LO", "LOT", R.drawable.lot),
            new Airline("LH", "Lufthansa", R.drawable.lufthansa),
            new Airline("SK", "Scandinavian Airlines", R.drawable.sas),
            new Airline("ZH", "Shenzhen Airlines", R.drawable.shenzhen),
            new Airline("SQ", "Singapore Airlines", R.drawable.singapore),
            new Airline("SA", "South African Airways", R.drawable.southafrican),
            new Airline("LX", "Swiss", R.drawable.swiss),
            new Airline("TP", "TAP Air Portugal", R.drawable.tap),
            new Airline("TG", "Thai Airways International", R.drawable.thai),
            new Airline("TK", "Turkish Airlines", R.drawable.turkish),
            new Airline("XQ", "SunExpress", R.drawable.sunexpress),
            new Airline("XG", "SunExpress", R.drawable.sunexpress),
            new Airline("UA", "United Airlines", R.drawable.united),
            new Airline("CX", "Cathay Pacific", R.drawable.cathay)
    };

    // search and return the airline logo depending on airline shortcut
    public static Drawable getLogo(String code, Context context) {
        Drawable id = null;

        for(int index = 0; index<airlines.length; index++) {
            if (airlines[index].getAirlineCode().equals(code))
                return context.getDrawable(airlines[index].getLogo());
        }

        return id;
    }

    public static int getLogoId(String code) {
        for(int index = 0; index<airlines.length; index++) {
            if (airlines[index].getAirlineCode().equals(code))
                return airlines[index].getLogo();
        }

        return 0;
    }

    // search for airline shortcut with airline name
    public static String getCode(String airlineName) {
        int i = 0;
        while (i<airlines.length) {
            if (airlines[i].getAirlineName().equals(airlineName))
                return airlines[i].getAirlineCode();

            ++i;
        }

        return null;
    }
}
