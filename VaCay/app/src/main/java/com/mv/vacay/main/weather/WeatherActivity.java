package com.mv.vacay.main.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener   {

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private LatLng latLng=null;
    WebView webview;
    LinearLayout searchButton;
    EditText inputBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        title.setTypeface(font);

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(AppIndex.API).build();

        webview = (WebView) findViewById(R.id.webView);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webview.setWebChromeClient(new WebChromeClient());
        webview.loadUrl("https://darksky.net/forecast/38.7868,-104.8455/si12/en");

        inputBox=(EditText)findViewById(R.id.inputbox);

        searchButton=(LinearLayout)findViewById(R.id.search_button);
        searchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        searchButton.setBackground(getDrawable(R.drawable.black_fill_rect));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        searchButton.setBackground(getDrawable(R.drawable.green_thin_stroke));
                        searchLocationOnAddress();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        searchButton.getBackground().clearColorFilter();
                        searchButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        final LinearLayout mylocButton=(LinearLayout)findViewById(R.id.myloc);

        mylocButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mylocButton.setBackground(getDrawable(R.drawable.yellow_round_fill));
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        mylocButton.setBackground(getDrawable(R.drawable.login_roundrect));
                        Intent intent=new Intent(getApplicationContext(),WeatherActivity.class);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(0,0);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        mylocButton.getBackground().clearColorFilter();
                        mylocButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
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

        try {
            latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            double lat=latLng.latitude;
            double lng=latLng.longitude;
            webview.loadUrl("https://darksky.net/forecast/"+lat+","+lng+"/si12/en");

        }catch (NullPointerException e){
            e.printStackTrace();
            webview.loadUrl("https://darksky.net/forecast/38.7868,-104.8455/si12/en");
        }

    }

    private void searchLocationOnAddress() {
        List<Address> addresses =null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            String addr = inputBox.getText().toString();
            addresses = geocoder.getFromLocationName(addr, 1);

            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                webview.loadUrl("https://darksky.net/forecast/"+latitude+","+longitude+"/si12/en");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
