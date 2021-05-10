package com.mv.vacay.main.beauty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.beautymen.MenBeautyServiceActivity;
import com.mv.vacay.main.beautymen.NewBeautyServiceDetailActivity;
import com.mv.vacay.models.BeautyServiceEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BeautyWelcomeActivity extends AppCompatActivity {

    ProgressDialog _progressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_welcome);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        TextView men=(TextView)findViewById(R.id.male);
        men.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._gender_flag=1;
                Intent intent=new Intent(getApplication(),MenBeautyServiceActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fade,R.anim.space);
            }
        });
        TextView women=(TextView)findViewById(R.id.female);
        women.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._gender_flag=0;
                Intent intent=new Intent(getApplication(),BeautyServiceEntryActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.fade,R.anim.fade_off);
            }
        });
        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(),HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        updateAllProviderSchedules();
    }

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", this.getString(R.string.loading),true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            _progressDlg.dismiss();
            _progressDlg = null;
        }
    }

    public void updateAllProviderSchedules() {

        String url = ReqConst.SERVER_URL + "updateAllProviderSchedules";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                parseRestUrlsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                closeProgress();
                Log.d("debug", error.toString());
                showToast(getString(R.string.error));
            }
        }) {  };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

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

    public void parseRestUrlsResponse(String json) {
        closeProgress();

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS){

                Toast.makeText(getApplicationContext(),"All providers have been updated!", Toast.LENGTH_SHORT).show();
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
    public void onBackPressed() {
        Intent intent=new Intent(getApplication(),HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }
}
































