package com.example.a7510.herdproject.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a7510.herdproject.Interface.ItemClickListener;
import com.example.a7510.herdproject.R;

public class DepartmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //Class that makes the courses clickable
    public TextView department_name;
    public ImageView department_image;

    private ItemClickListener itemClickListener;

    public DepartmentViewHolder(View itemView) {
        super(itemView);

        department_name = (TextView)itemView.findViewById(R.id.department_name);
        department_image = (ImageView)itemView.findViewById(R.id.department_image);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
