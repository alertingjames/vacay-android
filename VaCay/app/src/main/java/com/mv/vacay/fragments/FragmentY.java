package com.mv.vacay.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.mv.vacay.R;
import com.mv.vacay.adapter.InboxMessageAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.VideoMediaPlayActivity;
import com.mv.vacay.main.carpediem.VideoDisplayActivity;

/**
 * Created by sonback123456 on 11/26/2017.
 */

public class FragmentY extends Fragment{
    WebView video, youtube;
    FrameLayout videoFrame, youtubeFrame;
    TextView videoFull, youtubeFull, note;

    String YOUTUBE_PATH="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_y, container, false);

        video = (WebView) v.findViewById(R.id.video);
        youtube = (WebView) v.findViewById(R.id.youtube);

        videoFrame = (FrameLayout) v.findViewById(R.id.videoFrame);
        youtubeFrame = (FrameLayout) v.findViewById(R.id.youtubeFrame);

        videoFull = (TextView) v.findViewById(R.id.videoFull);
        youtubeFull = (TextView) v.findViewById(R.id.youtubeFull);

        note=(TextView)v.findViewById(R.id.note) ;

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

        return v;
    }

    public static FragmentY newInstance(String text) {

        FragmentY f = new FragmentY();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        videoFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), VideoMediaPlayActivity.class);
                intent.putExtra("url",Commons.mediaEntity.getVideo());
                startActivity(intent);
            }
        });

        youtubeFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), VideoMediaPlayActivity.class);
                intent.putExtra("url",Commons.mediaEntity.getYoutube());
                startActivity(intent);
            }
        });
    }

}
































