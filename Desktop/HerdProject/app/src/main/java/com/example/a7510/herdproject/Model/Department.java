package com.example.a7510.herdproject.Model;

//Class that holds the courses data
public class Department {
    private String Name;
    private String Image;
    private String MenuId;
    private String TotalPeople;
    private String Description;


    public Department(){

    }

    public Department(String name, String image, String menuId, String peopleTotal, String description){
        Name = name;
        Image = image;
        MenuId = menuId;
        TotalPeople = peopleTotal;
        Description = description;
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

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getPeopleTotal() {
        return TotalPeople;
    }

    public void setPeopleTotal(String peopleTotal) {
        TotalPeople = peopleTotal;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
