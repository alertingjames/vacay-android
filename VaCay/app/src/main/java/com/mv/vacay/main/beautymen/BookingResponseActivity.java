package com.mv.vacay.main.beautymen;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.ProviderScheduleListAdapter;
import com.mv.vacay.chat.UserDetails;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.main.ChatListActivity;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.main.meetfriends.RequestLocationViewActivity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.sms.SendSMSActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingResponseActivity extends AppCompatActivity {

    CircularNetworkImageView photoNet;
    CircularImageView photo;
    TextView name, email, phone;
    TextView accept, decline;
    ImageView scheduleButton;
    TextView requestBox;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    Firebase reference1, reference2, reference3;
    boolean is_typing=false;
    LinearLayout schedulePage;
    ListView list;
    ImageView back,cancel;
    String message="";
    static String scheduleDateTime="", selectedDTime="";
    int year, month, day,hour,minute;
    static TextView selectedDateTime;
    boolean type=false;
    String mailId="";
    ProviderScheduleListAdapter providerScheduleListAdapter = new ProviderScheduleListAdapter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_page);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Commons.year=year;
        Commons.month=month;
        Commons.day=day;
        Commons.hour=hour;
        Commons.min=minute;

        selectedDateTime=(TextView)findViewById(R.id.selectedDateTime);

        UserDetails.username= Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott");
        UserDetails.chatWith= Commons.messageEntity.get_useremail().replace(".com","").replace(".","ddoott");

        Firebase.setAndroidContext(this);
