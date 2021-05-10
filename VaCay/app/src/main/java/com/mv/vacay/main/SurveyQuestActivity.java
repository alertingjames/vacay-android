package com.mv.vacay.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
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
import com.mv.vacay.classes.Notification;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SurveyQuestActivity extends AppCompatActivity implements View.OnClickListener{

    CheckBox checkbox4,checkbox1,checkbox2,checkbox3,checkbox5,checkbox6,checkbox7;
    TextView ok;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_quest);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(this);

        Commons.survey_quest="";

        checkbox1=(CheckBox) findViewById(R.id.checkboxA);

        checkbox2=(CheckBox) findViewById(R.id.checkboxB);

        checkbox3=(CheckBox) findViewById(R.id.checkboxC);

        checkbox4=(CheckBox) findViewById(R.id.checkboxD);

        checkbox5=(CheckBox) findViewById(R.id.checkboxE);

        checkbox6=(CheckBox) findViewById(R.id.checkboxF);

        checkbox7=(CheckBox) findViewById(R.id.checkboxG);

        if(Commons.thisEntity.get_adminId()>0){
            checkbox2.setText(getString(R.string.questb));
            checkbox3.setText(getString(R.string.questc));
            checkbox4.setText(getString(R.string.questdd));
        }

        Log.d("SURVEY===",Commons.survey_quest);

        getMyProfile(Commons.thisEntity.get_email());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok:
                if(checkbox1.isChecked()||checkbox2.isChecked()||checkbox3.isChecked()||checkbox4.isChecked()||checkbox5.isChecked()||checkbox6.isChecked()||checkbox7.isChecked()){
                    if(checkbox1.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox1.getText().toString()+"\n";}
                    if(checkbox2.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox2.getText().toString()+"\n";}
                    if(checkbox3.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox3.getText().toString()+"\n";}
                    if(checkbox4.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox4.getText().toString()+"\n";}
                    if(checkbox5.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox5.getText().toString()+"\n";}
                    if(checkbox6.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox6.getText().toString()+"\n";}
                    if(checkbox7.isChecked()){Commons.survey_quest=Commons.survey_quest+checkbox7.getText().toString()+"\n";}

//=====================================================////=========================================================================================================================
//                   Commons.thisEntity.set_firstName("Cayley");
//                   Commons.thisEntity.set_lastName("W.");
//                   Commons.thisEntity.set_age_range("25-30");
//                   Commons.thisEntity.set_city("San Francisco");
//                    Commons.thisEntity.set_imageRes(R.drawable.cayley);
//                   Commons.thisEntity.set_education("Yale");
//                   Commons.thisEntity.set_interest("Hockey, Tennis, Reading");
//====================================================////============================================================================================================================

  //                  Commons.thisEntity.set_job("Senior Mobile developer at Canada Computers & Electronics");

                   Commons.thisEntity.set_survey_quest(Commons.survey_quest);

//                    if(Commons._is_vendorSelect) {
//                        intent=new Intent(this,VirtualSignupActivity.class);
//                        Commons._is_vendorSelect=false;
//                    }
//                    else

                    intent=new Intent(this,SignupActivity.class);
                    startActivity(intent);
                   finish();
                   overridePendingTransition(R.anim.right_in,R.anim.left_out);
               }else {
                    LayoutInflater inflater = this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.toast_view, null);
                    TextView textView=(TextView)dialogView.findViewById(R.id.text);
                    textView.setText("Please select any boxes that apply.");
                    Toast toast=new Toast(this);
                    toast.setView(dialogView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
        }

    }

    public void getMyProfile(final String email) {

        String url = ReqConst.SERVER_URL + "getUserProfile";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetBeautiesResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetBeautiesResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);  Log.d("Json===>",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code.equals("0")){
                showAlertDialog("Would you update your profile?");
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void onSkipRegister() {

        Preference.getInstance().put(this,
                PrefConst.PREFKEY_USEREMAIL, Commons.thisEntity.get_email());
        Preference.getInstance().put(this,
                PrefConst.PREFKEY_EMPLOYEEADMINID, Commons.thisEntity.get_adminId());
        if(Commons.thisEntity.get_adminId()==0)
            Preference.getInstance().put(this,
                    PrefConst.PREFKEY_EMPLOYEEID, 0);

        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();overridePendingTransition(0,0);
    }

    public void showAlertDialog(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setIcon(R.drawable.noti);
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "No, Skip",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onSkipRegister();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }
}



























