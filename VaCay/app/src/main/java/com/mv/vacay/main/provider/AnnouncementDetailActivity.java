package com.mv.vacay.main.provider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.activity.BroadmoorProductPictureViewActivity;
import com.mv.vacay.models.AnnouncementEntity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AnnouncementDetailActivity extends AppCompatActivity {

    NetworkImageView logo;
    ImageView back, picture;
    TextView title, views, responses, company, audience, subject, callofAction, postingDate, description, messageOwner,signupButton;
    TextView adminEmail, surveyButton;
    ImageLoader _imageLoader;
    LinearLayout viewBar;
    private ProgressDialog _progressDlg;
    String aEmail="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement_detail);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        TextView title2=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title2.setTypeface(font);


        logo=(NetworkImageView)findViewById(R.id.logo);
        picture= (ImageView) findViewById(R.id.picture);

        adminEmail=(TextView)findViewById(R.id.adminemail);
        surveyButton=(TextView)findViewById(R.id.surveyButton) ;
        surveyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        surveyButton.setBackgroundResource(R.drawable.surveymonkeyicon2);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        surveyButton.setBackgroundResource(R.drawable.surveymonkeyicon);

                        if(Commons.announcement.getSurvey().startsWith("http") && Commons.announcement.getSurvey().contains("?usp=sf_link")) {
                            Intent intent=new Intent(getApplicationContext(), GoogleSurveyActivity.class);
                            intent.putExtra("adminEmail", aEmail);
                            intent.putExtra("surveyLink",Commons.announcement.getSurvey());
                            startActivity(intent);
                            overridePendingTransition(0,0);
                        }
                        else {
                            showToast("No survey provided!");
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        surveyButton.getBackground().clearColorFilter();
                        surveyButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        back=(ImageView)findViewById(R.id.back);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        back.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        back.setBackgroundColor(Color.BLACK);
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        back.getBackground().clearColorFilter();
                        back.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        viewBar=(LinearLayout)findViewById(R.id.viewBar);

        title=(TextView)findViewById(R.id.title);
        audience=(TextView)findViewById(R.id.audience);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        audience.setTypeface(font);


        company=(TextView)findViewById(R.id.company);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/futura-md-bt-bold-58e2b41ab199c.ttf");
        company.setTypeface(font);


        subject=(TextView)findViewById(R.id.subject);
        callofAction=(TextView)findViewById(R.id.callofAction);
        messageOwner=(TextView)findViewById(R.id.messageOwner);
        postingDate=(TextView)findViewById(R.id.postDate);
        description=(TextView)findViewById(R.id.description);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        description.setTypeface(font);


        views=(TextView)findViewById(R.id.views);
        responses=(TextView)findViewById(R.id.responses);

        if(!Commons._is_admin){
            viewBar.setVisibility(View.GONE);
        }else viewBar.setVisibility(View.VISIBLE);

        signupButton=(TextView)findViewById(R.id.signupButton);
        signupButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        signupButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        signupButton.setBackground(getDrawable(R.drawable.black_fill_rect));
                        if(!Commons._is_admin)
                            showAlertDialogConfirm("Do you want to attend?");
                        else {
                            Intent intent=new Intent(getApplicationContext(),SignedEmployeesViewActivity.class);
                            startActivity(intent);
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        signupButton.getBackground().clearColorFilter();
                        signupButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        final LinearLayout mediaButton=(LinearLayout)findViewById(R.id.mediaButton);

        mediaButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mediaButton.setBackgroundResource(R.drawable.green_fillround);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        mediaButton.setBackgroundResource(R.drawable.login_roundrect);

                        enterMedia(Commons.announcement);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        mediaButton.getBackground().clearColorFilter();
                        mediaButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        if(Commons.announcement.getLogoUrl().length()>0)
            logo.setImageUrl(Commons.announcement.getLogoUrl(),_imageLoader);

//        RoundedBitmapDrawable RBD = RoundedBitmapDrawableFactory.create(getResources(),image);
//
//        RBD.setCornerRadius(20);
//
//        RBD.setAntiAlias(true);

        Bitmap image = null;

        if(Commons.announcement.getPictureUrl().length()<1000) {
            image = drawableToBitmap(LoadImageFromWebOperations(Commons.announcement.getPictureUrl()));
            if(image.getWidth()>800 && image.getHeight()>800)
                image=getResizedBitmap(image,800);
            picture.setImageBitmap(image);
        }
        else {
            image=base64ToBitmap(Commons.announcement.getPictureUrl());
            if(image.getWidth()>800 && image.getHeight()>800)
                image=getResizedBitmap(image,800);
            picture.setImageBitmap(image);
        }
        final Bitmap finalImage1 = image;
        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(), BroadmoorProductPictureViewActivity.class);
                Commons.broadmoorImage= finalImage1;
                startActivity(intent);
                overridePendingTransition(R.anim.scale,R.anim.fade_off);
            }
        });
        title.setText(Commons.announcement.getTitle());
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogAnnounceName(title.getText().toString());
            }
        });
        audience.setText(Commons.announcement.getAudience());

        company.setText(Commons.announcement.getCompany());
        subject.setText(Commons.announcement.getSubject());
        callofAction.setText(Commons.announcement.getCallofAction());
        description.setText(Commons.announcement.getDescription());
        messageOwner.setText(Commons.announcement.getMessageOwnerEmail());
        postingDate.setText(Commons.announcement.getPostDate());
        views.setText(Commons.announcement.getViews());
        responses.setText(Commons.announcement.getResponses());

        if(!Commons._is_admin)
            signupAnnouncement(String.valueOf(0));

        getAdminEmail(String.valueOf(Commons.thisEntity.get_adminId()));
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public void getAdminEmail(String idx) {

        Log.d("AdminID===>",String.valueOf(idx));
        String url = ReqConst.SERVER_URL + "getAdminData";

        String params = String.format("/%s", idx);
        url += params; Log.d("URL===>",url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
                Log.d("AdminGetJson===>",json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("AdminResponse===>",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray userInfo = response.getJSONArray("adminData");

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);
                    String name=jsonUser.getString("adminName");
                    String email=jsonUser.getString("adminEmail");
                    String image=jsonUser.getString("adminImageUrl");

                    Log.d("AdminInfo===>",name+"/"+email+"/"+image);
                    aEmail=email;
                    adminEmail.setText(aEmail);
                }

            }else if(result_code==113){
            }
            else {
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void showAlertDialogAnnounceName(String name) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Announcement Title");
        alertDialog.setMessage(name);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try
        {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
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

    public void showAlertDialogConfirm(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(Commons.announcement.getTitle());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setVisibility(View.GONE);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(msg);
        alertDialog.setView(dialogView);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signupAnnouncement(String.valueOf(1));
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

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

    private void signupAnnouncement(final String index) {

        String url = ReqConst.SERVER_URL + "updateCount";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseAllAnnounceResponse(response,index);

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

                params.put("an_id", Commons.announcement.getIdx());
                params.put("em_id", String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0)));
                params.put("index", index);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseAllAnnounceResponse(String json,String index) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("EmResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                if(index.equals(String.valueOf(1)))
                    showAlertDialogSignupResult("Thank you!\nYou are Signed-Up!");
                else if(index.equals(String.valueOf(0)))
                    showAlertDialogSignupResult("Welcome!\nYou are visiting this announcement now!");
            }
            else {

                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast("Server connection failed!");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void showAlertDialogSignupResult(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(msg);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        final CircularImageView photo2=(CircularImageView) dialogView.findViewById(R.id.photo2);

        if(Commons.thisEntity.get_photoUrl().length()<1000) {
            photo.setImageUrl(Commons.thisEntity.get_photoUrl(), VaCayApplication.getInstance().getImageLoader());
        }
        else {
            photo2.setVisibility(View.VISIBLE);
            photo2.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(Commons.thisEntity.get_fullName());
        alertDialog.setView(dialogView);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        alertDialog.show();

    }

    public void enterMedia(AnnouncementEntity entity){
        getMedia(String.valueOf(entity.getIdx()),"announce");
    }

    public void getMedia(final String itemID, final String item) {

        String url = ReqConst.SERVER_URL + "get_media";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetMessagesResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("item_id", itemID);
                params.put("item", item);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetMessagesResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("MediaResponse===>",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);

            if (result_code.equals("0")) {

                JSONObject medias = response.getJSONObject("media");
                Log.d("Medias===>", medias.toString());

                MediaEntity mediaEntity=new MediaEntity();
                mediaEntity.setVideo(medias.getString("video_url"));
                mediaEntity.setYoutube(medias.getString("youtube_url"));
                mediaEntity.setImageA(medias.getString("image_a"));
                mediaEntity.setImageB(medias.getString("image_b"));
                mediaEntity.setImageC(medias.getString("image_c"));
                mediaEntity.setImageD(medias.getString("image_d"));
                mediaEntity.setImageE(medias.getString("image_e"));
                mediaEntity.setImageF(medias.getString("image_f"));

                if(medias.length()>0){

                    mediaEntity.setObjimage(Commons.announcement.getLogoUrl());
                    mediaEntity.setObjtitle(Commons.announcement.getTitle());

                    Intent intent=new Intent(getApplicationContext(),MediaActivity.class);
                    Commons.mediaEntity=mediaEntity;      Log.d("YouTube Url===>",Commons.mediaEntity.getYoutube());
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }else
                    Toast.makeText(getApplicationContext(),"No media!",Toast.LENGTH_SHORT).show();

            }else if(result_code.equals("1")){
                Toast.makeText(getApplicationContext(),"No place where medias are!",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Server Error!",Toast.LENGTH_SHORT).show();
        }
    }
}








































