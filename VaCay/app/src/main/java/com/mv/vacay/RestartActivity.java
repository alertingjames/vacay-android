package com.mv.vacay;

/**
 * Created by a on 2016.11.22.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.mv.vacay.base.CommonActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.SplashActivity;

/**
 * Created by a on 6/14/2016.
 */
public class RestartActivity extends CommonActivity implements View.OnClickListener{

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(!Commons.g_isAppRunning){

            Intent goIntro = new Intent(this, SplashActivity.class);
            startActivity(goIntro);
        }

        finish();
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
    }

    @Override
    public void onClick(View view){

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){

        return true;
    }
}

