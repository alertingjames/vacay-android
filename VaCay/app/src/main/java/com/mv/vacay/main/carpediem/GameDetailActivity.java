package com.mv.vacay.main.carpediem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.CheckinListAdapter0;
import com.mv.vacay.adapter.GameListAdapter;
import com.mv.vacay.adapter.MenuListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.CheckinActivity;
import com.mv.vacay.models.GameEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class GameDetailActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView back,playButton,barImage;
    View view2,view1,view3,view4;
    NetworkImageView gameImage;
    TextView barName,gameName,menuList,videoList,checkinList,specials,teamChannel,detailTitle,allVideos,checkinTitle,duration;
    ListView listVideos,menu,checkin;
    LinearLayout lytListVideos,lytmenu,lytListCheckin;
    CheckinListAdapter0 checkinListAdapter=new CheckinListAdapter0(this);
    GameListAdapter gameListAdapter=new GameListAdapter(this);
    MenuListAdapter menuListAdapter;
    ImageLoader _imageLoader;
    boolean _openMenu=false,_openVideos=false,_openCheckin=false;
    ArrayList<GameEntity> _datas_videos=new ArrayList<>();
    ArrayList<UserEntity> _datas_checkin=new ArrayList<>();
    private ProgressDialog _progressDlg;
    AdView mAdView;
    File file;
    int _idx;
    boolean _is_videos_menu_click=false;
    boolean _is_checkin_menu_click=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        TextView title=(TextView)findViewById(R.id.detail_title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        barImage=(ImageView)findViewById(R.id.bar_image);
        if(Commons.gameEntity.getBarType().trim().equals("Restaurant"))
            barImage.setImageResource(R.drawable.reestaurant);
        else if(Commons.gameEntity.getBarType().trim().equals("Bar"))
            barImage.setImageResource(R.drawable.beer);
        else if(Commons.gameEntity.getBarType().trim().equals("Park"))
            barImage.setImageResource(R.drawable.ppark);

        gameImage=(NetworkImageView)findViewById(R.id.game_image);
        playButton=(ImageView) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);
        barName=(TextView)findViewById(R.id.barname);
        gameName=(TextView)findViewById(R.id.gamename);
        duration=(TextView)findViewById(R.id.duration);
        if(Commons.gameEntity.getDuaration().length()>0){
            duration.setVisibility(View.VISIBLE);
            duration.setText(Commons.gameEntity.getDuaration());
        }else duration.setVisibility(View.GONE);
        allVideos=(TextView)findViewById(R.id.allvideos);
        allVideos.setOnClickListener(this);
        detailTitle=(TextView)findViewById(R.id.detail_title);
        if(Commons.gameEntity.getVideoType().toLowerCase().equals("game"))
            detailTitle.setText("Welcome! Please enjoy this game as much as you like.");
        else detailTitle.setText("Welcome! Please enjoy this movie as much as you like.");
        menuList=(TextView)findViewById(R.id.menuList);
        menuList.setOnClickListener(this);
        videoList=(TextView)findViewById(R.id.videos);
        if(Commons._is_game){
//            Commons._is_game=false;
            videoList.setText("Games(More)");
        }
        videoList.setOnClickListener(this);
        checkinList=(TextView)findViewById(R.id.checkin);
        checkinList.setOnClickListener(this);
        specials=(TextView)findViewById(R.id.specials);
        Log.d("Special===>",Commons.gameEntity.getSpecials());
        if(Commons.gameEntity.getSpecials().length()>0)
            specials.setText(Commons.gameEntity.getSpecials());
        teamChannel=(TextView)findViewById(R.id.team_channel);
        lytListVideos=(LinearLayout)findViewById(R.id.lyt_listVideos);
        lytListCheckin=(LinearLayout)findViewById(R.id.lyt_listCheckin);
        lytmenu=(LinearLayout)findViewById(R.id.lyt_list_menu);
        listVideos=(ListView)findViewById(R.id.list_videos);
        checkin=(ListView)findViewById(R.id.list_checkin);
        menu=(ListView)findViewById(R.id.list_menu);
        view1=(View)findViewById(R.id.bound);
        view2=(View)findViewById(R.id.bound1);
        view3=(View)findViewById(R.id.bound2);
        view4=(View)findViewById(R.id.bound3);
        checkinTitle=(TextView)findViewById(R.id.checkin1);

        if (Commons.gameEntity.getGameThumbnailUrl().length() > 0) {
            gameImage.setImageUrl(Commons.gameEntity.getGameThumbnailUrl(),_imageLoader);
        }
        barName.setText(Commons.gameEntity.getBarName());
        gameName.setText(Commons.gameEntity.getGameName());
        if(Commons.gameEntity.getTeamName().length()>0){
            teamChannel.setText("Team: "+Commons.gameEntity.getTeamName()+"\nChannel: "+Commons.gameEntity.getChannel());
        }else if(Commons.gameEntity.getChannel().length()>0) teamChannel.setText("Channel: "+Commons.gameEntity.getChannel());
     //   specials.setText();

        for(int i=0;i<Commons.gameEntities.size();i++){
            if(
//                    Commons.gameEntity.getBarName().equals(Commons.gameEntities.get(i).getBarName()) &&
                            !Commons.gameEntity.getGameName().equals(Commons.gameEntities.get(i).getGameName())) {

                GameEntity gameEntity = new GameEntity();
                gameEntity.setGameName(Commons.gameEntities.get(i).getGameName());
                gameEntity.setVideoId(Commons.gameEntities.get(i).getVideoId());
                gameEntity.setVideoType(Commons.gameEntities.get(i).getVideoType());
                gameEntity.setTeamName(Commons.gameEntities.get(i).getTeamName());
                gameEntity.setDuaration(Commons.gameEntities.get(i).getDuaration());
                try{
                    gameEntity.setSalePrice(Commons.gameEntities.get(i).getSalePrice());
                    gameEntity.setRentPrice(Commons.gameEntities.get(i).getRentPrice());
                    gameEntity.setVideoTypeSecondary(Commons.gameEntities.get(i).getVideoTypeSecondary());
                    gameEntity.setWatchWithPrice(Commons.gameEntities.get(i).getWatchWithPrice());
                }catch (NullPointerException e){}
                gameEntity.setGameThumbnailUrl(Commons.gameEntities.get(i).getGameThumbnailUrl());
//            gameEntity.setResImageId(gameResIds[i]);
                gameEntity.setChannel(Commons.gameEntities.get(i).getChannel());
                gameEntity.setBarName(Commons.gameEntities.get(i).getBarName());
                gameEntity.setBarType(Commons.gameEntities.get(i).getBarType());
                gameEntity.setKnownName(Commons.gameEntities.get(i).getKnownName());
                ArrayList<String> arrayList = new ArrayList<>();
                for (int k = 0; k < Commons.gameEntities.get(i).getResMenus().size(); k++)
                    arrayList.add(Commons.gameEntities.get(i).getResMenus().get(k));
                gameEntity.setResMenus(arrayList);
//            ArrayList<String> arrayList1=new ArrayList<>();
//            for(int k=0;k<secondaryGameIds[i].length;k++)
//                arrayList1.add(secondaryGameIds[i][k]);
//            gameEntity.setVideoIds(arrayList1);
//            ArrayList<String> arrayList2=new ArrayList<>();
//            for(int k=0;k<secondaryGameNames[i].length;k++)
//                arrayList2.add(secondaryGameNames[i][k]);
//            gameEntity.setGameNames(arrayList2);
//            ArrayList<String> arrayList3=new ArrayList<>();
//            for(int k=0;k<secondaryGameThumbnails[i].length;k++)
//                arrayList3.add(secondaryGameThumbnails[i][k]);
//            gameEntity.setGameThumbUrls(arrayList3);
                _datas_videos.add(gameEntity);
            }
        }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.playButton:
                showInfo();
                break;
            case R.id.menuList:

                if (!_openMenu) {
                    lytmenu.setVisibility(View.VISIBLE);
                    lytmenu.setActivated(true);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    menuList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop2,// right
                            0//bottom
                    );
                    menuListAdapter=new MenuListAdapter(this,Commons.gameEntity.getResMenus());
                    menu.setAdapter(menuListAdapter);
                    _openMenu = true;
                } else {
                    lytmenu.setVisibility(View.GONE);
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    menuList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop1,// right
                            0//bottom
                    );
                    _openMenu = false;
                }
                break;
            case R.id.videos:
                if (!_openVideos) {
                    lytListVideos.setVisibility(View.VISIBLE);
                    lytListVideos.setActivated(true);
                    _is_videos_menu_click=true;
            //        view3.setVisibility(View.GONE);
                    if(_is_checkin_menu_click){
                        checkinTitle.setVisibility(View.VISIBLE);
                    }else checkinTitle.setVisibility(View.GONE);
                    videoList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop0,// right
                            0//bottom
                    );
                    if(_datas_videos.isEmpty()) showToast("No videos");
                    gameListAdapter.setGameDatas(_datas_videos);
                    listVideos.setAdapter(gameListAdapter);
                    _openVideos = true;
                } else {
                    lytListVideos.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    _is_videos_menu_click=false;
                    if(_is_checkin_menu_click){
                        checkinTitle.setVisibility(View.GONE);
                        view3.setVisibility(View.GONE);
                    }
                    videoList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop,// right
                            0//bottom
                    );
                    _openVideos = false;
                }
                break;
            case R.id.checkin:
                if (!_openCheckin) {
                    lytListCheckin.setVisibility(View.VISIBLE);
                    lytListCheckin.setActivated(true);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.VISIBLE);
                    _is_checkin_menu_click=true;
                    if(_is_videos_menu_click){
                        checkinTitle.setVisibility(View.VISIBLE);
                    }
                    checkinList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop12,// right
                            0//bottom
                    );
                    _openCheckin = true;
                    checkIn();
                } else {
                    lytListCheckin.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    _is_checkin_menu_click=false;
                    if(_is_videos_menu_click){
                        checkinTitle.setVisibility(View.GONE);
                    }
                    checkinList.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop11,// right
                            0//bottom
                    );
                    _openCheckin = false;
                }
                break;
            case R.id.back:
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.allvideos:
                Commons._is_all_videos_theBar=true;
                Intent intent1=new Intent(this,GameListActivity.class);
                startActivity(intent1);
//                finish();
                overridePendingTransition(0,0);
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
                String[] publicNames={"mississauga","denver","colorado","colorado"};
                String[] relationships={"Single","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(jsonUser.getString(ReqConst.RES_AGE));
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
                checkin.setAdapter(checkinListAdapter);

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

        showToast("Your will watch this video.");

        Intent intent=new Intent(this,VideoDisplayActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);

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
    private  void showInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Hint!");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        if(Commons.thisEntity.get_photoUrl().length()>0) photo.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);
        else photo.setDefaultImageResId(Commons.thisEntity.get_imageRes());
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText("Please check in to watch and comment on this video. You must be at this very for to participate");
        builder.setView(dialogView);
        builder.setIcon(R.drawable.noti);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent=new Intent(getApplicationContext(), CheckinActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
