package com.mv.vacay.models;

/**
 * Created by sonback123456 on 12/2/2017.
 */

public class WaterCoolerEntity {
    String idx="";
    String userName="";
    String company="";
    String category="";
    String content="";
    String profilePhotoUrl="";
    String article="";

    public WaterCoolerEntity(){

    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getIdx() {
        return idx;
    }

    public String getUserName() {
        return userName;
    }

    public String getCompany() {
        return company;
    }

    public String getCategory() {
        return category;
    }

    public String getContent() {
        return content;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public String getArticle() {
        return article;
    }
}
