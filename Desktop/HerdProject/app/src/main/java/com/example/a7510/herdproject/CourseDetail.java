package com.example.a7510.herdproject;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a7510.herdproject.Model.Department;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class CourseDetail extends AppCompatActivity {

    TextView courseName, courseTotal, courseDescription;
    ImageView courseImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnGroup;

    String courseId = "";

    FirebaseDatabase db;
    DatabaseReference courses;

    Department department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        //Firebase code
        db = FirebaseDatabase.getInstance();
        courses = db.getReference("Courses");

        //Initialize the View
        btnGroup = (FloatingActionButton)findViewById(R.id.btnGroup);
        btnGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        courseDescription = (TextView)findViewById(R.id.course_description);
        courseName = (TextView)findViewById(R.id.course_name);
        courseTotal = (TextView)findViewById(R.id.course_total_people);
        courseImage = (ImageView)findViewById(R.id.img_course);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        //Get the course ID from Intent
        if(getIntent() != null){
            courseId = getIntent().getStringExtra("CourseId");
        }

        if(!courseId.isEmpty()){
            getCourseDetail(courseId);
        }


    }

    //Function that gets course details
    private void getCourseDetail(String courseId) {
        courses.child(courseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                department = dataSnapshot.getValue(Department.class);

                //Set the image
                Picasso.with(getBaseContext()).load(department.getImage()).into(courseImage);

                //Set the the text for the components
                collapsingToolbarLayout.setTitle(department.getName());
                courseTotal.setText(department.getPeopleTotal());
                courseName.setText(department.getName());
                courseDescription.setText(department.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
