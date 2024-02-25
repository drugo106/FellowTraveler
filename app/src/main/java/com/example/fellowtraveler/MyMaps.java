package com.example.fellowtraveler;

import android.content.Context;
import android.graphics.Color;

import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.TilesOverlay;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyMaps {
    // Open Cycle Map is common that doesn't download tiles and i think that is the cause that make some thread (RetriveElevationTask)
    //crash in C level (tombsone)

    public static ITileSource MAPNIK(){
        return TileSourceFactory.MAPNIK;
    }

    public static ITileSource CyclOSM(){
        String[] tileURLs = {"https://a.tile-cyclosm.openstreetmap.fr/cyclosm/",
                "https://b.tile-cyclosm.openstreetmap.fr/cyclosm/",
                "https://c.tile-cyclosm.openstreetmap.fr/cyclosm/"};

        ITileSource CyclOSM =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png",
                        tileURLs);
        return CyclOSM;
    }

    public static ITileSource OCM(){
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
        return OCM;
    }

    public static ITileSource Spinal(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/spinal-map/",
                "http://b.tile.thunderforest.com/spinal-map/",
                "http://c.tile.thunderforest.com/spinal-map/"};

        ITileSource Spinal =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Spinal;
    }


    public static ITileSource Transport(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/transport/",
                "http://b.tile.thunderforest.com/transport/",
                "http://c.tile.thunderforest.com/transport/"};

        ITileSource Transport =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Transport;
    }

    public static ITileSource TransportDark(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/transport-dark/",
                "http://b.tile.thunderforest.com/transport-dark/",
                "http://c.tile.thunderforest.com/transport-dark/"};

        ITileSource Atlas =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Atlas;
    }

    public static ITileSource Landscape(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/landscape/",
                "http://b.tile.thunderforest.com/landscape/",
                "http://c.tile.thunderforest.com/landscape/"};

        ITileSource Landscape =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Landscape;
    }

    public static ITileSource Outdoors(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/outdoors/",
                "http://b.tile.thunderforest.com/outdoors/",
                "http://c.tile.thunderforest.com/outdoors/"};

        ITileSource Outdoors =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Outdoors;
    }

    public static ITileSource Atlas(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/atlas/",
                "http://b.tile.thunderforest.com/atlas/",
                "http://c.tile.thunderforest.com/atlas/"};

        ITileSource Atlas =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Atlas;
    }

    public static ITileSource MobileAtlas(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/mobile-atlas/",
                "http://b.tile.thunderforest.com/mobile-atlas/",
                "http://c.tile.thunderforest.com/mobile-atlas/"};

        ITileSource MobileAtlas =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return MobileAtlas;
    }

    public static ITileSource Pioneer(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/pioneer/",
                "http://b.tile.thunderforest.com/pioneer/",
                "http://c.tile.thunderforest.com/pioneer/"};

        ITileSource Pioneer =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Pioneer;
    }

    public static ITileSource Neighbourhood(){
        String[] tileURLs = {"http://a.tile.thunderforest.com/neighbourhood/",
                "http://b.tile.thunderforest.com/neighbourhood/",
                "http://c.tile.thunderforest.com/neighbourhood/"};

        ITileSource Neighbourhood =
                new XYTileSource("Open Cycle Map",
                        0,
                        19,
                        512,
                        ".png?apikey=774e562d81c94d36bf6f489e0f0a33ec",
                        tileURLs,
                        "from open cycle map");
        return Neighbourhood;
    }

    public static ITileSource ORM(){

        String[] tileURLs = {"http://a.tiles.openrailwaymap.org/standard/",
                "http://b.tiles.openrailwaymap.org/standard/",
                "http://c.tiles.openrailwaymap.org/standard/"};

        ITileSource ORM =
                new XYTileSource("Open Railway Map",
                        0,
                        19,
                        512,
                        ".png",
                        tileURLs,
                        "© OpenStreetMap contributors");
        return ORM;
    }

    public static ITileSource WMT(){

        String[] tileURLs = {"https://tile.waymarkedtrails.org/hiking/"};

        ITileSource WMT =
                new XYTileSource("Waymarked Trails",
                        0,
                        17,
                        512,
                        ".png",
                        tileURLs,
                        "© waymarkedtrails.org, OpenStreetMap contributors, CC by-SA 3.0 ");
        return WMT;
    }

    public static TilesOverlay waymarkOverlay(Context context){
        MapTileProviderBasic provider = new MapTileProviderBasic(context, new OnlineTileSourceBase("Waymarked Trails", 0, 17, 512, ".png", new String[]{"https://tile.waymarkedtrails.org/hiking/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                System.out.println(MapTileIndex.getZoom(pMapTileIndex)+" "+MapTileIndex.getX(pMapTileIndex)+" "+MapTileIndex.getY(pMapTileIndex));
                int z = MapTileIndex.getZoom(pMapTileIndex);
                int x = MapTileIndex.getX(pMapTileIndex);
                int y = MapTileIndex.getY(pMapTileIndex);
                String url = "https://tile.waymarkedtrails.org/hiking/"+z+"/"+x+"/"+y+".png";
                return url;
            }
        });
        TilesOverlay layer = new TilesOverlay(provider, context);
        layer.setLoadingBackgroundColor(Color.TRANSPARENT);
        layer.setLoadingLineColor(Color.TRANSPARENT);
        return layer;
    }

}
