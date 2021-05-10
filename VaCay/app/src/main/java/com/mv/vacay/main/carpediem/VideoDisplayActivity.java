package com.mv.vacay.main.carpediem;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.CheckinListAdapter;
import com.mv.vacay.adapter.MessageAdapter;
import com.mv.vacay.adapter.TVWatchingAdapter;
import com.mv.vacay.adapter.TVWatchingPeopleAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.utils.MultiPartRequest;
import com.mv.vacay.utils.MyVideoView;
//import com.sendbird.android.OpenChannel;
//import com.sendbird.android.OpenChannelListQuery;
//import com.sendbird.android.SendBird;
//import com.sendbird.android.SendBirdException;
//import com.sendbird.android.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 */
public class VideoDisplayActivity extends YouTubeFailureRecoveryActivity implements View.OnClickListener{

//    private OpenChannelListQuery channelListQuery;

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    private static final String TAG = VideoDisplayActivity.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    ImageView back,myCommentImage,attachButton,barImage;
    CircularNetworkImageView personImage, myImage;
    NetworkImageView personCommentImage;
    TextView videoName,barName,spinWatchingPeople,spinCheckedin,personName,myName,
            commentButton,fullScreenButton,checkinnote, personComment,dateButton;
    static EditText myComment;
    ListView watchingList,checkinList;
    LinearLayout watchingPeopleList,checkinPeopleList,watchingPersonComments;
    ImageLoader _imageLoader;
    private YouTubePlayer player;
    boolean _watching_people=false,_checkin=false;
    ArrayList<UserEntity> _datas_checkin=new ArrayList<>();
    ArrayList<UserEntity> _datas_watching=new ArrayList<>();
    private ProgressDialog _progressDlg;
    AdView mAdView;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;
    Switch messageButton;
    static String date="";
    static String message="";
    File file;
    int _idx;
    MyVideoView videoView;
    LinearLayout ui_lytvideo;
    static String datebuff="";
    static String string="";
    static TextView DateEdit;
    boolean _is_message=false;
    boolean _is_select_watching_person=false;
    int year, month, day,hour,minute;
    ArrayList<MessageEntity> _datas=new ArrayList<>();
    TVWatchingAdapter adapter;
    MessageAdapter messageAdapter;
    CheckinListAdapter checkinListAdapter=new CheckinListAdapter(this);
    TVWatchingPeopleAdapter tvWatchingPeopleAdapter=new TVWatchingPeopleAdapter(this);

    public static String sUserId;
    private String mNickname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display);

//        SendBird.init(Config.appId, this);

        mNickname=Commons.thisEntity.get_fullName();
        sUserId=Commons.thisEntity.get_email();

//        connect();

        Commons._video_activity=true;

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Commons.year=year;
        Commons.month=month;
        Commons.day=day;
        Commons.hour=hour;
        Commons.min=minute;

        DateEdit = (TextView) findViewById(R.id.sendate);

        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons._calendar_set=false;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });

        myComment=(EditText)findViewById(R.id.mycomment);
 //       string=Commons.now_watching_message;

