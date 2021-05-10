package com.mv.vacay.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.main.provider.BroadmoorActivity;
import com.mv.vacay.main.provider.CompanyManagerActivity;
import com.mv.vacay.main.provider.ProviderHomeActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.MultiPartRequest;
import com.mv.vacay.widgets.KenBurnsActivity;
import com.mv.vacay.widgets.KenBurnsView;
import com.mv.vacay.widgets.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import pl.tajchert.nammu.PermissionCallback;

public class LoginRActivity extends KenBurnsActivity implements View.OnClickListener,KenBurnsView.TransitionListener {

    private static final String TAG = LoginRActivity.class.getSimpleName();

    private static final int TRANSITIONS_TO_SWITCH = 3;
    private ViewSwitcher mViewSwitcher;
    private int mTransitionsCount = 0;
    EditText email, pwd, name, company;
    TextView login, takePhoto, pickfromGallery,broadmoorUploadButton, signupButton, loginButton;
    CheckBox broadMoor;
    LinearLayout loginFrame, alert, linearLayout, broadmoorPicturePage, buttonPage;
    String emailInitial = "", pwdInitial = "", nameInitial = "";
    FrameLayout photo;
    CircularImageView providerPhoto,broadmoorLogo;
    FloatingActionButton gallery;
    FloatingActionButton camera;
    FloatingActionsMenu menu;
    TextInputLayout companyContainer;

