package com.mv.vacay.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.utils.MultiPartRequest;
//import com.sendbird.android.SendBird;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class  SignupActivity extends BaseActivity implements View.OnClickListener{
    TextView age,ok,firstname,lastname,answer_survey,gender,name,email,checkin;
    EditText education,mailAddr,job;
    TextView city, relationship, millennial;
    CircularNetworkImageView ui_userphoto_net;
    CircularImageView ui_userphoto;
    LinearLayout spin,spinrelationship, spinmillennial, spin1,spinSub,spin1Sub;
    ImageView back, dropMark;
    ScrollView scrollView;
    private ImageLoader _imageLoader;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String TAG = SignupActivity.class.getSimpleName();
    String firstinter="",otherinter="",firstSubinter="",otherSubinter="",relations="", millennialStr="";
    private int _idx = 0, subitem=4,subitemSub=0;
    String _photoPath = "",interests="",interestsStr="";
    private Uri _imageCaptureUri;
    private int i=0;
    ArrayList<String> arrayList=new ArrayList<>();
    String[] items=new String[4];

    LinearLayout interestGroup, golfGroup, runGroup, skiGroup, tennisGroup, bikingGroup, fishingGroup, surfingGroup, exploringGroup;
    CheckBox golf, run, ski, tennis, biking, fishing, surfing, exploring;
    CheckBox golfBeginner, golfIntermediate, golfAdvanced, golfScramble,
            runWalk, run30mins, run1mile, run5miles, run10miles,
            skiHot, skiBunny, skiGreens, skiBlues, skiBlacks, skiDouble, skiAfraid,
            tennis3, tennis4, tennis5, tennis6, tennisWTF,
            bikingShort, bikingLong, bikingMountain, bikingRoad,
            fishingFly, fishingLake, fishingOcean,
            surfingOcean, surfingLake, surfingKitesurfing,
            exploringMuseums, exploringCity, exploringNature, exploringArt, exploringConcerts, exploringLectures;

    boolean interestGroupVisible=false, golfGroupVisible=false, runGroupVisible=false, skiGroupVisible=false, tennisGroupVisible=false,
            bikingGroupVisible=false, fishingGroupVisible=false, surfingGroupVisible=false, exploringGroupVisible=false;

    final String[][] Items = {{"Run","Ski & Snowboard","Tennis"},
            {"Golf","Ski & Snowboard","Tennis"},
            {"Golf","Run","Tennis"},
            {"Golf","Run","Ski & Snowboard"},
            {"Golf","Run","Ski & Snowboard","Tennis"}
    };

    String[][] interestSubs={{"Beginner","Intermediate","Advanced","Scramble Secret Weapon"},
            {"\"I walk because it burns fat\"runs","30 minutes","1 mile","5 miles","10 miles"},
            {"Hot Chocolate & Lodge\"skier\"","Bunny Slopes","Greens","Blues","Blacks","Double Black Diamonds","I'm not afraid of death"},
            {"3.0+","4.0+","5.0+","6.0+","WTF does that mean"}};

    final String[] items0 = {"Golf",
            "Run",
            "Ski & Snowboard",
            "Tennis"
    };

    /**
     * To test push notifications with your own appId, you should replace google-services.json with yours.
     * Also you need to set Server API Token and Sender ID in SendBird dashboard.
     * Please carefully read "Push notifications" section in SendBird Android documentation
     */

//    private static final String appId = "A7A2672C-AD11-11E4-8DAA-0A18B21C2D82"; /* Sample SendBird Application */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

//        sUserId = getPreferences(Context.MODE_PRIVATE).getString("user_id", "");
//        mNickname = getPreferences(Context.MODE_PRIVATE).getString("nickname", "");

