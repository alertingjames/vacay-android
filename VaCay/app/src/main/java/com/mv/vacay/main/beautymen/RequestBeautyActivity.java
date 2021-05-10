package com.mv.vacay.main.beautymen;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.firebase.client.Firebase;
import com.google.gson.Gson;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.chat.UserDetails;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.main.SignupActivity;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.main.meetfriends.MessageActivity;
import com.mv.vacay.main.payment.CollectionActivity;
import com.mv.vacay.main.payment.DBCards;
import com.mv.vacay.main.payment.PayPalPaymentActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.sms.SendSMSActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.utils.MultiPartRequest;
//import com.sendbird.android.shadow.com.google.gson.Gson;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Recipient;
import com.stripe.model.Transfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.mv.vacay.main.payment.VerifyPaymentActivity.SECRET_KEY;

public class RequestBeautyActivity extends BaseActivity {

    ImageView beautyImage;
    NetworkImageView beautyImageNet;
    ImageView back;
    TextView beautyPrice,beautyCategory,beautyName,beautyDescription,password,company,email,location,available;
    ImageLoader _imageloader;
    TextView providerName, request, requestpay, requestLocation;
    EditText requestDescription;
    static TextView DateEdit;
    int i;
    int year, month, day,hour,minute;
    CircularImageView providerImage;
    CircularNetworkImageView providerImageNet;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private Animation.AnimationListener mAnimationListener;
    ViewFlipper mHomeflipper;

    public String cardHolderName = "";
    public String creditCardNumber = "";
    public String expiryDate = "";
    public String cvv = "";
    public int expmonth = 0;
    public int expyear = 0;
    public Stripe stripe;
    public SQLiteDatabase db;
    public Token stripeToken;

    AlertDialog alert=null;
    LinearLayout layout;
    boolean buck_updatev=false;
    String admin_accountId="";
    DBManager dbManager;
    ImageView viewloc;

    String proId="";
    private ProgressDialog _progressDlg;
    public static final String PUBLISHABLE_KEY = "pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV";            // secret_key:    sk_live_bxyrJ9CkAhUhVDUw4Zvw7hGW     publishable_key:     pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV

