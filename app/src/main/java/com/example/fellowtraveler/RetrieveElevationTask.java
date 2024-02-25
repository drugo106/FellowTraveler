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

public class RetrieveElevationTask extends AsyncTask<Double, Void, String> {

    private static double oldLat = -1000000.,oldLong = -1000000.;
    private static String result = "No Data";

    @Override
    protected String doInBackground(Double... coord) {
        if(oldLat != coord[0] || oldLong != coord[1]) {
            URL url = null;
            try {
                url = new URL("https://api.opentopodata.org/v1/eudem25m?locations=" + coord[0] + "," + coord[1]);
                oldLat = coord[0];
                oldLong = coord[1];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String res = IOUtils.toString(in, StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(res);
                result = json.getJSONArray("results").getJSONObject(0).getString("elevation");
                if(result.equals("null"))
                    result = "0"; //NO DATA

                else
                    result = (int) Math.round(Double.parseDouble(result)) +"";
            } catch (IOException e) {
                result = "0";  //NO CONNECTION
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println(url);
                e.printStackTrace();
            }
        }
        return result;
    }


}
