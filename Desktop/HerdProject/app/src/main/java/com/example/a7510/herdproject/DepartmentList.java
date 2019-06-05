package com.example.a7510.herdproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.a7510.herdproject.Interface.ItemClickListener;
import com.example.a7510.herdproject.Model.Department;
import com.example.a7510.herdproject.ViewHolder.DepartmentViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DepartmentList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase db;
    DatabaseReference departmentList;

    String subjectId="";

    FirebaseRecyclerAdapter<Department, DepartmentViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);

        //Retrieve Departments data from FireBase
        db = FirebaseDatabase.getInstance();
        departmentList = db.getReference("Courses");

        recyclerView = (RecyclerView)findViewById(R.id.recycler_departments);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Retrieve the intent
        if(getIntent() != null){
            subjectId = getIntent().getStringExtra("SubjectId");
        }
        if(!subjectId.isEmpty() && subjectId != null){
            loadDepartmentList(subjectId);
        }
    }

    //Function that loads courses
    private void loadDepartmentList(String subjectId) {
        adapter = new FirebaseRecyclerAdapter<Department, DepartmentViewHolder>(Department.class, R.layout.department_item, DepartmentViewHolder.class, departmentList.orderByChild("MenuId").equalTo(subjectId)) {
            @Override
            protected void populateViewHolder(DepartmentViewHolder viewHolder, Department model, int position) {
                viewHolder.department_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.department_image);

                final Department local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //A new activity
                        Intent courseDetail = new Intent(DepartmentList.this, CourseDetail.class);
                        //Send the course ID to the new activity
                        courseDetail.putExtra("CourseId",adapter.getRef(position).getKey());
                        startActivity(courseDetail);
                    }
                });
            }
        };

        //Set the adapter
        recyclerView.setAdapter(adapter);
    }
}