    @SuppressWarnings("deprecation")
    private final GestureDetector _detector = new GestureDetector(new SwipeGestureDetector());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_beauty);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        _imageloader = VaCayApplication.getInstance().getImageLoader();
        mHomeflipper = (ViewFlipper) findViewById(R.id.home_viewflipper);

        getProductInfo();

        DateEdit = (TextView) findViewById(R.id.request_date);
        if (Commons._location_set) {
            DateEdit.setText(Commons.requestDateTime);
        }
        DateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons._calendar_set=true;
                showTruitonTimePickerDialog();
                showTruitonDatePickerDialog();
            }
        });

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

        dbManager = new DBManager(this);
        dbManager.open();

        providerImage=(CircularImageView) findViewById(R.id.provider_profile);
        providerImageNet=(CircularNetworkImageView) findViewById(R.id.provider_profile_net);

        if(Commons.newBeautyEntity.getProviderPhotoUrl().length()>1000) {
            providerImageNet.setVisibility(View.GONE);
            providerImage.setImageBitmap(base64ToBitmap(Commons.newBeautyEntity.getProviderPhotoUrl()));
        }
        else {
            providerImageNet.setVisibility(View.VISIBLE);
            providerImageNet.setImageUrl(Commons.newBeautyEntity.getProviderPhotoUrl(),_imageloader);
        }

        providerName=(TextView)findViewById(R.id.provider_name);
        providerName.setText(Commons.newBeautyEntity.getFullName());

        email=(TextView)findViewById(R.id.providerEmail);
        email.setText(Commons.newBeautyEntity.getEmail());

        password=(TextView)findViewById(R.id.tempPwd);
        password.setText("Pwd: "+Commons.newBeautyEntity.getPassword());

        company=(TextView)findViewById(R.id.company_name);
        company.setText(Commons.newBeautyEntity.getCompanyName());

        location=(TextView)findViewById(R.id.location);
        location.setText(Commons.newBeautyEntity.getLocation());

        available=(TextView)findViewById(R.id.available);
        available.setText(Commons.newBeautyEntity.getEnableTime());

        viewloc=(ImageView) findViewById(R.id.viewLoc);

        ImageView availableicon = (ImageView)findViewById(R.id.availableicon);

        if (Commons.newBeautyEntity.getAvailable().equals("true") || Commons.newBeautyEntity.getAvailable().length()==0)
            availableicon.setImageResource(R.drawable.bluecircle);
        else availableicon.setImageResource(R.drawable.redcircle);

        cardHolderName=Commons.thisEntity.get_fullName();

        i=getIntent().getIntExtra("beautyNumber",0);

        try {
            beautyImage = (ImageView) findViewById(R.id.beauty_image);
            beautyImageNet = (NetworkImageView) findViewById(R.id.beauty_image_net);

            if (Commons.newBeautyEntity.getBeautyImageUrl().length() > 1000) {
                beautyImage.setVisibility(View.VISIBLE);
                beautyImage.setImageBitmap(base64ToBitmap(Commons.newBeautyEntity.getBeautyImageUrl()));
            }
            else {
                beautyImage.setVisibility(View.GONE);
                beautyImageNet.setImageUrl(Commons.newBeautyEntity.getBeautyImageUrl(),_imageloader);
            }
            beautyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Commons.resId = Commons.newBeautyEntity.getBeautyImageRes();
                    Commons.photoUrl = Commons.newBeautyEntity.getBeautyImageUrl();
                    Intent intent = new Intent(getApplicationContext(), ViewActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            });

            beautyPrice = (TextView) findViewById(R.id.beauty_price);

            beautyPrice.setTypeface(font);

            if(!Commons.newBeautyEntity.getBeautyPrice().contains("$")){
                beautyPrice.setText("$ "+Commons.newBeautyEntity.getBeautyPrice());
            }
            else beautyPrice.setText(Commons.newBeautyEntity.getBeautyPrice());

            beautyCategory = (TextView) findViewById(R.id.beauty_category);
            beautyCategory.setText(Commons.newBeautyEntity.getBeautyName());

            beautyName = (TextView) findViewById(R.id.beauty_name);
            beautyName.setTypeface(font);

            beautyName.setText(Commons.newBeautyEntity.getBeautySubName());

            beautyDescription = (TextView) findViewById(R.id.description);
            font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
            beautyDescription.setTypeface(font);

            beautyDescription.setText(Commons.newBeautyEntity.getBeautyDescription());

        } catch (NullPointerException e) {
            e.printStackTrace();

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        TextView buyproduct=(TextView)findViewById(R.id.buyproduct);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        buyproduct.setTypeface(font);


        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._location_set=false;
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        requestDescription=(EditText)findViewById(R.id.requestMessage);
        if (Commons._location_set) {
            requestDescription.setText(Commons.requestDescription);
        }

        requestLocation = (TextView) findViewById(R.id.request_location);
        if (Commons._location_set) {
            requestLocation.setText(Commons.loc_url);
            viewloc.setVisibility(View.VISIBLE);
            viewloc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewlocation(Commons.loc_url);
                }
            });
            //    Commons._location_set=false;
        }
        requestLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(requestDescription.getText().length()>0){
                    Commons.requestDescription=requestDescription.getText().toString();
                }
                if(DateEdit.getText().length()>0){
                    Commons.requestDateTime=DateEdit.getText().toString();
                }
                Intent intent = new Intent(getApplicationContext(), com.mv.vacay.main.beautymen.RequestLocationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            }
        });

        request = (TextView) findViewById(R.id.request);
        request.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        request.setBackground(getDrawable(R.drawable.green_fillround));
                        request.setTextColor(ColorStateList.valueOf(Color.WHITE));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        request.setBackground(getDrawable(R.drawable.white_stroke));
                        request.setTextColor(ColorStateList.valueOf(Color.WHITE));

                        Commons._calendar_set = false;
                        Commons._location_set = false;

                        Commons._datetime=DateEdit.getText().toString();

                        if(Commons._datetime.length()>0 && Commons.loc_url.length()>0 && requestDescription.getText().length()>0) {
                            sendMsg();
                        }
                        else
                            Toast.makeText(getApplicationContext(),"Please check empty field", Toast.LENGTH_SHORT).show();

