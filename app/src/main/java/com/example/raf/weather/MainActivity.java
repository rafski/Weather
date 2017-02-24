package com.example.raf.weather;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    String temperature;
    String icon;
    String summary;
    String windSpeed;
    String pressure;
    String currentLocation;
    String queryString;
    String currentCity;
    String searchedCity;
    String capitalizedCity;

    Location location;

    TextView temperatureText;
    TextView weatherDescription;

    ImageView weatherIcon;

    View parentLayout;

    LocationManager locationManager;
    LocationListener locationListener;

    public void goToDetails(View view){

        Intent intent = new Intent(MainActivity.this, WeatherDetail.class);
        intent.putExtra("location", currentLocation);

        if (searchedCity !=null) {
            intent.putExtra("cityName", capitalizedCity);
        }else {
            intent.putExtra("cityName", currentCity);
        }
            startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 500, locationListener);
            }
        }

    }

    public void updateLocation (Location location){

        //Log.i("Location", String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
        currentLocation = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (addressList != null && addressList.size() > 0){

                //Log.i("address", addressList.get(0).toString());

                currentCity = addressList.get(0).getLocality().toString();


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void encodeUrl(){

            String url = "https://api.darksky.net/forecast/";
            String key = "4a3412e7dc871ea6610d108d3d0c6058/";
            String location = currentLocation;
            String parameters = "?exclude=minutely,hourly,alerts,flags?&&units=uk2";
            queryString = url + key + location + parameters ;

    }

    public void updateWeather(){


        if (queryString != null){

            DownloadTask task = new DownloadTask();
            JSONObject jsonObject = null;

            try {
                jsonObject = task.execute(queryString).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (jsonObject != null) {
                JSONObject currently = null;
                try {
                    currently = jsonObject.getJSONObject("currently");

                    temperature = String.valueOf(Math.round(currently.getDouble("temperature")));

                    icon = currently.getString("icon");

                    summary = currently.getString("summary");

                    windSpeed = currently.getString("windSpeed");

                    pressure = currently.getString("pressure");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String iconReadable = icon.replace("-", "_");

                Context c = getApplicationContext();
                int id = c.getResources().getIdentifier("drawable/" + iconReadable, null, c.getPackageName());

                temperatureText.setText(temperature + " \u00B0C");
                if (searchedCity !=null){
                    capitalizedCity  = searchedCity;
                    capitalizedCity = capitalizedCity.substring(0,1).toUpperCase() + capitalizedCity.substring(1).toLowerCase();
                    weatherDescription.setText("It is " + summary + " in " + capitalizedCity + " , the pressure is " + pressure + " hPa with a wind speed of " + windSpeed +" mph.");
                }else {
                    weatherDescription.setText("It is " + summary + " in " + currentCity + " , the pressure is " + pressure + " hPa with a wind speed of " + windSpeed +" mph.");
                }

                weatherIcon.setImageResource(id);
            }else{
                weatherDescription.setText("Weather provider unavailable");
            }

        } else {
            weatherDescription.setText("Weather provider unavailable");
        }
    }




    public class DownloadTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {

                Log.i("URL", params[0]);

                String result = "";
                JSONObject jsonObject = null;

                URL url;
                HttpURLConnection urlConnection = null;

                try {

                    url = new URL(params[0]);

                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream inputStream = urlConnection.getInputStream();

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    int data = inputStreamReader.read();

                    while (data != -1) {

                        char current = (char) data;

                        result += current;

                        data = inputStreamReader.read();
                    }

                    try {

                        jsonObject = new JSONObject(result);

                        //Log.i("temp now", temperature + icon + summary);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return jsonObject;

                } catch (Exception e) {
                    e.printStackTrace();

                    return null;
                }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parentLayout = findViewById(R.id.fabCoordinator);

        temperatureText = (TextView)findViewById(R.id.temperatureTextView);
        weatherDescription = (TextView)findViewById(R.id.weatherDescriptionTextView);
        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 500, locationListener);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //get explicit permission

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 500, locationListener);


        }

        if (location == null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(location);

        }

        encodeUrl();
        updateWeather();

        Snackbar.make(parentLayout, "Weather updated", Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .show();


        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.updateWeatherFAB);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                encodeUrl();
                updateWeather();

                Snackbar snackbar = Snackbar
                        .make(view, "Weather updated", Snackbar.LENGTH_LONG);
                snackbar.setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                searchedCity = String.valueOf(place.getName());
                currentLocation = String.valueOf(place.getLatLng().latitude) +", " + String.valueOf(place.getLatLng().longitude);

                encodeUrl();
                updateWeather();
            }

            @Override
            public void onError(Status status) {
                Log.i("error", "An error occurred: " + status);
            }
        });
    }

}
