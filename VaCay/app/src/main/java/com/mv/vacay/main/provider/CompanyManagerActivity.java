package com.mv.vacay.main.provider;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.AnnouncementListAdapter;
import com.mv.vacay.adapter.CompanyListAdapter;
import com.mv.vacay.adapter.DrawerAdapter;
import com.mv.vacay.adapter.EmployeesListAdapter;
import com.mv.vacay.adapter.JobListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.ChatHistoryActivity;
import com.mv.vacay.main.JobListActivity;
import com.mv.vacay.main.LoginRActivity;
import com.mv.vacay.main.RegisterActivity;
import com.mv.vacay.main.inbox.InboxActivity;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.main.payment.CollectionActivity;
import com.mv.vacay.main.weather.MmainActivity;
import com.mv.vacay.models.AnnouncementEntity;
import com.mv.vacay.models.CompanyEntity;
import com.mv.vacay.models.Items;
import com.mv.vacay.models.JobsEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.nearby.Splash;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.widgets.KenBurnsView;
import com.mv.vacay.widgets.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

public class CompanyManagerActivity extends AppCompatActivity implements View.OnClickListener,KenBurnsView.TransitionListener {

    private String[] mDrawerTitles;
    private String[] mFooterTitles;
    private TypedArray mDrawerIcons;
    private ArrayList<Items> drawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList,employeeList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CircularNetworkImageView userPhoto;
    private CircularImageView uPhoto;
    private TextView userName;
    private TextView userEmail,searchUnverified;
    private static final int TRANSITIONS_TO_SWITCH = 3;
    private ViewSwitcher mViewSwitcher;
    private int mTransitionsCount = 0;
    private WebView myPage;
    private ImageView webcancel,providersCancel;
    private String myEmail="", myName="", myId="", myFirst="", myLast="";
    private ProgressDialog _progressDlg;
    private ImageLoader _imageloader;
    private EmployeesListAdapter employeesListAdapter=new EmployeesListAdapter(this);
    private ArrayList<UserEntity> userEntities=new ArrayList<>();

    private ArrayList<JobsEntity> jobsEntities=new ArrayList<>();
    private JobListAdapter jobListAdapter=new JobListAdapter(this);

    private ArrayList<AnnouncementEntity> announcementEntities=new ArrayList<>();
    private AnnouncementListAdapter announcementListAdapter=new AnnouncementListAdapter(this);

    private ArrayList<CompanyEntity> companyEntities=new ArrayList<>();
    private CompanyListAdapter companyListAdapter=new CompanyListAdapter(this);

    EditText ui_edtsearch;
    LinearLayout search;

    ArrayList<UserEntity> _datas_user=new ArrayList<>();

    String email="", sender="", name="", photo="", message="";
    Bitmap bitmapPhoto=null;

    UserEntity userEntity=new UserEntity();

    private static FragmentManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_manager);

        _imageloader = VaCayApplication.getInstance().getImageLoader();
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

