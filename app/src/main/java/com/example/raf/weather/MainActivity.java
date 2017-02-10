package com.example.raf.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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

        TextView temperatureText = (TextView)findViewById(R.id.temperatureTextView);
        TextView weatherDescription = (TextView)findViewById(R.id.weatherDescriptionTextView);
        ImageView weatherIcon = (ImageView)findViewById(R.id.weatherIcon);


        DownloadTask task = new DownloadTask();
        JSONObject jsonObject = null;
        try {
            jsonObject = task.execute("https://api.darksky.net/forecast/4a3412e7dc871ea6610d108d3d0c6058/51.544524, -0.133146?exclude=minutely,hourly,alerts,flags?&&units=uk2").get();
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
        int id = c.getResources().getIdentifier("drawable/"+iconReadable, null, c.getPackageName());

        temperatureText.setText(temperature + " \u00B0C");
        weatherDescription.setText(summary);
        weatherIcon.setImageResource(id);


    }
}
