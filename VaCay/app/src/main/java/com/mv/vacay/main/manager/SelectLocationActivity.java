package com.mv.vacay.main.manager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
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

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mv.vacay.R;
import com.mv.vacay.classes.MapWrapperLayout;
import com.mv.vacay.commons.Commons;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
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
        setContentView(R.layout.activity_request_location_view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
        mapFragment.getMapAsync(this);
        mapWrapperLayout.init(mMap, getPixelsFromDp(this, 39 + 20));

        mapFragment.setHasOptionsMenu(true);

        Commons.providerLat=0.0f;
        Commons.providerlng=0.0f;

        search = (LinearLayout) findViewById(R.id.lyt_search);
        search.setOnClickListener(this);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        showInfo=(LinearLayout)findViewById(R.id.showInfo);
        showInfo.setOnClickListener(this);
//        share = (LinearLayout) findViewById(R.id.lyt_share);
//        share.setOnClickListener(this);
        edit_search = (EditText) findViewById(R.id.edt_search);

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
            case R.id.lyt_search:
                showToast("Please wait..........");
                searchLocationOnAddress();
                break;
            case R.id.showInfo:
                showInfo();
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

        mMap.setMapType(MAP_TYPES[curMapTypeIndex]);
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
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        double latitude = latLng1.latitude;
        double longitude = latLng1.longitude;

        Log.e("latitude", "latitude--" + latitude);

        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(addresses.size() > 0){

                String address=addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                String zip = addresses.get(0).getPostalCode();
                url= addresses.get(0).getUrl();

                latLng=new LatLng(latitude,longitude);
                Commons.loc_url=url;
                Commons.providerLatlng=latLng;
                Commons.providerLat=latitude;
                Commons.providerlng=longitude;

                info="address: "+address+"\n"+"city: "+city+"\n"+"state: "+state+"\n"+"country: "+country+"\n"+"postalCode: "+postalCode+"\n"+"publicName: "+knownName+"\n"+"zip: "+zip+"\n"+"url: "+url+"\n"+"loc: "+latitude+"/"+longitude+"\n"+"centre: "+getAddressFromLatLng(latLng);

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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    private void searchLocationOnAddress() {
        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            String addr=edit_search.getText().toString();
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
                url= addresses.get(0).getUrl();

                latLng=new LatLng(latitude,longitude); Log.d("ProLatlng===",latLng.toString());
                Commons.loc_url=url;
                Commons.providerLatlng=latLng;
                Commons.providerLat=latitude; Log.d("Lat===",String.valueOf(latitude));
                Commons.providerlng=longitude;  Log.d("Lng===",String.valueOf(longitude));

                info="address: "+address+"\n"+"city: "+city+"\n"+"state: "+state+"\n"+"country: "+country+"\n"+"postalCode: "+postalCode+"\n"+"publicName: "+knownName+"\n"+"zip: "+zip+"\n"+"url: "+url+"\n"+"loc: "+latitude+"/"+longitude+"\n"+"centre: "+getAddressFromLatLng(latLng);

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

    private  void showInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Request location information");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);
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

}