//                        showAlertDialogRequest("Did you see this provider's schedule?");

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        request.getBackground().clearColorFilter();
                        request.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        requestpay=(TextView)findViewById(R.id.payNow);
        requestpay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        requestpay.setBackground(getDrawable(R.drawable.green_fillround));
                        requestpay.setTextColor(ColorStateList.valueOf(Color.WHITE));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        requestpay.setBackground(getDrawable(R.drawable.white_stroke));
                        requestpay.setTextColor(ColorStateList.valueOf(Color.WHITE));

//                        String myEmployeeId=String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
//                        if(!myEmployeeId.equals("0"))
//                            selectPaymentMethod2();
//                        else {
//                            selectCardRegistry();
//                        }

                        getAdminPaymentAccountId();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        requestpay.getBackground().clearColorFilter();
                        requestpay.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

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
            String datetimeStr = "";
            if(hourOfDay>12){
                hourOfDay=hourOfDay-12;
                if(hourOfDay<10) {
                    datetimeStr = " - 0" + hourOfDay + ":" + minute + " PM";
                }
                else if(minute<10){
                    datetimeStr = " - " + hourOfDay + ":0" + minute + " PM";
                }
                else if(hourOfDay<10 && minute<10){
                    datetimeStr = " - 0" + hourOfDay + ":0" + minute + " PM";
                }
                else {
                    datetimeStr = " - " + hourOfDay + ":" + minute + " PM";
                }
            }else {
                if(hourOfDay<10) {
                    datetimeStr = " - 0" + hourOfDay + ":" + minute + " AM";
                }
                else if(minute<10){
                    datetimeStr = " - " + hourOfDay + ":0" + minute + " AM";
                }
                else if(hourOfDay<10 && minute<10){
                    datetimeStr = " - 0" + hourOfDay + ":0" + minute + " AM";
                }
                else {
                    datetimeStr = " - " + hourOfDay + ":" + minute + " AM";
                }
            }
            DateEdit.setText(DateEdit.getText() + datetimeStr);
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
            if(day<10)
                DateEdit.setText(monthes[month] + " 0" + day + "," + year);
            else
                DateEdit.setText(monthes[month] + " " + day + "," + year);
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

    private  void viewlocation(String locationInfo) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Request location");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        photo.setVisibility(View.GONE);
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        textview.setTypeface(font);
        textview.setText(locationInfo);
        builder.setView(dialogView);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(RequestBeautyActivity.this, R.anim.left_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(RequestBeautyActivity.this, R.anim.left_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showNext();

                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(RequestBeautyActivity.this, R.anim.right_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(RequestBeautyActivity.this,R.anim.right_out));
                    // controlling animation
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);
                    mHomeflipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public void getProductInfo() {

        Commons.productEntities.clear();

        String url = ReqConst.SERVER_URL + "getProductInfo";

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

                params.put("proid", String.valueOf(Commons.newBeautyEntity.get_proIdx()));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        //      ui_RefreshLayout.setRefreshing(false);
        closeProgress();

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("response===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS) {

                JSONArray products = response.getJSONArray("productInfo");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("products===", products.toString());

                for (int i = 0; i < products.length(); i++) {

                    JSONObject jsonBeauty = (JSONObject) products.get(i);

                    BeautyProductEntity productEntity = new BeautyProductEntity();

                    productEntity.set_idx(jsonBeauty.getInt("itemid"));

                    productEntity.setBrand(jsonBeauty.getString("itemBrand"));
                    productEntity.setProduct(jsonBeauty.getString("itemProduct"));
                    productEntity.setProductName(jsonBeauty.getString("itemName"));
                    productEntity.setSize(jsonBeauty.getString("itemSize"));
                    productEntity.setPrice(jsonBeauty.getString("itemPrice"));
                    productEntity.setDescription(jsonBeauty.getString("itemDescription"));
                    productEntity.setProductImageUrl(jsonBeauty.getString("itemPictureUrl")); Log.d("Picture====>",productEntity.getProductImageUrl());
                    productEntity.setInventoryNumber(jsonBeauty.getString("itemInventoryNum"));
                    productEntity.setSaleStatus(jsonBeauty.getString("itemSaleStatus"));
                    productEntity.setLocation(Commons.newBeautyEntity.getLocation());
                    productEntity.setCompanyName(Commons.newBeautyEntity.getCompanyName());


                    Commons.productEntities.add(0, productEntity);
                    Log.d("NUMBER===", String.valueOf(Commons.beautyEntities_net.size()));
                }
                for (int i = 0; i <Commons.productEntities.size(); i++) {

                    if (Commons.productEntities.get(i).getProductImageUrl().length() > 1000 ) {
                        final ImageView imageView = new ImageView(this);
                        imageView.setImageBitmap(base64ToBitmap(Commons.productEntities.get(i).getProductImageUrl()));
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imageView.setClickable(true);
                        imageView.setId(1000 + i);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                Commons.beautyProductEntity = Commons.productEntities.get(v.getId() - 1000);
                                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                                startActivity(intent);
                            }
                        });
//                mHomeflipper.removeView(imageView);
                        mHomeflipper.addView(imageView);
                    }else {
                        final NetworkImageView imageViewNet = new NetworkImageView(this);
                        imageViewNet.setImageUrl(Commons.productEntities.get(i).getProductImageUrl(),_imageloader);
                        imageViewNet.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            imageView.setClickable(true);
                        imageViewNet.setId(1000 + i);
                        imageViewNet.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
                                Commons.beautyProductEntity = Commons.productEntities.get(v.getId() - 1000);
                                Intent intent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                                startActivity(intent);
                            }
                        });
