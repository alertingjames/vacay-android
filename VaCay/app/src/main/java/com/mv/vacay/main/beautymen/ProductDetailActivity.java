package com.mv.vacay.main.beautymen;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
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
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.google.gson.Gson;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.payment.CollectionActivity;
import com.mv.vacay.main.payment.DBCards;
import com.mv.vacay.main.payment.PayPalPaymentActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.BroadmoorEntity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularNetworkImageView;
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
import com.stripe.model.Charge;
import com.stripe.model.Transfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.mv.vacay.main.payment.VerifyPaymentActivity.SECRET_KEY;

public class ProductDetailActivity extends BaseActivity implements View.OnClickListener{

    ImageView productImage;
    NetworkImageView productImageNet;
    ImageView back;
    TextView brand, product, productName, size, price, description, company, location, inventory, pay, gotoList;

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

    String proId="";
    private ProgressDialog _progressDlg;
    public static final String PUBLISHABLE_KEY = "pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV";           // secret_key:    sk_live_bxyrJ9CkAhUhVDUw4Zvw7hGW     publishable_key:     pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        productImage=(ImageView)findViewById(R.id.productImage);
        productImageNet=(NetworkImageView) findViewById(R.id.productImageNet);

        if (Commons.beautyProductEntity.getProductImageUrl().length() > 1000) {
            productImage.setVisibility(View.VISIBLE);
            productImage.setImageBitmap(base64ToBitmap(Commons.beautyProductEntity.getProductImageUrl()));
        }else {
            productImage.setVisibility(View.GONE);
            productImageNet.setImageUrl(Commons.beautyProductEntity.getProductImageUrl(),_imageLoader);
        }

        cardHolderName=Commons.thisEntity.get_fullName();

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        brand=(TextView)findViewById(R.id.brand);
        brand.setText(Commons.beautyProductEntity.getBrand());

        product=(TextView)findViewById(R.id.product);
        product.setText(Commons.beautyProductEntity.getProduct());

        productName=(TextView)findViewById(R.id.productName);
        productName.setText(Commons.beautyProductEntity.getProductName());

        size=(TextView)findViewById(R.id.size);
        size.setText(Commons.beautyProductEntity.getSize());

        price=(TextView)findViewById(R.id.price);
        price.setText("$"+Commons.beautyProductEntity.getPrice().replace("$",""));

        description=(TextView)findViewById(R.id.description);
        description.setText(Commons.beautyProductEntity.getDescription());

        company=(TextView)findViewById(R.id.company);
        company.setText(Commons.beautyProductEntity.getCompanyName());

        inventory=(TextView)findViewById(R.id.inventory);
        inventory.setText("Inv: "+Commons.beautyProductEntity.getInventoryNumber());

        pay=(TextView)findViewById(R.id.buyButton);
        pay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        pay.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        pay.setBackgroundColor(Color.parseColor("#02a8ff"));//  02a8ff

//                        String myEmployeeId=String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
//                        if(!myEmployeeId.equals("0"))
//                            selectPaymentMethod2();
//                        else {
//                            selectCardRegistry();
//                        }
                        getAdminPaymentAccountId();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        pay.getBackground().clearColorFilter();
                        pay.invalidate();
                        break;
                    }
                }

                return true;
            }
        });


        gotoList=(TextView)findViewById(R.id.gotoList);
        gotoList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        gotoList.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        gotoList.setBackgroundColor(Color.parseColor("#1ba195"));//   1ba195
                        Intent intent=new Intent(getApplicationContext(),ProductListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        gotoList.getBackground().clearColorFilter();
                        gotoList.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
        location=(TextView)findViewById(R.id.location);
        location.setText(Commons.beautyProductEntity.getLocation());
        location.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        location.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        location.setBackgroundColor(Color.parseColor("#1ba195"));//  Color.parseColor("#1ba195")

                        Intent intent=new Intent(getApplicationContext(),ProductLocationViewActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in,R.anim.left_out);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        location.getBackground().clearColorFilter();
                        location.invalidate();
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

                        enterMedia(Commons.beautyProductEntity);

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

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
        }
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
//                            payProduct(stripeToken);
                            try {
                                payProduct2(stripeToken);
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

    public void selectPaymentMethod() {

        final String[] items = {"I use PayPal","I use Stripe"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                        Commons.payEmail=Commons.newBeautyEntity.getEmail();
                        Commons.payPrice=Commons.beautyProductEntity.getPrice();
                        Commons.payName=Commons.beautyProductEntity.getProduct();
                        Intent intent = new Intent(getApplicationContext(), PayPalPaymentActivity.class);
                        startActivity(intent);
                    }
                } else if (item == 1) {
//                    registerUsedBuck();
                    selectCardRegistry();
                }
            }
        });
        android.app.AlertDialog alert = builder.create();
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
                    if(Commons.newBeautyEntity.getToken().length()==0){
                        showToast("Sorry, this provider's Stripe account isn't verified.");
                    }else onScanPress(findViewById(android.R.id.content));
                } else if(item == 1){
                    if(Commons.newBeautyEntity.getToken().length()==0){
                        showToast("Sorry, this provider's Stripe account isn't verified.");
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

    public void showAlertDialogPay(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sendPayInfoProduct();
                        sendPayInfoProduct2();
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
                params.put("amount", Commons.beautyProductEntity.getPrice().replace("$",""));

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

                showAlertDialogFromBuck("You used $"+Commons.beautyProductEntity.getPrice().replace("$","")+" in VaCay bucks");
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
            else if (success.equals("99")){
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

    public void payProduct2(final Token token) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        com.stripe.Stripe.apiKey = SECRET_KEY;

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", ((int)Math.round(Float.parseFloat(Commons.beautyProductEntity.getPrice().replace("$","").replace(",", "").trim())*100)));
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token.getId());
// ^ obtained with Stripe.js
        Charge charge=Charge.create(chargeParams);
        if(charge.getId().length()>0){
            double providerOffer= Float.parseFloat(Commons.beautyProductEntity.getPrice().replace("$","").replace(",", "").trim())*100*0.8f*Float.parseFloat(Commons.newBeautyEntity.getProductSalePercent().replace("%","").replace(",", ""))*0.01;

            Map<String, Object> transferParams = new HashMap<String, Object>();
            transferParams.put("amount", ((int)Math.round(providerOffer)));
            transferParams.put("currency", "usd");
            transferParams.put("destination", Commons.newBeautyEntity.getToken());

            Transfer transfer=Transfer.create(transferParams);
            if(transfer.getId().length()>0) {
                double providerOffer2= Float.parseFloat(Commons.beautyProductEntity.getPrice().replace("$","").replace(",", "").trim())*100*0.8f*(100-Float.parseFloat(Commons.newBeautyEntity.getProductSalePercent().replace("%","").replace(",", "")))*0.01;

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

    public void sendPayInfoProduct2() {

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

                params.put("senderEmail", Commons.thisEntity.get_email());
                params.put("receiverEmail", Commons.newBeautyEntity.getEmail());   // cent
                params.put("paidMoney", Commons.beautyProductEntity.getPrice().replace("$",""));

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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    public void enterMedia(BeautyProductEntity entity){
        getMedia(String.valueOf(entity.get_idx()),"product");
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

                    mediaEntity.setObjimage(Commons.newBeautyEntity.getProviderPhotoUrl());
                    mediaEntity.setObjtitle(Commons.beautyProductEntity.getProductName());

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




































