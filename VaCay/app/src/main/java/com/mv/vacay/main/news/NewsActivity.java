package com.mv.vacay.main.news;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.GameListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.carpediem.EntertainmentEntryActivity;
import com.mv.vacay.models.GameEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

public class NewsActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    ProgressDialog progressDialog;
    android.widget.PopupMenu popupMenu;
    EditText ui_edtsearch;
    TextView categoryButton,allCancel;
    ListView listView;
    boolean _openMenu=false;
    ImageView back,search;
    SwipyRefreshLayout ui_RefreshLayout;
    GameListAdapter gameListAdapter=new GameListAdapter(this);
    ArrayList<GameEntity> _datas=new ArrayList<>();
    ArrayList<GameEntity> _datas_cnn=new ArrayList<>();
    ArrayList<GameEntity> _datas_cnbc=new ArrayList<>();
    ArrayList<GameEntity> _datas_fox=new ArrayList<>();
    ArrayList<GameEntity> _datas_bloomberg=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        listView=(ListView)findViewById(R.id.list_entertainment);

        allCancel=(TextView)findViewById(R.id.button_cancel);
        allCancel.setOnClickListener(this);

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

//        listView.setAdapter((ListAdapter) adapter);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);
        categoryButton=(TextView)findViewById(R.id.button_news_menu);
        categoryButton.setOnClickListener(this);
        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

        getAllNews();

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
                    if(_datas.isEmpty()) showToast("No videos");

                    Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.custom);
                    ui_RefreshLayout.startAnimation(animation1);

                    gameListAdapter.filterNameOthers(text);

                }else  {
                    gameListAdapter.setGameDatas(_datas);
                    Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide);
                    ui_RefreshLayout.startAnimation(animation1);
                    listView.setAdapter(gameListAdapter);
                }

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent=new Intent(getApplicationContext(), EntertainmentEntryActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.button_news_menu:

                if (!_openMenu) {
                    categoryButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop0,// right
                            0//bottom
                    );
                    openMenuItems();
                    _openMenu = true;
                } else {
                    categoryButton.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop,// right
                            0//bottom
                    );
                    popupMenu.dismiss();
                    _openMenu = false;
                }
                break;
            case R.id.button_cancel:

                Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.slide_up);
                ui_RefreshLayout.startAnimation(animation1);
                animation1 = AnimationUtils.loadAnimation(this, R.anim.fade);
                ui_RefreshLayout.startAnimation(animation1);
                gameListAdapter.setGameDatas(_datas);
                gameListAdapter.notifyDataSetChanged();
                listView.setAdapter(gameListAdapter);
                allCancel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    private void openMenuItems() {
        View view = findViewById(R.id.button_news_menu);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.news_category);
        Object menuHelper;
        Class[] argTypes;
        try {
            Field fMenuHelper = android.widget.PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popupMenu);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
            // Possible exceptions are NoSuchMethodError and NoSuchFieldError
            //
            // In either case, an exception indicates something is wrong with the reflection code, or the
            // structure of the PopupMenu class or its dependencies has changed.
            //
            // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
            // but in the case that they do, we simply can't force icons to display, so log the error and
            // show the menu normally.

            Log.w("Error====>", "error forcing menu icons to show", e);
            popupMenu.show();
            return;
        }
        popupMenu.show();
    }

    public void dismissPopupMenu(){
        categoryButton.setCompoundDrawablesWithIntrinsicBounds(
                0,// left
                0,//top
                R.drawable.ic_drop,// right
                0//bottom
        );
        popupMenu.dismiss();
        _openMenu = false;
    }

    public void cnn(MenuItem menuItem){

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
        ui_RefreshLayout.startAnimation(animation1);
        allCancel.setVisibility(View.VISIBLE);
        if(_datas_cnn.isEmpty()) showToast("No videos");
        Commons.gameEntities.clear();
        Commons.gameEntities.addAll(_datas_cnn);

        gameListAdapter.setGameDatas(_datas_cnn);
        listView.setAdapter(gameListAdapter);

        dismissPopupMenu();
    }
    public void cnbc(MenuItem menuItem){

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
        ui_RefreshLayout.startAnimation(animation1);
        allCancel.setVisibility(View.VISIBLE);
        if(_datas_cnbc.isEmpty()) showToast("No videos");
        Commons.gameEntities.clear();
        Commons.gameEntities.addAll(_datas_cnbc);

        gameListAdapter.setGameDatas(_datas_cnbc);
        listView.setAdapter(gameListAdapter);

        dismissPopupMenu();
    }
    public void fox(MenuItem menuItem){

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
        ui_RefreshLayout.startAnimation(animation1);
        allCancel.setVisibility(View.VISIBLE);
        if(_datas_fox.isEmpty()) showToast("No videos");
        Commons.gameEntities.clear();
        Commons.gameEntities.addAll(_datas_fox);

        gameListAdapter.setGameDatas(_datas_fox);
        listView.setAdapter(gameListAdapter);

        dismissPopupMenu();
    }
    public void bloomberg(MenuItem menuItem){

        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
        ui_RefreshLayout.startAnimation(animation1);
        allCancel.setVisibility(View.VISIBLE);
        if(_datas_bloomberg.isEmpty()) showToast("No videos");
        Commons.gameEntities.clear();
        Commons.gameEntities.addAll(_datas_bloomberg);

        gameListAdapter.setGameDatas(_datas_bloomberg);
        listView.setAdapter(gameListAdapter);

        dismissPopupMenu();
    }
    public void cancel(MenuItem menuItem){
        dismissPopupMenu();
    }

    public void getAllNews() {

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
                    if(video.getVideoType().toLowerCase().equals("news")) _datas.add(video);
                    if(video.getVideoTypeSecondary().toLowerCase().equals("cnn")) _datas_cnn.add(video);
                    if(video.getVideoTypeSecondary().toLowerCase().equals("cnbc")) _datas_cnbc.add(video);
                    if(video.getVideoTypeSecondary().toLowerCase().equals("fox")) _datas_fox.add(video);
                    if(video.getVideoTypeSecondary().toLowerCase().equals("bloomberg")) _datas_bloomberg.add(video);

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
