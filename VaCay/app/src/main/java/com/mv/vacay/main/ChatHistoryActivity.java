package com.mv.vacay.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.adapter.ChatUserListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.database.DBManager;
import com.mv.vacay.models.UserEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class ChatHistoryActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private DBManager dbManager;
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    LinearLayout toggle;
    TextView title, toggleButton;
    String email="", sender="", name="", photo="", message="";
    SwipyRefreshLayout ui_RefreshLayout;
    com.mv.vacay.main.meetfriends.MeetFriendActivity context;
    ArrayList<UserEntity> _datas=new ArrayList<>();
    ChatUserListAdapter chatUserListAdapter=new ChatUserListAdapter(this);

    boolean recent_flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        TextView title2=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title2.setTypeface(font);


        dbManager = new DBManager(this);
        dbManager.open();

        _datas.clear();
        _datas.addAll(Commons.userEntities);

        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_friends);

        ImageView speechButton=(ImageView)findViewById(R.id.search_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });
        ImageView delete=(ImageView)findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui_edtsearch.setText("");
            }
        });

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        title=(TextView)findViewById(R.id.title);
        title.setText("Messaged Contacts!");

        toggleButton=(TextView)findViewById(R.id.toggleButton);
        toggleButton.setText("Recent");

        toggle=(LinearLayout)findViewById(R.id.toggle);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recent_flag){
                    recent_flag=false;
                    toggleButton.setText("Recent");
                    title.setText("Messaged Contacts!");
                    if(Commons.userEntities.isEmpty())
                        showToast("No messaged contact...");
                    chatUserListAdapter.setDatas(Commons.userEntities);
                    chatUserListAdapter.notifyDataSetChanged();
                    listView.setAdapter(chatUserListAdapter);

                }else {
                    recent_flag=true;
                    toggleButton.setText("Messaged");
                    title.setText("Recent Contacts!");
                    if(dbManager.getAllMembers().isEmpty())
                        showToast("No recent contact...");
                    chatUserListAdapter.setDatas(dbManager.getAllMembers());
                    chatUserListAdapter.notifyDataSetChanged();
                    listView.setAdapter(chatUserListAdapter);
                }
            }
        });
//        chatUserListAdapter.setDatas(_datas);
//        listView.setAdapter(chatUserListAdapter);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    chatUserListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    chatUserListAdapter.setDatas(_datas);
                    listView.setAdapter(chatUserListAdapter);
                }

            }
        });

        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {

                if(!recent_flag && Commons.userEntities.isEmpty()){
                    recent_flag=true;
                    title.setText("Recent Contacts!");
                    toggleButton.setText("Messaged");
                    _datas.clear();
                    _datas.addAll(dbManager.getAllMembers());
                    if(recent_flag && _datas.isEmpty()){
                        finish();
                    }
                }

                chatUserListAdapter.setDatas(_datas);
                chatUserListAdapter.notifyDataSetChanged();
                listView.setAdapter(chatUserListAdapter);
            }

        });


    }

    public void clearRecentUsers(){
        for(int i=0;i<dbManager.getAllMembers().size();i++){
            dbManager.delete(dbManager.getAllMembers().get(i).get_idx());
        }
//                getAllUsers();
        Commons.userEntities.clear();
        Commons.userEntities.addAll(dbManager.getAllMembers());
        if(Commons.userEntities.isEmpty())finish();
        chatUserListAdapter.setDatas(Commons.userEntities);
        chatUserListAdapter.notifyDataSetChanged();
        listView.setAdapter(chatUserListAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            showToast("Sorry! Your device doesn\'t support speech input");
        }catch (NullPointerException a) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            ui_edtsearch.setText(matches.get(0));

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.left_in,R.anim.right_out);
    }


    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //       getAllUsers();
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
}

