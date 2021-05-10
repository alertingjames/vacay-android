package com.mv.vacay.main.provider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.JobMediaActivity;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.JobsEntity;
import com.mv.vacay.models.MediaEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobDetailActivity extends AppCompatActivity {

    NetworkImageView logo;
    ImageView back, facebookButton, linkedinButton, twitterButton, instagramButton;
    TextView jobName, jobName1, company, department,
            reqId, location, postingDate, description, empty,shareButton, down;
    ScrollView scroll;
    ImageLoader _imageLoader;
    String content="",title="", logoUrl="", desc="";
    File file;
    String adminEmail="";
    TextView surveyButton;
    TextView surveyEmail;
    LinearLayout surveyportion;
    private ProgressDialog _progressDlg;

    LinearLayout screenshot;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private static final Bitmap.CompressFormat COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int COMPRESSION_QUALITY = 50; // from 0 to 100 works only for JPG

    private static final String TAG = "JobDetailActivity";

    int buttonNum=0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        // Initialize facebook SDK.
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Create a callbackManager to handle the login responses.
        callbackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);

        // This part is optional
        shareDialog.registerCallback(callbackManager, callback);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        scroll=(ScrollView)findViewById(R.id.scroll);
        title="Job title: "+Commons.job.getJobName();
        logoUrl=Commons.job.getLogoUrl();
        content="Department: "+Commons.job.getDepartment()+"\n"+
                "Location: "+Commons.job.getLocation()+"\n"+
                "Req ID: "+Commons.job.getJobReqId()+"\n"+
                "Posting Date: "+Commons.job.getPostingDate()+"\n"+
                "Extra: "+Commons.job.getEmptyField();
        desc="Description: "+Commons.job.getDescription();

        logo=(NetworkImageView)findViewById(R.id.logo);
        back=(ImageView)findViewById(R.id.back);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        back.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        back.setBackgroundColor(Color.BLACK);
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        back.getBackground().clearColorFilter();
                        back.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        facebookButton=(ImageView)findViewById(R.id.facebooksharebutton);
        facebookButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        facebookButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        facebookButton.setBackgroundResource(R.drawable.facebookicon);

                        showAlertDialogPost(0);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        facebookButton.getBackground().clearColorFilter();
                        facebookButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        linkedinButton=(ImageView)findViewById(R.id.linkedinsharebutton);
        linkedinButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        linkedinButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        linkedinButton.setBackgroundResource(R.drawable.linkedinicon);

