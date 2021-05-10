package com.mv.vacay.login;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.CheckinActivity;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.SurveyAnswerViewActivity;
import com.mv.vacay.main.SurveyQuestActivity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class  VirtualSignupActivity extends BaseActivity implements View.OnClickListener{
    TextView ok,answer_survey,checkin,name,gendr;
    EditText age,city,education,otherinterests,mailAddr,firstname,lastname,email,job;
    CircularImageView ui_userphoto;
    LinearLayout spin,spinrelationship,gender;
    ImageView back;
    private ImageLoader _imageLoader;
    String firstinter="",relations="";
    private int _idx = 0;
    String _photoPath = "";
    Bitmap bitmap=null;
    private Uri _imageCaptureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtual_signup);
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(this);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        ui_userphoto=(CircularImageView)findViewById(R.id.imv_photo);
        ui_userphoto.setOnClickListener(this);
        //   ui_userphoto.setImageURI(Uri.parse(Commons.thisEntity.get_photoUrl().toString()));
        //    ui_userphoto.setImageBitmap(Commons.thisEntity.get_bitmap());

        firstname=(EditText) findViewById(R.id.txv_firstname);

        lastname=(EditText) findViewById(R.id.txv_lastname);

        answer_survey=(TextView)findViewById(R.id.answer_survey);
        answer_survey.setText(Commons.thisEntity.get_survey_quest());
        answer_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SurveyAnswerViewActivity.class);
                startActivity(intent);
            }
        });
        gendr=(TextView) findViewById(R.id.gender);
        checkin=(TextView) findViewById(R.id.checkin);
        checkin.setOnClickListener(this);
        email=(EditText)findViewById(R.id.email);

        job=(EditText)findViewById(R.id.job);

        age=(EditText)findViewById(R.id.edt_age);
        city=(EditText)findViewById(R.id.edt_city);
        education=(EditText)findViewById(R.id.edt_education);
        spin=(LinearLayout)findViewById(R.id.lyt_spin);
        spin.setOnClickListener(this);
        gender=(LinearLayout)findViewById(R.id.lyt_gender);
        gender.setOnClickListener(this);
        spinrelationship=(LinearLayout)findViewById(R.id.lyt_spin_relationship);
        spinrelationship.setOnClickListener(this);
        otherinterests=(EditText)findViewById(R.id.edt_otherinterest);
        mailAddr=(EditText)findViewById(R.id.edt_mailing);
        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SurveyQuestActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ok:

                if ((age.getText().length() > 0)) {
                    if (firstname.getText().length() > 0 && _photoPath.length() > 0 && lastname.getText().length() > 0 && email.getText().length() > 0 &&
                            job.getText().length() > 0 && city.getText().length() > 0 && education.getText().length() > 0 && firstinter.length() > 0 &&
                            relations.length() > 0) {

                        final Calendar c = Calendar.getInstance();
                        int year = c.get(Calendar.YEAR);
                        int ageint = Integer.parseInt(age.getText().toString());
                        int birthyear = year - ageint;

                        Commons.thisEntity.set_photoUrl(_photoPath);
                        Commons.thisEntity.set_lastName(lastname.getText().toString());
                        Commons.thisEntity.set_firstName(firstname.getText().toString());
                        Commons.thisEntity.set_email(email.getText().toString());
                        Commons.thisEntity.set_job(job.getText().toString());
                        Commons.thisEntity.set_age_range(String.valueOf(birthyear));
                        Commons.thisEntity.set_city(city.getText().toString());
                        Commons.thisEntity.set_education(education.getText().toString());
                        if (otherinterests.getText().toString() != "")
                            Commons.thisEntity.set_interest(firstinter + "-" + otherinterests.getText().toString());
                        else Commons.thisEntity.set_interest(firstinter);
                        Commons.thisEntity.set_relations(relations);

                        Commons.thisEntity.set_publicName(" ");
                        Commons.thisEntity.set_userlat(0.0f);
                        Commons.thisEntity.set_userlng(0.0f);

                        register();
                    }
                    else showToast("Please check the blanks");
                }
                else showToast("Please check the blanks");



