package com.mv.vacay.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.toolbox.ImageLoader;
import com.cunoraz.gifview.library.GifView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.mv.vacay.PlaceDetail.RoundImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.main.linkedin.LinkedinActivity;
import com.mv.vacay.models.FacebookUser;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.widgets.KenBurnsActivity;
import com.mv.vacay.widgets.KenBurnsView;
import com.mv.vacay.widgets.Transition;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import static com.mv.vacay.widgets.KenBurnsView.TransitionListener;

public class RegisterActivity extends KenBurnsActivity implements View.OnClickListener,TransitionListener, TextToSpeech.OnInitListener{

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
            android.Manifest.permission.CAPTURE_VIDEO_OUTPUT,
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.LOCATION_HARDWARE};

    public static final int REQUEST_CODE = 0;

    private static final int TRANSITIONS_TO_SWITCH = 3;

    private ViewSwitcher mViewSwitcher;

    private int mTransitionsCount = 0;

    KenBurnsView mImg;
    TextToSpeech textToSpeech;

    TextView ui_txvvendor, ui_txvsignup, ui_txvforgotpsd;
    LinearLayout ui_facebook, ui_twitter;
    private ProgressBar progressBar;
    private LoginButton loginButton;
    ImageLoader _imageLoader;
    public static CallbackManager callbackManager;

    private String FEmail, Name, Firstname, Lastname, Id, Gender = "", Image_url,Birthday, Education;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    boolean _isFromLogout = false;

    TextView beauty, retail, company;
    LinearLayout buttonPage;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Commons._is_vendorSelect=false;
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();


        long minRunningMemory = (1024 * 1024);

        Runtime runtime = Runtime.getRuntime();

        if (runtime.freeMemory() < minRunningMemory)
            System.gc();


        setContentView(R.layout.activity_register);

        checkAllPermission();

        initValue();

        textToSpeech = new TextToSpeech(RegisterActivity.this, RegisterActivity.this);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        try {

            PackageInfo info = getPackageManager().getPackageInfo("com.mv.vacay", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");

                md.update(signature.toByteArray());
                Log.i("KeyHash::", Base64.encodeToString(md.digest(), Base64.DEFAULT));//will give developer key hash
    //            Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show(); //will give app key hash or release key hash

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

//        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
//        img2.setTransitionListener(this);

        KenBurnsView img3 = (KenBurnsView) findViewById(R.id.img3);
        img3.setTransitionListener(this);


        GifView gifView1 = (GifView)findViewById(R.id.gif1);
        gifView1.setVisibility(View.VISIBLE);
        gifView1.play();

        ui_facebook = (LinearLayout) findViewById(R.id.lytfacebook);

        ui_facebook.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ui_facebook.setBackground(getDrawable(R.drawable.green_fillrect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        ui_facebook.setBackground(getDrawable(R.drawable.facebook_fillrect));
                        if(Commons._is_admin)Commons._is_admin=false;
                        if(Commons._is_provider)Commons._is_provider=false;
                        if(Commons._is_employeeSelect)Commons._is_employeeSelect=false;
                        if(Commons._is_companyManager)Commons._is_companyManager=false;
                        if(Commons._is_broadmoor)Commons._is_broadmoor=false;
                        loginWithFB();
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        ui_facebook.getBackground().clearColorFilter();
                        ui_facebook.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        ui_twitter = (LinearLayout) findViewById(R.id.lytlinkedin);

        ui_twitter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ui_twitter.setBackground(getDrawable(R.drawable.green_fillrect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        ui_twitter.setBackground(getDrawable(R.drawable.linkedin_fillrect));

                        if(Commons._is_admin)Commons._is_admin=false;
                        if(Commons._is_provider)Commons._is_provider=false;
                        if(Commons._is_employeeSelect)Commons._is_employeeSelect=false;
                        if(Commons._is_companyManager)Commons._is_companyManager=false;
                        if(Commons._is_broadmoor)Commons._is_broadmoor=false;

                        showLinkedinInfo("You need to login with Linkedin through Facebook");

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        ui_twitter.getBackground().clearColorFilter();
                        ui_twitter.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        loginButton= (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","publish_actions");
        loginButton.setOnClickListener(this);
        ui_txvvendor=(TextView)findViewById(R.id.txv_vendor);
        ui_txvvendor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ui_txvvendor.setTextColor(ColorStateList.valueOf(0xFFFF0281));

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        ui_txvvendor.setTextColor(ColorStateList.valueOf(0xffffffff));
                        Commons._is_admin=true;
//                        selectManager();
                        showButtons();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
            //            ui_txvvendor.getBackground().clearColorFilter();
                        ui_txvvendor.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        final TextView provider=(TextView)findViewById(R.id.toProviderPage);
        provider.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        provider.setTextColor(ColorStateList.valueOf(0xFFFF0281));

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        provider.setTextColor(ColorStateList.valueOf(0xffffffff));
                        Commons._is_vendorSelect=true;
                        Commons._is_admin=false;
                        if(Commons._is_employeeSelect)Commons._is_employeeSelect=false;
                        Intent intent=new Intent(getApplicationContext(), ServiceProviderSignupLoginActivity.class);// SurveyQuestActivity
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        //            ui_txvvendor.getBackground().clearColorFilter();
                        provider.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        final TextView employee=(TextView)findViewById(R.id.toEmployeePage);
        employee.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        employee.setTextColor(ColorStateList.valueOf(0xFFFF0281));

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        employee.setTextColor(ColorStateList.valueOf(0xffffffff));
                        Commons._is_vendorSelect=true;
                        Commons._is_employeeSelect=true;
                        Commons._is_admin=false;
                        Intent intent=new Intent(getApplicationContext(), ServiceProviderSignupLoginActivity.class);// SurveyQuestActivity
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        //            ui_txvvendor.getBackground().clearColorFilter();
                        employee.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        if (_isFromLogout) {

            //save user to empty.
            Preference.getInstance().put(this,
                    PrefConst.PREFKEY_USEREMAIL, "");
            Preference.getInstance().put(this,
                    PrefConst.PREFKEY_EMPLOYEEADMINID, "");
        }else {
            String email = Preference.getInstance().getValue(this, PrefConst.PREFKEY_USEREMAIL, "");
            String company = Preference.getInstance().getValue(this, PrefConst.PREFKEY_EMPLOYEECOMPANY, "");
            int adminId = Preference.getInstance().getValue(this, PrefConst.PREFKEY_EMPLOYEEADMINID, 0);

            Commons.thisEntity.set_adminId(adminId);
            Commons.thisEntity.setCompany(company);

            if (email.length() > 0) {
                Intent intent=new Intent(this,HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }

        }

        TextToSpeechFunction(((TextView)findViewById(R.id.explaination)).getText().toString());

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        ((TextView)findViewById(R.id.explaination)).setTypeface(font);

        ((FrameLayout)findViewById(R.id.frame)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissButtons();
                dismissShadow();
            }
        });

        buttonPage=(LinearLayout)findViewById(R.id.buttonPage);
        beauty=(TextView)findViewById(R.id.beauty);
        retail=(TextView)findViewById(R.id.retail);
        company=(TextView)findViewById(R.id.company);
        frame=(FrameLayout)findViewById(R.id.layout);

        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissButtons();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisibility(View.GONE);
                        buttonPage.setVisibility(View.GONE);

                        Commons._is_vendorSelect=true;
                        Commons._is_companyManager=false;
                        Commons._is_broadmoor=false;
                        Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                }, 500);
            }
        });

        retail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissButtons();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisibility(View.GONE);
                        buttonPage.setVisibility(View.GONE);

                        Commons._is_vendorSelect=true;
                        Commons._is_companyManager=false;
                        Commons._is_broadmoor=true;
                        Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                }, 500);
            }
        });

        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismissButtons();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.setVisibility(View.GONE);
                        buttonPage.setVisibility(View.GONE);

                        Commons._is_vendorSelect=true;
                        Commons._is_companyManager=true;
                        Commons._is_broadmoor=false;
                        Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                }, 500);
            }
        });

    }

    public void showShadow(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    public void dismissShadow(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.setVisibility(View.GONE);
                buttonPage.setVisibility(View.GONE);
            }
        }, 500);
    }

    private void showButtons() {
        buttonPage.setVisibility(View.VISIBLE);
        beauty.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        beauty.startAnimation(animation);
        retail.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromleft);
        retail.startAnimation(animation);
        company.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        company.startAnimation(animation);
        showShadow();
    }

    private void dismissButtons() {
//        buttonPage.setVisibility(View.GONE);
        beauty.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
        beauty.startAnimation(animation);
        retail.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
        retail.startAnimation(animation);
        company.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
        company.startAnimation(animation);
    }

    @Override
    protected void onPlayClick() {
        KenBurnsView currentImage = (KenBurnsView) mViewSwitcher.getCurrentView();
        currentImage.resume();
    }


    @Override
    protected void onPauseClick() {
        KenBurnsView currentImage = (KenBurnsView) mViewSwitcher.getCurrentView();
        currentImage.pause();
    }

    private void initValue(){

        Intent intent = getIntent();
        try {
            _isFromLogout = intent.getBooleanExtra(Constants.KEY_LOGOUT, false);
        } catch (Exception e) {
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lytfacebook:

                break;
            case R.id.login_button:

                loginButton.setReadPermissions("email","publish_actions");
                //    getProfileInfo();
                break;
            case R.id.lytlinkedin:

                break;
            case R.id.txv_vendor:

//                showVendorInfo("Welcome! We hope you will serve best for quests. " +
//                        "You will need to pay monthly fee. " +
//                        "If you aren't a real vendor, all service will be stopped."); // forward this can be corrected
                break;
        }

    }

    public void selectManager() {

        final String[] items = {"I am Beauty service", "I am retail store", "I am company"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What manager are you?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Commons._is_vendorSelect=true;
                    Commons._is_companyManager=false;
                    Commons._is_broadmoor=false;
                    Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                else if(item==1){
                    Commons._is_vendorSelect=true;
                    Commons._is_companyManager=false;
                    Commons._is_broadmoor=true;
                    Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                else {
                    Commons._is_vendorSelect=true;
                    Commons._is_companyManager=true;
                    Commons._is_broadmoor=false;
                    Intent intent=new Intent(getApplicationContext(), LoginRActivity.class);// SurveyQuestActivity
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

    //====================================Facebook Login Start======================================
    private void loginWithFB() {

        callbackManager = CallbackManager.Factory.create();

        // set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));    // "public_profile" , "email", "user_photos", "user_gender", "user_birthday"

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();

                // Facebook Email address
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                Log.v("LoginActivity Response ", response.toString());
//                                try{
//                                    Birthday=object.getString("birthday");
//                                    Log.d("Birthday: ",Birthday);
//                                }catch (JSONException e){
//                                    e.printStackTrace();
//                                }

                                try {
                                    if (android.os.Build.VERSION.SDK_INT > 9) {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                        String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                        URL fb_url = new URL(profilePicUrl);//small | noraml | large
                                        HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
                                        HttpsURLConnection.setFollowRedirects(true);
                                        conn1.setInstanceFollowRedirects(true);
                                        Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());
                                        Commons.thisEntity.set_bitmap(fb_img);
                                    }
                                }catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                                try {
                                    Name = object.getString("name");
                                    Name.replace(" ", "");
                                    Id = object.getString("id");
                                    Firstname = object.getString("first_name");
                                    Lastname = object.getString("last_name");
//                                    Gender = object.getString("gender");
                                    FEmail = object.getString("email");
                                    Image_url = "http://graph.facebook.com/(Id)/picture?type=large";
                                    Image_url = URLEncoder.encode(Image_url);
                                    Log.d("Email = ", " " + FEmail);
                                    Log.d("Name======", Name);
                                    Log.d("Image====",Image_url.toString());
                                    Log.d("firstName======", Firstname);
                                    Log.d("lastName======", Lastname);
//                                    Log.d("Gender======", Gender);
                                    Log.d("id======", Id);
                                    Log.d("Object=====>", object.toString());
                                    Log.d("photourl======", Image_url.toString());

                                    if (object.has("picture")) {
                                        JSONObject jsonPicture = object.getJSONObject("picture");
                                        if (jsonPicture.has("data")) {
                                            JSONObject jsonData = jsonPicture.getJSONObject("data");
                                            if (jsonData.has("url"))
                                                Commons.thisEntity.set_photoUrl(jsonData.getString("url"));
                                        }
                                    }

                                    // SocialLogin(Firstname, Lastname,Gender,FEmail,Image_url,Id,"facebook");
                                    showInfo("first name: "+Firstname+"\n"+"Last name: "+Lastname+"\n"+"Email: "+FEmail+"\n");
                                    //    showInfo(object.toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();

            }


            @Override
            public void onCancel() {
                LoginManager.getInstance().logOut();

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    public void gotoSurveyQuestActivity() {
        Intent intent = new Intent(getApplicationContext(), SurveyQuestActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        showToast("Please wait...");
    }
    //==================================Face book Login End====================================

    private  void showInfo(String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Please login with Linkedin too.");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        textview.setTypeface(font);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Commons.thisEntity.set_firstName(Firstname);
                Commons.thisEntity.set_lastName(Lastname.substring(0,1)+".");
//                   Commons.thisEntity.set_age_range("25-30");
//                                    Commons.thisEntity.set_city("San Francisco");
//                                    Commons.thisEntity.set_job("Ecommerce Manager");
//                                    Commons.thisEntity.set_education("Yale");
//                                    Commons.thisEntity.set_interest("Hockey, Tennis, Reading");
                //                    Commons.thisEntity.set_survey_quest(Commons.survey_quest);
                //        Commons.thisEntity.set_photoUrl(Image_url);
                Commons.thisEntity.set_email(FEmail);
                Commons.thisuserGender=Gender;

                Commons.thisEntity.set_adminId(0);

                Intent intent=new Intent(getApplicationContext(),LinkedinActivity.class);                                          //SurveyQuestActivity //LinkedinActivity
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void getProfileInfo(){

        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("email");
        permissions.add("public_profile");
        permissions.add("user_birthday");
        loginButton.setReadPermissions(permissions);

        callbackManager = new CallbackManager.Factory().create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final FacebookUser fbUser = new FacebookUser();
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("response", object.toString());
                        try {

                            fbUser.setId(object.getString("id"));
                            fbUser.setFullName(object.getString("name"));
                            fbUser.setFirstName(object.getString("first_name"));
                            fbUser.setLastName(object.getString("last_name"));
                            if (object.has("gender"))
                                fbUser.setGender(object.getString("gender"));
                            if (object.has("birthday"))
                                fbUser.setBirthday(object.getString("birthday"));
                            if (object.has("picture")) {
                                JSONObject jsonPicture = object.getJSONObject("picture");
                                if (jsonPicture.has("data")) {
                                    JSONObject jsonData = jsonPicture.getJSONObject("data");
                                    if (jsonData.has("url"))
                                        fbUser.setAvatar(jsonData.getString("url"));
                                }
                            }
                            fbUser.setEmail(object.getString("email"));

                            //            signUpwithFacebookUser(fbUser);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle params = new Bundle();
                params.putString("fields", "id,name,email,gender,birthday,locale,cover,picture,first_name,last_name");
                request.setParameters(params);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

    }
    private  void showLinkedinInfo(String infomation) {

        TextToSpeechFunction(infomation);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                TextToSpeechFunction(((TextView)findViewById(R.id.explaination)).getText().toString());
            }
        }, 3000);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Likedin Login");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setVisibility(View.GONE);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                loginWithFB();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();

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

    public Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

//        paint.setXfermode(new PorterDuffXfermode(Mode.raw_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {

        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }

    public void TextToSpeechFunction(String msg)
    {

        String textholder = msg;

        textToSpeech.speak(textholder, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onDestroy() {

        textToSpeech.shutdown();

        super.onDestroy();
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {

            textToSpeech.setLanguage(Locale.US);
            TextToSpeechFunction(((TextView)findViewById(R.id.explaination)).getText().toString());
        }
    }
}



















































































