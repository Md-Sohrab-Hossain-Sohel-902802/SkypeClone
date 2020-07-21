package com.example.skypeclone;

public class Contacts {

    String name;
    String uid;
    String status;
    String image;


    public  Contacts(){

    }

    public Contacts(String name, String uid, String status, String image) {
        this.name = name;
        this.uid = uid;
        this.status = status;
        this.image = image;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
