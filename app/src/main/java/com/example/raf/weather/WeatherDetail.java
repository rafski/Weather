package com.example.raf.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

public class WeatherDetail extends FragmentActivity
        implements OnMapReadyCallback {

    static String markerName;
    static LatLng userLocation;
    String dailySummary;
    ArrayList<Integer> maxTemps;
    ArrayList<Integer> minTemps;


    private XYPlot plot;

    TextView dailySummaryText;

    public void getDataFromMain() {
        String weeklyMaxTemps;
        String weeklyMinTemps;

        Intent intent = getIntent();
        markerName = intent.getStringExtra("cityName");
        dailySummary = intent.getStringExtra("dailySummary");
        weeklyMaxTemps = intent.getStringExtra("weeklyMaxTemps");
        weeklyMinTemps = intent.getStringExtra("weeklyMinTemps");

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


        String[] latlong = intent.getStringExtra("location").split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);

        userLocation = new LatLng(latitude, longitude);

    }

    public void showDetails() {

        if (dailySummary != null && dailySummary.length() > 0) {
            dailySummaryText.setText(dailySummary);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dailySummaryText = (TextView)findViewById(R.id.dailySummaryTextView);

        getDataFromMain();
        showDetails();

        plot = (XYPlot) findViewById(R.id.plot);

        final Number[] domainLabels = {1, 2, 3, 4, 5};
        Number[] series1Numbers = {maxTemps.get(0), maxTemps.get(1), maxTemps.get(2),maxTemps.get(3), maxTemps.get(4)};
        Number[] series2Numbers = {minTemps.get(0), minTemps.get(1), minTemps.get(2),minTemps.get(3), minTemps.get(4)};

        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temp Max");
        XYSeries series2 = new SimpleXYSeries(
                Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temp Min");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.RED, null, null);
        LineAndPointFormatter series2Format = new LineAndPointFormatter(Color.GREEN, Color.GREEN, null, null);

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series1Format.setLegendIconEnabled(false);
        series2Format.setLegendIconEnabled(false);


        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);


        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap mapFragment) {

        mapFragment.clear();

        mapFragment.addMarker(new MarkerOptions().position(userLocation).title(markerName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

        mapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }




}





