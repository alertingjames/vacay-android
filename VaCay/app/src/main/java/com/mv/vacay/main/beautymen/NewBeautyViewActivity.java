package com.mv.vacay.main.beautymen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;

public class NewBeautyViewActivity extends AppCompatActivity {

    ImageView back;
    NetworkImageView imageView;
    ImageLoader _imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beauty_view);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
        imageView=(NetworkImageView) findViewById(R.id.image);
        if (Commons.photoUrl.length() > 0) {
            imageView.setImageUrl(Commons.photoUrl,_imageLoader);
        }
        else
        if(Commons.resId!=0){
            imageView.setDefaultImageResId(Commons.resId);
            Commons.resId=0;
        } else imageView.setImageBitmap(Commons.bitmap);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
    }
}
