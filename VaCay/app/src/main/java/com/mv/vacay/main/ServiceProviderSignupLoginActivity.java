package com.mv.vacay.main;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.firebase.client.Firebase;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.provider.SProviderHomeActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.widgets.KenBurnsActivity;
import com.mv.vacay.widgets.KenBurnsView;
import com.mv.vacay.widgets.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServiceProviderSignupLoginActivity extends KenBurnsActivity implements KenBurnsView.TransitionListener {

    private static final String TAG = LoginRActivity.class.getSimpleName();

    private static final int TRANSITIONS_TO_SWITCH = 3;
    private ViewSwitcher mViewSwitcher;
    private int mTransitionsCount = 0;
    EditText email, pwd,emailConfirm,fullName,cityName,companyName;
    TextView login,forgotPassword,verify;
    TextInputLayout cityContainer;
    LinearLayout loginFrame,confirmFrame;
    String emailInitial = "", pwdInitial = "",emailStr="aaa@gmail.com", pwdStr="aaa",
            fullNameStr="aaa", cityStr="aaa", companyStr="aaa", availableStr="";
    PermissionHelper permissionHelper;
    int adminid=0, proid=0; String address="", token="",first="", last="", image="";
    int emAdminid=0, emId=0; String emName="",
            emGender="",emEmail="", emPassword="", emImage="", emMillennial="", emGivenBuck="",emUsedBuck="", emInteraction="";


    private ProgressDialog _progressDlg;
    int _idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        Commons.thisEntity=new UserEntity();
        permissionHelper = PermissionHelper.getInstance(this);

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

//        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
//        img2.setTransitionListener(this);

        KenBurnsView img3 = (KenBurnsView) findViewById(R.id.img3);
        img3.setTransitionListener(this);
        if(Commons._is_employeeSelect)emailInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_EMPLOYEEEMAIL, "");
        else emailInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_SPROVIDEREMAIL, "");
        pwdInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_SPROVIDERPWD, "");

        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.password);

        emailConfirm = (EditText) findViewById(R.id.emailConfirm);
        fullName = (EditText) findViewById(R.id.fullName);
        cityName = (EditText) findViewById(R.id.cityName);

        cityContainer=(TextInputLayout)findViewById(R.id.cityContainer);
        if(Commons._is_employeeSelect) cityContainer.setVisibility(View.GONE);

        companyName = (EditText) findViewById(R.id.companyName);

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmFrame.setVisibility(View.GONE);
            }
        });

        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        forgotPassword.setTextColor(ColorStateList.valueOf(0xF9FF0213));
                        forgotPassword.setTextSize(16);

                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        forgotPassword.setTextColor(ColorStateList.valueOf(0x7fff0284));
                        forgotPassword.setTextSize(14);

                        showAlertDialogLogin();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        //            ui_txvvendor.getBackground().clearColorFilter();
                        forgotPassword.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        login = (TextView) findViewById(R.id.email_sign_in_button);
        if(Commons._is_employeeSelect)login.setText("Login as an employee");
        login.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        login.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        login.setBackground(getDrawable(R.drawable.login_button_fillrect));

                        if (pwd.getText().length() > 0 && email.getText().length() > 0 && email.getText().toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*"
                                        + "(\\.[A-Za-z]{2,})$")) {
                            if(Commons._is_employeeSelect)loginEmployee();
                            else login();
                        } else if (email.getText().length() == 0) {
                            showToast("Please input your email.");
                        } else if (pwd.getText().length() == 0) {
                            showToast("Please input your password.");
                        }else if (!email.getText().toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*"
                                + "(\\.[A-Za-z]{2,})$")) {
                            email.setError("Your email is disabled.");
                            showToast("Your email is disabled.");
                        }
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        login.getBackground().clearColorFilter();
                        login.invalidate();
                        break;
                    }
                }

                return true;
            }

        });

        verify = (TextView) findViewById(R.id.confirm);
        verify.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        verify.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        verify.setBackground(getDrawable(R.drawable.login_button_fillrect));
                        if(Commons._is_employeeSelect && fullName.getText().length() > 0 && emailConfirm.getText().length() > 0 &&
                                companyName.getText().length() > 0) confirmEmployee();
                        if (!Commons._is_employeeSelect && fullName.getText().length() > 0 && emailConfirm.getText().length() > 0 &&
                                cityName.getText().length() > 0 && companyName.getText().length() > 0) {
                            confirm();
                        } else {
                            showToast("Please check blank field.");
                        }
                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        verify.getBackground().clearColorFilter();
                        verify.invalidate();
                        break;
                    }
                }

                return true;
            }

        });

        confirmFrame = (LinearLayout) findViewById(R.id.email_confirm_form);
        loginFrame = (LinearLayout) findViewById(R.id.email_login_form);
        loginFrame.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        loginFrame.startAnimation(animation);
        if(emailInitial.length()>0)email.setText(emailInitial);
        if(pwd.getText().length()>0)pwd.setText("");
    }

    private void loginEmployee() {
        String url = ReqConst.SERVER_URL + "getEmployeeByEmail";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponseEmployee(response);

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

                params.put("em_email", email.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponseEmployee(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseProvider====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray userInfo = response.getJSONArray("employee_info");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("providers===",userInfo.toString());

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    emId = jsonUser.getInt("em_id");
                    emAdminid = jsonUser.getInt("adminID");
                    emName= jsonUser.getString("em_name");
                    emEmail=jsonUser.getString("em_email");
                    emImage=jsonUser.getString("em_image");
                    emPassword=jsonUser.getString("em_password"); Log.d("EMAIL===>",emEmail+"/"+email.getText());
                    emGender=jsonUser.getString("em_gender");
                    companyStr=jsonUser.getString("adminCompany");
                    emMillennial=jsonUser.getString("em_millennial");
                    emGivenBuck=jsonUser.getString("em_givenbuck");
                    emUsedBuck=jsonUser.getString("em_usedbuck"); Log.d("PWD===>",emPassword+"/"+pwd.getText());
                    emInteraction=jsonUser.getString("em_interaction");
                    if(emInteraction.length()==0)emInteraction="0";


                    if(email.getText().toString().toLowerCase().equals(emEmail.toLowerCase()) && pwd.getText().toString().equals(emPassword)){
                        break;

                    }else if (!email.getText().toString().toLowerCase().equals(emailStr.toLowerCase()))
                        showToast("Your email is incorrect. Please reinput your email.");
                    else if(!pwd.getText().equals(pwdStr)) {
                        Log.d("PWD===>",pwdStr+"/"+pwd.getText().toString());
                        showToast("Your password is incorrect. Please reinput your password.");
                    }

                }

                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_EMPLOYEEPWD, pwd.getText().toString());
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_EMPLOYEEEMAIL, emEmail);
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_EMPLOYEEID, emId);
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_EMPLOYEEADMINID, emAdminid);
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_EMPLOYEECOMPANY, companyStr);

                String myName=emName;
                String myFirst="";
                String myLast="";

                if(myName.contains(" ")){
                    if(myName.indexOf(" ")>=1) {
                        myFirst = myName.substring(0, myName.indexOf(" "));
                        myLast=myName.substring(myName.indexOf(" ")+1,myName.length());
                    }
                    else {
                        myFirst=myName;
                        myLast="";
                    }
                }else {
                    myFirst=myName;
                    myLast="";
                }

                Commons.thisEntity.set_adminId(emAdminid);  Log.d("MyAdminId===>",String.valueOf(Commons.thisEntity.get_adminId()));
                Commons.thisEntity.set_firstName(myFirst);
                Commons.thisEntity.set_lastName(myLast);
                Commons.thisEntity.set_email(emEmail);
                Commons.thisEntity.setGender(emGender);
                Commons.thisuserGender=emGender;
                Commons.thisEntity.set_photoUrl(emImage);
                Commons.thisEntity.setMillennial(emMillennial);
                Commons.thisEntity.setVacayBucksGiven(emGivenBuck);
                Commons.thisEntity.setVacayBucksUsed(emUsedBuck);
                Commons.thisEntity.setInteractions(emInteraction);
                Commons.thisEntity.setCompany(companyStr);


                showToast("Success!");
                Intent intent=new Intent(getApplicationContext(), SurveyQuestActivity.class);

                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
            else {

                showToast("Login failed...");
            }

        } catch (JSONException e) {

            showToast("Server connection failed...");

            e.printStackTrace();
        }

    }

    public void confirm(){

        if(emailConfirm.getText().toString().toLowerCase().equals(emEmail.toString().toLowerCase()) &&
                fullName.getText().toString().toLowerCase().equals(fullNameStr.toString().toLowerCase()) &&
                cityName.getText().toString().toLowerCase().equals(cityStr.toString().toLowerCase()) &&
                companyName.getText().toString().toLowerCase().equals(companyStr.toString().toLowerCase())){
            showAlertDialog("Your password: "+pwdStr);
        }else if(!emailConfirm.getText().equals(emailStr))showToast("Your email is incorrect. Please reinput your email.");
        else if(!fullName.getText().equals(fullNameStr))showToast("Your name is incorrect. Please reinput your name.");
        else if(!cityName.getText().equals(cityStr))showToast("Your city name is incorrect. Please reinput your city name.");
        else if(!companyName.getText().equals(companyStr))showToast("Your company name is incorrect. Please reinput your company name.");
    }

    public void confirmEmployee(){

        Log.d("Confirm===>",emailConfirm.getText().toString()+"/"+emEmail);
        if(emailConfirm.getText().toString().toLowerCase().equals(emEmail.toString().toLowerCase()) &&
                fullName.getText().toString().toLowerCase().equals(emName.toString().toLowerCase()) &&
                companyName.getText().toString().toLowerCase().equals(companyStr.toString().toLowerCase())){
            showAlertDialog("Your password: "+emPassword);
        }else if(!emailConfirm.getText().toString().toLowerCase().equals(emEmail.toString().toLowerCase()))showToast("Your email is incorrect. Please reinput your email.");
        else if(!fullName.getText().toString().toLowerCase().equals(emName.toString().toLowerCase()))showToast("Your name is incorrect. Please reinput your name.");
        else if(!companyName.getText().toString().toLowerCase().equals(companyStr.toString().toLowerCase()))showToast("Your company name is incorrect. Please reinput your company name.");
    }


    public void showAlertDialog(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        confirmFrame.setVisibility(View.GONE);
                        pwd.setText(pwdStr);
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogLogin() {

        final String[] items = {"I am approved but I forgot the password", "I haven't received my password yet", "I will check my email"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you approved to login by receiving the password from your manager?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    confirmFrame.setVisibility(View.VISIBLE);
                }
                else if(item==1){

                }
                else {

                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void login() {

        String url = ReqConst.SERVER_URL + "getProviderByProEmail";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse1(response);

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

                params.put("proEmail", email.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseProvider====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray userInfo = response.getJSONArray("provider_info");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("providers===",userInfo.toString());

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    proid = jsonUser.getInt("proid");
                    adminid = jsonUser.getInt("adminID");
                    first= jsonUser.getString("proFirstName");
                    last=jsonUser.getString("proLastName");
                    fullNameStr=first+" "+last;
                    image=jsonUser.getString("proProfileImageUrl");
                    emailStr=jsonUser.getString("proEmail"); Log.d("EMAIL===>",emailStr+"/"+email.getText());
                    cityStr=jsonUser.getString("proCity");
                    companyStr=jsonUser.getString("proCompany");
                    address=jsonUser.getString("proAddress");
                    pwdStr=jsonUser.getString("proPassword"); Log.d("PWD===>",pwdStr+"/"+pwd.getText());
                    token=jsonUser.getString("proToken");


                    if(email.getText().toString().toLowerCase().equals(emailStr.toLowerCase()) && pwd.getText().toString().equals(pwdStr)){
                        break;

                    }else if (!email.getText().toString().toLowerCase().equals(emailStr.toLowerCase())) {
                        showToast("Your email is incorrect. Please reinput your email.");
                        return;
                    }
                    else if(!pwd.getText().toString().trim().equals(pwdStr.trim())) {
                        Log.d("PWD===>",pwdStr+"/"+pwd.getText().toString());
                        showToast("Your password is incorrect. Please reinput your password.");
                        return;
                    }

                }

                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_SPROVIDERPWD, pwd.getText().toString());
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_SPROVIDEREMAIL, emailStr);
                Preference.getInstance().put(getApplicationContext(),
                        PrefConst.PREFKEY_SPROVIDERID, String.valueOf(proid));

                registerChatRoom(emailStr,image,fullNameStr);
                showToast("Success!");

                Intent intent=new Intent(getApplicationContext(), SProviderHomeActivity.class);
                intent.putExtra("proid",String.valueOf(proid));
                intent.putExtra("adminid",String.valueOf(adminid));
                intent.putExtra("first",first);
                intent.putExtra("last",last);
                intent.putExtra("full",fullNameStr);
                intent.putExtra("image",image);
                intent.putExtra("email",emailStr);
                intent.putExtra("city",cityStr);
                intent.putExtra("company",companyStr);
                intent.putExtra("address",address);
                intent.putExtra("password",pwdStr);
                intent.putExtra("accountid",token);

                if(token.length()>0) intent.putExtra("verified","Verified");
                else intent.putExtra("verified","unVerified");
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);

            }
            else {

                showToast("Login failed...");
            }

        } catch (JSONException e) {

            showToast(getString(R.string.register_fail));

            e.printStackTrace();
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

    @Override
    protected void onPlayClick() {

        KenBurnsView currentImage = (KenBurnsView) mViewSwitcher.getCurrentView();
        currentImage.resume();
    }

    @Override
    protected void onPauseClick() {
        KenBurnsView currentImage = (KenBurnsView) mViewSwitcher.getCurrentView();
        currentImage.pause();
    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {

        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
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

    public void registerChatRoom(final String email,final String photoUrl, final String name){

        String url = ReqConst.FIREBASE_DATABASE_URL+"users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"users/"+email.replace(".com","").replace(".","ddoott"));

                if(s.equals("null")) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("email", email);

                    map.put("name", name);

                    map.put("photo", photoUrl);

                    reference.push().setValue(map);
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(email.replace(".com","").replace(".","ddoott"))) {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("email", email);

                            map.put("name", name);

                            map.put("photo", photoUrl);

                            reference.push().setValue(map);
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(ServiceProviderSignupLoginActivity.this);
        rQueue.add(request);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

}





































































