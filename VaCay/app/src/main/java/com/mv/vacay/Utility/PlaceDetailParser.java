package com.mv.vacay.Utility;

import android.util.Log;

import com.mv.vacay.PlaceDetail.PlaceDetailBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailParser {

    private String jsonData;
    private PlaceDetailBean detailBean;

    public PlaceDetailParser(String data){
        jsonData = data;
    }

    public String getStatus(){
        String status = null;

        try{
            JSONObject jsonObject = new JSONObject(jsonData);
            status = jsonObject.getString("status");
        }catch(JSONException e){
            Log.e("JSON Parser", "Unable to get status", e);
        }

        return status;
    }

    public PlaceDetailBean getPlaceDetail() throws Exception {
        detailBean = new PlaceDetailBean();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject result = jsonObject.getJSONObject("result");
            if(result.has("formatted_address")){
                detailBean.setFormatted_address(result.getString("formatted_address"));
            }else {
                detailBean.setFormatted_address("Not available");
            }
            if(result.has("formatted_phone_number")){
                detailBean.setFormatted_phone_number(result.getString("formatted_phone_number"));
            }else {
                detailBean.setFormatted_phone_number("Not available");
            }
            if(result.has("international_phone_number")){
                detailBean.setInternational_phone_number(result.getString("international_phone_number"));
            }else {
                detailBean.setInternational_phone_number("Not available");
            }
            if(result.has("name")){
                detailBean.setName(result.getString("name"));
            }else {
                detailBean.setName("Not available");
            }
            if(result.has("geometry")){
                JSONObject geometry = result.getJSONObject("geometry");
                if(geometry.has("location")){
                    JSONObject location = geometry.getJSONObject("location");
                    detailBean.setLat(location.getDouble("lat"));
                    detailBean.setLng(location.getDouble("lng"));
                }
                else{
                    detailBean.setLat(0.0);
                    detailBean.setLng(0.0);
                }
            }
            else{
                detailBean.setLat(0.0);
                detailBean.setLng(0.0);
            }
            if(result.has("id")){
                detailBean.setPlace_id(result.getString("id"));
            }else {
                detailBean.setPlace_id(null);
            }
            if(result.has("rating")){
                detailBean.setRating((float) result.getDouble("rating"));
            }else {
                detailBean.setRating(0.0f);
            }
            if(result.has("photos")){
                JSONArray photosArray = result.getJSONArray("photos");
                String[]photoRef = new String[photosArray.length()];
                for(int i = 0; i < photosArray.length(); i++){
                    JSONObject photo = photosArray.getJSONObject(i);
                    photoRef[i] = photo.getString("photo_reference");
                }
                detailBean.setPhotos(photoRef);
            }else {
                detailBean.setPhotos(null);
            }
            if(result.has("reviews")){
                JSONArray reviewArray = result.getJSONArray("reviews");
                PlaceDetailBean.Review reviews[] = new PlaceDetailBean.Review[reviewArray.length()];
                for(int i = 0;i < reviewArray.length(); i++){
                    PlaceDetailBean.Review review = detailBean.new Review();
                    JSONObject reviewObj = reviewArray.getJSONObject(i);
                    if(reviewObj.has("author_name")){
                        review.setAuthor_name(reviewObj.getString("author_name"));
                    }else {
                        review.setAuthor_name(null);
                    }
                    if(reviewObj.has("author_url")){
                        review.setAuthor_url(reviewObj.getString("author_url"));
                    }else {
                        review.setAuthor_url(null);
                    }
                    if(reviewObj.has("rating")){
                        review.setRating((float)reviewObj.getDouble("rating"));
                    }else {
                        review.setRating(0.0f);
                    }
                    if(reviewObj.has("text")){
                        review.setAuthor_text(reviewObj.getString("text"));
                    }else {
                        review.setAuthor_text(null);
                    }
                    if(reviewObj.has("time")){
                        review.setWritten_time(reviewObj.getLong("time"));
                    }else {
                        review.setWritten_time(0);
                    }
                    reviews[i] = review;
                }
                detailBean.setReviews(reviews);

            }else {
                detailBean.setReviews(null);
            }
        }catch(JSONException ex){
            Log.e("PlaceDetailParser", ex.toString());
            throw new Exception("Something went wrong on server.");
        }
        return detailBean;
    }
}
