package com.mv.vacay.main.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.paypal.android.MEP.CheckoutButton;
import com.paypal.android.MEP.PayPal;
import com.paypal.android.MEP.PayPalActivity;
import com.paypal.android.MEP.PayPalAdvancedPayment;
import com.paypal.android.MEP.PayPalPayment;
import com.paypal.android.MEP.PayPalReceiverDetails;
import com.paypal.android.MEP.PayPalResultDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PayPalPaymentActivity extends BaseActivity implements PayPalResultDelegate{

    CheckoutButton launchPayPalButton;
    public static int _payment_id=0;
    public static final String LIVE_MY_EMAIL_ADDRESS = "cayleywetzig@gmail.com";   // Buyer login email: Cayvacay09@gmail.com and pw : Tennis1234!
    AlertDialog alert;
    RelativeLayout relativeLayout;
    PayPal payPal;
    PayPal ppObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal_payment);

        TextView email=(TextView)findViewById(R.id.email);
        email.setText(Commons.payEmail);
        TextView payName=(TextView)findViewById(R.id.payName);
        payName.setText(Commons.payName);
        TextView price=(TextView)findViewById(R.id.price);
        if(!Commons.payPrice.contains("$")){
            price.setText("$ "+Commons.payPrice);
        }
        else price.setText(Commons.payPrice);

        payPal = PayPal.getInstance();
        ppObj = payPal.initWithAppID(this.getBaseContext(),
                "APP-62370397YB552731M", PayPal.ENV_LIVE);

        ppObj.setLanguage("en_US");
    //    ppObj.setShippingEnabled(true);
        ppObj.setFeesPayer (PayPal.FEEPAYER_PRIMARYRECEIVER);
    //    ppObj.setDynamicAmountCalculationEnabled(true);

        launchPayPalButton =
                ppObj.getCheckoutButton(this, PayPal.BUTTON_194x37,
                        CheckoutButton.TEXT_PAY);
        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.bottomMargin = 2;
        launchPayPalButton.setLayoutParams(params);
        launchPayPalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chainedPayment();
             //   finish();
            }
        });
        ((RelativeLayout)findViewById(R.id.activity_pay_pal_payment)).addView(launchPayPalButton);
    }
    public void chainedPayment(){

        Log.d("Value===>", Commons.payPrice);
        double service_price=0.00f;
        if(Commons.payPrice.contains("$")){
            Commons.payPrice=Commons.payPrice.replace("$","");
        }
 //       service_price=(double)Double.parseDouble(Commons.payPrice);

        service_price=1.00f;

        PayPalReceiverDetails receiver0, receiver1;
        receiver0 = new PayPalReceiverDetails();

//        receiver0.setRecipient(Commons.payEmail);

        receiver0.setRecipient("caseykovee123@gmail.com");

//        receiver0.setRecipient(LIVE_MY_EMAIL_ADDRESS);

        receiver0.setMerchantName("Pay the provider"+" for "+Commons.payName);
        receiver0.setSubtotal(BigDecimal.valueOf(service_price));
        receiver0.setIsPrimary(true);
        receiver0.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);   // chained ===> PAYMENT_TYPE_SERVICE ,   single or parallel ===> PAYMENT_TYPE_GOOD   or  PAYMENT_TYPE_PERSONAL

        receiver1 = new PayPalReceiverDetails();
        receiver1.setRecipient(LIVE_MY_EMAIL_ADDRESS);    //  the master of this app
//        receiver1.setRecipient("caseykovee123@gmail.com");
        receiver1.setMerchantName("(+)");
        receiver1.setSubtotal(BigDecimal.valueOf(service_price*0.6));
        receiver1.setIsPrimary(false);
        receiver1.setPaymentType(PayPal.PAYMENT_TYPE_SERVICE);

        PayPalAdvancedPayment advPayment = new PayPalAdvancedPayment();
        advPayment.setCurrencyType("USD");
        advPayment.getReceivers().add(receiver0);
        advPayment.getReceivers().add(receiver1);
        Intent paypalIntent = ppObj.checkout(advPayment, this);
        this.startActivityForResult(paypalIntent, 1);

    }
    public void singlePayment(){
        PayPalPayment newPayment = new
                PayPalPayment();
        newPayment.setSubtotal(BigDecimal.valueOf(100));
        newPayment.setCurrencyType("PHP");
        newPayment.setRecipient("mksob@gmail.com");
        newPayment.setMerchantName("My Company");
        newPayment.setPaymentType(PayPal.PAYMENT_TYPE_GOODS);
        Intent paypalIntent = PayPal.getInstance().checkout(newPayment, getApplicationContext());
        startActivityForResult(paypalIntent, 1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(resultCode) {
            case Activity.RESULT_OK:
//The payment succeeded
                String payKey = data.getStringExtra(PayPalActivity.EXTRA_PAY_KEY);
                Log.d("PAYKEY===>",payKey);
                showToast("Successfully Paid!"+"\n"+"paykey==>"+payKey);
                registerUsedBuck();
//Tell the user their payment succeeded
                break;
            case Activity.RESULT_CANCELED:
//The payment was canceled
//Tell the user their payment was canceled
                showToast("Payment Canceled!");
                break;
            case PayPalActivity.RESULT_FAILURE:
//The payment failed -- we get the error from the EXTRA_ERROR_ID and EXTRA_ERROR_MESSAGE
                String errorID =
                        data.getStringExtra(PayPalActivity.EXTRA_ERROR_ID);
                String errorMessage =
                        data.getStringExtra(PayPalActivity.EXTRA_ERROR_MESSAGE);
                showToast("Payment Failed!"+"\n"+errorID+"\n"+errorMessage);
                Log.d("ERRORID===>",errorID+"\n"+errorMessage);
//Tell the user their payment was failed.
        }
    }

    @Override
    public void onPaymentSucceeded(String s, String s1) {
        Toast.makeText(getApplicationContext(),"PayKey: "+s+"\n"+"Payment Status: "+s1,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentFailed(String s, String s1, String s2, String s3, String s4) {
        showToast("Payment Failed!");
    }

    @Override
    public void onPaymentCanceled(String s) {
        showToast("Payment Canceled!");
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
                params.put("amount", Commons.payPrice);

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

                showAlertDialogFromBuck("You used $"+Commons.payPrice+" in VaCay bucks");
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
                            "Rest of buck: $"+(Float.valueOf(em_givenbuck.replace("$",""))-Float.valueOf(em_usedbuck.replace("$","")));
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

    private  void showBuckInfo(String image, String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        builder.setTitle("Your Bucks Status:");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        if(image.length()>0)
            photo.setImageUrl(image,_imageLoader);
        else photo.setVisibility(View.GONE);

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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


}