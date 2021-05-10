package com.mv.vacay.main.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.ActionUserInfoAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout.OnRefreshListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ExploringActivity extends BaseActivity implements View.OnClickListener,OnRefreshListener{
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<UserEntity> _datas=new ArrayList<>();
    ActionUserInfoAdapter actionUserInfoAdapter=new ActionUserInfoAdapter(this);

    TextView friend, shop;
    LinearLayout buttonPage;
    TextView all, nearby;
    LinearLayout buttonPage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exploring);

        buttonPage=(LinearLayout)findViewById(R.id.buttonPage);
        friend=(TextView)findViewById(R.id.friend);
        shop=(TextView)findViewById(R.id.shop);

        buttonPage2=(LinearLayout)findViewById(R.id.buttonPage2);
        all=(TextView)findViewById(R.id.all);
        nearby=(TextView)findViewById(R.id.nearby);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    actionUserInfoAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    actionUserInfoAdapter.setUserDatas(_datas);
                    listView.setAdapter(actionUserInfoAdapter);
                }

            }
        });


        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_exploring);

        actionUserInfoAdapter.setUserDatas(_datas);
        listView.setAdapter(actionUserInfoAdapter);

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

//        selectWay();
        showButtons();
    }

    private void showButtons() {
        buttonPage.setVisibility(View.VISIBLE);
        friend.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        friend.startAnimation(animation);
        shop.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromleft);
        shop.startAnimation(animation);
    }
    private void showButtons2() {
        buttonPage2.setVisibility(View.VISIBLE);
        all.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        all.startAnimation(animation);
        nearby.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromleft);
        nearby.startAnimation(animation);
    }

    private void dismissButtons() {
//        buttonPage.setVisibility(View.GONE);
        friend.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
        friend.startAnimation(animation);
        shop.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
        shop.startAnimation(animation);
    }
    private void dismissButtons2() {
//        buttonPage2.setVisibility(View.GONE);
        all.setVisibility(View.GONE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
        all.startAnimation(animation);
        nearby.setVisibility(View.GONE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
        nearby.startAnimation(animation);
    }

    public void friend(View view){
        dismissButtons();
        showButtons2();
    }
    public void shop(View view){
        dismissButtons();
        Intent intent=new Intent(getApplicationContext(),BroadmoorProductPageActivity.class);
        intent.putExtra("category","exploring");
        startActivity(intent);
        finish();
    }

    public void all(View view){
        dismissButtons2();
//        selectWay2();
        getAllUsers("all");
    }
    public void nearby(View view){
        dismissButtons2();
        getAllUsers("nearby");
    }

    public void getAllUsers(final String option) {

        String url = ReqConst.SERVER_URL + "getAllUsers";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString()); Log.d("GETALL===>",response.toString());

                parseGetUsersResponse(response, option);

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

    public void parseGetUsersResponse(String json, String option) {

        ui_RefreshLayout.setRefreshing(false);

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);


                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID));
//                    user.set_userName(jsonUser.getString(ReqConst.RES_USERNAME));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
//                    final Calendar c = Calendar.getInstance();
//                    int year = c.get(Calendar.YEAR);
//                    int birthyear=Integer.parseInt(jsonUser.getString(ReqConst.RES_AGE));
//                    int age=year-birthyear;
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest("-"+jsonUser.getString(ReqConst.RES_INTERESTS).replace("{","").replace("}","").replace("\",","\n-")
                            .replace("\"","").replace("[","").replace("]",""));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(jsonUser.getString("survey"));

                    try{
                        user.set_relations(jsonUser.getString("relationship"));
                        user.setMillennial(jsonUser.getString("em_millennial"));
                        user.set_publicName(jsonUser.getString("place_name"));
                        user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon")));
                        user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat")));
                    }catch (NullPointerException e){}

//                    user.set_aboutme(jsonUser.getString(ReqConst.RES_ABOUTME));   //user email
//                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));  //user survey info
//                    user.set_friendCount(jsonUser.getInt(ReqConst.RES_FRIENDCOUNT));  //message

                    // except me
                    if (user.get_idx() == Commons.thisEntity.get_idx())
                        continue;

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    if(option.equals("all")){
                        if (user.get_interest().contains("Exploring"))
                            _datas.add(0, user);
                    }
                    else if(option.equals("nearby")){
                        if (user.get_interest().contains("Exploring") && user.get_city().equals(Commons.thisEntity.get_city()))
                            _datas.add(0, user);
                    }
                }
                if(_datas.isEmpty())
                    showToast("No friends...");
                actionUserInfoAdapter.setUserDatas(_datas);
                actionUserInfoAdapter.notifyDataSetChanged();
                listView.setAdapter(actionUserInfoAdapter);

            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Commons._exploring_activity=false;
                Intent intent=new Intent(this,ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
//     getAllUsers();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public void selectWay() {

        final String[] items = {"I want to meet a VaCay friend", "I want to shop!"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want to do?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    selectWay2();
                } else if(item == 1){
                    Intent intent=new Intent(getApplicationContext(),BroadmoorProductPageActivity.class);
                    intent.putExtra("category","exploring");
                    startActivity(intent);
                    finish();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectWay2() {

        final String[] items = {"All VaCay friends who like exploring", "The nearby VaCay friends who likes exploring"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What friends?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    getAllUsers("all");
                } else if(item == 1){
                    getAllUsers("nearby");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
