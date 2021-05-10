package com.mv.vacay.main.beautymen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.MenBeautyServiceAdapter;
import com.mv.vacay.adapter.ProviderScheduleListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.beauty.BeautyServiceEntryActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BeautyListActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener{

    ListView listView,list;
    ImageView imvback,cancel;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<BeautyProductEntity> _datas=new ArrayList<>(10000);
    MenBeautyServiceAdapter proBeautyListAdapter=new MenBeautyServiceAdapter(this);
    ProviderScheduleListAdapter providerScheduleListAdapter = new ProviderScheduleListAdapter(this);
    Intent intent;
    LinearLayout schedulePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_list);

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.beautyList);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        if(Commons.subnewbeautyEntities.isEmpty()) showToast("No Beauty Service ...");
        proBeautyListAdapter.setUserDatas(Commons.subnewbeautyEntities);
        proBeautyListAdapter.notifyDataSetChanged();
        listView.setAdapter(proBeautyListAdapter);

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
                    proBeautyListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    proBeautyListAdapter.setUserDatas(Commons.subnewbeautyEntities);
                    listView.setAdapter(proBeautyListAdapter);
                }

            }
        });

        schedulePage=(LinearLayout) findViewById(R.id.timeListContainer);
        list=(ListView)findViewById(R.id.list);
        cancel=(ImageView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedulePage.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                if(Commons._gender_flag==1) intent=new Intent(getApplicationContext(),MenBeautyServiceActivity.class);
                else if(Commons._gender_flag==0)intent=new Intent(getApplicationContext(), BeautyServiceEntryActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void showSchedulePage(){
        schedulePage.setVisibility(View.VISIBLE);
        getProviderAvailable();
    }

    public void getProviderAvailable(){

        Commons.scheduleInfo.clear();

        String url = ReqConst.SERVER_URL + "getProviderAvailable";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseProviderAvailableResponse1(response);

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

                params.put("proid", String.valueOf(Commons.newBeautyEntity.get_proIdx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseProviderAvailableResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseAvailable====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray userInfo = response.getJSONArray("available_info");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("available===",userInfo.toString());

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    ProviderScheduleEntity providerScheduleEntity=new ProviderScheduleEntity();
                    String availableId=jsonUser.getString("availableid");
                    String proId=jsonUser.getString("proid");
                    String availableStart=jsonUser.getString("availableStart");
                    String availableEnd=jsonUser.getString("availableEnd");
                    String availableComment=jsonUser.getString("availableComment");

                    providerScheduleEntity.setScheduleId(availableId);
                    providerScheduleEntity.setProId(proId);
                    providerScheduleEntity.setScheduleStart(availableStart);
                    providerScheduleEntity.setScheduleEnd(availableEnd);
                    providerScheduleEntity.setScheduleComment(availableComment);

                    Commons.scheduleInfo.add(0,providerScheduleEntity);
                }
                list.setVisibility(View.VISIBLE);
                providerScheduleListAdapter.setDatas(Commons.scheduleInfo);
                providerScheduleListAdapter.notifyDataSetChanged();
                list.setAdapter(providerScheduleListAdapter);

            }
            else {

                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {

            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public void enterMedia(BeautyServiceEntity entity){
        getMedia(String.valueOf(entity.get_idx()),"service");
    }

    public void getMedia(final String itemID, final String item) {

        String url = ReqConst.SERVER_URL + "get_media";

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
                mediaEntity.setImageA(medias.getString("image_a"));
                mediaEntity.setImageB(medias.getString("image_b"));
                mediaEntity.setImageC(medias.getString("image_c"));
                mediaEntity.setImageD(medias.getString("image_d"));
                mediaEntity.setImageE(medias.getString("image_e"));
                mediaEntity.setImageF(medias.getString("image_f"));

                if(medias.length()>0){

                    mediaEntity.setObjimage(Commons.newBeautyEntity.getProviderPhotoUrl());
                    mediaEntity.setObjtitle(Commons.newBeautyEntity.getBeautyName());
                    mediaEntity.setObjsubtitle(Commons.newBeautyEntity.getBeautySubName());

                    Intent intent=new Intent(getApplicationContext(),MediaActivity.class);
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







































