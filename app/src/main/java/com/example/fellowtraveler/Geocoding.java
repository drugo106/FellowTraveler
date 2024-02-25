package com.example.fellowtraveler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Geocoding {
    private static String USER_AGENT = Configuration.getInstance().getUserAgentValue();

    public static GeoPoint reverseGeocoding(String query){
        GeocoderNominatim geocoder = new GeocoderNominatim(USER_AGENT);
        try {
            List<Address> address = geocoder.getFromLocationName(query,1);
            //System.out.println("@@@@@@@@@@@@@@@ " + a.getExtras().getCharSequence("display_name"));
            if(address.size()>0)
                return new GeoPoint(address.get(0).getLatitude(),address.get(0).getLongitude());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Road getRoad(Context context, String start,String end, String vehicles) {
        OSRMRoadManager roadManager = new OSRMRoadManager(context, USER_AGENT);
        roadManager.setMean(vehicles);
        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(reverseGeocoding(start));
        waypoints.add(reverseGeocoding(end));
        Road road = roadManager.getRoad(waypoints);
        return road;
    }

    public static Polyline getRoute(Road road){
        return RoadManager.buildRoadOverlay(road);
    }

    public static ArrayList<Marker> getMarkers(Context context, MapView map, Road road){
            ArrayList<Marker> routeMarkers = new ArrayList<>();
            Drawable nodeIcon = context.getResources().getDrawable(R.drawable.marker_node);
            for (int i=1; i<road.mNodes.size()-1; i++){
                RoadNode node = road.mNodes.get(i);
                Marker nodeMarker = new Marker(map);
                nodeMarker.setSnippet(node.mInstructions);
                nodeMarker.setSubDescription(Road.getLengthDurationText(context, node.mLength, node.mDuration));
                nodeMarker.setPosition(node.mLocation);
                nodeMarker.setIcon(nodeIcon);
                nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                nodeMarker.setTitle("Step "+i);
                routeMarkers.add(nodeMarker);
                //map.getOverlays().add(nodeMarker);
            }
        return routeMarkers;
    }
}

