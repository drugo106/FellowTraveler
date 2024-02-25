package com.example.fellowtraveler;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrackHolder extends RecyclerView.ViewHolder {

    TextView name,length,time;
    Button delete, show;
    View itemLayout;

    public TrackHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.grid_item_name);
        length = itemView.findViewById(R.id.grid_item_length);
        time = itemView.findViewById(R.id.grid_item_duration);
        delete = itemView.findViewById(R.id.grid_item_delete);
        show = itemView.findViewById(R.id.grid_item_view);
        itemLayout = itemView.findViewById(R.id.item_layout);

    }


}