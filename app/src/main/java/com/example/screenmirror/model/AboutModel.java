package com.example.screenmirror.model;

public class AboutModel {
    int id;
    String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public AboutModel(int id, String name, String description, String picture) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
    }

    String description;
    String picture;



}
