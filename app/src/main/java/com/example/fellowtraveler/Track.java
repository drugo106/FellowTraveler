package com.example.fellowtraveler;

import android.graphics.Paint;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Track {
    private String path;
    private String name;
    private Polyline track;
    private List<Double> speedPerPoint;
    private List<Double> ongoingDistance;
    private List<Integer> elevations;
    private List<Long> ongoingTime;

    public Track(String path){
        this.path = path;
        String[] s = path.split("/");
        this.name = s[s.length-1];
        loadTrackAndSetFields();
    }

    private void loadTrackAndSetFields(){
        speedPerPoint = new ArrayList<>();
        ongoingDistance = new ArrayList<>();
        elevations = new ArrayList<>();
        ongoingTime = new ArrayList<>();
        File file = new File(String.valueOf(this.path));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        this.track = new Polyline();
        this.track.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
        try {
            // parse XML file
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("trkpt");
            GeoPoint previousPoint = null;
            String previousTime = null;
            String start = "";
            double totaldistance = 0;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String e = element.getElementsByTagName("ele").item(0).getTextContent();
                    String t = element.getElementsByTagName("time").item(0).getTextContent();
                    Double lat = Double.parseDouble(((Element) node).getAttribute("lat"));
                    Double lon = Double.parseDouble(((Element) node).getAttribute("lon"));
                    t = t.replace("T"," ");
                    GeoPoint currentPoint = new GeoPoint(lat,lon);
                    track.addPoint(new GeoPoint(lat, lon));
                    if(previousPoint==null){
                        start = t;
                        speedPerPoint.add(0.);
                    }else{
                        speedPerPoint.add(getSpeed(previousPoint,currentPoint,previousTime,t));
                        totaldistance += getDistance(previousPoint,currentPoint);
                    }
                    ongoingDistance.add(totaldistance);
                    elevations.add(Integer.valueOf(e));
                    ongoingTime.add(getDurationMilliseconds(start,t));
                    previousTime = t;
                    previousPoint = currentPoint;
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private double getDistance(GeoPoint previouse, GeoPoint current){
        return previouse.distanceToAsDouble(current);
    }

    private Double getSpeed(GeoPoint previouse, GeoPoint current, String timeprevious, String timecurrent){
        long duration = getDurationMilliseconds(timeprevious,timecurrent)/1000;
        double distance = getDistance(previouse,current);
        return (distance/duration)*3.6;

    }

    private long getDurationMilliseconds(String start, String stop){
        Timestamp begin = Timestamp.valueOf(start);
        Timestamp end = Timestamp.valueOf(stop);
        long duration = end.getTime() - begin.getTime();
        return duration;
    }

    private String getDuration(String start, String stop){
        long duration = getDurationMilliseconds(start,stop);
        return millisecondsToTimestamp(duration);
    }

    public static String millisecondsToTimestamp(long time){
        int SS = (int) (time / 1000) % 60 ;
        int MM = (int) ((time / (1000*60)) % 60);
        int HH   = (int) ((time / (1000*60*60)) % 24);
        return String.format("%d:%02d:%02d", HH, MM, SS);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Polyline getPolyTrack() {
        return track;
    }

    public List<Double> getSpeedPerPoint() {
        return speedPerPoint;
    }

    public List<Double> getOngoingDistance() {
        return ongoingDistance;
    }

    public List<Integer> getElevations() {
        return elevations;
    }

    public List<Long> getOngoingTime(){
        return ongoingTime;
    }

    public static <T extends Number> Double max(List<T> list) {
        double max = list.get(0).doubleValue();
        for (T num : list) {
            double value = num.doubleValue();
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public static <T extends Number> Double min(List<T> list) {
        double max = list.get(0).doubleValue();
        for (T num : list) {
            double value = num.doubleValue();
            if (value < max) {
                max = value;
            }
        }
        return max;
    }

    public double getMaxSpeed(){
        return max(speedPerPoint);
    }

    public Double getMinSpeed(){
        return min(speedPerPoint);
    }

    public Double getAverageSpeed(){
        double sum = 0;
        for (double num : speedPerPoint) {
            sum += num;
        }
        return sum / speedPerPoint.size();
    }

    public Integer getMaxElevation(){
        return max(elevations).intValue();
    }

    public Integer getMinElevation(){
        return min(elevations).intValue();
    }

}