//                mHomeflipper.removeView(imageView);
                        mHomeflipper.addView(imageViewNet);
                    }

                    mHomeflipper.setAutoStart(true);
                    mHomeflipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.right_in));
                    mHomeflipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.left_out));
                    mHomeflipper.setFlipInterval(2000);
                    mHomeflipper.getInAnimation().setAnimationListener(mAnimationListener);

                    mHomeflipper.startFlipping();


                    mHomeflipper.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            _detector.onTouchEvent(motionEvent);
                            return true;
                        }
                    });
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false

        int MY_SCAN_REQUEST_CODE = 100;
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK && requestCode == 2) {

//            cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                creditCardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                expiryDate = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

                expmonth = Integer.parseInt(expiryDate.substring(0, 2));
                expyear = Integer.parseInt(expiryDate.substring(3));
            }
            if (requestCode == 100) {

                if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                    CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                    // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                    creditCardNumber = scanResult.cardNumber;

                    if (scanResult.isExpiryValid()) {
                        expmonth = scanResult.expiryMonth;
                        expyear = scanResult.expiryYear;

                    }

                    if (scanResult.cvv != null) {
                        cvv = scanResult.cvv;
                    }


                } else {
                    showToast("Scan Canceled");
                }
            }

            createCreditCard();

    }

    public void createCreditCard() {

        Card card = new Card(creditCardNumber, expmonth, expyear, cvv);
        if (card.validateCard() && card.validateCVC() && card.validateExpiryDate()) {
            showToast("Valid Card");
            card.setName(cardHolderName);
            createToken(card);
        } else showToast("Invalid Card");

    }

    public void createToken(Card card) {
        _progressDlg=ProgressDialog.show(this, "", "Please wait...\nVerifying this payment...",true);
        try {

            stripe = new Stripe(this);

            stripe.createToken(card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            _progressDlg.dismiss();
                            // Send token to your server
                            showToast("Successfully verified!");
                            Log.d("Token===>",token.getId());
                            storeCard(token);
                            stripeToken=token;
//                            payBeautyService(stripeToken);

                            try {
                                payBeautyService2(stripeToken);
                            } catch (CardException e) {
                                e.printStackTrace();
                            } catch (APIException e) {
                                e.printStackTrace();
                            } catch (AuthenticationException e) {
                                e.printStackTrace();
                            } catch (InvalidRequestException e) {
                                e.printStackTrace();
                            } catch (APIConnectionException e) {
                                e.printStackTrace();
                            }
                        }

                        public void onError(Exception e) {
                            _progressDlg.dismiss();
                            showToast("Token error==>"+e.getMessage());
                            Log.e("Log_Stripe", "token error", e);
                        }
                    }
            );
        } catch (Exception eAu) {
            _progressDlg.dismiss();
            showToast("Auth error==>"+eAu.getMessage());
            Log.e("Log_Stripe", "Auth error", eAu);
        }
    }

    private void storeCard(Token token) {

        db = null;
        try {
            DBCards cardsDB = new DBCards(this, "DBCards", null, 1);
            db = cardsDB.getWritableDatabase();
            synchronized (db) {
                ContentValues cv = new ContentValues();
                cv.put("tokens", tokenToString(token));
                db.insert("Cards", null, cv);
            }
        } catch (Exception e) {
            Log.e("Log_Stripe", "DB error");

        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public String tokenToString(Token token) {
        Gson gson = new Gson();
        return gson.toJson(token);

    }

    public void selectPaymentMethod() {

        final String[] items = {"I use PayPal","I use Stripe"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select...");
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
                    if(Commons.newBeautyEntity.getEmail().length()==0){
                        showAlertDialog("Sorry, this provider's PayPal account isn't verified.");
                    }else {
                        Commons.payEmail = Commons.newBeautyEntity.getEmail();
                        Commons.payPrice = Commons.newBeautyEntity.getBeautyPrice();
                        Commons.payName = Commons.newBeautyEntity.getBeautySubName();
                        Intent intent = new Intent(getApplicationContext(), PayPalPaymentActivity.class);
                        startActivity(intent);
                    }
                } else if (item == 1) {
                    selectCardRegistry();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectPaymentMethod2() {

        final String[] items = {"VaCay bucks status","Stripe payment"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Options:");
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
                    getVaCayBucksInfo();
                } else if (item == 1) {
//                    registerUsedBuck();
                    selectCardRegistry();
                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectCardRegistry() {

        final String[] items = {"A New Card", "The Old Card","Card Collection"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Select...");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
//                if (item == 0) {
//                    Intent intent = new Intent(getApplicationContext(), VerifyPaymentActivity.class);
//
//                    intent.putExtra("proid",String.valueOf(Commons.thisEntity.get_idx()));
//                    intent.putExtra("proemail",Commons.thisEntity.get_email());
//                    intent.putExtra("proName",Commons.thisEntity.get_fullName());
//                    intent.putExtra("proFirst",Commons.thisEntity.get_firstName());
//                    intent.putExtra("proLast",Commons.thisEntity.get_lastName());
//        //            startActivity(intent);
//                }
//                else
                if (item == 0) {

//                    showAlertDialogPay("Successfully Paid!");

                    if(Commons.newBeautyEntity.getToken().length()==0 || admin_accountId.length()==0){
                        showToast("Sorry, this provider's payment isn't verified.");
                    }else onScanPress(findViewById(android.R.id.content));

                } else if(item == 1){
                    if(Commons.newBeautyEntity.getToken().length()==0 || admin_accountId.length()==0){
                        showToast("Sorry, this provider's payment isn't verified.");
                    }else registerOldCard();
                }else if(item == 2){
                    Intent a = new Intent(getApplicationContext(), CollectionActivity.class);
                    startActivity(a);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void registerOldCard(){
        final int GET_NEW_CARD = 2;
        Intent intent = new Intent(this, CardEditActivity.class);
        startActivityForResult(intent, GET_NEW_CARD);
    }

    public void showAlertDialogPay(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sendPayInfoBeautyService();
                        sendPayInfoBeautyService2();
                    }
                });
        alertDialog.show();

    }

    public void showAlertDialog(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogRequest(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Commons._calendar_set = false;
                        Commons._location_set = false;
                        Commons._request_set = true;
                        Commons.requestMessage=
                                "REQUEST\n" +
                                        " Date: "+DateEdit.getText().toString() + "\n" +
                                        " Location: " + Commons.loc_url + "\n" +
                                        " LatLang: " + String.valueOf(Commons.requestLatlng)+"\n"+
                                        " Description: " + requestDescription.getText().toString();
                        Commons.loc_url="";

                        if(Commons.newBeautyEntity.getProviderPhotoUrl().length()>0) {
                            try {
                                Commons.userEntity.set_photoUrl(Commons.newBeautyEntity.getProviderPhotoUrl()); Log.d("IMAGEurl===",String.valueOf(Commons.beautyEntity.get_provider_imageURL()));
                            }catch (NullPointerException e){}
                        }
                        try {
                            Commons.userEntity.set_name(Commons.newBeautyEntity.getFullName());  Log.d("PRONAME===",String.valueOf(Commons.beautyEntity.get_provider_name()));
                            Commons.userEntity.set_email(Commons.newBeautyEntity.getEmail());
                        }catch (NullPointerException e){}

                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0, 0);
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "No, Sorry",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    private void registerUsedBuck() {

        String url = ReqConst.SERVER_URL + "addEmUsedBuck";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUsedBuckResponse(response);

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

                params.put("em_id", String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0)));
                params.put("amount", Commons.newBeautyEntity.getBeautyPrice().replace("$",""));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUsedBuckResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("EmResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                showAlertDialogFromBuck("You used $"+Commons.newBeautyEntity.getBeautyPrice().replace("$","")+" in VaCay bucks");
            }
            else if (success.equals("100")) {

                showAlertDialogFromBuck("You can't exceed given buck");
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

    public void showAlertDialogFromBuck(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("VaCay Bucks' Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getVaCayBucksInfo();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void getVaCayBucksInfo() {

        String idx = String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));

        String url = ReqConst.SERVER_URL + "getVaCayBucksInfo";

        String params = String.format("/%s", idx);
        url += params; Log.d("URL===>",url);

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBucksResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetBucksResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("BuckResponse===>",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray userInfo = response.getJSONArray("bucks_data");

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    String em_id=jsonUser.getString("em_id");
                    String em_name=jsonUser.getString("em_name");
                    String em_image=jsonUser.getString("em_image");
                    String em_givenbuck=jsonUser.getString("em_givenbuck");
                    String em_usedbuck=jsonUser.getString("em_usedbuck");

                    String info = "Given buck: "+em_givenbuck+"\n"+
                            "Used buck: "+em_usedbuck+"\n"
                            +
                            "Rest of buck: $"+String.valueOf(Float.valueOf(em_givenbuck.replace("$","").replace(",", ""))-Float.valueOf(em_usedbuck.replace("$","").replace(",", "")));
                    showBuckInfo(em_image,info);
                }

            }else if(result_code==113){
                showToast("Unregistered employee");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    private  void showBuckInfo(final String image, final String infomation) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        builder.setTitle("Your Bucks Status:");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_buck_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        if(image.length()>0)
            photo.setImageUrl(image,_imageLoader);
        else photo.setVisibility(View.GONE);

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(infomation);
        layout = (LinearLayout) dialogView.findViewById(R.id.updateLayout);
        if(buck_updatev)
            layout.setVisibility(View.VISIBLE);
        else layout.setVisibility(View.GONE);
        final EditText editview = (EditText) dialogView.findViewById(R.id.updatedView);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                buck_updatev=false;
                if(editview.getText().length()>0){
                    updateGivenBuck(editview.getText().toString());
                }else {
                    alert.dismiss();
                }
            }
        });
        builder.setNeutralButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alert.dismiss();
                buck_updatev=true;
                showBuckInfo(image,infomation);
            }
        });

        alert = builder.create();
        alert.show();
    }

    private void updateGivenBuck(final String buck) {

        String url = ReqConst.SERVER_URL + "updateGivenBuck";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateBuckResponse(response);

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

                params.put("em_id", String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0)));
                params.put("amount", buck.replace("$",""));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateBuckResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("EmResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                showAlertDialogFromBuck("You have updated your given buck");
            }
            else if (success.equals("99")) {

                showAlertDialog("Please enter amount more than old");
            }
            else if (success.equals("100")) {

                showAlertDialogFromBuck("You can't exceed given buck limit");
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

    public void payBeautyService2(final Token token) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        com.stripe.Stripe.apiKey = SECRET_KEY;

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", ((int)Math.round(Float.parseFloat(Commons.newBeautyEntity.getBeautyPrice().replace("$","").replace(",", "").trim())*100)));
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token.getId());
        chargeParams.put("description", "Charge for " + Commons.newBeautyEntity.getEmail());
