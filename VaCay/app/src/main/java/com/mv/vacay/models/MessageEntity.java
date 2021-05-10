package com.mv.vacay.models;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by a on 2016.11.05.
 */
public class MessageEntity {
    int _idx=0;
    String mail_id="0";
    String _userfullname="";
    String _username="";
    String _useremail="";
    String _usermessage="";
    int _resId=0;
    String _imageUrl="";
    String _photoUrl="";
    Bitmap _bitmap=null;
    String _requestLocation="";
    LatLng _requestLatLng=null;
    String _request_date="";
    String _service_reqdate="";
    String _service="";
    String _status="";
    boolean _message_flag=false;

    public MessageEntity(){
    }

    public String get_status() {
        return _status;
    }

    public void set_status(String _status) {
        this._status = _status;
    }

    public String get_service_reqdate() {
        return _service_reqdate;
    }

    public void set_service_reqdate(String _service_reqdate) {
        this._service_reqdate = _service_reqdate;
    }

    public void set_request_date(String _request_date) {
        this._request_date = _request_date;
    }

    public void set_service(String _service) {
        this._service = _service;
    }

    public String get_request_date() {
        return _request_date;
    }

    public String get_service() {
        return _service;
    }

    public void setMail_id(String mail_id) {
        this.mail_id = mail_id;
    }

    public String getMail_id() {
        return mail_id;
    }

    public void set_userfullname(String _userfullname) {
        this._userfullname = _userfullname;
    }

    public void set_username(String _username) {
        this._username = _username;
    }

    public void set_requestLocation(String _requestLocation) {
        this._requestLocation = _requestLocation;
    }

    public String get_username() {
        return _username;
    }

    public String get_requestLocation() {
        return _requestLocation;
    }

    public void set_idx(int idx){this._idx=idx;}
    public int get_idx(){return _idx;}

    public void setUserfullname(String userfullname){this._userfullname=userfullname;}
    public String get_userfullname(){return _userfullname;}

    public void set_useremail(String useremail){this._useremail=useremail;}
    public String get_useremail(){return _useremail;}

    public void set_usermessage(String usermessage){this._usermessage=usermessage;}
    public String get_usermessage(){return _usermessage;}

    public void set_imageUrl(String imageUrl){this._imageUrl=imageUrl;}
    public String get_imageUrl(){return _imageUrl;}

    public void set_photoUrl(String photoUrl){this._photoUrl=photoUrl;}
    public String get_photoUrl(){return _photoUrl;}

    public void setRequestLocation(String requestLocation){this._requestLocation=requestLocation;}
    public String getRequestLocation(){return _requestLocation;}

    public void set_requestLatLng(LatLng requestLatLng){this._requestLatLng=requestLatLng;}
    public LatLng get_requestLatLng(){return _requestLatLng;}

    public int get_resId(){return _resId;}
    public void set_resId(int resId){this._resId=resId;}

    public Bitmap get_bitmap(){return _bitmap;}
    public void set_bitmap(Bitmap bitmap){this._bitmap=bitmap;}

    public void set_message_flag(boolean message_flag){
        this._message_flag=message_flag;
    }
    public boolean is_message_flag(){return _message_flag;}
}
