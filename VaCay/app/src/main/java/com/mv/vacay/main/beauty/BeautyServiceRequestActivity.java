package com.mv.vacay.main.beauty;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.manager.RestaurantManagerActivity;
import com.mv.vacay.main.meetfriends.MessageActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.Calendar;

public class BeautyServiceRequestActivity extends FragmentActivity implements View.OnClickListener {

    ImageView back;
    NetworkImageView beautyImage;
    CircularNetworkImageView providerImage;
    TextView providerName, requestpay, requestLocation;
    EditText beautyDescription;
    TextView accept, beautyPrice,beautyName;
    LinearLayout admin;
    static EditText DateEdit;
    int year, month, day,hour,minute;

    private static final String TAG = "VaCayPaymentActivity";

    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
    private static final String CONFING_CLIENT_ID = "AexEJ3-oVFtiToF6YcrYWNCUDgbiJi84VaT4pqjShl1v1BBRbnxq8JTo3Ncz3b1RiK8yIQtaRfJtFZT4";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration  config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFING_CLIENT_ID)
            .acceptCreditCards(true)
            .merchantName("Hipster Store")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.paypal.com/webapps/mpp/ua/privacy-full"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.paypal.com/webapps/mpp/ua/useragreement-full"));

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_service_request);

        ImageLoader _imageloader = VaCayApplication.getInstance().getImageLoader();

        DateEdit = (EditText) findViewById(R.id.request_date);
        if(Commons.beautyEntity.get_request_date().length()!=0)
            DateEdit.setText(Commons.beautyEntity.get_request_date());
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

        Commons.userEntity=new UserEntity();

        beautyImage = (NetworkImageView) findViewById(R.id.beauty_image);

        if (Commons.beautyEntity.get_beauty_imageURL().length()>0)
            beautyImage.setImageUrl(Commons.beautyEntity.get_beauty_imageURL(),_imageloader);
        else

        if (Commons.beautyEntity.get_beauty_resId() != 0)
            beautyImage.setDefaultImageResId(Commons.beautyEntity.get_beauty_resId());
        else
            beautyImage.setImageBitmap(Commons.beautyEntity.get_beautyBitmap());


//        if(!Commons.beautyEntity.get_beautyBitmap().equals(null))beautyImage.setImageBitmap(Commons.beautyEntity.get_beautyBitmap());
//        else beautyImage.setImageResource(Commons.beautyEntity.get_beauty_resId());

        providerImage = (CircularNetworkImageView) findViewById(R.id.provider_profile);
        if (Commons.beautyEntity.get_provider_imageURL().length() > 0)
            providerImage.setImageUrl(Commons.beautyEntity.get_provider_imageURL(),_imageloader);
        else

        if (Commons.beautyEntity.get_provider_resId() != 0)
            providerImage.setDefaultImageResId(Commons.beautyEntity.get_provider_resId());
        else
            providerImage.setImageBitmap(Commons.beautyEntity.get_providerBitmap());


//        if(!Commons.beautyEntity.get_providerBitmap().equals(null))providerImage.setImageBitmap(Commons.beautyEntity.get_providerBitmap());
//        else providerImage.setImageResource(Commons.beautyEntity.get_provider_resId());

        providerName = (TextView) findViewById(R.id.provider_name);
        providerName.setText(Commons.beautyEntity.get_provider_name()); Log.d("PRONAME0===",String.valueOf(Commons.beautyEntity.get_provider_name()));

        admin = (LinearLayout) findViewById(R.id.admin);
        admin.setVisibility(View.INVISIBLE);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RestaurantManagerActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        beautyName=(TextView)findViewById(R.id.beauty_name);
        beautyName.setText("Beauty name: "+Commons.beautyEntity.get_beauty_name());

        requestpay=(TextView)findViewById(R.id.payment);
        requestpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PayPalPayment thingToPay = null;
                thingToPay = new PayPalPayment(new BigDecimal(Commons.beautyEntity.get_beauty_price()), "USD", Commons.beautyEntity.get_beauty_name(), PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(BeautyServiceRequestActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToPay);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        });