// ^ obtained with Stripe.js
        Charge charge=Charge.create(chargeParams);

        if(charge.getId().length()>0){
            double providerOffer= Float.parseFloat(Commons.newBeautyEntity.getBeautyPrice().replace("$","").replace(",", "").trim())*100*0.8f*Float.parseFloat(Commons.newBeautyEntity.getServicePercent().replace("%","").replace(",", ""))*0.01;

            Map<String, Object> transferParams = new HashMap<String, Object>();
            transferParams.put("amount", ((int)Math.round(providerOffer)));
            transferParams.put("currency", "usd");
            transferParams.put("destination", Commons.newBeautyEntity.getToken());

            Transfer transfer=Transfer.create(transferParams);
            if(transfer.getId().length()>0) {

                double providerOffer2= Float.parseFloat(Commons.newBeautyEntity.getBeautyPrice().replace("$","").replace(",", "").trim())*100*0.8f*(100-Float.parseFloat(Commons.newBeautyEntity.getServicePercent().replace("%","").replace(",", "")))*0.01;

                Map<String, Object> transferParams2 = new HashMap<String, Object>();
                transferParams2.put("amount", ((int)Math.round(providerOffer2)));
                transferParams2.put("currency", "usd");
                transferParams2.put("destination", admin_accountId);

                Transfer transfer2=Transfer.create(transferParams2);

                if(transfer2.getId().length()>0)
                    showAlertDialogPay("Successfully Paid!");
                else
                    showToast("Payment transfer failed");
            }
            else showToast("Payment transfer failed");
        }else showToast("Charge failed");

    }

    public void sendPayInfoBeautyService2() {

        String url = ReqConst.SERVER_URL + "savePaySendMail"; Log.d("API===>",url);

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseSendPayInfoResponse2(response);

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

                params.put("senderEmail", Commons.thisEntity.get_email());   Log.d("Me===>",Commons.thisEntity.get_email());
                params.put("receiverEmail", Commons.newBeautyEntity.getEmail());   // cent
//                params.put("receiverEmail", "alertingjames@gmail.com");
                params.put("paidMoney", Commons.newBeautyEntity.getBeautyPrice().replace("$",""));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseSendPayInfoResponse2(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("Response=====> :",response.toString());

            String status = response.getString("result");  Log.d("Status=====> :",status);

            if (status.equals("0")) {
                showToast("Payment Message Sent!");
                if(Commons.thisEntity.get_adminId()>0)
                    registerUsedBuck();
            }
            else {
                showAlertDialog("Payment mailing failed");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    private void getAdminPaymentAccountId() {

        String url = ReqConst.SERVER_URL + "getAdminPaymentAccountId";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseGetAdminPaymentAccountIdResponse(response);

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

                params.put("proEmail", Commons.newBeautyEntity.getEmail());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseGetAdminPaymentAccountIdResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("Response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {
                admin_accountId=response.getString("accountid");
                if(Commons.newBeautyEntity.getToken().length()>0) {
                    String myEmployeeId = String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));
                    Log.d("EMID===>", myEmployeeId);
                    if (!myEmployeeId.equals("0"))
                        selectPaymentMethod2();
                    else {
                        selectCardRegistry();
                    }
                }else showToast("Sorry, this provider's payment isn't verified.");
            }
            else if (success.equals("100")) {
                showToast("Unverified provider!");
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

    String message="";
    int _idx=0;
    File file;
    Firebase reference1, reference2, reference3;
    boolean is_typing=false;

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

                Log.d("Debug===>", error.toString());
                closeProgress();
                showAlertDialog("Connection to server failed.");

                Commons.messageForDate="";
                Commons._datetime="";
                Commons.requestMessage="";
                Commons.speechMessage="";

                DateEdit.setText("");
                requestLocation.setText("");
                requestDescription.setText("");
                viewloc.setVisibility(View.GONE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                Date date=new Date();
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm");
                String sendDateTime = formatter.format(date);

                message= "Hi, "+Commons.newBeautyEntity.getFullName()+"\n"
                        +Commons.thisEntity.get_name()+" wants "+Commons.newBeautyEntity.getBeautySubName()+" as following:\n"
                        +"Requested Date & Time:\n   "+Commons._datetime+"\n"
                        +"Request Location:\n   "+Commons.loc_url+"\n"
                        +"Service Category: "+Commons.newBeautyEntity.getBeautyName()+"\n"
                        +"Service Name: "+Commons.newBeautyEntity.getBeautySubName()+"\n"
                        +"Description: "+requestDescription.getText().toString()+"\n\n"
                        +"Please review the information. If you have questions, you can reply directly to the customer. If you want to accept or decline, please click here."+"\n"
                        +"Thanks\n"+sendDateTime+"\n"+Commons.thisEntity.get_name();

                if(Commons.loc_url!=""){
                    Commons.loc_url="";
                }

                params.put("name", Commons.thisEntity.get_name());  Log.d("Name===>", params.get("name"));
                params.put("photo_url", Commons.thisEntity.get_photoUrl());   Log.d("Photo===>", params.get("photo_url"));
                params.put("from_mail", Commons.thisEntity.get_email());   Log.d("FEmail===>", params.get("from_mail"));
                params.put("to_mail", Commons.newBeautyEntity.getEmail());   Log.d("TEmail===>", params.get("to_mail"));
                params.put("text_message", message);   Log.d("Message===>", params.get("text_message"));
                params.put("request_date", sendDateTime);   Log.d("RequestDate===>", params.get("request_date"));
                params.put("service", Commons.newBeautyEntity.getBeautySubName());   Log.d("Service===>", params.get("service"));
                params.put("service_reqdate", Commons._datetime);

                try {
                    if(Commons.requestLatlng!=null) {
                        params.put("lon_message", String.valueOf(Commons.requestLatlng.longitude));
                        params.put("lat_message", String.valueOf(Commons.requestLatlng.latitude));
                    }
                    else {
                        params.put("lon_message", "0.0");
                        params.put("lat_message", "0.0");
                    }

                    Log.d("Lat===>", params.get("lat_message"));
                    Log.d("Lng===>", params.get("lon_message"));

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
                Commons.speechMessage="";

                DateEdit.setText("");
                requestLocation.setText("");
                requestDescription.setText("");
                viewloc.setVisibility(View.GONE);

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

                Commons.messageForDate="";
                Commons._datetime="";
                Commons.requestMessage="";
                Commons.speechMessage="";

                DateEdit.setText("");
                requestLocation.setText("");
                requestDescription.setText("");
                viewloc.setVisibility(View.GONE);
                showAlertDialog(getString(R.string.error));
            }

        } catch (JSONException e) {

            e.printStackTrace();  Log.d("ERROR===>",e.getMessage());
            Commons.messageForDate="";
            Commons._datetime="";
            Commons.requestMessage="";
            Commons.speechMessage="";

            DateEdit.setText("");
            requestLocation.setText("");
            requestDescription.setText("");
            viewloc.setVisibility(View.GONE);
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

        String url = ReqConst.SERVER_URL + "sendMailMes";

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
                params.put("message", message);

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
                    if(dbManager.getAllMembers().get(i).get_email().equals(Commons.newBeautyEntity.getEmail())){
                        dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
                    }
                }

                if(Commons.newBeautyEntity.getFullName().length()>0)
                    dbManager.insert(Commons.newBeautyEntity.getFullName(), Commons.newBeautyEntity.getEmail(),Commons.newBeautyEntity.getProviderPhotoUrl(),"0");

                String myEmployeeId=String.valueOf(Preference.getInstance().getValue(this, PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
                sendNotification();
            }
        }catch (JSONException e){

        }
    }

    public void sendNotification(){

        UserDetails.username= Commons.thisEntity.get_email().replace(".com","").replace(".","ddoott");
        UserDetails.chatWith= Commons.newBeautyEntity.getEmail().replace(".com","").replace(".","ddoott");

        Firebase.setAndroidContext(this);
//        reference1 = new Firebase("https://androidchatapp-76776.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        reference3 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/" + UserDetails.chatWith + "/"+UserDetails.username);

        Map<String, String> map = new HashMap<String, String>();
        map.put("message", message);
        map.put("time", String.valueOf(new Date().getTime()));
        map.put("image", "");
        map.put("video", String.valueOf(_idx));
        map.put("lat", Commons.newBeautyEntity.getBeautySubName());
        map.put("lon", Commons._datetime);
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

        Toast.makeText(getApplicationContext(),"Booking message sent!",Toast.LENGTH_SHORT).show();

        showAlertDialogForBooking("Your provider was contacted and will let you know if he/she accepted. Thank you!\nDo you want SMS for booking?");
    }

    public void showAlertDialogForBooking(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setIcon(R.drawable.noti);
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Commons._customer_booking=true;
                        Commons.requestMessage=message;
                        Intent intent=new Intent(getApplicationContext(),SendSMSActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                    }
                });

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "No",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.show();

    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


}




































