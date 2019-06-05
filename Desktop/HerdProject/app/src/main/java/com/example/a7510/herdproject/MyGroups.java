package com.example.a7510.herdproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import info.hoang8f.widget.FButton;

public class MyGroups extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase db;
    DatabaseReference request;

    TextView textTotalGroups;
    FButton buttonDeleteAll;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        //Initializing Firebase variables
        db = FirebaseDatabase.getInstance();
        request = db.getReference("Courses");

        //Getting ID's
        recyclerView = (RecyclerView)findViewById(R.id.listMyGroups);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textTotalGroups = (TextView)findViewById(R.id.totalGroups);
        buttonDeleteAll = (FButton)findViewById(R.id.buttonLeaveAll);


    }
}