//        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
//        youTubeView.initialize(Config.API_KEY, this);

        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Config.API_KEY, this);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        loadLayout();

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    private void loadLayout() {

        barImage=(ImageView)findViewById(R.id.bar_image);
        if(Commons.gameEntity.getBarType().trim().equals("Restaurant"))
            barImage.setImageResource(R.drawable.reestaurant);
        else if(Commons.gameEntity.getBarType().trim().equals("Bar"))
            barImage.setImageResource(R.drawable.beer);
        else if(Commons.gameEntity.getBarType().trim().equals("Park"))
            barImage.setImageResource(R.drawable.ppark);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        videoName=(TextView)findViewById(R.id.title);
        videoName.setText(Commons.gameEntity.getGameName());
        barName=(TextView)findViewById(R.id.barname);
        barName.setText(Commons.gameEntity.getBarName());
        spinWatchingPeople=(TextView)findViewById(R.id.people);
        spinWatchingPeople.setOnClickListener(this);
        spinCheckedin=(TextView)findViewById(R.id.checkin);
        spinCheckedin.setOnClickListener(this);

        ui_lytvideo=(LinearLayout)findViewById(R.id.lytvideo);
        videoView=(MyVideoView) findViewById(R.id.videoView);
        videoView.setVideoSize(ui_lytvideo.getWidth(),ui_lytvideo.getHeight());

    //    Log.d("VIDEOID===>",Commons.gameEntity.getGameThumbnailUrl().substring(1,8));
        if(Commons.gameEntity.getVideoId().endsWith(".mp4")){
            ui_lytvideo.setVisibility(View.VISIBLE);
            videoView.setBackground(null);
            videoView.setMediaController(new MediaController(this));
//                videoView.setVideoURI(Commons.videouri);
            videoView.setVideoPath(Commons.gameEntity.getVideoId());
            videoView.requestFocus();
//            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    videoView.start();
                }
            });
        }else ui_lytvideo.setVisibility(View.GONE);

        messageButton=(Switch)findViewById(R.id.swich_message);
        messageButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if(b) {
                    _is_message=true;
                   messageButton.setTextColor(Color.parseColor("#fb7a23"));
                   messageButton.setText("Message ON");
               }

                else {
                   _is_message=false;
                   messageButton.setTextColor(Color.parseColor("#000000"));
                   messageButton.setText("Message OFF");
               }
            }
        });

        personImage=(CircularNetworkImageView)findViewById(R.id.person);
        myImage=(CircularNetworkImageView)findViewById(R.id.myphoto);
        myImage.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);

        personName=(TextView)findViewById(R.id.person_name);
        myName=(TextView)findViewById(R.id.myname);
        myName.setText(Commons.thisEntity.get_fullName());

        personComment=(TextView) findViewById(R.id.person_comment);

        personCommentImage=(NetworkImageView)findViewById(R.id.person_image);
        myCommentImage=(ImageView)findViewById(R.id.myimage);
        fullScreenButton=(TextView)findViewById(R.id.fullscreenbutton);
        fullScreenButton.setOnClickListener(this);
        commentButton=(TextView)findViewById(R.id.commentbutton);
        commentButton.setOnClickListener(this);
        attachButton=(ImageView)findViewById(R.id.attachbutton);
        attachButton.setOnClickListener(this);

        watchingList=(ListView)findViewById(R.id.list_people_watching);
        checkinList=(ListView)findViewById(R.id.list_checkin);

        checkinnote=(TextView)findViewById(R.id.checkin1);
        watchingPeopleList=(LinearLayout)findViewById(R.id.lyt_listPeopleWatching);
        watchingPersonComments=(LinearLayout)findViewById(R.id.lyt_watchingperson);
        checkinPeopleList=(LinearLayout)findViewById(R.id.lyt_listCheckin);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player,
                                        boolean wasRestored) {
        this.player = player;

        if (!wasRestored) {

            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            if(!Commons.gameEntity.getVideoId().substring(1,7).equals("storage")){

                player.cueVideo(Commons.gameEntity.getVideoId());
            }

            //        player.cueVideo(Commons.gameEntity.getVideoId());

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                   // player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    player.play();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                    showToast(errorReason.toString());
                }
            });
    //        player.play();
            //    player.cueVideo("4mu4Fz-I5sw");
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    //    return (YouTubePlayerView) findViewById(R.id.youtube_view);
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
        switch (requestCode) {

            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

//                        BitmapFactory.Options options = new BitmapFactory.Options();
//
//                        final Bitmap bitmap = BitmapFactory.decodeFile(_photoPath, options);


                        in.close();

                        //set The bitmap data to image View
                        if(bitmap!=null){

                            Commons.bitmap_activity=bitmap;
                            myCommentImage.setVisibility(View.VISIBLE);
                            myCommentImage.setImageBitmap(bitmap);
//                        imagemessage.setImageBitmap(Commons.bitmap_activity);
                            //           Constants.userphoto=ui_imvphoto.getDrawable();
                            _photoPath = saveFile.getAbsolutePath();
                            Commons.imageUrl=_photoPath;

                        }
                        //           back();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();
                }

            case Constants.PICK_FROM_CAMERA: {
                try {

                    _photoPath=_imageCaptureUri.getPath();

                    //_photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
                    //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    intent.putExtra("crop", true);
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                    intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("noFaceDetection", true);
                    //intent.putExtra("return-data", true);
                    intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                    startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private int parseInt(String text, int defaultValue) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return defaultValue;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                Commons._video_activity=false;

                Intent in=new Intent(this,GameDetailActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);

//                Commons.now_watching_message="end/"+Commons.now_watching_message;
//                sendMsg(Commons.gameEntity.getVideoId().toLowerCase());

                break;
            case R.id.fullscreenbutton:
                Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                        this, Config.API_KEY, Commons.gameEntity.getVideoId(), 100, true, false);
                if (intent != null) {
                    if (canResolveIntent(intent)) {
                        startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
                    } else {
                        // Could not resolve the intent - must need to install or update the YouTube API service.
                        YouTubeInitializationResult.SERVICE_MISSING
                                .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
                    }
                }
                break;
            case R.id.commentbutton:

                if(myComment.getText().toString().length()==0) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.toast_view, null);
                    TextView textView=(TextView)dialogView.findViewById(R.id.text);
                    textView.setText("Please input your comment or message.");
                    Toast toast=new Toast(this);
                    toast.setView(dialogView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }else if(!_is_message){

                    message = myComment.getText().toString();
                    if(bitmap !=null){
                        bitmap=null;
                        myCommentImage.setVisibility(View.GONE);
                    }

                    if(date.equals("")) datebuff="";

                    Commons.comment=datebuff + message;
                    Commons.now_watching_message=datebuff + message;

                    myComment.setText(""); date="";


                    intent=new Intent(this,PostCommentActivity.class);
                    startActivity(intent);
                }
                else {
                    if(_is_select_watching_person){
                        Commons._allow_sendMessage=true;
                        message = myComment.getText().toString();
                        if(bitmap !=null){
                            bitmap=null;
                            myCommentImage.setVisibility(View.GONE);
                        }

                        if(date.equals("")) datebuff="";

                        Commons.comment=datebuff + message;
                        Commons.now_watching_message=datebuff + message;

                        myComment.setText("");  date="";


                        intent=new Intent(this,PostCommentActivity.class);
                        startActivity(intent);

                    }else showToast("Please select a friend to send your message to.");
                }
                break;

            case R.id.attachbutton:
                openMenuItems();
                break;
            case R.id.people:

                if (!_watching_people) {
                    watchingPeopleList.setVisibility(View.VISIBLE);
                    spinWatchingPeople.setActivated(true);
                    _watching_people=true;
                    if(_checkin){
                        checkinnote.setVisibility(View.VISIBLE);
                    }
                    spinWatchingPeople.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop12,// right
                            0//bottom
                    );

