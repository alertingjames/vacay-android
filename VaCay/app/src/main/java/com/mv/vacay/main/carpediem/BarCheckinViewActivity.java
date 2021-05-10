package com.mv.vacay.main.carpediem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.classes.MapWrapperLayout;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.CheckinlistActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BarCheckinViewActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener  {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LatLng latLng=null;
    MapWrapperLayout mapWrapperLayout;
    LinearLayout search,share,showInfo;
    ImageView back;
    EditText edit_search;
    String info,url;
    ImageLoader _imageLoader;
    private ProgressDialog _progressDlg;
    ArrayList<UserEntity> _datas=new ArrayList<>();

    private final int[] MAP_TYPES = {
            GoogleMap.MAP_TYPE_SATELLITE,
            GoogleMap.MAP_TYPE_NORMAL,
            GoogleMap.MAP_TYPE_HYBRID,
            GoogleMap.MAP_TYPE_TERRAIN,
            GoogleMap.MAP_TYPE_NONE};
    private int curMapTypeIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chenkin_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        mapFragment.setHasOptionsMenu(true);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

//        search = (LinearLayout) findViewById(R.id.lyt_search);
//        search.setOnClickListener(this);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        showInfo=(LinearLayout)findViewById(R.id.showInfo);
        showInfo.setOnClickListener(this);
//        share = (LinearLayout) findViewById(R.id.lyt_share);
//        share.setOnClickListener(this);
//        edit_search = (EditText) findViewById(R.id.edt_search);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.showInfo:
                Commons.userEntities.clear();
                Commons.userEntities.addAll(_datas);
                Intent intent=new Intent(this,CheckinlistActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        initListeners();
    }


    private void initListeners() {

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);

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
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public static int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        searchLocationOnAddress(Commons.gameEntity.getKnownName());
    }
    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(),"Service connection suspended",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Service connection failed",Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

    }
    @Override
    public void onMapClick(LatLng latLng1) {

    }
    @Override
    public boolean onMarkerClick(Marker marker) {

        return false;
    }

    private String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this);
        String address = "";
        try {
            address = geocoder
                    .getFromLocation(latLng.latitude, latLng.longitude,1)
                    .get(0).getAddressLine(0);
        } catch (IOException e) {}
        return address;
    }

    private void initCamera(LatLng location) {
        CameraPosition position = CameraPosition.builder()
                .target(location)
                .zoom(15f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(position), null);


        mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
        mMap.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled( true );
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    private void searchLocationOnAddress(String placeName) {
        closeProgress();
        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            String addr=placeName.trim();
            addresses = geocoder.getFromLocationName(addr, 1);

            if(addresses.size() > 0){
                mMap.clear();
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                String address=addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d("KnownName===>",knownName);
                String zip = addresses.get(0).getPostalCode();
                url= addresses.get(0).getUrl();

                latLng=new LatLng(latitude,longitude); Log.d("ProLatlng===",latLng.toString());


                info="address: "+address+"\n"+"city: "+city+"\n"+"state: "+state+"\n"+"country: "+country+"\n"+"postalCode: "+postalCode+"\n"+"publicName: "+knownName+"\n"+"zip: "+zip+"\n"+"url: "+url+"\n"+"loc: "+latitude+"/"+longitude+"\n"+"centre: "+getAddressFromLatLng(latLng);

//                Commons.requestLatlng=latLng;
                Log.d("POSITION===>",String.valueOf(latitude)+String.valueOf(longitude));

                MarkerOptions options = new MarkerOptions().position(latLng);
                options.snippet("latlng: " + String.valueOf(latLng));
                options.title("myAddr: " + address);  Log.d("Infos===>",info);
                //    options.title(String.valueOf(latLng));

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options).showInfoWindow();
                initCamera(latLng);

                checkIn();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.toast_view, null);
            TextView textView=(TextView)dialogView.findViewById(R.id.text);
            textView.setText("No correct address.");
            Toast toast=new Toast(this);
            toast.setView(dialogView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void getFullAddressFromLocation(LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        String zip = addresses.get(0).getPostalCode();
        String url= addresses.get(0).getUrl();

    }

    public String getKnownNameFromLocation(LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        String zip = addresses.get(0).getPostalCode();
        String url= addresses.get(0).getUrl();

        info="address: "+address+"\n"+"city: "+city+"\n"+"state: "+state+"\n"+"country: "+country+"\n"+"postalCode: "+postalCode+"\n"+"publicName: "+knownName+"\n"+"zip: "+zip+"\n"+"url: "+url+"\n"+"loc: "+latLng.latitude+"/"+latLng.longitude+"\n"+"centre: "+getAddressFromLatLng(latLng);


        return knownName;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private  void showInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Request location information");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView)dialogView.findViewById(R.id.photo);
        if(Commons.thisEntity.get_photoUrl().length()>0)
            photo.setImageUrl(Commons.thisEntity.get_photoUrl(),_imageLoader);
        else photo.setDefaultImageResId(Commons.thisEntity.get_imageRes());
        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        textview.setText(info);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Commons.loc_url=url;
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void checkIn() {

        _datas.clear();
        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETALLUSERS;

//        String params = String.format("/%d", _curpage);
//        url += params;

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        closeProgress();
        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                double[] lats={39.5530507,39.7380517,39.5542507,39.5501507};
                double[] lngs={-105.78306,-104.99506,-105.78706,-105.78406};
                String[] publicNames={"Colorado","denver","colorado","colorado"};
                String[] relationships={"In Relationship","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    user.set_age_range(jsonUser.getString(ReqConst.RES_AGE));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(user.get_age_range());
                    int age=year-birthyear;
                    user.set_age_range(String.valueOf(age));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("-",","));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    user.set_relations(jsonUser.getString("relationship"));
                    user.set_publicName(jsonUser.getString("place_name").replace("-",",").replace("%20"," "));
                    user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon").replace("%20","-").replace("-",".")));
                    user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat").replace("%20","-").replace("-",".")));

//                    if(jsonUser.getString("place_name").replace("-",",").replace("%20"," ").length()>0){
//
//                    }
//                    else {
//                        user.set_relations(relationships[i]);
//                        user.set_publicName(publicNames[i]);
//                        user.set_userlng(lngs[i]);
//                        user.set_userlat(lats[i]);
//                    }

                    // except me
                    if (user.get_idx() == Commons.thisEntity.get_idx())
                        continue;

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    try{
                        if(Commons.gameEntity.getKnownName().toLowerCase().trim().equals(user.get_publicName().toLowerCase().trim())) _datas.add(0,user);
                        Log.d("CheckinUser===",user.get_fullName()+" "+user.get_publicName());

                    }catch (NullPointerException e){}

                }

                if(_datas.isEmpty()) showToast("No people checked in");

                for(int j=0;j<_datas.size();j++){

                    try{
                        final double lat=_datas.get(j).get_userlat();   Log.d("LAT===",String.valueOf(lat));
                        final double lng=_datas.get(j).get_userlng();   Log.d("LNG===",String.valueOf(lng));
                        LatLng loc=new LatLng(lat,lng);  Log.d("LOC===",loc.toString());
//        loc = Commons.beautyEntity.get_providerLatlng();
                        int resId = _datas.get(j).get_imageRes();
                        String imageUrl = _datas.get(j).get_photoUrl();

                        if(resId!=0){
                            addCustomMarker(resId, new LatLng(lat,lng));
                        }
                        else {
                            try {
                                Bitmap image = drawableToBitmap(LoadImageFromWebOperations(imageUrl));
                                addCustomMarker_bitmap(image, new LatLng(lat,lng));
                            }catch (NullPointerException e){
//                    options = new MarkerOptions().position(new LatLng(lat,lng));
//                    options.title("Click marker for detail.");
//                    options.snippet("Addr:" + getAddressFromLatLng(new LatLng(lat,lng)));
//                    //    options.title(String.valueOf(latLng));
//
//                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                    mMap.addMarker(options).showInfoWindow();
//                initCamera(loc);
                            }
                        }

                        MapsInitializer.initialize(this);
                    }catch (NullPointerException e){}
                }

            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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
            try{
                _progressDlg.dismiss();
                _progressDlg = null;
            }catch (IllegalArgumentException e){}
        }
    }

    private void addCustomMarker(int ResId,LatLng latLng) {
        Log.d("", "addCustomMarker()");
        if (mMap == null) {
            return;
        }
        // adding a marker on map with image from  drawable
        try{
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(ResId))));
        }catch (IllegalArgumentException e){}
    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private void addCustomMarker_bitmap(Bitmap bitmap,LatLng latLng) {
        Log.d("", "addCustomMarker()");
        if (mMap == null) {
            return;
        }
        // adding a marker on map with image from  drawable
        try{
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView_bitmap(bitmap))));
        }catch (IllegalArgumentException e){}
    }

    private Bitmap getMarkerBitmapFromView_bitmap(@DrawableRes Bitmap bitmap) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageBitmap(bitmap);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
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

}
