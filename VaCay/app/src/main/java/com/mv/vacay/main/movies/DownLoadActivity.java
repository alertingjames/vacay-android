package com.mv.vacay.main.movies;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;
import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownLoadActivity extends AppCompatActivity {

    private static final String YOUTUBE_ID = Commons.gameEntityUpload.getVideoId();
    TextView resultURL;

    private final YouTubeExtractor mExtractor = YouTubeExtractor.create();


    private Callback<YouTubeExtractionResult> mExtractionCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            bindVideoResult(response.body());
        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            onError(t);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);

//        For android youtube extractor library  com.github.Commit451.YouTubeExtractor:youtubeextractor:2.1.0'

        mExtractor.extract(YOUTUBE_ID).enqueue(mExtractionCallback);


    }


    private void onError(Throwable t) {
        t.printStackTrace();
        Toast.makeText(DownLoadActivity.this, "It failed to extract. So sad", Toast.LENGTH_SHORT).show();
    }


    private void bindVideoResult(YouTubeExtractionResult result) {

//        Here you can get download url link

        Log.d("OnSuccess", "Got a result with the best url: " + result.getBestAvailableQualityVideoUri());

        Toast.makeText(this, "result : " + result.getSd360VideoUri(), Toast.LENGTH_SHORT).show();
        Uri uriUrl = Uri.parse(result.getBestAvailableQualityVideoUri().toString());
//        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
//        startActivity(launchBrowser);
        try {
            //Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            //Intent intent = new Intent(Intent.ACTION_WEB_SEARCH, Uri.parse("http://www.gsalafi.com"));

            //Intent intent = new Intent(Intent.ACTION_WEB_SEARCH).setData(uriUrl);

            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);

            intent.putExtra(SearchManager.QUERY, result.getBestAvailableQualityVideoUri().toString());
            startActivity(intent);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}

