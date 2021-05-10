package com.mv.vacay.main.meetfriends;

import android.Manifest;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.chat.UserDetails;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.main.activity.GolfActivity;
import com.mv.vacay.main.activity.RunningActivity;
import com.mv.vacay.main.activity.SkiingSnowboardingActivity;
import com.mv.vacay.main.activity.TennisActivity;
import com.mv.vacay.main.location.LocationCaptureActivity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.tajchert.nammu.PermissionCallback;

public class ShowMessageActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imagemessage, attachbutton,location,deleteMessage,speechButton;
    NetworkImageView imagefromfriend;
    TextView sendbutton,friendemail,friendmessage,requestLocationView;
    EditText usermessage;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;
    ImageLoader _imageLoader;
    private ProgressDialog _progressDlg;
    File file=null;
    int _idx;
    static TextView DateEdit;
    static String date="";
    int year, month, day,hour,minute;
    LinearLayout replyFrame;
    boolean _takePhoto=false;

    PermissionHelper permissionHelper;
    Bitmap bm=null;
    boolean type=false;
    String message="";
    static String scheduleDateTime="", selectedDTime="";
    String mailId="";

    private static final String TAG = ShowMessageActivity.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    boolean is_speech=false;

    Firebase reference1, reference2, reference3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_message);

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

        permissionHelper = PermissionHelper.getInstance(this);

        DateEdit = (TextView) findViewById(R.id.sendate);

        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons._calendar_set=true;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });

        UserDetails.username= Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott");
        UserDetails.chatWith= Commons.messageEntity.get_useremail().replace(".com","").replace(".","ddoott");

        Firebase.setAndroidContext(this);
//        reference1 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/" + UserDetails.chatWith + "/"+UserDetails.username);

        scheduleDateTime=Commons.messageEntity.get_service_reqdate();

        replyFrame=(LinearLayout)findViewById(R.id.replyFrame);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        imagefromfriend=(NetworkImageView) findViewById(R.id.messageImagefromfriend);
        if(Commons.messageEntity.get_imageUrl().length()>0){
            imagefromfriend.setImageUrl(Commons.messageEntity.get_imageUrl(),_imageLoader);
        }
        imagefromfriend.setOnClickListener(this);

        imagemessage=(ImageView)findViewById(R.id.messageImage);
