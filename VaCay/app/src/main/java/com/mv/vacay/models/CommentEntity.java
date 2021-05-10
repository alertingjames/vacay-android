package com.mv.vacay.models;

/**
 * Created by sonback123456 on 12/12/2017.
 */

public class CommentEntity {
    String idx="";
    String info_id="";
    String photoUrl="";
    String name="";
    String text="";
    String imageUrl="";

    public CommentEntity(){

    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setInfo_id(String info_id) {
        this.info_id = info_id;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIdx() {
        return idx;
    }

    public String getInfo_id() {
        return info_id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
