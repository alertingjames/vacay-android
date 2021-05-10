package com.mv.vacay.main.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.HomeActivity;
import com.mv.vacay.main.MyCurrentLocationViewActivity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

public class ActionsActivity extends AppCompatActivity implements View.OnClickListener{
    FrameLayout running, tennis, golf, skiing, biking, fishing, surfing, exploring;
    ImageView back;
    private ProgressDialog _progressDlg;
    LinearLayout alertdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);

        alertdialog=(LinearLayout)findViewById(R.id.alertDialog);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        running=(FrameLayout)findViewById(R.id.running);
        running.setOnClickListener(this);
        tennis=(FrameLayout)findViewById(R.id.tennis);
        tennis.setOnClickListener(this);
        golf=(FrameLayout)findViewById(R.id.golf);
        golf.setOnClickListener(this);
        skiing=(FrameLayout)findViewById(R.id.skiing);
        skiing.setOnClickListener(this);

        biking=(FrameLayout)findViewById(R.id.biking);
        biking.setOnClickListener(this);
        fishing=(FrameLayout)findViewById(R.id.fishing);
        fishing.setOnClickListener(this);
        surfing=(FrameLayout)findViewById(R.id.surfing);
        surfing.setOnClickListener(this);
        exploring=(FrameLayout)findViewById(R.id.exploring);
        exploring.setOnClickListener(this);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        if(!Commons._isMyLocationVerified)
            showInfo();

    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.running:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._run_activity = true;
                    intent = new Intent(this, RunningActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.tennis:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._tennis_activity = true;
                    intent = new Intent(this, TennisActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.golf:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._golf_activity = true;
                    intent = new Intent(this, GolfActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.skiing:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._ski_activity = true;
                    intent = new Intent(this, SkiingSnowboardingActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.biking:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._biking_activity = true;
                    intent = new Intent(this, BikingActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.fishing:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._fishing_activity = true;
                    intent = new Intent(this, FishingActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.surfing:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._surfing_activity = true;
                    intent = new Intent(this, SurfingActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }
                break;
            case R.id.exploring:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    Commons._exploring_activity = true;
                    intent = new Intent(this, ExploringActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                }

                break;
            case R.id.back:
                if(alertdialog.getVisibility()!=View.VISIBLE) {
                    intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                }
                break;
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

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", this.getString(R.string.loading),true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            try{
                _progressDlg.dismiss();
                _progressDlg = null;
            }catch (IllegalArgumentException e){}
        }
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    private  void showInfo() {

        alertdialog.setVisibility(View.VISIBLE);
        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        alertdialog.setAnimation(animation);
        TextView alertText=(TextView)findViewById(R.id.alertText);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        alertText.setTypeface(font);
        final FrameLayout layout=(FrameLayout) findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alertdialog.getVisibility()==View.VISIBLE){
                    alertdialog.setVisibility(View.GONE);
                    Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_off);
                    alertdialog.setAnimation(animation);
                    layout.setVisibility(View.GONE);
                    layout.setAnimation(animation);
                }
            }
        });
        layout.setVisibility(View.VISIBLE);
        layout.setAnimation(animation);

        TextView okay=(TextView)findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Commons._isMyLocationVerified){
                    Intent intent=new Intent(getApplicationContext(), MyCurrentLocationViewActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(0,0);
                    Commons._isMyLocationVerified=true;
                    alertdialog.setVisibility(View.GONE);
                    Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
                    alertdialog.setAnimation(animation);
                    layout.setVisibility(View.GONE);
                    layout.setAnimation(animation);
                }
            }
        });
        TextView cancel=(TextView)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertdialog.setVisibility(View.GONE);
                Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
                alertdialog.setAnimation(animation);
                layout.setVisibility(View.GONE);
                layout.setAnimation(animation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }
}



