//        imagemessage.setImageBitmap(Commons.bitmap_activity);

        friendemail=(TextView) findViewById(R.id.friend_email);
        friendemail.setText(Commons.messageEntity.get_useremail());

        speechButton=(ImageView)findViewById(R.id.search_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        speechDescription.setVisibility(View.VISIBLE);
                is_speech=true;
                startVoiceRecognitionActivity();
            }
        });

        friendmessage=(TextView) findViewById(R.id.friend_message);
        friendmessage.setText(Commons.messageEntity.get_usermessage());

        if(friendmessage.getText().toString().contains("Please review the information. If you have questions, you can reply directly to the customer. If you want to accept or decline, please click here.")){
            //=========================================================================================================================================

            int i1 = Commons.messageEntity.get_usermessage().indexOf("please click here.");    Log.d("SUBINDEX1===>", String.valueOf(i1));
            int i2 = Commons.messageEntity.get_usermessage().indexOf("Thanks",i1);              Log.d("SUBINDEX2===>", String.valueOf(i2));

            String msg = Commons.messageEntity.get_usermessage().substring(0,i2-1)+"\n\n"+"Accept"+"               "+"Decline"+"\n\n"+Commons.messageEntity.get_usermessage().substring(i2, Commons.messageEntity.get_usermessage().length());

            Log.d("MSG===>", msg);

            friendmessage.setMovementMethod(LinkMovementMethod.getInstance());
            friendmessage.setText(msg, TextView.BufferType.SPANNABLE);
            Spannable mySpannable = (Spannable)friendmessage.getText();
            ClickableSpan myClickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                /* do something */
                    sendMsg2(type=true);
                }
            };

            ClickableSpan myClickableSpan2 = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                /* do something */
                    sendMsg2(type=false);
                }
            };

            mySpannable.setSpan(myClickableSpan1, i2, i2 + 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            mySpannable.setSpan(myClickableSpan2, i2+20, i2 + 29, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //=========================================================================================================================================
        }

        usermessage=(EditText) findViewById(R.id.sendmessage);
        sendbutton=(TextView) findViewById(R.id.sendbutton);
        sendbutton.setOnClickListener(this);
        attachbutton=(ImageView)findViewById(R.id.attachbutton);
        attachbutton.setOnClickListener(this);

        deleteMessage=(ImageView)findViewById(R.id.deletemessage);
        deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usermessage.setText("");
            }
        });

        location=(ImageView)findViewById(R.id.locationbutton);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LocationCaptureActivity.class);
                startActivity(intent);
                Commons._location_activity=true;
            }
        });

        Log.d("LatLng===>",Commons.messageEntity.get_requestLatLng().toString());

        requestLocationView=(TextView)findViewById(R.id.requestLocationViewButton);

        if(friendemail.getText().toString().equals("service@vacay.com")) {
            requestLocationView.setVisibility(View.GONE);
            replyFrame.setVisibility(View.GONE);
        }
        requestLocationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Commons.messageEntity.get_requestLatLng().equals(new LatLng(0.0,0.0))) {
                    Intent intent = new Intent(getApplicationContext(), RequestLocationViewActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }else Toast.makeText(getApplicationContext(),"No request location.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendbutton:
                if(usermessage.getText().toString().length()==0) {
                    LayoutInflater inflater = this.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.toast_view, null);
                    TextView textView=(TextView)dialogView.findViewById(R.id.text);
                    textView.setText("Please input your message to send to your friend.");
                    Toast toast=new Toast(this);
                    toast.setView(dialogView);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    sendMsg();
                }

                break;
            case R.id.attachbutton:
                openMenuItems();
                break;
            case R.id.messageImagefromfriend:
                Intent intent=new Intent(this,ImageViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
        }
    }
    private void openMenuItems() {
        View view = findViewById(R.id.attachbutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.attach_menu);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

    }
    public void fromGallery(MenuItem menuItem) {

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto(MenuItem menuItem) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);

    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
