package com.mv.vacay.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.fragments.FragmentX;
import com.mv.vacay.fragments.FragmentY;
import com.mv.vacay.fragments.Fragment_Compose;
import com.mv.vacay.fragments.Fragment_Inbox;
import com.mv.vacay.fragments.Fragment_SentMessages;
import com.mv.vacay.main.meetfriends.MessageActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

public class MediaActivity extends AppCompatActivity {

    CircularImageView providerImage;
    CircularNetworkImageView providerImageNet;
    TextView title, subtitle;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        title=(TextView)findViewById(R.id.title);
        subtitle=(TextView)findViewById(R.id.subtitle);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        subtitle.setTypeface(font);
        title.setTypeface(font);


        providerImage=(CircularImageView) findViewById(R.id.imv_photo);
        providerImageNet=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);

        if(Commons.mediaEntity.getObjimage().length()>1000) {
            providerImageNet.setVisibility(View.GONE);
            providerImage.setImageBitmap(base64ToBitmap(Commons.mediaEntity.getObjimage()));
        }else {
            providerImageNet.setVisibility(View.VISIBLE);
            providerImageNet.setImageUrl(Commons.mediaEntity.getObjimage(), VaCayApplication.getInstance()._imageLoader);
        }

        title.setText(Commons.mediaEntity.getObjtitle());
        subtitle.setText(Commons.mediaEntity.getObjsubtitle());

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabsStrip.setViewPager(pager);

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

    private class MyPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        final int[] icons = new int[]{R.drawable.galleryicon, R.drawable.videorecordicon};
        private String tabTitles[] = {"Pictures", "Videos"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position){
//                case 0: return "Pictures";
//                case 1: return "Videos";
//            }
//            return "Pictures";
//        }
        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return FragmentX.newInstance("FirstFragment, Instance 1");
                case 1: return FragmentY.newInstance("SecondFragment, Instance 2");
                default: return FragmentX.newInstance("FirstFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getPageIconResId(int position) {
            return icons[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            Drawable image = getApplicationContext().getResources().getDrawable(icons[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            // Replace blank spaces with image icon
            SpannableString sb = new SpannableString("    " + tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

}
