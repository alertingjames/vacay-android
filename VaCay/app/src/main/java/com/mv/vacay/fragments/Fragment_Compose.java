package com.mv.vacay.fragments;

/**
 * Created by a on 2016.11.05.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.main.SelectDateTimeActivity;
import com.mv.vacay.main.activity.GolfActivity;
import com.mv.vacay.main.activity.RunningActivity;
import com.mv.vacay.main.activity.SkiingSnowboardingActivity;
import com.mv.vacay.main.activity.TennisActivity;
import com.mv.vacay.main.meetfriends.MeetFriendActivity;
import com.mv.vacay.main.meetfriends.SpeechMessageActivity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a on 2016.11.05.
 */
public class Fragment_Compose extends Fragment{
    EditText sendemail,sendmessage,speechMessage;
    TextView sendbutton;
    ImageView imagemessage,speechButton,showMessage,addMessage;
    ArrayList<MessageEntity> _datas=new ArrayList<>();
    ArrayList<MessageEntity> _datas_temp=new ArrayList<>();
    List<String> titlesort=new ArrayList<>();
    boolean _arrange_flag=false,sentMail=false;
    private ProgressDialog _progressDlg;
    File file;
    int _idx;
    TextView DateEdit;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    DBManager dbManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_compose, container, false);

        Commons._is_composeDateBack=false;

        dbManager = new DBManager(getActivity());
        dbManager.open();

        sendemail=(EditText) v.findViewById(R.id.sendemail);
        try {
            sendemail.setText(Commons.userEntity.get_email());
        } catch (NullPointerException e) {
        }
        sendmessage=(EditText) v.findViewById(R.id.sendmessage);
        if(Commons._request_set) {
            sendemail.setText(Commons.newBeautyEntity.getEmail());
            sendmessage.setText(Commons.requestMessage);
            Commons.messageForDate=Commons.requestMessage;
        }
        sendmessage.setText(Commons.messageForDate);
        ImageView deleteMessage=(ImageView)v.findViewById(R.id.deletemessage);
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage.setText("");
            }
        });

        ImageView deleteEmail=(ImageView)v.findViewById(R.id.deleteemail);
        deleteEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendemail.setText("");
            }
        });

        DateEdit = (TextView)v. findViewById(R.id.sendate);

        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons._calendar_set=true;
                Commons.messageForDate=sendmessage.getText().toString();
                Intent intent=new Intent(getActivity(), SelectDateTimeActivity.class);
                startActivity(intent);
            }
        });

        imagemessage=(ImageView)v.findViewById(R.id.messageImage);
        speechMessage=(EditText)v.findViewById(R.id.speechmessage);
        showMessage=(ImageView)v.findViewById(R.id.viewspeechmessage);
        showMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.speechMessage.length()>0){
                    speechMessage.setText(Commons.speechMessage);
                }
            }
        });
        addMessage=(ImageView)v.findViewById(R.id.addspeechmessage);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons.speechMessage.length()>0){
                    if(sendmessage.getText().length()>0)
                        sendmessage.setText(sendmessage.getText().toString()+"\n"+Commons.speechMessage);
                    else sendmessage.setText(Commons.speechMessage);
                    speechMessage.setText("");
                }
            }
        });

        Commons.imageView=imagemessage;
        try{
            imagemessage.setImageBitmap(Commons.bitmap);
        }catch (NullPointerException e){

        }

        sendbutton=(TextView)v.findViewById(R.id.sendbutton);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sendmessage.getText().toString().length()==0||sendemail.getText().toString().length()==0) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    final View dialogView = inflater.inflate(R.layout.toast_view, null);
                    TextView textView=(TextView)dialogView.findViewById(R.id.text);
                    textView.setText("Please input your message to send to your friend.");
                    Toast toast=new Toast(getContext());
                    toast.setView(dialogView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if(sendmessage.getText().toString().length()>0 && sendemail.getText().toString().length()>0) {
                    if(sendemail.getText().toString().equals(Commons.thisEntity.get_email())){
                        showAlertDialog("Please select a person to send to.");
                    }else {
                        sendMsg();
                    }
                }
            }
        });

        speechButton=(ImageView)v.findViewById(R.id.search_button);

        return v;
    }

    public static Fragment_Compose newInstance(String text) {

        Fragment_Compose f = new Fragment_Compose();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        speechDescription.setVisibility(View.VISIBLE);
                Intent intent=new Intent(getContext(), SpeechMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showProgress() {
//        closeProgress();
        _progressDlg = ProgressDialog.show(getActivity(), "", this.getString(R.string.loading),true);
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

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogInteraction(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getActivity().getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        registerInteraction();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void sendMsg() {


        String url = ReqConst.SERVER_URL + ReqConst.REQ_MAKEMAIL;
        showProgress();


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
                closeProgress();
                showAlertDialog("Connection to server failed.");

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                String message=sendmessage.getText().toString();

                if(Commons.loc_url!=""){
                    message=message+"\nLocation for VaCay:\n"+Commons.loc_url;
                    Commons.loc_url="";
                }

                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String sendDateTime = formatter.format(date);

                params.put("name", Commons.thisEntity.get_name());
                params.put("photo_url", Commons.thisEntity.get_photoUrl());
                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", sendemail.getText().toString().trim());
                if(Commons._datetime.length()>0) {
                    params.put("request_date", sendDateTime);
                    params.put("text_message", message);
                }
                else {
                    params.put("request_date", sendDateTime);
                    params.put("text_message", message);
                }
                params.put("service", "no_service");
                params.put("service_reqdate", "");

                try {
                    if(Commons.requestLatlng!=null) {
                        params.put("lon_message", String.valueOf(Commons.requestLatlng.longitude));
                        params.put("lat_message", String.valueOf(Commons.requestLatlng.latitude));
                    }
                    else {
                        params.put("lon_message", "0.0");
                        params.put("lat_message", "0.0");
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

            if (success.equals("0")) {
                _idx=response.getInt("mail_id");

                Commons.messageForDate="";
                Commons._datetime="";
                Commons.requestMessage="";
                sendmessage.setText("");
                speechMessage.setText("");
                Commons.speechMessage="";

    //            Log.d("Map Image===>",Commons.file.getPath());

                if (Commons.destination!=null) {
                    file = Commons.destination;
                    uploadImage();
                }
                else if (Commons.file!=null){
                    file = Commons.file;
//                        Log.d("Map Image===>",Commons.file.getPath());
                    uploadImage();
                }else {
                    makeMail(_idx);
                }

            }
            else{

                showAlertDialog(getString(R.string.error));
            //    Toast.makeText(getContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();;
            }

        } catch (JSONException e) {

            e.printStackTrace();
            showAlertDialog(getString(R.string.error));
        }
    }

    public void uploadImage() {

        try {

            final Map<String, String> params = new HashMap<>();
            params.put("mail_id", String.valueOf(_idx));

            String url = ReqConst.SERVER_URL + "uploadMailPhoto";
            showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    showAlertDialog(getString(R.string.photo_upload_fail));

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
            closeProgress();
            showAlertDialog(getString(R.string.photo_upload_fail));
        }
    }


    public void ParseUploadImgResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            String result_code = response.getString(ReqConst.RES_CODE);
            Log.d("RESULT===",String.valueOf(result_code));

            if (result_code.equals("0")) {
                Commons.imageUrl="";
                Commons.destination=null;
                Commons.file=null;
                Commons.requestLatlng=null;

                makeMail(_idx);
                showAlertDialog("Message sent!");
            }
            else {
                showAlertDialog(getString(R.string.photo_upload_fail));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showAlertDialog(getString(R.string.photo_upload_fail));
        }
    }

    public void makeMail(final int mail_id) {

        String url = ReqConst.SERVER_URL + "sendMailMessage";

        showProgress();
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseSendMailResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("mail_id", String.valueOf(mail_id));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseSendMailResponse(String json) {

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===", response.toString());

            String result_code = response.getString("result");

            if (result_code.equals("0")) {

                for(int i=0;i<dbManager.getAllMembers().size();i++){
                    if(dbManager.getAllMembers().get(i).get_email().equals(Commons.userEntity.get_email())){
                        dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
                    }
                }

                if(Commons.userEntity.get_name().length()>0)
                    dbManager.insert(Commons.userEntity.get_name(), Commons.userEntity.get_email(),Commons.userEntity.get_photoUrl(),"0");
                else if(Commons.userEntity.get_fullName().length()>0)
                    dbManager.insert(Commons.userEntity.get_fullName(), Commons.userEntity.get_email(),Commons.userEntity.get_photoUrl(),"0");

                String myEmployeeId=String.valueOf(Preference.getInstance().getValue(getActivity(), PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
                if(!myEmployeeId.equals("0"))
                    showAlertDialogInteraction("Your message sent successfully");
                else showAlertDialog("Your message sent successfully");
            }
        }catch (JSONException e){

        }
    }

    public void reportResult(){

        for(int i=0;i<100;i++) for(int j=0;j<100;j++){imagemessage.setImageBitmap(Commons.bitmap);}

    //    Toast.makeText(getContext(),"Success!!!",Toast.LENGTH_SHORT).show();;

        if(Commons._golf_activity){
            Intent intent=new Intent(getActivity(),GolfActivity.class);
            startActivity(intent);
        }else if(Commons._run_activity){
            Intent intent=new Intent(getActivity(),RunningActivity.class);
            startActivity(intent);
        }else if(Commons._ski_activity){
            Intent intent=new Intent(getActivity(),SkiingSnowboardingActivity.class);
            startActivity(intent);
        }else if(Commons._tennis_activity){
            Intent intent=new Intent(getActivity(),TennisActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getActivity(), MeetFriendActivity.class);
            startActivity(intent);
        }
    }

    private void registerInteraction() {

        String url = ReqConst.SERVER_URL + "addEmInteraction";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseInteractionResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showAlertDialog(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("em_id", String.valueOf(Preference.getInstance().getValue(getActivity(), PrefConst.PREFKEY_EMPLOYEEID, 0)));


                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseInteractionResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("EmResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                showAlertDialog("Your interaction is done.");
            }
            else {

                String error = response.getString(ReqConst.RES_ERROR);
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showAlertDialog(error);
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showAlertDialog(getString(R.string.error));
        }
    }

}
