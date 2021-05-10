package com.mv.vacay.main.provider;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
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
import com.mv.vacay.adapter.BeautyListAdapter;
import com.mv.vacay.adapter.DrawerAdapter;
import com.mv.vacay.classes.Notification;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.ChatListActivity;
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.beautymen.BookingActivity;
import com.mv.vacay.main.inbox.InboxActivity;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.main.weather.MmainActivity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.Items;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.mv.vacay.models.SProviderIntentEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.nearby.Splash;
import com.mv.vacay.sms.SendSMSActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.mv.vacay.widgets.KenBurnsView;
import com.mv.vacay.widgets.Transition;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SProviderHomeActivity extends AppCompatActivity implements View.OnClickListener,KenBurnsView.TransitionListener {

    private String[] mDrawerTitles;
    private String[] mFooterTitles;
    private TypedArray mDrawerIcons;
    private ArrayList<Items> drawerItems;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList,providerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private CircularNetworkImageView userPhoto;
    private TextView userName;
    private TextView userEmail,searchUnverified;
    private static final int TRANSITIONS_TO_SWITCH = 3;
    private ViewSwitcher mViewSwitcher;
    private int mTransitionsCount = 0;
    private WebView myPage;
    private ImageView providersCancel;
    private ProgressDialog _progressDlg;
    private ImageLoader _imageloader;
    private BeautyListAdapter beautyListAdapter=new BeautyListAdapter(this);
    private ArrayList<BeautyServiceEntity> beautyServiceEntities=new ArrayList<>();
    private ArrayList<String> availables=new ArrayList<>();
    EditText ui_edtsearch;
    LinearLayout search;
    String proid="", adminid="",available="";
    String fullName="",email="",city="", address="",company="",firstName="",lastName="",image="",pwd="",token="",verified="";

    ArrayList<UserEntity> _datas_user=new ArrayList<>();

    String sender="", name="", photo="", message="";
    Bitmap bitmapPhoto=null;

    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();

    private static FragmentManager mManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sprovider_home);

        _imageloader = VaCayApplication.getInstance().getImageLoader();
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        KenBurnsView img1 = (KenBurnsView) findViewById(R.id.img1);
        img1.setTransitionListener(this);

        mTimer.schedule(doAsynchronousTask, 0, 30000);

        Commons.mNotificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Commons._vibrator=(Vibrator) getSystemService(this.VIBRATOR_SERVICE);

