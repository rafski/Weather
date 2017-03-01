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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

public class WeatherDetail extends FragmentActivity
        implements OnMapReadyCallback {

    static String markerName;
    static LatLng userLocation;
    String dailySummary;

    private XYPlot plot;

    TextView dailySummaryText;

    public void getDataFromMain() {

        Intent intent = getIntent();
        markerName = intent.getStringExtra("cityName");
        dailySummary = intent.getStringExtra("dailySummary");

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

        // create a couple arrays of y-values to plot:
        final Number[] domainLabels = {1, 2, 3, 6, 7, 8, 9};
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Temperature");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
                LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.DKGRAY, Color.BLACK, Color.GRAY, null);

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));


        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

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





