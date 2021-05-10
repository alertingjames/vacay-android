package com.mv.vacay.models;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by a on 2016.12.22.
 */

public class GameEntity {
    int _idx=0;
    private String barName="";
    private String barType="";
    private String barImageUrl="";
    private int resImageId=0;
    private Bitmap imageBitmap=null;
    private String menuUrl="";
    private String channel="";
    private String teamName="";
    private String videoId="";
    private String videoType="";
    private String videoTypeSecondary="";
    private String specials="";
    private String salePrice="";
    private String rentPrice="";
    private String watchWithPrice="";
    private String gameName="";
    private String gameThumbnailUrl="";
    private String duaration="";
    private String knownName="";
    private double barlat=0.0f;
    private double barlng=0.0f;
    private ArrayList<String> videoIds=new ArrayList<>();
    private ArrayList<String> gameNames=new ArrayList<>();
    private ArrayList<String> gameThumbUrls=new ArrayList<>();
    private ArrayList<String> resMenus=new ArrayList<>();

    public void setVideoTypeSecondary(String videoTypeSecondary) {
        this.videoTypeSecondary = videoTypeSecondary;
    }

    public int get_idx() {
        return _idx;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setDuaration(String duaration) {
        this.duaration = duaration;
    }

    public String getDuaration() {
        return duaration;
    }


    public void setSpecials(String specials) {
        this.specials = specials;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public void setRentPrice(String rentPrice) {
        this.rentPrice = rentPrice;
    }

    public void setWatchWithPrice(String watchWithPrice) {
        this.watchWithPrice = watchWithPrice;
    }

    public String getVideoTypeSecondary() {
        return videoTypeSecondary;
    }

    public String getSpecials() {
        return specials;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public String getRentPrice() {
        return rentPrice;
    }

    public String getWatchWithPrice() {
        return watchWithPrice;
    }

    public ArrayList<String> getGameThumbUrls() {
        return gameThumbUrls;
    }

    public void setGameThumbUrls(ArrayList<String> gameThumbUrls) {
        this.gameThumbUrls.addAll(gameThumbUrls);
    }

    public void setGameThumbnailUrl(String gameThumbnailUrl) {
        this.gameThumbnailUrl = gameThumbnailUrl;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoType() {
        return videoType;
    }

    public String getGameThumbnailUrl() {
        return gameThumbnailUrl;
    }

    public void setGameNames(ArrayList<String> gameNames) {
        this.gameNames = gameNames;
    }

    public void setResMenus(ArrayList<String> resMenus) {
        this.resMenus.addAll(resMenus);
    }

    public ArrayList<String> getGameNames() {
        return gameNames;
    }

    public ArrayList<String> getResMenus() {
        return resMenus;
    }

    public void setVideoIds(ArrayList<String> videoIds) {
        this.videoIds = videoIds;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getBarType() {
        return barType;
    }

    public void setBarType(String barType) {
        this.barType = barType;
    }

    public void setBarlng(double barlng) {
        this.barlng = barlng;
    }

    public void setBarlat(double barlat) {
        this.barlat = barlat;
    }

    public void setKnownName(String knownName) {
        this.knownName = knownName;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public void setResImageId(int resImageId) {
        this.resImageId = resImageId;
    }

    public void setBarImageUrl(String barImage) {
        this.barImageUrl = barImage;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public String getBarName() {
        return barName;
    }

    public String getBarImageUrl() {
        return barImageUrl;
    }

    public int getResImageId() {
        return resImageId;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public String getChannel() {
        return channel;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getKnownName() {
        return knownName;
    }

    public String getVideoId() {
        return videoId;
    }

    public double getBarlat() {
        return barlat;
    }

    public double getBarlng() {
        return barlng;
    }

    public ArrayList<String> getVideoIds() {
        return videoIds;
    }

    public GameEntity(){}

}
