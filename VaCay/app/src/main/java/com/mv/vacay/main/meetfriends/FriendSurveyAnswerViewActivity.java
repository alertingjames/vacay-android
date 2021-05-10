package com.mv.vacay.main.meetfriends;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

public class FriendSurveyAnswerViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_survey_view);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        TextView textView=(TextView)findViewById(R.id.answer_survey);
        textView.setText("* "+Commons.userEntity.get_survey_quest().replace("\n","\n* "));
        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
