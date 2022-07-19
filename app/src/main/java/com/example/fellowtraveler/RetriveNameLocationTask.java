package com.example.fellowtraveler;

import android.os.AsyncTask;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;

import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;



public class RetriveNameLocationTask extends AsyncTask<Double, Void, String> {

    private static double oldLat = -1000000.,oldLong = -1000000.;
    private static String result = "No Data";

    @Override
    protected String doInBackground(Double... coord) {
        if(oldLat != coord[0] || oldLong != coord[1]) {
            try {
                URL url = new URL("https://nominatim.openstreetmap.org/reverse?format=json&lat="+coord[0]+"&lon="+coord[1]+"&zoom=18&addressdetails=1");
                oldLat = coord[0];
                oldLong = coord[1];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String res = IOUtils.toString(in, StandardCharsets.UTF_8);  //res is in xml format
                JSONObject json = new JSONObject(res);
                result = json.getString("display_name");
                if(result.equals("null"))
                    result = "New Track";
            } catch (IOException e) {
                result = "New Track";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("cazzolone");
        return result;
    }
}
