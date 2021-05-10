package com.mv.vacay.main.restaurant;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.RestaurantAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.RestaurantEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DenverRestaurantMenuActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener{

    private final int REQ_CODE_SPEECH_INPUT = 100;
    RestaurantAdapter restaurantAdapter=new RestaurantAdapter(this);
    SwipyRefreshLayout ui_RefreshLayout;
    ListView resmenu;
    ImageView back;
    EditText ui_edtsearch;
    ArrayList<RestaurantEntity> _datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_restaurant_menu);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        ImageView speechButton=(ImageView)findViewById(R.id.search_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });
        ImageView delete=(ImageView)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui_edtsearch.setText("");
            }
        });

        resmenu=(ListView)findViewById(R.id.list_food_menu);
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
                    restaurantAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    restaurantAdapter.setUserDatas(_datas);
                    resmenu.setAdapter(restaurantAdapter);
                }

            }
        });

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

//        for(int i = 0; i< Commons.restaurantEntities.size(); i++){
//            try {
//                _datas.add(Commons.restaurantEntities.get(i));
//            }catch (NullPointerException e){}
//        }

        int[] resIds={R.drawable.dencru,R.drawable.denedge,R.drawable.denguard,R.drawable.denhapa,R.drawable.denlinger,R.drawable.denlola,R.drawable.denmister,R.drawable.denocean,R.drawable.denosteria,R.drawable.denreoja,R.drawable.densqu,R.drawable.dentherio,R.drawable.denthirsty,R.drawable.denzengo};
        String[] resnames={"Cru Wine Bar","Edge Bar","Delarosa","Guard and Grace","Hapa Sushi","Linger","Lola","Mister Tuna","Ocean Prime","Osteria Marco","Rioja","Squeaky Bean","The Rio","Thirsty Lion","Zengo"};
        String[] restypes={"Napa Style Food & Wine","Steakhouse","Modern Steakhouse","Sushi","Mexican","Mexican","New American","Steakhouse","Italian","Mediterranean","Farm and Table","Mexican","Modern Pub","Asian"};
        String[] resmenus= new String[]{getString(R.string.menu0), getString(R.string.menu1), getString(R.string.menu2), getString(R.string.menu3), getString(R.string.menu4), getString(R.string.menu5), getString(R.string.menu6), getString(R.string.menu7), getString(R.string.menu8), getString(R.string.menu9), getString(R.string.menu10), getString(R.string.menu11), getString(R.string.menu12), getString(R.string.menu13)};
        String[] opentables= new String[]{getString(R.string.open0), getString(R.string.open1), getString(R.string.open2), getString(R.string.open3), getString(R.string.open4), getString(R.string.open5), getString(R.string.open6), getString(R.string.open7), getString(R.string.open8),getString(R.string.open9), getString(R.string.open10), getString(R.string.open11), getString(R.string.open12), getString(R.string.open13)};
        String[] locations= new String[]{getString(R.string.loc0), getString(R.string.loc1), getString(R.string.loc2), getString(R.string.loc3), getString(R.string.loc4), getString(R.string.loc5), getString(R.string.loc6), getString(R.string.loc7), getString(R.string.loc8), getString(R.string.loc9), getString(R.string.loc10), getString(R.string.loc11),getString(R.string.loc12),getString(R.string.loc13)};

        for(int i=0;i<resIds.length;i++){
            RestaurantEntity restaurantEntity=new RestaurantEntity();
            restaurantEntity.setImageRes(resIds[i]);
            restaurantEntity.setRestaurant_name(resnames[i]);
            restaurantEntity.setRestaurant_type(restypes[i]);
            restaurantEntity.setFood_menu_url(resmenus[i]);
            restaurantEntity.setOpentable_url(opentables[i]);
            restaurantEntity.setRestaurant_location_url(locations[i]);
            _datas.add(restaurantEntity);
        }
        restaurantAdapter.setUserDatas(_datas);
        resmenu.setAdapter(restaurantAdapter);

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {
//                getAllMenus();
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent=new Intent(this,LocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //     getAllMenus();
    }

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToast("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            ui_edtsearch.setText(matches.get(0));

        }
    }
}
