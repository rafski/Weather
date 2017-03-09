package com.example.raf.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WeatherDetail extends FragmentActivity
        implements OnMapReadyCallback {

    static String markerName;
    static LatLng userLocation;
    String dailySummary;
    ArrayList<Integer> maxTemps;
    ArrayList<Integer> minTemps;
    ArrayList<String> days;
    ArrayList<String> summaries;

    TextView dailySummaryText;
    TextView tomorrowText;
    TextView dayTwo;
    TextView dayThree;

    public void getDataFromMain() {
        String weeklyMaxTemps;
        String weeklyMinTemps;
        String graphDays;
        String weeklySummaries;

        Intent intent = getIntent();
        markerName = intent.getStringExtra("cityName");
        dailySummary = intent.getStringExtra("dailySummary");
        weeklyMaxTemps = intent.getStringExtra("weeklyMaxTemps");
        weeklyMinTemps = intent.getStringExtra("weeklyMinTemps");
        graphDays = intent.getStringExtra("graphDays");
        weeklySummaries = intent.getStringExtra("weeklySummaries");

        try {
            maxTemps =(ArrayList<Integer>) ObjectSerializer.deserialize(weeklyMaxTemps);
        } catch (IOException e) {
            e.printStackTrace();
        };

        try {
            minTemps =(ArrayList<Integer>) ObjectSerializer.deserialize(weeklyMinTemps);
        } catch (IOException e) {
            e.printStackTrace();
        };

        try {
            days = (ArrayList<String>) ObjectSerializer.deserialize(graphDays);
        } catch (IOException e) {
            e.printStackTrace();
        };
        try {
            summaries = (ArrayList<String>) ObjectSerializer.deserialize(weeklySummaries);
        } catch (IOException e) {
            e.printStackTrace();
        };


        String[] latlong = intent.getStringExtra("location").split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        userLocation = new LatLng(latitude, longitude);

    }

    public void showDetails() {

        if (dailySummary != null && dailySummary.length() > 0) {
            dailySummaryText.setText(dailySummary);
        }

        tomorrowText.setText("Tommorow: " + summaries.get(0) + " Temperature from " + minTemps.get(0) + " \u00B0C"+ " to " + maxTemps.get(0) + " \u00B0C");
        dayTwo.setText("Day after tomorrow: " + summaries.get(1) + " Temperature from " + minTemps.get(1) + " \u00B0C"+ " to " + maxTemps.get(1) + " \u00B0C");
        dayThree.setText("In three days: " + summaries.get(2) + " Temperature from " + minTemps.get(2) + " \u00B0C"+ " to " + maxTemps.get(2) + " \u00B0C");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        tomorrowText = (TextView)findViewById(R.id.tomorrowText);
        dayTwo = (TextView)findViewById(R.id.twoDaysText);
        dayThree = (TextView)findViewById(R.id.threeDaysText);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dailySummaryText = (TextView)findViewById(R.id.dailySummaryTextView);

        getDataFromMain();
        showDetails();

        Calendar calendar = Calendar.getInstance();

        Date d0 = calendar.getTime();
        calendar.add(Calendar.DATE, 0);
        Date d1 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d2 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d3 = calendar.getTime();
        calendar.add(Calendar.DATE, 1);
        Date d4 = calendar.getTime();




        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> maxTemp = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d0, maxTemps.get(0)),
                new DataPoint(d1, maxTemps.get(1)),
                new DataPoint(d2, maxTemps.get(2)),
                new DataPoint(d3, maxTemps.get(3)),
                new DataPoint(d4, maxTemps.get(4))
        });
        graph.addSeries(maxTemp);
        graph.getViewport().setMinX(d0.getTime());
        graph.getViewport().setMaxX(d4.getTime());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        maxTemp.setAnimated(true);
        maxTemp.setColor(R.color.accent);
        maxTemp.setDrawBackground(true);
        maxTemp.setBackgroundColor(R.color.wind);

        LineGraphSeries<DataPoint> minTemp = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(d0, minTemps.get(0)),
                new DataPoint(d1, minTemps.get(1)),
                new DataPoint(d2, minTemps.get(2)),
                new DataPoint(d3, minTemps.get(3)),
                new DataPoint(d4, minTemps.get(4))
        });
        graph.addSeries(minTemp);
        minTemp.setAnimated(true);
        minTemp.setColor(R.color.primary_dark);
        minTemp.setDrawBackground(true);
        minTemp.setBackgroundColor(Color.WHITE);



    }

    @Override
    public void onMapReady(GoogleMap mapFragment) {

        mapFragment.clear();

        mapFragment.addMarker(new MarkerOptions().position(userLocation).title(markerName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        mapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }




}





