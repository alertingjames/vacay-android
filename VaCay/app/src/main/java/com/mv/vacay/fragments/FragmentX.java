package com.mv.vacay.fragments;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.InboxMessageAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sonback123456 on 11/26/2017.
 */

public class FragmentX extends Fragment {
    ArrayList<String> images=new ArrayList<>();
    Gallery gallery;
    ImageView imageView;
    NetworkImageView imageViewNet;
    TextView note;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_x, container, false);
        gallery = (Gallery) v.findViewById(R.id.gallery1);

        imageView = (ImageView) v.findViewById(R.id.image1);
        imageViewNet = (NetworkImageView) v.findViewById(R.id.imageNet);
        note=(TextView)v.findViewById(R.id.note);

        return v;
    }

    public static FragmentX newInstance(String text) {

        FragmentX f = new FragmentX();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        images.clear();
        if(Commons.mediaEntity.getImageA().length()>0 && !Commons.mediaEntity.getImageA().equals("None"))
            images.add(Commons.mediaEntity.getImageA());
        if(Commons.mediaEntity.getImageB().length()>0 && !Commons.mediaEntity.getImageB().equals("None"))
            images.add(Commons.mediaEntity.getImageB());
        if(Commons.mediaEntity.getImageC().length()>0 && !Commons.mediaEntity.getImageC().equals("None"))
            images.add(Commons.mediaEntity.getImageC());
        if(Commons.mediaEntity.getImageD().length()>0 && !Commons.mediaEntity.getImageD().equals("None"))
            images.add(Commons.mediaEntity.getImageD());
        if(Commons.mediaEntity.getImageE().length()>0 && !Commons.mediaEntity.getImageE().equals("None"))
            images.add(Commons.mediaEntity.getImageE());
        if(Commons.mediaEntity.getImageF().length()>0 && !Commons.mediaEntity.getImageF().equals("None"))
            images.add(Commons.mediaEntity.getImageF());

        try {
            if(images.size()>0){
                String imageUrl=images.get(0);
                if(imageUrl.length()<1000){
                    imageViewNet.setVisibility(View.VISIBLE);
                    imageViewNet.setImageUrl(imageUrl,VaCayApplication.getInstance()._imageLoader);
                }
                if(imageUrl.length()>1000) {
                    imageViewNet.setVisibility(View.GONE);
                    imageView.setImageBitmap(base64ToBitmap(imageUrl));
                }
                note.setVisibility(View.GONE);
            }else {
                note.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(),"No Picture ...", Toast.LENGTH_SHORT).show();
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            note.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),"No Picture ...", Toast.LENGTH_SHORT).show();
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
            note.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),"No Picture ...", Toast.LENGTH_SHORT).show();
        }

        gallery.setAdapter(new ImageAdapter(getActivity(),images));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position,long id)
            {
                // display the images selected
                String imageUrl=images.get(position);
                if(imageUrl.length()<1000){
                    imageViewNet.setVisibility(View.VISIBLE);
                    imageViewNet.setImageUrl(imageUrl,VaCayApplication.getInstance()._imageLoader);
                }
                if(imageUrl.length()>1000) {
                    imageViewNet.setVisibility(View.GONE);
                    imageView.setImageBitmap(base64ToBitmap(imageUrl));
                }
            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<String> imageList=new ArrayList<>();
        public ImageAdapter(Context c, ArrayList<String> images)
        {
            context = c;
            imageList.addAll(images);
        }
        // returns the number of images
        public int getCount() {
            return imageList.size();
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView = new ImageView(context);
            NetworkImageView networkImageView = new NetworkImageView(context);

            String imageUrl=images.get(position);
            if(imageUrl.length()<1000){
                networkImageView.setImageUrl(imageUrl, VaCayApplication.getInstance()._imageLoader);
                networkImageView.setLayoutParams(new Gallery.LayoutParams(250, 250));
                networkImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                convertView=networkImageView;
            }else {
                imageView.setImageBitmap(base64ToBitmap(imageUrl));
                imageView.setLayoutParams(new Gallery.LayoutParams(300, 300));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                convertView=imageView;
            }

            return convertView;
        }

    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

}




























