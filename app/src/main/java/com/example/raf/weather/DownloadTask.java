package com.example.raf.weather;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by raf on 09/03/2017.
 */

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
