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
        System.out.println(oldLat +" "+oldLong);
        if(oldLat != coord[0].doubleValue() || oldLong != coord[1].doubleValue()) {
            try {
                URL url = new URL("https://api.opentopodata.org/v1/eudem25m?locations=" + coord[0] + "," + coord[1]);
                oldLat = coord[0];
                oldLong = coord[1];
                //System.out.println(url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String res = IOUtils.toString(in, StandardCharsets.UTF_8);
                JSONObject json = new JSONObject(res);
                //System.out.println(res);
                result = json.getJSONArray("results").getJSONObject(0).getString("elevation");
                if(result.equals("null"))
                    result = "0";
                else
                    result = (int) Math.round(Double.parseDouble(result)) +"";
            } catch (IOException e) {
                result = "No Connection";
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("cazzolone");
        return result;
    }


}
