package com.example.fellowtraveler;



import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.List;

public class TrackRecorder extends Thread{
    private MapView map;
    private MyLocationNewOverlay mLocation;
    public Polyline track;
    private boolean stay;

    public TrackRecorder(MapView m, MyLocationNewOverlay l){
        map = m;
        mLocation = l;
        track = new Polyline(map);
        stay = true;
    }

    @Override
    public void run() {
        int i =0;
        while(stay){
            if(mLocation.getMyLocation() != null) {
                List<GeoPoint> points = track.getActualPoints();
                System.out.println(points.size());
                if(points.size() > 0) {
                    if (!points.get(points.size() - 1).equals(new GeoPoint(mLocation.getMyLocation()))) {
                        track.addPoint(mLocation.getMyLocation());
                    }
                }else {
                    track.addPoint(mLocation.getMyLocation());
                }
                //track.addPoint(new GeoPoint(45.464664 + new Random().nextDouble() , 9.188540 + i));
                track.getActualPoints().forEach((p)->{System.out.println(p);});
                map.getOverlays().remove(track);
                map.getOverlays().add(track);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }

    }


    public void exit() {
        stay = false;
    }
}
