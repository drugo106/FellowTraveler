package com.example.fellowtraveler;



import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TrackRecorder extends Thread{
    private MapView map;
    private MyLocationNewOverlay mLocation;
    public Polyline track;
    private boolean stay;
    private String gpxStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> <gpx version=\"1.1\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" xmlns:oa=\"http://www.outdooractive.com/GPX/Extensions/1\">";


    public TrackRecorder(MapView m, MyLocationNewOverlay l, String nameTrk){
        map = m;
        mLocation = l;
        track = new Polyline(map);
        stay = true;
        gpxStr += "<metadata>"+
                "</metadata>"+
                "<trk>"+
                "<name>"+nameTrk+"</name>"+
                "<trkseg>";
    }

    @Override
    public void run() {
        int i =0;
        //convertStringToXMLDocument(xmlStr+"</gpx>");


        while(stay){
            if(mLocation.getMyLocation() != null) {
                List<GeoPoint> points = track.getActualPoints();
                //System.out.println(points.size());
                GeoPoint myLocation = mLocation.getMyLocation();
                if(points.size() > 0) {
                    if (!points.get(points.size() - 1).equals(new GeoPoint(mLocation.getMyLocation()))) {
                        track.addPoint(myLocation);
                    }
                }else {
                    track.addPoint(myLocation);
                }
                try {
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                    timeStamp = timeStamp.replace(" ","T");
                    gpxStr+="<trkpt lat=\""+myLocation.getLatitude()+"\" lon=\""+myLocation.getLongitude()+"\">"+
                            "<ele>"+new RetrieveElevationTask().execute(myLocation.getLatitude(),myLocation.getLongitude()).get()+"</ele>"+
                            "<time>"+timeStamp+"</time>"+
                            "</trkpt>";
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                //track.getActualPoints().forEach((p)->{System.out.println(p);});
                map.getOverlays().remove(track);
                map.getOverlays().add(track);

                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
        gpxStr+="</trkseg></trk></gpx>";
        Document gpx = convertStringToXMLDocument(gpxStr);
        try {
            printDocument(gpx);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();
            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void printDocument(Document doc) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        // initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        String xmlString = result.getWriter().toString();
        System.out.println(xmlString);
    }

    public void exit() {
        stay = false;
    }
}
