package com.mv.vacay.main.provider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.mv.vacay.adapter.ProductSizeListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.classes.FontListParser;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.activity.BroadmoorProductPictureViewActivity;
import com.mv.vacay.main.payment.CollectionActivity;
import com.mv.vacay.main.payment.DBCards;
import com.mv.vacay.main.payment.PayPalPaymentActivity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.BroadmoorEntity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.ProductSizeEntity;
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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import static com.mv.vacay.main.payment.VerifyPaymentActivity.SECRET_KEY;

public class BroadmoorProductDetailActivity extends BaseActivity implements View.OnClickListener{

    ImageView back,productImage,broadmoorLogo;
    TextView  size, price, additional,buyButton,quantity;
    TextView productName;
    TextView sizeButton;
    EditText requestQuantity;
    EditText requestPrice;
    ListView list;
    LinearLayout sizePage, buyButtonBar,requestQuantityPortion, mediaButton;
    boolean _openDetail=false;
    ProductSizeListAdapter productSizeListAdapter=new ProductSizeListAdapter(this);
    ArrayList<ProductSizeEntity> productSizeEntities=new ArrayList<>();
    ProductSizeEntity _productSizeEntity = new ProductSizeEntity();

    public Stripe stripe;
    public SQLiteDatabase db;

    public String cardHolderName = "";
    public String creditCardNumber = "";
    public String expiryDate = "";
    public String cvv = "";
    public String accountId="";
    public String accountStatus="";
    public int expmonth = 0;
    public int expyear = 0;
    public Token stripeToken;

    AlertDialog alert=null;
    LinearLayout layout;
    boolean buck_updatev=false;

    private ProgressDialog _progressDlg;
    public static final String PUBLISHABLE_KEY = "pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadmoor_product_detail);

        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        productImage=(ImageView)findViewById(R.id.productImage);

//        Bitmap image = drawableToBitmap(LoadImageFromWebOperations(Commons.broadmoorEntity.getProductImageUrl()));
        Bitmap image=null;

        if(Commons.broadmoorEntity.getProductImageUrl().length()>1000){
            image=base64ToBitmap(Commons.broadmoorEntity.getProductImageUrl());
            if(image.getWidth()>800 && image.getHeight()>800)
                image=getResizedBitmap(image,800);
            productImage.setImageBitmap(image);
        }else {
            image = drawableToBitmap(LoadImageFromWebOperations(Commons.broadmoorEntity.getProductImageUrl()));
            if(image.getWidth()>800 && image.getHeight()>800)
                image=getResizedBitmap(image,800);
            productImage.setImageBitmap(image);
        }
        final Bitmap finalImage = image;
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplication(), BroadmoorProductPictureViewActivity.class);
                Commons.broadmoorImage= finalImage;
                startActivity(intent);
            }
        });

        broadmoorLogo=(ImageView)findViewById(R.id.broadmoorLogo);

        Bitmap image1 = drawableToBitmap(LoadImageFromWebOperations(Commons.broadmoorEntity.getBroadmoorLogoUrl()));
        if(image1.getWidth()>800 && image1.getHeight()>800)
            image1=getResizedBitmap(image1,800);
        broadmoorLogo.setImageBitmap(image1);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        productName=(TextView) findViewById(R.id.productName);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        productName.setTypeface(font);


        productName.setText(Commons.broadmoorEntity.getProductName());
        productName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogProductName(productName.getText().toString());
            }
        });

        requestPrice=(EditText) findViewById(R.id.requestPrice);
        requestQuantity=(EditText)findViewById(R.id.requestQuantity);

        sizeButton=(TextView)findViewById(R.id.sizeButton);
        if(Commons._is_admin)sizeButton.setText("Click here to view size & price from list");
        list=(ListView)findViewById(R.id.list);
        sizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!_openDetail) {
                    list.setVisibility(View.VISIBLE);
                    final LinearLayout.LayoutParams layoutparams = (LinearLayout.LayoutParams) list.getLayoutParams();
                    layoutparams.height=productSizeEntities.size()*200;
                    sizePage.setVisibility(View.GONE);
                    sizeButton.setActivated(true);
                    sizeButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop0,// right
                            0//bottom
                    );
                    if(productSizeEntities.isEmpty())showToast("No details");
                    productSizeListAdapter.setDatas(productSizeEntities);
                    list.setAdapter(productSizeListAdapter);
                    _openDetail = true;
                } else {
                    list.setVisibility(View.GONE);
                    sizePage.setVisibility(View.VISIBLE);
                    sizeButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop,// right
                            0//bottom
                    );
                    _openDetail = false;
                }
            }
        });

        mediaButton=(LinearLayout)findViewById(R.id.mediaButton);

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

                        enterMedia(Commons.broadmoorEntity);

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

        additional=(TextView)findViewById(R.id.additional);
        additional.setText(Commons.broadmoorEntity.getProductAdditional());

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        additional.setTypeface(font);


        sizePage=(LinearLayout)findViewById(R.id.productSize);

        requestQuantityPortion=(LinearLayout)findViewById(R.id.requestQuantityPortion);
        if(Commons._is_admin)requestQuantityPortion.setVisibility(View.GONE);

        buyButtonBar=(LinearLayout)findViewById(R.id.buyButtonBar);
        if(!Commons._is_admin)
            buyButtonBar.setVisibility(View.VISIBLE);
        else buyButtonBar.setVisibility(View.GONE);

        size=(TextView)findViewById(R.id.size);
        price=(TextView)findViewById(R.id.price);
        quantity=(TextView)findViewById(R.id.quantity);

        buyButton=(TextView)findViewById(R.id.buyButton);
        buyButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        buyButton.setBackgroundColor(Color.MAGENTA);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        buyButton.setBackgroundResource(R.drawable.gray_fillrect);     //    blue_fill_white_stroke
                        if(requestQuantity.getText().length()>0 && requestPrice.getText().length()>0)
