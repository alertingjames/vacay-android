package com.mv.vacay.models;

import android.graphics.Bitmap;

/**
 * Created by a on 2016.11.06.
 */
public class RestaurantEntity {
    int _idx=0;
    String restaurant_name="";
    String restaurant_type="";
    String restaurant_location_url="";
    String food_menu_url="";
    String opentable_url="";
    int price=0;
    int imageRes=0;
    Bitmap imageBitmap=null;
    String _photoUrl = "";
    String _area="";

    public RestaurantEntity(){}

    public int get_idx() {
        return _idx;
    }
    public void set_idx(int idx) {
        this._idx = idx;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public int getImageRes() {
        return imageRes;
    }
    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }
    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_type() {
        return restaurant_type;
    }
    public void setRestaurant_type(String restaurant_type) {
        this.restaurant_type = restaurant_type;
    }

    public String get_area() {
        return _area;
    }
    public void set_area(String area) {
        this._area = area;
    }

    public String getRestaurant_location_url() {
        return restaurant_location_url;
    }
    public void setRestaurant_location_url(String restaurant_location_url) {
        this.restaurant_location_url = restaurant_location_url;
    }

    public String getFood_menu_url() {
        return food_menu_url;
    }
    public void setFood_menu_url(String food_menu_url) {
        this.food_menu_url = food_menu_url;
    }

    public String getOpentable_url() {
        return opentable_url;
    }
    public void setOpentable_url(String opentable_url) {
        this.opentable_url = opentable_url;
    }

    public String get_photoUrl() {
        return _photoUrl;
    }
    public void set_photoUrl(String photoUrl) {
        this._photoUrl = photoUrl;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

}
