package com.example.fellowtraveler.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.fellowtraveler.CustomGraphView;
import com.example.fellowtraveler.GraphTools;
import com.example.fellowtraveler.R;
import com.example.fellowtraveler.Track;
import com.jjoe64.graphview.GraphView;

import java.util.List;

public class ElevationGraphFragment extends Fragment {

    private Track track;
    private List<Long> ongoingtime;
    private List<Double> elevations;
    public GraphView graph;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.elevation_graph_fragment, container, false);
        Bundle bundle = this.getArguments();
        String trackname = bundle.getString("track");
        track = new Track(trackname);
        graph = fragmentView.findViewById(R.id.elevation_graph);
        GraphTools.setGraph(graph);
        loadAndSetInfoTrack();
        return fragmentView;
    }

    private void loadAndSetInfoTrack(){
        new Thread() {
            public void run() {
                ongoingtime = track.getOngoingTime();
                elevations = track.getElevations();
                GraphTools.drawTrackOnGraph(graph,ongoingtime,elevations,"m", Color.MAGENTA, 5);
            }
        }.start();
    }
}