//                Preference.getInstance().put(this,
//                        PrefConst.PREFKEY_USEREMAIL, Commons.thisEntity.get_email());
//
//                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.left_in,R.anim.right_out);

                break;
            case R.id.lyt_spin:
                showChoiceDialog();
                break;
            case R.id.lyt_gender:
                showChoiceDialog_gender();
                break;
            case R.id.lyt_spin_relationship:
                showChoiceDialog_relationship();
                break;
            case R.id.checkin:
                Intent intent=new Intent(this,CheckinActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.imv_photo:
                selectUserPhoto();
                break;
        }
    }

    public void showChoiceDialog() {

        final String[] items = {"Golf",
                "Run",
                "Ski & Snowboard",
                "Tennis"
        };

        final EditText firstinterest=(EditText) findViewById(R.id.edt_firstinterest);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your first interest.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                firstinterest.setText(items[item]);
                firstinter=items[item];
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_relationship() {

        final String[] items = {"Single",
                "In Relationship"
        };

        final EditText relationship=(EditText) findViewById(R.id.relationship);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
//        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                relationship.setText(items[item]);
                relations=relationship.getText().toString();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_gender() {

        final String[] items = {"Male",
                "Female"
        };

        final EditText relationship=(EditText) findViewById(R.id.relationship);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
//        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                //do something with edt.getText().toString();
//
//            }
//
//        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                gendr.setText(items[item]);
                Commons.thisuserGender=gendr.getText().toString();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectUserPhoto() {

        final String[] items = {"Take photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    takePhoto();
                } else {
                    fromGallery();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void fromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto() {

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        _imageCaptureUri = this.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                        _photoPath = saveFile.getAbsolutePath();

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap=bitmap;
            //            ui_userphoto.setImageBitmap(bitmap);
                        ui_userphoto.setImageBitmap(bitmap);

                        //           Constants.userphoto=ui_imvphoto.getDrawable();


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
                try {

                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    //   File saveFile = BitmapUtils.getOutputMediaFile(this);

                    File saveFile = new File(_photoPath);

                    InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                    BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                    in.close();

                    //set The bitmap data to image View
                    Commons.bitmap=bitmap;
                    ui_userphoto.setImageBitmap(bitmap);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

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
                break;
            }
        }
    }


    public void register() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_REGISTERUSER;

        try {
            String firstname = Commons.thisEntity.get_firstName();
            //       firstname = URLEncoder.encode(firstname, "utf-8");
            String lastname = Commons.thisEntity.get_lastName().replace(".","-");
            //       lastname = URLEncoder.encode(lastname, "utf-8");
            String email = Commons.thisEntity.get_email();
            //        email = URLEncoder.encode(email, "utf-8");
            String age_range = Commons.thisEntity.get_age_range();
            //        age_range = URLEncoder.encode(age_range, "utf-8");
            String city = Commons.thisEntity.get_city().replace(" ","%20");

            String job = Commons.thisEntity.get_job().replace("&","%26").replace(" ","%20");
//            job = URLEncoder.encode(job, "utf-8");
            String education = Commons.thisEntity.get_education().replace(" ","%20");

            String interests = Commons.thisEntity.get_interest().replace("&","%26").replace(",","-").replace(" ","%20");
//            interests = URLEncoder.encode(interests, "utf-8");
//            String imageurl = _photoPath.replace("\\","20%");
            String relationship = Commons.thisEntity.get_relations().replace(" ","%20");
            String publicName = Commons.thisEntity.get_publicName().replace(" ","%20");
            String lat= String.valueOf(Commons.thisEntity.get_userlat()).replace("-","%20").replace(".","-");
            String lng= String.valueOf(Commons.thisEntity.get_userlng()).replace("-","%20").replace(".","-");

            String params = String.format("/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s/%s",email,firstname, lastname,
                    age_range, city, job,education, interests, relationship, publicName, lat, lng);

            url += params;
            Log.d("API====",url);

        } catch (Exception ex) {
            return;
        }

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {

                parseRegisterResponse(json);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);

    }

    public void parseRegisterResponse(String json) {

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                _idx = response.getInt(ReqConst.RES_USERID);   Log.d("idx====",String.valueOf(_idx));

                uploadPhoto();

                //    showAlertDialog("Successfully registered on server.");

//                onSuccessRegister();

            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                showToast(getString(R.string.email_exist));
                _idx = response.getInt(ReqConst.RES_USERID);
                uploadPhoto();

            }
            else {
                closeProgress();
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            closeProgress();
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public void uploadPhoto() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_UPDATEUSERPROFILE;

        Log.d("request url :", url.toString());

        Map<String, String> params = new HashMap<>();

        params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
//        params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(0));
        params.put(ReqConst.PARAM_PHOTOURL, _photoPath);

        Log.d("Login params=====> :", params.toString());

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.d("login response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseLoginResponse(response);

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

                params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
//                params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(0));
                params.put(ReqConst.PARAM_PHOTOURL, _photoPath);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);


    }

    public void parseLoginResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("resultcode=====> :",success);

            if (success==String.valueOf(0)) {


            } else
            if(success==String.valueOf(102)){
                closeProgress();
                showToast("Unregistered user.");
            }else if(success==String.valueOf(103)){
                closeProgress();
                showToast("Upload file size error.");
            }else if (success == String.valueOf(ReqConst.CODE_EXISTEMAIL)) {

                //            showToast(getString(R.string.email_exist));

            }
            else{

//                closeProgress();
//                showAlertDialog(getString(R.string.email_exist));

                String error = response.getString(ReqConst.RES_ERROR);
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast(error);
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void onSuccessRegister() {

        Preference.getInstance().put(this,
                PrefConst.PREFKEY_USEREMAIL, Commons.thisEntity.get_email());

        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();overridePendingTransition(0,0);

        showToast("Successfully registered on server.");
    }


//    public void selectPhoto() {
//
//        final String[] items = {"Take photo", "Choose from Gallery"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int item) {
//                if (item == 0) {
//                    doTakePhoto();
//
//                } else {
//                    doTakeGallery();
//                }
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    public void doTakePhoto() {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        String picturePath = BitmapUtils.getTempFolderPath() + "photo_temp.jpg";
//        _imageCaptureUri = Uri.fromFile(new File(picturePath));
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
//        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);//????????????????????????///
//
//    }
//
//    private void doTakeGallery() {
//
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);
//
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        switch (requestCode) {
//
//            case Constants.CROP_FROM_CAMERA: {
//
//                if (resultCode == RESULT_OK) {
//                    try {
//
//                        File saveFile = BitmapUtils.getOutputMediaFile(this);
//
//                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
//                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
//                        Bitmap bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
//                        in.close();
//
//                        //set The bitmap data to image View
//                        ui_userphoto.setImageBitmap(bitmap);
//                        _photoPath = saveFile.getAbsolutePath();
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//            }
//            case Constants.PICK_FROM_ALBUM:
//
//                if (resultCode == RESULT_OK) {
//                    _imageCaptureUri = data.getData();
//                }
//
//            case Constants.PICK_FROM_CAMERA: {
//                try {
//
//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
//
//                    Intent intent = new Intent("com.android.camera.action.CROP");
//                    intent.setDataAndType(_imageCaptureUri, "image/*");
//
//                    intent.putExtra("crop", true);
//                    intent.putExtra("scale", true);
//                    intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
//                    intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
//                    intent.putExtra("aspectX", 1);
//                    intent.putExtra("aspectY", 1);
//                    intent.putExtra("noFaceDetection", true);
//                    //intent.putExtra("return-data", true);
//                    intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));
//
//                    startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
//        }
//    }

    public void uploadImage() {

        try {

            File file = new File(_photoPath);

            Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(0));
            params.put(ReqConst.PARAM_PHOTOURL, _photoPath);

            String url = ReqConst.SERVER_URL + ReqConst.REQ_UPLOADPHOTO;

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    showToast(getString(R.string.photo_upload_fail));
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse(json);
                }
            }, file, ReqConst.PARAM_FILE, params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VaCayApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            closeProgress();
            showToast(getString(R.string.photo_upload_fail));
        }



    }


    public void ParseUploadImgResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("resultAA===",String.valueOf(result_code));

            if (result_code == 0) {

//                showAlertDialog("Successfully registered on server.");

            } else {
                showToast(getString(R.string.photo_upload_fail));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.photo_upload_fail));
        }
    }
}