    String str="";
    int REQUEST_CAMERA=0,SELECT_FILE = 1;
    Bitmap bm=null;
    int value=0;
    PermissionHelper permissionHelper;
    boolean broadmoorFlag=false;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri _imageCaptureUri;
    String _photoPath = "", _photoPathBroadmoor = "";
    Bitmap bitmap;
    private ProgressDialog _progressDlg;
    int _idx = 0;
    boolean _is_login = false,_is_broadmoorImage=false;
    File destination=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Commons.thisEntity=new UserEntity();
        permissionHelper = PermissionHelper.getInstance(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

//        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
//        img2.setTransitionListener(this);

        KenBurnsView img3 = (KenBurnsView) findViewById(R.id.img3);
        img3.setTransitionListener(this);

        broadMoor=(CheckBox)findViewById(R.id.setBroadmoor);
        if(Commons._is_companyManager)broadMoor.setVisibility(View.GONE);
        else broadMoor.setVisibility(View.VISIBLE);

        broadMoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        photo = (FrameLayout) findViewById(R.id.photo);
        providerPhoto = (CircularImageView) findViewById(R.id.providerPhoto);
        providerPhoto.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        if(Commons._is_companyManager){
            emailInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_CPROVIDEREMAIL, "");
            pwdInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_CPROVIDERPWD, "");
        }else if(Commons._is_broadmoor){
            emailInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_BPROVIDEREMAIL, "");
            pwdInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_BPROVIDERPWD, "");
        }else {
            emailInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_PROVIDEREMAIL, "");
            pwdInitial = Preference.getInstance().getValue(this, PrefConst.PREFKEY_PROVIDERPWD, "");
        }

        pwd = (EditText) findViewById(R.id.password);

        companyContainer=(TextInputLayout)findViewById(R.id.companyContainer);
        company=(EditText)findViewById(R.id.company);

        if(Commons._is_companyManager)companyContainer.setVisibility(View.VISIBLE);
        else companyContainer.setVisibility(View.GONE);

        signupButton=(TextView)findViewById(R.id.signupButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _is_login=false;
                buttonPage.setVisibility(View.GONE);
                photo.setVisibility(View.VISIBLE);
                company.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
                photo.startAnimation(animation);
                loginFrame.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                loginFrame.startAnimation(animation);
            }
        });

        loginButton=(TextView)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _is_login=true;
                buttonPage.setVisibility(View.GONE);
                photo.setVisibility(View.GONE);
                company.setVisibility(View.GONE);
                loginFrame.setVisibility(View.VISIBLE);
                broadMoor.setVisibility(View.GONE);
                TextInputLayout nameContainer=(TextInputLayout)findViewById(R.id.nameContainer);
                nameContainer.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                loginFrame.startAnimation(animation);
                photo.setVisibility(View.GONE);
                login.setText("Login as a Manager");
                if(emailInitial.length()>0)email.setText(emailInitial);
                if(pwd.getText().length()>0)pwd.setText("");
            }
        });

        login = (TextView) findViewById(R.id.email_sign_in_button);
        login.setOnClickListener(this);
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

                        if (!Commons._is_companyManager && _is_login && pwd.getText().length() > 0 && email.getText().length() > 0) {
                            login();
                        } else if (_is_login && email.getText().length() == 0) {
                            showToast("Please input your email.");
                        } else if (_is_login && pwd.getText().length() == 0) {
                            showToast("Please input your password.");
                        } else if (!Commons._is_companyManager && !_is_login
                                && checkValues()
                                ) {
                            registerProviderInfo();
//                            if(broadmoorFlag)
//                                uploadImageBroadmoorLogo();
                  //          uploadImageBroadmoorLogo();



//                            Preference.getInstance().put(getApplicationContext(),
//                                    PrefConst.PREFKEY_PROVIDERPWD, pwd.getText().toString());
//                            Preference.getInstance().put(getApplicationContext(),
//                                    PrefConst.PREFKEY_PROVIDEREMAIL, email.getText().toString());
//                            Preference.getInstance().put(getApplicationContext(),
//                                    PrefConst.PREFKEY_PROVIDERID, String.valueOf("2"));

                        }else if(Commons._is_companyManager){
                            if (_is_login && pwd.getText().length() > 0 && email.getText().length() > 0) {
                                login();
                            }
                            else if (_is_login && email.getText().length() == 0) {
                                showToast("Please input your email.");
                            } else if (_is_login && pwd.getText().length() == 0) {
                                showToast("Please input your password.");
                            }else if(!_is_login && checkValuesCompanyManager()){
                                registerCompanyManager();
                            }
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


        alert = (LinearLayout) findViewById(R.id.alert);
        buttonPage=(LinearLayout)findViewById(R.id.buttonPage);

        broadmoorPicturePage = (LinearLayout) findViewById(R.id.broadmoorPicturePage);

        broadmoorUploadButton=(TextView)findViewById(R.id.broadmoorButton);
        broadmoorUploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        broadmoorUploadButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        broadmoorUploadButton.setBackground(getDrawable(R.drawable.white_stroke));

                        uploadImageBroadmoorLogo();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        broadmoorUploadButton.getBackground().clearColorFilter();
                        broadmoorUploadButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        loginFrame = (LinearLayout) findViewById(R.id.email_login_form);

        broadmoorLogo=(CircularImageView) findViewById(R.id.logoImage);
        broadmoorLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setVisibility(View.VISIBLE);
                _is_broadmoorImage=true;
            }
        });

        takePhoto = (TextView) findViewById(R.id.takePhoto);
        takePhoto.setOnClickListener(this);
        pickfromGallery = (TextView) findViewById(R.id.pickfromGallery);
        pickfromGallery.setOnClickListener(this);
        ImageView cancel = (ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.setVisibility(View.GONE);
            }
        });

        menu=(FloatingActionsMenu)findViewById(R.id.right_labels);
        menu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                linearLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                linearLayout.setVisibility(View.GONE);
            }
        });

        gallery=(FloatingActionButton) findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);//
                startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_ALBUM);
  //              startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);

            }
        });
        camera=(FloatingActionButton) findViewById(R.id.camera);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    //            startActivityForResult(intent, REQUEST_CAMERA);

            }
        });
        linearLayout=(LinearLayout) findViewById(R.id.midlayer);

