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

    public String getTemperature() {
        try {
            return String.valueOf(Math.round(this.jsonObject.getDouble("temperature")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMaxTemperature() {
        try {
            return String.valueOf(Math.round(this.jsonObject.getDouble("temperatureMax")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMinTemperature() {
        try {
            return String.valueOf(Math.round(this.jsonObject.getDouble("temperatureMin")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getIcon(){

        try {
            return this.jsonObject.getString("icon");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getWindSpeed(){

        try {
            return this.jsonObject.getString("windSpeed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSummary(){

        try {
            return this.jsonObject.getString("summary");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getPressure(){

        try {
            return this.jsonObject.getString("pressure");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
