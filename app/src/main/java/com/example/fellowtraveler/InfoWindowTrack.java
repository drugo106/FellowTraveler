package com.example.fellowtraveler;

import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

public class InfoWindowTrack extends InfoWindow {

    private final LinearLayout layout;
    private TextView description;

    public InfoWindowTrack(int layoutResId, MapView mapView) {
        super(layoutResId, mapView);
        layout = (LinearLayout) mView.findViewById(R.id.info_window);
        description = (TextView) mView.findViewById(R.id.infoWindowText);
    }
    @Override
    public void onOpen(Object item) {

    }

    @Override
    public void onClose() {

    }

    public void update(String info){
        description.setText((info));
    }


}
