package com.example.fellowtraveler.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fellowtraveler.R;
import com.example.fellowtraveler.Track;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class SpeedGraphFragment extends Fragment {

    private Track track;
    private List<Double> speedPerPoint;
    private List<Double> ongoingDistance;
    private List<Long> ongoingtime;
    private List<Integer> elevations;
    private View fragmentView;
    private GraphView graph;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.speed_graph_fragment, container, false);
        Bundle bundle = this.getArguments();
        String trackname = bundle.getString("track");
        track = new Track(trackname);
        graph = fragmentView.findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);
        graph.getGridLabelRenderer().setTextSize(25f);
        graph.getLegendRenderer().setVisible(true);
        graph.getGridLabelRenderer();
        loadAndSetInfoTrack();
        graph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        return fragmentView;
    }

    private void loadAndSetInfoTrack(){
        new Thread() {
            public void run() {
                speedPerPoint = track.getSpeedPerPoint();
                ongoingDistance = track.getOngoingDistance();
                ongoingtime = track.getOngoingTime();
                elevations = track.getElevations();
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

    private void drawTrackOnGraph(){
        DataPoint[] speedPoints = new DataPoint[speedPerPoint.size()];
        DataPoint[] elevationPoints = new DataPoint[elevations.size()];

        for(int i=0; i < speedPerPoint.size();i++) {
            speedPoints[i] = new DataPoint(ongoingtime.get(i), speedPerPoint.get(i));
            //elevationPoints[i] = new DataPoint(ongoingtime.get(i), elevations.get(i));
        }
        LineGraphSeries<DataPoint> speedPlot = new LineGraphSeries<>(speedPoints);
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
