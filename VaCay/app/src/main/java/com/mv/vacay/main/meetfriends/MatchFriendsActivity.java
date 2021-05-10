package com.mv.vacay.main.meetfriends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.fragments.FragmentA;
import com.mv.vacay.fragments.FragmentB;
import com.mv.vacay.fragments.FragmentC;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

public class MatchFriendsActivity extends AppCompatActivity {

    ImageLoader _imageLoader;
    AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_friends);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(1);
        showInfo();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return FragmentA.newInstance("FirstFragment, Instance 1");
                case 1: return FragmentB.newInstance("SecondFragment, Instance 1");
                case 2: return FragmentC.newInstance("ThirdFragment, Instance 1");
                default: return FragmentB.newInstance("SecondFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    private  void showInfo() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Hint!");
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_alertdialog, null);

        final CircularNetworkImageView photo=(CircularNetworkImageView) dialogView.findViewById(R.id.photo);
        final CircularImageView photo2=(CircularImageView) dialogView.findViewById(R.id.photo2);

        if(Commons.userEntity.get_photoUrl().length()<1000) {
            photo.setImageUrl(Commons.userEntity.get_photoUrl(), VaCayApplication.getInstance().getImageLoader());
        }
        else {
            photo2.setVisibility(View.VISIBLE);
            photo2.setImageBitmap(base64ToBitmap(Commons.userEntity.get_photoUrl()));
        }


        final TextView textview = (TextView) dialogView.findViewById(R.id.customView);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        textview.setTypeface(font);
        textview.setText("Swipe right if you want to accept this friend and swipe left if you don’t want to accept the friend");
        builder.setView(dialogView);
        builder.setIcon(R.drawable.noti);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alert = builder.create();
        alert.getWindow().getAttributes().windowAnimations = R.style.DialogTheme3; //style id
        alert.show();
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( alert!=null && alert.isShowing() ){
            alert.cancel();
            alert.dismiss();
        }
    }
}