//        requestDate = (TextView) findViewById(R.id.request_date);
//        if (Commons._calendar_set) {
//            requestDate.setText("Request Date: " + Commons.beautyEntity.get_request_date());
//            //    Commons._calendar_set=false;
//        }
//        requestDate.setOnClickListener(this);
        requestLocation = (TextView) findViewById(R.id.request_location);
        if (Commons._location_set) {
            requestLocation.setText("Request location: \n" + Commons.beautyEntity.get_request_location_url());
            //    Commons._location_set=false;
        }
        requestLocation.setOnClickListener(this);

        beautyPrice = (TextView) findViewById(R.id.beauty_price);
        beautyPrice.setText(String.valueOf("Price: $" + Commons.beautyEntity.get_beauty_price()));
        beautyDescription = (EditText) findViewById(R.id.description);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

        accept = (TextView) findViewById(R.id.accept_request);
        accept.setOnClickListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        final Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    public void showToast(String content) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView = (TextView) dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast = new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                Commons._calendar_set = false;
                Commons._location_set = false;
                Intent intent = new Intent(this, BeautyDetailActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
                break;
            case R.id.accept_request:
                Commons._calendar_set = false;
                Commons._location_set = false;
                Commons._request_set = true;

                if (beautyDescription.getText().toString().equals("0.00000")) {
                    admin.setVisibility(View.VISIBLE);
                    return;
                }

//                if (DateEdit.getText().length() == 0 || requestLocation.getText().length() == 0 || beautyDescription.getText().length() == 0) {
//                    showToast("Please input all inputboxes.");
//                    return;
//                }

//                MessageEntity messageEntity = new MessageEntity();
//                messageEntity.set_useremail(Commons.beautyEntity.get_provider_email());
                Commons.beautyEntity.set_request_description(beautyDescription.getText().toString());
                Commons.beautyEntity.set_request_date(DateEdit.getText().toString());

                Commons.requestMessage=
                        "Req date:" + Commons.beautyEntity.get_request_date() + "\n" +
                        "Req loc info:" + Commons.beautyEntity.get_request_location_url() + "\n" +
                        "Req loc: " + String.valueOf(Commons.beautyEntity.get_requestLatLng())+"\n"+
                                "Desc:" + Commons.beautyEntity.get_request_description();
//                if(Commons.beautyEntity.get_request_location_url()!=""){
//                    messageEntity.set_usermessage(messageEntity.get_usermessage()+"\nLocation for VaCay: \n"+Commons.loc_url);
//                    messageEntity.setRequestLocation(Commons.loc_url);
//                    Commons.loc_url="";
//                }
//                messageEntity.set_requestLatLng(Commons.beautyEntity.get_requestLatLng());
//                messageEntity.setUserfullname(Commons.thisEntity.get_fullName());
//                messageEntity.set_bitmap(Commons.bitmap);
                if(Commons.beautyEntity.get_provider_resId()!=0){
                    try {
                        Commons.userEntity.set_imageRes(Commons.beautyEntity.get_provider_resId());  Log.d("IMAGE===",String.valueOf(Commons.beautyEntity.get_provider_resId()));
                    }catch (NullPointerException e){}
                }
                else if(Commons.beautyEntity.get_provider_imageURL().length()>0) {
                    try {
                        Commons.userEntity.set_photoUrl(Commons.beautyEntity.get_provider_imageURL()); Log.d("IMAGEurl===",String.valueOf(Commons.beautyEntity.get_provider_imageURL()));
                    }catch (NullPointerException e){}
                }
                try {
                    Commons.userEntity.set_name(Commons.beautyEntity.get_provider_name());  Log.d("PRONAME===",String.valueOf(Commons.beautyEntity.get_provider_name()));
                    Commons.userEntity.set_email(Commons.beautyEntity.get_provider_email());
                }catch (NullPointerException e){}
//                messageEntity.set_resId(getResources().getDrawable(imagemessage.getDrawable().get));
//                Commons.thisEntity.get_messagesentList().add(messageEntity);

         //       ================================================================================

                intent = new Intent(this, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.request_date:
//                intent = new Intent(this, CalendarActivity.class);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(0, 0);
                break;
            case R.id.request_location:
                intent = new Intent(this, RequestLocationActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                break;
        }
    }

    private  void confirmPaymentEmailPsd() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please login with PayPal by following email and password.");
//        builder.setTitle(Commons.userEntity.get_city());
        builder.setMessage("Email: cayleywetzig-facilitator@gmail.com\nPassword: ");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PayPalPayment thingToPay = null;
                thingToPay = new PayPalPayment(new BigDecimal(Commons.beautyEntity.get_beauty_price()), "USD", Commons.beautyEntity.get_beauty_name(), PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(BeautyServiceRequestActivity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToPay);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
                DateEdit.setText(DateEdit.getText() + " -" + hourOfDay + ":" + minute+" PM");
            }else {
                DateEdit.setText(DateEdit.getText() + " -" + hourOfDay + ":" + minute+" AM");
            }
            Commons.beautyEntity.set_request_date(DateEdit.getText().toString());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PAYMENT){
            if (resultCode == Activity.RESULT_OK){

                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null){

                    try{

                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        showToast("Payment Success! \nPaymentConfirmation info received from PayPal");

                    }catch (JSONException e){

                        Log.e(TAG, "an extremely unlikely failure occured: ", e);
                    }
                }else if (resultCode == Activity.RESULT_CANCELED){

                    Log.i(TAG, "The User canceled");
                }else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                    Log.i(TAG, "An invaild Payment or PayPalConfiguration was submitted. Please see the docs.");
                }
            }

//
//            if (confirm != null){
//                try{
//
//                    System.out.println( confirm.toJSONObject().toString(4));
//                    System.out.println( confirm.getPayment().toJSONObject().toString(4));
//                }catch (){}
//            }
        }

    }

}


