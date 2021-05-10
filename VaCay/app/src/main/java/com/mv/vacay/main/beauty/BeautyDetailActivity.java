package com.mv.vacay.main.beauty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import com.mv.vacay.utils.CircularNetworkImageView;

public class BeautyDetailActivity extends AppCompatActivity implements View.OnClickListener{

    NetworkImageView beautyImage;
    ImageView back;
    private AdView mAdView;
    CircularNetworkImageView providerImage;
    TextView providerName,beautyPrice,beautyCategory,beautyName,beautyDescription;
    TextView accept;
    ProgressDialog  _progressDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_detail);
        ImageLoader _imageloader = VaCayApplication.getInstance().getImageLoader();

        closeProgress();
        beautyImage=(NetworkImageView) findViewById(R.id.beauty_image);

        if(Commons.beautyEntity.get_beauty_imageURL().length()>0)
            beautyImage.setImageUrl(Commons.beautyEntity.get_beauty_imageURL(),_imageloader);
        else

        if(Commons.beautyEntity.get_beauty_resId()!=0)
            beautyImage.setDefaultImageResId(Commons.beautyEntity.get_beauty_resId());
        else
            beautyImage.setImageBitmap(Commons.beautyEntity.get_beautyBitmap());


//        if(!Commons.beautyEntity.get_beautyBitmap().equals(null))beautyImage.setImageBitmap(Commons.beautyEntity.get_beautyBitmap());
//        else beautyImage.setImageResource(Commons.beautyEntity.get_beauty_resId());
        beautyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.resId=Commons.beautyEntity.get_beauty_resId();
                Commons.photoUrl=Commons.beautyEntity.get_beauty_imageURL();
                Intent intent=new Intent(getApplicationContext(),ViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });

        providerImage=(CircularNetworkImageView) findViewById(R.id.provider_profile);
        if(Commons.beautyEntity.get_provider_imageURL().length()>0)
            providerImage.setImageUrl(Commons.beautyEntity.get_provider_imageURL(),_imageloader);
        else

        if(Commons.beautyEntity.get_provider_resId()!=0)
            providerImage.setDefaultImageResId(Commons.beautyEntity.get_provider_resId());
        else
            providerImage.setImageBitmap(Commons.beautyEntity.get_providerBitmap());


//        if(!Commons.beautyEntity.get_providerBitmap().equals(null))providerImage.setImageBitmap(Commons.beautyEntity.get_providerBitmap());
//        else providerImage.setImageResource(Commons.beautyEntity.get_provider_resId());
        providerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("Please wait.........");
//                showProgress();
                Intent intent=new Intent(getApplicationContext(),ProviderLocationViewActivity.class);
                startActivity(intent);
//                finish();
                overridePendingTransition(0,0);
            }
        });

        providerName=(TextView)findViewById(R.id.provider_name);
        providerName.setText(Commons.beautyEntity.get_provider_name());

        beautyPrice=(TextView)findViewById(R.id.beauty_price);
        beautyPrice.setText(String.valueOf("Price: $"+Commons.beautyEntity.get_beauty_price()));

        beautyCategory=(TextView)findViewById(R.id.beauty_category);
        beautyCategory.setText("Category: "+Commons.beautyEntity.get_beauty_category());

        beautyName=(TextView)findViewById(R.id.beauty_name);
        beautyName.setText("Beauty name: "+Commons.beautyEntity.get_beauty_name());

        beautyDescription=(TextView)findViewById(R.id.description);
        beautyDescription.setText(Commons.beautyEntity.get_description());

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        accept=(TextView)findViewById(R.id.accept_request);
        accept.setOnClickListener(this);

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
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

    public void showProgress(boolean cancelable) {

        closeProgress();

        _progressDlg = new ProgressDialog(this, R.style.MyTheme);
        _progressDlg
                .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        _progressDlg.setCancelable(cancelable);
        _progressDlg.show();
    }

    public void showProgress() {
        showProgress(false);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        _progressDlg.dismiss();
        _progressDlg = null;
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
            case R.id.accept_request:
                Intent intent=new Intent(this,BeautyServiceRequestActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
        }
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
