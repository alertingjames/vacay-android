package com.mv.vacay.notification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.models.UserEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindNotiUserActivity extends AppCompatActivity {

    private ProgressDialog _progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_noti_user);

        getAllUsers();
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

    public void getAllUsers() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETALLUSERS;

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                showToast(getString(R.string.error));
                finish();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)};

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                double[] lats={39.5530507,39.7380517,39.5542507,39.5501507};
                double[] lngs={-105.78306,-104.99506,-105.78706,-105.78406};
                String[] publicNames={"Colorado","denver","colorado","colorado"};
                String[] relationships={"In Relationship","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID));
                    Log.d("USERID===", String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME));
//                    final Calendar c = Calendar.getInstance();
//                    int year = c.get(Calendar.YEAR);
//                    int birthyear=Integer.parseInt(jsonUser.getString(ReqConst.RES_AGE));
//                    int age=year-birthyear;
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("ppp", "(").replace("qqq", ")").replace("separate", ",\n"));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            + surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            + surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            + surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            + surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    user.set_relations(jsonUser.getString("relationship"));
                    user.set_publicName(jsonUser.getString("place_name"));
                    user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon")));
                    user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat")));

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    if (!user.get_age_range().equals("0") && user.get_email().equals(Commons.notiEmail)) {
                        Commons.userEntity=user;
                        Commons.notiEmail="";
                        Intent intent=new Intent(this, ChatActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);
                        break;
                    }
                }
        //        showToast("No found such an user.");
                finish();

            } else {
                showToast(getString(R.string.error));
                finish();
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
            finish();
        }
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


}
