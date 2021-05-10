package com.mv.vacay.main.provider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.RegisterActivity;

public class WebPageActivity extends AppCompatActivity {

    private WebView myPage;
    boolean _is_show=false;
    NetworkImageView logo;
    TextView jobName1, company, department,
            reqId, location, postingDate, description, empty;
    ScrollView scroll;
    ImageLoader _imageLoader;
    String content="",title="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);

        TextView title2=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title2.setTypeface(font);

        String loginUrl=getIntent().getStringExtra("login");

        myPage=(WebView)findViewById(R.id.myPage);
        myPage.getSettings().setJavaScriptEnabled(true);

        //loads the WebView completely zoomed out
        myPage.getSettings().setLoadWithOverviewMode(true);

        //true makes the Webview have a normal viewport such as a normal desktop browser
        //when false the webview will have a viewport constrained to it's own dimensions
        myPage.getSettings().setUseWideViewPort(true);

        //override the web client to open all links in the same webview
        myPage.setWebViewClient(new MyWebViewClient());
        myPage.setWebChromeClient(new MyWebChromeClient());

        myPage.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        myPage.setScrollbarFadingEnabled(false);
        myPage.getSettings().setBuiltInZoomControls(true);
        myPage.getSettings().setAllowFileAccess(true);

        //Injects the supplied Java object into this WebView. The object is injected into the
        //JavaScript context of the main frame, using the supplied name. This allows the
        //Java object's public methods to be accessed from JavaScript.
        myPage.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        //load the home page URL
        myPage.loadUrl(loginUrl);   //    http://35.162.12.207
        // linkedin  https://www.linkedin.com/uas/login    twitter  https://twitter.com/login/

        final LinearLayout editPage=(LinearLayout)findViewById(R.id.editPage);
        final ImageView show=(ImageView)findViewById(R.id.show);
        show.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        show.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        if(!_is_show) {
                            _is_show=true;
                            myPage.setVisibility(View.GONE);
                            show.setBackgroundResource(R.drawable.light_blue_fill_round);
                            show.setImageResource(R.drawable.cancel8);
                        }else {
                            _is_show=false;
                            show.setBackgroundResource(R.drawable.light_blue_fill_round);
                            show.setImageResource(R.drawable.show2);
                            myPage.setVisibility(View.VISIBLE);
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        show.getBackground().clearColorFilter();
                        show.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        scroll=(ScrollView)findViewById(R.id.scroll);
        title=Commons.job.getJobName();

        logo=(NetworkImageView)findViewById(R.id.logo);

        jobName1=(TextView)findViewById(R.id.name1);
        company=(TextView)findViewById(R.id.company);
        department=(TextView)findViewById(R.id.department);
        reqId=(TextView)findViewById(R.id.reqId);
        location=(TextView)findViewById(R.id.location);
        postingDate=(TextView)findViewById(R.id.postDate);
        description=(TextView)findViewById(R.id.description);
        empty=(TextView)findViewById(R.id.empty);

        if(Commons.job.getLogoUrl().length()>0)
            logo.setImageUrl(Commons.job.getLogoUrl(),_imageLoader);

        jobName1.setText(Commons.job.getJobName());

        company.setText(Commons.job.getCompany());
        reqId.setText(Commons.job.getJobReqId());
        department.setText(Commons.job.getDepartment());
        description.setText(Commons.job.getDescription());
        location.setText(Commons.job.getLocation());
        postingDate.setText(Commons.job.getPostingDate());
        empty.setText(Commons.job.getEmptyField());

        myPage.setVisibility(View.GONE);
        show.setVisibility(View.VISIBLE);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("demo.mysamplecode.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        //display alert message in Web View
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("ProviderHomePage", message);
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message).setCancelable(true).show();
            result.confirm();
            return true;
        }

    }

    public class JavaScriptInterface {
        Context mContext;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        //using Javascript to call the finish activity
        public void closeMyActivity() {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,JobDetailActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

}