//                    showTVWatchingPeople();
                    _datas.clear();
                    getMessage(Commons.gameEntity.getVideoId());

                } else {
                    _is_select_watching_person=false;
                    watchingPeopleList.setVisibility(View.GONE);
                    _watching_people=false;
                    if(_checkin){
                        checkinnote.setVisibility(View.GONE);
                    }
                    spinWatchingPeople.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop11,// right
                            0//bottom
                    );
                }

                break;
            case R.id.checkin:

                if (!_checkin) {
                    checkinPeopleList.setVisibility(View.VISIBLE);
                    spinCheckedin.setActivated(true);
                    _checkin=true;
                    if(_watching_people){
                        checkinnote.setVisibility(View.VISIBLE);
                    }
                    spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop12,// right
                            0//bottom
                    );

                    checkIn();

                } else {
                    checkinPeopleList.setVisibility(View.GONE);
                    _checkin=false;
                    if(_watching_people){
                        checkinnote.setVisibility(View.GONE);
                    }
                    spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop11,// right
                            0//bottom
                    );
                }

                break;

        }
    }
    public void checkIn() {

        _datas_checkin.clear();
        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETALLUSERS;

//        String params = String.format("/%d", _curpage);
//        url += params;

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        closeProgress();
        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                double[] lats={39.5530507,39.7380517,39.5542507,39.5501507};
                double[] lngs={-105.78306,-104.99506,-105.78706,-105.78406};
                String[] publicNames={"Colorado","denver","colorado","colorado"};
                String[] relationships={"In Relationship","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(user.get_age_range());
                    int age=year-birthyear;
                    user.set_age_range(String.valueOf(age));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("-",","));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    user.set_relations(jsonUser.getString("relationship"));
                    user.set_publicName(jsonUser.getString("place_name").replace("-",",").replace("%20"," "));
                    user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon").replace("%20","-").replace("-",".")));
                    user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat").replace("%20","-").replace("-",".")));

//                    if(jsonUser.getString("place_name").replace("-",",").replace("%20"," ").length()>0){
//
//                    }
//                    else {
//                        user.set_relations(relationships[i]);
//                        user.set_publicName(publicNames[i]);
//                        user.set_userlng(lngs[i]);
//                        user.set_userlat(lats[i]);
//                    }

                    // except me
//                    if (user.get_idx() == Commons.thisEntity.get_idx())
//                        continue;

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    try{
                        if(Commons.gameEntity.getKnownName().toLowerCase().trim().equals(user.get_publicName().toLowerCase().trim())) _datas_checkin.add(0,user);
                        Log.d("Datas===",_datas_checkin.toString());

                    }catch (NullPointerException e){}
                }

                if(_datas_checkin.isEmpty()) showToast("No people checked in");

                checkinListAdapter.setUserDatas(_datas_checkin);
                checkinList.setAdapter(checkinListAdapter);

            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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
    public void showTVWatchingPeople() {

        _datas_watching.clear();
        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETALLUSERS;

//        String params = String.format("/%d", _curpage);
//        url += params;

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse1(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse1(String json) {

        closeProgress();
        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                double[] lats={39.5530507,39.7380517,39.5542507,39.5501507};
                double[] lngs={-105.78306,-104.99506,-105.78706,-105.78406};
                String[] publicNames={"colorado","denver","colorado","colorado"};
                String[] relationships={"In Relationship","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(user.get_age_range());
                    int age=year-birthyear;
                    user.set_age_range(String.valueOf(age));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("-",","));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    try{
                        user.set_relations(relationships[i]);
                        user.set_publicName(publicNames[i]);
                        user.set_userlng(lngs[i]);
                        user.set_userlat(lats[i]);
                    }catch(NullPointerException e){}catch(IndexOutOfBoundsException e){}
//                    user.set_aboutme(jsonUser.getString(ReqConst.RES_ABOUTME));   //user email
//                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));  //user survey info
//                    user.set_friendCount(jsonUser.getInt(ReqConst.RES_FRIENDCOUNT));  //message

                    // except me

//                    if (user.get_idx() == Commons.thisEntity.get_idx())
//                        continue;

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    try{
                        if(Commons.gameEntity.getKnownName().toLowerCase().trim().equals(user.get_publicName().toLowerCase().trim()))
                            _datas_watching.add(0,user);
                        Log.d("Datas===",_datas_watching.toString());

                    }catch (NullPointerException e){}
                }

                tvWatchingPeopleAdapter.setUserDatas(_datas_watching);
                watchingList.setAdapter(tvWatchingPeopleAdapter);

            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }
    public void showTVWatchingPersonComments1(){

        watchingPeopleList.setVisibility(View.GONE);
        _watching_people=false;
        if(_checkin){
            checkinnote.setVisibility(View.GONE);
        }
        spinWatchingPeople.setCompoundDrawablesWithIntrinsicBounds(
                0,// left
                0,//top
                R.drawable.ic_drop11,// right
                0//bottom
        );

        watchingPersonComments.setVisibility(View.VISIBLE);
        if(Commons.userEntity.get_photoUrl().length()>0)
            personImage.setImageUrl(Commons.userEntity.get_photoUrl(),_imageLoader);
        personName.setText(Commons.userEntity.get_fullName());
        personComment.setText("");
        personCommentImage.setVisibility(View.VISIBLE);
        personCommentImage.setImageUrl("",_imageLoader);
    }

    public void showTVWatchingPersonComments(){
//        getSentMessage(Commons.userEntity.get_email());
        watchingPeopleList.setVisibility(View.GONE);
        _watching_people=false;
        if(_checkin){
            checkinnote.setVisibility(View.GONE);
        }
        spinWatchingPeople.setCompoundDrawablesWithIntrinsicBounds(
                0,// left
                0,//top
                R.drawable.ic_drop11,// right
                0//bottom
        );

        watchingPersonComments.setVisibility(View.VISIBLE);
        if(Commons.messageEntity.get_photoUrl().length()>0) {
            personImage.setImageUrl(Commons.messageEntity.get_photoUrl(), _imageLoader);
        }
        personName.setText(Commons.messageEntity.get_userfullname());
        personComment.setText(Commons.messageEntity.get_usermessage());
        if(Commons.messageEntity.get_imageUrl().length()>0){
            personCommentImage.setVisibility(View.VISIBLE);
            personCommentImage.setImageUrl(Commons.messageEntity.get_imageUrl(),_imageLoader);
        }else personCommentImage.setVisibility(View.GONE);

        _is_select_watching_person=true;
    }

    public void showSelectedPersonMessages(){
        _datas.clear();
        getSentMessage(Commons.userEntity.get_email());
    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, Commons.hour, Commons.min,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if(hourOfDay>12){
                hourOfDay=hourOfDay-12;
                date=date + " - " + hourOfDay + ":" + minute+" PM";
            }else {
                date=date+" - " + hourOfDay + ":" + minute+" AM";
            }

            datebuff = date+"\n";
            if(date.equals("")) datebuff="";

//            Commons.comment=datebuff + message;
//            Commons.now_watching_message=datebuff + message;
//
//            myComment.setText(""); date="";
//
//
//            Intent intent=new Intent(getActivity(),PostCommentActivity.class);
//            startActivity(intent);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        String[] monthes={"January","February","March","April","May","June","July","August","September","October","November","December"};

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this,Commons.year, Commons.month, Commons.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month=month+1;
            date="Sent at: "+month+"/" + day + "/" + year;
        }
    }

    public void showTruitonTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTruitonDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    private void openMenuItems() {
        View view = findViewById(R.id.attachbutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.attach_menu);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

    }

    //    public void takePhoto(MenuItem menuItem) {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        String picturePath = BitmapUtils.getTempFolderPath() + "photo_temp.jpg";
