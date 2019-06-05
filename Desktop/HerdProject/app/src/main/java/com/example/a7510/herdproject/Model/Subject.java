package com.example.a7510.herdproject.Model;

//Class that holds the courses data for FireBase
public class Subject {
    private String Name;
    private String Image;

    public Subject(){

    }

    public Subject(String name, String image){
        Name = name;
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
