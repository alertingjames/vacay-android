package com.mv.vacay.main.movies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.api.CustomVolleyRequest;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.config.EndPoints;
import com.mv.vacay.main.manager.VendorUploadActivity;
import com.mv.vacay.models.VideoData;

public class VideoDetail extends AppCompatActivity {
    private VideoData videoDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        try {
            videoDetail = (VideoData)getIntent().getSerializableExtra("ob");
            Log.d("VideoId===",videoDetail.getVideoId());
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 10/3/16 Handle Error
            Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
        }

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setTitle(videoDetail.getVideoTitle());
        generateView(getApplicationContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void generateView(Context context) {

        NetworkImageView imageView = (NetworkImageView)findViewById(R.id.videoDetailThumbnail);
        ImageLoader imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(videoDetail.getLargeThumbnail(), ImageLoader.getImageListener(imageView, R.drawable.ic_action_android, R.drawable.ic_action_android));
        imageView.setImageUrl(videoDetail.getLargeThumbnail(), imageLoader); Log.d("VideoThumbnail==>",videoDetail.getMediumThumbnail());
        ((TextView)findViewById(R.id.detailLikes)).setText(videoDetail.getLikeCount());
        ((TextView)findViewById(R.id.detailDislike)).setText(videoDetail.getDislikeCount());
        ((TextView)findViewById(R.id.detailDuration)).setText(videoDetail.getDuration());
        ((TextView)findViewById(R.id.detailViews)).setText(videoDetail.getViewCount() + " views");
        ((TextView)findViewById(R.id.detailTitle)).setText(videoDetail.getVideoTitle());  Log.d("Title===>",videoDetail.getVideoTitle());
        ((TextView)findViewById(R.id.detailUploaderName)).setText(videoDetail.getChannelTitle());  Log.d("Channel===>",videoDetail.getChannelTitle());
        ((TextView)findViewById(R.id.detailFavorite)).setText(videoDetail.getFavouriteCount());
        ((TextView)findViewById(R.id.videoid)).setText(videoDetail.getVideoId());

        Commons.gameEntityUpload.setGameThumbnailUrl(videoDetail.getMediumThumbnail());
        Commons.gameEntityUpload.setGameName(videoDetail.getVideoTitle());
        Commons.gameEntityUpload.setChannel(videoDetail.getChannelTitle());
        Commons.gameEntityUpload.setVideoId(videoDetail.getVideoId());
        Commons.gameEntityUpload.setDuaration(videoDetail.getDuration());

        FloatingActionButton playButton =   (FloatingActionButton)findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(EndPoints.YOUTUBE_URL_VIDEO + videoDetail.getVideoId())));
            }
        });
        TextView selectButton =   (TextView) findViewById(R.id.selectbutton);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), VendorUploadActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        TextView downloadButton =   (TextView) findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), DownLoadActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }
}
