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

import static com.mv.vacay.R.string.opa;

public class SanFranciscoRestaurantMenuActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener{

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
        setContentView(R.layout.activity_menu);

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

        int[] resIds={R.drawable.sf0,R.drawable.sf1,R.drawable.sf2,R.drawable.sf3,R.drawable.sf4,R.drawable.sf5,R.drawable.sf6,R.drawable.sf7,R.drawable.sf8,R.drawable.sf9,R.drawable.sf10,R.drawable.sf11};
        String[] resnames={"Acquerello","Boulevard","Delarosa","Gary Danko","Hog Island Oyster Co","House of Prime Rib","La Folie","Nopa","Saison","Spruce","The House","Tony’s Pizza Napoletana"};
        String[] restypes={"Italian","American","Italian","French","New Seafood","English","French","New American","New American","American","Asian","Italian"};
        String[] resmenus= new String[]{getString(R.string.menua), getString(R.string.menub), getString(R.string.menud), getString(R.string.menug), getString(R.string.menuh), getString(R.string.menuhh), getString(R.string.menul), getString(R.string.menun), getString(R.string.menus), getString(R.string.menuss), getString(R.string.menut), getString(R.string.menutt)};
        String[] opentables= new String[]{getString(opa), getString(R.string.opb), getString(R.string.opd), getString(R.string.opg), getString(R.string.oph), getString(R.string.ophh), getString(R.string.opl), getString(R.string.opn), getString(R.string.ops),getString(R.string.opss), getString(R.string.opt), getString(R.string.optt)};
        String[] locations= new String[]{getString(R.string.aa), getString(R.string.bb), getString(R.string.dd), getString(R.string.gg), getString(R.string.hh), getString(R.string.hhh), getString(R.string.ll), getString(R.string.nn), getString(R.string.ss), getString(R.string.sss), getString(R.string.tt), getString(R.string.ttt),};

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
//       getAllMenus();
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
