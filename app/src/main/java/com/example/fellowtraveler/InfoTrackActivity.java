package com.example.fellowtraveler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_track);

        map = findViewById(R.id.map_single_track);
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



    }

    public String loadPreference(String key){
        SharedPreferences sh = getSharedPreferences("MySharedPref", 0);
        String s1 = sh.getString(key, "Spinal");
        return s1;
    }

    private void loadAndSetInfoTrack(){
        new Thread() {
            public void run() {
                polyTrack = track.getPolyTrack();
                speedPerPoint = track.getSpeedPerPoint();
                ongoingDistance = track.getOngoingDistance();
                ongoingtime = track.getOngoingTime();
                elevations = track.getElevations();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        map.getOverlays().add(polyTrack);
                        //map.zoomToBoundingBox(polyTrack.getBounds(),false);
                        map.invalidate();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                map.zoomToBoundingBox(polyTrack.getBounds(), false);
                                if(map.getZoomLevelDouble()<18) {
                                    mapController.setZoom(map.getZoomLevelDouble() - 0.2);
                                }else {
                                    mapController.setZoom(18.);
                                }
                            }
                        }, 100);
                        //mapController.setZoom(15.);
                        /*if(map.getZoomLevelDouble()>18) {
                            mapController.setZoom(map.getZoomLevelDouble() - 0.2);
                        }else {
                            mapController.setZoom(18.);
                        }*/
                    }
                });
                drawTrackOnGraph();

            }
        }.start();
    }

    private void overrideFormatLabel(){
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                DecimalFormat nf = (DecimalFormat) NumberFormat.getInstance();
                nf.setMinimumFractionDigits(0);
                nf.setMaximumFractionDigits(0);
                if (isValueX) {
                    // show normal x values
                    return timestampXAxis((long) value);
                } else {
                    // show currency for y values
                    return nf.format(value) + " km/h ";
                }
            }
        });
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

    private void drawTrackOnGraph(){
        DataPoint[] speedPoints = new DataPoint[speedPerPoint.size()];
        DataPoint[] elevationPoints = new DataPoint[elevations.size()];

        for(int i=0; i < speedPerPoint.size();i++) {
            speedPoints[i] = new DataPoint(ongoingtime.get(i), speedPerPoint.get(i));
            //elevationPoints[i] = new DataPoint(ongoingtime.get(i), elevations.get(i));
        }
        LineGraphSeries <DataPoint> speedPlot = new LineGraphSeries<>(speedPoints);
        graph.addSeries(speedPlot);
        //graph.getViewport().setYAxisBoundsManual(true);
        overrideFormatLabel();
        graph.getGridLabelRenderer().setLabelsSpace(0);
        graph.getViewport().setScalable(true);

    }

    public static String timestampXAxis(long time){
        int MM = (int) ((time / (1000*60)) % 60);
        int HH   = (int) ((time / (1000*60*60)) % 24);
        return String.format("%d:%02d", HH, MM);
    }

}