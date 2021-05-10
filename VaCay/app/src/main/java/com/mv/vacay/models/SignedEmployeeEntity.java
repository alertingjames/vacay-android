package com.mv.vacay.models;

/**
 * Created by a on 4/11/2017.
 */

public class SignedEmployeeEntity {
    String idx="0";
    String name="";
    String photoUrl="";

    public SignedEmployeeEntity(){

    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getIdx() {
        return idx;
    }

    public String getName() {
        return name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
