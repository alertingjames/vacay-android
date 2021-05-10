package com.mv.vacay.main;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.cunoraz.gifview.library.GifView;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.classes.Notification;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config_mapping.Mapping;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.main.activity.ActionsActivity;
import com.mv.vacay.main.beauty.BeautyWelcomeActivity;
import com.mv.vacay.main.carpediem.SelectCarpeDiemActivity;
import com.mv.vacay.main.inbox.InboxActivity;
import com.mv.vacay.main.info.CommonInfoViewActivity;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.main.meetfriends.MeetFriendActivity;
import com.mv.vacay.main.restaurant.FoodEntryActivity;
import com.mv.vacay.main.video.FileUtils;
import com.mv.vacay.main.watercooler.WatercoolerSetupActivity;
import com.mv.vacay.main.watercooler.WatercoolerViewActivity;
import com.mv.vacay.main.weather.MmainActivity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.nearby.Splash;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.INSTALL_PACKAGES,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.WRITE_CALENDAR,
            android.Manifest.permission.SET_TIME,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT,
            android.Manifest.permission.LOCATION_HARDWARE};

    public static final int REQUEST_CODE = 0;
    private static final int REQUEST_APP_SETTINGS = 168;
    ImageView logout, meet, beauty, food, action, myprofile;
    TextView msgNum,inboxButton,weatherButton,jobButton, announceButton, watercoolerButton, infoButton;
    private AdView mAdView,mAdView1;
    String device_id="";
    int i, messages=0;
    ArrayList<MessageEntity> _datas=new ArrayList<>();
    ArrayList<UserEntity> _datas_user=new ArrayList<>();
    private ProgressDialog _progressDlg;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Animation.AnimationListener mAnimationListener;
    ViewFlipper mHomeflipper;
    ImageLoader _imageloader;
    LinearLayout carpediem;
    FrameLayout frameLayoutMailbox;
    GifView gifView1,gifView2;
    String email="", sender="", name="", photo="", message="";
    private Vibrator _vibrator=null;
    boolean _newMessage=false;
    Bitmap bitmapPhoto=null;
    private DBManager dbManager;

    @SuppressWarnings("deprecation")
    private final GestureDetector _detector = new GestureDetector(new SwipeGestureDetector());

    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        TextView meettext=(TextView)findViewById(R.id.meettext);
        meettext.setTypeface(font);

        TextView beautyTitle=(TextView)findViewById(R.id.beautyTitle);
        beautyTitle.setTypeface(font);

        TextView eatTitle=(TextView)findViewById(R.id.eatTitle);
        eatTitle.setTypeface(font);

        TextView actTitle=(TextView)findViewById(R.id.actTitle);
        actTitle.setTypeface(font);

        checkAllPermission();

        Firebase.setAndroidContext(this);

        FileUtils.createApplicationFolder();

        dbManager = new DBManager(this);
        dbManager.open();

        if (Commons.userEntities.size()==0)
            ShortcutBadger.removeCount(getApplicationContext());

        Commons.mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Commons._vibrator=(Vibrator) getSystemService(this.VIBRATOR_SERVICE);

        Commons._isMyLocationVerified=false;

        _imageloader = VaCayApplication.getInstance().getImageLoader();
        mHomeflipper = (ViewFlipper) findViewById(R.id.home_viewflipper);

        final int[] resIds={R.drawable.sf0,
                R.drawable.sf1,R.drawable.sf2,R.drawable.sf3,R.drawable.sf4,R.drawable.sf5,
                R.drawable.sf6,R.drawable.sf7,R.drawable.sf8,R.drawable.sf9,R.drawable.sf10,
                R.drawable.sf11};

        gifView1 = (GifView)findViewById(R.id.gif1);
        msgNum=(TextView)findViewById(R.id.msg_number);
        gifView1.play();

        gifView2 = (GifView)findViewById(R.id.gif2);

