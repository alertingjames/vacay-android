package com.mv.vacay.main.inbox;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.mv.vacay.fragments.Fragment_Compose;
import com.mv.vacay.fragments.Fragment_Inbox;
import com.mv.vacay.fragments.Fragment_SentMessages;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.location.LocationCaptureActivity;
import com.mv.vacay.main.meetfriends.MeetFriendActivity;
import com.mv.vacay.main.provider.BroadmoorActivity;
import com.mv.vacay.main.provider.CompanyManagerActivity;
import com.mv.vacay.main.provider.ProviderHomeActivity;
import com.mv.vacay.main.provider.SProviderHomeActivity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pl.tajchert.nammu.PermissionCallback;

public class InboxActivity extends AppCompatActivity implements View.OnClickListener{

    private final int REQ_CODE_SPEECH_INPUT = 100;

    CircularNetworkImageView friend_photo_net;
    CircularImageView friend_photo;
    TextView friend_name, friend_email,pwContent,signout,payHistory,ok;
    ImageView attachbutton,back,location,searchUserButton;
    ImageView image;
    Button speechButton;
    private Uri _imageCaptureUri;
    String _photoPath = "", pwd="" ;
    Bitmap bitmap;
    ImageLoader _imageLoader;
    File file=null;
    boolean _takePhoto=false;
    private AdView mAdView;
    FrameLayout inboxframe;
    LinearLayout note,linearLayout,imagePortion, profilePortion, welcomePortion;
    TextView okay,no,noticontent;
    private ListView resultList;
    Intent intent;

    PermissionHelper permissionHelper;
    Bitmap bm=null;
    File destination=null;
    boolean is_speech=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        //    Preference.getInstance().put(getApplicationContext(), PrefConst.PREFKEY_MESSAGEPWD, "");

        permissionHelper = PermissionHelper.getInstance(this);

        pwd = Preference.getInstance().getValue(this, PrefConst.PREFKEY_MESSAGEPWD, "");  Log.d("EXISTPW===>",pwd);
        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        linearLayout=(LinearLayout)findViewById(R.id.lyt_container);
        imagePortion=(LinearLayout)findViewById(R.id.imagePortion);

        noticontent=(TextView)findViewById(R.id.noticontent);

