package com.example.fellowtraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class InfoTrackActivity extends AppCompatActivity {
    private MapView map;
    private IMapController mapController;
    private Polyline polyTrack;
    private Track track;
    
    private Marker marker;
    private ViewPager2 simpleViewPager;
    private TabLayout tabLayout;
    private SeekBar slider;
    private InfoWindowTrack infowindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);

        //SETTING MAP
        Intent intent = getIntent();
        map = findViewById(R.id.map_single_track);
        mapController = map.getController();
        setMap(loadPreference("map"));

        //LOAD TRACK ON MAP
        track = new Track(intent.getStringExtra("track"));
        loadAndSetTrackOnMap();

        //SLIDER
        slider = findViewById(R.id.seekbar);
        setSlider();

        //SETTING TAB AND PAGE VIEWS
        simpleViewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        MyPagerAdapter adapter = new MyPagerAdapter(this, tabLayout.getTabCount(),intent.getStringExtra("track") );
        simpleViewPager.setAdapter(adapter);
        String[] tabTitles = {"Statistics","Speed","Elevation"};
        new TabLayoutMediator(tabLayout, simpleViewPager,
                (tab, position) -> {
                    tab.setText(tabTitles[position]);
        }).attach();



        /*map = findViewById(R.id.map_single_track);
        setMap(loadPreference("map"));
        mapController = map.getController();
        graph = findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);
        graph.getGridLabelRenderer().setTextSize(25f);
        graph.getLegendRenderer().setVisible(true);
        graph.getGridLabelRenderer();

        //setListenersAndFormatters();

        //mapController.setZoom(15.);
        Intent intent = getIntent();
        track = new Track(intent.getStringExtra("track"));
        loadAndSetInfoTrack();


        */
    }

    public String loadPreference(String key){
        SharedPreferences sh = getSharedPreferences("MySharedPref", 0);
        String s1 = sh.getString(key, "Spinal");
        return s1;
    }

    private void loadAndSetTrackOnMap(){
        polyTrack = track.getPolyTrack();
        map.getOverlays().add(polyTrack);
        //map.zoomToBoundingBox(polyTrack.getBounds(),false);
        map.invalidate();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                map.zoomToBoundingBox(polyTrack.getBounds(), false);
                if (map.getZoomLevelDouble() < 18) {
                    mapController.setZoom(map.getZoomLevelDouble() - 0.2);
                } else {
                    mapController.setZoom(18.);
                }
            }
        }, 100);
    }

    private void setSlider(){
        List<GeoPoint> polyTrackPoints = polyTrack.getActualPoints();
        setMarker(polyTrackPoints);
        map.getOverlays().add(marker);
        slider.setMax(polyTrack.getActualPoints().size() - 1); // Set max value of slider
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                marker.setPosition(polyTrackPoints.get(progress));
                //marker.setSnippet(track.getDistanceUntilNowFormatted(progress));
                infowindow.update(track.getMarkerInformation(progress));
                infowindow.open(marker, marker.getPosition(), 0, -20);
                map.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setMarker(List<GeoPoint> polyTrackPoints){
        marker = new Marker(map);
        //marker.setInfoWindow(null); // Disables the built-in info window
        marker.setPosition(polyTrackPoints.get(0));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        Drawable iconDrawable = getDrawable(R.drawable.progress_marker);
        Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(),
                iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(iconBitmap);
        iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        iconDrawable.draw(canvas);

        infowindow = new InfoWindowTrack(R.layout.custom_marker_info_window, map);
        marker.setInfoWindow(infowindow);
        infowindow.update(track.getMarkerInformation(0));
        infowindow.open(marker, marker.getPosition(), 0, -20);
        //marker.setSnippet(track.getDistanceUntilNowFormatted(0));

        //marker.showInfoWindow();
        marker.setIcon(new BitmapDrawable(getResources(), iconBitmap));
    }


    private void setMap(String name) {
        SqlTileWriter sqlTileWriter = new SqlTileWriter();
        boolean b = sqlTileWriter.purgeCache();
        sqlTileWriter.onDetach();
        //cazzola style
        try {
            Class<?> c = Class.forName("com.example.fellowtraveler.MyMaps");
            Method method = c.getDeclaredMethod(name);
            map.getTileProvider().clearTileCache();
            map.setTileSource((ITileSource) method.invoke(null, null));
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }


}