//        gifView2.setVisibility(View.VISIBLE);
//        gifView2.play();

        frameLayoutMailbox=(FrameLayout)findViewById(R.id.mail_inbox);
        frameLayoutMailbox.setOnClickListener(this);

 //       frameLayoutMailbox.setVisibility(View.VISIBLE);

        for (i = 0; i <resIds.length; i++) {
            final NetworkImageView imageView = new NetworkImageView(this);
//            imageView.setImageUrl(Constants.promotionurls.get(i), _imageloader);

//            if(beautyEntities.get(i).get_beauty_imageURL().length()>0) {
//                Log.d("KKK====", beautyEntities.get(i).get_beauty_imageURL().toString());
//                imageView.setImageUrl(beautyEntities.get(i).get_beauty_imageURL(), _imageloader);
//            }
//            else
//            if(beautyEntities.get(i).get_beauty_resId()!=0)

                imageView.setDefaultImageResId(resIds[i]);
//            else
//                imageView.setImageBitmap(beautyEntities.get(i).get_beautyBitmap());


//            if(!beautyEntities.get(i).get_beautyBitmap().equals(null))imageView.setImageBitmap(beautyEntities.get(i).get_beautyBitmap());
//            else imageView.setImageResource(beautyEntities.get(i).get_beauty_resId());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setClickable(true);
            imageView.setId(1000 + i);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

//                    Commons.beautyEntity=beautyEntities.get(v.getId()-1000);

//                    Intent intent = new Intent(HairBeautyServiceActivity.this, BeautyDetailActivity.class);
//                    startActivity(intent);

                    Intent intent=new Intent(getApplicationContext(), SelectCarpeDiemActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();

                }
            });
            mHomeflipper.addView(imageView);
        }

        mHomeflipper.setAutoStart(true);
        mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
        mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
        mHomeflipper.setFlipInterval(1500);
        mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);

        mHomeflipper.startFlipping();


        mHomeflipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                _detector.onTouchEvent(motionEvent);
                return true;
            }
        });

        LoadAdviser();
        // animation listener
        mAnimationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("Flipper", "start");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("Flipper", "Repeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("Flipper", "end");

            }
        };

        meet=(ImageView)findViewById(R.id.meet_friend);
        meet.setOnClickListener(this);
        beauty=(ImageView)findViewById(R.id.beauty);
        beauty.setOnClickListener(this);
        food=(ImageView)findViewById(R.id.drink);
        food.setOnClickListener(this);
        action=(ImageView)findViewById(R.id.activity);
        action.setOnClickListener(this);
        myprofile=(ImageView)findViewById(R.id.myprofile);
        logout=(ImageView)findViewById(R.id.button_logout);
        logout.setOnClickListener(this);
        carpediem=(LinearLayout)findViewById(R.id.carpediem);
        carpediem.setOnClickListener(this);
        inboxButton=(TextView)findViewById(R.id.inbox);
        inboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayoutMailbox.setVisibility(View.GONE);
                Commons._inboxUserSearch=false;
                Commons._home_to_inbox=true;
                Intent intent=new Intent(getApplicationContext(), InboxActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
        //        finish();
            }
        });

        weatherButton=(TextView)findViewById(R.id.weather);
        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MmainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        jobButton=(TextView)findViewById(R.id.job);
        jobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._is_select_job=false;
                Intent intent=new Intent(getApplicationContext(), JobListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        announceButton=(TextView)findViewById(R.id.announcement);
        announceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), AnnouncementListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        watercoolerButton=(TextView)findViewById(R.id.watercooler);
        watercoolerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), WatercoolerViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        infoButton=(TextView)findViewById(R.id.info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), CommonInfoViewActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        TextView nearby=(TextView) findViewById(R.id.nearby);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Splash.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        int adminId = Preference.getInstance().getValue(this, PrefConst.PREFKEY_EMPLOYEEADMINID, 0);

        Log.d("ADminID===>",String.valueOf(adminId));

        Commons.thisEntity.set_adminId(adminId);

        if(Commons.thisEntity.get_adminId()>0){
            LinearLayout employeePortion=(LinearLayout)findViewById(R.id.employeePortion);
            employeePortion.setVisibility(View.VISIBLE);
            infoButton.setVisibility(View.GONE);
            beautyTitle=(TextView)findViewById(R.id.beautyTitle);
            beautyTitle.setText("EMPLOYEE APPRECIATION!");
        }

        Commons._golf_activity=false;
        Commons._run_activity=false;
        Commons._ski_activity=false;
        Commons._tennis_activity=false;

        Commons._fishing_activity=false;
        Commons._surfing_activity=false;
        Commons._exploring_activity=false;
        Commons._biking_activity=false;

        Commons.userEntity=null;

        Commons._mappingGetFlag=false;

        mTimer.schedule(doAsynchronousTask, 0, 30000);

        Intent intent = getIntent();
        email = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");
        _datas.clear();

        Log.d("EMAIL===>", email);

        getMyProfile(email);

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer1))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView1 = (AdView) findViewById(R.id.adView1);

//        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
//        device_id = md5(android_id).toUpperCase();

    //    Log.d("Device_ID===",device_id);