//        else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                    + "VID_" + timeStamp + ".mp4");
//        }
        else {
            return null;
        }

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap_activity=bitmap;
                        imagemessage.setImageBitmap(Commons.bitmap_activity);
                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();
                        Commons.destination=new File(_photoPath);
                        Commons.imageUrl=_photoPath;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();
                }

            case Constants.PICK_FROM_CAMERA: {

                if(!is_speech){
                    try {

                        permissionHelper.verifyPermission(
                                new String[]{"take picture"},
                                new String[]{Manifest.permission.CAMERA},
                                new PermissionCallback() {
                                    @Override
                                    public void permissionGranted() {
                                        //action to perform when permission granteed
                                    }

                                    @Override
                                    public void permissionRefused() {
                                        //action to perform when permission refused
                                    }
                                }
                        );

                        try{
                            onCaptureImageResult(data);
                            return;
                        }catch (Exception e){
                            e.printStackTrace();
                        }


//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
                        //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        _imageCaptureUri = data.getData();

                        Intent intent = new Intent("com.android.camera.action.CROP");
                        intent.setDataAndType(_imageCaptureUri, "image/*");

                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                        intent.putExtra("crop", true);
                        intent.putExtra("scale", true);
                        intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                        intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("noFaceDetection", true);
                        //intent.putExtra("return-data", true);
                        intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                        startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            if(is_speech){
                Log.d("ResultCode===>",String.valueOf(456));
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(usermessage.getText().length()>0)
                    usermessage.setText(usermessage.getText().toString()+"\n"+matches.get(0));
                else usermessage.setText(matches.get(0));
                is_speech=false;
            }

        }
    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        Commons.destination = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            Commons.destination.createNewFile();
            fo = new FileOutputStream(Commons.destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagemessage.setImageBitmap(thumbnail);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onDestroy() {
        permissionHelper.finish();
        super.onDestroy();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

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

    public void sendMsg() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_MAKEMAIL;
//        showProgress();


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

                String message=usermessage.getText().toString();

                if(Commons.loc_url!=""){
                    message=message+"\nLocation for VaCay:\n"+Commons.loc_url;
                    Commons.loc_url="";
                }

                Date date2=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String sendDateTime = formatter.format(date2);

                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", Commons.messageEntity.get_useremail().trim());
                params.put("name", Commons.thisEntity.get_name());
                params.put("photo_url", Commons.thisEntity.get_photoUrl());
                if(date.length()>0) {
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

//        closeProgress();

        try {

            JSONObject response = new JSONObject(json);   Log.d("RRRRResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("RRResultcode=====> :",success);

            if (success.equals("0")) {
                _idx=response.getInt("mail_id");
                usermessage.setText("");

        //        Log.d("Map Image===>",Commons.file.getPath());

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

        String url = ReqConst.SERVER_URL + "sendMailMessage"; Log.d("MailUrl===>", url);

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
                String myEmployeeId=String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
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
            Intent intent=new Intent(this,GolfActivity.class);
            startActivity(intent);
        }else if(Commons._run_activity){
            Intent intent=new Intent(this,RunningActivity.class);
            startActivity(intent);
        }else if(Commons._ski_activity){
            Intent intent=new Intent(this,SkiingSnowboardingActivity.class);
            startActivity(intent);
        }else if(Commons._tennis_activity){
            Intent intent=new Intent(this,TennisActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, MeetFriendActivity.class);
            startActivity(intent);
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
                date=date + " - " + hourOfDay + ":" + minute+" PM";
            }else {
                date=date+" - " + hourOfDay + ":" + minute+" AM";
            }
            selectedDTime = date;
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
            month=month+1;
            date=month+"/" + day + "/" + year;
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

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");

        try {
            Log.d("ResultCode===>",String.valueOf(123));
            this.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showAlertDialog("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogInteraction(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        registerInteraction();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    private void registerInteraction() {

        String url = ReqConst.SERVER_URL + "addEmInteraction";

 //       showProgress();


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
 //               closeProgress();
                showAlertDialog(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("em_id", String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0)));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseInteractionResponse(String json) {

//        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("EmResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                showAlertDialog("Your interaction is done.");
            }
            else {

                String error = response.getString(ReqConst.RES_ERROR);
 //               closeProgress();
//                showAlertDialog(getString(R.string.error));
                showAlertDialog(error);
            }

        } catch (JSONException e) {
 //           closeProgress();
            e.printStackTrace();

            showAlertDialog(getString(R.string.error));
        }
    }

    public void sendMsg2(final boolean type) {


        String url = ReqConst.SERVER_URL + ReqConst.REQ_MAKEMAIL;
        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.d("Text response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseSendMessageResponse(response);

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
                            +Commons.thisEntity.get_name()+" can't do your service "+" with your requested schedule of "+scheduleDateTime+"\n"

                            +"Please select another time or another service provider.\n" +
                            "We apologize for the inconvenience\n"
                            +datetime+"\n"+Commons.thisEntity.get_name();
                }

                params.put("name", Commons.thisEntity.get_name());
                params.put("photo_url", Commons.thisEntity.get_photoUrl());
                params.put("from_mail", Commons.thisEntity.get_email());
                params.put("to_mail", Commons.messageEntity.get_useremail());

                if(selectedDTime.length()>0)
                    params.put("request_date", datetime);
                else
                    params.put("request_date", datetime);
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

    public void parseSendMessageResponse(String json) {

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);   Log.d("RRRRResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("RRResultcode=====> :",success);

            if (success.equals("0")) {

                selectedDTime="";

                makeMail2(response.getInt("mail_id"));

            }
            else{

                Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }

    public void makeMail2(final int mail_id) {

        String url = ReqConst.SERVER_URL + "sendMailMessage";

        showProgress();
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseSendMailResponse2(response);


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

    public void parseSendMailResponse2(String json) {

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

        mailId = Commons.messageEntity.getMail_id();    Log.d("MailID===>", Commons.messageEntity.getMail_id());

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

}











































