package com.mv.vacay.main.info;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
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
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.WaterCoolerListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.watercooler.WatercoolerSetupActivity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.mv.vacay.models.WaterCoolerEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommonInfoViewActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    LinearLayout menuPortion;
    TextView menuButton, setupButton, helpful, inspiration, praise, wellness, question;
    boolean isOpened=false;
    FrameLayout frame;
    WaterCoolerListAdapter adapter=new WaterCoolerListAdapter(this);
    ArrayList<WaterCoolerEntity> waterCoolerEntities=new ArrayList<>();
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_info_view);

        TextView title=(TextView)findViewById(R.id.fontableTextView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        title.setTypeface(font);


        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        listView=(ListView) findViewById(R.id.list);

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
                    adapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    adapter.setDatas(waterCoolerEntities);
                    listView.setAdapter(adapter);
                }

            }
        });

        TextView setupButton=(TextView)findViewById(R.id.setupButton);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        setupButton.setTypeface(font);


        menuPortion=(LinearLayout)findViewById(R.id.menuPortion);

        menuButton=(TextView)findViewById(R.id.categoryButton);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        menuButton.setTypeface(font);


        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpened) {
                    isOpened=true;
                    menuButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.arrow_back3,// left
                            0,//top
                            0,// right
                            0//bottom
                    );

                    menuPortion.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
                    menuPortion.startAnimation(animation);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shadowLayout();
                        }
                    }, 300);

                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
                    menuPortion.startAnimation(animation);
                    menuPortion.setVisibility(View.GONE);
                    isOpened=false;
                    menuButton.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.menubuttonicon,// left
                            0,//top
                            0,// right
                            0//bottom
                    );
                    dismissShadow();
                }
            }
        });

        frame=(FrameLayout)findViewById(R.id.layout);

        frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissMenuPortion();
                dismissShadow();
            }
        });

        helpful=(TextView)findViewById(R.id.helpful);
        helpful.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(helpful.getText());
                dismissMenuPortion();
                dismissShadow();
                getWatercoolerInfo();
            }
        });
        inspiration=(TextView)findViewById(R.id.inspiration);
        inspiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(inspiration.getText());
                dismissMenuPortion();
                dismissShadow();
                getWatercoolerInfo();
            }
        });
        wellness=(TextView)findViewById(R.id.wellness);
        wellness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(wellness.getText());
                dismissMenuPortion();
                dismissShadow();
                getWatercoolerInfo();
            }
        });
        question=(TextView)findViewById(R.id.question);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(question.getText());
                dismissMenuPortion();
                dismissShadow();
                getWatercoolerInfo();
            }
        });

    }
    public void dismissMenuPortion(){
        if(isOpened){
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
            menuPortion.startAnimation(animation);
            menuPortion.setVisibility(View.GONE);
            isOpened=false;
            menuButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.menubuttonicon,// left
                    0,//top
                    0,// right
                    0//bottom
            );
        }
    }
    public void gotoWatercoolerSetup(View v){
        Intent intent=new Intent(getApplicationContext(), CommonInfoSetupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
    public void shadowLayout(){
        frame.setVisibility(View.VISIBLE);
    }

    public void dismissShadow(){
        frame.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getWatercoolerInfo(){
        waterCoolerEntities.clear();
        if(menuButton.getText().equals("Category"))
            showToast("Please select category...");
        else
            getWatercooler(menuButton.getText().toString(), "common");
    }

    public void getWatercooler(final String category, final String company) {

        String url = ReqConst.SERVER_URL + "get_watercooler";

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

                params.put("category", category);
                params.put("company", company);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetMessagesResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("WCresponse====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray watercoolers = response.getJSONArray("data");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("watercoolers===",watercoolers.toString());

                for (int i = 0; i < watercoolers.length(); i++) {

                    JSONObject jsonWaterCooler = (JSONObject) watercoolers.get(i);

                    WaterCoolerEntity waterCoolerEntity=new WaterCoolerEntity();

                    waterCoolerEntity.setIdx(jsonWaterCooler.getString("id"));
                    waterCoolerEntity.setUserName(jsonWaterCooler.getString("name"));
                    waterCoolerEntity.setCategory(jsonWaterCooler.getString("category"));
                    waterCoolerEntity.setCompany(jsonWaterCooler.getString("company"));
                    waterCoolerEntity.setContent(jsonWaterCooler.getString("content"));
                    waterCoolerEntity.setArticle(jsonWaterCooler.getString("link"));
                    waterCoolerEntity.setProfilePhotoUrl(jsonWaterCooler.getString("photoUrl"));

                    waterCoolerEntities.add(waterCoolerEntity);
                }
                if(waterCoolerEntities.isEmpty())
                    showToast("No Users' Info...");
                adapter.setDatas(waterCoolerEntities);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

            }
            else {

                showToast("Server Error...");
            }

        } catch (JSONException e) {

            showToast("Server Error...");

            e.printStackTrace();
        }
    }
}


















































































