package com.mv.vacay.main.beauty;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.BeautyServiceAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.BeautyEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class BeautyAllDetailOfCategoryActivity extends BaseActivity implements SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    BeautyServiceAdapter beautyServiceAdapter=new BeautyServiceAdapter(this);
    ListView listView;
    ImageView back;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
//    ArrayList<BeautyEntity> beautyEntities1=new ArrayList<>();
    final String[] items_list_beauty={"Haircut","Color","Brazilian Blowout","Keratin Treatment","Deep Conditioner",
            "Blowout","Manicure","Manicure: Gel","Pedicure","Pedicure: Gel","Pink & White",
            "Deep Tissue Massage (50 Minutes)","Deep Tissue Massage (90 Minutes)","Swedish Massage (50 Minutes)","Swedish Massage (90 minutes)",
            "Eye Brow Wax","Lip Wax","Bikini Wax","Brazilian Wax","Basic Facial","Premium Facial"};
    final String[] categories = {"Hair",
            "Blowout",
            "Manicure/Pedicure",
            "Massage",
            "Wax",
            "Facial"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_all_detail_of_category);

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

        listView=(ListView)findViewById(R.id.list_beauty_detail);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons._beauty_blowout_set){
//                    Commons._beauty_blowout_set=false;
                    Intent intent=new Intent(getApplicationContext(),BlowoutActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }else if(Commons._beauty_facial_set){
//                    Commons._beauty_facial_set=false;
                    Intent intent=new Intent(getApplicationContext(),FacialActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }else if(Commons._beauty_hair_set){
//                    Commons._beauty_hair_set=false;
                    Intent intent=new Intent(getApplicationContext(),HairBeautyServiceActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }else if(Commons._beauty_manicure_set){
//                    Commons._beauty_manicure_set=false;
                    Intent intent=new Intent(getApplicationContext(),ManicureActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }else if(Commons._beauty_massage_set){
//                    Commons._beauty_massage_set=false;
                    Intent intent=new Intent(getApplicationContext(),MassageActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }else if(Commons._beauty_wax_set){
//                    Commons._beauty_wax_set=false;
                    Intent intent=new Intent(getApplicationContext(),WaxActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }
            }
        });

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
                    beautyServiceAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    beautyServiceAdapter.setbeautyDatas(Commons.beautyEntities1);
                    listView.setAdapter(beautyServiceAdapter);
                }

            }
        });

        showProgress();
        Commons.beautyEntities1.clear();

        for(int i=0;i< Commons.subbeautyEntities.size();i++){
            BeautyEntity beautyEntity=new BeautyEntity();
            beautyEntity.set_provider_name(Commons.subbeautyEntities.get(i).get_provider_name());
            beautyEntity.set_beauty_category(Commons.subbeautyEntities.get(i).get_beauty_category());
            beautyEntity.set_provider_resId(Commons.subbeautyEntities.get(i).get_provider_resId());
            beautyEntity.set_beauty_imageURL(Commons.subbeautyEntities.get(i).get_beauty_imageURL());
            beautyEntity.set_provider_imageURL(Commons.subbeautyEntities.get(i).get_provider_imageURL());
            beautyEntity.set_beautyBitmap(Commons.subbeautyEntities.get(i).get_beautyBitmap());
            beautyEntity.set_providerBitmap(Commons.subbeautyEntities.get(i).get_providerBitmap());
            beautyEntity.set_beauty_resId(Commons.subbeautyEntities.get(i).get_beauty_resId());
            beautyEntity.set_beauty_name(Commons.subbeautyEntities.get(i).get_beauty_name());
            beautyEntity.set_description(Commons.subbeautyEntities.get(i).get_description());
            beautyEntity.set_beauty_price(Commons.subbeautyEntities.get(i).get_beauty_price());
            beautyEntity.set_provider_email(Commons.subbeautyEntities.get(i).get_provider_email());
            beautyEntity.set_providerLatlng(Commons.subbeautyEntities.get(i).get_providerLatlng());
            double lat=beautyEntity.get_providerLatlng().latitude;  Log.d("PROlat0====",String.valueOf(lat));
            double lng=beautyEntity.get_providerLatlng().longitude;  Log.d("PROlng0====",String.valueOf(lng));
            beautyEntity.set_providerlat(lat);
            beautyEntity.set_providerlng(lng);

            Commons.beautyEntities1.add(beautyEntity);
        }
        closeProgress();
        beautyServiceAdapter.setbeautyDatas(Commons.beautyEntities1);
        beautyServiceAdapter.notifyDataSetChanged();
        listView.setAdapter(beautyServiceAdapter);

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

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {

//                getAllMenus(Commons.beautyCategoryId,Commons.beautyAreaId);

            }

        });
    }

    public void getAllMenus(int beautyCategoryId, int beautyAreaId) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETPROVIDERINFO;

        String params = String.format("/%d/%d",beautyCategoryId,beautyAreaId);
        url += params;

        ui_RefreshLayout.setRefreshing(true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                ui_RefreshLayout.setRefreshing(false);
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetBeautiesResponse(String json) {

        ui_RefreshLayout.setRefreshing(false);

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray providers = response.getJSONArray(ReqConst.RES_PROVIDERINFO);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("providers===",providers.toString());

                for (int i = 0; i < providers.length(); i++) {

                    JSONObject jsonBeauty = (JSONObject) providers.get(i);

                    BeautyEntity beautyEntity = new BeautyEntity();

                    beautyEntity.set_idx(jsonBeauty.getInt(ReqConst.RES_PROID));
//                    user.set_userName(jsonUser.getString(ReqConst.RES_USERNAME));
                    beautyEntity.set_provider_email(jsonBeauty.getString(ReqConst.RES_PROEMAIL));
                    beautyEntity.set_provider_name(jsonBeauty.getString(ReqConst.RES_PRONAME));
                    beautyEntity.set_provider_imageURL(jsonBeauty.getString(ReqConst.RES_PROPHOTO));
                    beautyEntity.set_providerlat(jsonBeauty.getDouble(ReqConst.RES_PROLATITUDE));
                    beautyEntity.set_providerlng(jsonBeauty.getDouble(ReqConst.RES_PROLONGITUDE));
                    double lat=beautyEntity.get_providerlat();  Log.d("PROlat====",String.valueOf(lat));
                    double lng=beautyEntity.get_providerlng();  Log.d("PROlng====",String.valueOf(lng));
                    LatLng latLng=new LatLng(lat,lng);
                    beautyEntity.set_providerLatlng(new LatLng(lat,lng)); Log.d("PROlatlng====",beautyEntity.get_providerLatlng().toString());
                    beautyEntity.set_beauty_price((float)jsonBeauty.getDouble(ReqConst.RES_BEAUTYPRICE));
                    beautyEntity.set_description(jsonBeauty.getString(ReqConst.RES_BEAUTYDESCRIPTION));
                    beautyEntity.set_beauty_imageURL(jsonBeauty.getString(ReqConst.RES_BEAUTYIMAGE));
                    beautyEntity.set_beauty_name(items_list_beauty[jsonBeauty.getInt(ReqConst.RES_BEAUTYNAMEID)]);
                    beautyEntity.set_beauty_category(categories[Commons.beautyCategoryId]);
//                    user.set_aboutme(jsonUser.getString(ReqConst.RES_ABOUTME));   //user email
//                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));  //user survey info
//                    user.set_friendCount(jsonUser.getInt(ReqConst.RES_FRIENDCOUNT));  //message

                    // except me
//                    if (restaurantEntity.get_idx() == Commons.g_user.get_idx())
//                        continue;

                    String firstLetter = beautyEntity.get_beauty_name().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }

                    Commons.beautyEntities1.add(beautyEntity);
                }

//                _curpage++;

                beautyServiceAdapter.setbeautyDatas(Commons.beautyEntities1);
                beautyServiceAdapter.resorting(Commons.beautyEntities1);
                beautyServiceAdapter.notifyDataSetChanged();

            }else if(result_code == 106){
                showToast("The provider doesn't serve such beauty in the area.");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
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


    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //     getAllMenus(Commons.beautyCategoryId,Commons.beautyAreaId);
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
}
