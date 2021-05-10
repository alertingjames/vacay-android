package com.mv.vacay.main.provider;

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
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.beautymen.ProductLocationViewActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.MediaEntity;
import com.stripe.android.Stripe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProviderProductDetailActivity extends BaseActivity implements View.OnClickListener{

    NetworkImageView productImageNet;
    ImageView productImage;
    ImageView back;
    TextView brand, product, productName, size, price, description, company, location, inventory, pay, gotoList;
    TextView processingFee, totalPrice, providerTakeHome;

    public Stripe stripe;
    public SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_product_detail);

        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        productImageNet=(NetworkImageView)findViewById(R.id.productImageNet);
        productImage=(ImageView)findViewById(R.id.productImage);

        if (Commons.beautyProductEntity.getProductImageUrl().length() < 1000) {
            productImageNet.setVisibility(View.VISIBLE);
            productImageNet.setImageUrl(Commons.beautyProductEntity.getProductImageUrl(),_imageLoader);
        }
        else {
            productImageNet.setVisibility(View.GONE);
            productImage.setImageBitmap(base64ToBitmap(Commons.beautyProductEntity.getProductImageUrl()));
        }


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

        processingFee=(TextView)findViewById(R.id.processingFee);
        totalPrice=(TextView)findViewById(R.id.totalPrice);
        providerTakeHome=(TextView)findViewById(R.id.takeHomePay);

        providerTakeHome.setText("$" + Commons.beautyProductEntity.getProviderTakeHome());
        processingFee.setText("20%");
        if(Commons.beautyProductEntity.getPrice().contains("$"))
            totalPrice.setText(Commons.beautyProductEntity.getPrice());
        else totalPrice.setText("$" + Commons.beautyProductEntity.getPrice());
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

                    mediaEntity.setObjimage(Commons.thisEntity.get_photoUrl());
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




































