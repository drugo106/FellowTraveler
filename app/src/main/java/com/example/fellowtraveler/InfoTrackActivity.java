package com.example.fellowtraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class InfoTrackActivity extends AppCompatActivity {
    private MapView map;
    private IMapController mapController;
    private Polyline polyTrack;
    private List<Double> speedPerPoint;
    private List<Double> ongoingDistance;
    private List<Long> ongoingtime;
    private List<Integer> elevations;
    private GraphView graph;
    private Track track;

    private ViewPager2 simpleViewPager;
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);

        Intent intent = getIntent();
        map = findViewById(R.id.map_single_track);
        mapController = map.getController();
        setMap(loadPreference("map"));
        track = new Track(intent.getStringExtra("track"));
        loadAndSetTrackOnMap();

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