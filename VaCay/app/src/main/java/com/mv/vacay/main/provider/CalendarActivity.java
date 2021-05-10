package com.mv.vacay.main.provider;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.ProviderDateTimeListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.ProviderScheduleEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarActivity extends BaseActivity {
    ImageLoader _imageloader;
    String proid="", scheduleStr="";
    static LinearLayout timeAlert,buttonBar;
    static TextView start, end,start1, end1, saveButton, setButton,refreshButton;
    static int _startFlag=0,_startFlag1=0;
    ListView list;
    static String sdateTime="",edateTime="",comment="",dateTimeStr="";
    ArrayList<String> dateTimes=new ArrayList<>();
    ArrayList<ProviderScheduleEntity> providerScheduleEntities=new ArrayList<>();
    ProviderDateTimeListAdapter dateTimeListAdapter=new ProviderDateTimeListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        Intent intent=getIntent();
        proid = intent.getStringExtra("proid");

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        Commons.year=year;
        Commons.month=month;
        Commons.day=day;
        Commons.hour=hour;
        Commons.min=minute;

        saveButton=(TextView)findViewById(R.id.saveButton) ;
        saveButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        saveButton.setBackgroundColor(Color.GREEN);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        saveButton.setBackgroundResource(R.drawable.button_gradient_fill);
                        buttonBar.setVisibility(View.GONE);
        //                showDialogComment();

//                        registerSchedule(sdateTime,edateTime,comment);

                        try {
                            if(createScheduleJsonString().length()>0){
                                registerMultipleSchedule(createScheduleJsonString());
                            }else showToast("Please set your service unavailable schedule");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        saveButton.getBackground().clearColorFilter();
                        saveButton.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        setButton=(TextView)findViewById(R.id.setButton) ;
        setButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        setButton.setBackgroundColor(Color.GREEN);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        setButton.setBackgroundResource(R.drawable.button_gradient_fill);
                        if(sdateTime.length()>0 && edateTime.length()>0) {
                            showDialogComment();
                        }else showToast("Please input date and time.");

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        setButton.getBackground().clearColorFilter();
                        setButton.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

        refreshButton=(TextView)findViewById(R.id.refreshButton) ;
        refreshButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        refreshButton.setBackgroundColor(Color.GREEN);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        refreshButton.setBackgroundResource(R.drawable.button_gradient_fill);
                        refreshSchedule();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        refreshButton.getBackground().clearColorFilter();
                        refreshButton.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
        start=(TextView)findViewById(R.id.start) ;
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start.setBackgroundResource(R.drawable.blue_fill_white_stroke);
                end.setBackground(null);
               if(sdateTime.length()==0&& edateTime.length()==0){
                   _startFlag=1;
                   showTruitonTimePickerDialog();
                   showTruitonDatePickerDialog();
               }else showToast("Please set the current date & time info");
            }
        });

        end=(TextView)findViewById(R.id.end) ;
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end.setBackgroundResource(R.drawable.blue_fill_white_stroke);
                start.setBackground(null);
                if(start.getText().length()==0) {
                    Toast.makeText(getApplicationContext(),"Please first input start date and time",Toast.LENGTH_SHORT).show();
                    return;
                }
                _startFlag=2;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageView calendar=(ImageView)findViewById(R.id.calendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        timeAlert=(LinearLayout)findViewById(R.id.timeListContainer);
        buttonBar=(LinearLayout)findViewById(R.id.buttonBar);

        list=(ListView)findViewById(R.id.list);
        dateTimeListAdapter.setDatas(Commons.scheduleInfo);
        list.setAdapter(dateTimeListAdapter);

        refreshSchedule();
    }

    public void showDialogComment() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.customView);

        dialogBuilder.setTitle("You can add a comment");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                if(sdateTime.length()>0 && edateTime.length()>0){
                    ProviderScheduleEntity providerScheduleEntity=new ProviderScheduleEntity();
                    providerScheduleEntity.setScheduleStart(sdateTime);
                    providerScheduleEntity.setScheduleEnd(edateTime);
                    providerScheduleEntity.setScheduleComment(edt.getText().toString());

                    Commons.scheduleInfo.add(0,providerScheduleEntity);
                    comment=edt.getText().toString();
                    Commons._datetime="";
                    start.setText("");
                    end.setText("");
                    sdateTime="";
                    edateTime="";

                    dateTimeListAdapter.setDatas(Commons.scheduleInfo);
                    list.setAdapter(dateTimeListAdapter);
                    //            registerSchedule();
                }
            }
        });
        dialogBuilder.setNegativeButton("SKIP", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                if(sdateTime.length()>0 && edateTime.length()>0) {
                    ProviderScheduleEntity providerScheduleEntity = new ProviderScheduleEntity();
                    providerScheduleEntity.setScheduleStart(sdateTime);
                    providerScheduleEntity.setScheduleEnd(edateTime);
                    providerScheduleEntity.setScheduleComment("");

                    Commons.scheduleInfo.add(0, providerScheduleEntity);
                    comment = "";
                    Commons._datetime = "";
                    start.setText("");
                    end.setText("");
                    sdateTime = "";
                    edateTime = "";

                    dateTimeListAdapter.setDatas(Commons.scheduleInfo);
                    list.setAdapter(dateTimeListAdapter);
                }

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void registerSchedule(final String start, final String end, final String comment) {

        String url = ReqConst.SERVER_URL + "updateProviderAvailable";

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

                params.put("proid", proid);
                params.put("availableStart", start);
                params.put("availableEnd", end);
                params.put("availableComment", comment);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        closeProgress();

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                showToast("Your schedule saved successfully");
                sdateTime="";
                edateTime="";
                comment="";

            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                showToast("Your schedule saved successfully");
                sdateTime="";
                edateTime="";
                comment="";

            }
            else {
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

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

                            Commons._datetime=Commons._datetime+ " - " + hourOfDay + ":" + minute+" PM";
            }else {
                            Commons._datetime=Commons._datetime+ " - " + hourOfDay + ":" + minute+" AM";
            }

//            dateTime.setText(Commons._datetime);
            if(_startFlag==1){
                start.setText(Commons._datetime);
                sdateTime=Commons._datetime; _startFlag=0;
            }
            else if(_startFlag==2){
                _startFlag=0;
                end.setText(Commons._datetime);
                if(start.getText().length()>0) {
                    buttonBar.setVisibility(View.VISIBLE);
                    edateTime=Commons._datetime;
                }else {
                    Toast.makeText(getActivity(),"Please first input start date and time",Toast.LENGTH_SHORT).show();
                }
            }
            else if(_startFlag1==1){
                start1.setText(Commons._datetime);
                sdateTime=Commons._datetime; _startFlag1=0;
            }
            else if(_startFlag1==2){
                _startFlag1=0;
                end1.setText(Commons._datetime);
                if(start1.getText().length()>0) {
                    edateTime=Commons._datetime;
                }else {
                    Toast.makeText(getActivity(),"Please first input start date and time",Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
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

                Commons._datetime=monthes[month] + " " + day + "," + year;
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

    private String createScheduleData(){
        String jsonString = "";

        try {

            JSONObject scheduleObj = new JSONObject(); // Main Json

            for (int i=0;i<dateTimes.size();i++){
                scheduleObj.put("schedule"+i,dateTimes.get(i));
            }

            jsonString = scheduleObj.toString();


        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    public void showChoiceDialog() {

        final String[] items = {"Edit before save",
                "Delete",
                "Share",

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select ...");


        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if(item==0){
                    showDialogEdit();
                }else if(item==1){
                    Commons.scheduleInfo.remove(Commons._datetimeId);
                    dateTimeListAdapter.setDatas(Commons.scheduleInfo);
                    dateTimeListAdapter.notifyDataSetChanged();
                    list.setAdapter(dateTimeListAdapter);
                    if(!Commons.dateTime.getScheduleId().equals(null)||Commons.dateTime.getScheduleId().length()>0){
                        deleteProviderScheduleInfo(Commons.dateTime.getScheduleId());
                    }

                }else if(item==2){
                    String shareBody = Commons.dateTime.getScheduleStart()+" -> "+Commons.dateTime.getScheduleEnd()+"\n"+Commons.dateTime.getScheduleComment();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Provider's schedule");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share as:"));
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showDialogEdit() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_provider_datetime_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        start1 = (TextView) dialogView.findViewById(R.id.start);
        start1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _startFlag1=1;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });
        end1 = (TextView) dialogView.findViewById(R.id.end);
        end1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _startFlag1=2;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });
        final EditText comment = (EditText) dialogView.findViewById(R.id.comment);
        start1.setText(Commons.dateTime.getScheduleStart());
        end1.setText(Commons.dateTime.getScheduleEnd());
        comment.setText(Commons.dateTime.getScheduleComment());
        dialogBuilder.setTitle("Edit before save...");
        dialogBuilder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(start1.getText().length()>0 && end1.getText().length()>0) {
                    Commons.scheduleInfo.remove(Commons._datetimeId);

                    Commons.dateTime.setScheduleStart(start1.getText().toString());
                    Commons.dateTime.setScheduleEnd(end1.getText().toString());
                    Commons.dateTime.setScheduleComment(comment.getText().toString());

                    Commons.scheduleInfo.add(Commons._datetimeId, Commons.dateTime);

                    dateTimeListAdapter.setDatas(Commons.scheduleInfo);
                    list.setAdapter(dateTimeListAdapter);

                    if(!Commons.dateTime.getScheduleId().equals(null)|| Commons.dateTime.getScheduleId().length()>0){
                        deleteProviderScheduleInfo1(Commons.dateTime.getScheduleId());

                    }

                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void deleteProviderScheduleInfo(final String availableid) {

        String url = ReqConst.SERVER_URL + "deleteProviderAvailable";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseDeleteScheduleResponse(response);

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

                params.put("availableid", availableid);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseDeleteScheduleResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseDelete====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                showToast("Data deleted");
            }
            else {

                showToast("Data deleting failed");
            }

        } catch (JSONException e) {

            showToast("Data deleting failed");

            e.printStackTrace();
        }

    }

    public void deleteProviderScheduleInfo1(final String availableid) {

        String url = ReqConst.SERVER_URL + "deleteProviderAvailable";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseDeleteScheduleResponse1(response);

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

                params.put("availableid", availableid);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseDeleteScheduleResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseProvider====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                updateSchedule(Commons.dateTime.getScheduleStart(),Commons.dateTime.getScheduleEnd(),Commons.dateTime.getScheduleComment());
            }
            else {

                showToast("Data deleting failed");
            }

        } catch (JSONException e) {

            showToast("Data deleting failed");

            e.printStackTrace();
        }

    }

    public void updateSchedule(final String start, final String end, final String comment) {

        String url = ReqConst.SERVER_URL + "updateProviderAvailable";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateResponse(response);

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

                params.put("proid", proid);
                params.put("availableStart", start);
                params.put("availableEnd", end);
                params.put("availableComment", comment);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateResponse(String json) {

        closeProgress();

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                showToast("Your schedule saved successfully");


            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                showToast("Your schedule saved successfully");
            }
            else {
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public void refreshSchedule(){

        Commons.scheduleInfo.clear();
        providerScheduleEntities.clear();

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
                showToast(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("proid", proid);

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

                    providerScheduleEntities.add(0,providerScheduleEntity);
                }

                dateTimeListAdapter.setDatas(providerScheduleEntities);
                dateTimeListAdapter.notifyDataSetChanged();
                list.setAdapter(dateTimeListAdapter);
            }
            else {

                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {

            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public String createScheduleJsonString()throws JSONException{

        scheduleStr = "";
        JSONObject jsonObj = null;
        JSONArray jsonArr = new JSONArray();
        if (Commons.scheduleInfo.size()>0){
            for(int i=0; i<Commons.scheduleInfo.size(); i++){

                String startStr = Commons.scheduleInfo.get(i).getScheduleStart();
                String endStr = Commons.scheduleInfo.get(i).getScheduleEnd();
                String comment = Commons.scheduleInfo.get(i).getScheduleComment();

                jsonObj=new JSONObject();

                try {
                    jsonObj.put("start",startStr);
                    jsonObj.put("end",endStr);
                    jsonObj.put("comment",comment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                jsonArr.put(jsonObj);
            }
            JSONObject scheduleObj = new JSONObject();
            scheduleObj.put("schedule", jsonArr);
            scheduleStr = scheduleObj.toString();
            return scheduleStr;
        }else {
            showToast("Please set your service-unavailable schedule");
        }

        return scheduleStr;
    }

    public void registerMultipleSchedule(final String scheduleStr) {

        String url = ReqConst.SERVER_URL + "uploadMultipleSchedule";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterMultipleScheduleResponse(response);

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

                params.put("proid", proid);
                params.put("schedulestr", scheduleStr);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterMultipleScheduleResponse(String json) {

        closeProgress();

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                Commons.scheduleInfo.clear();
                showToast("Your service unavailable schedule saved successfully");

            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                showToast("Your schedule saved successfully");

            }
            else {
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

}


























