package com.mv.vacay.main.beautymen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.beauty.HairBeautyServiceActivity;
import com.mv.vacay.models.BeautyServiceEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MenMassageActivity extends BaseActivity implements View.OnClickListener{

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Animation.AnimationListener mAnimationListener;
    ViewFlipper mHomeflipper;
    private Context mContext;
    ImageView back;
    private AdView mAdView;
    TextView ok;
    String ch1="",ch2="",ch3="",ch4="",ch5="";
    CheckBox checkBox1,checkBox2,checkBox3,checkBox4,checkBox5;
    ImageLoader _imageloader;
    int i,j;
    ArrayList<BeautyServiceEntity> beautyEntities=new ArrayList<>();
    ArrayList<BeautyServiceEntity> beautyEntitiesBuf=new ArrayList<>();

    ImageView floatbutton;

    LinearLayout onlineadviserlayout, offllineadviserlayout;

    @SuppressWarnings("deprecation")
    private final GestureDetector _detector = new GestureDetector(new SwipeGestureDetector());

    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();

    final String[] items_list_beauty={"Classic Cut","Frosty Color","Trendy Cut","Classic Color","Executive Shave",
            "Chairman of the Board Shave","Neck Cleanup","Hot Towel Shave","MANicure","PIECE of Heaven CURE","Feet Meet Your Never-ending Treat",
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
        setContentView(R.layout.activity_massage2);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        _imageloader = VaCayApplication.getInstance().getImageLoader();
        mHomeflipper = (ViewFlipper) findViewById(R.id.home_viewflipper);

//        if(Commons._is_beautyBack) {
        getServiceProviderInfo();

        LoadAdviser();

        // animation listener
        mAnimationListener = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.d("Flipper", "start");

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                Log.d("Flipper", "Repeat");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.d("Flipper", "end");

            }
        };
        mTimer.schedule(doAsynchronousTask, 0, 10000);

        checkBox1=(CheckBox)findViewById(R.id.checkboxA);
        checkBox2=(CheckBox)findViewById(R.id.checkboxB);
        checkBox3=(CheckBox)findViewById(R.id.checkboxC);
        checkBox4=(CheckBox)findViewById(R.id.checkboxD);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        ok=(TextView) findViewById(R.id.ok);
        ok.setOnClickListener(this);

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

    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    LoadAdviser();
                }
            });
        }
    };

    private void LoadAdviser() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Commons._beauty_hotshave_set=false;
                Intent intent=new Intent(this,MenBeautyServiceActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
            case R.id.ok:
                if(!checkBox1.isChecked()&&!checkBox2.isChecked()&&!checkBox3.isChecked()&&!checkBox4.isChecked()) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.toast_view, null);
                    TextView textView=(TextView)dialogView.findViewById(R.id.text);
                    textView.setText("Please select the above items.");
                    Toast toast=new Toast(this);
                    toast.setView(dialogView);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                }
                else {
                    Commons.subnewbeautyEntities.clear();
                    beautyEntitiesBuf.clear();
                    beautyEntitiesBuf.addAll(beautyEntities);
                    showProgress();
                    if(checkBox1.isChecked()){ch1=checkBox1.getText().toString(); Log.d("checked===",ch1);}
                    else ch1="";
                    if(checkBox2.isChecked()){ch2=checkBox2.getText().toString();Log.d("checked===",ch2);}
                    else ch2="";
                    if(checkBox3.isChecked()){ch3=checkBox3.getText().toString();Log.d("checked===",ch3);}
                    else ch3="";
                    if(checkBox4.isChecked()){ch4=checkBox4.getText().toString();Log.d("checked===",ch4);}
                    else ch4="";

                    for(int i=0;i<beautyEntitiesBuf.size();i++){
                        String beautyname1 = beautyEntitiesBuf.get(i).getBeautySubName();
                        if ((beautyname1.equals(ch1)) || (beautyname1.equals(ch2)) || (beautyname1.equals(ch3)) || (beautyname1.equals(ch4))) {
                            Commons.subnewbeautyEntities.add(beautyEntitiesBuf.get(i));
                        }

                    }

                    Log.d("Result===",String.valueOf(Commons.subnewbeautyEntities.size()));
                    closeProgress();
                    intent=new Intent(this,BeautyListActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                break;
        }

    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(MenMassageActivity.this, R.anim.left_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(MenMassageActivity.this, R.anim.left_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showNext();

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(MenMassageActivity.this, R.anim.right_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(MenMassageActivity.this,R.anim.right_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
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

                JSONArray providers = response.getJSONArray("service_provider_info");
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
                    beautyEntity.setPhone(jsonBeauty.getString("proPhone"));
                    beautyEntity.setCity(jsonBeauty.getString("proCity"));
                    beautyEntity.setLocation(jsonBeauty.getString("proAddress"));
                    beautyEntity.setCompanyName(jsonBeauty.getString("proCompany"));
                    beautyEntity.setToken(jsonBeauty.getString("proToken"));
                    beautyEntity.setAvailable(jsonBeauty.getString("proAvailable"));

                    beautyEntity.setServicePercent(jsonBeauty.getString("proServicePercent"));
                    beautyEntity.setSalary(jsonBeauty.getString("proSalary"));
                    beautyEntity.setProductSalePercent(jsonBeauty.getString("proProductSalePercent"));

                    Commons.beautyEntities_net.add(0,beautyEntity);     Log.d("NUMBER===",String.valueOf(Commons.beautyEntities_net.size()));

                }

                if(Commons.beautyEntities_net.isEmpty())showToast("No Services");

                beautyEntities.clear();
                Commons.newBeautyEntitiesBuf.clear();
                Commons.newBeautyEntitiesBuf.addAll(Commons.beautyEntities_net);
                beautyEntities.addAll(Commons.beautyEntities_net);

                Log.d("NUM2====",String.valueOf(beautyEntities.size()));

                //   getAllMenushair(Commons.beautyCategoryId,Commons.beautyAreaId);


                for (i = 0; i <beautyEntities.size(); i++) {

                    if (beautyEntities.get(i).getBeautyImageUrl().length() > 0 ) {
//                        imageView.setImageUrl(beautyEntities.get(i).getBeautyImageUrl(), _imageloader);
                        if (beautyEntities.get(i).getBeautyImageUrl().length()>1000) {
                            final ImageView imageView = new ImageView(this);
                            imageView.setImageBitmap(base64ToBitmap(beautyEntities.get(i).getBeautyImageUrl()));
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            //            imageView.setClickable(true);
                            imageView.setId(1000 + i);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    Commons.newBeautyEntity = beautyEntities.get(v.getId() - 1000);
                                    Intent intent = new Intent(MenMassageActivity.this, NewBeautyServiceDetailActivity.class);
                                    startActivity(intent);
                                }
                            });
                            //                mHomeflipper.removeView(imageView);
                            mHomeflipper.addView(imageView);
                        }
                        else {
                            final NetworkImageView imageView = new NetworkImageView(this);
                            imageView.setImageUrl(beautyEntities.get(i).getBeautyImageUrl(), _imageloader);
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            //            imageView.setClickable(true);
                            imageView.setId(1000 + i);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View v) {
                                    Commons.newBeautyEntity = beautyEntities.get(v.getId() - 1000);
                                    Intent intent = new Intent(MenMassageActivity.this, NewBeautyServiceDetailActivity.class);
                                    startActivity(intent);
                                }
                            });
                            //                mHomeflipper.removeView(imageView);
                            mHomeflipper.addView(imageView);
                        }

                    }
                    mHomeflipper.setAutoStart(true);
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
                    mHomeflipper.setFlipInterval(3000);
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);

                    mHomeflipper.startFlipping();


                    mHomeflipper.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            _detector.onTouchEvent(motionEvent);
                            return true;
                        }
                    });

                }

//                resorting(Commons.beautyEntities_net);


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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

}



