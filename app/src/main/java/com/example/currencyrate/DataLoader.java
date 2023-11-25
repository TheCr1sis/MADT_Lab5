package com.example.currencyrate;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataLoader extends AsyncTask<String, Void, String> {

    private OnDataLoadedListener listener;

    public interface OnDataLoadedListener {
        void onDataLoaded(String result);

        void onError(String error);
    }

    public DataLoader(OnDataLoadedListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... urls) {
        String urlString = urls[0];
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                return result.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            Log.e("DataLoader", "IOException: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            listener.onDataLoaded(result);
        } else {
            listener.onError("Failed to load data");
        }
    }
}