//                            selectPaymentMethod();
                        {
                            String myEmployeeId=String.valueOf(Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_EMPLOYEEID, 0));       Log.d("EMID===>", myEmployeeId);
                            if(!myEmployeeId.equals("0"))
                                selectPaymentMethod2();
                            else {
                                viewAccountStatus3(Commons.broadmoorEntity.getAdminEmail());
                            }
                        }
                        else if(requestPrice.getText().length()==0)showToast("No price for this product.");
                        else if(requestQuantity.getText().length()==0)showToast("No quantity for this product.");
                        else showToast("Please type your request price and size.");

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        buyButton.getBackground().clearColorFilter();
                        buyButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        getProductSizeDetail();
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
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

    public void showAlertDialogPayment(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        selectCardRegistry();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

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
                    viewAccountStatus3(Commons.broadmoorEntity.getAdminEmail());
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
                if (item == 0) {
                    onScanPress(findViewById(android.R.id.content));
                } else if(item == 1){
                    registerOldCard();
                }else if(item == 2){
                    Intent a = new Intent(getApplicationContext(), CollectionActivity.class);
                    startActivity(a);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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
        _progressDlg= ProgressDialog.show(this, "", "Please wait...\nVerifying this payment...",true);
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
//                            payBroadmoor(stripeToken);
                            try {
                                payBroadmoor2(stripeToken);
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

    public void showAlertDialogPay(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Information");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendPayBroadmoorInfo2();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

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

    public void registerOldCard(){
        final int GET_NEW_CARD = 2;
        Intent intent = new Intent(this, CardEditActivity.class);
        startActivityForResult(intent, GET_NEW_CARD);
    }

    public void getProductSizeDetail(){

        productSizeEntities.clear();

        String url = ReqConst.SERVER_URL + "getBroadmoorDetailInfo";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseBroadmoorResponse(response);

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

                params.put("bm_proid", Commons.broadmoorEntity.getIdx());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseBroadmoorResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                JSONArray productInfo = response.getJSONArray("detail_info");

                for (int i = 0; i < productInfo.length(); i++) {

                    JSONObject jsonBroadmoor = (JSONObject) productInfo.get(i);

                    ProductSizeEntity productSizeEntity = new ProductSizeEntity();

                    productSizeEntity.setIdx(jsonBroadmoor.getString("bm_detailID"));
                    productSizeEntity.setProductSize(jsonBroadmoor.getString("bm_proSize"));
                    productSizeEntity.setProductPrice(jsonBroadmoor.getString("bm_proPrice"));
                    productSizeEntity.setProductQuantity(jsonBroadmoor.getString("bm_proQuantity"));
//
                    productSizeEntities.add(productSizeEntity);
                }
                if(productSizeEntities.isEmpty()) showToast("No Details");
                else {
                    size.setText(productSizeEntities.get(0).getProductSize());
                    price.setText(productSizeEntities.get(0).getProductPrice().replace("$",""));
                    productSizeListAdapter.setDatas(productSizeEntities);
                    productSizeListAdapter.notifyDataSetChanged();
                    list.setAdapter(productSizeListAdapter);
                }
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
    public void showProductSizeDetail(ProductSizeEntity productSizeEntity){
        _productSizeEntity=productSizeEntity;
        sizePage.setVisibility(View.VISIBLE);
        size.setText(productSizeEntity.getProductSize());
        price.setText(productSizeEntity.getProductPrice().replace("$",""));
        quantity.setText(productSizeEntity.getProductQuantity());
   //     requestPrice.setText(productSizeEntity.getProductPrice());

        list.setVisibility(View.GONE);
        sizeButton.setCompoundDrawablesWithIntrinsicBounds(
                0,// left
                0,//top
                R.drawable.ic_drop,// right
                0//bottom
        );
        _openDetail = false;
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

    public void showFontFamilyList(){

        final List<FontListParser.SystemFont> fonts = FontListParser.safelyGetSystemFonts();
        String[] items = new String[fonts.size()];
        for (int i = 0; i < fonts.size(); i++) {
            items[i] = fonts.get(i).name;
        }

        new AlertDialog.Builder(this).setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FontListParser.SystemFont selectedFont = fonts.get(which);
                // TODO: do something with the font
                Toast.makeText(getApplicationContext(), selectedFont.path, Toast.LENGTH_LONG).show();
            }
        }).show();

    }

    public void showAlertDialogProductName(String name) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Product Name");
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
                params.put("amount", price.getText().toString().replace("$",""));

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

                showAlertDialogFromBuck("You used $"+price.getText().toString().replace("$","")+" in VaCay bucks");
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
            else if(success.equals("99")){
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

    public void viewAccountStatus3(final String email) {

        String url = ReqConst.SERVER_URL + "account_details";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseViewAccountResponse3(response);

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

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseViewAccountResponse3(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("accountResponse=====> :",response.toString());

            String status = response.getString("status");

            if (status.equals("success")) {

                JSONObject userInfo = response.getJSONObject("account_data");  Log.d("accountData=====> :",userInfo.toString());

                accountId=userInfo.getString("accountid");
                accountStatus=userInfo.getString("status");
                if(accountStatus.startsWith("Pending"))
                    showAlertDialog("This retailer is on pending of payment-verification yet.");
                else if(accountStatus.equals("Approved")){
                    showAlertDialogPayment("This retailer is payment-verified so you can pay her.");
                }
            }
            else if(status.equals("error")) showAlertDialog("This retailer's payment account isn't created yet.");
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

    public void payBroadmoor2(final Token token) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        com.stripe.Stripe.apiKey = SECRET_KEY;

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", ((int)Math.round(Float.parseFloat(requestPrice.getText().toString().replace("$","").replace(",", "").trim())*100)));
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token.getId());
// ^ obtained with Stripe.js
        Charge charge=Charge.create(chargeParams);
        if(charge.getId().length()>0){
            Float providerOffer= Float.parseFloat(requestPrice.getText().toString().replace("$","").replace(",", "").trim())*100*0.8f;

            Map<String, Object> transferParams = new HashMap<String, Object>();
            transferParams.put("amount", ((int)Math.round(providerOffer)));
            transferParams.put("currency", "usd");
            transferParams.put("destination", accountId);

            Transfer transfer=Transfer.create(transferParams);
            if(transfer.getId().length()>0)
                showAlertDialogPay("Successfully Paid!");
            else showToast("Payment transfer failed");
        }else showToast("Charge failed");

    }

    public void sendPayBroadmoorInfo2() {

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
                params.put("receiverEmail", Commons.broadmoorEntity.getAdminEmail());
                params.put("paidMoney", price.getText().toString().replace("$",""));

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

    public void enterMedia(BroadmoorEntity entity){
        getMedia(String.valueOf(entity.getIdx()),"bproduct");
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

                    mediaEntity.setObjimage(Commons.broadmoorEntity.getBroadmoorLogoUrl());
                    mediaEntity.setObjtitle(Commons.broadmoorEntity.getProductName());

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





































