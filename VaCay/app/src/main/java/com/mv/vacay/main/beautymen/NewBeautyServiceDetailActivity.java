package com.mv.vacay.main.beautymen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.mv.vacay.adapter.ProviderScheduleListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewBeautyServiceDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private AdView mAdView;
    ImageView back,cancel;
    CircularImageView providerImage;
    CircularNetworkImageView providerImageNet;
    NetworkImageView serviceImageNet;
    TextView providerName,password,company,email,location,productList,available;
    ProgressDialog _progressDlg;
    ImageLoader _imageloader;
    LinearLayout schedulePage;
    ListView list;
    ProviderScheduleListAdapter providerScheduleListAdapter = new ProviderScheduleListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_beauty_service_detail);

        _imageloader = VaCayApplication.getInstance().getImageLoader();

        closeProgress();

        providerImage=(CircularImageView) findViewById(R.id.provider_profile);
        providerImageNet=(CircularNetworkImageView) findViewById(R.id.provider_profile_net);

        if(Commons.newBeautyEntity.getProviderPhotoUrl().length()>1000) {
            providerImageNet.setVisibility(View.GONE);
            providerImage.setImageBitmap(base64ToBitmap(Commons.newBeautyEntity.getProviderPhotoUrl()));
        }else {
            providerImageNet.setVisibility(View.VISIBLE);
            providerImageNet.setImageUrl(Commons.newBeautyEntity.getProviderPhotoUrl(),_imageloader);
        }

//        if(!Commons.beautyEntity.get_providerBitmap().equals(null))providerImage.setImageBitmap(Commons.beautyEntity.get_providerBitmap());
//        else providerImage.setImageResource(Commons.beautyEntity.get_provider_resId());
        providerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Please wait.........");
//                showProgress();
                Intent intent=new Intent(getApplicationContext(),NewProviderLocationViewActivity.class);
                startActivity(intent);
//                finish();
                overridePendingTransition(0,0);
            }
        });

        providerName=(TextView)findViewById(R.id.provider_name);
        providerName.setText(Commons.newBeautyEntity.getFullName());

        email=(TextView)findViewById(R.id.providerEmail);
        email.setText(Commons.newBeautyEntity.getEmail());

        password=(TextView)findViewById(R.id.tempPwd);
        password.setText("Pwd: "+Commons.newBeautyEntity.getPassword());

        company=(TextView)findViewById(R.id.company_name);
        company.setText(Commons.newBeautyEntity.getCompanyName());

        location=(TextView)findViewById(R.id.location);
        location.setText(Commons.newBeautyEntity.getLocation());

        available=(TextView)findViewById(R.id.available);
        available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                schedulePage.setVisibility(View.VISIBLE);
                getProviderAvailable();
            }
        });

        ImageView availableicon = (ImageView)findViewById(R.id.availableicon);

        if (Commons.newBeautyEntity.getAvailable().equals("true") || Commons.newBeautyEntity.getAvailable().length()==0)
            availableicon.setImageResource(R.drawable.bluecircle);
        else availableicon.setImageResource(R.drawable.redcircle);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        productList=(TextView)findViewById(R.id.productList);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        productList.setTypeface(font);


        productList.setOnClickListener(this);

        final CustomHolder holder=new CustomHolder();

        holder.linearLayout1=(LinearLayout) findViewById(R.id.beautyFrame1);
        holder.beautyImageNet=(NetworkImageView) findViewById(R.id.beauty_image_net);
        holder.beautyImage1=(ImageView) findViewById(R.id.beauty_image);
        holder.beautyPrice1=(TextView)findViewById(R.id.beauty_price);
        holder.beautyName1=(TextView)findViewById(R.id.beauty_category);
        holder.beautySubName1=(TextView)findViewById(R.id.beauty_name);
        holder.beautyDescription1=(TextView)findViewById(R.id.description);
        holder.accept1=(TextView)findViewById(R.id.accept_request);

        holder.beautyPrice1.setTypeface(font);
        holder.beautySubName1.setTypeface(font);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        holder.beautyDescription1.setTypeface(font);


        try {
            if(!Commons.newBeautyEntity.getBeautyPrice().contains("$")){
                holder.beautyPrice1.setText("$ "+Commons.newBeautyEntity.getBeautyPrice());
            }
            else holder.beautyPrice1.setText(Commons.newBeautyEntity.getBeautyPrice());
            holder.beautyName1.setText(Commons.newBeautyEntity.getBeautyName());
            holder.beautySubName1.setText(Commons.newBeautyEntity.getBeautySubName());
            holder.beautyDescription1.setText(Commons.newBeautyEntity.getBeautyDescription());

            if (Commons.newBeautyEntity.getBeautyImageUrl().length() > 1000) {
                holder.beautyImageNet.setVisibility(View.GONE);
                holder.beautyImage1.setImageBitmap(base64ToBitmap(Commons.newBeautyEntity.getBeautyImageUrl()));
            }else {
                holder.beautyImageNet.setVisibility(View.VISIBLE);
                holder.beautyImageNet.setImageUrl(Commons.newBeautyEntity.getBeautyImageUrl(),_imageloader);
            }

            holder.beautyImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Commons.resId = Commons.newBeautyEntity.getBeautyImageRes();
                    Commons.photoUrl = Commons.newBeautyEntity.getBeautyImageUrl();
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    startActivity(intent);
                }
            });
            holder.accept1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            holder.accept1.setBackgroundResource(R.drawable.green_fillround);
                            holder.accept1.setTextColor(ColorStateList.valueOf(Color.WHITE));
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            holder.accept1.setBackgroundResource(R.drawable.white_stroke);
                            holder.accept1.setTextColor(ColorStateList.valueOf(Color.WHITE));

                            Intent intent=new Intent(getApplicationContext(),RequestBeautyActivity.class);
                            startActivity(intent);

                        case MotionEvent.ACTION_CANCEL: {
                            //clear the overlay
                            holder.accept1.getBackground().clearColorFilter();
                            holder.accept1.invalidate();
                            break;
                        }
                    }

                    return true;
                }
            });

            holder.linearLayout1.setVisibility(View.VISIBLE);

        }catch (NullPointerException e){
            e.printStackTrace();
            TextView nobeauty=(TextView)findViewById(R.id.nobeauty);
            nobeauty.setVisibility(View.VISIBLE);
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

    public void enterMedia(View v){
        getMedia(String.valueOf(Commons.newBeautyEntity.get_idx()),"service");
    }

    public void showProgress(boolean cancelable) {

        closeProgress();

        _progressDlg = new ProgressDialog(this, R.style.MyTheme);
        _progressDlg
                .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        _progressDlg.setCancelable(cancelable);
        _progressDlg.show();
    }

    public void showProgress() {
        showProgress(false);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        _progressDlg.dismiss();
        _progressDlg = null;
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            case R.id.productList:
                Intent intent1 = new Intent(this, ProductListActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    class CustomHolder {

        public LinearLayout linearLayout1;
        public NetworkImageView beautyImageNet;
        public ImageView beautyImage1;
        public TextView beautyName1;
        public TextView beautySubName1;
        public TextView beautyPrice1;
        public TextView beautyDescription1;
        public TextView accept1;

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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
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
                    Intent intent=new Intent(getApplicationContext(),MediaActivity.class);

                    mediaEntity.setObjimage(Commons.newBeautyEntity.getProviderPhotoUrl());
                    mediaEntity.setObjtitle(Commons.newBeautyEntity.getBeautyName());
                    mediaEntity.setObjsubtitle(Commons.newBeautyEntity.getBeautySubName());

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







































