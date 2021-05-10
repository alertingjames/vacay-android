package com.mv.vacay.models;

import java.util.ArrayList;

/**
 * Created by a on 2/15/2017.
 */

public class NewBeautyEntity {
    int _idx=0;
    String firstName="";
    String lastName="";
    String fullName="";
    String photoUrl="";
    int resPhoto=0;
    String email="";
    String password="";
    String location="";
    String companyName="";

    ArrayList<BeautyServiceEntity> beautyServiceEntities=new ArrayList<>();
    ArrayList<BeautyProductEntity> beautyProductEntities=new ArrayList<>();

    public NewBeautyEntity(){}

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setResPhoto(int resPhoto) {
        this.resPhoto = resPhoto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setBeautyServiceEntities(ArrayList<BeautyServiceEntity> beautyServiceEntities) {
        this.beautyServiceEntities.addAll(beautyServiceEntities) ;
    }

    public void setBeautyProductEntities(ArrayList<BeautyProductEntity> beautyProductEntities) {
        this.beautyProductEntities.addAll( beautyProductEntities);
    }

    public int get_idx() {
        return _idx;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName+" "+lastName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getResPhoto() {
        return resPhoto;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getLocation() {
        return location;
    }

    public String getCompanyName() {
        return companyName;
    }

    public ArrayList<BeautyServiceEntity> getBeautyServiceEntities() {
        return beautyServiceEntities;
    }

    public ArrayList<BeautyProductEntity> getBeautyProductEntities() {
        return beautyProductEntities;
    }
}
