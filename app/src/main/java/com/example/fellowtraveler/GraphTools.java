package com.example.fellowtraveler;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

    private static LineGraphSeries<DataPoint> speedLineSeries;
    private static LineGraphSeries<DataPoint> elevationLineSeries;


    @SuppressLint("ClickableViewAccessibility")
    public static void setGraph(GraphView graph){
        graph.setVisibility(View.VISIBLE);
        graph.getGridLabelRenderer().setTextSize(25f);
        //graph.getLegendRenderer().setVisible(true);
        //graph.getGridLabelRenderer();
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

    public static void drawTrackOnGraph(GraphView graph, List<Long> X, List<Double> Y, String format, int color, int thickness){
        DataPoint[] points = new DataPoint[Y.size()];
        for(int i=0; i < X.size();i++) {
            points[i] = new DataPoint(X.get(i), Y.get(i));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        series.setColor(color);
        series.setThickness(thickness);
        series.setDrawBackground(true);
        graph.addSeries(series);

        graph.getViewport().setYAxisBoundsManual(true);
        overrideFormatLabel(graph,format);
        graph.getGridLabelRenderer().setLabelsSpace(0);
        graph.getViewport().setScalable(true);

        //plot.setBackgroundColor(Color.parseColor("#80FF0000"));

        /*LineGraphSeries<DataPoint> verticalLine = new LineGraphSeries<>();
        verticalLine.setColor(Color.RED);
        verticalLine.setThickness(3);
        graph.addSeries(verticalLine);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                // Update the data points of the vertical line series
                verticalLine.resetData(new DataPoint[]{
                        new DataPoint(dataPoint.getX(), 0), // Start point of the line (x, minY)
                        new DataPoint(dataPoint.getX(), 1000)  // End point of the line (x, maxY)
                });
            }
        });*/



    }

    public static void drawSpeedLine(GraphView graph, double X){
        if(speedLineSeries !=null)
            graph.removeSeries(speedLineSeries);
        speedLineSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(X, 0),
                new DataPoint(X,   graph.getRootView().getHeight())
        });
        speedLineSeries.setColor(Color.RED);
        speedLineSeries.setThickness(5);

        graph.addSeries(speedLineSeries);
    }

    public static void drawElevationLine(GraphView graph, double X){
        if(elevationLineSeries !=null)
            graph.removeSeries(elevationLineSeries);
        elevationLineSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(X, 0),
                new DataPoint(X,   graph.getRootView().getHeight())
        });
        elevationLineSeries.setColor(Color.RED);
        elevationLineSeries.setThickness(5);

        graph.addSeries(elevationLineSeries);
    }
}
