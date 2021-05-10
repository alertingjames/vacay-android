package com.mv.vacay.main.meetfriends;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;

public class ShowUserMessageActivity extends BaseActivity implements View.OnClickListener{

    TextView friendemail,usermessage,requestLocationView;
    NetworkImageView imagemessage;
    ImageLoader _imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user_message);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        friendemail=(TextView)findViewById(R.id.friend_email);
        friendemail.setText(Commons.messageEntity.get_useremail());
        usermessage=(TextView)findViewById(R.id.user_message);
        usermessage.setText(Commons.messageEntity.get_usermessage());

        imagemessage=(NetworkImageView) findViewById(R.id.messageImage);
        imagemessage.setOnClickListener(this);
        if(Commons.messageEntity.get_imageUrl().length()>0){
            imagemessage.setImageUrl(Commons.messageEntity.get_imageUrl(),_imageLoader);
        }
        else imagemessage.setImageBitmap(Commons.messageEntity.get_bitmap());

        Log.d("LatLng===>",Commons.messageEntity.get_requestLatLng().toString());

        requestLocationView=(TextView)findViewById(R.id.requestLocationViewButton);
        if(friendemail.getText().toString().equals("service@vacay.com"))
            requestLocationView.setVisibility(View.GONE);
        requestLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Commons.messageEntity.get_requestLatLng().equals(new LatLng(0.0,0.0))) {
                    Intent intent = new Intent(getApplicationContext(), RequestLocationViewActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else showToast("No request location.");
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.messageImage:
                Intent intent=new Intent(this,ImageViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
        }
    }
}
