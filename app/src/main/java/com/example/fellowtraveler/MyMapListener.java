package com.example.fellowtraveler;

import android.widget.TextView;

import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.views.MapView;

public class MyMapListener implements MapListener {

    private MapView map;
    private TextView logger;

    public MyMapListener(MapView m, TextView l){
        map = m;
        logger = l;
    }

    @Override
    public boolean onScroll(ScrollEvent event) {

        return false;
    }

    @Override
    public boolean onZoom(ZoomEvent event) {
        return false;
    }
}
