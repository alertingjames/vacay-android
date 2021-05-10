package com.mv.vacay.main.meetfriends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.main.location.LocationActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.lang.reflect.Field;

public class ActionProfileActivity extends AppCompatActivity implements View.OnClickListener{
    TextView txv_firstname,txv_lastname, firstname, lastname, city, job, education, interest, agerange,answer_survey,relationship, millennial;
    TextView title;
    ImageView  location, back, match;
    CircularNetworkImageView photoNet;
    CircularImageView photo;
    LinearLayout lyt_millennial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_profile);

        ImageLoader _imageLoader = VaCayApplication.getInstance().getImageLoader();

        title=(TextView)findViewById(R.id.title);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        txv_firstname=(TextView)findViewById(R.id.txv_firstname);
        txv_firstname.setText(Commons.userEntity.get_firstName());

        txv_lastname=(TextView)findViewById(R.id.txv_lastname);
        txv_lastname.setText(Commons.userEntity.get_lastName());

        firstname=(TextView)findViewById(R.id.firstname);
        firstname.setText(Commons.userEntity.get_firstName());

        lastname=(TextView)findViewById(R.id.lastname);
        lastname.setText(Commons.userEntity.get_lastName());

        city=(TextView)findViewById(R.id.city);
        city.setText(Commons.userEntity.get_city());

        job=(TextView)findViewById(R.id.job);
        job.setText(Commons.userEntity.get_job());

        education=(TextView)findViewById(R.id.education);
        education.setText(Commons.userEntity.get_education());

        agerange=(TextView)findViewById(R.id.age_range);
        agerange.setText(Commons.userEntity.get_age_range());

        relationship=(TextView)findViewById(R.id.relations);

        if(Commons.userEntity.get_relations().contains("common")) {
            TextView deptitle=(TextView)findViewById(R.id.deptitle);
            deptitle.setText("Info:");
            Commons.userEntity.set_relations(Commons.userEntity.get_relations().replace("common", ""));
        }

        relationship.setText(Commons.userEntity.get_relations());

        lyt_millennial=(LinearLayout)findViewById(R.id.lyt_millennial);
        millennial=(TextView)findViewById(R.id.millennial);
        if(Commons.userEntity.getMillennial().length() > 0){
            lyt_millennial.setVisibility(View.VISIBLE);
            millennial.setText(Commons.userEntity.getMillennial());
        }else {
            lyt_millennial.setVisibility(View.GONE);
        }

        answer_survey=(TextView)findViewById(R.id.answer_survey);
        answer_survey.setText(Commons.userEntity.get_survey_quest());
        answer_survey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),FriendSurveyAnswerViewActivity.class);
                startActivity(intent);
            }
        });

        interest=(TextView)findViewById(R.id.interest);
        interest.setText(Commons.userEntity.get_interest()); Log.d("Interest===>",Commons.userEntity.get_interest());
        Log.d("InterestAABB===>",interest.getText().toString());

        photoNet=(CircularNetworkImageView) findViewById(R.id.imv_photo_net);
        photo=(CircularImageView) findViewById(R.id.imv_photo);

        if (Commons.userEntity.get_photoUrl().length() <1000) {
            photoNet.setVisibility(View.VISIBLE);
            photoNet.setImageUrl(Commons.userEntity.get_photoUrl(), _imageLoader);
        }
        else{
            photoNet.setVisibility(View.GONE);
            photo.setImageBitmap(base64ToBitmap(Commons.userEntity.get_photoUrl()));
        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.photoUrl=Commons.userEntity.get_photoUrl();
                Commons.resId=Commons.userEntity.get_imageRes();
                Intent intent=new Intent(getApplicationContext(), ViewActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });


        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        location=(ImageView) findViewById(R.id.location);
        location.setOnClickListener(this);

        match=(ImageView) findViewById(R.id.match);
        match.setOnClickListener(this);
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
            case R.id.location:
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.toast_view, null);
                Toast toast=new Toast(this);
                toast.setView(dialogView);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.show();

                Intent intent=new Intent(this,LocationActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
            case R.id.match:
//                confirmSelectFriend();
                openMenuItems();
                break;
        }
    }

    private void openMenuItems() {
        View view = findViewById(R.id.match);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.friend_match_menu);
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

    public void messageToInbox(MenuItem menuItem){
        Intent intent=new Intent(getApplicationContext(),MatchFriendsActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }
    public void talking(MenuItem menuItem){
        Intent intent=new Intent(getApplicationContext(), ChatActivity.class);// SurveyQuestActivity
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in,R.anim.left_out);
    }

    private  void confirmSelectFriend() {

        final String[] items = {"Message via email", "Talking"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("What do you want?");

        builder.setItems(items,  new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent=new Intent(getApplicationContext(),MatchFriendsActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
                else if(item==1){

                    Intent intent=new Intent(getApplicationContext(), ChatActivity.class);// SurveyQuestActivity
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in,R.anim.left_out);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