//        selectSignupLogin();
        buttonPage.setVisibility(View.VISIBLE);
        showButtons();
    }

    private void registerCompanyManager() {
        String url = ReqConst.SERVER_URL + "registerEmployer";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponseCompanyManager(response);

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

                params.put("adminName", name.getText().toString());
                params.put("adminEmail", email.getText().toString().trim());
                params.put("adminPassword", pwd.getText().toString().trim());
                params.put("adminCompany", company.getText().toString().trim());
                params.put("adminBroadmoor", String.valueOf(2));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponseCompanyManager(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                _idx = response.getInt("adminID");   Log.d("AdminID===>",String.valueOf(_idx));
                uploadImage(_idx);
            //    showToast("Successfully registered on server.");

            } else if(success.equals("101")){
                closeProgress();
                showToast("You are already registered with the email. Please login.");
                selectSignupLogin();
            }
            else {

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


    private void showButtons() {
        signupButton.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
        signupButton.startAnimation(animation);
        loginButton.setVisibility(View.VISIBLE);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromleft);
        loginButton.startAnimation(animation);
    }

    @Override
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
                        if(bitmap!=null){

                            Commons.bitmap_activity=bitmap;
                            if(_is_broadmoorImage) {
                                _photoPathBroadmoor = saveFile.getAbsolutePath();
                                destination=new File(_photoPathBroadmoor);
                                broadmoorLogo.setImageBitmap(bitmap);
                                menu.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.GONE);
                                Commons.imageUrl=_photoPathBroadmoor;
                                _is_broadmoorImage=false;
                                Log.e("PATH===>","BroadmoorPhoto: "+_photoPathBroadmoor);
                            }else {
                                _photoPath = saveFile.getAbsolutePath();
                                destination=new File(_photoPath);
                                providerPhoto.setVisibility(View.VISIBLE);
                                providerPhoto.setImageBitmap(bitmap);
                                menu.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.GONE);
                                Commons.imageUrl=_photoPath;
                                Log.e("PATH===>","Photo: "+_photoPath);
                            }
                        }
                        //           back();

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
                    menu.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                    return;
                }catch (Exception e){}

                try {
                    //_photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
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
                break;
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);

            }
            else if (requestCode == REQUEST_CAMERA)
            {
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
                onCaptureImageResult(data);
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

        destination = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(_is_broadmoorImage){
            broadmoorLogo.setImageBitmap(thumbnail);
            _is_broadmoorImage=false;
        }
        else providerPhoto.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getApplicationContext().getContentResolver(), data.getData());
                bm=getResizedBitmap(bm,300);
                providerPhoto.setImageBitmap(bm);
                _photoPath=data.getData().getPath();
                    _photoPath=imagetostrimg(bm);
                Log.e("PATH===>","Photo: "+_photoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ivImage.setImageBitmap(bm);
    }

    public String imagetostrimg(Bitmap bm){
        String bal="";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        bal = Base64.encodeToString(b, Base64.URL_SAFE);
        return bal;
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

    public void login() {

        String url = ReqConst.SERVER_URL + "loginAdmin";

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

                params.put("adminEmail", email.getText().toString().trim());
                params.put("adminPassword", pwd.getText().toString().trim());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse1(String json) {

        closeProgress();
        Log.d("JsonAAA====", json);
        try {
            JSONObject response = new JSONObject(json);

            String result_code = response.getString(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code.equals("0")) {

                //==========================================================

                JSONArray userInfo = response.getJSONArray("adminData");

                int id=1; String eml="", image="", password="", broadmoor="", company="", logo="", name="";

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    id = jsonUser.getInt("adminID");    Log.d("AdminID===>", String.valueOf(id));
                    eml= jsonUser.getString("adminEmail");
                    image=jsonUser.getString("adminImageUrl");
                    password=jsonUser.getString("adminPassword");
                    broadmoor=jsonUser.getString("adminBroadmoor");
                    company=jsonUser.getString("adminCompany");
                    logo=jsonUser.getString("adminLogoImageUrl");
                    name=jsonUser.getString("adminName");

                }

                //===================================================================

//                int id = response.getInt("adminID");
//                String eml= response.getString("adminEmail");
//                String image=response.getString("adminImageUrl");
//                String password=response.getString("adminPassword");
//                String broadmoor=response.getString("adminBroadmoor");
//                String company=response.getString("adminCompany");
//                String logo=response.getString("adminLogoImageUrl");
//                String name=response.getString("adminName");

                showToast("Success!");
                if(broadmoor.equals(String.valueOf(1))){
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_BPROVIDERPWD, password);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_BPROVIDEREMAIL, eml);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_BPROVIDERID, String.valueOf(id));
                    Intent intent=new Intent(getApplicationContext(), BroadmoorActivity.class);    // replace with toBroadmoor page

                    startActivity(intent);
                }
                else if(Commons._is_companyManager){
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_CPROVIDERPWD, password);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_CPROVIDEREMAIL, eml);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_CPROVIDERID, String.valueOf(id));
                    Intent intent=new Intent(getApplicationContext(), CompanyManagerActivity.class);
                    intent.putExtra("adname",name);
                    intent.putExtra("ademail",eml);
                    intent.putExtra("adimage",image);
                    intent.putExtra("adcompany",company);
                    intent.putExtra("logo",logo);
                    intent.putExtra("pwd",password);

                    startActivity(intent);
                }
                else {
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDERPWD, password);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDEREMAIL, eml);
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDERID, String.valueOf(id));
                    Intent intent=new Intent(getApplicationContext(), ProviderHomeActivity.class);

                    startActivity(intent);
                }
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);

            } else if (result_code.equals("113")) {

                showToast("Sorry, you aren't registered. Please sign up.");
                selectSignupLogin();

            } else if(result_code.equals("114")){
                showToast("Your password is incorrect. Please input again.");
            }
            else {

                showToast("Login failed...Try again.");
            }

        } catch (JSONException e) {

            showToast("Login failed...Try again.");

            e.printStackTrace();
        }

    }

        @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.providerPhoto:
    //            alert.setVisibility(View.VISIBLE);
    //            selectPhoto();
                menu.setVisibility(View.VISIBLE);
                break;
            case R.id.takePhoto:
                takePhoto();
                alert.setVisibility(View.GONE);
                break;
            case R.id.pickfromGallery:
                fromGallery();
                alert.setVisibility(View.GONE);
                break;
        }
    }

    public boolean checkValues(){
        if(name.getText().length()>0 && email.getText().length()>0 && email.getText().toString().contains("@") && pwd.getText().length()>0 && _photoPath.length()>0) {
            if(!broadMoor.isChecked() && Commons._is_broadmoor){
                showToast("Please click the broadmoor checkbox.");
                return false;
            }else if(broadMoor.isChecked() && !Commons._is_broadmoor){
                showToast("Do you want a retail store?");
                return false;
            }

            return true;
        }
        else if (name.getText().length()==0)showToast("Please input your name.");
        else if (email.getText().length()==0)showToast("Please input your email.");
        else if (email.getText().length()>0 && !email.getText().toString().contains("@"))showToast("Please check your email. Your email is unavailable.");
        else if(pwd.getText().length()==0)showToast("Please input your password.");
        else if(_photoPath.length()==0)showToast("Please input your photo.");

        return false;
    }

    public boolean checkValuesCompanyManager(){
        if(name.getText().length()>0 && email.getText().length()>0 && email.getText().toString().contains("@") &&
                pwd.getText().length()>0 && company.getText().length()>0 && _photoPath.length()>0) {

            return true;
        }
        else if (name.getText().length()==0)showToast("Please input your name.");
        else if (email.getText().length()==0)showToast("Please input your email.");
        else if (email.getText().length()>0 && !email.getText().toString().contains("@"))showToast("Please check your email. Your email is unavailable.");
        else if(pwd.getText().length()==0)showToast("Please input your password.");
        else if(company.getText().length()==0)showToast("Please input your company name.");
        else if(_photoPath.length()==0)showToast("Please input your photo.");

        return false;
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(getApplicationContext());
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

    public void selectSignupLogin() {

        final String[] items = {"Sign up with a new email", "Log in"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Login or Sign up...");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    _is_login=false;
                    photo.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translatefromright);
                    photo.startAnimation(animation);
                    loginFrame.setVisibility(View.VISIBLE);
                    animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                    loginFrame.startAnimation(animation);
                } else {
                    _is_login=true;
                    photo.setVisibility(View.GONE);
                    loginFrame.setVisibility(View.VISIBLE);
                    broadMoor.setVisibility(View.GONE);
                    TextInputLayout nameContainer=(TextInputLayout)findViewById(R.id.nameContainer);
                    nameContainer.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
                    loginFrame.startAnimation(animation);
                    photo.setVisibility(View.GONE);
                    login.setText("Login as a Manager");
                    if(emailInitial.length()>0)email.setText(emailInitial);
                    if(pwd.getText().length()>0)pwd.setText("");
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void fromGallery() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto() {

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

    public void registerProviderInfo() {

        String url = ReqConst.SERVER_URL + "registerAdmin";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

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

                params.put("adminName", name.getText().toString());
                params.put("adminEmail", email.getText().toString().trim());
                params.put("adminPassword", pwd.getText().toString().trim());
                params.put("adminCompany", "");
                if(broadMoor.isChecked())params.put("adminBroadmoor", String.valueOf(1));
                else params.put("adminBroadmoor", String.valueOf(0));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                _idx = response.getInt("adminID");   Log.d("AdminID===>",String.valueOf(_idx));
    //            uploadImageBroadmoorLogo();

                uploadImage(_idx);

//                showAlertDialog("Successfully registered on server.");

            } else if(success.equals("101")){
                closeProgress();
                showToast("You are already registered with the email. Please login.");
                selectSignupLogin();
            }
            else {

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

    public void uploadImage(int id) {

        try {

//            File file = new File(_photoPath);     //     /storage/emulated/0/DCIM/Camera/20170115_141323.mp4
            //        File file = new File("/storage/emulated/0/DCIM/Camera/20170115_141323.mp4");

            final Map<String, String> params = new HashMap<>();
            params.put("adminID", String.valueOf(id));

            String url = ReqConst.SERVER_URL + "uploadAdminImage";

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    Toast.makeText(getApplicationContext(),getString(R.string.photo_upload_fail), Toast.LENGTH_SHORT).show();
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse(json);
                    Log.d("imageJson===",json.toString());
                    Log.d("params====",params.toString());
                }
            }, destination, ReqConst.PARAM_FILE, params);

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
            Log.d("resultFF===",String.valueOf(result_code));

            if (result_code == 0) {

                if(Commons._is_companyManager || Commons._is_broadmoor){
        //            registerChatRoom(email.getText().toString().trim());
                    broadmoorPicturePage.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    destination=null;
                }else if(broadMoor.isChecked()){
        //            registerChatRoom(email.getText().toString().trim());
                    broadmoorPicturePage.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.VISIBLE);
                    destination=null;
                }else {
                    broadmoorPicturePage.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);

                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDERPWD, pwd.getText().toString().trim());
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDEREMAIL, email.getText().toString().trim());
                    Preference.getInstance().put(getApplicationContext(),
                            PrefConst.PREFKEY_PROVIDERID, String.valueOf(_idx));  Log.d("AdminID===>",Preference.getInstance().getValue(this, PrefConst.PREFKEY_PROVIDERID, ""));
                    showToast("Welcome! you registered successfully.");

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
                    loginFrame.startAnimation(animation);
                    Commons.thisEntity.set_email(email.getText().toString());
                    _photoPath="";
                    _photoPathBroadmoor="";

        //            registerChatRoom(email.getText().toString().trim());

                    if(broadMoor.isChecked()){
                        Intent intent=new Intent(getApplicationContext(), BroadmoorActivity.class);    // replace with toBroadmoor page
                        startActivity(intent);
                    }else if(Commons._is_companyManager){

                        Intent intent=new Intent(getApplicationContext(), CompanyManagerActivity.class);    // replace with toBroadmoor page
                        startActivity(intent);
                    }
                    else {
                        Intent intent=new Intent(getApplicationContext(), ProviderHomeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }

            } else if(result_code==113){
                showToast("Sorry, your email and password aren't registered already.");
            }else if(result_code==103){
                showToast("Sorry, you can't register this photo because it is too big!");
            }
            else {
                showToast(getString(R.string.photo_upload_fail));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.photo_upload_fail));
        }
    }

    public void uploadImageBroadmoorLogo() {

        try {

            File file = new File(_photoPathBroadmoor);     //     /storage/emulated/0/DCIM/Camera/20170115_141323.mp4
            //        File file = new File("/storage/emulated/0/DCIM/Camera/20170115_141323.mp4");

            final Map<String, String> params = new HashMap<>();
            params.put("adminID", String.valueOf(_idx));   Log.d("ADMINID===>",String.valueOf(_idx));

            String url = ReqConst.SERVER_URL + "uploadAdminLogoImage";

            showProgress();

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    Toast.makeText(getApplicationContext(),getString(R.string.photo_upload_fail),Toast.LENGTH_SHORT).show();
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgBroadmoorLogoResponse(json);
                    Log.d("imageJsonBroadmoor===",json.toString());
                    Log.d("paramsBroadmoor====",params.toString());
                }
            }, destination, ReqConst.PARAM_FILE, params);

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


    public void ParseUploadImgBroadmoorLogoResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("resultFFBroadmoor===",String.valueOf(result_code));

            if (result_code == 0) {

                destination=null;
                    if(broadMoor.isChecked()){
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_BPROVIDERPWD, pwd.getText().toString().trim());
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_BPROVIDEREMAIL, email.getText().toString().trim());
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_BPROVIDERID, String.valueOf(_idx));
                    }else if(Commons._is_companyManager){
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_CPROVIDERPWD, pwd.getText().toString().trim());
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_CPROVIDEREMAIL, email.getText().toString().trim());
                        Preference.getInstance().put(getApplicationContext(),
                                PrefConst.PREFKEY_CPROVIDERID, String.valueOf(_idx));
                    }
                    Log.d("AdminID===>", Preference.getInstance().getValue(this, PrefConst.PREFKEY_PROVIDERID, ""));
                    showToast("Welcome! you registered successfully.");

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
                    loginFrame.startAnimation(animation);
                    Commons.thisEntity.set_email(email.getText().toString());
                    _photoPath = "";
                    _photoPathBroadmoor = "";

                if (broadMoor.isChecked()) {
                    Intent intent = new Intent(getApplicationContext(), BroadmoorActivity.class);    // replace with toBroadmoor page
                    startActivity(intent);
                } else if (Commons._is_companyManager) {
                        Intent intent = new Intent(getApplicationContext(), CompanyManagerActivity.class);    // replace with toBroadmoor page
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ProviderHomeActivity.class);
                        startActivity(intent);
                    }
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);

            } else if(result_code==113){
                showToast("Sorry, your email and password aren't registered already.");
            }else if(result_code==103){
                showToast("Sorry, you can't register this photo because it is too big!");
            }
            else {
                showToast(getString(R.string.photo_upload_fail));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.photo_upload_fail));
        }
    }

    public void showAlerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please upload the Broadmoor logo.");

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImageBroadmoorLogo();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void registerChatRoom(final String email, final String photoUrl){

        String url = "https://vacay-42bcd.firebaseio.com/users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase("https://vacay-42bcd.firebaseio.com/users");

                if(s.equals("null")) {
                    reference.child(email).setValue(email);
                    //            onSuccessRegister();
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(email)) {
                            reference.child(email).setValue(email);
                            //                onSuccessRegister();
                        } else {
                            //                onSuccessRegister();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                closeProgress();
            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
                closeProgress();
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(LoginRActivity.this);
        rQueue.add(request);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

}





































