//        _imageCaptureUri = Uri.fromFile(new File(picturePath));
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
//        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);//????????????????????????///
//
//    }

    public abstract class YouTubeFailureRecoveryActivity extends YouTubeBaseActivity implements
            YouTubePlayer.OnInitializedListener {

        private static final int RECOVERY_DIALOG_REQUEST = 1;

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                            YouTubeInitializationResult errorReason) {
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            } else {
                String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == RECOVERY_DIALOG_REQUEST) {
                // Retry initialization if user performed a recovery action
                getYouTubePlayerProvider().initialize(Config.API_KEY, this);
            }
        }
        protected abstract YouTubePlayer.Provider getYouTubePlayerProvider();
    }

    public void getMessage(String email) {

        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETMAIL;      //  "Making%20A%20Christmas%20Card%20with%206%20Cats"

        String params = String.format("/%s",email.toLowerCase());
        url += params;   Log.d("URL=====>",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                closeProgress();
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

            JSONObject response = new JSONObject(json);Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS) {

                JSONArray messages = response.getJSONArray(ReqConst.RES_MESSAGECONTENT);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("MESSAGES===", messages.toString());

                for (int i = 0; i < messages.length(); i++) {

                    JSONObject jsonMessage = (JSONObject) messages.get(i);

                    MessageEntity messageEntity = new MessageEntity();

                    //        messageEntity.set_idx(jsonMessage.getInt("mail_id"));
                    messageEntity.set_useremail(jsonMessage.getString("from_mail").replace("%20"," "));
                    messageEntity.setUserfullname(jsonMessage.getString("first_name").replace("-", ".")+" "+jsonMessage.getString("last_name").replace("-", "."));
                    messageEntity.set_photoUrl(jsonMessage.getString("photo_url"));
                    messageEntity.set_usermessage(jsonMessage.getString("text_message")
//                            .replace("%20"," ")
//                            .replace("%26"," & ")
//                            .replace("-",",")
                    );
                    messageEntity.set_imageUrl(jsonMessage.getString("image_message_url"));
                    messageEntity.set_requestLatLng(new LatLng(jsonMessage.getDouble("lat_message"),jsonMessage.getDouble("lon_message")));

                        Log.d("Message0===",String.valueOf(messageEntity.get_usermessage().length()));

//                    if(messageEntity.get_usermessage().startsWith("end/start/") && messageEntity.get_usermessage().length()>10){
//                        Log.d("Message1===",String.valueOf(messageEntity.get_usermessage().length()));
//                        messageEntity.set_usermessage(messageEntity.get_usermessage().substring(10,messageEntity.get_usermessage().length()));
//                        Log.d("MessageSub===>",messageEntity.get_usermessage());
//                    }else

//                    if(messageEntity.get_usermessage().startsWith("start/")
////                            && messageEntity.get_usermessage().length()>6
//                            ){
//                        Log.d("Message1===",String.valueOf(messageEntity.get_usermessage().length()));
//                        messageEntity.set_usermessage(messageEntity.get_usermessage().substring(6,messageEntity.get_usermessage().length()));
//                        _datas.add(0, messageEntity);
//                        Log.d("MessageSub===>",messageEntity.get_usermessage());
//                    }

                    _datas.add(0, messageEntity);

//                    if(messageEntity.get_usermessage().startsWith("start/") && messageEntity.get_usermessage().length()>6) {
//                        _datas.add(0, messageEntity);
//                    }
                }
                if(_datas.isEmpty()){
                    showToast("No others watching this video.");
                }
                adapter = new TVWatchingAdapter(this, _datas);
                adapter.notifyDataSetChanged();
                watchingList.setAdapter(adapter);

            }else if(result_code ==109){
                showToast("No comments from others.");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    public void getSentMessage(String email) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_ALLSENTMAIL;

        String params = String.format("/%s",email.replace(" ","%20"));
        url += params;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse1(json);
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

    public void parseGetBeautiesResponse1(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSESENT===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS) {

                JSONArray messages = response.getJSONArray(ReqConst.RES_SENTMAILINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("SENTMESSAGES===", messages.toString());

                for (int i = 0; i < messages.length(); i++) {

                    JSONObject jsonMessage = (JSONObject) messages.get(i);

                    MessageEntity messageEntity = new MessageEntity();

                    messageEntity.set_idx(jsonMessage.getInt("mail_id"));
                    Log.d("USERID===", String.valueOf(messageEntity.get_idx()));
                    messageEntity.set_useremail(jsonMessage.getString("to_mail").replace("%20"," "));  Log.d("ToMail===>",messageEntity.get_useremail());
                    messageEntity.setUserfullname(jsonMessage.getString("first_name").replace("-", ".")+" "+jsonMessage.getString("last_name").replace("-", "."));
                    messageEntity.set_usermessage(jsonMessage.getString("text_message")
//                            .replace("%20"," ")
//                            .replace("%26"," & ")
//                            .replace("-",",")
                    );
                    messageEntity.set_imageUrl(jsonMessage.getString("image_message_url"));
                    messageEntity.set_requestLatLng(new LatLng(jsonMessage.getDouble("lat_message"),jsonMessage.getDouble("lon_message")));

//                    if(messageEntity.get_useremail().equals(Commons.gameEntity.getVideoId()))
                        _datas.add(0,messageEntity);
                }
                adapter = new TVWatchingAdapter(this, _datas);
//                adapter.resorting(_datas);
                adapter.notifyDataSetChanged();
                watchingList.setAdapter(adapter);

            }else if(result_code ==110){
                showToast("No messages by the user.");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

//    public void fromGallery(MenuItem menuItem) {
//
//        Intent intent=new Intent(this,TakePhotoActivity.class);
//        intent.putExtra("key","gallery");
//        startActivity(intent);
//        finish();
//        overridePendingTransition(0,0);
//
//    }
//
//    public void takePhoto(MenuItem menuItem) {
//
//        Intent intent=new Intent(this,TakePhotoActivity.class);
//        intent.putExtra("key","camera");
//        startActivity(intent);
//        finish();
//        overridePendingTransition(0,0);
//
//    }

    public void fromGallery(MenuItem menuItem) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto(MenuItem menuItem) {

        //  _takePhoto=true;

//        ContentValues values = new ContentValues(1);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//        _imageCaptureUri = this.getContentResolver()
//                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
//                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
//        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);




        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        _imageCaptureUri = getOutputMediaFileUri(1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);

        // start the image capture Intent
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

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
        }
//        else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                    + "VID_" + timeStamp + ".mp4");
//        }
        else {
            return null;
        }

        return mediaFile;
    }


    //    public void takePhoto() {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        String picturePath = BitmapUtils.getTempFolderPath() + "photo_temp.jpg";
//        _imageCaptureUri = Uri.fromFile(new File(picturePath));
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
//        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);//????????????????????????///
//
//    }

    public void sendMsg(final String toMail) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_MAKEMAIL;
        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Text response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseLoginResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
//                closeProgress();
                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

//                params.put("from_mail", Commons.thisEntity.get_email());
//                params.put("to_mail", Commons.userEntity.get_email().replace(" ","%20"));

                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", toMail);  Log.d("ToMail===>",toMail);
                params.put("text_message",
                        Commons.now_watching_message
//                        .toString()
//                        .replace(" ","%20")
//                        .replace(" & ","%26")
//                        .replace(",","-")
                );
                try {
                    if(Commons.thisEntity.get_userlat()!=0.0f && Commons.thisEntity.get_userlng()!=0.0f) {
                        params.put("lon_message", String.valueOf(Commons.thisEntity.get_userlat()));
                        params.put("lat_message", String.valueOf(Commons.thisEntity.get_userlng()));
                    }
                    else {
                        params.put("lon_message", "null");
                        params.put("lat_message", "null");
                    }
                }catch (NullPointerException e){}

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);


    }

    public void parseLoginResponse(String json) {

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);   Log.d("RRRRResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("RRResultcode=====> :",success);

            if (success==String.valueOf(0)) {
                _idx=response.getInt("mail_id");

                try {
                    if (Commons.imageUrl.length() > 0) {
                        file = new File(Commons.imageUrl);
                        uploadImage();
                    }
                    else if (!Commons.file.equals(null)){
                        file = Commons.file;
                        uploadImage();
                    }else {
                        makeMail(_idx);
                    }


                }catch (NullPointerException e){
                    makeMail(_idx);
                }


            }
            else{

                String error = response.getString(ReqConst.RES_ERROR);

                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
                //    Toast.makeText(getContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();;
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage() {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(4));

            String url = ReqConst.SERVER_URL + ReqConst.REQ_UPLOADPHOTO;
            //           showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(),"Registration to server failed.",Toast.LENGTH_SHORT).show();

                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse(json);
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
            //           closeProgress();
            Toast.makeText(getApplicationContext(),"Registration to server failed.",Toast.LENGTH_SHORT).show();
        }
    }


    public void ParseUploadImgResponse(String json) {

        //       closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("RESULT===",String.valueOf(result_code));

            if (result_code == 0) {
                Commons.imageUrl="";
                Commons.file=null;
                Commons.requestLatlng=null;
                Commons.now_watching_message="";

                makeMail(_idx);

            } else if(result_code==102){
                Toast.makeText(getApplicationContext(),"Unregistered user.",Toast.LENGTH_SHORT).show();
            }else if(result_code==103){
                Toast.makeText(getApplicationContext(),"Upload file size error.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Picture upload to server failed.",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Picture upload to server failed.",Toast.LENGTH_SHORT).show();
        }
    }

    public void makeMail(int mail_id) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_SENDMAIL;

        String params = String.format("/%s",mail_id);
        url += params;

        showToast("Your will end watching this video.");

        Intent in=new Intent(this,GameDetailActivity.class);
        startActivity(in);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                //           parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
//        Intent intent=new Intent(this,VideoDisplayActivity.class);
//        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
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
//
//                        showToast("Successfully connected.");
//                        //    setState(State.CONNECTED);
//                    }
//                });
//
//                if (FirebaseInstanceId.getInstance().getToken() == null) return;
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
//    private void searchChannels() {
//
//        channelListQuery = OpenChannel.createOpenChannelListQuery();
//        channelListQuery.setNameKeyword(Commons.gameEntity.getVideoId());
//
//        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
//            @Override
//            public void onResult(List<OpenChannel> channels, SendBirdException e) {
//                if (e != null) {
//                    // Error!
//                    showToast("No this video channel.");
//                    return;
//                }
//                // Returns a List of channels that have "NameKeyword" in their names.
//
//            }
//        });
//    }
}
