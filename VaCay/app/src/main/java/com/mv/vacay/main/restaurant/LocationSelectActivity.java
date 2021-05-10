package com.mv.vacay.main.restaurant;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mv.vacay.R;


public class LocationSelectActivity extends AppCompatActivity implements View.OnClickListener{
    GridView gridView;
    ImageView back,san,newy,chica,denv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationselect);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        TextView santext=(TextView)findViewById(R.id.santext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        santext.setTypeface(font);

        TextView newtext=(TextView)findViewById(R.id.newtext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        newtext.setTypeface(font);

        TextView chitext=(TextView)findViewById(R.id.chitext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        chitext.setTypeface(font);

        TextView dentext=(TextView)findViewById(R.id.dentext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        dentext.setTypeface(font);

        TextView austintext=(TextView)findViewById(R.id.austintext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        austintext.setTypeface(font);

        TextView londontext=(TextView)findViewById(R.id.londontext);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        londontext.setTypeface(font);

        san=(ImageView)findViewById(R.id.imv_sanfrancisco);
        san.setOnClickListener(this);
        newy=(ImageView)findViewById(R.id.imv_newyork);
        newy.setOnClickListener(this);
        chica=(ImageView)findViewById(R.id.imv_chicaco);
        chica.setOnClickListener(this);
        denv=(ImageView)findViewById(R.id.imv_denver);
        denv.setOnClickListener(this);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent=new Intent(this,FoodEntryActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
                break;
            case R.id.imv_sanfrancisco:
                intent=new Intent(this,SanFranciscoRestaurantMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.imv_newyork:
                intent=new Intent(this,NewYorkRestaurantMenuActivity.class);//don't sure
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.imv_chicaco:
                intent=new Intent(this,ChicagoRestaurantMenuActivity.class);//don't sure
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
            case R.id.imv_denver:
                intent=new Intent(this,DenverRestaurantMenuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
                break;
        }
    }
}