        payHistory=(TextView)findViewById(R.id.payHistory);
        if(Commons._is_provider)
    //        payHistory.setVisibility(View.VISIBLE);
        payHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        signout=(TextView)findViewById(R.id.signout);
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preference.getInstance().put(getApplicationContext(), PrefConst.PREFKEY_MESSAGEPWD, "");
                showToast("You signed out.");
                if(Commons._is_admin){
                    intent=new Intent(getApplicationContext(),ProviderHomeActivity.class);
                }
                else intent=new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });
        pwContent=(EditText)findViewById(R.id.pwcontent);

        resultList = (ListView) findViewById(R.id.list);
        speechButton=(Button)findViewById(R.id.speechbutton);

        // Disable button if no recognition service is present

        PackageManager pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(

                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

        if (activities.size() == 0) {

            speechButton.setEnabled(false);

            Toast.makeText(getApplicationContext(), "Recognizer Not Found",Toast.LENGTH_LONG).show();

        }
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_speech=true;
                startVoiceRecognitionActivity();
            }
        });
        inboxframe=(FrameLayout)findViewById(R.id.inboxframe);
        note=(LinearLayout)findViewById(R.id.note);
        okay=(TextView)findViewById(R.id.okay);
        no=(TextView)findViewById(R.id.no);
        linearLayout.setVisibility(View.GONE);
        if(!Commons._newMessage){
            noticontent.setText("No new messages!\n   Do you want to send a message to anyone?");
            noticontent.setTextColor(Color.BLACK);
            if(pwd.length()==0)noticontent.setText("No new messages!\n   Please input your new password.");
        }else {
            noticontent.setText("WELCOME! NEW MESSAGE TO YOU\n   Please check your inbox.");
            noticontent.setTextColor(Color.MAGENTA);
            if(pwd.length()==0)noticontent.setText("WELCOME! NEW MESSAGE TO YOU\n   Please input your new password.");
        }

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pwContent.getText().length()>0)login();
                else showToast("Please input your password.");
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

    //    confirmSelectFriend();
        friend_photo_net=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);
        friend_photo=(CircularImageView) findViewById(R.id.imv_photo);
        friend_name=(TextView)findViewById(R.id.txv_name);
        friend_email=(TextView)findViewById(R.id.txv_email);
        profilePortion = (LinearLayout)findViewById(R.id.profilePortion);
        welcomePortion = (LinearLayout)findViewById(R.id.welcomePortion);

        if(Commons._inboxUserSearch){
            linearLayout.setVisibility(View.VISIBLE);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            inboxframe.setAnimation(animation1);
            inboxframe.setVisibility(View.GONE);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translateoff);
            note.setAnimation(animation2);
            note.setVisibility(View.GONE);
            profilePortion.setVisibility(View.VISIBLE);
            welcomePortion.setVisibility(View.GONE);

            try {
                if (Commons.userEntity.get_photoUrl().length() < 1000) {
                    friend_photo_net.setVisibility(View.VISIBLE);
                    friend_photo_net.setImageUrl(Commons.userEntity.get_photoUrl(), _imageLoader);
                } else {
                    friend_photo_net.setVisibility(View.GONE);
                    friend_photo.setImageBitmap(base64ToBitmap(Commons.userEntity.get_photoUrl()));
                } Log.d("USERIMAGE===",String.valueOf(Commons.userEntity.get_imageRes()));
            }catch (NullPointerException e){}

            try {
                if (!Commons.userEntity.get_name().equals(""))
                    friend_name.setText(Commons.userEntity.get_name());
                else friend_name.setText(Commons.userEntity.get_fullName()); Log.d("USERNAME===",friend_name.getText().toString());
            }catch (NullPointerException e){}
            try {
                friend_email.setText(Commons.userEntity.get_email());
            }catch (NullPointerException e){}
            if(!Commons._search_selected){
                profilePortion.setVisibility(View.GONE);
                welcomePortion.setVisibility(View.VISIBLE);
            }
        }
        else {
            inboxframe.setVisibility(View.VISIBLE);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
            inboxframe.setAnimation(animation1);
            note.setVisibility(View.VISIBLE);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translateup);
            note.setAnimation(animation2);
            profilePortion.setVisibility(View.GONE);
            welcomePortion.setVisibility(View.VISIBLE);
        }

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

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

        image=(ImageView) findViewById(R.id.image);
        ok=(TextView)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePortion.setVisibility(View.GONE);
            }
        });

        searchUserButton=(ImageView)findViewById(R.id.touserbutton);
        searchUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._inboxUserSearch=true;
                Intent intent=new Intent(getApplicationContext(),SelectRecieverActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(pager);
        if(Commons._is_composeDateBack) pager.setCurrentItem(2);
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
    private  void confirmSelectFriend() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Click \"ok\" to select this VaCay");

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent intent=new Intent(getActivity(), NotificationCompat.MessagingStyle.Message.class);
//                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(), MeetFriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
                Commons._inboxUserSearch=false;
                if(Commons._home_to_inbox){
                    Commons._home_to_inbox=false;
                    Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._proManager_to_inbox){
                    Commons._proManager_to_inbox=false;
                    Intent intent=new Intent(getApplicationContext(),ProviderHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._broadmoor_to_inbox){
                    Commons._broadmoor_to_inbox=false;
                    Intent intent=new Intent(getApplicationContext(),BroadmoorActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._company_to_inbox){
                    Commons._company_to_inbox=false;
                    Intent intent=new Intent(getApplicationContext(),CompanyManagerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._provider_to_inbox){
                    Commons._provider_to_inbox=false;
                    Intent intent=new Intent(getApplicationContext(),SProviderHomeActivity.class);
                    intent.putExtra("proid",Commons.sProviderIntentEntity.getProid());
                    intent.putExtra("adminid",Commons.sProviderIntentEntity.getAdminid());
                    intent.putExtra("first",Commons.sProviderIntentEntity.getFirstName());
                    intent.putExtra("last",Commons.sProviderIntentEntity.getLastName());
                    intent.putExtra("full",Commons.sProviderIntentEntity.getFullName());
                    intent.putExtra("image",Commons.sProviderIntentEntity.getImage());
                    intent.putExtra("email",Commons.sProviderIntentEntity.getEmail());
                    intent.putExtra("city",Commons.sProviderIntentEntity.getCity());
                    intent.putExtra("company",Commons.sProviderIntentEntity.getCompany());
                    intent.putExtra("address",Commons.sProviderIntentEntity.getAddress());
                    intent.putExtra("password",Commons.sProviderIntentEntity.getPwd());
                    intent.putExtra("accountid",Commons.sProviderIntentEntity.getToken());
                    intent.putExtra("verified",Commons.sProviderIntentEntity.getVerified());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
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
                if(!is_speech){

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


//                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
                        //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        _imageCaptureUri = data.getData();

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

                }
                break;
            }
        }

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            if(is_speech){
                ArrayList<String> matches = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                resultList.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, matches));

                for(int i=0;i<matches.size();i++) {
                    if (matches.get(i).equals(pwd)) {
                        pwContent.setText(matches.get(i));

                    }else pwContent.setText(matches.get(0));
                }
                is_speech=false;
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


    private void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, new Long(2000));
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000);
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 20000);

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,

                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");

//        startActivityForResult(intent, REQUEST_CODE);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }

    }
    public void login(){
        Log.d("LoginData===>",pwd+"//////"+pwContent.getText().toString());
        if(pwd.length()>0){
            Log.d("Compare===>",String.valueOf(pwContent.getText().equals(pwd)));
            if(pwContent.getText().toString().equals(pwd)){
                linearLayout.setVisibility(View.VISIBLE);
                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                inboxframe.setAnimation(animation1);
                inboxframe.setVisibility(View.GONE);
                Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translateoff);
                note.setAnimation(animation2);
                note.setVisibility(View.GONE);
            }else {
                pwContent.setText("");
                showToast("Your password is incorrect. Please reinput your password.");
            }
        }
        else {
            Preference.getInstance().put(this,
                    PrefConst.PREFKEY_MESSAGEPWD, pwContent.getText().toString());
            showToast("Your password registered successfully.");
            linearLayout.setVisibility(View.VISIBLE);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
            inboxframe.setAnimation(animation1);
            inboxframe.setVisibility(View.GONE);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translateoff);
            note.setAnimation(animation2);
            note.setVisibility(View.GONE);
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
}