//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice(device_id)
//                .build();

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
                mAdView.setVisibility(View.VISIBLE);
                super.onAdOpened();
            }
        });

        mAdView1.setAdListener(new AdListener() {
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
        mAdView1.loadAd(adRequest);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Commons.userEntities.addAll(_datas_user);
//                pushNotification(Commons.thisEntity.get_email());
//            }
//        }, 1000);

        try{
            pushNotification(Commons.thisEntity.get_email());
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }

    }

    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

//                    if(Commons._mappingGetFlag)
                        new Notification(getApplicationContext(),Commons.thisEntity.get_email());
                }
            });
        }
    };

    private void LoadAdviser() {

    }

    public void logout() {

        Intent intent = new Intent(HomeActivity.this, RegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Preference.getInstance().put(this, PrefConst.PREFKEY_USEREMAIL, "");
        startActivity(intent);
        finish();
    }

    public void getMyProfile(final String email) {

        String url = ReqConst.SERVER_URL + "getUserProfile";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetBeautiesResponse(response);

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

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetBeautiesResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);  Log.d("Json===>",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code.equals("0")){

                JSONArray userInfo = response.getJSONArray(ReqConst.RES_USERPROFILE);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("providers===",userInfo.toString());

                JSONObject jsonUser = (JSONObject) userInfo.get(0);

                UserEntity user = new UserEntity();

                user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME));
                user.set_name(user.get_fullName());
                user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
//                    final Calendar c = Calendar.getInstance();
//                    int year = c.get(Calendar.YEAR);
//                    int birthyear=Integer.parseInt(user.get_age_range());
//                    int age=year-birthyear;
//                    user.set_age_range(String.valueOf(age));   Log.d("AGE===",user.get_age_range());
                user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                user.set_interest("-"+jsonUser.getString(ReqConst.RES_INTERESTS).replace("{","").replace("}","").replace("\",","\n-")
                        .replace("\"","").replace("[","").replace("]",""));
                user.setMillennial(jsonUser.getString("em_millennial"));
                user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                user.set_survey_quest(jsonUser.getString("survey"));

                user.set_relations(jsonUser.getString("relationship"));
                user.set_publicName(jsonUser.getString("place_name"));
                user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon")));
                user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat")));

                user.setCompany(Commons.thisEntity.getCompany());
                user.set_adminId(Commons.thisEntity.get_adminId());

                Commons.thisEntity=user;

                Log.d("ME===>", Commons.thisEntity.get_photoUrl()+"\n"+Commons.thisEntity.get_name()+"\n"+Commons.thisEntity.get_email());

                new Notification(this,Commons.thisEntity.get_email());

