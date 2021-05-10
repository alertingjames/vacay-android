package com.mv.vacay.main.provider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

public class GoogleSurveyActivity extends AppCompatActivity {

    String adminEmail="", surveyLink="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_googel_survey);

        adminEmail=getIntent().getStringExtra("adminEmail");
        surveyLink=getIntent().getStringExtra("surveyLink");

        TextView email=(TextView)findViewById(R.id.adminemail);
        email.setText(adminEmail);
        final TextView imageView=(TextView)findViewById(R.id.surveyButton);
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        //    ImageView imageView = (ImageView) v.findViewById(R.id.imv_likedislike);
                        //overlay is black with transparency of 0x77 (119)
                        imageView.setBackgroundResource(R.drawable.surveymonkeyicon2);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        imageView.setBackgroundResource(R.drawable.surveymonkeyicon);

                        respondToSurvey();

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        imageView.getBackground().clearColorFilter();
                        imageView.invalidate();
                        break;
                    }
                }

                return true;
            }
        });
    }
    private void respondToSurvey() {
        String url = surveyLink.replace("?usp=sf_link","?embedded=true");
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
