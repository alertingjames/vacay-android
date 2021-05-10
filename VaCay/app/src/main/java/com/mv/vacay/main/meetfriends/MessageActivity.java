package com.mv.vacay.main.meetfriends;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.astuetz.PagerSlidingTabStrip;
import com.ayz4sci.androidfactory.permissionhelper.PermissionHelper;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.config.Config;
import com.mv.vacay.fragments.Fragment_Compose;
import com.mv.vacay.fragments.Fragment_Inbox;
import com.mv.vacay.fragments.Fragment_SentMessages;
import com.mv.vacay.main.activity.GolfActivity;
import com.mv.vacay.main.activity.RunningActivity;
import com.mv.vacay.main.activity.SkiingSnowboardingActivity;
import com.mv.vacay.main.activity.TennisActivity;
import com.mv.vacay.main.beauty.BeautyServiceEntryActivity;
import com.mv.vacay.main.beautymen.MenBeautyServiceActivity;
import com.mv.vacay.main.carpediem.VideoDisplayTestActivity;
import com.mv.vacay.main.location.LocationCaptureActivity;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pl.tajchert.nammu.PermissionCallback;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener{

    CircularNetworkImageView friend_photo_net;
    CircularImageView friend_photo;
    TextView friend_name, friend_email,ok;
    ImageView attachbutton,back,location;
    ImageView image;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;
    ImageLoader _imageLoader;
    File file=null;
    boolean _takePhoto=false;
    private AdView mAdView;
    LinearLayout imagePortion;

    PermissionHelper permissionHelper;
    Bitmap bm=null;
    File destination=null;

    private static final String TAG = MessageActivity.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        permissionHelper = PermissionHelper.getInstance(this);

        if(!Commons._request_set){
//            confirmSelectFriend();
        }
        friend_photo_net=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);
        friend_photo=(CircularImageView) findViewById(R.id.imv_photo);

        if(Commons._request_set){
            if(Commons.newBeautyEntity.getProviderPhotoUrl().length()<1000) {
                friend_photo_net.setVisibility(View.VISIBLE);
                friend_photo_net.setImageUrl(Commons.newBeautyEntity.getProviderPhotoUrl(), _imageLoader);
            }
            else {
                friend_photo_net.setVisibility(View.GONE);
                friend_photo.setImageBitmap(base64ToBitmap(Commons.newBeautyEntity.getProviderPhotoUrl()));
            }
        }else {

            try {
                if (Commons.userEntity.get_photoUrl().length() < 1000) {
                    friend_photo_net.setVisibility(View.VISIBLE);
                    friend_photo_net.setImageUrl(Commons.userEntity.get_photoUrl(), _imageLoader);
                } else {
                    friend_photo_net.setVisibility(View.GONE);
                    friend_photo.setImageBitmap(base64ToBitmap(Commons.userEntity.get_photoUrl()));
                    Log.d("USERIMAGE===", String.valueOf(Commons.userEntity.get_imageRes()));
                }
            }catch (NullPointerException e){}

        }

        imagePortion=(LinearLayout)findViewById(R.id.imagePortion);

        friend_name=(TextView)findViewById(R.id.txv_name);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        friend_name.setTypeface(font);


        try {
            if (!Commons.userEntity.get_name().equals(""))
                friend_name.setText(Commons.userEntity.get_name());
            else friend_name.setText(Commons.userEntity.get_fullName()); Log.d("USERNAME===",friend_name.getText().toString());
        }catch (NullPointerException e){}
        friend_email=(TextView)findViewById(R.id.txv_email);
        try {
            friend_email.setText(Commons.userEntity.get_email());
        }catch (NullPointerException e){}

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        image=(ImageView) findViewById(R.id.image);
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePortion.setVisibility(View.GONE);
            }
        });

        attachbutton=(ImageView)findViewById(R.id.attachbutton);
        attachbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               openMenuItems();
            }
        });
        location=(ImageView)findViewById(R.id.locationbutton);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelectLocation();
            }
        });

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(pager);
        if(Commons._is_composeDateBack||Commons._request_set) pager.setCurrentItem(2);
        else pager.setCurrentItem(0);

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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }



    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Inbox";
                case 1: return "Sent Messages";
                case 2: return "Compose";
            }
            return "Inbox";
        }
        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return Fragment_Inbox.newInstance("FirstFragment, Instance 1");
                case 1: return Fragment_SentMessages.newInstance("SecondFragment, Instance 1");
                case 2: return Fragment_Compose.newInstance("ThirdFragment, Instance 1");
                default: return Fragment_Inbox.newInstance("FourthFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

    }

    private  void confirmSelectLocation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to search & select where you to meet this friend for VaCay?");
        builder.setMessage("You can search the location and take capture of the location on the map.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),LocationCaptureActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                if(Commons._golf_activity){
                    Intent intent=new Intent(this,GolfActivity.class);
                    startActivity(intent);
                }else if(Commons._run_activity){
                    Intent intent=new Intent(this,RunningActivity.class);
                    startActivity(intent);
                }else if(Commons._ski_activity){
                    Intent intent=new Intent(this,SkiingSnowboardingActivity.class);
                    startActivity(intent);
                }else if(Commons._tennis_activity){
                    Intent intent=new Intent(this,TennisActivity.class);
                    startActivity(intent);
                }else if(Commons._video_activity) {
                    Intent intent=new Intent(this,VideoDisplayTestActivity.class);
                    startActivity(intent);
                }
                else {
                    if(Commons._request_set){
                        Intent intent = null;
                        Commons._request_set=false;
                        if(Commons._gender_flag==1) intent=new Intent(getApplicationContext(),MenBeautyServiceActivity.class);
                        else if(Commons._gender_flag==0)intent=new Intent(getApplicationContext(), BeautyServiceEntryActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    }else {
                        Intent intent = new Intent(this, MeetFriendActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }

                }
                Commons.speechMessage="";
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
        }
    }
    private void openMenuItems() {
        View view = findViewById(R.id.attachbutton);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.attach_menu);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();

    }
    public void fromGallery(MenuItem menuItem) {

//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto(MenuItem menuItem) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        }
//        else if (type == MEDIA_TYPE_VIDEO) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                    + "VID_" + timeStamp + ".mp4");
//        }
        else {
            return null;
        }

        return mediaFile;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        permissionHelper.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap=bitmap;

                        imagePortion.setVisibility(View.VISIBLE);
                        image.setImageBitmap(bitmap);
                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();
                        Commons.destination=new File(_photoPath);
                        Commons.imageUrl=_photoPath;    Log.d("PHOTOPATH===",_photoPath.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();   Log.d("PHOTOURL===",_imageCaptureUri.toString());
                }

            case Constants.PICK_FROM_CAMERA: {
                try {

                    permissionHelper.verifyPermission(
                            new String[]{"take picture"},
                            new String[]{Manifest.permission.CAMERA},
                            new PermissionCallback() {
                                @Override
                                public void permissionGranted() {
                                    //action to perform when permission granteed
                                }

                                @Override
                                public void permissionRefused() {
                                    //action to perform when permission refused
                                }
                            }
                    );

                    try{
                        onCaptureImageResult(data);
                        return;
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    _imageCaptureUri = data.getData();

//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);  Log.d("PHOTOPATH0000===",_photoPath.toString());
            //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);


                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


                    intent.putExtra("crop", true);
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                    intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
//                    intent.putExtra("outputX", 800);
//                    intent.putExtra("outputY", 800);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("noFaceDetection", true);
//                    intent.putExtra("return-data", true);
                    intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                    startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void onCaptureImageResult(Intent data) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "Pictures");
        if (!dir.exists())
            dir.mkdirs();

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        Commons.destination = new File(Environment.getExternalStorageDirectory()+"/Pictures",
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            Commons.destination.createNewFile();
            fo = new FileOutputStream(Commons.destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imagePortion.setVisibility(View.VISIBLE);
        image.setImageBitmap(thumbnail);
    }


    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        permissionHelper.finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(Commons._golf_activity){
            Intent intent=new Intent(this,GolfActivity.class);
            startActivity(intent);
        }else if(Commons._run_activity){
            Intent intent=new Intent(this,RunningActivity.class);
            startActivity(intent);
        }else if(Commons._ski_activity){
            Intent intent=new Intent(this,SkiingSnowboardingActivity.class);
            startActivity(intent);
        }else if(Commons._tennis_activity){
            Intent intent=new Intent(this,TennisActivity.class);
            startActivity(intent);
        }else if(Commons._video_activity) {
            Intent intent=new Intent(this,VideoDisplayTestActivity.class);
            startActivity(intent);
        }
        else {
            if(Commons._request_set){
                Intent intent = null;
                Commons._request_set=false;
                if(Commons._gender_flag==1) intent=new Intent(getApplicationContext(),MenBeautyServiceActivity.class);
                else if(Commons._gender_flag==0)intent=new Intent(getApplicationContext(), BeautyServiceEntryActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }else {
                Intent intent = new Intent(this, MeetFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

        }
        Commons.speechMessage="";
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }
}

