//        SendBird.init(Config.appId, this);
        
        arrayList.clear();
        for (int i=0;i<items0.length;i++) arrayList.add(items0[i]);

        ok=(TextView)findViewById(R.id.ok);
        ok.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ok.setBackground(getDrawable(R.drawable.green_strokerect));
                        ok.setTextColor(ColorStateList.valueOf(Color.BLUE));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        ok.setBackground(getDrawable(R.drawable.green_fillround));
                        ok.setTextColor(ColorStateList.valueOf(Color.WHITE));

                        try {
                            interests=createInterestArrayList();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(job.getText().length()>0 && age.getText().length()>0 && education.getText().length()>0&& interests.length()>0&& relations.length()>0) {
//                            final Calendar c = Calendar.getInstance();
//                            int year = c.get(Calendar.YEAR);
//                            int ageint = Integer.parseInt(age.getText().toString());
//                            int birthyear = year - ageint;

                            Commons.thisEntity.set_age_range(age.getText().toString());
                            Commons.thisEntity.set_city(city.getText().toString());
                            Commons.thisEntity.set_education(education.getText().toString());
//                            if (otherinter!= "")
//                                Commons.thisEntity.set_interest(firstinter + "-" + otherinter);
//                            else Commons.thisEntity.set_interest(firstinter);

                            Commons.thisEntity.set_interest(interests);

                            Log.d("Interests===>",Commons.thisEntity.get_interest());
                            interests="";
                            interestsStr="";
                            subitem=4;

                            Commons.thisEntity.set_relations(relations);

                            Commons.thisEntity.set_publicName(" ");
                            Commons.thisEntity.set_userlat(0.0f);
                            Commons.thisEntity.set_userlng(0.0f);

                            if(Commons.thisEntity.get_job().length()==0) {
                                Commons.thisEntity.set_job(job.getText().toString());
                            }

                            register();

                            //                connect();
                        }
                        else showToast("Please check blank fields.");

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        ok.getBackground().clearColorFilter();
                        ok.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        ui_userphoto_net=(CircularNetworkImageView)findViewById(R.id.imv_photo_net);
        ui_userphoto=(CircularImageView)findViewById(R.id.imv_photo);

        ui_userphoto.setOnClickListener(this);
     //   ui_userphoto.setImageURI(Uri.parse(Commons.thisEntity.get_photoUrl().toString()));
        //    ui_userphoto.setImageBitmap(Commons.thisEntity.get_bitmap());
        if(Commons.thisEntity.get_photoUrl().length()<1000) {
            ui_userphoto_net.setVisibility(View.VISIBLE);
            ui_userphoto_net.setImageUrl(Commons.thisEntity.get_photoUrl(), _imageLoader);
        }else {
            ui_userphoto_net.setVisibility(View.GONE);
            ui_userphoto.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        Log.d("ImageUrl===>",Commons.thisEntity.get_photoUrl());
        _photoPath=Commons.thisEntity.get_photoUrl();

        firstname=(TextView)findViewById(R.id.txv_firstname);
        firstname.setText(Commons.thisEntity.get_firstName());
        lastname=(TextView)findViewById(R.id.txv_lastname);
        lastname.setText(Commons.thisEntity.get_lastName());

        answer_survey=(TextView)findViewById(R.id.answer_survey);
        answer_survey.setText(Commons.thisEntity.get_survey_quest());
        answer_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SurveyAnswerViewActivity.class);
                startActivity(intent);
            }
        });
        gender=(TextView)findViewById(R.id.gender);
        gender.setText(Commons.thisuserGender);
        checkin=(TextView)findViewById(R.id.checkin);
        checkin.setOnClickListener(this);
        name=(TextView)findViewById(R.id.name);
        name.setText(Commons.thisEntity.get_firstName()+" "+Commons.thisEntity.get_lastName());
        email=(TextView)findViewById(R.id.email);
        email.setText(Commons.thisEntity.get_email());
        job=(EditText) findViewById(R.id.job);
        job.setText(Commons.thisEntity.get_job());

        age=(TextView) findViewById(R.id.edt_age);
        age.setOnClickListener(this);
        city=(TextView) findViewById(R.id.edt_city);
        if(Commons.thisEntity.get_city().length()>0){
            city.setText(Commons.thisEntity.get_city());
        }
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Commons.thisEntity.get_job().length()==0) {
                    Commons.thisEntity.set_job(job.getText().toString());
                }

                Intent intent=new Intent(getApplicationContext(), MyCurrentLocationViewActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        final ImageView searchLoc=(ImageView)findViewById(R.id.searchLoc);
        searchLoc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        searchLoc.setBackgroundColor(Color.RED);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        searchLoc.setBackgroundColor(Color.WHITE);

                        if(Commons.thisEntity.get_job().length()==0) {
                            Commons.thisEntity.set_job(job.getText().toString());
                        }

                        Intent intent=new Intent(getApplicationContext(), MyCurrentLocationViewActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        searchLoc.getBackground().clearColorFilter();
                        searchLoc.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
        education=(EditText)findViewById(R.id.edt_education);
        spin=(LinearLayout)findViewById(R.id.lyt_spin);
        spin.setOnClickListener(this);
        spin1=(LinearLayout)findViewById(R.id.lyt_spin1);
        spin1.setOnClickListener(this);
        spinSub=(LinearLayout)findViewById(R.id.lyt_spin_sub);
        spinSub.setOnClickListener(this);
        spin1Sub=(LinearLayout)findViewById(R.id.lyt_spin1_sub);
        spin1Sub.setOnClickListener(this);
        spinrelationship=(LinearLayout)findViewById(R.id.lyt_spin_relationship);
        spinrelationship.setOnClickListener(this);

        relationship=(TextView) findViewById(R.id.relationship);

        spinmillennial=(LinearLayout)findViewById(R.id.lyt_spin_millennial);

        millennial=(TextView) findViewById(R.id.millennial);
        millennial.setText(Commons.thisEntity.getMillennial());

        if(Commons.thisEntity.get_adminId()>0){
            relationship.setText("Department");
            spinmillennial.setVisibility(View.VISIBLE);
        }else {
            relationship.setText("Info");
            spinmillennial.setVisibility(View.GONE);
        }

        mailAddr=(EditText)findViewById(R.id.edt_mailing);
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SurveyQuestActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        scrollView=(ScrollView)findViewById(R.id.scrollView);

        golf=(CheckBox)findViewById(R.id.chk_golf);
        golf.setOnClickListener(this);
        run=(CheckBox)findViewById(R.id.chk_run);
        run.setOnClickListener(this);
        ski=(CheckBox)findViewById(R.id.chk_ski);
        ski.setOnClickListener(this);
        tennis=(CheckBox)findViewById(R.id.chk_tennis);
        tennis.setOnClickListener(this);
        biking=(CheckBox)findViewById(R.id.chk_biking);
        biking.setOnClickListener(this);
        fishing=(CheckBox)findViewById(R.id.chk_fishing);
        fishing.setOnClickListener(this);
        surfing=(CheckBox)findViewById(R.id.chk_surfing);
        surfing.setOnClickListener(this);
        exploring=(CheckBox)findViewById(R.id.chk_exploring);
        exploring.setOnClickListener(this);

        golfBeginner=(CheckBox)findViewById(R.id.golf_chk_beginner);
        golfIntermediate=(CheckBox)findViewById(R.id.golf_chk_intermediate);
        golfAdvanced=(CheckBox)findViewById(R.id.golf_chk_advanced);
        golfScramble=(CheckBox)findViewById(R.id.golf_chk_scramble);

        runWalk=(CheckBox)findViewById(R.id.run_chk_walk);
        run30mins=(CheckBox)findViewById(R.id.run_chk_30mins);
        run1mile=(CheckBox)findViewById(R.id.run_chk_1mile);
        run5miles=(CheckBox)findViewById(R.id.run_chk_5miles);
        run10miles=(CheckBox)findViewById(R.id.run_chk_10miles);

        skiHot=(CheckBox)findViewById(R.id.ski_chk_hot);
        skiBunny=(CheckBox)findViewById(R.id.ski_chk_bunny);
        skiGreens=(CheckBox)findViewById(R.id.ski_chk_greens);
        skiBlues=(CheckBox)findViewById(R.id.ski_chk_blues);
        skiBlacks=(CheckBox)findViewById(R.id.ski_chk_blacks);
        skiDouble=(CheckBox)findViewById(R.id.ski_chk_double);
        skiAfraid=(CheckBox)findViewById(R.id.ski_chk_afraid);

        tennis3=(CheckBox)findViewById(R.id.tennis_chk_3);
        tennis4=(CheckBox)findViewById(R.id.tennis_chk_4);
        tennis5=(CheckBox)findViewById(R.id.tennis_chk_5);
        tennis6=(CheckBox)findViewById(R.id.tennis_chk_6);
        tennisWTF=(CheckBox)findViewById(R.id.tennis_chk_wtf);

        bikingShort=(CheckBox)findViewById(R.id.biking_chk_short);
        bikingLong=(CheckBox)findViewById(R.id.biking_chk_long);
        bikingMountain=(CheckBox)findViewById(R.id.biking_chk_mountain);
        bikingRoad=(CheckBox)findViewById(R.id.biking_chk_road);

        fishingFly=(CheckBox)findViewById(R.id.fishing_chk_fly);
        fishingLake=(CheckBox)findViewById(R.id.fishing_chk_lake);
        fishingOcean=(CheckBox)findViewById(R.id.fishing_chk_ocean);

        surfingOcean=(CheckBox)findViewById(R.id.surfing_chk_ocean);
        surfingLake=(CheckBox)findViewById(R.id.surfing_chk_lake);
        surfingKitesurfing=(CheckBox)findViewById(R.id.surfing_chk_kitesurfing);

        exploringMuseums=(CheckBox)findViewById(R.id.exploring_chk_museums);
        exploringCity=(CheckBox)findViewById(R.id.exploring_chk_city);
        exploringNature=(CheckBox)findViewById(R.id.exploring_chk_nature);
        exploringArt=(CheckBox)findViewById(R.id.exploring_chk_art);
        exploringConcerts=(CheckBox)findViewById(R.id.exploring_chk_concerts);
        exploringLectures=(CheckBox)findViewById(R.id.exploring_chk_lectures);

        interestGroup=(LinearLayout)findViewById(R.id.interest_group);
        golfGroup=(LinearLayout)findViewById(R.id.golf_chk_group);
        runGroup=(LinearLayout)findViewById(R.id.run_chk_group);
        skiGroup=(LinearLayout)findViewById(R.id.ski_chk_group);
        tennisGroup=(LinearLayout)findViewById(R.id.tennis_chk_group);
        bikingGroup=(LinearLayout)findViewById(R.id.biking_chk_group);
        fishingGroup=(LinearLayout)findViewById(R.id.fishing_chk_group);
        surfingGroup=(LinearLayout)findViewById(R.id.surfing_chk_group);
        exploringGroup=(LinearLayout)findViewById(R.id.exploring_chk_group);

        dropMark=(ImageView)findViewById(R.id.dropmark);
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SendBird.disconnect(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityResumedForOldAndroids();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityPausedForOldAndroids();
    }

    private String createInterestData(){
        String jsonString = "";

        try {

            JSONObject interObj = new JSONObject(); // Main Json

            interObj.put("first",firstinter);
            interObj.put("firstSub",firstSubinter);
            interObj.put("other",otherinter);
            interObj.put("otherSub",otherSubinter);

            jsonString = interObj.toString();


        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }


//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//
//            case R.id.lyt_spin:
//                arrayList.clear();
//                for (int i=0;i<items0.length;i++) arrayList.add(items0[i]);
//                i=0;
//                showChoiceDialog();
//                break;
//            case R.id.lyt_spin1:
//                showChoiceDialogOtherInterests();
//                break;
//            case R.id.lyt_spin_sub:
//    //            showChoiceDialogSub();
//                break;
//            case R.id.lyt_spin1_sub:
//                showChoiceDialogOtherInterestsSub();
//                break;
//            case R.id.lyt_spin_relationship:
//                showChoiceDialog_relationship();
//                break;
//            case R.id.checkin:
//                Intent intent=new Intent(this,CheckinActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0,0);
//                break;
//            case R.id.imv_photo:
//            //    selectPhoto();
//                break;
//            case R.id.edt_age:
//                showChoiceDialog_ageRange();
//                break;
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.lyt_spin:
                if(!interestGroupVisible) {
                    interestGroup.setVisibility(View.VISIBLE);
                    interestGroupVisible=true;
                    dropMark.setImageResource(R.drawable.ic_drop2);
                    scrollDown();
                }else {
                    interestGroup.setVisibility(View.GONE);
                    interestGroupVisible=false;
                    dropMark.setImageResource(R.drawable.ic_drop1);
                }
                break;

            case R.id.chk_golf:
                if(!golfGroupVisible) {
                    golfGroup.setVisibility(View.VISIBLE);
                    golfGroupVisible=true;
                    scrollDown();
                }else {
                    golfGroup.setVisibility(View.GONE);
                    golfGroupVisible=false;
                }
                break;

            case R.id.chk_run:
                if(!runGroupVisible) {
                    runGroup.setVisibility(View.VISIBLE);
                    runGroupVisible=true;
                    scrollDown();
                }else {
                    runGroup.setVisibility(View.GONE);
                    runGroupVisible=false;
                }
                break;

            case R.id.chk_ski:
                if(!skiGroupVisible) {
                    skiGroup.setVisibility(View.VISIBLE);
                    skiGroupVisible=true;
                    scrollDown();
                }else {
                    skiGroup.setVisibility(View.GONE);
                    skiGroupVisible=false;
                }
                break;

            case R.id.chk_tennis:
                if(!tennisGroupVisible) {
                    tennisGroup.setVisibility(View.VISIBLE);
                    tennisGroupVisible=true;
                    scrollDown();
                }else {
                    tennisGroup.setVisibility(View.GONE);
                    tennisGroupVisible=false;
                }
                break;

            case R.id.chk_biking:
                if(!bikingGroupVisible) {
                    bikingGroup.setVisibility(View.VISIBLE);
                    bikingGroupVisible=true;
                    scrollDown();
                }else {
                    bikingGroup.setVisibility(View.GONE);
                    bikingGroupVisible=false;
                }
                break;

            case R.id.chk_fishing:
                if(!fishingGroupVisible) {
                    fishingGroup.setVisibility(View.VISIBLE);
                    fishingGroupVisible=true;
                    scrollDown();
                }else {
                    fishingGroup.setVisibility(View.GONE);
                    fishingGroupVisible=false;
                }
                break;

            case R.id.chk_surfing:
                if(!surfingGroupVisible) {
                    surfingGroup.setVisibility(View.VISIBLE);
                    surfingGroupVisible=true;
                    scrollDown();
                }else {
                    surfingGroup.setVisibility(View.GONE);
                    surfingGroupVisible=false;
                }
                break;

            case R.id.chk_exploring:
                if(!exploringGroupVisible) {
                    exploringGroup.setVisibility(View.VISIBLE);
                    exploringGroupVisible=true;
                    scrollDown();
                }else {
                    exploringGroup.setVisibility(View.GONE);
                    exploringGroupVisible=false;
                }
                break;

            case R.id.lyt_spin1:
                showChoiceDialogOtherInterests();
                break;
            case R.id.lyt_spin_sub:
                //            showChoiceDialogSub();
                break;
            case R.id.lyt_spin1_sub:
                showChoiceDialogOtherInterestsSub();
                break;
            case R.id.lyt_spin_relationship:
                if(Commons.thisEntity.get_adminId()>0)
                    showChoiceDialog_relationship();
                else
                    showInfoDialog();
                break;
            case R.id.checkin:
                Intent intent=new Intent(this,CheckinActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.imv_photo:
                //    selectPhoto();
                break;
            case R.id.edt_age:
                showChoiceDialog_ageRange();
                break;
        }
    }

    private void scrollDown() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {

                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void showChoiceDialog() {

        if(items.length>0) {
            for (int k = 0; k < items.length; k++) items[k] = "";
        }
        for (int i=0;i<arrayList.size();i++){
            items[i]=arrayList.get(i);
        }

        final TextView firstinterest=(TextView) findViewById(R.id.edt_firstinterest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your interest.");
        builder.setPositiveButton("End", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                interestsStr=interests;
                firstinterest.setText(interestsStr.replace("ppp","(").replace("qqq",")").replace("separate",",\n")
                        .replace("Golf","# Golf").replace("Tennis","# Tennis").replace("Run","# Run").replace("Ski & Snowboard","# Ski & Snowboard"));

                Log.d("Interestsfinally===>",interests);
            }

        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                interestsStr=interests="";
                i=0;
                subitem=4;
                arrayList.clear();
                for (int i=0;i<items0.length;i++) arrayList.add(items0[i]);
            }

        });

        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
        //        firstinterest.setText(items[item]);
                for(int i=0;i<items0.length;i++){
                    if(items[item].equals(items0[i])){
                        subitemSub=i;
                    }
                }
                firstinter=items[item];
                if(firstinter.contains("->")){
                    firstinter="";
                }
                else {
                    subitem=item; i++;
                    arrayList.remove(item);
                    arrayList.add(item,firstinter+"  ->   "+"Selected");
                    if(i==1) interests+=firstinter;
                    else interests+="separate"+firstinter;
                    if(firstinter.length()>0){
                        firstinter="";
                        showChoiceDialogSub(subitemSub);
                    }
                    Log.d("Interests1===>",interests);
                }

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialogSub(int subitem) {

        final String[] items = interestSubs[subitem];

        final TextView firstSubinterest=(TextView) findViewById(R.id.edt_firstsubinterest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select interest subitem.");
//        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
//        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
    //            firstSubinterest.setText(items[item]);
                firstSubinter=items[item];
                interests+="ppp"+firstSubinter+"qqq";
                firstSubinter="";
                Log.d("Interests2===>",interests);
                showChoiceDialog();

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialogOtherInterestsSub() {

        final String[] items = interestSubs[subitem];

        final TextView otherSubinterest=(TextView) findViewById(R.id.edt_othersubinterest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your first interest.");
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
                otherSubinterest.setText(items[item]);
                otherSubinter=items[item];
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialogOtherInterests() {

        final String[] items = {"Golf",
                "Run",
                "Ski & Snowboard",
                "Tennis"
        };

        final TextView otherinterests=(TextView) findViewById(R.id.edt_otherinterest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your first interest.");
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
                otherinterests.setText(items[item]);
                otherinter=items[item];
                subitem=item;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void showChoiceDialog_relationship() {

        final String[] items = {"Sales/Marketing",
                "Legal","Finance","IT","Human Resources","Business Services","Product Development"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
//        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                relationship.setText(items[item]);
                relations=relationship.getText().toString();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_millennial() {

        final String[] items = {"Millennial",
                "Gen Xer","Baby Boomer","Gen Y","Other"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
//        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                millennial.setText(items[item]);
                millennialStr=millennial.getText().toString();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_ageRange() {

        final String[] items = {"21-25",
                "26-30","31-35","36-40","41-45","46-50","50+"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select \nAge Range");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                age.setText(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = 700;
//        lp.height = 700;
//        lp.x=80;
//        lp.y=-1200;
        alert.getWindow().setAttributes(lp);

    }

    private  void showInfoDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Info...");
        builder.setIcon(R.drawable.noti);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog_info, null);

        final CheckBox single = (CheckBox) dialogView.findViewById(R.id.single);
        final CheckBox relationship = (CheckBox) dialogView.findViewById(R.id.relationship);
        final CheckBox kids5 = (CheckBox) dialogView.findViewById(R.id.kids5);
        final CheckBox kids6 = (CheckBox) dialogView.findViewById(R.id.kids6);
        final CheckBox dogs = (CheckBox) dialogView.findViewById(R.id.dogs);

        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                relations="";
                if(single.isChecked())relations=relations+single.getText().toString()+"\n";
                if(relationship.isChecked())relations=relations+relationship.getText().toString()+"\n";
                if(kids5.isChecked())relations=relations+kids5.getText().toString()+"\n";
                if(kids6.isChecked())relations=relations+kids6.getText().toString()+"\n";
                if(dogs.isChecked())relations=relations+dogs.getText().toString()+"\n";
                relations=relations+"common";
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void register() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_REGISTERUSER;

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

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

                params.put("email", Commons.thisEntity.get_email());
                params.put("first_name", Commons.thisEntity.get_firstName());
                params.put("last_name", Commons.thisEntity.get_lastName());
                params.put("age", Commons.thisEntity.get_age_range());
                params.put("address", Commons.thisEntity.get_city());
                params.put("job", Commons.thisEntity.get_job());
                params.put("education", Commons.thisEntity.get_education());
                params.put("interests", Commons.thisEntity.get_interest());
                params.put("relationship", Commons.thisEntity.get_relations());
                if(Commons.thisEntity.get_adminId()>0) {
                    params.put("place_name", Commons.thisEntity.getCompany());
                    params.put("em_millennial", Commons.thisEntity.getMillennial());
                }
                else {
                    params.put("place_name", " ");
                    params.put("em_millennial", "");
                }
                params.put("user_lat", "0.0");
                params.put("user_lon", "0.0");
                params.put("photo_url", _photoPath);
                params.put("survey", Commons.survey_quest);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                registerChatRoom();
                Toast.makeText(getApplicationContext(),"Successfully registered",Toast.LENGTH_SHORT).show();

            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                registerChatRoom();
                Toast.makeText(getApplicationContext(),"Successfully logged in & updated", Toast.LENGTH_SHORT).show();
            }
            else {
                closeProgress();
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            closeProgress();
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public void onSuccessRegister() {

        Preference.getInstance().put(this,
                PrefConst.PREFKEY_USEREMAIL, Commons.thisEntity.get_email());
        Preference.getInstance().put(this,
                PrefConst.PREFKEY_EMPLOYEEADMINID, Commons.thisEntity.get_adminId());
        if(Commons.thisEntity.get_adminId()==0)
            Preference.getInstance().put(this,
                PrefConst.PREFKEY_EMPLOYEEID, 0);

        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();overridePendingTransition(0,0);
    }

    public void registerChatRoom(){

        String url = ReqConst.FIREBASE_DATABASE_URL+"users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"users/"+Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott"));

                if(s.equals("null")) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("email", Commons.thisEntity.get_email());

                    if(Commons.thisEntity.get_fullName().length()>0)
                        map.put("name", Commons.thisEntity.get_fullName());

                    if(Commons.thisEntity.get_name().length()>0)
                        map.put("name", Commons.thisEntity.get_name());

                    map.put("photo", Commons.thisEntity.get_photoUrl());

                    reference.push().setValue(map);
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott"))) {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("email", Commons.thisEntity.get_email());

                            if(Commons.thisEntity.get_fullName().length()>0)
                                map.put("name", Commons.thisEntity.get_fullName());

                            if(Commons.thisEntity.get_name().length()>0)
                                map.put("name", Commons.thisEntity.get_name());

                            map.put("photo", Commons.thisEntity.get_photoUrl());

                            reference.push().setValue(map);
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(SignupActivity.this);
        rQueue.add(request);

        onSuccessRegister();
    }

    public String createInterestArrayList() throws JSONException {
        JSONObject interestList=new JSONObject();
        ArrayList<String> golfArr, runArr, skiArr, tennisArr, bikingArr, fishingArr, surfingArr, exploringArr;
        String jsonStr="";

        if(golf.isChecked()){
            golfArr=new ArrayList<>();
            if(golfBeginner.isChecked()) golfArr.add(golfBeginner.getText().toString());
            if(golfIntermediate.isChecked()) golfArr.add(golfIntermediate.getText().toString());
            if(golfAdvanced.isChecked()) golfArr.add(golfAdvanced.getText().toString());
            if(golfScramble.isChecked()) golfArr.add(golfScramble.getText().toString());
            interestList.put("Golf", golfArr);
        }

        if(run.isChecked()){
            runArr=new ArrayList<>();
            if(runWalk.isChecked()) runArr.add(runWalk.getText().toString());
            if(run30mins.isChecked()) runArr.add(run30mins.getText().toString());
            if(run1mile.isChecked()) runArr.add(run1mile.getText().toString());
            if(run5miles.isChecked()) runArr.add(run5miles.getText().toString());
            if(run10miles.isChecked()) runArr.add(run10miles.getText().toString());
            interestList.put("Run", runArr);
        }

        if(ski.isChecked()){
            skiArr=new ArrayList<>();
            if(skiHot.isChecked()) skiArr.add(skiHot.getText().toString());
            if(skiBunny.isChecked()) skiArr.add(skiBunny.getText().toString());
            if(skiGreens.isChecked()) skiArr.add(skiGreens.getText().toString());
            if(skiBlues.isChecked()) skiArr.add(skiBlues.getText().toString());
            if(skiBlacks.isChecked()) skiArr.add(skiBlacks.getText().toString());
            if(skiDouble.isChecked()) skiArr.add(skiDouble.getText().toString());
            if(skiAfraid.isChecked()) skiArr.add(skiAfraid.getText().toString());
            interestList.put("Ski & Snowboard", skiArr);
        }

        if(tennis.isChecked()){
            tennisArr=new ArrayList<>();
            if(tennis3.isChecked()) tennisArr.add(tennis3.getText().toString());
            if(tennis4.isChecked()) tennisArr.add(tennis4.getText().toString());
            if(tennis5.isChecked()) tennisArr.add(tennis5.getText().toString());
            if(tennis6.isChecked()) tennisArr.add(tennis6.getText().toString());
            if(tennisWTF.isChecked()) tennisArr.add(tennisWTF.getText().toString());
            interestList.put("Tennis", tennisArr);
        }

        if(biking.isChecked()){
            bikingArr=new ArrayList<>();
            if(bikingShort.isChecked()) bikingArr.add(bikingShort.getText().toString());
            if(bikingLong.isChecked()) bikingArr.add(bikingLong.getText().toString());
            if(bikingMountain.isChecked()) bikingArr.add(bikingMountain.getText().toString());
            if(bikingRoad.isChecked()) bikingArr.add(bikingRoad.getText().toString());
            interestList.put("Biking", bikingArr);
        }

        if(fishing.isChecked()){
            fishingArr=new ArrayList<>();
            if(fishingFly.isChecked()) fishingArr.add(fishingFly.getText().toString());
            if(fishingLake.isChecked()) fishingArr.add(fishingLake.getText().toString());
            if(fishingOcean.isChecked()) fishingArr.add(fishingOcean.getText().toString());
            interestList.put("Fishing", fishingArr);
        }

        if(surfing.isChecked()){
            surfingArr=new ArrayList<>();
            if(surfingOcean.isChecked()) surfingArr.add(surfingOcean.getText().toString());
            if(surfingLake.isChecked()) surfingArr.add(surfingLake.getText().toString());
            if(surfingKitesurfing.isChecked()) surfingArr.add(surfingKitesurfing.getText().toString());
            interestList.put("Surfing/Kitesurfing", surfingArr);
        }

        if(exploring.isChecked()){
            exploringArr=new ArrayList<>();
            if(exploringMuseums.isChecked()) exploringArr.add(exploringMuseums.getText().toString());
            if(exploringCity.isChecked()) exploringArr.add(exploringCity.getText().toString());
            if(exploringNature.isChecked()) exploringArr.add(exploringNature.getText().toString());
            if(exploringArt.isChecked()) exploringArr.add(exploringArt.getText().toString());
            if(exploringConcerts.isChecked()) exploringArr.add(exploringConcerts.getText().toString());
            if(exploringLectures.isChecked()) exploringArr.add(exploringLectures.getText().toString());
            interestList.put("Exploring", exploringArr);
        }

        jsonStr=interestList.toString();

        return jsonStr;
    }

}












































