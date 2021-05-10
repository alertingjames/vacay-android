package com.mv.vacay.main.beautymen;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.ProductListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.BeautyProductEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProductListActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<BeautyProductEntity> _datas=new ArrayList<>(10000);
    ProductListAdapter productListAdapter=new ProductListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        Commons.productEntities.clear();

        getProductInfo();

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.productList);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    productListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    productListAdapter.setUserDatas(Commons.productEntities);
                    listView.setAdapter(productListAdapter);
                }

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //       getAllUsers();
    }

    public void getProductInfo() {

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
                }

                if(Commons.productEntities.size()==0) {
                    showToast("No Products");
                    TextView noItems=(TextView)findViewById(R.id.noitems);
                    noItems.setVisibility(View.VISIBLE);
                }else {
                    productListAdapter.setUserDatas(Commons.productEntities);
                    productListAdapter.notifyDataSetChanged();
                    listView.setAdapter(productListAdapter);
                }

                Log.d("PRODUCTS===>",String.valueOf(Commons.productEntities.size()));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }
}



