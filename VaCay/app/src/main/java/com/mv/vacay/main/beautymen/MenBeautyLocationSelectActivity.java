package com.mv.vacay.main.beautymen;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.BeautyEntity;
import com.mv.vacay.models.BeautyServiceEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MenBeautyLocationSelectActivity extends BaseActivity implements View.OnClickListener{
    GridView gridView;
    ImageView back,san,newy,chica,denv;
    boolean _getAllMenus_flag=false;
    Intent intent;
    private AdView mAdView;
    final String[] items_list_beauty={"Classic Cut","Frosty Color","Trendy Cut","Classic Color","Executive Shave",
            "Chairman of the Board Shave","Neck Cleanup","How Towel Shave","MANicure","PIECE of Heaven CURE","Feet Meet Your Never-ending Treat",
            "Deep Tissue Massage","Swedish Massage","Neck & Back Massage","Sugar Massage","Chest Wax","Nose Wax","Back Wax","Eyebrow Wax",
            "Rejuvenating Eye Masque","Fountain of Youth Massage","De-Stress Facial","Gentleman's Facial"};
    final String[] categories = {"","Hair(Men)",
            "Hot Shave",
            "Manicure/Pedicure(Men)",
            "Massage(Men)",
            "Wax(Men)",
            "Facial(Men)"
    };

    final String[] cities = {"","San Francisco",
            "New York",
            "Chicago",
            "Denver"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_men_beauty_location_select);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        TextView santext=(TextView)findViewById(R.id.santext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        santext.setTypeface(font);

        TextView newtext=(TextView)findViewById(R.id.newtext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        newtext.setTypeface(font);

        TextView chitext=(TextView)findViewById(R.id.chitext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        chitext.setTypeface(font);

        TextView dentext=(TextView)findViewById(R.id.dentext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        dentext.setTypeface(font);

        TextView austintext=(TextView)findViewById(R.id.austintext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        austintext.setTypeface(font);

        TextView londontext=(TextView)findViewById(R.id.londontext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        londontext.setTypeface(font);

        san=(ImageView)findViewById(R.id.imv_sanfrancisco);
        san.setOnClickListener(this);
        newy=(ImageView)findViewById(R.id.imv_newyork);
        newy.setOnClickListener(this);
        chica=(ImageView)findViewById(R.id.imv_chicaco);
        chica.setOnClickListener(this);
        denv=(ImageView)findViewById(R.id.imv_denver);
        denv.setOnClickListener(this);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        Commons.beautyEntities_net.clear();

//        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
//            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        mAdView = (AdView) findViewById(R.id.adView);
//
//        AdRequest adRequest = new AdRequest.Builder()
////                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
////                // Check the LogCat to get your test device ID
////                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
//                .build();
//
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//            }
//
//            @Override
//            public void onAdClosed() {
//                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//        });
//
//        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Commons._beauty_facial_set=false;
                Commons._beauty_wax_set=false;
                Commons._beauty_massage_set=false;
                Commons._beauty_manicure_set=false;
                Commons._beauty_hotshave_set=false;
                Commons._beauty_hair_set=false;
                Intent intent=new Intent(this,MenBeautyServiceActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
            case R.id.imv_sanfrancisco:
                Commons.beautyAreaId=1;
                _getAllMenus_flag=true;
                gotoNext();
                break;
            case R.id.imv_newyork:
                Commons.beautyAreaId=2;
                _getAllMenus_flag=true;
                gotoNext();
                break;
            case R.id.imv_chicaco:
                Commons.beautyAreaId=3;
                _getAllMenus_flag=true;
                gotoNext();
                break;
            case R.id.imv_denver:
                Commons.beautyAreaId=4;
                _getAllMenus_flag=true;
                gotoNext();
                break;
        }
    }
    public void getServiceProviderInfo() {

        String url = ReqConst.SERVER_URL + "getServiceProviderInfo";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

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

                params.put("proBeautyCategory", categories[Commons.beautyCategoryId]);
                params.put("proCity", cities[Commons.beautyAreaId]);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        //      ui_RefreshLayout.setRefreshing(false);
        closeProgress();

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

                    BeautyServiceEntity beautyEntity = new BeautyServiceEntity();

                    beautyEntity.set_idx(jsonBeauty.getInt("serviceid"));
                    beautyEntity.set_proIdx(jsonBeauty.getInt("proid"));

                    beautyEntity.setBeautyName(jsonBeauty.getString("proBeautyCategory"));
                    beautyEntity.setBeautySubName(jsonBeauty.getString("proBeautySubcategory"));
                    beautyEntity.setBeautyPrice(jsonBeauty.getString("proServicePrice"));
                    beautyEntity.setBeautyImageUrl(jsonBeauty.getString("proServicePictureUrl"));
                    beautyEntity.setBeautyDescription(jsonBeauty.getString("proServiceDescription"));
                    beautyEntity.setProviderPhotoUrl(jsonBeauty.getString("proProfileImageUrl"));
                    beautyEntity.setFirstName(jsonBeauty.getString("proFirstName"));
                    beautyEntity.setLastName(jsonBeauty.getString("proLastName"));
                    beautyEntity.setEmail(jsonBeauty.getString("proEmail"));
                    beautyEntity.setCity(jsonBeauty.getString("proCity"));
                    beautyEntity.setLocation(jsonBeauty.getString("proAddress"));
                    beautyEntity.setCompanyName(jsonBeauty.getString("proCompany"));


                    Commons.beautyEntities_net.add(0,beautyEntity);     Log.d("NUMBER===",String.valueOf(Commons.beautyEntities_net.size()));

                }

//                resorting(Commons.beautyEntities_net);
                _getAllMenus_flag=true;
                gotoNext();

            }else if(result_code == 106){
                showToast("The provider doesn't serve such beauty in the area.");
                _getAllMenus_flag=true;
                gotoNext();
            }
            else {
                showToast(getString(R.string.error));
                _getAllMenus_flag=true;
                gotoNext();
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
            _getAllMenus_flag=true;
            gotoNext();
        }
    }

    public void resorting(ArrayList<BeautyEntity> entities){
        ArrayList<BeautyEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            BeautyEntity beautyEntity=new BeautyEntity();
            beautyEntity=entities.get(i);
            datas.add(beautyEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    public void gotoNext(){
        if(_getAllMenus_flag) {
            if (Commons._beauty_hair_set && Commons.beautyCategoryId==1) {
                intent = new Intent(this, MenHairActivity.class);
                startActivity(intent);
                finish();
                //           Commons._beauty_hair_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else if (Commons._beauty_facial_set && Commons.beautyCategoryId==6) {
                intent = new Intent(this, MenFacialActivity.class);
                startActivity(intent);
                finish();
                //            Commons._beauty_facial_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else if (Commons._beauty_wax_set && Commons.beautyCategoryId==5) {
                intent = new Intent(this, MenWaxActivity.class);
                startActivity(intent);
                finish();
                //            Commons._beauty_wax_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else if (Commons._beauty_massage_set && Commons.beautyCategoryId==4) {
                intent = new Intent(this, MenMassageActivity.class);
                startActivity(intent);
                finish();
                //            Commons._beauty_massage_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else if (Commons._beauty_manicure_set && Commons.beautyCategoryId==3) {
                intent = new Intent(this, MenManicureActivity.class);
                startActivity(intent);
                finish();
                //            Commons._beauty_manicure_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else if (Commons._beauty_hotshave_set && Commons.beautyCategoryId==2) {
                intent = new Intent(this, HotShaveActivity.class);
                startActivity(intent);
                finish();
                //            Commons._beauty_blowout_set = false;
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        }
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
