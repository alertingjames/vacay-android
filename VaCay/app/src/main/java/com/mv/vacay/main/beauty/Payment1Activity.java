package com.mv.vacay.main.beauty;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mv.vacay.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

//import butterknife.Bind;
//import butterknife.ButterKnife;

public class Payment1Activity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "UpgradeActivity";

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

    EditText amount, beautyname;
    Button payment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        loadLayout();

        final Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);
    }

    private void loadLayout(){
        amount=(EditText)findViewById(R.id.amount);
        beautyname=(EditText)findViewById(R.id.beautyname);
        payment=(Button)findViewById(R.id.payment);
        payment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.payment:

                PayPalPayment thingToPay = null;

                thingToPay = new PayPalPayment(new BigDecimal(30), "USD", "Beauty", PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(Payment1Activity.this, PaymentActivity.class);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToPay);
                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                break;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE_PAYMENT){
            if (resultCode == Activity.RESULT_OK){

                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null){

                    try{

                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));

                        showToast("Payment Success! PaymentConfirmation info received from PayPal");

//                        Preference.getInstance().put(Payment1Activity.this, PrefConst.PREFKEY_PAID, "paid");
//
//                        gotoSignup();

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