//        KenBurnsView img2 = (KenBurnsView) findViewById(R.id.img2);
//        img2.setTransitionListener(this);

        KenBurnsView img3 = (KenBurnsView) findViewById(R.id.img3);
        img3.setTransitionListener(this);

        Intent intent=getIntent();
        proid = intent.getStringExtra("proid");
        adminid = intent.getStringExtra("adminid");
        fullName = intent.getStringExtra("full");
        firstName = intent.getStringExtra("first");
        lastName = intent.getStringExtra("last");
        email = intent.getStringExtra("email");
        pwd = intent.getStringExtra("password");
        company=intent.getStringExtra("company");
        city = intent.getStringExtra("city");
        address = intent.getStringExtra("address");
        token = intent.getStringExtra("accountid");
        verified = intent.getStringExtra("verified");
        image = intent.getStringExtra("image");
        available= intent.getStringExtra("available");

        registerChatRoom(email,image,fullName);

        Commons._is_provider=true;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        mManager = getSupportFragmentManager();

        mDrawerTitles = getResources().getStringArray(R.array
                .drawer_provider_titles);
        mFooterTitles = getResources().getStringArray(R.array.footer_titles);
        mDrawerIcons = getResources().obtainTypedArray(R.array.drawer_provider_icons);
        drawerItems = new ArrayList<Items>();
        providerList=(ListView)findViewById(R.id.providerList);

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

        providersCancel=(ImageView)findViewById(R.id.providersCancel);
        providersCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                providerList.setVisibility(View.GONE);
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
                    beautyListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    beautyListAdapter.setUserDatas(beautyServiceEntities);
                    providerList.setAdapter(beautyListAdapter);
                }

            }
        });

        userPhoto=(CircularNetworkImageView) findViewById(R.id.userPhotoNet);
        CircularImageView userPhoto2=(CircularImageView) findViewById(R.id.userPhoto);

        userName=(TextView)findViewById(R.id.userName);
        userEmail=(TextView)findViewById(R.id.userEmail);
        Commons.thisEntity.set_photoUrl(image);
        Commons.thisEntity.set_email(email);
        Commons.thisEntity.set_name(fullName);
        Commons.thisEntity.set_idx(Integer.valueOf(proid));
        userName.setText(fullName);
        userEmail.setText(email);

        if(image.length()<1000) {
            userPhoto.setVisibility(View.VISIBLE);
            userPhoto.setImageUrl(image, _imageloader);
        }else {
            userPhoto.setVisibility(View.GONE);
            userPhoto2.setImageBitmap(base64ToBitmap(image));
        }

        try{
            pushNotification(Commons.thisEntity.get_email());
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    new Notification(getApplicationContext(),Commons.thisEntity.get_email());
                }
            });
        }
    };

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
                getProviderAvailable();
                break;
            case 2:
                //calendar
                Intent intent=new Intent(this,CalendarActivity.class);
                intent.putExtra("proid",proid);
                startActivity(intent);
                break;
            case 3:
                //My Services
                search.setVisibility(View.VISIBLE);
                providersCancel.setVisibility(View.VISIBLE);
                providerList.setVisibility(View.VISIBLE);
                getBeautyServiceByProviderId();

                break;
            case 4:
                //My Products
                Log.d("PROID===>",proid);
                intent=new Intent(getApplicationContext(), ProviderProductListActivity.class);
                intent.putExtra("proid",proid);
                intent.putExtra("company",company);
                intent.putExtra("address",address);
                startActivity(intent);
                break;
            case 5:
                // Message & Contact
                intent = new Intent(getApplicationContext(), BookingActivity.class);
                startActivity(intent);
                break;
            case 6:
                // Message & Contact
                Commons.userEntities.addAll(_datas_user);
                intent = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(intent);
                break;
            case 7:
                // Message & Contact
                intent = new Intent(getApplicationContext(), SendSMSActivity.class);
                startActivity(intent);
                break;
            case 8:
                //Nearby Places
                intent = new Intent(getApplicationContext(), Splash.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;

        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        if (position != 0) {
            setTitle(mDrawerTitles[position-1]);
            updateView(position, position, true);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
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
        Commons._datetime = "";
        Commons.userEntity= Commons.thisEntity;
        Commons._inboxUserSearch=false;
        Commons._provider_to_inbox=true;

        SProviderIntentEntity sProviderIntentEntity=new SProviderIntentEntity();
        sProviderIntentEntity.setProid(proid);
        sProviderIntentEntity.setFullName(fullName);
        sProviderIntentEntity.setFirstName(firstName);
        sProviderIntentEntity.setLastName(lastName);
        sProviderIntentEntity.setAdminid(adminid);
        sProviderIntentEntity.setEmail(email);
        sProviderIntentEntity.setPwd(pwd);
        sProviderIntentEntity.setCompany(company);
        sProviderIntentEntity.setCity(city);
        sProviderIntentEntity.setAddress(address);
        sProviderIntentEntity.setToken(token);
        sProviderIntentEntity.setVerified(verified);
        sProviderIntentEntity.setImage(image);
        sProviderIntentEntity.setAvailable(available);

        Commons.sProviderIntentEntity=sProviderIntentEntity;

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

    private  void showMyInfo(String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("My Profile");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);

        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        final CircularImageView photo2=(CircularImageView) dialogView.findViewById(R.id.photo2);

        if(Commons.thisEntity.get_photoUrl().length()< 1000) {
            photo2.setVisibility(View.GONE);
            photo.setImageUrl(Commons.thisEntity.get_photoUrl(), _imageloader);
        }
        else {
            photo2.setVisibility(View.VISIBLE);
            photo2.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setNegativeButton("MAP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Please wait...");
                Commons.thisEntity.set_city(address);
                Intent intent=new Intent(getApplicationContext(), SProviderLocationViewActivity.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void getBeautyServiceByProviderId() {

        beautyServiceEntities.clear();

        String url = ReqConst.SERVER_URL + "getServiceByProviderId";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseProBeautyServiceResponse1(response);

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

                params.put("proid", proid);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseProBeautyServiceResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseProvider====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray serviceInfo = response.getJSONArray("service_info");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);


                for (int i = 0; i < serviceInfo.length(); i++) {

                    JSONObject jsonEntity = (JSONObject) serviceInfo.get(i);

                    BeautyServiceEntity newBeautyEntity=new BeautyServiceEntity();

                    newBeautyEntity.set_idx(jsonEntity.getInt("serviceid"));
                    newBeautyEntity.set_proIdx(jsonEntity.getInt("proid"));
                    newBeautyEntity.setBeautyName(jsonEntity.getString("proBeautyCategory"));
                    newBeautyEntity.setBeautySubName(jsonEntity.getString("proBeautySubCategory"));
                    newBeautyEntity.setBeautyPrice(jsonEntity.getString("proServicePrice"));
                    newBeautyEntity.setBeautyDescription(jsonEntity.getString("proServiceDescription"));
                    newBeautyEntity.setBeautyImageUrl(jsonEntity.getString("proServicePictureUrl"));

                    newBeautyEntity.setProviderTakeHome(jsonEntity.getString("providerTakeHome"));
                    newBeautyEntity.setManagerTakeHome(jsonEntity.getString("managerTakeHome"));

                    newBeautyEntity.setCompanyName(company);
                    newBeautyEntity.setLocation(address);

                    beautyServiceEntities.add(newBeautyEntity);
                }
                beautyListAdapter.setUserDatas(beautyServiceEntities);
                beautyListAdapter.notifyDataSetChanged();
                providerList.setAdapter(beautyListAdapter);
            }
            else {

                showToast("Server connection failed...");
            }

        } catch (JSONException e) {

            showToast("Server connection failed...");

            e.printStackTrace();
        }

    }

    public void getProviderAvailable(){

        availables.clear();
        Commons.scheduleInfo.clear();

        String url = ReqConst.SERVER_URL + "getProviderAvailable";

        showProgress();

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseProviderAvailableResponse1(response);

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

                params.put("proid", proid);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseProviderAvailableResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);   Log.d("ResponseAvailable====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray userInfo = response.getJSONArray("available_info");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("available===",userInfo.toString());

                for (int i = 0; i < userInfo.length(); i++) {

                    JSONObject jsonUser = (JSONObject) userInfo.get(i);

                    ProviderScheduleEntity providerScheduleEntity=new ProviderScheduleEntity();
                    String availableId=jsonUser.getString("availableid");
                    String proId=jsonUser.getString("proid");
                    String availableStart=jsonUser.getString("availableStart");
                    String availableEnd=jsonUser.getString("availableEnd");
                    String availableComment=jsonUser.getString("availableComment");

                    providerScheduleEntity.setScheduleId(availableId);
                    providerScheduleEntity.setProId(proId);
                    providerScheduleEntity.setScheduleStart(availableStart);
                    providerScheduleEntity.setScheduleEnd(availableEnd);
                    providerScheduleEntity.setScheduleComment(availableComment);

                    Commons.scheduleInfo.add(providerScheduleEntity);
                }

                String myInfo="Name: "+fullName+"\n"+"Email: "+email+"\n"+"Password: "+pwd+"\n"+
                        "City: "+city+"\n"+"Address: "+address+"\n"+"Company: "+company+"\n"+"Status: "+verified;
                showMyInfo(myInfo);

            }
            else {

                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {

            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

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

        RequestQueue rQueue = Volley.newRequestQueue(SProviderHomeActivity.this);
        rQueue.add(request);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
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

        if(photo.length()<1000){
            try {
                bitmapPhoto=BitmapFactory.decodeStream((InputStream) new URL(photo).getContent());
            } catch (IOException e) {
                e.printStackTrace();
                bitmapPhoto=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.messages);
            }
        }else
            bitmapPhoto=base64ToBitmap(photo);

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

    public void enterMedia(BeautyServiceEntity entity){
        getMedia(String.valueOf(entity.get_idx()),"service");
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
                    mediaEntity.setObjtitle(Commons.newBeautyEntity.getBeautyName());
                    mediaEntity.setObjsubtitle(Commons.newBeautyEntity.getBeautySubName());

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




































