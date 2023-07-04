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
