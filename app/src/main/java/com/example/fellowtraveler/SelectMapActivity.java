package com.example.fellowtraveler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class SelectMapActivity extends AppCompatActivity {

    GridView gridView;

    static final String[] MAPS = new String[] {
            "MAPNIK", "OpenTopo", "OCM", "Outdoors", "Transport", "Transport Dark", "Landscape", "Spinal", "Atlas", "Mobile Atlas", "Pioneer", "Neighbourhood"};

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Intent intent = getIntent();

        String task = intent.getStringExtra("task");
        if(task.equals("map"))
            gridSelectionMap();
        else
            gridSelectionTrack();


    }

    private void gridSelectionMap(){
        setContentView(R.layout.activity_select_map);

        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new MapAdapter(this, MAPS));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String map = (String) ((TextView) view.findViewById(R.id.grid_item_label)).getText();
                map = map.replaceAll("\\s", "");
                MainActivity.newMap = map;
                //Intent intent = new Intent(SelectMapActivity.this, MainActivity.class);
                //intent.putExtra("map",map);
                //startActivity(intent);
                finish();
            }
        });
    }

    private void gridSelectionTrack(){
        setContentView(R.layout.activity_select_track);

        RecyclerView recyclerView = findViewById(R.id.recicleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TrackAdapter(this));


    }
}

        /*findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectMapActivity.this, MainActivity.class);
                intent.putExtra("map","Spinal");
                startActivity(intent);
            }
        });*/
