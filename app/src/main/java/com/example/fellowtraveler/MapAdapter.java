package com.example.fellowtraveler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MapAdapter extends BaseAdapter {

    private final Context context;
    private final String[] maps;

    public MapAdapter(Context context, String[] maps) {
        this.context = context;
        this.maps = maps;

    }

    @Override
    public int getCount() {
        return maps.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


            view = inflater.inflate(R.layout.item_map, null);

            TextView textView = (TextView) view
                    .findViewById(R.id.grid_item_label);
            textView.setText(maps[position]);

            ImageView imageView = (ImageView) view
                    .findViewById(R.id.grid_item_image);

            String map = maps[position];

            switch (map) {

                case "MAPNIK":
                    imageView.setImageResource(R.drawable.mapnik);
                    break;

                case "Spinal":
                    imageView.setImageResource(R.drawable.spinal);
                    break;

                case "OCM":
                    imageView.setImageResource(R.drawable.ocm);
                    break;

                case "Outdoors":
                    imageView.setImageResource(R.drawable.outdoors);
                    break;

                case "Transport":
                    imageView.setImageResource(R.drawable.transport);
                    break;

                case "Transport Dark":
                    imageView.setImageResource(R.drawable.transport_dark);
                    break;

                case "Landscape":
                    imageView.setImageResource(R.drawable.landscape);
                    break;

                case "Atlas":
                    imageView.setImageResource(R.drawable.atlas);
                    break;

                case "Mobile Atlas":
                    imageView.setImageResource(R.drawable.modile_atlas);
                    break;

                case "Pioneer":
                    imageView.setImageResource(R.drawable.pioneer);
                    break;

                case "Neighbourhood":
                    imageView.setImageResource(R.drawable.neighbourhood);
                    break;
            }
        }

        return view;


    }
}