//        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
//        img2.setTransitionListener(this);

        KenBurnsView img3 = (KenBurnsView) findViewById(R.id.img3);
        img3.setTransitionListener(this);

        Commons._is_admin=true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        mManager = getSupportFragmentManager();

        mDrawerTitles = getResources().getStringArray(R.array.drawer_company_titles);
        mFooterTitles = getResources().getStringArray(R.array.footer_titles);
        mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_company_icons);
        drawerItems = new ArrayList<Items>();
        employeeList=(ListView)findViewById(R.id.providerList);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        for (int i = 0; i < mDrawerTitles.length; i++) {
            drawerItems.add(new Items(mDrawerTitles[i], mDrawerIcons.getResourceId(i, -(i + 1))));
            Log.d("ListItemTitle===>",mDrawerTitles[i]+i);
        }

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup header = (ViewGroup) inflater.inflate(R.layout.header,
                mDrawerList, false);

        final ViewGroup footer = (ViewGroup) inflater.inflate(R.layout.footer,
                mDrawerList, false);

        // Give your Toolbar a subtitle!
        /* mToolbar.setSubtitle("Subtitle"); */

        mDrawerList.addHeaderView(header, null, true); // true = clickable
        mDrawerList.addFooterView(footer, null, true); // true = clickable

        //Set width of drawer
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
        lp.width = calculateDrawerWidth();
        mDrawerList.setLayoutParams(lp);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new DrawerAdapter(getApplicationContext(), drawerItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        myPage=(WebView)findViewById(R.id.myPage);
        myPage.getSettings().setJavaScriptEnabled(true);

        //loads the WebView completely zoomed out
        myPage.getSettings().setLoadWithOverviewMode(true);

        //true makes the Webview have a normal viewport such as a normal desktop browser
        //when false the webview will have a viewport constrained to it's own dimensions
        myPage.getSettings().setUseWideViewPort(true);

        //override the web client to open all links in the same webview
        myPage.setWebViewClient(new MyWebViewClient());
        myPage.setWebChromeClient(new MyWebChromeClient());

        //Injects the supplied Java object into this WebView. The object is injected into the
        //JavaScript context of the main frame, using the supplied name. This allows the
        //Java object's public methods to be accessed from JavaScript.
        myPage.addJavascriptInterface(new JavaScriptInterface(this), "Android");

        //load the home page URL
        myPage.loadUrl(ReqConst.SERVER_ADDR);   //    http://35.162.12.207                http://52.15.146.192:8080/media/
        //    myPage.loadUrl("http://www.hadeninteractive.com/digital-marketing-beauty-products/");

        webcancel=(ImageView)findViewById(R.id.webcancel);
        webcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPage.setVisibility(View.GONE);
                webcancel.setVisibility(View.GONE);
            }
        });
        providersCancel=(ImageView)findViewById(R.id.providersCancel);
        providersCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                employeeList.setVisibility(View.GONE);
                providersCancel.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
            }
        });

        search=(LinearLayout)findViewById(R.id.search);
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
                    if(userEntities.size()>0) employeesListAdapter.filter(text);
                    else if(companyEntities.size()>0)companyListAdapter.filter(text);
                    else if(jobsEntities.size()>0)jobListAdapter.filter(text);
                    else if(announcementEntities.size()>0)announcementListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    if(userEntities.size()>0){
                        employeesListAdapter.setUserDatas(userEntities);
                        employeeList.setAdapter(employeesListAdapter);
                    }else if(companyEntities.size()>0){
                        companyListAdapter.setDatas(companyEntities);
                        employeeList.setAdapter(companyListAdapter);
                    }else if(jobsEntities.size()>0){
                        jobListAdapter.setDatas(jobsEntities);
                        employeeList.setAdapter(jobListAdapter);
                    }else if(announcementEntities.size()>0){
                        announcementListAdapter.setDatas(announcementEntities);
                        employeeList.setAdapter(announcementListAdapter);
                    }

                }

            }
        });

        getAdminInfo();

        try{
            pushNotification(Commons.thisEntity.get_email());
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public int calculateDrawerWidth() {
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        Display display = getWindowManager().getDefaultDisplay();
        int width;
        int height;
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth();  // deprecated
            height = display.getHeight();  // deprecated
        }
        return width - actionBarHeight;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onTransitionStart(Transition transition) {

    }

    @Override
    public void onTransitionEnd(Transition transition) {
        mTransitionsCount++;
        if (mTransitionsCount == TRANSITIONS_TO_SWITCH) {
            mViewSwitcher.showNext();
            mTransitionsCount = 0;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Log.d("Pos===>",String.valueOf(position));
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                //HomePage

                break;
            case 1:
                Intent intent = new Intent(getApplicationContext(), Splash.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);

                break;
            case 2:
                //employees
                companyEntities.clear();
                jobsEntities.clear();
                announcementEntities.clear();
                getEmployees();
                break;
            case 3:
                //jobs
                jobsEntities.clear();
                announcementEntities.clear();
                userEntities.clear();
//                getCompanies();
                getJobs(String.valueOf(Commons.thisEntity.get_adminId()));

                break;
            case 4:
                //announcemets
                userEntities.clear();
                jobsEntities.clear();
                companyEntities.clear();
                getAnnouncements();
                break;

            case 5:
                //Contacts
                intent=new Intent(getApplicationContext(), ChatHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;

        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        if (position != 0) {
            setTitle(mDrawerTitles[position-1]);
            if(position==3)setTitle("Jobs");
            updateView(position, position, true);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void getAnnouncements() {
        announcementEntities.clear();

        String url = ReqConst.SERVER_URL + "getAllAnnounceByAdminID";

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

                JSONArray announceInfo = response.getJSONArray("announce_info");

                for (int i = 0; i < announceInfo.length(); i++) {

                    JSONObject jsonAnnounce = (JSONObject) announceInfo.get(i);

                    AnnouncementEntity announce = new AnnouncementEntity();

                    announce.setIdx(jsonAnnounce.getString("an_id"));
                    announce.setLogoUrl(jsonAnnounce.getString("adminLogoImageUrl"));
                    announce.setTitle(jsonAnnounce.getString("an_title"));
                    announce.setAudience(jsonAnnounce.getString("an_audience"));
                    announce.setSubject(jsonAnnounce.getString("an_subject"));
                    announce.setDescription(jsonAnnounce.getString("an_description"));
                    announce.setCallofAction(jsonAnnounce.getString("an_callofaction"));
                    announce.setMessageOwnerEmail(jsonAnnounce.getString("an_owneremail"));
                    announce.setPictureUrl(jsonAnnounce.getString("an_image"));
                    announce.setViews(jsonAnnounce.getString("an_viewnum"));
                    announce.setResponses(jsonAnnounce.getString("an_responsenum"));
                    announce.setCompany(jsonAnnounce.getString("adminCompany"));
                    announce.setPostDate(jsonAnnounce.getString("an_postdate"));

                    if(announce.getCompany().equals(Commons.thisEntity.getCompany()))
                        announcementEntities.add(announce);
                }
                if(announcementEntities.isEmpty()) showToast("No announcements");
                else {
                    employeeList.setVisibility(View.VISIBLE);
                    providersCancel.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    announcementListAdapter.setDatas(announcementEntities);
                    announcementListAdapter.notifyDataSetChanged();
                    employeeList.setAdapter(announcementListAdapter);
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

                    jobsEntities.add(job);

                }
                if(jobsEntities.isEmpty()) showToast("No jobs");
                else {
                    employeeList.setVisibility(View.VISIBLE);
                    providersCancel.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    jobListAdapter.setDatas(jobsEntities);
                    jobListAdapter.notifyDataSetChanged();
                    employeeList.setAdapter(jobListAdapter);
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

    private void updateView(int position, int counter, boolean visible) {

        switch (position) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
        }
    }

    public void showWeather(View view){
        Intent intent=new Intent(getApplicationContext(), MmainActivity.class);
        startActivity(intent);
        //    finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    public void showMailInbox(View view){
        Commons.userEntity= Commons.thisEntity;
        Commons._inboxUserSearch=false;
        Commons._company_to_inbox=true;
        Intent intent=new Intent(getApplicationContext(), InboxActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.provider_menu_home, menu);
        return true;
    }
    public void logOut(MenuItem menuItem){

        if(Commons._is_companyManager){
            Preference.getInstance().put(getApplicationContext(),
                    PrefConst.PREFKEY_CPROVIDERPWD, "");
            Preference.getInstance().put(getApplicationContext(),
                    PrefConst.PREFKEY_CPROVIDEREMAIL, "");
            Preference.getInstance().put(getApplicationContext(),
                    PrefConst.PREFKEY_CPROVIDERID, "");
        }

        Intent intent=new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }
    public void cardCollection(MenuItem menuItem){
        Intent a = new Intent(this, CollectionActivity.class);
        startActivity(a);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("demo.mysamplecode.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        //display alert message in Web View
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("ProviderHomePage", message);
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message).setCancelable(true).show();
            result.confirm();
            return true;
        }

    }

    public class JavaScriptInterface {
        Context mContext;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        //using Javascript to call the finish activity
        public void closeMyActivity() {
            finish();
        }

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

    public void getAdminInfo() {

        String idx = Preference.getInstance().getValue(this, PrefConst.PREFKEY_CPROVIDERID, "");
        myId=idx;
        Log.d("AdminID===>",String.valueOf(idx));
        String url = ReqConst.SERVER_URL + "getAdminData";

        String params = String.format("/%s", idx);
        url += params; Log.d("URL===>",url);

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
                Log.d("AdminGetJson===>",json);
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

    public void parseGetUsersResponse(String json) {

        closeProgress();

        try{

            JSONObject response = new JSONObject(json);
            Log.d("AdminResponse===>",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray userInfo = response.getJSONArray("adminData");

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);
                    String name=jsonUser.getString("adminName");
                    String email=jsonUser.getString("adminEmail");
                    String image=jsonUser.getString("adminImageUrl");
                    String broadmoor=jsonUser.getString("adminBroadmoor");
                    String broadmoorLogo=jsonUser.getString("adminLogoImageUrl");
                    String company=jsonUser.getString("adminCompany");
                    String id=jsonUser.getString("adminID");

                    myEmail=email;
                    myName=name;

                    if(myName.contains(" ")){
                        if(myName.indexOf(" ")>=1) {
                            myFirst = myName.substring(0, myName.indexOf(" "));
                            myLast=myName.substring(myName.indexOf(" ")+1,myName.length());
                        }
                        else {
                            myFirst=myName;
                            myLast="";
                        }
                    }else {
                        myFirst=myName;
                        myLast="";
                    }

                    Log.d("AdminInfo===>",name+"/"+email+"/"+image);

                    initManagerInfo(id, name, email, image,broadmoorLogo, company);
                }

            }else if(result_code==113){
                showToast("Unregistered Admin");
            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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

                    if(companyEntity.getCompany().equals(Commons.thisEntity.getCompany()))
                        companyEntities.add(companyEntity);
                }

                employeeList.setVisibility(View.VISIBLE);
                providersCancel.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                companyListAdapter.setDatas(companyEntities);
                companyListAdapter.notifyDataSetChanged();
                employeeList.setAdapter(companyListAdapter);

            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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

    public void initManagerInfo(String adminId, String name, String email, String image, String broadmoorLogo, String company){

        userPhoto=(CircularNetworkImageView) findViewById(R.id.userPhotoNet);
        uPhoto=(CircularImageView) findViewById(R.id.userPhoto);
        userName=(TextView)findViewById(R.id.userName);
        userEmail=(TextView)findViewById(R.id.userEmail);
        Commons.thisEntity.set_photoUrl(image);
        Commons.thisEntity.set_email(email);
        Commons.thisEntity.set_name(name);
        Commons.thisEntity.setCompany(company);
        Commons.thisEntity.set_adminId(Integer.parseInt(adminId));
        userName.setText(name);
        userEmail.setText(email);
        if(image.length()<1000) {
            userPhoto.setVisibility(View.VISIBLE);
            userPhoto.setImageUrl(image, _imageloader);
        }else {
            userPhoto.setVisibility(View.GONE);
            uPhoto.setImageBitmap(base64ToBitmap(image));
        }

        registerChatRoom(Commons.thisEntity.get_email(),Commons.thisEntity.get_photoUrl(), Commons.thisEntity.get_name());
    }


    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public void registerChatRoom(final String email,final String photoUrl, final String name){

        String url = ReqConst.FIREBASE_DATABASE_URL+"users.json";

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String s) {
                Firebase reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"users/"+email.replace(".com","").replace(".","ddoott"));

                if(s.equals("null")) {

                    Map<String, String> map = new HashMap<String, String>();
                    map.put("email", email);

                    map.put("name", name);

                    map.put("photo", photoUrl);

                    reference.push().setValue(map);
                }
                else {
                    try {
                        JSONObject obj = new JSONObject(s);

                        if (!obj.has(email.replace(".com","").replace(".","ddoott"))) {

                            Map<String, String> map = new HashMap<String, String>();
                            map.put("email", email);

                            map.put("name", name);

                            map.put("photo", photoUrl);

                            reference.push().setValue(map);
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                System.out.println("" + volleyError );
            }
        });

        RequestQueue rQueue = Volley.newRequestQueue(CompanyManagerActivity.this);
        rQueue.add(request);

    }


    public void getEmployees() {

        userEntities.clear();

        String url = ReqConst.SERVER_URL + "getAllEmployeeByAdminID";

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

                params.put("adminID", Preference.getInstance().getValue(getApplicationContext(), PrefConst.PREFKEY_CPROVIDERID, ""));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("proResponse=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("Rcode=====> :",success);

            if (success.equals("0")) {

                String status="";
                JSONArray userInfo = response.getJSONArray("employee_info");

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    UserEntity employee = new UserEntity();

                    employee.set_idx(Integer.parseInt(jsonUser.getString("em_id")));
                    employee.set_photoUrl(jsonUser.getString("em_image"));
                    employee.set_name(jsonUser.getString("em_name"));
                    employee.setGender(jsonUser.getString("em_gender"));
                    employee.set_password(jsonUser.getString("em_password"));
                    employee.set_email(jsonUser.getString("em_email"));
                    employee.setMillennial(jsonUser.getString("em_millennial"));
                    employee.setVacayBucksGiven(jsonUser.getString("em_givenbuck"));
                    employee.setVacayBucksUsed(jsonUser.getString("em_usedbuck"));
                    employee.setInteractions(jsonUser.getString("em_interaction"));
                    employee.setCompany(jsonUser.getString("adminCompany"));
                    employee.set_adminId(jsonUser.getInt("adminID"));
                    employee.setLogoUrl(jsonUser.getString("adminLogoImageUrl"));
                    status=jsonUser.getString("em_status");
                    if(status.equals("") || status.equals("0")){
                        employee.setStatus("No Approved");
                    }else employee.setStatus("Login Approved");


                    userEntities.add(employee);
                }
                if(userEntities.isEmpty()) showToast("No employees");
                else {
                    employeeList.setVisibility(View.VISIBLE);
                    providersCancel.setVisibility(View.VISIBLE);
                    search.setVisibility(View.VISIBLE);
                    employeesListAdapter.setUserDatas(userEntities);
                    employeesListAdapter.notifyDataSetChanged();
                    employeeList.setAdapter(employeesListAdapter);
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

    public void showJobList(){
        if(!Commons._is_admin){
            Commons._is_select_job=true;
            Intent intent=new Intent(this, JobListActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0,0);
        }
        else {
            setTitle("Jobs");
            companyEntities.clear();
            announcementEntities.clear();
            userEntities.clear();
            getJobs(Commons.companyEntity.getAdminId());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }

    public void showAlertDialog(String msg) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);

        builder.setTitle(msg);
     //   builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectThingsForEmployee(final UserEntity user) {

        final String[] items = {"A. I want to talk to him", "B. I want to send emails to him\n  a) Username and VaCay bucks\n  b) Password"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("What do you want to do?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {

                    Commons.userEntity=user;
                    Intent intent=new Intent(getApplicationContext(), ChatActivity.class);// SurveyQuestActivity
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                else if(item==1){

                    Commons.employee=user;
              //      showAlertDialog("You will send two emails to your employee."+"\n"+"  1. Username and VaCay bucks"+"\n"+"  2. Password");
                    sendSignMailtoEmployee(String.valueOf(Commons.employee.get_idx()));
                }
            }
        });
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void sendSignMailtoEmployee(final String employeeId) {

        String url = ReqConst.SERVER_URL + "sendEmEmailfromApp";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.d("Text response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateEmStatusResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
//                closeProgress();
                Toast.makeText(getApplicationContext(),"Connection to server failed.",Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("em_id", employeeId);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);


    }

    public void parseUpdateEmStatusResponse(String json) {

        closeProgress();

        try {

            JSONObject response = new JSONObject(json);

            String success = response.getString(ReqConst.RES_CODE);

            if (success.equals("0")) {
                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_SHORT).show();
            }
            else{

                Toast.makeText(getApplicationContext(),"Mailing failed.",Toast.LENGTH_SHORT).show();
                //    Toast.makeText(getContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();;
            }

        } catch (JSONException e) {

            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Mailing failed.",Toast.LENGTH_SHORT).show();
        }
    }


    public void pushNotification(final String email) {

        final ArrayList<String> _emails=new ArrayList<>();

        _datas_user.clear();
        _emails.clear();
        Commons.userEntities.clear();

        final Firebase reference = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+ email.replace(".com","").replace(".","ddoott"));

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("Count===>", String.valueOf(dataSnapshot.getChildrenCount()));

                final Firebase reference1 = new Firebase(ReqConst.FIREBASE_DATABASE_URL+"notification/"+ email.replace(".com","").replace(".","ddoott")+"/"+dataSnapshot.getKey());
                Log.d("Reference===>", reference1.toString());

                reference1.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        Map map = dataSnapshot.getValue(Map.class);
                        try{
                            message = map.get("msg").toString();
                            sender = map.get("sender").toString();
                            photo = map.get("senderPhoto").toString();
                            name = map.get("senderName").toString();

                            Commons.notiEmail = sender.replace("ddoott",".") + ".com";
                            Commons.firebase = reference;
                            Commons.mapping=map;

                            UserEntity user = new UserEntity();
                            user.set_name(name);
                            user.set_email(Commons.notiEmail);
                            user.set_photoUrl(photo);

                            if(user.get_name().length()>0){

                                if(!_emails.contains(user.get_email())){
                                    _emails.add(user.get_email());
                                    user.set_num("1");
                                    _datas_user.add(user);
                                    ShortcutBadger.applyCount(getApplicationContext(), _datas_user.size());
                                    shownot();
                                }
                            }

                            //        showToast("You received a message!");
                        }catch (NullPointerException e){}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        if (_datas_user.size()==0)
            ShortcutBadger.removeCount(getApplicationContext());

        if(_datas_user.size()>0){
            ShortcutBadger.applyCount(getApplicationContext(), _datas_user.size());
        }
    }


    public void shownot() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        long[] v = {500,1000};

        Commons.userEntity=new UserEntity();
        Commons.userEntity.set_photoUrl(photo);
        Commons.userEntity.set_name(name);
        Commons.userEntity.set_email(Commons.notiEmail);    Log.d("NotiEmail===>",Commons.notiEmail);

        if(photo.length()>0){
            try {
                bitmapPhoto= BitmapFactory.decodeStream((InputStream) new URL(photo).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);
            }
        }else bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);

        Intent intent = new Intent(this, ChatActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new android.app.Notification.Builder(this)
                .setContentTitle(name)
                .setFullScreenIntent(pIntent,true)
                .setContentText(message)
                .setSmallIcon(R.drawable.noti).setLargeIcon(bitmapPhoto)
                .setContentIntent(pIntent)
                .setSound(uri)
                //      .setVibrate(v)
                .setAutoCancel(true).build();

        notificationManager.notify(0, n);
    }

}