//        reference1 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/" + UserDetails.chatWith + "/"+UserDetails.username);

        Intent intent=getIntent();
        String status=intent.getStringExtra("status");
        if(status.equals("decline")){
            final ScrollView scrollView=(ScrollView)findViewById(R.id.scrollView) ;
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }

        mailId = Commons.messageEntity.getMail_id();

        requestBox=(TextView)findViewById(R.id.requestBox);
        requestBox.setText(Commons.messageEntity.get_usermessage());

        //=========================================================================================================================================

        int i1 = Commons.messageEntity.get_usermessage().indexOf("please click here.");    Log.d("SUBINDEX1===>", String.valueOf(i1));
        int i2 = Commons.messageEntity.get_usermessage().indexOf("Thanks",i1);              Log.d("SUBINDEX2===>", String.valueOf(i2));

        String msg = Commons.messageEntity.get_usermessage().substring(0,i2-1)+"\n\n"+"Accept"+"               "+"Decline"+"\n\n"+Commons.messageEntity.get_usermessage().substring(i2, Commons.messageEntity.get_usermessage().length());

        Log.d("MSG===>", msg);

        requestBox.setMovementMethod(LinkMovementMethod.getInstance());
        requestBox.setText(msg, TextView.BufferType.SPANNABLE);
        Spannable mySpannable = (Spannable)requestBox.getText();
        ClickableSpan myClickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                /* do something */
                sendMsg(type=true);
            }
        };

        ClickableSpan myClickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                /* do something */
                sendMsg(type=false);
            }
        };

        mySpannable.setSpan(myClickableSpan1, i2, i2 + 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mySpannable.setSpan(myClickableSpan2, i2+20, i2 + 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //=========================================================================================================================================

        photoNet = (CircularNetworkImageView) findViewById(R.id.photo_net);
        photo = (CircularImageView) findViewById(R.id.photo);

        if(Commons.messageEntity.get_photoUrl().length()<1000) {
            photoNet.setVisibility(View.VISIBLE);
            photoNet.setImageUrl(Commons.messageEntity.get_photoUrl(), VaCayApplication.getInstance()._imageLoader);
        }else {
            photoNet.setVisibility(View.GONE);
            photo.setImageBitmap(base64ToBitmap(Commons.messageEntity.get_photoUrl()));
        }

        name=(TextView)findViewById(R.id.name);

        if(Commons.messageEntity.get_userfullname().length()>0)
            name.setText(Commons.messageEntity.get_userfullname());

        email=(TextView)findViewById(R.id.email);
        email.setText(Commons.messageEntity.get_useremail());

        scheduleDateTime=Commons.messageEntity.get_service_reqdate();

        accept=(TextView)findViewById(R.id.accept);
        accept.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        accept.setBackground(getDrawable(R.drawable.green_fillrect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        accept.setBackground(getDrawable(R.drawable.blue_fill_white_stroke));

                        sendMsg(type=true);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        accept.getBackground().clearColorFilter();
                        accept.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        decline=(TextView)findViewById(R.id.decline);
        decline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        decline.setBackground(getDrawable(R.drawable.green_fillrect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        decline.setBackground(getDrawable(R.drawable.light_green_thin_stroke));

                        sendMsg(type=false);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        decline.getBackground().clearColorFilter();
                        decline.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public void selectDateTime(View view){
        showAlertDialog("Do you want to change your service\nschedule date and time for the customer?\nIf you accept with it, you will\nsend your changed schedule to\nthe customer.");
    }

    public void showAlertDialog(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Warning!");
        alertDialog.setIcon(R.drawable.noti);
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        showTruitonTimePickerDialog();
                        showTruitonDatePickerDialog();

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectedDTime="";
                    }
                });
        alertDialog.show();

    }

    public static class TimePickerFragment extends DialogFragment implements
            TimePickerDialog.OnTimeSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
//            final Calendar c = Calendar.getInstance();
//            int hour = c.get(Calendar.HOUR_OF_DAY);
//            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, Commons.hour, Commons.min,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if(hourOfDay>12){
                hourOfDay=hourOfDay-12;
                selectedDTime = selectedDTime + " - " + hourOfDay + ":" + minute+" PM";
            }else {
                selectedDTime = selectedDTime + " - " + hourOfDay + ":" + minute+" AM";
            }
            selectedDateTime.setText(selectedDTime);
        }
    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {

        String[] monthes={"January","February","March","April","May","June","July","August","September","October","November","December"};

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this,Commons.year, Commons.month, Commons.day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            selectedDTime = monthes[month] + " " + day + "," + year;
        }
    }

    public void showTruitonTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showTruitonDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void talking(View view){

        UserEntity userEntity=new UserEntity();
        userEntity.set_name(Commons.messageEntity.get_userfullname());
        userEntity.set_email(Commons.messageEntity.get_useremail());
        userEntity.set_photoUrl(Commons.messageEntity.get_photoUrl());
        Commons.userEntity=userEntity;

        Intent intent=new Intent(getApplicationContext(), ChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void sendSMS(View view){

        Commons._provider_booking=true;
        Commons.requestMessage=message;

        Intent intent=new Intent(getApplicationContext(), SendSMSActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void viewRequestLocation(View view){
        if(!Commons.messageEntity.get_requestLatLng().equals(new LatLng(0.0,0.0))) {
            Intent intent = new Intent(getApplicationContext(), RequestLocationViewActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }else Toast.makeText(getApplicationContext(),"No request location.",Toast.LENGTH_SHORT).show();
    }

    ProgressDialog _progressDlg;

    public void showProgress() {
//        closeProgress();
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

    public void sendMsg(final boolean type) {


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
                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String datetime = formatter.format(date);

                if(type){
                    if(selectedDTime.length()>0){
                        message= "Hi, "+Commons.messageEntity.get_userfullname()+"\n"
                                +Commons.thisEntity.get_name()+" has updated you "+Commons.messageEntity.get_service()+" with schedule of "+selectedDTime+"\n"
                                +"So if you need to reschedule, you will need to send another message via the email portion in our app."
                                +"Thanks\n"+datetime+"\n"+Commons.thisEntity.get_name();
                    }else {
                        message= "Hi, "+Commons.messageEntity.get_userfullname()+"\n"
                                +Commons.thisEntity.get_name()+" has accepted you "+Commons.messageEntity.get_service()+" with schedule of "+scheduleDateTime+"\n"
                                +"Thanks\n"+datetime+"\n"+Commons.thisEntity.get_name();
                    }
                }else {
                    message= "Hi, "+Commons.messageEntity.get_userfullname()+"\n"
                            +Commons.thisEntity.get_name()+" can't do you " + Commons.messageEntity.get_service() + " with your requested schedule of "+scheduleDateTime+"\n"

                            +"Please select another time or another service provider.\n" +
                            "We apologize for the inconvenience\n"
                            +datetime+"\n"+Commons.thisEntity.get_name();
                }

                params.put("name", Commons.thisEntity.get_name());
                params.put("photo_url", Commons.thisEntity.get_photoUrl());
                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", Commons.messageEntity.get_useremail());

                if(selectedDTime.length()>0) {
                    params.put("request_date", datetime);
                }
                else {
                    params.put("request_date", datetime);
                }
                params.put("service", "no_service");
                params.put("service_reqdate", "");

                params.put("text_message", message);

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

                selectedDTime="";

                makeMail(response.getInt("mail_id"));

            }
            else{

                Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
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

    DBManager dbManager;

    public void parseSendMailResponse(String json) {

        closeProgress();

        dbManager = new DBManager(getApplicationContext());
        dbManager.open();

        try {

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===", response.toString());

            String result_code = response.getString("result");

            if (result_code.equals("0")) {

                for(int i=0;i<dbManager.getAllMembers().size();i++){
                    if(dbManager.getAllMembers().get(i).get_email().equals(Commons.messageEntity.get_useremail())){
                        dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
                    }
                }

                if(Commons.messageEntity.get_userfullname().length()>0)
                    dbManager.insert(Commons.messageEntity.get_userfullname(), Commons.messageEntity.get_useremail(),Commons.messageEntity.get_photoUrl(),"0");

                sendNotification();
            }
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }

    public void sendNotification(){

        Map<String, String> map = new HashMap<String, String>();
        map.put("message", message);
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("image", "");
        map.put("video", "");
        map.put("lat", "");
        map.put("lon", "");
        map.put("user", UserDetails.username);
        reference1.push().setValue(map);
        reference2.push().setValue(map);
        is_typing=false;

        Map<String, String> map1 = new HashMap<String, String>();
        map1.put("sender", UserDetails.username);
        if(Commons.thisEntity.get_fullName().length()>0)
            map1.put("senderName", Commons.thisEntity.get_fullName());
        if(Commons.thisEntity.get_name().length()>0)
            map1.put("senderName", Commons.thisEntity.get_name());
        map1.put("senderPhoto", Commons.thisEntity.get_photoUrl());
        map1.put("msg", message);
        reference3.push().setValue(map1);

        updateRequestFromCustomer(type);
    }

    public void updateRequestFromCustomer(final boolean type){

        String url = ReqConst.SERVER_URL + "update_request_message";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateRequestResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                Toast.makeText(getApplicationContext(),getString(R.string.error), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("mail_id", mailId);
                if(type) params.put("status", "accepted");
                else params.put("status", "declined");

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseUpdateRequestResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code.equals("0")) {
                Toast.makeText(getApplicationContext(),"Message sent!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Server connection failed",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            Toast.makeText(getApplicationContext(),"Server connection failed",Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }

    public void showMySchedule(View view){
        schedulePage.setVisibility(View.VISIBLE);
        getProviderAvailable();
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
                Toast.makeText(getApplicationContext(),getString(R.string.error), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("proid", String.valueOf(Commons.thisEntity.get_idx()));

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

                Toast.makeText(getApplicationContext(),getString(R.string.register_fail),Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            Toast.makeText(getApplicationContext(),getString(R.string.register_fail),Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }

    }
}






































