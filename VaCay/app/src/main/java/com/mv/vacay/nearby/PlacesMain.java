package com.mv.vacay.nearby;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;

import com.mv.vacay.R;

public class PlacesMain extends FragmentActivity{

    Fragment fragAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_main);

        ImageView search = (ImageView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlacesMain.this, Search.class);
                startActivity(intent);
            }
        });

        ImageView radius = (ImageView)findViewById(R.id.radius);
        radius.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment df = new SetRadiusDialog();
                df.show(getFragmentManager(), "");
            }
        });

        fragAll = new PlacesGrid();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.places_grid, fragAll);
        ft.commit();
    }

}
