package com.mv.vacay.main.meetfriends;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;

public class ImageViewActivity extends AppCompatActivity {

    ImageView back;
    NetworkImageView imageView;
    ImageLoader _imageLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        imageView=(NetworkImageView) findViewById(R.id.image);
        if(Commons.messageEntity.get_imageUrl().length()>0){
            imageView.setImageUrl(Commons.messageEntity.get_imageUrl(),_imageLoader);
        }
        else imageView.setImageBitmap(Commons.messageEntity.get_bitmap());
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
