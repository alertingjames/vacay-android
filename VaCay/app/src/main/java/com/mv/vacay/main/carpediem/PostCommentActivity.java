package com.mv.vacay.main.carpediem;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PostCommentActivity extends AppCompatActivity {

    File file;
    int _idx;
    private ProgressDialog _progressDlg;
    static String date="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment);
        if(!Commons._allow_sendMessage) {
            sendMsg(Commons.gameEntity.getVideoId().toLowerCase());
        }else {
            sendMsg(Commons.messageEntity.get_useremail());
            Commons._allow_sendMessage=false;
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

    public void sendMsg(final String toMail) {

        String message="";
        if(!Commons._allow_sendMessage){
            message=Commons.now_watching_message;
        }else message=Commons.comment;

        String url = ReqConst.SERVER_URL + ReqConst.REQ_MAKEMAIL;
        showProgress();

        final String finalMessage = message;
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("Text response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseLoginResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
//                closeProgress();
                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

//                params.put("from_mail", Commons.thisEntity.get_email());
//                params.put("to_mail", Commons.userEntity.get_email().replace(" ","%20"));

                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", toMail);  Log.d("ToMail===>",toMail);
                params.put("text_message",
                        finalMessage
//                        .toString()
//                        .replace(" ","%20")
//                        .replace(" & ","%26")
//                        .replace(",","-")
                );
                try {
                    if(Commons.thisEntity.get_userlat()!=0.0f && Commons.thisEntity.get_userlng()!=0.0f) {
                        params.put("lon_message", String.valueOf(Commons.thisEntity.get_userlat()));
                        params.put("lat_message", String.valueOf(Commons.thisEntity.get_userlng()));
                    }
                    else {
                        params.put("lon_message", "null");
                        params.put("lat_message", "null");
                    }
                }catch (NullPointerException e){}

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);


    }

    public void parseLoginResponse(String json) {

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);   Log.d("RRRRResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("RRResultcode=====> :",success);

            if (success==String.valueOf(0)) {
                _idx=response.getInt("mail_id");

                try {
                    if (Commons.imageUrl.length() > 0) {
                        file = new File(Commons.imageUrl);
                        uploadImage();
                    }
                    else if (!Commons.file.equals(null)){
                        file = Commons.file;
                        uploadImage();
                    }else {
                        makeMail(_idx);
                    }


                }catch (NullPointerException e){
                    makeMail(_idx);
                }


            }
            else{

                String error = response.getString(ReqConst.RES_ERROR);

                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
                //    Toast.makeText(getContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();;
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImage() {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(4));

            String url = ReqConst.SERVER_URL + ReqConst.REQ_UPLOADPHOTO;
            //           showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(),"Registration to server failed.",Toast.LENGTH_SHORT).show();

                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse(json);
                    Log.d("imageJson===",json.toString());
                    Log.d("params====",params.toString());
                }
            }, file, ReqConst.PARAM_FILE, params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VaCayApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            //           closeProgress();
            Toast.makeText(getApplicationContext(),"Registration to server failed.",Toast.LENGTH_SHORT).show();
        }
    }


    public void ParseUploadImgResponse(String json) {

        //       closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("RESULT===",String.valueOf(result_code));

            if (result_code == 0) {
                Commons.imageUrl="";
                Commons.file=null;
                Commons.requestLatlng=null;
                if(!Commons.comment.equals("")) Commons.comment="";

                makeMail(_idx);

            } else if(result_code==102){
                Toast.makeText(getApplicationContext(),"Unregistered user.",Toast.LENGTH_SHORT).show();
            }else if(result_code==103){
                Toast.makeText(getApplicationContext(),"Upload file size error.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Picture upload to server failed.",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Picture upload to server failed.",Toast.LENGTH_SHORT).show();
        }
    }

    public void makeMail(int mail_id) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_SENDMAIL;

        String params = String.format("/%s",mail_id);
        url += params;

        showToast("Your comment sent successfully");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                //           parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
//        Intent intent=new Intent(this,VideoDisplayActivity.class);
//        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }
}
