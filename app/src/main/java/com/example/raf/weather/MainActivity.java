package com.example.raf.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {


    String temperature;
    String icon;
    String summary;
    String currentLocation;
    String encodedUrl;
    String queryString;

    TextView temperatureText;
    TextView weatherDescription;

    ImageView weatherIcon;

    LocationManager locationManager;
    LocationListener locationListener;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }

        }
    }


    public void encodeUrl(){

            String url = "https://api.darksky.net/forecast/";
            String key = "4a3412e7dc871ea6610d108d3d0c6058/";
            String location = currentLocation;
            String parameters = "?exclude=minutely,hourly,alerts,flags?&&units=uk2";
            queryString = url + key + location + parameters ;

        Log.i("generated url", queryString);

    }


    public void refreshWeather(View view){

        encodeUrl();
        updateWeather();

    }

    public void updateWeather(){

        if (queryString == null){

            temperatureText.setText("Please update weather");

        }else {

            DownloadTask task = new DownloadTask();
            JSONObject jsonObject = null;

            try {
                jsonObject = task.execute(queryString).get();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.i("jo", jsonObject.toString());

            JSONObject currently = null;
            try {
                currently = jsonObject.getJSONObject("currently");

                temperature = currently.getString("temperature");

                icon = currently.getString("icon");

                summary = currently.getString("summary");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String iconReadable = icon.replace("-", "_");

            Context c = getApplicationContext();
            int id = c.getResources().getIdentifier("drawable/" + iconReadable, null, c.getPackageName());

            temperatureText.setText(temperature + " \u00B0C");
            weatherDescription.setText(summary);
            weatherIcon.setImageResource(id);
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

                    /*



                    Log.i("temp now", temperature + icon + summary);*/

                } catch (JSONException e){
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

        temperatureText = (TextView)findViewById(R.id.temperatureTextView);
        weatherDescription = (TextView)findViewById(R.id.weatherDescriptionTextView);
        weatherIcon = (ImageView)findViewById(R.id.weatherIcon);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Log.i("Location", String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude()));
                currentLocation = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());

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

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //get explicit permission

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        currentLocation = String.valueOf(location.getLatitude()) + ", " + String.valueOf(location.getLongitude());

        encodeUrl();
        updateWeather();
    }

}
