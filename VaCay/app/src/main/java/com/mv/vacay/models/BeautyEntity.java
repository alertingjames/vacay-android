package com.mv.vacay.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by a on 2016.11.09.
 */
public class BeautyEntity {
    int _idx=0;
    String _beauty_name="";
    String _beauty_category="";
    String _beauty_imageURL="";
    int  _beauty_resId=0;
    String _provider_imageURL="";
    int _provider_resId=0;
    float _beauty_price=0;
    String _description="";
    String _provider_location_url="";
    String _provider_email="";
    LatLng _providerLatlng=null;
    double _providerlat=0.0f;
    double _providerlng=0.0;
    String _provider_name="";
    String _request_location_url="";
    String _request_date="";
    float _request_price=0;
    String _request_description="";
    Bitmap _providerBitmap=null;
    Bitmap _beautyBitmap=null;

    String _requestLocation="";
    LatLng _requestLatLng=null;

    String _area="";

    public BeautyEntity(){}

    public int get_idx() {
        return _idx;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public String get_beauty_name() {
        return _beauty_name;
    }

    public void set_beauty_name(String _beauty_name) {
        this._beauty_name = _beauty_name;
    }

    public String get_beauty_category() {
        return _beauty_category;
    }

    public void set_beauty_category(String _beauty_category) {
        this._beauty_category = _beauty_category;
    }

    public String get_beauty_imageURL() {
        return _beauty_imageURL;
    }

    public void set_beauty_imageURL(String _beauty_imageURL) {
        this._beauty_imageURL = _beauty_imageURL;
    }

    public int get_beauty_resId() {
        return _beauty_resId;
    }

    public void set_beauty_resId(int _beauty_resId) {
        this._beauty_resId = _beauty_resId;
    }

    public String get_provider_imageURL() {
        return _provider_imageURL;
    }

    public void set_provider_imageURL(String _provider_imageURL) {
        this._provider_imageURL = _provider_imageURL;
    }

    public String get_area() {
        return _area;
    }
    public void set_area(String area) {
        this._area = area;
    }

    public int get_provider_resId() {
        return _provider_resId;
    }

    public void set_provider_resId(int _provider_resId) {
        this._provider_resId = _provider_resId;
    }

    public float get_beauty_price() {
        return _beauty_price;
    }

    public void set_beauty_price(float _beauty_price) {
        this._beauty_price = _beauty_price;
    }

    public float get_request_price() {
        return _request_price;
    }

    public void set_request_price(float request_price) {
        this._request_price = request_price;
    }

    public double get_providerlat() {
        return _providerlat;
    }

    public void set_providerlat(double providerlat) {
        this._providerlat = providerlat;
    }

    public double get_providerlng() {
        return _providerlng;
    }

    public void set_providerlng(double providerlng) {
        this._providerlng = providerlng;
    }

    public String get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description = _description;
    }

    public String get_request_description() {
        return _request_description;
    }

    public void set_request_description(String request_description) {
        this._request_description = request_description;
    }

    public String get_provider_location_url() {
        return _provider_location_url;
    }

    public void set_provider_location_url(String _provider_location_url) {
        this._provider_location_url = _provider_location_url;
    }

    public String get_provider_email() {
        return _provider_email;
    }

    public void set_provider_email(String provider_email) {
        this._provider_email = provider_email;
    }

    public String get_provider_name() {
        return _provider_name;
    }

    public void set_provider_name(String provider_name) {
        this._provider_name = provider_name;
    }

    public String get_request_location_url() {
        return _request_location_url;
    }

    public void set_request_location_url(String request_location_url) {
        this._request_location_url = request_location_url;
    }

    public String get_request_date() {
        return _request_date;
    }

    public void set_request_date(String request_date) {
        this._request_date = request_date;
    }

    public void setRequestLocation(String requestLocation){this._requestLocation=requestLocation;}
    public String getRequestLocation(){return _requestLocation;}

    public void set_requestLatLng(LatLng requestLatLng){this._requestLatLng=requestLatLng;}
    public LatLng get_requestLatLng(){return _requestLatLng;}

    public void set_providerLatlng(LatLng providerLatlng){this._providerLatlng=providerLatlng;}
    public LatLng get_providerLatlng(){return _providerLatlng;}

    public void set_providerBitmap(Bitmap providerBitmap){this._providerBitmap=providerBitmap;}
    public Bitmap get_providerBitmap(){return _providerBitmap;}

    public void set_beautyBitmap(Bitmap beautyBitmap){this._beautyBitmap=beautyBitmap;}
    public Bitmap get_beautyBitmap(){return _beautyBitmap;}
}
