package com.mv.vacay.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

public class VideoMediaPlayActivity extends AppCompatActivity{
    private WebView webview;
    String VIDEO_PATH="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play2);

        TextView note=(TextView)findViewById(R.id.note) ;

        String url=getIntent().getStringExtra("url");
        if(url.startsWith("http"))
            VIDEO_PATH = url;
        else VIDEO_PATH = "https://www.youtube.com/embed/"+ url +"?autoplay=1";

        Log.d("VIDEOPath===>",VIDEO_PATH);

        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl(VIDEO_PATH);
    }
}






































