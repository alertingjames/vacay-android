package com.mv.vacay.main.manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.classes.AndroidMultiPartEntity;
import com.mv.vacay.classes.AndroidMultiPartEntity.ProgressListener;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.video.VideoCompressActivity;
import com.mv.vacay.models.GameEntity;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.utils.MultiPartRequest;
//import com.sendbird.android.OpenChannel;
//import com.sendbird.android.OpenChannelListQuery;
//import com.sendbird.android.SendBird;
//import com.sendbird.android.SendBirdException;
//import com.sendbird.android.User;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VendorUploadActivity extends AppCompatActivity implements View.OnClickListener {

    public static String sUserId;
    private String mNickname;

//    private OpenChannelListQuery channelListQuery;

    ImageView back,dropImage;
    NetworkImageView videoImage;
    CircularNetworkImageView vendorPhoto;
    TextView fromList,fromGoogle,fromCamera,barName,barType,barPlace,checkIn,videoName,videoId,videoCategory,videoduration,
            videoType,teamName,channelTitle,salePrice,rentPrice,watchWithPrice,specials,uploadButton,menuA,menuB,menuC,menuD,
            menuE,menuF,menuG,menuH,menuI,menuJ,selectTitle;
    LinearLayout lytBarType,lytVideoCategory,lytVideoType,lytMenu,lytMenuList,lytProgress;
    LinearLayout linearLayout,linearLayout1;
    String bar_Type="",video_Category="",video_Type="";
    int bar_Type_id=0,video_Category_id=0;
    boolean _openMenus=false;
    boolean _openOptions=false;
    private ProgressDialog _progressDlg;
    private int _idx = 0;
    ImageLoader _imageloader;
    private ProgressBar progressBar;
    private String filePath = null;
    private TextView txtPercentage;
    private static final String TAG = VendorUploadActivity.class.getSimpleName();
    long totalSize = 0;
    Animation animation1,animation2;

    private File selectedFile;
    String inputPath,outPath;

    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_upload);

        mNickname=Commons.thisEntity.get_fullName();
        sUserId=Commons.thisEntity.get_email();

        uploadUI();
        Commons.gameEntityUpload=new GameEntity();
    }

    private void uploadUI() {

        _imageloader = VaCayApplication.getInstance().getImageLoader();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        videoImage=(NetworkImageView) findViewById(R.id.video_image);
        videoImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Commons.videoUrl, MediaStore.Images.Thumbnails.MINI_KIND);
        if(Commons.gameEntityUpload.getGameThumbnailUrl().length()>0) {
            videoImage.setImageUrl(Commons.gameEntityUpload.getGameThumbnailUrl(),_imageloader);
            Commons.videoUrl=Commons.gameEntityUpload.getGameThumbnailUrl();
            Log.d("VIDEO===>",Commons.videoUrl);
            videoImage.setBackground(new BitmapDrawable(getResources(),thumb));
        }
        else if(Commons.thumb!=null) videoImage.setBackground(new BitmapDrawable(getResources(),Commons.thumb));

        vendorPhoto=(CircularNetworkImageView) findViewById(R.id.vendor_photo);
        if(Commons.thisEntity.get_photoUrl().length()>0) vendorPhoto.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageloader);

        fromList=(TextView)findViewById(R.id.videolistview);
        fromList.setOnClickListener(this);
        fromGoogle=(TextView)findViewById(R.id.videosearch);
        fromGoogle.setOnClickListener(this);
        fromCamera=(TextView)findViewById(R.id.videofromcamera);
        fromCamera.setOnClickListener(this);
        checkIn=(TextView)findViewById(R.id.checkin);
        checkIn.setOnClickListener(this);
        barType=(TextView)findViewById(R.id.txv_bartype);
        if(Commons.gameEntityUpload.getBarType().length()>0)barType.setText(Commons.gameEntityUpload.getBarType());
        lytBarType=(LinearLayout) findViewById(R.id.lyt_spin_bartype);
        lytBarType.setOnClickListener(this);
        lytProgress=(LinearLayout)findViewById(R.id.lyt_progress);
        linearLayout = (LinearLayout) findViewById(R.id.opions);
        linearLayout1 = (LinearLayout) findViewById(R.id.lyt_fromlist);
        selectTitle=(TextView)findViewById(R.id.selectfrom);
        videoCategory=(TextView)findViewById(R.id.video_category);
        if(Commons.gameEntityUpload.getVideoType().length()>0)videoCategory.setText(Commons.gameEntityUpload.getVideoType());
        lytVideoCategory=(LinearLayout) findViewById(R.id.lyt_spin_videocategory);
        lytVideoCategory.setOnClickListener(this);
        videoType=(TextView)findViewById(R.id.videotype);
        if(Commons.gameEntityUpload.getVideoTypeSecondary().length()>0)videoType.setText(Commons.gameEntityUpload.getVideoTypeSecondary());
        lytVideoType=(LinearLayout) findViewById(R.id.lyt_spin_videotype);
        lytVideoType.setOnClickListener(this);
        uploadButton=(TextView)findViewById(R.id.uploadbutton);
        uploadButton.setOnClickListener(this);
        barName=(EditText)findViewById(R.id.barname);
        if(Commons.gameEntityUpload.getBarName().length()>0)barName.setText(Commons.gameEntityUpload.getBarName());
        barPlace=(TextView) findViewById(R.id.edt_barplacename);
        if(Commons.gameEntityUpload.getKnownName().length()>0)barPlace.setText(Commons.gameEntityUpload.getKnownName());
        videoName=(EditText)findViewById(R.id.edt_videoname);
        if(Commons.gameEntityUpload.getGameName().length()>0)videoName.setText(Commons.gameEntityUpload.getGameName());

        videoId=(EditText)findViewById(R.id.edt_videoid);
        if(Commons.gameEntityUpload.getVideoId().length()>0)videoId.setText(Commons.gameEntityUpload.getVideoId());
        else if(Commons.videoUrl.length()>0) videoId.setText(Commons.videoUrl);

        videoduration=(EditText)findViewById(R.id.edt_videoduration);
        if(Commons.gameEntityUpload.getDuaration().length()>0)videoduration.setText(Commons.gameEntityUpload.getDuaration());

        teamName=(EditText)findViewById(R.id.edt_videoteam);
        if(Commons.gameEntityUpload.getTeamName().length()>0)teamName.setText(Commons.gameEntityUpload.getTeamName());
        channelTitle=(EditText)findViewById(R.id.edt_videochannel);
        if(Commons.gameEntityUpload.getChannel().length()>0)channelTitle.setText(Commons.gameEntityUpload.getChannel());

        salePrice=(EditText)findViewById(R.id.edt_videoprice);
        if(Commons.gameEntityUpload.getSalePrice().length()>0)salePrice.setText(Commons.gameEntityUpload.getSalePrice());
        rentPrice=(EditText)findViewById(R.id.edt_rent);
        if(Commons.gameEntityUpload.getRentPrice().length()>0)rentPrice.setText(Commons.gameEntityUpload.getRentPrice());
        watchWithPrice=(EditText)findViewById(R.id.edt_pricewatchwithfriend);
        if(Commons.gameEntityUpload.getWatchWithPrice().length()>0)watchWithPrice.setText(Commons.gameEntityUpload.getWatchWithPrice());
        specials=(EditText)findViewById(R.id.edt_special);
        if(Commons.gameEntityUpload.getSpecials().length()>0)specials.setText(Commons.gameEntityUpload.getSpecials());

        dropImage=(ImageView)findViewById(R.id.drop_image);
        lytMenu=(LinearLayout)findViewById(R.id.lyt_spin_menu);
        lytMenu.setOnClickListener(this);
        lytMenuList=(LinearLayout)findViewById(R.id.menuList);
        menuA=(EditText)findViewById(R.id.edt_menua);
        menuB=(EditText)findViewById(R.id.edt_menub);
        menuC=(EditText)findViewById(R.id.edt_menuc);
        menuD=(EditText)findViewById(R.id.edt_menud);
        menuE=(EditText)findViewById(R.id.edt_menue);
        menuF=(EditText)findViewById(R.id.edt_menuf);
        menuG=(EditText)findViewById(R.id.edt_menug);
        menuH=(EditText)findViewById(R.id.edt_menuh);
        menuI=(EditText)findViewById(R.id.edt_menui);
        menuJ=(EditText)findViewById(R.id.edt_menuj);

        if(Commons._video_compressed){

//            SendBird.init(Config.appId, getApplicationContext());
            Commons._video_compressed=false;
//            connect();
        }
    }

    public void initialValues(){
        GameEntity gameEntity=new GameEntity();
        if(barName.getText().length()>0)gameEntity.setBarName(barName.getText().toString());
        if(barType.getText().length()>0)gameEntity.setBarType(barType.getText().toString());
        if(barPlace.getText().length()>0)gameEntity.setKnownName(barPlace.getText().toString());
        if(videoName.getText().length()>0)gameEntity.setGameName(videoName.getText().toString());
        if(videoId.getText().length()>0)gameEntity.setVideoId(videoId.getText().toString());
        if(videoduration.getText().length()>0)gameEntity.setDuaration(videoduration.getText().toString());
        if(videoType.getText().length()>0)gameEntity.setVideoTypeSecondary(videoType.getText().toString());
        if(videoCategory.getText().length()>0)gameEntity.setVideoType(videoCategory.getText().toString());
        if(teamName.getText().length()>0)gameEntity.setTeamName(teamName.getText().toString());
        if(channelTitle.getText().length()>0)gameEntity.setChannel(channelTitle.getText().toString());
        if(salePrice.getText().length()>0)gameEntity.setSalePrice(salePrice.getText().toString());
        if(rentPrice.getText().length()>0)gameEntity.setRentPrice(rentPrice.getText().toString());
        if(watchWithPrice.getText().length()>0)gameEntity.setWatchWithPrice(watchWithPrice.getText().toString());
        if(specials.getText().length()>0)gameEntity.setSpecials(specials.getText().toString());
        if(Commons.videoUrl.length()>0)gameEntity.setGameThumbnailUrl(Commons.videoUrl.toString());

        Commons.gameEntityUpload=gameEntity;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent in=new Intent(this, HomeActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.videolistview:
                initialValues();
                Intent intent=new Intent(this,VideoListActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.videosearch:
                initialValues();
                intent=new Intent(this,GoogleSearchActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.videofromcamera:
                initialValues();
                selectVideo();
                break;
            case R.id.lyt_spin_bartype:
                showChoiceDialog_barType();
                break;
            case R.id.lyt_spin_videocategory:
                showChoiceDialog_videoCategory();
                break;
            case R.id.lyt_spin_videotype:
                showChoiceDialog_videoType();
                break;
            case R.id.checkin:

                showToast("Please wait... ");
                initialValues();
                intent=new Intent(this,UploadCheckInActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);

//                if(barName.getText().length()>0){
//
//                }else showToast("Please input a Restaurant/Bar/Park's name.");
                break;
            case R.id.lyt_spin_menu:
                if (!_openMenus) {
                    lytMenuList.setVisibility(View.VISIBLE);
                    dropImage.setImageResource(R.drawable.ic_drop0);
                    _openMenus = true;
                } else {
                    lytMenuList.setVisibility(View.GONE);
                    dropImage.setImageResource(R.drawable.ic_drop);
                    _openMenus = false;
                }
                break;
            case R.id.uploadbutton:

                if(checkValues()){
                    showConfirmUpload();
                }

//                showConfirmUpload();
                break;
        }
    }

    public void optionView(View view){

        if(!_openOptions ){
            linearLayout1.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translateoff);
            selectTitle.startAnimation(animation1);
            selectTitle.setVisibility(View.GONE);
            animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move1);
            linearLayout1.startAnimation(animation1);
            linearLayout.setVisibility(View.VISIBLE);
            animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move);
            linearLayout.startAnimation(animation2);

            _openOptions=true;
        }else {
            animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            linearLayout.startAnimation(animation1);
            animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            linearLayout1.startAnimation(animation2);
            _openOptions=false;
            selectTitle.setVisibility(View.VISIBLE);
            animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate);
            selectTitle.startAnimation(animation1);
        }
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

    public void showConfirmUpload() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to upload this video?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                uploadVideo();
            }

        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    private void uploadVideo() {

        final String special= videoduration.getText().toString()+"    *** "+specials.getText().toString();

        String url = ReqConst.SERVER_URL + "saveVideo";

        Log.d("request url :", url.toString());

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.d("REST response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseVideoInfoResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showToast(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                if(videoId.getText().toString().startsWith("/storage/")) params.put("vIdentification", "");
                else params.put("vIdentification", videoId.getText().toString());
                params.put("vType", video_Category);
                params.put("vOtherType", video_Type);
                params.put("vName", videoName.getText().toString());
                params.put("vTeamName", teamName.getText().toString());
                params.put("vChannelName", channelTitle.getText().toString());
                params.put("vPrice", salePrice.getText().toString());
                params.put("barName", barName.getText().toString());
                params.put("barType", barType.getText().toString());
                params.put("barPlaceName", barPlace.getText().toString());
                params.put("barRentPrice", rentPrice.getText().toString());
                params.put("barMeetPrice", watchWithPrice.getText().toString());
                params.put("vThumbnail", Commons.videoUrl);
                params.put("vSpecial", special);
                params.put("barMenuList", createMenuData());  Log.d("Menus===>",createMenuData());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseVideoInfoResponse(String json){

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("resultcode=====> :",success);

            if (success==String.valueOf(0)) {

                _idx = response.getInt("vid");
                Commons._idx=_idx;

                if(videoId.getText().toString().startsWith("/storage/")){

                    uploadVideoThumbnail();

                }
                else {

                    showToast("Successfully file uploaded on server.\n Now creating channel.........");
                    initFieldValues();
//                    SendBird.init(Config.appId, getApplicationContext());
//                    connect1();
                }

            } else {

                String error = response.getString(ReqConst.RES_ERROR);
//                showAlertDialog(getString(R.string.error));
                showToast(error);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    public void uploadVideoFile() {

        try {

            File file = new File(videoId.getText().toString());     Log.d("video ID===>",videoId.getText().toString());
            //        File file = new File("/storage/emulated/0/DCIM/Camera/20170115_141323.mp4");

            final Map<String, String> params = new HashMap<>();
            params.put("vid", String.valueOf(_idx));
    //        params.put("filename", videoName.getText().toString());

            String url = ReqConst.SERVER_URL + "uploadVideoFile";

            showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    showToast(getString(R.string.photo_upload_fail));
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadVideoFileResponse(json);
                    Log.d("imageJson===",json.toString());
                    Log.d("params====",params.toString());
                }
            }, file, ReqConst.PARAM_FILE, params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VaCayApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            closeProgress();
            showToast(getString(R.string.photo_upload_fail));
        }
    }


    public void ParseUploadVideoFileResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);

            if (result_code == 0) {
                showToast("Successfully video file uploaded on server.");
                initFieldValues();

            } else if(result_code==113){
                showToast(getString(R.string.unregistered_user));
            }else if(result_code==103){
                showToast("Upload file size error!");
            }
            else {
                showToast("Video file registration failed.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Video file registration failed.");
        }
    }

    public void uploadVideoThumbnail() {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("vid", String.valueOf(_idx));
            //        params.put("filename", videoName.getText().toString());

            String url = ReqConst.SERVER_URL + "uploadVideoThumbnail";

            showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    showToast(getString(R.string.photo_upload_fail));
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadVideoThumbnailResponse(json);
                    Log.d("imageJson===",json.toString());
                    Log.d("params====",params.toString());
                }
            }, Commons.file, ReqConst.PARAM_FILE, params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VaCayApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            closeProgress();
            showToast(getString(R.string.photo_upload_fail));
        }
    }


    public void ParseUploadVideoThumbnailResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);

            if (result_code == 0) {

                Commons.file=null;

                initialValues();
                Intent i=new Intent(getApplicationContext(), VideoCompressActivity.class);
                startActivity(i);
                finish();

            } else if(result_code==113){
                showToast(getString(R.string.unregistered_user));
            }else if(result_code==103){
                showToast("Upload file size error!");
            }
            else {
                showToast("Video file registration failed.");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast("Video file registration failed.");
        }
    }

    private String createMenuData(){
        String jsonString = "";

        try {

            JSONObject menuObj = new JSONObject(); // Main Json

            menuObj.put("menuA",menuA.getText().toString());
            menuObj.put("menuB",menuB.getText().toString());
            menuObj.put("menuC",menuC.getText().toString());
            menuObj.put("menuD",menuD.getText().toString());
            menuObj.put("menuE",menuE.getText().toString());
            menuObj.put("menuF",menuF.getText().toString());
            menuObj.put("menuG",menuG.getText().toString());
            menuObj.put("menuH",menuH.getText().toString());
            menuObj.put("menuI",menuI.getText().toString());
            menuObj.put("menuJ",menuJ.getText().toString());

            jsonString = menuObj.toString();


        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    private void initFieldValues() {

        barName.setText("");
        barType.setText("");
        barPlace.setText("");
        videoId.setText(""); Commons.videouri=null;
        videoduration.setText("");
        videoName.setText("");
        videoType.setText("");
        videoCategory.setText("");
        teamName.setText("");
        channelTitle.setText("");
        salePrice.setText("");
        specials.setText("");
        rentPrice.setText("");
        watchWithPrice.setText("");
        videoImage.setImageUrl("",_imageloader); Commons.videoUrl=""; Commons.thumb=null;
        menuA.setText("");
        menuB.setText("");
        menuC.setText("");
        menuD.setText("");
        menuE.setText("");
        menuF.setText("");
        menuG.setText("");
        menuH.setText("");
        menuI.setText("");
        menuJ.setText("");
        if(_openMenus) {
            lytMenuList.setVisibility(View.GONE);
            dropImage.setImageResource(R.drawable.ic_drop);
            _openMenus = false;
        }

    }

    public boolean checkValues(){
        if(barName.getText().length()>0 && barType.getText().length()>0 && barPlace.getText().length()>0 && videoName.getText().length()>0 &&
                videoId.getText().length()>0 && videoCategory.getText().length()>0 &&
                Commons.videoUrl.length()>0) return true;
        else showToast("Please check empty fields and fill in the blanks.");
        return false;
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showChoiceDialog_barType() {

        final String[] items = {"Restaurant",
                "Bar",
                "Park"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                barType.setText(items[item]);
                bar_Type=items[item];
                bar_Type_id=item;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void showChoiceDialog_videoCategory() {

        final String[] items = {"Game",
                "Movie",
                "News"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                videoCategory.setText(items[item]);
                video_Category=items[item];
                video_Category_id=item;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_videoType() {

        final String[][] items_list={{""},{"Romantic","Comedy","Horror","Drama","All"},
                {"CNN","CNBC","FOX","BLOOMBERG"}};

        final String[] items = items_list[video_Category_id];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select"+" "+video_Category+"'s Type.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(video_Category.equals("Game")) videoType.setText("");
            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(video_Category.equals("Game")) videoType.setText("");
            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                videoType.setText(items[item]);
                video_Type=items[item];
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void gotoTakeVideo(String option) {
        Intent intent=new Intent(this,VideoCaptureActivity.class);
        intent.putExtra("OPTION",option);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void selectVideo() {

        final String[] items = {"Take video", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {

    //                recordVideo();
                    gotoTakeVideo("capture");

                } else {
                    gotoTakeVideo("pick");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://35.162.12.207/index.php/Api/uploadVideoFile");

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(Commons.compressedvideoUrl);
    //            File sourceFile = new File(Commons.videoUrl);

                // Adding file data to http body
                entity.addPart("vid", new StringBody(String.valueOf(Commons._idx)));
                entity.addPart("file", new FileBody(sourceFile));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);    Log.d("Response===>",response.toString());
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 100) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showToast("Successfully uploaded on server.\n Now creating channel.........");
            lytProgress.setVisibility(View.GONE);
            Commons.compressedvideoUrl="";
            initFieldValues();

            createChannel();

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    private void connect() {
//        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//                if (e != null) {
//                    showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                    return;
//                }
//
//                String nickname = mNickname;
//
//                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
//                    @Override
//                    public void onUpdated(SendBirdException e) {
//                        if (e != null) {
//                            showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                            //    setState(State.DISCONNECTED);
//                            return;
//                        }
//
////                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
////                        editor.putString("user_id", sUserId);
////                        editor.putString("nickname", mNickname);
////                        editor.commit();
//                        showToast("Successfully connected.");
//                        lytProgress.setVisibility(View.VISIBLE);
//                        new UploadFileToServer().execute();
//                        //    setState(State.CONNECTED);
//                    }
//                });
//
////                if (FirebaseInstanceId.getInstance().getToken() == null) return;
//
//            }
//        });
//
//        //    setState(State.CONNECTING);
//    }
//
//    private void connect1() {
//        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//                if (e != null) {
//                    showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                    return;
//                }
//
//                String nickname = mNickname;
//
//                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
//                    @Override
//                    public void onUpdated(SendBirdException e) {
//                        if (e != null) {
//                            showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                            //    setState(State.DISCONNECTED);
//                            return;
//                        }
//
////                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
////                        editor.putString("user_id", sUserId);
////                        editor.putString("nickname", mNickname);
////                        editor.commit();
//                        showToast("Successfully connected.");
//                        createChannel();
//                        //    setState(State.CONNECTED);
//                    }
//                });
//
////                if (FirebaseInstanceId.getInstance().getToken() == null) return;
//
//            }
//        });
//
//        //    setState(State.CONNECTING);
//    }
//
//    private void disconnect() {
//        SendBird.disconnect(new SendBird.DisconnectHandler() {
//            @Override
//            public void onDisconnected() {
//                //            setState(State.DISCONNECTED);
//            }
//        });
//    }
//
//    private void createChannel(final String channelName){
//
//        List<User> operators = new ArrayList<>();
//        operators.add(SendBird.getCurrentUser());
//
//        OpenChannel.createChannel(channelName, null, null, operators, new OpenChannel.OpenChannelCreateHandler() {
//            @Override
//            public void onResult(OpenChannel openChannel, SendBirdException e) {
//                if (e != null) {
//                    showToast(e.getCode() + ":" + e.getMessage());
//                    return;
//                }
//                showAlert("Successfully channel created.");
//                Log.d("UploadChannel===>",channelName);
//            }
//        });
//    }

    public void createChannel() {

        String url = ReqConst.SERVER_URL + "getVideo";

        showProgress();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetBeautiesResponse(String json) {

        closeProgress();
        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("VIDEOresponse===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray videoInfo = response.getJSONArray("video_infos");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("VIDEOs===",videoInfo.toString());

                for (int i = 0; i < videoInfo.length(); i++) {

                    JSONObject jsonVideo = (JSONObject) videoInfo.get(i);

                    GameEntity video = new GameEntity();

                    video.set_idx(jsonVideo.getInt("vid"));

                    if(jsonVideo.getString("vFileLink").length()>0) {

                        video.setVideoId(jsonVideo.getString("vFileLink"));
                        video.setGameThumbnailUrl(jsonVideo.getString("vFileThumbnail"));
                    }

                    else {
                        video.setVideoId(jsonVideo.getString("vIdentification"));
                        video.setGameThumbnailUrl(jsonVideo.getString("vThumbnail"));
                    }


                    Log.d("VideoID===>",video.getVideoId());

                    video.setGameName(jsonVideo.getString("vName"));
                    video.setVideoType(jsonVideo.getString("vType"));
                    video.setVideoTypeSecondary(jsonVideo.getString("vOtherType"));
                    video.setTeamName(jsonVideo.getString("vTeamName"));
                    video.setChannel(jsonVideo.getString("vChannelName"));
                    video.setSalePrice(jsonVideo.getString("vPrice"));
                    video.setSpecials(jsonVideo.getString("vSpecial")); Log.d("Special==>",video.getSpecials());
//                    video.setGameThumbnailUrl(jsonVideo.getString("vThumbnail"));
                    video.setBarName(jsonVideo.getString("barName"));
                    video.setBarType(jsonVideo.getString("barType"));
                    video.setKnownName(jsonVideo.getString("barPlaceName"));
                    video.setRentPrice(jsonVideo.getString("barRentPrice"));
                    video.setWatchWithPrice(jsonVideo.getString("barMeetPrice"));
                    if(video.getSpecials().length()>0){
                        try{
                            String duration=video.getSpecials().substring(0,8).trim();
                            video.setDuaration(duration);
                        }catch (StringIndexOutOfBoundsException e){}
                    }

                    String menus = jsonVideo.getString("barMenuList");

                    JSONObject menuObj = new JSONObject(menus);
                    Log.d("menuObj===>",menuObj.toString());

                    ArrayList<String> arrayList=new ArrayList<>();
                    for(int k=0;k<menus.length();k++) {
                        arrayList.add(menuObj.getString("menuA"));
                        arrayList.add(menuObj.getString("menuB"));
                        arrayList.add(menuObj.getString("menuC"));
                        arrayList.add(menuObj.getString("menuD"));
                        arrayList.add(menuObj.getString("menuE"));
                        arrayList.add(menuObj.getString("menuF"));
                        arrayList.add(menuObj.getString("menuG"));
                        arrayList.add(menuObj.getString("menuH"));
                        arrayList.add(menuObj.getString("menuI"));
                        arrayList.add(menuObj.getString("menuJ"));

                    }
                    video.setResMenus(arrayList);
//                    if(video.get_idx()==Commons._idx) {
//                        if(video.getVideoId().contains("http://")) {
//                            Log.d("UploadChannel===>",video.getVideoId().substring(47,video.getVideoId().length()-4));
//                            createChannel(video.getVideoId().substring(47,video.getVideoId().length()-4));
//                        }
//                        else {
//                            Log.d("UploadChannel===>",video.getVideoId());
//                            createChannel(video.getVideoId());
//                        }
//
//                    }
                }

            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
    //            launchUploadActivity(false);
                showAlert("You recorded a video successfully!!!");

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

}

