package com.example.raf.weather;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by raf on 01/03/2017.
 */

public class Weather {
    private JSONObject jsonObject;

    public Weather(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getCurrentTemperature() {
        try {
            return String.valueOf(Math.round(this.jsonObject.getDouble("temperature")));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCurrentIcon(){

        try {
            return this.jsonObject.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getCurrentWindSpeed(){

        try {
            return this.jsonObject.getString("windSpeed");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getCurrentSummary(){

        try {
            return this.jsonObject.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getCurrentPressure(){

        try {
            return this.jsonObject.getString("pressure");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getWeeklySummary(){

        try {
            return this.jsonObject.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getDailySummary(){

        try {
            return this.jsonObject.getJSONArray("data").getJSONObject(0).getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
