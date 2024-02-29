package com.example.fellowtraveler;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.modules.SqlTileWriter;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static boolean FIRST_START = true;
    private MapView map;
    private IMapController mapController;
    private MyLocationNewOverlay mLocationOverlay;
    private TextView logger;
    private TextView rec;
    private TextView pos;
    private CheckBox waymark;
    private CheckBox seamark;
    private TilesOverlay waymarkLayer;
    private TilesOverlay seamarkLayer;
    private SearchView searchView;
    private Spinner spinner;
    private Marker markerLocation;
    private ArrayList<Marker> routeMarkers;
    private Polyline route;

    private LocationListener mLocationListener;
    private DelayedMapListener mapListener;
    private DisplayMetrics dm;
    private MinimapOverlay mMinimapOverlay;

    private boolean isRecording = false;

    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ScaleBarOverlay mScaleBarOverlay;
    private static String selectedMap = "MAPNIK";
    public static String newMap = "MAPNIK";

    private File pathToSave;

    private String USER_AGENT = Configuration.getInstance().getUserAgentValue();
    private Context context = this;
    private CheckBox hikebikemark;
    private TilesOverlay hikebikeLayer;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(this.getPackageName());
        Configuration.getInstance().setDebugMode(false  );
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's tile servers will get you banned based on this string

        //inflate and create the map

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            if (isStoragePermissionGranted()) {

            }
        }

        // INIZIALIZE VIEWS
        rec = findViewById(R.id.rec);
        logger = findViewById(R.id.log);
        pos = findViewById(R.id.pos);
        map = findViewById(R.id.mapView);
        waymark = findViewById(R.id.waymark_check);
        seamark = findViewById(R.id.seamark_check);
        hikebikemark = findViewById(R.id.hikebike_check);

        waymarkLayer = MyMaps.WaymarkOverlay(this);
        seamarkLayer = MyMaps.SeamarkOverlay(this);
        hikebikeLayer = MyMaps.HikeBikeOverlay(this);

        searchView = (SearchView) findViewById(R.id.searchView);
        /*
        spinner = (Spinner) findViewById(R.id.sport_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sport_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */
        //INIZIALIZE MAP
        //map.setBuiltInZoomControls(true);
        setMap(loadPreference("map"));
        map.setMultiTouchControls(true);
        //map.addMapListener(new MyMapListener());
        mapController = map.getController();
        mapController.setZoom(15.);
        myLocation();
        GeoPoint startPoint = new GeoPoint(45.464664, 9.188540);
        mapController.setCenter(startPoint);
        mapScale();
        markerLocation = new Marker(map);

        //setMarkerOnStartPosition(startPoint);

        //SET SERVICES AND LISTENERS
        setListeners();
        startServices();

        //DEBUG
        //touchOverlay();
        //getRoute();
        //show my location + if is following


        loadTracks();

    }



    private void setMap(String name) {
        //clear cache before change map

        if(!selectedMap.equals(name)) {
            selectedMap = name;
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
            savePreference("map", name);
        }
    }

    private void setMarkerOnStartPosition(GeoPoint startPoint) {
        Marker markerLocation = new Marker(map);
        markerLocation.setPosition(startPoint);
        markerLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(markerLocation);
    }

    public void myLocation(){
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
    }

    public void mapScale(){
        final Context context = this;
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        map.getOverlays().add(this.mScaleBarOverlay);
    }

    private void followMyLocation(){
        if (mLocationOverlay.isFollowLocationEnabled())
            mLocationOverlay.disableFollowLocation();
        else {
            mLocationOverlay.enableFollowLocation();
            mapController.zoomIn(15L);
        }
    }

    private TrackRecorder recordTrack(TrackRecorder recorder) throws ExecutionException, InterruptedException {
        if(recorder == null){
            rec.setVisibility(View.VISIBLE);
            recorder = new TrackRecorder(this.getExternalFilesDir(null),map,mLocationOverlay,getNameLocation(mLocationOverlay.getMyLocation()));
            recorder.start();
        }else{
            rec.setVisibility(View.INVISIBLE);
            recorder.exit();
            recorder = null;
        }
        return recorder;
    }

    private String getElevation(GeoPoint g) throws ExecutionException, InterruptedException {
        return getElevation(g.getLatitude(),g.getLongitude());
    }

    private String getElevation(double latitude, double longitude) throws ExecutionException, InterruptedException {
        // guarda qui: https://www.opentopodata.org/datasets/eudem/

        /*String url = "http://gisdata.usgs.gov/"
                + "xmlwebservices2/elevation_service.asmx/"
                + "getElevation?X_Value=" + String.valueOf(longitude)
                + "&Y_Value=" + String.valueOf(latitude)
                + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";*/
        String url = "https://api.opentopodata.org/v1/eudem25m?locations="+latitude+","+longitude;
        return new RetrieveElevationTask().execute(latitude,longitude).get();
    }

    private String getNameLocation(GeoPoint g) throws ExecutionException, InterruptedException {
        String name = new RetriveNameLocationTask().execute(g.getLatitude(),g.getLongitude()).get();
        //String name = "ciao";
        return name;
    }


    private void removeRoute(){
        if(routeMarkers != null){
            for(Marker m: routeMarkers)
                map.getOverlays().remove(m);
            routeMarkers = null;
        }
        if(route != null) {
            map.getOverlays().remove(route);
            route = null;
        }
        map.invalidate();
    }

    private File[] loadFilesTrack(){
        pathToSave = this.getExternalFilesDir(null);
        pathToSave.mkdirs();
        File[] files = new File(String.valueOf(pathToSave)).listFiles();
        return files;
    }

    private void loadTracks(){
        File[] files = loadFilesTrack();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        for(File f: files) {
            Polyline track = new Polyline(map);
            track.getOutlinePaint().setStrokeCap(Paint.Cap.ROUND);
            new Thread() {
                public void run() {
                    try {
                        System.out.println(f);
                        // parse XML file
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        Document doc = db.parse(f);
                        doc.getDocumentElement().normalize();
                        NodeList list = doc.getElementsByTagName("trkpt");

                        for (int i = 0; i < list.getLength(); i++) {
                            Node node = list.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element = (Element) node;
                                String e = element.getElementsByTagName("ele").item(0).getTextContent();
                                String t = element.getElementsByTagName("time").item(0).getTextContent();
                                Double lat = Double.parseDouble(((Element) node).getAttribute("lat"));
                                Double lon = Double.parseDouble(((Element) node).getAttribute("lon"));
                                track.addPoint(new GeoPoint(lat, lon));
                            }
                        }
                        map.getOverlays().add(track);
                        System.out.println(f+" ####### "+track.getActualPoints().size());
                    } catch (ParserConfigurationException | IOException | SAXException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    private void touchOverlay() {
        Overlay touchOverlay = new Overlay(){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                if(markerLocation!=null) {
                    map.getOverlays().remove(markerLocation);
                    map.invalidate();
                }
                //final Drawable marker = AppCompatResources.getDrawable(this,R.drawable.ic_menu_mylocation);
                /*Projection proj = mapView.getProjection();
                GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
                ArrayList<OverlayItem> overlayArray = new ArrayList<OverlayItem>();
                OverlayItem mapItem = new OverlayItem("", "", new GeoPoint(loc.getLatitude(), loc.getLongitude()));
                //mapItem.setMarker(marker);

                overlayArray.add(mapItem);
                if(anotherItemizedIconOverlay==null){
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                    mapView.invalidate();
                }else{
                    mapView.getOverlays().remove(anotherItemizedIconOverlay);
                    mapView.invalidate();
                    anotherItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getApplicationContext(), overlayArray,null);
                    mapView.getOverlays().add(anotherItemizedIconOverlay);
                }*/
                //      dlgThread();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e, MapView mapView){
                removeRoute();
                map.invalidate();
                System.out.println(route);
                return true;
            }
        };
        map.getOverlays().add(touchOverlay);
    }

    public void savePreference(String key, String value){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",0);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key,value);
        myEdit.apply();
    }

    public String loadPreference(String key){
        SharedPreferences sh = getSharedPreferences("MySharedPref", 0);
        String s1 = sh.getString(key, "Spinal");
        return s1;
    }

    public void createRoute(String vehicles){
        EditText editS = findViewById(R.id.start_waypoint);
        EditText editE = findViewById(R.id.end_waypoint);
        String start = editS.getText().toString();
        String end = editE.getText().toString();
        removeRoute();
        Road road = Geocoding.getRoad(context, start, end, vehicles);
        route = Geocoding.getRoute(road);
        map.getOverlays().add(route);
        routeMarkers = Geocoding.getMarkers(context,map,road);
        for(Marker m: routeMarkers)
            map.getOverlays().add(m);
        map.invalidate();
    }

    private void startServices(){

        new Thread(){
            public void run() {
                while(true) {
                    if(mLocationOverlay.getMyLocation() != null)
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pos.setText(mLocationOverlay.getMyLocation().toString()+ " " + mLocationOverlay.isFollowLocationEnabled());
                            }
                        });
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void setListeners(){
        findViewById(R.id.my_position_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followMyLocation();
                try {
                    if(mLocationOverlay.getMyLocation()!=null)
                        logger.setText(String.valueOf(getElevation(mLocationOverlay.getMyLocation())));
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //start and stop recording
        findViewById(R.id.record_btn).setOnClickListener(new View.OnClickListener() {
            TrackRecorder recorder;
            @Override
            public void onClick(View view) {
                try {
                    followMyLocation();
                    recorder = recordTrack(recorder);
                    isRecording = !isRecording;
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        //update elevation of center map
        map.addMapListener(new DelayedMapListener (new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                try {
                    logger.setText(getElevation((GeoPoint) map.getMapCenter()));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        },200));

        /*spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
                Object item = parent.getItemAtPosition(pos);
                Intent intent = getIntent();
                String newmap = intent.getStringExtra("map");
                if (newmap != null) {
                    setMap(newmap);
                    intent.removeExtra("map");
                }else
                    setMap(item.toString());
            }

            public void onNothingSelected(AdapterView<?> parent){}
        });*/

        waymark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked)
                    map.getOverlays().add(waymarkLayer);
                else
                    map.getOverlays().remove(waymarkLayer);
            }
        });

        seamark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked)
                    map.getOverlays().add(seamarkLayer);
                else
                    map.getOverlays().remove(seamarkLayer);
            }
        });

        hikebikemark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked)
                    map.getOverlays().add(hikebikeLayer);
                else
                    map.getOverlays().remove(hikebikeLayer);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GeoPoint location = Geocoding.reverseGeocoding(query);
                if(location != null){
                    map.getController().animateTo(location);
                    markerLocation.setPosition(location);
                    markerLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(markerLocation);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
            }
        });

        findViewById(R.id.rout_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View search_layout = findViewById(R.id.search_layout);
                if (search_layout.getVisibility() == VISIBLE){
                    findViewById(R.id.search_layout).setVisibility(GONE);
                    findViewById(R.id.route_layout).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.search_layout).setVisibility(VISIBLE);
                    findViewById(R.id.route_layout).setVisibility(GONE);
                }
            }
        });

        findViewById(R.id.car_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute(OSRMRoadManager.MEAN_BY_CAR );
            }
        });

        findViewById(R.id.bike_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute(OSRMRoadManager.MEAN_BY_BIKE );
            }
        });

        findViewById(R.id.foot_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoute(OSRMRoadManager.MEAN_BY_FOOT );
            }
        });


        findViewById(R.id.map_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectMapActivity.class);
                intent.putExtra("task","map");
                startActivity(intent);
            }
        });

        findViewById(R.id.track_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectMapActivity.class);
                intent.putExtra("task","track");
                startActivity(intent);
            }
        });



        touchOverlay();
    }



    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (!FIRST_START)
            setMap(newMap);
        else
            FIRST_START = false;

        if (map != null) {
            if (!isRecording) {
                map.onResume(); //needed for compass, my location overlays, v6.0.0 and u
                mLocationOverlay.enableMyLocation();
            }
        }
    }

    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        if (map != null) {
            if(!isRecording) {
                map.onPause();
                //needed for compass, my location overlays, v6.0.0 and up
                mLocationOverlay.disableMyLocation();
            }
        }
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }

    // for Sport Spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(),text,Toast.LENGTH_SHORT).show();
    }

    // for Sport Spinner
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*public LocationListener getLocationListener() {
        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.d(TAG, "onLocationChanged: Provider = " + location.getProvider());
                Log.d(TAG, "onLocationChanged: accuracy = " + location.getAccuracy());
                Log.d(TAG,
                        "onLocationChanged: lon = " + location.getLongitude() + " lat = " + location.getLatitude()
                                + " alt = " + location.getAltitude());
                Bundle extras = location.getExtras();
                for (String s : extras.keySet()) {
                    Log.d(TAG, "onLocationChanged: " + s + " = " + extras.get(s));
                }
                mapController.setCenter(new GeoPoint(location));
                mapController.setZoom(15.);
                logger.setText(String.valueOf((location.getAltitude())));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "onStatusChanged: " + provider + " status = " + status);
                extras.keySet();
                for (String s : extras.keySet()) {
                    Log.d(TAG, "onStatusChanged: " + s + " = " + extras.get(s));
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "onProviderEnabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "onProviderDisabled: " + provider);
            }
        };
        return mLocationListener;
    }*/


    /*private void tileProvider() {
        map.setTileSource(TileSourceFactory.USGS_TOPO);
        MapTileProviderBasic provider = new MapTileProviderBasic(this, new OnlineTileSourceBase("MGRS",0,15,256,"PNG", new String[0]) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                BoundingBox bbox=tile2boundingBox(MapTileIndex.getX(pMapTileIndex) ,MapTileIndex.getY(pMapTileIndex),  MapTileIndex.getZoom(pMapTileIndex));
                String baseUrl ="http://egeoint.nrlssc.navy.mil/arcgis/rest/services/usng/USNG_93/MapServer/export?dpi=96&transparent=true&format=png24&bbox="+bbox.west+","+bbox.south+","+bbox.east+","+bbox.north+"&size=256,256&f=image";
                logger.setText(baseUrl);

                return baseUrl;
            }
        });
        TilesOverlay layer = new TilesOverlay(provider, this);
        layer.setLoadingBackgroundColor(Color.TRANSPARENT);
        layer.setLoadingLineColor(Color.TRANSPARENT);
        map.getOverlays().add(layer);
    }

    class BoundingBox {
        double north;
        double south;
        double east;
        double west;
    }
    BoundingBox tile2boundingBox(final int x, final int y, final int zoom) {
        BoundingBox bb = new BoundingBox();
        bb.north = tile2lat(y, zoom);
        bb.south = tile2lat(y + 1, zoom);
        bb.west = tile2lon(x, zoom);
        bb.east = tile2lon(x + 1, zoom);
        return bb;
    }

    static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }*/

//tileProvider();
        /*map.addMapListener(new DelayedMapListener(new MapListener() {
            public boolean onZoom(final ZoomEvent e) {
                //do something
                return true;
            }

            public boolean onScroll(final ScrollEvent e) {
                double result = Double.NaN;
                Double latitude = e.getSource().getMapCenter().getLatitude();
                Double longitude = e.getSource().getMapCenter().getLongitude();
                HttpURLConnection urlConnection = null;

                try {
                    URL url = new URL("http://maps.googleapis.com/maps/api/elevation/"
                            + "xml?locations=" + String.valueOf(latitude)
                            + "," + String.valueOf(longitude)
                            + "&sensor=true");
                    //"http://maps.googleapis.com/maps/api/elevation/xml?locations=51,0&sensor=true"
                    logger.setText(url.toString());
                    urlConnection = (HttpURLConnection) url
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        logger.setText(current);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }

                return true;
            }
        }, 100 ));*/


}