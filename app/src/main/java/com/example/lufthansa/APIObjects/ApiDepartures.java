package com.example.lufthansa.APIObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiDepartures {
    private String buffer;
    private JSONObject flightStatusResource;

    @JsonProperty("FlightStatusResource")
    public void setFlightStatusResource(Object nestedItem) {
        buffer = new Gson().toJson(nestedItem);
        try {
            flightStatusResource = new JSONObject(buffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getDeparturesResource() { return flightStatusResource; }
}
