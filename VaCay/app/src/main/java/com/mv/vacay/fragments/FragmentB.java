package com.mv.vacay.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

/**
 * Created by a on 2016.11.02.
 */
public class FragmentB extends Fragment {
    TextView friend_firstname,friend_lastname,friend_survey_quest,friend_ages_range,friend_city,friend_job,friend_education,friend_interests;
    TextView user_firstname,user_lastname,user_survey_quest,user_ages_range,user_city,user_job,user_education,user_interests;
    CircularImageView friend_photo,user_photo,back;
    CircularNetworkImageView friend_photoNet,user_photoNet;
    ImageLoader _imageLoader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.second_frag, container, false);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        friend_firstname=(TextView)v.findViewById(R.id.friend_firstname);
        friend_firstname.setText(Commons.userEntity.get_firstName());

        friend_lastname=(TextView)v.findViewById(R.id.friend_lastname);
        friend_lastname.setText(Commons.userEntity.get_lastName());

        friend_survey_quest=(TextView)v.findViewById(R.id.friend_survey_quest);
        friend_survey_quest.setText(Commons.userEntity.get_survey_quest());

        friend_ages_range=(TextView)v.findViewById(R.id.friend_txv_age_range);
        friend_ages_range.setText(Commons.userEntity.get_age_range());

        friend_city=(TextView)v.findViewById(R.id.friend_txv_city);
        friend_city.setText(Commons.userEntity.get_city());

        friend_job=(TextView)v.findViewById(R.id.friend_txv_job);
        friend_job.setText(Commons.userEntity.get_job());

        friend_education=(TextView)v.findViewById(R.id.friend_txv_education);
        friend_education.setText(Commons.userEntity.get_education());

        friend_interests=(TextView)v.findViewById(R.id.friend_txv_interest);
        friend_interests.setText(Commons.userEntity.get_interest());

        friend_photo=(CircularImageView) v.findViewById(R.id.friendphoto);
        friend_photoNet=(CircularNetworkImageView) v.findViewById(R.id.friendphotoNet);

        if(Commons.userEntity.get_photoUrl().length()<1000){
            friend_photoNet.setVisibility(View.VISIBLE);
            friend_photoNet.setImageUrl(Commons.userEntity.get_photoUrl(),_imageLoader);
        }
        else {
            friend_photoNet.setVisibility(View.GONE);
            friend_photo.setImageBitmap(base64ToBitmap(Commons.userEntity.get_photoUrl()));
        }

        user_firstname=(TextView)v.findViewById(R.id.firstname);
        user_firstname.setText(Commons.thisEntity.get_firstName());

        user_lastname=(TextView)v.findViewById(R.id.lastname);
        user_lastname.setText(Commons.thisEntity.get_lastName());

        user_survey_quest=(TextView)v.findViewById(R.id.survey_quest);
        user_survey_quest.setText(Commons.thisEntity.get_survey_quest());

        user_ages_range=(TextView)v.findViewById(R.id.txv_age_range);
        user_ages_range.setText(Commons.thisEntity.get_age_range());

        user_city=(TextView)v.findViewById(R.id.txv_city);
        user_city.setText(Commons.thisEntity.get_city());

        user_job=(TextView)v.findViewById(R.id.txv_job);
        user_job.setText(Commons.thisEntity.get_job());

        user_education=(TextView)v.findViewById(R.id.txv_education);
        user_education.setText(Commons.thisEntity.get_education());

        user_interests=(TextView)v.findViewById(R.id.txv_interest);
        user_interests.setText(Commons.thisEntity.get_interest());

        user_photo=(CircularImageView) v.findViewById(R.id.photo);
        user_photoNet=(CircularNetworkImageView) v.findViewById(R.id.photoNet);

        if(Commons.thisEntity.get_photoUrl().length()< 1000){
            user_photoNet.setVisibility(View.VISIBLE);
            user_photoNet.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);
        }
        else {
            user_photoNet.setVisibility(View.GONE);
            user_photo.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        return v;
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public static FragmentB newInstance(String text) {

        FragmentB f = new FragmentB();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
}
