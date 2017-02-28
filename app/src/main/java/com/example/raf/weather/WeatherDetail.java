package com.example.raf.weather;

import android.content.Intent;
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

public class WeatherDetail extends FragmentActivity
        implements OnMapReadyCallback {

    static String markerName;
    static LatLng userLocation;
    String dailySummary;

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


    }

    @Override
    public void onMapReady(GoogleMap mapFragment) {


        mapFragment.clear();


        mapFragment.addMarker(new MarkerOptions().position(userLocation).title(markerName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));


        mapFragment.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }


}





