package com.mv.vacay.main.restaurant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.meetfriends.MeetFriendActivity;

public class RestaurantMenuDetailActivity extends AppCompatActivity implements View.OnClickListener{

    NetworkImageView resImage;
    TextView resname, restype,resmenu,opentable,resloc,btncontinue;
    ImageLoader _imageLoader;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_menu_detail);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText("Would you go to this restaurant? If you want, please click 'Continue' button.");
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
    //    toast.show();

//        Toast.makeText(this,"If you want to go to this restaurant, please click 'Continue' button.",Toast.LENGTH_LONG).show();

        resImage=(NetworkImageView)findViewById(R.id.resimage);
        if (Commons.restaurantEntity.get_photoUrl().length() > 0) {
            resImage.setImageUrl(Commons.restaurantEntity.get_photoUrl(),_imageLoader);
        }
        if(Commons.restaurantEntity.getImageRes()!=0) resImage.setDefaultImageResId(Commons.restaurantEntity.getImageRes());
        else resImage.setImageBitmap(Commons.restaurantEntity.getImageBitmap());

        resname=(TextView)findViewById(R.id.resname);
        resname.setText(Commons.restaurantEntity.getRestaurant_name());

        restype=(TextView)findViewById(R.id.restype);
        restype.setText(Commons.restaurantEntity.getRestaurant_type());

        resmenu=(TextView)findViewById(R.id.resmenu);
        resmenu.setText(Commons.restaurantEntity.getFood_menu_url());
        resmenu.setMovementMethod(LinkMovementMethod.getInstance());

        opentable=(TextView)findViewById(R.id.opentable);
        opentable.setText(Commons.restaurantEntity.getOpentable_url());
        opentable.setMovementMethod(LinkMovementMethod.getInstance());

        resloc=(TextView)findViewById(R.id.resloc);
        resloc.setText(Commons.restaurantEntity.getRestaurant_location_url());
        resloc.setMovementMethod(LinkMovementMethod.getInstance());

        btncontinue=(TextView)findViewById(R.id.button_continue);
        btncontinue.setOnClickListener(this);

//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
//        startActivity(intent);

//        String kmlWebAddress = "http://www.afischer-online.de/sos/AFTrack/tracks/e1/01.24.Soltau2Wietzendorf.kml";
//        String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s",kmlWebAddress);
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//        startActivity(intent);

//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr="+src_lat+","+src_ltg+"&daddr="+des_lat+","+des_ltg));
//        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//        startActivity(intent);

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")    //     C04B1BFFB0774708339BC273F8A43708
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_continue:
                showQuestAloneOrWithFriend();
                break;
        }
    }
    private  void showQuestAloneOrWithFriend() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You can go alone or with a friend there.\n Do you want to go there with a friend?");
//        builder.setTitle(Commons.userEntity.get_city());

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),MeetFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });
        builder.setNegativeButton("No, alone", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}
