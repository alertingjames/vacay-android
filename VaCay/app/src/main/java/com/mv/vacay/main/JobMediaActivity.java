package com.mv.vacay.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

public class JobMediaActivity extends AppCompatActivity {

    CircularImageView providerImage;
    CircularNetworkImageView providerImageNet;
    TextView title, subtitle;

    private AdView mAdView;
    WebView video, youtube;
    FrameLayout videoFrame, youtubeFrame;
    TextView videoFull, youtubeFull, note;

    String YOUTUBE_PATH="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_media);

        title=(TextView)findViewById(R.id.title);
        subtitle=(TextView)findViewById(R.id.subtitle);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        subtitle.setTypeface(font);
        title.setTypeface(font);

        providerImage=(CircularImageView) findViewById(R.id.imv_photo);
        providerImageNet=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);

        if(Commons.mediaEntity.getObjimage().length()>1000) {
            providerImageNet.setVisibility(View.GONE);
            providerImage.setImageBitmap(base64ToBitmap(Commons.mediaEntity.getObjimage()));
        }else {
            providerImageNet.setVisibility(View.VISIBLE);
            providerImageNet.setImageUrl(Commons.mediaEntity.getObjimage(), VaCayApplication.getInstance()._imageLoader);
        }

        title.setText(Commons.mediaEntity.getObjtitle());
        subtitle.setText(Commons.mediaEntity.getObjsubtitle());

        video = (WebView) findViewById(R.id.video);
        youtube = (WebView) findViewById(R.id.youtube);

        videoFrame = (FrameLayout) findViewById(R.id.videoFrame);
        youtubeFrame = (FrameLayout) findViewById(R.id.youtubeFrame);

        videoFull = (TextView) findViewById(R.id.videoFull);
        youtubeFull = (TextView) findViewById(R.id.youtubeFull);

        note=(TextView)findViewById(R.id.note) ;

        if((Commons.mediaEntity.getVideo().equals("None") || Commons.mediaEntity.getVideo().length()==0) &&
                (Commons.mediaEntity.getYoutube().equals("None") || Commons.mediaEntity.getYoutube().length()==0)) {
            note.setVisibility(View.VISIBLE);
            videoFrame.setVisibility(View.GONE);
            youtubeFrame.setVisibility(View.GONE);
        }
        else if(Commons.mediaEntity.getVideo().equals("None") || Commons.mediaEntity.getVideo().length()==0)
            videoFrame.setVisibility(View.GONE);
        else if(Commons.mediaEntity.getYoutube().equals("None") || Commons.mediaEntity.getYoutube().length()==0)
            youtubeFrame.setVisibility(View.GONE);
        else note.setVisibility(View.GONE);

        video.setWebViewClient(new WebViewClient());
        video.getSettings().setJavaScriptEnabled(true);
        video.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        video.getSettings().setPluginState(WebSettings.PluginState.ON);
        video.getSettings().setMediaPlaybackRequiresUserGesture(false);
        video.setWebChromeClient(new WebChromeClient());
        video.loadUrl(Commons.mediaEntity.getVideo());

        YOUTUBE_PATH = "https://www.youtube.com/embed/"+ Commons.mediaEntity.getYoutube()+"?autoplay=1";

        youtube.setWebViewClient(new WebViewClient());
        youtube.getSettings().setJavaScriptEnabled(true);
        youtube.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        youtube.getSettings().setPluginState(WebSettings.PluginState.ON);
        youtube.getSettings().setMediaPlaybackRequiresUserGesture(false);
        youtube.setWebChromeClient(new WebChromeClient());
        youtube.loadUrl(YOUTUBE_PATH);

        videoFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), VideoMediaPlayActivity.class);
                intent.putExtra("url",Commons.mediaEntity.getVideo());
                startActivity(intent);
            }
        });

        youtubeFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), VideoMediaPlayActivity.class);
                intent.putExtra("url",Commons.mediaEntity.getYoutube());
                startActivity(intent);
            }
        });
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }
}
