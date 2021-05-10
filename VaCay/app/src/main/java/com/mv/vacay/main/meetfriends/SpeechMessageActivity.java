package com.mv.vacay.main.meetfriends;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechMessageActivity extends BaseActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_message);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);

        startVoiceRecognitionActivity();
        ImageView speechButton=(ImageView)findViewById(R.id.speech_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });
    }

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"en","ko","de","ja","fr"});

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");

        try {
            Log.d("ResultCode===>",String.valueOf(123));
            this.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showAlertDialog("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            EditText sendmessage=(EditText) findViewById(R.id.speechMessage);
            Log.d("ResultCode===>",String.valueOf(456));
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if(sendmessage.getText().length()>0)
                sendmessage.setText(sendmessage.getText().toString()+"\n"+matches.get(0));
            else sendmessage.setText(matches.get(0));
            message=sendmessage.getText().toString();

        }
    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public void showAlertDialogConfirm() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage("Will you send this message?");

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(message.length()>0) Commons.speechMessage=message;
                        SpeechMessageActivity.super.onBackPressed();
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No yet",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No but",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SpeechMessageActivity.super.onBackPressed();
                        finish();
                        overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    @Override
    public void onBackPressed() {
        showAlertDialogConfirm();
    }
}
