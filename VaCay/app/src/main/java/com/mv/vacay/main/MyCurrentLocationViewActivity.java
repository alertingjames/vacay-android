package com.mv.vacay.main;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.mv.vacay.classes.ImageLoader1;
import com.mv.vacay.classes.MapWrapperLayout;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.activity.ActionsActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MyCurrentLocationViewActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
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
    ImageView home,show;
    String info,info1;
    ProgressDialog _progressDlg;
    ImageLoader _imageLoader;
    ImageLoader1 imgLoader;
    Intent intent;

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
        setContentView(R.layout.activity_my_current_location_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        mapFragment.setHasOptionsMenu(true);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
        imgLoader = new ImageLoader1(getApplicationContext());

        showProgress();

        home=(ImageView)findViewById(R.id.imv_profile);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Commons._isMyLocationVerified){
                    intent=new Intent(getApplicationContext(),SignupActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);
                }
                else {
                    register();
                }

            }
        });

        show=(ImageView)findViewById(R.id.showInfo);
        show.setOnClickListener(this);

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
                if(info!=null)showInfo(info);
                break;

        }
    }

    public void showProgress() {
//        closeProgress();
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

    private void myLocationView() throws IOException {

        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocationName(Commons.thisEntity.get_city(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            if(addresses.size() > 0){
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();

                String address=addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String zip = addresses.get(0).getPostalCode();

                latLng=new LatLng(latitude,longitude);

                info="The current location's information: "+"\n"+"Address: "+address+"\n"+"City: "+city+"\n"+"State: "+state+"\n"+"Country: "+country+"\n"+"ZipCode: "+zip;

                Log.d("POSITION===>",String.valueOf(latitude)+String.valueOf(longitude));

                String imageUrl=Commons.thisEntity.get_photoUrl();

                try {
//                Bitmap image = drawableToBitmap(LoadImageFromWebOperations(imageUrl));
                    if(imageUrl.length()>1000)
                        addCustomMarker_bitmap(base64ToBitmap(imageUrl), latLng);
                    else
                        addCustomMarker_bitmap(drawableToBitmap(LoadImageFromWebOperations(imageUrl)), latLng);

                }catch (NullPointerException e){
                    MarkerOptions options = new MarkerOptions().position(latLng);
                    options.title("Click marker for detail.");
                    options.snippet("Addr:" + getAddressFromLatLng(latLng));
                    //    options.title(String.valueOf(latLng));

                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    mMap.addMarker(options).showInfoWindow();
//                initCamera(loc);
                }
                MapsInitializer.initialize(this);
                initCamera(latLng);
            }
        }catch (NullPointerException e){
            showToast("Google map service failed.");
        }

        LatLng loc=latLng;
        int resId=Commons.thisEntity.get_imageRes();

        try {

            URL fb_url = new URL(Commons.thisEntity.get_photoUrl());//small | noraml | large
            HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
            HttpsURLConnection.setFollowRedirects(true);
            conn1.setInstanceFollowRedirects(true);
            Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());

            addCustomMarker_bitmap(fb_img, loc);

            MapsInitializer.initialize(this);

            initCamera(loc);
        }catch (NetworkOnMainThreadException exception){
            try{
                MarkerOptions options = new MarkerOptions().position(loc);
                options.title("Click marker for detail.");
                options.snippet("Addr:" + getAddressFromLatLng(loc));
                //    options.title(String.valueOf(latLng));

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options).showInfoWindow();
                initCamera(loc);
            }catch (NullPointerException e){
                showToast("Google map service failed.");
            }
        }

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

    public String getCityFromLocation(LatLng latLng){
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

        info="Address: "+address+"\n"+"City: "+city+"\n"+"State: "+state+"\n"+"Country: "+country+"\n"+"ZipCode: "+zip;
        Log.d("Infos===>",info);

        return city;
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

    private void initListeners() {

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerDragListener(this);

    }

    private void CaptureScreen() {

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap=null;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                if(Commons._location_activity) {
                    Commons.bitmap_activity=bitmap;
                    Commons._location_activity=false;
                }
                else Commons.bitmap=bitmap;
                try {
                    saveImage(bitmap);
                    Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            private void saveImage(Bitmap bitmap) throws IOException{
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 40, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "test.png");
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.close();

            }
        };

        mMap.snapshot(callback);

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

        mCurrentLocation = LocationServices
                .FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        try{
            latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            String city=getCityFromLocation(latLng);
            Commons.thisEntity.set_city(city);
            closeProgress();

            showInfo1(info);

        }catch (NullPointerException e){
            closeProgress();
            showToast("Sorry, but we can't find your current location. Please try again.");
            if(!Commons._isMyLocationVerified){
                intent=new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
            else {
                intent=new Intent(getApplicationContext(),ActionsActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        }


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
    public void onMapClick(LatLng latLng) {

    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        try{
            if(info!=null)showInfo(info);
        }catch (NullPointerException e){}
        return true;
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
                .zoom(16f)
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

    private void searchLocationOnAddress(String addr) {
        closeProgress();
        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {

            addresses = geocoder.getFromLocationName(addr, 1);

            if(addresses.size() > 0){
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                String address=addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String zip = addresses.get(0).getPostalCode();
                String url= addresses.get(0).getUrl();

                latLng=new LatLng(latitude,longitude);

                info="The current location's information: "+"\n"+"address: "+address+"\n"+"city: "+city+"\n"+"state: "+state+"\n"+"country: "+country+"\n"+"postalCode: "+postalCode+"\n"+"publicName: "+knownName+"\n"+"zip: "+zip+"\n"+"url: "+url+"\n"+"loc: "+latitude+"/"+longitude+"\n"+"centre: "+getAddressFromLatLng(latLng);

//                Commons.loc_url=info;
//                Commons.requestLatlng=latLng;
                Log.d("POSITION===>",String.valueOf(latitude)+String.valueOf(longitude));

                MarkerOptions options = new MarkerOptions().position(latLng);
                options.title("Cntr:" + getAddressFromLatLng(latLng));
                options.snippet(String.valueOf(latLng));
                //    options.title(String.valueOf(latLng));

                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(options).showInfoWindow();
                initCamera(latLng);

            }
        } catch (IOException e) {
            e.printStackTrace();
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.toast_view, null);
            TextView textView=(TextView)dialogView.findViewById(R.id.text);
            textView.setText("Please input correct address.");
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

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    private  void showInfo(String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("My location information");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        final CircularImageView photo2=(CircularImageView) dialogView.findViewById(R.id.photo2);

        if(Commons.thisEntity.get_photoUrl().length()<1000) {
            photo.setImageUrl(Commons.thisEntity.get_photoUrl(), _imageLoader);
        }
        else {
            photo2.setVisibility(View.VISIBLE);
            photo2.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        textview.setTypeface(font);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }



    private  void showInfo1(String infomation) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Your Current Location");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);

        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        final CircularImageView photo2=(CircularImageView) dialogView.findViewById(R.id.photo2);

        if(Commons.thisEntity.get_photoUrl().length()<1000) {
            photo.setImageUrl(Commons.thisEntity.get_photoUrl(), _imageLoader);
        }
        else {
            photo2.setVisibility(View.VISIBLE);
            photo2.setImageBitmap(base64ToBitmap(Commons.thisEntity.get_photoUrl()));
        }

        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        textview.setTypeface(font);
        textview.setText(infomation);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!Commons._isMyLocationVerified){
                    intent=new Intent(getApplicationContext(),SignupActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);
                }
                else {
                    register();
                }
            }
        });
        builder.setNegativeButton("VIEW ON MAP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("Please wait...");
                try {
                    myLocationView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
            }
        });

        AlertDialog alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        alert.show();
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

    public void register() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_REGISTERUSER;

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

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

                params.put("email", Commons.thisEntity.get_email());
                params.put("first_name", Commons.thisEntity.get_firstName());
                params.put("last_name", Commons.thisEntity.get_lastName());
                params.put("age", Commons.thisEntity.get_age_range());
                params.put("address", Commons.thisEntity.get_city());
                params.put("job", Commons.thisEntity.get_job());
                params.put("education", Commons.thisEntity.get_education());
                params.put("interests", Commons.thisEntity.get_interest().replace("(","ppp")
                        .replace(")","qqq").replace(",","separate").replace("\n",""));
                params.put("relationship", Commons.thisEntity.get_relations());
                if(Commons.thisEntity.get_adminId()>0)
                    params.put("place_name", String.valueOf(Commons.thisEntity.getCompany()));
                else params.put("place_name", " ");
                params.put("user_lat", String.valueOf(latLng.latitude));
                params.put("user_lon", String.valueOf(latLng.longitude));
                params.put("photo_url", Commons.thisEntity.get_photoUrl());
                params.put("survey_one", Commons.thisEntity.getSurvey_one());
                params.put("survey_two", Commons.thisEntity.getSurvey_two());
                params.put("survey_four", Commons.thisEntity.getSurvey_four());
                params.put("survey_three", Commons.thisEntity.getSurvey_three());
                params.put("survey_five", Commons.thisEntity.getSurvey_five());

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }


    public void parseRegisterResponse(String json) {

        closeProgress();

        Log.d("JsonAAA====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {

                showToast("Your location is updated with your current location successfully.");
            }
            else {
                closeProgress();
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            closeProgress();
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

        Intent intent=new Intent(this,ActionsActivity.class);
        startActivity(intent);
        finish();
    }
}
