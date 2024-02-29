package com.example.fellowtraveler.Fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fellowtraveler.CustomGraphView;
import com.example.fellowtraveler.GraphTools;
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
    private List<Long> ongoingtime;
    public GraphView graph;



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
        graph = fragmentView.findViewById(R.id.speed_graph);
        GraphTools.setGraph(graph);
        loadAndSetInfoTrack();

        return fragmentView;
    }

    private void loadAndSetInfoTrack(){
        new Thread() {
            public void run() {
                speedPerPoint = track.getSpeedPerPoint();
                ongoingtime = track.getOngoingTime();
                GraphTools.drawTrackOnGraph(graph,ongoingtime,speedPerPoint,"km/h", Color.BLUE,5);
            }
        }.start();
    }



}
