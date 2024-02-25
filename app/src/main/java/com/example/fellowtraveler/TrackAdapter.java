package com.example.fellowtraveler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TrackAdapter extends RecyclerView.Adapter<TrackHolder> {

    Context context;
    File[] files;

    public TrackAdapter(Context context){
        this.context = context;
        File pathToSave = context.getExternalFilesDir(null);
        pathToSave.mkdirs();
        this.files = new File(String.valueOf(pathToSave)).listFiles();
    }

    @NonNull
    @Override
    public TrackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrackHolder(LayoutInflater.from(context).inflate(R.layout.item_track,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackHolder holder, @SuppressLint("RecyclerView") int position) {
        String[] info = getInfoFromXMLTrack(this.files[position]);
        String[] s = this.files[position].toString().split("/");
        holder.name.setText(s[s.length-1]);
        holder.length.setText(info[0]);
        holder.time.setText(info[1]);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean d = files[position].delete();
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@ "+d);
            }
        });

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InfoTrackActivity.class);
                intent.putExtra("track",files[position].toString());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.files.length;
    }

    public String[] getInfoFromXMLTrack(File f){
        Double distance = new Double(0);
        String[] startend = {"",""};

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("trkpt");
            GeoPoint lastPoint = null;
            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String e = element.getElementsByTagName("ele").item(0).getTextContent();
                    String t = element.getElementsByTagName("time").item(0).getTextContent();
                    t = t.replace("T"," ");
                    if(i==0)
                        startend[0] = t;
                    if(i == list.getLength()-1)
                        startend[1] = t;
                    GeoPoint current = getGeoPointFromElementNode(element);
                    if(lastPoint==null)
                        lastPoint = current;
                    distance += lastPoint.distanceToAsDouble(current);
                    lastPoint = current;
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        String duration = getDuration(startend[0],startend[1]);

        if(distance >= 1000){
            distance = distance/1000;
            return new String[]{String.format("%.2f",distance)+" km",duration};
        }
        return new String[]{distance.intValue() + " m", duration};
    }

    private GeoPoint getGeoPointFromElementNode(Element element){
        Double lat = Double.parseDouble(element.getAttribute("lat"));
        Double lon = Double.parseDouble(element.getAttribute("lon"));
        return new GeoPoint(lat,lon);
    }

    private String getDuration(String start, String stop){
        System.out.println(start +" "+ stop);
        Timestamp begin = Timestamp.valueOf(start);
        Timestamp end = Timestamp.valueOf(stop);
        long duration = end.getTime() - begin.getTime();
        int SS = (int) (duration / 1000) % 60 ;
        int MM = (int) ((duration / (1000*60)) % 60);
        int HH   = (int) ((duration / (1000*60*60)) % 24);
        return String.format("%d:%02d:%02d", HH, MM, SS);
    }
}
