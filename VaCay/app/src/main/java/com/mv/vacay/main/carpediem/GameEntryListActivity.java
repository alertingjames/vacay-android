package com.mv.vacay.main.carpediem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.GameListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.GameEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class GameEntryListActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    ImageView back;
    TextView allVideos;
    LinearLayout livegame,entertainment;
    EditText ui_edtsearch;
    ListView listView;
    private AdView mAdView;
    SwipyRefreshLayout ui_RefreshLayout;
    GameListAdapter gameListAdapter=new GameListAdapter(this);
    ArrayList<GameEntity> _datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_entry_list);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        allVideos=(TextView)findViewById(R.id.button_allvideo);
        allVideos.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_game);

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

        getAllGames();

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
                    if(_datas.isEmpty()) showToast("No games");
                    gameListAdapter.filterName(text);
                    gameListAdapter.notifyDataSetChanged();
                }else  {
                    gameListAdapter.setGameDatas(_datas);
                    listView.setAdapter(gameListAdapter);
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
        Commons._is_game=true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Commons._is_game=false;
                Intent intent=new Intent(getApplicationContext(), SelectCarpeDiemActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.button_allvideo:
                intent=new Intent(getApplicationContext(), VideoPlayListActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                break;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void getAllGames() {

        _datas.clear();

        String url = ReqConst.SERVER_URL + "getVideo";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetBeautiesResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {

                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetBeautiesResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("VIDEOresponse===>",response.toString());

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray videoInfo = response.getJSONArray("video_infos");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("VIDEOs===",videoInfo.toString());

                for (int i = 0; i < videoInfo.length(); i++) {

                    JSONObject jsonVideo = (JSONObject) videoInfo.get(i);

                    GameEntity video = new GameEntity();

                    video.set_idx(jsonVideo.getInt("vid"));

                    if(jsonVideo.getString("vFileLink").length()>0) {

                        video.setVideoId(jsonVideo.getString("vFileLink"));
                        video.setGameThumbnailUrl(jsonVideo.getString("vFileThumbnail"));
                    }

                    else {
                        video.setVideoId(jsonVideo.getString("vIdentification"));
                        video.setGameThumbnailUrl(jsonVideo.getString("vThumbnail"));
                    }


                    Log.d("VideoID===>",video.getVideoId());

                    video.setGameName(jsonVideo.getString("vName"));
                    video.setVideoType(jsonVideo.getString("vType"));
                    video.setVideoTypeSecondary(jsonVideo.getString("vOtherType"));
                    video.setTeamName(jsonVideo.getString("vTeamName"));
                    video.setChannel(jsonVideo.getString("vChannelName"));
                    video.setSalePrice(jsonVideo.getString("vPrice"));
                    video.setSpecials(jsonVideo.getString("vSpecial")); Log.d("Special==>",video.getSpecials());
//                    video.setGameThumbnailUrl(jsonVideo.getString("vThumbnail"));
                    video.setBarName(jsonVideo.getString("barName"));
                    video.setBarType(jsonVideo.getString("barType"));
                    video.setKnownName(jsonVideo.getString("barPlaceName"));
                    video.setRentPrice(jsonVideo.getString("barRentPrice"));
                    video.setWatchWithPrice(jsonVideo.getString("barMeetPrice"));
                    if(video.getSpecials().length()>0){
                        try{
                            String duration=video.getSpecials().substring(0,8).trim();
                            video.setDuaration(duration);
                        }catch (StringIndexOutOfBoundsException e){}
                    }

                    String menus = jsonVideo.getString("barMenuList");

                    JSONObject menuObj = new JSONObject(menus);
                    Log.d("menuObj===>",menuObj.toString());

                    ArrayList<String> arrayList=new ArrayList<>();
                    for(int k=0;k<menus.length();k++) {
                        arrayList.add(menuObj.getString("menuA"));
                        arrayList.add(menuObj.getString("menuB"));
                        arrayList.add(menuObj.getString("menuC"));
                        arrayList.add(menuObj.getString("menuD"));
                        arrayList.add(menuObj.getString("menuE"));
                        arrayList.add(menuObj.getString("menuF"));
                        arrayList.add(menuObj.getString("menuG"));
                        arrayList.add(menuObj.getString("menuH"));
                        arrayList.add(menuObj.getString("menuI"));
                        arrayList.add(menuObj.getString("menuJ"));

                    }
                    video.setResMenus(arrayList);
                    if(video.getVideoType().toLowerCase().equals("game")) {
                        _datas.add(video);    Log.d("Game ID===>",video.getVideoId());
                    }

                }

                if(_datas.isEmpty()) showToast("No videos");
                Commons.gameEntities.clear();
                Commons.gameEntities.addAll(_datas);

                gameListAdapter.setGameDatas(_datas);
                listView.setAdapter(gameListAdapter);

            }
            else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
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
}
