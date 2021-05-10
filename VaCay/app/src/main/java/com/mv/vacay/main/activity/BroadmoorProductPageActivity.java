package com.mv.vacay.main.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.BroadmoorProductListAdapter;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.BroadmoorEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BroadmoorProductPageActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    private ImageLoader _imageloader;
    private BroadmoorProductListAdapter broadmoorProductListAdapter=new BroadmoorProductListAdapter(this);
    private ArrayList<BroadmoorEntity> broadmoorEntities=new ArrayList<>();

    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ImageView imvback;
    private GridView productList;
    private ProgressDialog _progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadmoor_product_page);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        _imageloader = VaCayApplication.getInstance().getImageLoader();

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        productList=(GridView) findViewById(R.id.productList);

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
                    broadmoorProductListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    broadmoorProductListAdapter.setDatas(broadmoorEntities);
                    productList.setAdapter(broadmoorProductListAdapter);
                }

            }
        });

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        String category=getIntent().getStringExtra("category");
        switch (category){
            case "golf":
                getProducts("Golf");
                break;
            case "tennis":
                getProducts("Tennis");
                break;
            case "running":
                getProducts("Running");
                break;
            case "skiing":
                getProducts("Skiing & Snowboarding");
                break;
            case "exploring":
                getProducts("Exploring");
                break;
            case "biking":
                getProducts("Biking");
                break;
            case "surfing":
                getProducts("Surfing/Kitesurfing");
                break;
            case "fishing":
                getProducts("Fishing");
                break;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

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

    public void getProducts(final String categoty) {

        broadmoorEntities.clear();

        String url = ReqConst.SERVER_URL + "getBroadmoorInfo";

        showProgress();


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

                params.put("bm_proCategory", categoty);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseBroadmoorResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                JSONArray productInfo = response.getJSONArray("broadmoor_info");

                for (int i = 0; i < productInfo.length(); i++) {

                    JSONObject jsonBroadmoor = (JSONObject) productInfo.get(i);

                    BroadmoorEntity broadmoorEntity = new BroadmoorEntity();

                    broadmoorEntity.setIdx(jsonBroadmoor.getString("bm_proid"));
                    broadmoorEntity.setAdminId(jsonBroadmoor.getString("adminID"));
                    broadmoorEntity.setAdminEmail(jsonBroadmoor.getString("adminEmail"));
                    broadmoorEntity.setProductName(jsonBroadmoor.getString("bm_proName"));
//                    broadmoorEntity.setProductSize(jsonBroadmoor.getString("bm_proSize"));
//                    broadmoorEntity.setProductPrice(jsonBroadmoor.getString("bm_proPrice"));
                    broadmoorEntity.setProductImageUrl(jsonBroadmoor.getString("bm_proImageUrl"));
                    broadmoorEntity.setProductInventory(jsonBroadmoor.getString("bm_proInventoryNum"));
                    broadmoorEntity.setProductCategory(jsonBroadmoor.getString("bm_proCategory"));
                    broadmoorEntity.setProductAdditional(jsonBroadmoor.getString("bm_proAdditional"));
                    broadmoorEntity.setBroadmoorLogoUrl(jsonBroadmoor.getString("adminLogoImageUrl"));
//                    broadmoorEntity.setProductQuantity(jsonBroadmoor.getString("bm_proQuantity"));

                    broadmoorEntities.add(0,broadmoorEntity);
                }
                if(broadmoorEntities.isEmpty()) showToast("No Products");
                else {
                    broadmoorProductListAdapter.setDatas(broadmoorEntities);
                    broadmoorProductListAdapter.notifyDataSetChanged();
                    productList.setAdapter(broadmoorProductListAdapter);
                }
            }
            else {

//                String error = response.getString(ReqConst.RES_ERROR);
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
}
























