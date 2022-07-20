package com.example.fellowtraveler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationBarView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;


import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MapView map;
    private IMapController mapController;
    private MyLocationNewOverlay mLocationOverlay;
    private ArrayList<OverlayItem> items;
    private TextView logger;
    private TextView rec;
    private TextView pos;
    private LocationListener mLocationListener;
    private DelayedMapListener mapListener;
    private boolean isRecording = false;

    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ScaleBarOverlay mScaleBarOverlay;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here


        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
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

        Spinner spinner = (Spinner) findViewById(R.id.sport_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sport_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        rec = findViewById(R.id.rec);
        logger = findViewById(R.id.log);
        pos = findViewById(R.id.pos);
        map = findViewById(R.id.mapView);

        setMap();

        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        //map.addMapListener(new MyMapListener());
        mapController = map.getController();
        mapController.setZoom(15.);
        myLocation();

        GeoPoint startPoint = new GeoPoint(45.464664, 9.188540);

        //logger.setText(new GpsMyLocationProvider(getApplicationContext()).getLastKnownLocation());
        mapController.setCenter(startPoint);
        mapScale();
        setMarkerOnStartPosition(startPoint);
        touchOverlay();
        mLocationListener = getLocationListener();

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

        findViewById(R.id.my_position_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                followMyLocation();
                try {
                    if(mLocationOverlay.getMyLocation()!=null)
                        logger.setText(String.valueOf(getAltitude(mLocationOverlay.getMyLocation())));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

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

        map.addMapListener(new DelayedMapListener (new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                try {
                    logger.setText(getAltitude((GeoPoint) map.getMapCenter()));
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

    }

    private void setMap() {
        String[] tileURLs = {"http://a.tile.thunderforest.com/cycle/",
                "http://b.tile.thunderforest.com/cycle/",
                "http://c.tile.thunderforest.com/cycle/"};

        ITileSource OCM =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        map.setTileSource(OCM);
        //map.setTileSource(TileSourceFactory.MAPNIK);
    }

    private void setMarkerOnStartPosition(GeoPoint startPoint) {
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
    }

    public void myLocation(){
        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()),map);
        mLocationOverlay.enableMyLocation();
        //mLocationOverlay.setEnableAutoStop(false);
        map.getOverlays().add(mLocationOverlay);
        //mLocationOverlay.enableFollowLocation();
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

    private String getAltitude(GeoPoint g) throws ExecutionException, InterruptedException {
        return getAltitude(g.getLatitude(),g.getLongitude());
    }

    private String getAltitude(double latitude, double longitude) throws ExecutionException, InterruptedException {
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
        return new RetriveNameLocationTask().execute(g.getLatitude(),g.getLongitude()).get();
    }

    private void touchOverlay() {
        Overlay touchOverlay = new Overlay(){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {

                //final Drawable marker = AppCompatResources.getDrawable(this,R.drawable.ic_menu_mylocation);
                Projection proj = mapView.getProjection();
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
                }
                //      dlgThread();
                return true;
            }
        };
        map.getOverlays().add(touchOverlay);
    }

    public LocationListener getLocationListener() {
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
    }



    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
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