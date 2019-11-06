package com.example.lufthansa.APIObjects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiAirportResult {

    private String buffer;
    private JSONObject object;

    @JsonProperty("AirportResource")
    public void setAirportResource(Object obj) {
        buffer = new Gson().toJson(obj);
        try {
            object = new JSONObject(buffer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getObject() { return object; }
}
