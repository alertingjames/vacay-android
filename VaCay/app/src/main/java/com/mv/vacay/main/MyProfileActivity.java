package com.mv.vacay.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MyProfileActivity extends BaseActivity {

    TextView txv_firstname,txv_lastname, firstname, lastname, city, job, education, interest, agerange,answer_survey,relationship, millennial;
    ImageView location,back;
    CircularNetworkImageView user_photo_net;
    CircularImageView user_photo;
    private ImageLoader _imageLoader;
    LinearLayout lyt_millennial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        firstname=(TextView)findViewById(R.id.firstname);
        firstname.setText(Commons.thisEntity.get_firstName());

        lastname=(TextView)findViewById(R.id.lastname);
        lastname.setText(Commons.thisEntity.get_lastName());

        txv_firstname=(TextView)findViewById(R.id.txv_firstname);
        txv_firstname.setText(Commons.thisEntity.get_firstName());

        txv_lastname=(TextView)findViewById(R.id.txv_lastname);
        txv_lastname.setText(Commons.thisEntity.get_lastName());
        answer_survey=(TextView)findViewById(R.id.answer_survey);
        answer_survey.setText(Commons.thisEntity.get_survey_quest());
        answer_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SurveyAnswerViewActivity.class);
                startActivity(intent);
            }
        });

        location=(ImageView) findViewById(R.id.location);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Please wait...");
//                showProgress();
                Intent intent=new Intent(getApplicationContext(),MyLocationViewActivity.class);
                startActivity(intent);
                closeProgress();
            }
        });

        agerange=(TextView)findViewById(R.id.age_range);
        agerange.setText(Commons.thisEntity.get_age_range());

        city=(TextView)findViewById(R.id.city);
        city.setText(Commons.thisEntity.get_city());

        job=(TextView)findViewById(R.id.job);
        job.setText(Commons.thisEntity.get_job());

        education=(TextView)findViewById(R.id.education);
        education.setText(Commons.thisEntity.get_education());

        interest=(TextView)findViewById(R.id.interest);
        interest.setText(Commons.thisEntity.get_interest());    Log.d("InterestAABB===>",interest.getText().toString());

        relationship=(TextView)findViewById(R.id.relations);

        if(Commons.thisEntity.get_relations().contains("common")) {
            TextView deptitle=(TextView)findViewById(R.id.deptitle);
            deptitle.setText("Info:");
            Commons.thisEntity.set_relations("-"+Commons.thisEntity.get_relations().replace("\ncommon", "").replace("\n","\n-"));
        }

        relationship.setText(Commons.thisEntity.get_relations());

        lyt_millennial=(LinearLayout)findViewById(R.id.lyt_millennial);
        millennial=(TextView)findViewById(R.id.millennial);

        if(Commons.thisEntity.get_adminId() > 0){
            lyt_millennial.setVisibility(View.VISIBLE);
            millennial.setText(Commons.thisEntity.getMillennial());
        }else lyt_millennial.setVisibility(View.GONE);

        user_photo_net=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);
        user_photo=(CircularImageView) findViewById(R.id.imv_photo);

        if (Commons.thisEntity.get_photoUrl().length() < 1000) {
            user_photo_net.setVisibility(View.VISIBLE);
            user_photo_net.setImageUrl(Commons.thisEntity.get_photoUrl(), _imageLoader);
        }
        else {
            user_photo_net.setVisibility(View.GONE);
            user_photo.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public void getMyProfile(String email) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETUSERPROFILE;

        String params = String.format("/%s",email);
        url += params;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetBeautiesResponse(String json) {

        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray userInfo = response.getJSONArray(ReqConst.RES_USERPROFILE);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("providers===",userInfo.toString());

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME));
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(user.get_age_range());
                    int age=year-birthyear;
                    user.set_age_range(String.valueOf(age));   Log.d("AGE===",user.get_age_range());
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS));
                    user.setMillennial(jsonUser.getString("em_millennial"));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    Commons.thisEntity=user;

                }

            }else if(result_code == 108){
                showToast("Unknown user.");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

}
