package com.mv.vacay.main.info;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CommonInfoSetupActivity extends BaseActivity {

    LinearLayout menuPortion;
    TextView menuButton, setupButton, helpful, inspiration, praise, wellness, question, companyTitle;
    boolean isOpened=false;
    FrameLayout frame;
    EditText content, link;
    ImageView audioButton;
    TextView submitButton;
    AdView mAdView;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_info_setup);

        TextView title=(TextView)findViewById(R.id.fontableTextView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        title.setTypeface(font);


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
            }
        });
        inspiration=(TextView)findViewById(R.id.inspiration);
        inspiration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(inspiration.getText());
                dismissMenuPortion();
                dismissShadow();
            }
        });

        wellness=(TextView)findViewById(R.id.wellness);
        wellness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(wellness.getText());
                dismissMenuPortion();
                dismissShadow();
            }
        });
        question=(TextView)findViewById(R.id.question);
        question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuButton.setText(question.getText());
                dismissMenuPortion();
                dismissShadow();
            }
        });

        content=(EditText)findViewById(R.id.content);
        link=(EditText)findViewById(R.id.link);
        audioButton=(ImageView)findViewById(R.id.audioButton);
        submitButton=(TextView)findViewById(R.id.submitButton);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        submitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        submitButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        submitButton.setBackground(getDrawable(R.drawable.green_fillround));

                        watercoolerSubmit();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        submitButton.getBackground().clearColorFilter();
                        submitButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

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

    public void shadowLayout(){
        frame.setVisibility(View.VISIBLE);
    }

    public void dismissShadow(){
        frame.setVisibility(View.GONE);
    }

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"en","ko","de","ja","fr"});

//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
//        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn\'t support speech input",Toast.LENGTH_SHORT).show();
        }catch (NullPointerException a) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            content.setText(matches.get(0));

        }
    }

    private void watercoolerSubmit() {
        if(menuButton.getText().equals("Category")){
            showToast("Please select category...");
        }
        else if(content.getText().length()>0)
            uploadWatercoolerInfo();
        else showToast("Please check blank boxes...");
    }

    public void uploadWatercoolerInfo() {

        String url = ReqConst.SERVER_URL + "upload_watercooler";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

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

                params.put("name", Commons.thisEntity.get_name());
                params.put("photo", Commons.thisEntity.get_photoUrl());
                params.put("company", "common");
                params.put("category", menuButton.getText().toString());
                params.put("content", content.getText().toString());
                params.put("link", link.getText().toString());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        closeProgress();
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                Toast.makeText(getApplicationContext(),"Uploaded successfully!",Toast.LENGTH_SHORT).show();
                content.setText("");
                link.setText("");

            }
            else {
                showToast("Uploading failed...");
            }

        } catch (JSONException e) {
            showToast("Uploading failed...");

            e.printStackTrace();
        }

    }
}






































