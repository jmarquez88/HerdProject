package com.example.a7510.herdproject.ViewHolder;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a7510.herdproject.Interface.ItemClickListener;
import com.example.a7510.herdproject.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    //Class that makes the course sections clickable
    public TextView textMenuName;
    public ImageView imageView;

    private ItemClickListener itemClickListener;

    public MenuViewHolder(View itemView) {
        super(itemView);

        textMenuName = (TextView)itemView.findViewById(R.id.college_name);
        imageView = (ImageView)itemView.findViewById(R.id.college_image);

        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
