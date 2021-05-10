package com.mv.vacay.main.beautymen;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.BookedMessageListAdapter;
import com.mv.vacay.adapter.InboxMessageAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    ArrayList<MessageEntity> _datas = new ArrayList<>(10000);
    BookedMessageListAdapter bookedMessageListAdapter=new BookedMessageListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        imvback = (ImageView) findViewById(R.id.back);
        imvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.right_out);
            }
        });
        listView = (ListView) findViewById(R.id.list_friends);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        ImageView speechButton = (ImageView) findViewById(R.id.search_button);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });
        ImageView delete = (ImageView) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui_edtsearch.setText("");
            }
        });

        ui_edtsearch = (EditText) findViewById(R.id.edt_search);
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
                    bookedMessageListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                } else {
                    bookedMessageListAdapter.setDatas(_datas);
                    listView.setAdapter(bookedMessageListAdapter);
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

        Log.d("MyEmail===>",Commons.thisEntity.get_email());

        ui_RefreshLayout.post(new Runnable() {
            @Override

            public void run() {

                getAllBookingRequests(Commons.thisEntity.get_email());
            }

        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

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
            Toast.makeText(getApplicationContext(), "Sorry! Your device doesn\'t support speech input", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException a) {

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

    public void getAllBookingRequests(final String email) {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETMAIL;

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetMessagesResponse(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetMessagesResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);

            if(result_code.equals("0")) {

                JSONArray messages = response.getJSONArray(ReqConst.RES_MESSAGECONTENT);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("MESSAGES===", messages.toString());

                for (int i = 0; i < messages.length(); i++) {

                    JSONObject jsonMessage = (JSONObject) messages.get(i);

                    MessageEntity messageEntity = new MessageEntity();
                    messageEntity.setMail_id(jsonMessage.getString("mail_id"));
                    //        messageEntity.set_idx(jsonMessage.getInt("mail_id"));
                    messageEntity.set_useremail(jsonMessage.getString("from_mail").replace("%20"," "));
                    messageEntity.setUserfullname(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_username(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_photoUrl(jsonMessage.getString("photo_url"));
                    messageEntity.set_usermessage(jsonMessage.getString("text_message"));
                    messageEntity.set_request_date(jsonMessage.getString("request_date"));
                    messageEntity.set_service(jsonMessage.getString("service"));
                    messageEntity.set_status(jsonMessage.getString("status"));
                    messageEntity.set_service_reqdate(jsonMessage.getString("service_reqdate"));
                    messageEntity.set_imageUrl(jsonMessage.getString("image_message_url"));
                    messageEntity.set_requestLatLng(new LatLng(jsonMessage.getDouble("lat_message"),jsonMessage.getDouble("lon_message")));

                    if(!messageEntity.get_service().equals("no_service"))
                        _datas.add(0,messageEntity);
                }

                if(_datas.isEmpty())
                    Toast.makeText(getApplicationContext(),"No any booking request from customers", Toast.LENGTH_SHORT).show();
                bookedMessageListAdapter.setDatas(_datas);
                bookedMessageListAdapter.notifyDataSetChanged();
                listView.setAdapter(bookedMessageListAdapter);

            }else if(result_code.equals("109")){
                Toast.makeText(getApplicationContext(),"No booking message from customers.",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),getString(R.string.error),Toast.LENGTH_SHORT).show();
        }
    }

}













































