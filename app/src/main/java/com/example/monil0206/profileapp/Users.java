package com.example.monil0206.profileapp;

public class Users{

    private String Name;
    private String Image;
    private String Details;

    public Users(){

    }

    public Users(String name, String image, String details) {
        Name = name;
        Image = image;
        Details = details;
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

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }
}
