package com.mv.vacay.main.linkedin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mv.vacay.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LinkedinActivity extends AppCompatActivity {

    public static  final String PACKAGE = "com.mv.vacay";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkedin);

        generateHaskey();

    }

    public void generateHaskey()
    {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                ((TextView) findViewById(R.id.hashKey))
                        .setText(Base64.encodeToString(md.digest(),
                                Base64.NO_WRAP));

                Log.d("hashKey",Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
    }


    public void login(View v)
    {
        Intent intent = new Intent(LinkedinActivity.this, LoginLinkedin.class);
        startActivity(intent);
        finish();
    }
}
