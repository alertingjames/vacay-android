package com.mv.vacay.main.provider;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.SignedEmployeeListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.SignedEmployeeEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SignedEmployeesViewActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    private ImageLoader _imageloader;
    private SignedEmployeeListAdapter signedEmployeeListAdapter=new SignedEmployeeListAdapter(this);
    private ArrayList<SignedEmployeeEntity> signedEmployeeEntities=new ArrayList<>();

    EditText ui_edtsearch;
    TextView title, viewCompanies;
    SwipyRefreshLayout ui_RefreshLayout;
    ImageView imvback;
    private ListView list;
    private ProgressDialog _progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_employees_view);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        _imageloader = VaCayApplication.getInstance().getImageLoader();

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        list=(ListView) findViewById(R.id.list);
        title=(TextView)findViewById(R.id.title);

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
                    signedEmployeeListAdapter.filter(text);
                }else  {
                    signedEmployeeListAdapter.setDatas(signedEmployeeEntities);
                    list.setAdapter(signedEmployeeListAdapter);
                }

            }
        });

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(0,0);
            }
        });

        getSignedEmployees();

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

    private void getSignedEmployees() {
        signedEmployeeEntities.clear();

        String url = ReqConst.SERVER_URL + "getEmployeeForAnnounce";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseAllAnnounceResponse(response);

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

                params.put("adminID", Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_CPROVIDERID, ""));
                params.put("an_id", Commons.announcement.getIdx());

                Log.d("AdminId/AnnounceId",Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_CPROVIDERID, "")+
                        "/"+Commons.announcement.getIdx());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseAllAnnounceResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("proResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                JSONArray employeeInfo = response.getJSONArray("em_info");

                for (int i = 0; i < employeeInfo.length(); i++) {

                    JSONObject jsonEmployee = (JSONObject) employeeInfo.get(i);

                    SignedEmployeeEntity signedEmployeeEntity = new SignedEmployeeEntity();

                    signedEmployeeEntity.setIdx(jsonEmployee.getString("em_id"));
                    signedEmployeeEntity.setPhotoUrl(jsonEmployee.getString("em_image"));
                    signedEmployeeEntity.setName(jsonEmployee.getString("em_name"));


                    signedEmployeeEntities.add(signedEmployeeEntity);
                }
                if(signedEmployeeEntities.isEmpty()) showToast("No signed-up employees!");
                else {
                    list.setVisibility(View.VISIBLE);
                    signedEmployeeListAdapter.setDatas(signedEmployeeEntities);
                    signedEmployeeListAdapter.notifyDataSetChanged();
                    list.setAdapter(signedEmployeeListAdapter);
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
}


























