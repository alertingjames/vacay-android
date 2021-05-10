package com.mv.vacay.main;

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
import com.mv.vacay.adapter.CompanyListAdapter1;
import com.mv.vacay.adapter.JobListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.CompanyEntity;
import com.mv.vacay.models.JobsEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class JobListActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    private ImageLoader _imageloader;
    private JobListAdapter jobListAdapter=new JobListAdapter(this);
    private ArrayList<JobsEntity> jobsEntities=new ArrayList<>();

    private CompanyListAdapter1 companyListAdapter=new CompanyListAdapter1(this);
    private ArrayList<CompanyEntity> companyEntities=new ArrayList<>();

    EditText ui_edtsearch;
    TextView title, viewCompanies;
    SwipyRefreshLayout ui_RefreshLayout;
    ImageView imvback;
    private ListView list;
    private ProgressDialog _progressDlg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        _imageloader = VaCayApplication.getInstance().getImageLoader();

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        list=(ListView) findViewById(R.id.list);
        title=(TextView)findViewById(R.id.title);
        viewCompanies=(TextView)findViewById(R.id.viewCompanies);
        final TextView finalTitle = title;
        viewCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalTitle.setText("Companies!");
                Commons._is_select_job=false;
                getCompanies();
            }
        });

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
//                    if(Commons._is_select_job) {
//                        jobListAdapter.filter(text);
//                    }
//                    else {
//                        companyListAdapter.filter(text);
//                    }

                    jobListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
//                    if(Commons._is_select_job) {
//                        jobListAdapter.setDatas(jobsEntities);
//                        list.setAdapter(jobListAdapter);
//                    }
//                    else {
//                        companyListAdapter.setDatas(companyEntities);
//                        list.setAdapter(companyListAdapter);
//                    }

                    jobListAdapter.setDatas(jobsEntities);
                    list.setAdapter(jobListAdapter);
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

        title.setText("Company Jobs!");
//        getCompanies();
        getJobs(String.valueOf(Commons.thisEntity.get_adminId()));

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

    private void getJobs(final String adminId) {
        jobsEntities.clear();

        String url = ReqConst.SERVER_URL + "getAllJobByAdminID";

       showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseAllJobsResponse(response);

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

                params.put("adminID", adminId);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseAllJobsResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("proResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                JSONArray jobInfo = response.getJSONArray("job_info");

                for (int i = 0; i < jobInfo.length(); i++) {

                    JSONObject jsonJob = (JSONObject) jobInfo.get(i);

                    JobsEntity job = new JobsEntity();

                    job.setIdx(jsonJob.getString("job_id"));
                    job.setLogoUrl(jsonJob.getString("adminLogoImageUrl"));
                    job.setJobName(jsonJob.getString("job_name"));
                    job.setJobReqId(jsonJob.getString("job_req"));
                    job.setDepartment(jsonJob.getString("job_department"));
                    job.setLocation(jsonJob.getString("job_location"));
                    job.setDescription(jsonJob.getString("job_description"));
                    job.setPostingDate(jsonJob.getString("job_postdate"));
                    job.setEmptyField(jsonJob.getString("job_empty"));
                    job.setCompany(jsonJob.getString("adminCompany"));
                    job.setSurvey(jsonJob.getString("job_survey"));

                    if(job.getCompany().equals(Commons.thisEntity.getCompany()))
                        jobsEntities.add(job);
                }
                Log.d("ADMIN-Company===>", Commons.thisEntity.getCompany());
                if(jobsEntities.isEmpty()) showToast("No jobs");
                else {
                    list.setVisibility(View.VISIBLE);
                    jobListAdapter.setDatas(jobsEntities);
                    jobListAdapter.notifyDataSetChanged();
                    list.setAdapter(jobListAdapter);
                }
            }
            else {

                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast("Server connection failed...");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast("Server connection failed...");
        }
    }

    public void getCompanies() {

        companyEntities.clear();

        String url = ReqConst.SERVER_URL + "getAllCompanyNames";

        showProgress();
        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseAllCompaniesResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showToast(getString(R.string.error));

            }
        }) {

        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseAllCompaniesResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("CompanyResponse===>",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray companyInfo = response.getJSONArray("company_info");

                for (int i = 0; i < companyInfo.length(); i++) {

                    JSONObject jsonCompany = (JSONObject) companyInfo.get(i);

                    CompanyEntity companyEntity=new CompanyEntity();

                    String adminId=jsonCompany.getString("adminID");
                    String company=jsonCompany.getString("adminCompany");
                    String logo=jsonCompany.getString("adminLogoImageUrl");

                    companyEntity.setAdminId(adminId);
                    companyEntity.setCompany(company);
                    companyEntity.setLogoUrl(logo);

                    companyEntities.add(companyEntity);
                }

                list.setVisibility(View.VISIBLE);
                companyListAdapter.setDatas(companyEntities);
                companyListAdapter.notifyDataSetChanged();
                list.setAdapter(companyListAdapter);

            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }
    public void showJobList1(){
        title.setText("Jobs!");
        getJobs(Commons.companyEntity.getAdminId());
    }
}

























