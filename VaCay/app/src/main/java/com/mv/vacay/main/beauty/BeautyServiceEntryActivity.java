package com.mv.vacay.main.beauty;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;

public class BeautyServiceEntryActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView back,hair,blowout,manicure,massage,wax,facial,makeover;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_service_entry);

        Commons.beautyCategoryId=0;

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),BeautyWelcomeActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade,R.anim.slide_up);
            }
        });
        Commons.allNewBeautyEntities.clear();
        Commons.newBeautyEntities.clear();

        hair=(ImageView)findViewById(R.id.hairset);
        hair.setOnClickListener(this);
        blowout=(ImageView)findViewById(R.id.blowoutset);
        blowout.setOnClickListener(this);
        manicure=(ImageView)findViewById(R.id.manicureset);
        manicure.setOnClickListener(this);
        massage=(ImageView)findViewById(R.id.massageset);
        massage.setOnClickListener(this);
        wax=(ImageView)findViewById(R.id.waxset);
        wax.setOnClickListener(this);
        facial=(ImageView)findViewById(R.id.facialset);
        facial.setOnClickListener(this);
        makeover=(ImageView)findViewById(R.id.makeover);
        makeover.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.hairset:
                //               showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_hair_set=true;
                Commons.beautyCategoryId=1;
                Intent intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.blowoutset:
                //               showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_blowout_set=true;
                Commons.beautyCategoryId=2;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.manicureset:
//                showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_manicure_set=true;
                Commons.beautyCategoryId=3;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.massageset:
//                showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_massage_set=true;
                Commons.beautyCategoryId=4;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.waxset:
                //           showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_wax_set=true;
                Commons.beautyCategoryId=5;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.facialset:
                //           showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_facial_set=true;
                Commons.beautyCategoryId=6;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.makeover:
                //               showToast("Sorry, we can provide you with so far better Beauty service later soon.");

                Commons._beauty_makeover_set=true;
                Commons.beautyCategoryId=7;
                intent=new Intent(this,BeautyLocationSelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;

        }
    }
}