//                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                        shareIntent.setType("text/*");
//                        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+desc);
//                        startActivity(shareIntent);

                        //===========================================================

                        showAlertDialogPost(1);


                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        linkedinButton.getBackground().clearColorFilter();
                        linkedinButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        twitterButton=(ImageView)findViewById(R.id.twittersharebutton);
        twitterButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        twitterButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        twitterButton.setBackgroundResource(R.drawable.twittericon);

                        showAlertDialogPost(2);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        twitterButton.getBackground().clearColorFilter();
                        twitterButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        instagramButton=(ImageView)findViewById(R.id.instagrambutton);
        instagramButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        instagramButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        instagramButton.setBackgroundResource(R.drawable.instagramicon);

                        Commons._is_instagram=true;
                        Intent i=new Intent(getApplicationContext(),ScreenshotActivity.class);
                        startActivity(i);


                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        instagramButton.getBackground().clearColorFilter();
                        instagramButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        TextView title2=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title2.setTypeface(font);


        jobName=(TextView)findViewById(R.id.name);
        jobName1=(TextView)findViewById(R.id.name1);
        company=(TextView)findViewById(R.id.company);
        department=(TextView)findViewById(R.id.department);
        reqId=(TextView)findViewById(R.id.reqId);
        location=(TextView)findViewById(R.id.location);
        postingDate=(TextView)findViewById(R.id.postDate);
        description=(TextView)findViewById(R.id.description);
        empty=(TextView)findViewById(R.id.empty);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        jobName.setTypeface(font);
        jobName1.setTypeface(font);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        description.setTypeface(font);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/futura-md-bt-bold-58e2b41ab199c.ttf");
        company.setTypeface(font);

        down=(TextView)findViewById(R.id.down);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scroll.post(new Runnable() {
                    @Override
                    public void run() {

                        scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        surveyEmail=(TextView)findViewById(R.id.adminemail);
        surveyportion=(LinearLayout)findViewById(R.id.surveyportion);
        surveyportion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        surveyEmail.setTextColor(Color.RED);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        surveyEmail.setTextColor(Color.BLACK);

                        if(Commons.job.getSurvey().startsWith("http") && Commons.job.getSurvey().contains("?usp=sf_link")) {
                            Intent intent = new Intent(getApplicationContext(), GoogleSurveyActivity.class);
                            intent.putExtra("adminEmail", adminEmail);
                            intent.putExtra("surveyLink", Commons.job.getSurvey());
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"No survey provided!",Toast.LENGTH_SHORT).show();
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        surveyportion.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        shareButton=(TextView)findViewById(R.id.shareButton);
        shareButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        shareButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        shareButton.setBackground(getDrawable(R.drawable.black_fill_rect));

                        showChoiceDialogSelectPostingWBSite();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        shareButton.getBackground().clearColorFilter();
                        shareButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        final LinearLayout mediaButton=(LinearLayout)findViewById(R.id.mediaButton);

        mediaButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mediaButton.setBackgroundResource(R.drawable.green_fillround);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        mediaButton.setBackgroundResource(R.drawable.login_roundrect);

                        enterMedia(Commons.job);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        mediaButton.getBackground().clearColorFilter();
                        mediaButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });


        if(Commons.job.getLogoUrl().length()>0)
            logo.setImageUrl(Commons.job.getLogoUrl(),_imageLoader);

        jobName.setText(Commons.job.getJobName());
        jobName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogJobName(jobName.getText().toString());
            }
        });
        jobName1.setText(Commons.job.getJobName());

        company.setText(Commons.job.getCompany());
        reqId.setText(Commons.job.getJobReqId());
        department.setText(Commons.job.getDepartment());
        description.setText(Commons.job.getDescription());
        location.setText(Commons.job.getLocation());
        postingDate.setText(Commons.job.getPostingDate());
        empty.setText(Commons.job.getEmptyField());

        getAdminEmail(String.valueOf(Commons.thisEntity.get_adminId()));
    }

    public void getAdminEmail(String idx) {

        Log.d("AdminID===>",String.valueOf(idx));
        String url = ReqConst.SERVER_URL + "getAdminData";

        String params = String.format("/%s", idx);
        url += params; Log.d("URL===>",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
                Log.d("AdminGetJson===>",json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("AdminResponse===>",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray userInfo = response.getJSONArray("adminData");

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);
                    String name=jsonUser.getString("adminName");
                    String email=jsonUser.getString("adminEmail");
                    String image=jsonUser.getString("adminImageUrl");

                    Log.d("AdminInfo===>",name+"/"+email+"/"+image);
                    adminEmail=email;
                    surveyEmail.setText(adminEmail);
                    if(Commons.job.getSurvey().startsWith("http") && Commons.job.getSurvey().contains("?usp=sf_link")) {
                        surveyportion.setVisibility(View.VISIBLE);
                    }else
                        surveyportion.setVisibility(View.GONE);
                }

            }else if(result_code==113){
            }
            else {
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void showAlertDialogPost(final int buttonNum) {

        final String[] items = {"I want to send job info as screenshot", "I want to send job info as text"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("How do you want to post?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent i=new Intent(getApplicationContext(),ScreenshotActivity.class);
                    startActivity(i);
                }
                else if(item==1){

                    if(buttonNum==0){

                        Uri uri= Uri.parse(Commons.job.getLogoUrl());
 //                       Uri uri= Uri.parse("http://35.162.12.207/uploadfiles/restaurant/2017/05/102_food14959508616.jpg");
                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                                .setContentTitle(title)
                                .setContentDescription(content).setQuote(desc).setRef(content)
                                .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"))
                                .setImageUrl(uri)
                                .build();
                        ShareDialog.show(JobDetailActivity.this, shareLinkContent);

                    }else if(buttonNum==1){
                        if(desc.length()>1100)desc=desc.substring(0,1100);

                        Intent linkedinIntent = new Intent(Intent.ACTION_SEND);
                        linkedinIntent.setType("text/plain");
                        linkedinIntent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+desc);

                        boolean linkedinAppFound = false;
                        List<ResolveInfo> matches2 = getPackageManager()
                                .queryIntentActivities(linkedinIntent, 0);

                        for (ResolveInfo info : matches2) {
                            if (info.activityInfo.packageName.toLowerCase().startsWith(
                                    "com.linkedin")) {
                                linkedinIntent.setPackage(info.activityInfo.packageName);
                                linkedinAppFound = true;
                                break;
                            }
                        }

                        if (linkedinAppFound) {
                            startActivity(linkedinIntent);
                        }
                        else
                        {
                            Toast.makeText(JobDetailActivity.this,"LinkedIn app not Installed in your mobile", Toast.LENGTH_SHORT).show();
                        }

                    }else if(buttonNum==2){

                        String text=title+"\n"+content;
                        if(text.length()>140){
                            text=title+"\n"+"https://www.vacaycarpediem.com/posts";
                        }

                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/*");
                        sharingIntent.setPackage("com.twitter.android");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, text);

                        boolean twitterAppFound = false;
                        List<ResolveInfo> matches2 = getPackageManager()
                                .queryIntentActivities(sharingIntent, 0);

                        for (ResolveInfo info : matches2) {
                            if (info.activityInfo.packageName.toLowerCase().startsWith(
                                    "com.twitter")) {
                                sharingIntent.setPackage(info.activityInfo.packageName);
                                twitterAppFound = true;
                                break;
                            }
                        }

                        if (twitterAppFound) {
                            startActivity(sharingIntent);
                        }
                        else
                        {
                            Toast.makeText(JobDetailActivity.this,"Twitter app not Installed in your mobile", Toast.LENGTH_SHORT).show();
                        }

    //                    startActivity(sharingIntent);
                    }

                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void showAlertDialogSelectShareWebsite() {

        final String[] items = {"Facebook", "Linkedin",
                        "Twitter","Instagram","Google","Email/Gmail"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Where do you want to post?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {

                    showAlertDialogPost(0);

                }
                else if(item==1){

                    showAlertDialogPost(1);

                }
                else if(item==2){

                    showAlertDialogPost(2);

                }
                else if(item==3){

                    Intent i=new Intent(getApplicationContext(),ScreenshotActivity.class);
                    i.putExtra("select","instagram");
                    startActivity(i);

                }
                else if(item==4){

                    Intent shareIntent = new PlusShare.Builder(getApplicationContext())
                            .setType("text/plain")
                            .setText(title+"\n\n"+content+"\n\n"+desc)
                            .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"))
                            .getIntent();

                    startActivityForResult(shareIntent, 0);

                }
                else if(item==5){

                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            // Permission already granted
                            try {
                                createPdf();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (DocumentException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            //Request Location Permission
                            checkWritePermission();
                        }
                    }
                    else {
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void showAlertDialogPostToTW() {

        final String[] items = {"I want to send job info screenshot", "I want to send job info as text"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("How do you want to post?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent i=new Intent(getApplicationContext(),ScreenshotActivity.class);
                    startActivity(i);
                }
                else if(item==1){



                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    public void showAlertDialogJobName(String name) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Job Title");
        alertDialog.setMessage(name);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void onShareClick() {
        Resources resources = getResources();

        Uri uri = Uri.fromFile(file);
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+"Please read this pdf.");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.setType("message/rfc822");
//        emailIntent.setType("application/pdf");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
//        emailIntent.setType("application/pdf");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("text/plain");


        Intent openInChooser = Intent.createChooser(emailIntent, "Share As...");

        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if(packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
                emailIntent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+"Please read this pdf.");
                emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            } else if(
    //                packageName.contains("twitter") || packageName.contains("facebook") ||
                            packageName.contains("mms") ||
                            packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
//                if(packageName.contains("twitter")) {
//                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content);
//                    emailIntent.setType("application/pdf");
//                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                } else if(packageName.contains("facebook")) {
//                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
//                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
//                    // will show the <meta content ="..."> text from that page with our link in Facebook.
//
//                    emailIntent.setType("application/pdf");
//                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content);
//                }
//                else if(packageName.contains("linkedin")) {
//                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
//                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
//                    // will show the <meta content ="..."> text from that page with our link in Facebook.
//                    intent.putExtra(Intent.EXTRA_TEXT, "Linkedin Text");
//                }
//                else
                if(packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n"+content+"\n"+desc);
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                } else if(packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_TEXT, title+"\n\n"+content+"\n\n"+"Please read this pdf.");
                    intent.putExtra(Intent.EXTRA_SUBJECT, title);
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                }

                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }

        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray( new LabeledIntent[ intentList.size() ]);

        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        startActivity(openInChooser);
    }

    private void createPdfFile(){
        new Thread() {
            public void run() {
                // Get the directory for the app's private pictures directory.
                final File file = new File(Environment.getExternalStorageDirectory(), "PdfTest.pdf");

                if (file.exists ()) {
                    file.delete ();
                }

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);

                    PdfDocument document = new PdfDocument();
                    Point windowSize = new Point();
                    getWindowManager().getDefaultDisplay().getSize(windowSize);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(windowSize.x, windowSize.y, 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    View content = getWindow().getDecorView();
                    content.draw(page.getCanvas());
                    document.finishPage(page);
                    document.writeTo(out);
                    document.close();
                    out.flush();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(JobDetailActivity.this, "File created: "+file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.d("TAG_PDF", "File was not created: "+e.getMessage());
                } finally {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public static final int MY_PERMISSIONS_REQUEST_WRITE = 99;
    private void checkWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Write Storage Permission Needed")
                        .setMessage("This app needs the Write Storage permission, please accept to use to write functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(JobDetailActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_WRITE );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        try {
                            createPdf();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void sharePdfFile(){
        String emailAddress[] = {"alertingjames@gmail.com"}; // email: test@gmail.com

        Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "PdfTest.pdf"));
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Pdf");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi, Please get pdf");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.share_pdf:
//                sharePdfFile1();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private void createPdf() throws IOException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStorageDirectory().getPath()+"/documents");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
            Log.i("LOG_TAG", "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder, "pdfdemo" + timeStamp + ".pdf");

        file=myFile;

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content

        Bitmap image = drawableToBitmap(LoadImageFromWebOperations(Commons.job.getLogoUrl()));
//        if(image.getWidth()>800 && image.getHeight()>800)
//            image=getResizedBitmap(image,800);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50 , stream);
        Image myImg = Image.getInstance(stream.toByteArray());
        myImg.setAlignment(Image.TOP);
        document.add(myImg);

        document.add(new Paragraph(title));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(content));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(desc));

        //Step 5: Close the document
        document.close();

    //    Toast.makeText(JobDetailActivity.this, "File created: "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        onShareClick();

    //    sharePdfFile1();

    }

    private void sharePdfFile1(){
        String emailAddress[] = {"alertingjames@gmail.com"}; // email: test@gmail.com

        String emailAddress1[] = {"alertingjames@gmail.com"};

        Uri uri = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emailAddress1);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        emailIntent.putExtra(Intent.EXTRA_TEXT, title+"\n"+ content+"\n"+"\n"+"Please read this pdf");
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(emailIntent, "Send email using:"));
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/PICTURES/Screenshots/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public File makeScreenShot(View root) {
        //i have different code here in my project
        //it is just a test case

        String path="";  Log.d("Width/Height==>",String.valueOf(root.getWidth())+"/"+String.valueOf(root.getHeight()));
        Bitmap screenshot = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(screenshot);
        root.draw(canvas);
        File file = null;

        OutputStream fout = null;
        try {

    //        File imageFile = ScreenshotContentProvider.getFile(this);
            File imageFile = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "test.jpg");

            file=imageFile;
            Log.e("MA.makeScreenShot", "Saving File to: " + imageFile.getAbsolutePath());

            path=imageFile.getPath();
            fout = new FileOutputStream(imageFile);
            screenshot.compress(COMPRESS_FORMAT, COMPRESSION_QUALITY, fout);

            fout.flush();
            fout.close();

        } catch (Exception e) {
            Log.e("JobDetailActivity", "Exception in makeScreenShot");
            e.printStackTrace();
        }

        return file;

    }

    public void takeScreenshot(View view){

        Bitmap bitmap;

        View v1=view.getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "test.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlertDialogPost(final File bitmap) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle(Commons.job.getJobName());
        alertDialog.setMessage("Do you want to post?");

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

//                        Uri uri1= Uri.parse(Commons.job.getLogoUrl());
//
//    //                    Uri uri1= Uri.fromFile(bitmap);
//                        ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
////                                .setContentTitle(title)
////                                .setContentDescription(content).setQuote(desc).setRef(content)
////                                .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/helpful-tips"))
//                                .setImageUrl(uri1)
//                                .build();
//
//                        ShareDialog.show(JobDetailActivity.this, shareLinkContent);

                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(bitmap));
                        shareIntent.setType("image/jpeg");
                        startActivity(shareIntent);

//                        SharePhoto photo = new SharePhoto.Builder()
//                                .setBitmap(bitmap)
//                                .build();
//                        ShareMediaContent content = new ShareMediaContent.Builder()
//                                .addMedium(photo)
////				.addMedium(shareVideo)
//                                .build();
//
//
//                        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Call callbackManager.onActivityResult to pass login result to the LoginManager via callbackManager.
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.e(TAG, "Successfully posted");
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            Log.e(TAG, "Cancel occurred");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Log.e(TAG, error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };

    public void showChoiceDialogSelectPostingWBSite(){

        final Item_Media[] items = {
                new Item_Media("      Facebook", R.drawable.facebookicon),
                new Item_Media("      Linkedin",R.drawable.linkedinicon),
                new Item_Media("      Twitter", R.drawable.twittericon),
                new Item_Media("      Instagram", R.drawable.instagramicon),
                new Item_Media("      Google +", R.drawable.googleplusicon),
                new Item_Media("      Email/Gmail", R.drawable.emailicon),
        };

        ListAdapter adapter = new ArrayAdapter<Item_Media>(
                this,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (10 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);
                tv.setTextSize(17);
                return v;
            }
        };


        new android.app.AlertDialog.Builder(this)
                .setTitle("Where do you want to post?")

                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            showAlertDialogPost(0);

                        } else if (item == 1) {

                            showAlertDialogPost(1);

                        } else if (item == 2) {

                            showAlertDialogPost(2);

                        } else if (item == 3) {

                            Intent i = new Intent(getApplicationContext(), ScreenshotActivity.class);
                            i.putExtra("select", "instagram");
                            startActivity(i);

                        } else if (item == 4) {

                            Intent shareIntent = new PlusShare.Builder(getApplicationContext())
                                    .setType("text/plain")
                                    .setText(title + "\n\n" + content + "\n\n" + desc)
                                    .setContentUrl(Uri.parse("https://www.vacaycarpediem.com/posts"))
                                    .getIntent();

                            startActivityForResult(shareIntent, 0);

                        } else if (item == 5) {

                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    // Permission already granted
                                    try {
                                        createPdf();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (DocumentException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    //Request Location Permission
                                    checkWritePermission();
                                }
                            } else {
                                try {
                                    createPdf();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (DocumentException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }

                }).show();

    }

    public void enterMedia(JobsEntity entity){
        getMedia(String.valueOf(entity.getIdx()),"job");
    }

    public void getMedia(final String itemID, final String item) {

        String url = ReqConst.SERVER_URL + "get_job_media";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetMessagesResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("item_id", itemID);
                params.put("item", item);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", this.getString(R.string.loading),true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            _progressDlg.dismiss();
            _progressDlg = null;
        }
    }

    public void parseGetMessagesResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("MediaResponse===>",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);

            if (result_code.equals("0")) {

                JSONObject medias = response.getJSONObject("media");
                Log.d("Medias===>", medias.toString());

                MediaEntity mediaEntity=new MediaEntity();
                mediaEntity.setVideo(medias.getString("video_url"));
                mediaEntity.setYoutube(medias.getString("youtube_url"));

                if(medias.length()>0){

                    mediaEntity.setObjimage(Commons.job.getLogoUrl());
                    mediaEntity.setObjtitle(Commons.job.getJobName());

                    Intent intent=new Intent(getApplicationContext(),JobMediaActivity.class);
                    Commons.mediaEntity=mediaEntity;      Log.d("YouTube Url===>",Commons.mediaEntity.getYoutube());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else
                    Toast.makeText(getApplicationContext(),"No media!",Toast.LENGTH_SHORT).show();

            }else if(result_code.equals("1")){
                Toast.makeText(getApplicationContext(),"No place where medias are!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
        }
    }


}

class Item_Media{
    public final String text;
    public final int icon;
    public Item_Media(String text, Integer icon) {
        this.text = text;
        this.icon = icon;
    }
    @Override
    public String toString() {
        return text;
    }
}
