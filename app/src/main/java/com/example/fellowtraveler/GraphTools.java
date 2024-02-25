package com.example.fellowtraveler;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class GraphTools {


    @SuppressLint("ClickableViewAccessibility")
    public static void setGraph(GraphView graph){
        graph.setVisibility(View.VISIBLE);
        graph.getGridLabelRenderer().setTextSize(25f);
        graph.getLegendRenderer().setVisible(true);
        graph.getGridLabelRenderer();
        graph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    private static void overrideFormatLabel(GraphView graph, String format){
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
                    return nf.format(value) + " "+format+" ";
                }
            }
        });
    }

    private static String timestampXAxis(long time){
        int MM = (int) ((time / (1000*60)) % 60);
        int HH   = (int) ((time / (1000*60*60)) % 24);
        return String.format("%d:%02d", HH, MM);
    }

    public static void drawTrackOnGraph(GraphView graph, List<Long> X, List<Double> Y, String format){
        DataPoint[] points = new DataPoint[Y.size()];
        //DataPoint[] elevationPoints = new DataPoint[elevations.size()];

        for(int i=0; i < X.size();i++) {
            points[i] = new DataPoint(X.get(i), Y.get(i));
            //elevationPoints[i] = new DataPoint(ongoingtime.get(i), elevations.get(i));
        }
        LineGraphSeries<DataPoint> speedPlot = new LineGraphSeries<>(points);
        graph.addSeries(speedPlot);
        //graph.getViewport().setYAxisBoundsManual(true);
        overrideFormatLabel(graph,format);
        graph.getGridLabelRenderer().setLabelsSpace(0);
        graph.getViewport().setScalable(true);

    }
}