//                getAllUsersHashMap();

            }else if(result_code.equals("108")){
                showToast("Unknown user.");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.meet_friend:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }

                Commons.userEntities.addAll(_datas_user);

                Intent intent=new Intent(this,MeetFriendActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.beauty:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }
                intent=new Intent(this,BeautyWelcomeActivity.class);  //   VendorUploadActivity
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.drink:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }
                intent=new Intent(this,FoodEntryActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.activity:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }
                intent=new Intent(this,ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.button_logout:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }
                logout();
                break;
            case R.id.carpediem:
                if(_vibrator!=null){
                    _vibrator.cancel();
                    _vibrator=null;
                }
                intent=new Intent(this, SelectCarpeDiemActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                break;
        }
    }

    public void showMyProfile(MenuItem item){
        Intent intent=new Intent(this,MyProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void setting(MenuItem item){
        if(!Commons._appPermissions) {
            Commons._appPermissions=true;
        }
        showAlertDialogPermissions(" Welcome!\n Please grant all permissions.");
    }

    public void checkAllPermission() {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (hasPermissions(this, PERMISSIONS)){

        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 101);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(myAppSettings, REQUEST_APP_SETTINGS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasPermissions(@NonNull String... permissions) {
        for (String permission : permissions)
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(permission))
                return false;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(REQUEST_CODE == requestCode)
        {
            if(requestCode == RESULT_OK)
            {
                // done with activate to Device Admin
            }
            else
            {
                // cancle it.
            }
        }else if (requestCode == REQUEST_APP_SETTINGS) {
            if (hasPermissions(PERMISSIONS)) {
                Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        if (mAdView1 != null) {
            mAdView1.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
        if (mAdView1 != null) {
            mAdView1.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (mAdView1 != null) {
            mAdView1.destroy();
        }
        super.onDestroy();
    }

    public static String md5(String s) {
        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();


            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.left_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.left_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showNext();

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(HomeActivity.this, R.anim.right_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(HomeActivity.this,R.anim.right_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
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

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogPermissions(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setVisibility(View.GONE);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(msg);
        alertDialog.setView(dialogView);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                //        openPermissionSettings( HomeActivity.this);

                        if (Build.VERSION.SDK_INT > 22 && !hasPermissions(PERMISSIONS)) {
                            Toast.makeText(getApplicationContext(), "Please grant all permissions", Toast.LENGTH_LONG).show();
                            goToSettings();
                        }

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public static void openPermissionSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + activity.getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void getAllUsersHashMap() {

        String url = ReqConst.SERVER_URL + "getAllUsers";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETALL===>",response.toString());

                parseGetUsersResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showToast(getString(R.string.error));

            }
        }) {

        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetUsersResponse(String json) {

        closeProgress();

        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                Mapping.userMap.clear();

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    user.set_name(user.get_firstName()+" "+user.get_lastName());
//                    final Calendar c = Calendar.getInstance();
//                    int year = c.get(Calendar.YEAR);
//                    int birthyear=Integer.parseInt(jsonUser.getString(ReqConst.RES_AGE));
//                    int age=year-birthyear;
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("%26","&").replace("-",".").replace("&&","+").replace("00","\"")
                            .replace("%20"," ").replace("ppp","\n (").replace("qqq",")").replace("separate",",\n"));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    user.set_relations(jsonUser.getString("relationship"));
                    user.set_publicName(jsonUser.getString("place_name"));
                    user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon").replace("%20","-").replace("-",".")));
                    user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat").replace("%20","-").replace("-",".")));

                    // except me

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();

                    Mapping.userMap.put(user.get_email(),user);
        //            _datas_user.add(0,user);
                }
                Commons._mappingGetFlag=true;
    //            getMessage(email);

                new Notification(this,Commons.thisEntity.get_email());


            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    private class PushNotificationAsyncTask extends AsyncTask<Void, Integer, Void>{

        int progress_status;

        @Override
        protected void onPreExecute() {
            // update the UI immediately after the task is executed
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

    //        pushNotification(Commons.thisEntity.get_email().toString());
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }

    public void pushNotification(final String email) {

        final ArrayList<String> _emails=new ArrayList<>();

        _datas_user.clear();
        _emails.clear();
        Commons.userEntities.clear();

        final Firebase reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+ email.replace(".com","").replace(".","ddoott"));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("Count===>", String.valueOf(dataSnapshot.getChildrenCount()));

                final Firebase reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+ email.replace(".com","").replace(".","ddoott")+"/"+dataSnapshot.getKey());
                Log.d("Reference===>", reference1.toString());

                reference1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            message = map.get("msg").toString();
                            sender = map.get("sender").toString();
                            photo = map.get("senderPhoto").toString();
                            name = map.get("senderName").toString();

                            Commons.notiEmail = sender.replace("ddoott",".") + ".com";
                            Commons.firebase = reference;
                            Commons.mapping=map;

                            UserEntity user = new UserEntity();
                            user.set_name(name);
                            user.set_email(Commons.notiEmail);
                            user.set_photoUrl(photo);

                            if(user.get_name().length()>0){

                                if(!_emails.contains(user.get_email())){
                                    _emails.add(user.get_email());
                                    user.set_num("1");
                                    _datas_user.add(user);
                                    ShortcutBadger.applyCount(getApplicationContext(), _datas_user.size());
                                    shownot();
                                }
                            }

                            //        showToast("You received a message!");
                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if (_datas_user.size()==0)
            ShortcutBadger.removeCount(getApplicationContext());

        if(_datas_user.size()>0){
            ShortcutBadger.applyCount(getApplicationContext(), _datas_user.size());
        }
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    public void shownot() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        long[] v = {500,1000};

        Commons.userEntity=new UserEntity();
        Commons.userEntity.set_photoUrl(photo);
        Commons.userEntity.set_name(name);
        Commons.userEntity.set_email(Commons.notiEmail);    Log.d("NotiEmail===>",Commons.notiEmail);

        if(photo.length()<1000){
            try {
                bitmapPhoto=BitmapFactory.decodeStream((InputStream) new URL(photo).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);
            }
        }else {
            bitmapPhoto=base64ToBitmap(photo);
        }

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle(name)
                .setFullScreenIntent(pIntent,true)
                .setContentText(message)
                .setSmallIcon(R.drawable.noti).setLargeIcon(bitmapPhoto)
                .setContentIntent(pIntent)
                .setSound(uri)
                //      .setVibrate(v)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);
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

    android.widget.PopupMenu popupMenu;
    public void openMenuItems(View v) {
        View view = findViewById(R.id.myprofile);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.activity_main3);
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
}
