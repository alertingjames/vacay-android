package com.mv.vacay.main.carpediem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.CheckinListAdapter0;
import com.mv.vacay.classes.Notification;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config.Config;
import com.mv.vacay.config_mapping.Mapping;
import com.mv.vacay.main.meetfriends.ActionProfileActivity;
import com.mv.vacay.main.sendbird.Helper;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.MyVideoView;
import com.mv.vacay.utils.TextureVideoView;
//import com.sendbird.android.AdminMessage;
//import com.sendbird.android.BaseChannel;
//import com.sendbird.android.BaseMessage;
//import com.sendbird.android.FileMessage;
//import com.sendbird.android.OpenChannel;
//import com.sendbird.android.OpenChannelListQuery;
//import com.sendbird.android.PreviousMessageListQuery;
//import com.sendbird.android.SendBird;
//import com.sendbird.android.SendBirdException;
//import com.sendbird.android.User;
//import com.sendbird.android.UserListQuery;
//import com.sendbird.android.UserMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple YouTube Android API demo application which shows how to create a simple application that
 * displays a YouTube Video in a {@link YouTubePlayerView}.
 * <p>
 * Note, to use a {@link YouTubePlayerView}, your activity must extend {@link YouTubeBaseActivity}.
 */
public class VideoDisplayTestActivity extends FragmentActivity implements View.OnClickListener, YouTubePlayer.OnInitializedListener {

//    private OpenChannelListQuery channelListQuery;
//    private SendBirdParticipantListFragment mSendBirdParticipantListFragment;
//    private SendBirdBlockedUserListFragment mSendBirdBlockedUserListFragment;

    android.widget.PopupMenu popupMenu;

    private static final int REQ_START_STANDALONE_PLAYER = 1;

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    ImageView back,barImage,cancel,cancelcheckin;
    TextView videoName,barName,spinCheckedin,
            checkinnote,allCancel,allCancel0,allCancel1;
    ListView watchingList,checkinList;
    LinearLayout watchingPeopleList,checkinPeopleList,youtubeFragment;
    ImageLoader _imageLoader;
    private YouTubePlayer player;
    boolean _watching_people=false,_checkin=false;
    ArrayList<UserEntity> _datas_checkin=new ArrayList<>();
    ArrayList<UserEntity> _datas_watching=new ArrayList<>();
    private ProgressDialog _progressDlg;
    AdView mAdView;
    File file;
    int _idx;
    private TextureVideoView mTextureVideoView;
    private MyVideoView myVideoView;
    RelativeLayout ui_lytvideo;
    static String string="";
    int year, month, day,hour,minute;
    CheckinListAdapter0 checkinListAdapter=new CheckinListAdapter0(this);

    public static String sUserId;
    private String mNickname;
    private String mChannelUrl;
//    private SendBirdChatFragment mSendBirdChatFragment;

    final Handler mHandler = new Handler();
    Timer mTimer = new Timer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_display_test);

//        SendBird.init(Config.appId, this);

        mNickname=Commons.thisEntity.get_fullName();
        sUserId=Commons.thisEntity.get_email();

        Commons._video_activity=true;

        mTimer.schedule(doAsynchronousTask, 0, 10000);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        Commons.year=year;
        Commons.month=month;
        Commons.day=day;
        Commons.hour=hour;
        Commons.min=minute;

        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Config.API_KEY, this);

        _imageLoader = VaCayApplication.getInstance().getImageLoader();

        loadLayout();

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
    }

//    private void initFragment() {
//        mSendBirdChatFragment = new SendBirdChatFragment();
//        Bundle args = new Bundle();
//        args.putString("channel_url", mChannelUrl);
//        mSendBirdChatFragment.setArguments(args);
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, mSendBirdChatFragment)
//                .commit();
//    }
//
//    private void initFragment_participant() {
//        mSendBirdParticipantListFragment = new SendBirdParticipantListFragment();
//        Bundle args = new Bundle();
//        args.putString("channel_url", mChannelUrl);
//        mSendBirdParticipantListFragment.setArguments(args);
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container_participant, mSendBirdParticipantListFragment)
//                .commit();
//        ((LinearLayout)findViewById(R.id.lyt_container)).setVisibility(View.VISIBLE);
//        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
//        ((LinearLayout)findViewById(R.id.lyt_container)).startAnimation(animation1);
//        cancel=(ImageView) findViewById(R.id.cancelbutton);
//        cancel.setVisibility(View.VISIBLE);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.space);
//                ((LinearLayout)findViewById(R.id.lyt_container)).startAnimation(animation1);
//                ((LinearLayout)findViewById(R.id.lyt_container)).setVisibility(View.GONE);
//                cancel.setVisibility(View.GONE);
//            }
//        });
//    }

//    private void initFragment_blocked() {
////        mSendBirdBlockedUserListFragment = new SendBirdBlockedUserListFragment();
//        Bundle args = new Bundle();
//        args.putString("channel_url", mChannelUrl);
////        mSendBirdBlockedUserListFragment.setArguments(args);
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container_participant, mSendBirdBlockedUserListFragment)
//                .commit();
//        ((LinearLayout)findViewById(R.id.lyt_container)).setVisibility(View.VISIBLE);
//        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.move);
//        ((LinearLayout)findViewById(R.id.lyt_container)).startAnimation(animation1);
//        cancel=(ImageView) findViewById(R.id.cancelbutton);
//        cancel.setVisibility(View.VISIBLE);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.space);
//                ((LinearLayout)findViewById(R.id.lyt_container)).startAnimation(animation1);
//                ((LinearLayout)findViewById(R.id.lyt_container)).setVisibility(View.GONE);
//                cancel.setVisibility(View.GONE);
//            }
//        });
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Helper.MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityResumedForOldAndroids();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * If the minimum SDK version you support is under Android 4.0,
         * you MUST uncomment the below code to receive push notifications.
         */
//        SendBird.notifyActivityPausedForOldAndroids();
    }

    private void loadLayout() {

        youtubeFragment=(LinearLayout)findViewById(R.id.lyt_youtube);
        allCancel=(TextView)findViewById(R.id.button_cancel);
        allCancel.setOnClickListener(this);

        cancelcheckin=(ImageView)findViewById(R.id.cancelcheckin);
        cancelcheckin.setOnClickListener(this);

        allCancel0=(TextView)findViewById(R.id.button_cancel0);
        allCancel0.setOnClickListener(this);

        barImage=(ImageView)findViewById(R.id.bar_image);
        if(Commons.gameEntity.getBarType().trim().equals("Restaurant"))
            barImage.setImageResource(R.drawable.reestaurant);
        else if(Commons.gameEntity.getBarType().trim().equals("Bar"))
            barImage.setImageResource(R.drawable.beer);
        else if(Commons.gameEntity.getBarType().trim().equals("Park"))
            barImage.setImageResource(R.drawable.ppark);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(this);

        ((ImageView)findViewById(R.id.btn_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuItems();
            }
        });

        videoName=(TextView)findViewById(R.id.title);
        videoName.setText(Commons.gameEntity.getGameName());
        barName=(TextView)findViewById(R.id.barname);
        barName.setText(Commons.gameEntity.getBarName());
        spinCheckedin=(TextView)findViewById(R.id.checkin);
        spinCheckedin.setOnClickListener(this);

        ui_lytvideo=(RelativeLayout)findViewById(R.id.lytvideo);
        mTextureVideoView = (TextureVideoView) findViewById(R.id.cropTextureView);

        VideoView myVideoView=(VideoView)findViewById(R.id.myvideo);
        ImageView handler=(ImageView)findViewById(R.id.scrollhandler);
        final LinearLayout upstairs=(LinearLayout)findViewById(R.id.lyt_uptairs);
        final LinearLayout resName=(LinearLayout)findViewById(R.id.resname);
        final ViewGroup titleBar=(ViewGroup) findViewById(R.id.titlebar);
        final LinearLayout lytTitleBar=(LinearLayout) findViewById(R.id.titlebar);
        handler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upstairs.setVisibility(View.GONE);
                upstairs.removeView(resName);
                titleBar.addView(resName,2);
                resName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        upstairs.setVisibility(View.VISIBLE);
                        titleBar.removeView(resName);
                        upstairs.addView(resName,0);
                    }
                });
            }
        });
 //       myVideoView.setVideoSize(ui_lytvideo.getWidth(),ui_lytvideo.getHeight());

        //    Log.d("VIDEOID===>",Commons.gameEntity.getGameThumbnailUrl().substring(1,8));
        if(Commons.gameEntity.getVideoId().endsWith(".mp4")||Commons.gameEntity.getVideoId().endsWith(".gp3")){

            youtubeFragment.setVisibility(View.GONE);
            ui_lytvideo.setVisibility(View.VISIBLE);

//            mTextureVideoView.setScaleType(TextureVideoView.ScaleType.CENTER_CROP);
//            mTextureVideoView.setDataSource(Commons.gameEntity.getVideoId());
//            mTextureVideoView.setLooping(true);
//            mTextureVideoView.play();

            myVideoView.setBackground(null);
            myVideoView.setMediaController(new MediaController(this));
            myVideoView.setVideoPath(Commons.gameEntity.getVideoId());
            myVideoView.requestFocus();
            myVideoView.start();

            myVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });

        }else {
            ui_lytvideo.setVisibility(View.GONE);
            youtubeFragment.setVisibility(View.VISIBLE);
        }

        watchingList=(ListView)findViewById(R.id.list_people_watching);
        checkinList=(ListView)findViewById(R.id.list_checkin);
        watchingPeopleList=(LinearLayout)findViewById(R.id.lyt_listPeopleWatching);
        checkinPeopleList=(LinearLayout)findViewById(R.id.lyt_listCheckin);

//        connect();
    }


    TimerTask doAsynchronousTask = new TimerTask() {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if(Commons._mappingGetFlag) new Notification(getApplicationContext(),Commons.thisEntity.get_email());
                //                    getMessage(email);
                }
            });
        }
    };

    @Override
    public void finish() {
//        mSendBirdChatFragment.exitChannel();

        super.finish();
        overridePendingTransition(R.anim.sendbird_slide_in_from_top, R.anim.sendbird_slide_out_to_bottom);
    }


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player,
                                        boolean wasRestored) {
        this.player = player;

        if (!wasRestored) {

            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            if(!Commons.gameEntity.getVideoId().substring(1,7).equals("storage")){

                player.cueVideo(Commons.gameEntity.getVideoId());
            }

            //        player.cueVideo(Commons.gameEntity.getVideoId());

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                    // player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }

                @Override
                public void onLoaded(String s) {
                    player.play();
                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    player.play();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                    showToast(errorReason.toString());
                }
            });
            //        player.play();
            //    player.cueVideo("4mu4Fz-I5sw");
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        //    return (YouTubePlayerView) findViewById(R.id.youtube_view);
        return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
    }

    public abstract class YouTubeFailureRecoveryActivity extends YouTubeBaseActivity implements
            YouTubePlayer.OnInitializedListener {

        private static final int RECOVERY_DIALOG_REQUEST = 1;

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                            YouTubeInitializationResult errorReason) {
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
            } else {
                String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == RECOVERY_DIALOG_REQUEST) {
                // Retry initialization if user performed a recovery action
                getYouTubePlayerProvider().initialize(Config.API_KEY, this);
            }
        }
        protected abstract YouTubePlayer.Provider getYouTubePlayerProvider();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void openMenuItems() {
        View view = findViewById(R.id.btn_settings);
//        PopupMenu popup = new PopupMenu(this, view);
//        getMenuInflater().inflate(R.menu.attach_menu, popup.getMenu());
        popupMenu = new android.widget.PopupMenu(this, view);
        popupMenu.inflate(R.menu.participant_menu);
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

//    public void participantsView(MenuItem menuItem){
//        initFragment_participant();
//    }
//
//    public void blockedUsersView(MenuItem menuItem){
//        initFragment_blocked();
//    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.back:
                Commons._video_activity=false;

                Intent in=new Intent(this,GameDetailActivity.class);
                startActivity(in);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);

//                Commons.now_watching_message="end/"+Commons.now_watching_message;
//                sendMsg(Commons.gameEntity.getVideoId().toLowerCase());

                break;
            case R.id.button_cancel:

                Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_off);
                checkinPeopleList.startAnimation(animation1);
                _checkin=false;
                spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                        0,// left
                        0,//top
                        R.drawable.ic_drop,// right
                        0//bottom
                );
//                frameLayout.setVisibility(View.GONE);
                checkinPeopleList.setVisibility(View.GONE);
                allCancel.setVisibility(View.GONE);

                break;
            case R.id.cancelcheckin:

                animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_off);
                checkinPeopleList.startAnimation(animation1);
                _checkin=false;
                spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                        0,// left
                        0,//top
                        R.drawable.ic_drop,// right
                        0//bottom
                );
//                frameLayout.setVisibility(View.GONE);
                checkinPeopleList.setVisibility(View.GONE);
                cancelcheckin.setVisibility(View.GONE);

                break;
            case R.id.button_cancel0:

                break;

            case R.id.checkin:

                if (!_checkin) {
                    spinCheckedin.setActivated(true);
                    _checkin=true;
                    if(_watching_people){
                        checkinnote.setVisibility(View.VISIBLE);
                    }
                    spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop0,// right
                            0//bottom
                    );

                    checkIn();
                    checkinPeopleList.setVisibility(View.VISIBLE);
                    cancelcheckin.setVisibility(View.VISIBLE);
                    animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.custom);
                    checkinPeopleList.startAnimation(animation1);

                } else {

                    animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
                    checkinPeopleList.startAnimation(animation1);
                    checkinPeopleList.setVisibility(View.GONE);
                    cancelcheckin.setVisibility(View.GONE);
                    _checkin=false;
                    spinCheckedin.setCompoundDrawablesWithIntrinsicBounds(
                            0,// left
                            0,//top
                            R.drawable.ic_drop,// right
                            0//bottom
                    );
                }

                break;

        }
    }
    public void checkIn() {

        _datas_checkin.clear();
        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETALLUSERS;

//        String params = String.format("/%d", _curpage);
//        url += params;

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {
                parseGetUsersResponse(json);
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseGetUsersResponse(String json) {

        closeProgress();
        String[] surveyone={"",getString(R.string.questa)+"\n"};
        String[] surveytwo={"",getString(R.string.questb)+"\n"};
        String[] surveythree={"",getString(R.string.questc)+"\n"};
        String[] surveyfour={"",getString(R.string.questdd)+"\n"};
        String[] surveyfive={"",getString(R.string.queste)+"\n"};

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSE===",response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            if(result_code == ReqConst.CODE_SUCCESS){

                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("USERS===",users.toString());

                double[] lats={39.5530507,39.7380517,39.5542507,39.5501507};
                double[] lngs={-105.78306,-104.99506,-105.78706,-105.78406};
                String[] publicNames={"Colorado","denver","colorado","colorado"};
                String[] relationships={"In Relationship","In Relationship","Single","In Relationship"};

                for (int i = 0; i < users.length(); i++) {

                    JSONObject jsonUser = (JSONObject) users.get(i);

                    UserEntity user = new UserEntity();

                    user.set_idx(jsonUser.getInt(ReqConst.RES_USERID)); Log.d("USERID===",String.valueOf(user.get_idx()));
                    user.set_firstName(jsonUser.getString(ReqConst.RES_FIRSTNAME));
                    user.set_lastName(jsonUser.getString(ReqConst.RES_LASTNAME).replace("-","."));
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int birthyear=Integer.parseInt(jsonUser.getString(ReqConst.RES_AGE));
                    int age=year-birthyear;
                    user.set_age_range(String.valueOf(age));
                    user.set_city(jsonUser.getString(ReqConst.RES_ADDRESS));
                    user.set_job(jsonUser.getString(ReqConst.RES_JOB));
                    user.set_education(jsonUser.getString(ReqConst.RES_EDUCATION));
                    user.set_interest(jsonUser.getString(ReqConst.RES_INTERESTS).replace("-",","));
                    user.set_photoUrl(jsonUser.getString(ReqConst.RES_PHOTOURL));
                    user.set_email(jsonUser.getString(ReqConst.RES_EMAIL));
                    user.set_survey_quest(surveyone[jsonUser.getInt(ReqConst.RES_SURVEYONE)]
                            +surveytwo[jsonUser.getInt(ReqConst.RES_SURVEYTWO)]
                            +surveythree[jsonUser.getInt(ReqConst.RES_SURVEYTHREE)]
                            +surveyfour[jsonUser.getInt(ReqConst.RES_SURVEYFOUR)]
                            +surveyfive[jsonUser.getInt(ReqConst.RES_SURVEYFIVE)]);

                    user.set_relations(jsonUser.getString("relationship"));
                    user.set_publicName(jsonUser.getString("place_name").replace("-",",").replace("%20"," "));
                    user.set_userlng(Double.parseDouble(jsonUser.getString("user_lon").replace("%20","-").replace("-",".")));
                    user.set_userlat(Double.parseDouble(jsonUser.getString("user_lat").replace("%20","-").replace("-",".")));

                    // except me
//                    if (user.get_idx() == Commons.thisEntity.get_idx())
//                        continue;

                    String firstLetter = user.get_fullName().substring(0, 1).toUpperCase();
//                    if (_curIndex.length() == 0 || firstLetter.compareToIgnoreCase(_curIndex) > 0) {
//                        _users.add(firstLetter);
//                        _curIndex = firstLetter;
//                    }
                    try{
                        if(Commons.gameEntity.getKnownName().toLowerCase().trim().equals(user.get_publicName().toLowerCase().trim())) _datas_checkin.add(0,user);
                        Log.d("Datas===",_datas_checkin.toString());

                    }catch (NullPointerException e){}
                }

                if(_datas_checkin.isEmpty()) showToast("No people checked in");

                checkinListAdapter.setUserDatas(_datas_checkin);
                checkinList.setAdapter(checkinListAdapter);

            } else {
                showToast(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showToast(getString(R.string.error));
        }
    }

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", this.getString(R.string.loading),true);
    }

    public void closeProgress() {

        if(_progressDlg == null) {
            return;
        }

        if(_progressDlg!=null && _progressDlg.isShowing()){
            _progressDlg.dismiss();
            _progressDlg = null;
        }
    }
    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

//    private void connect() {
//        SendBird.connect(sUserId, new SendBird.ConnectHandler() {
//            @Override
//            public void onConnected(User user, SendBirdException e) {
//                if (e != null) {
//                    showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                    return;
//                }
//
//                String nickname = mNickname;
//
//                SendBird.updateCurrentUserInfo(nickname, null, new SendBird.UserInfoUpdateHandler() {
//                    @Override
//                    public void onUpdated(SendBirdException e) {
//                        if (e != null) {
//                            showToast(e.getCode() + ":" + e.getMessage()+"\n"+"Disconnected so try again later");
//                            //    setState(State.DISCONNECTED);
//                            return;
//                        }
//
////                        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
////                        editor.putString("user_id", sUserId);
////                        editor.putString("nickname", mNickname);
////                        editor.commit();
//
//                        showToast("Successfully connected.");
//                        searchChannels();
//                        //    setState(State.CONNECTED);
//                    }
//                });
//
////                if (FirebaseInstanceId.getInstance().getToken() == null) return;
//
//            }
//        });
//
//        //    setState(State.CONNECTING);
//    }
//
//    private void disconnect() {
//        SendBird.disconnect(new SendBird.DisconnectHandler() {
//            @Override
//            public void onDisconnected() {
//                //            setState(State.DISCONNECTED);
//            }
//        });
//    }
//
//    private void searchChannels() {
//
//        String keyword="";
//        if(Commons.gameEntity.getVideoId().contains("http://"))keyword=Commons.gameEntity.getVideoId().substring(47,Commons.gameEntity.getVideoId().length()-4);
//        else keyword=Commons.gameEntity.getVideoId();
//
//        Log.d("KeyWord===>",keyword);
//
//        channelListQuery = OpenChannel.createOpenChannelListQuery();
//        channelListQuery.setNameKeyword(keyword);
//
//        channelListQuery.next(new OpenChannelListQuery.OpenChannelListQueryResultHandler() {
//            @Override
//            public void onResult(List<OpenChannel> channels, SendBirdException e) {
//                if (e != null) {
//                    // Error!
//                    showToast("No this video channel.");
//                    return;
//                }
//                // Returns a List of channels that have "NameKeyword" in their names.
//                mChannelUrl=channels.get(0).getUrl();
//                if (mChannelUrl == null || mChannelUrl.length() <= 0) {
//                    finish();
//                    return;
//                }
//                initFragment();
//            }
//        });
//    }

//    public static class SendBirdChatFragment extends Fragment {
//        private static final int REQUEST_PICK_IMAGE = 100;
//        private static final String identifier = "SendBirdOpenChat";
//
//        private ListView mListView;
//        private SendBirdChatAdapter mAdapter;
//        private EditText mEtxtMessage;
//        private Button mBtnSend;
//        private ImageButton mBtnUpload;
//        private ProgressBar mProgressBtnUpload;
//        private String mChannelUrl;
//        private OpenChannel mOpenChannel;
//        private PreviousMessageListQuery mPrevMessageListQuery;
//        private boolean mIsUploading;
//
//        public SendBirdChatFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.sendbird_fragment_open_chat, container, false);
//
//            mChannelUrl = getArguments().getString("channel_url");
//
//            initUIComponents(rootView);
//
//            enterChannel(mChannelUrl);
//
//            return rootView;
//        }
//
//        @Override
//        public void onPause() {
//            super.onPause();
//            if (!mIsUploading) {
//                SendBird.removeChannelHandler(identifier);
//            }
//        }
//
//        @Override
//        public void onResume() {
//            super.onResume();
//            if (!mIsUploading) {
//                SendBird.addChannelHandler(identifier, new SendBird.ChannelHandler() {
//                    @Override
//                    public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
//                        if (baseChannel.getUrl().equals(mChannelUrl)) {
//                            mAdapter.appendMessage(baseMessage);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onMessageDeleted(BaseChannel channel, long msgId) {
//                        if (channel.getUrl().equals(mChannelUrl)) {
//                            boolean deleteMsg = false;
//
//                            for (int i = 0; i < mAdapter.getCount(); i++) {
//                                BaseMessage msg = (BaseMessage) mAdapter.getItem(i);
//                                if (msg.getMessageId() == msgId) {
//                                    mAdapter.delete(msg);
//                                    deleteMsg = true;
//                                    break;
//                                }
//                            }
//
//                            if (deleteMsg) {
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                });
//
//                refreshChannel();
//                loadPrevMessages(true);
//            } else {
//                mIsUploading = false;
//
//                /**
//                 * Set this as true to restart auto-background detection,
//                 * when your Activity is shown again after the external Activity is finished.
//                 */
//                SendBird.setAutoBackgroundDetection(true);
//            }
//        }
//
//        @Override
//        public void onDestroy() {
//            super.onDestroy();
//        }
//
//        private void loadPrevMessages(final boolean refresh) {
//            if (mOpenChannel == null) {
//                return;
//            }
//
//            if (refresh || mPrevMessageListQuery == null) {
//                mPrevMessageListQuery = mOpenChannel.createPreviousMessageListQuery();
//            }
//
//            if (mPrevMessageListQuery.isLoading()) {
//                return;
//            }
//
//            if (!mPrevMessageListQuery.hasMore()) {
//                return;
//            }
//
//            mPrevMessageListQuery.load(30, true, new PreviousMessageListQuery.MessageListQueryResult() {
//                @Override
//                public void onResult(List<BaseMessage> list, SendBirdException e) {
//                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    if (refresh) {
//                        mAdapter.clear();
//                    }
//
//                    for (BaseMessage message : list) {
//                        mAdapter.insertMessage(message);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                    mListView.setSelection(list.size());
//                }
//            });
//        }
//
//        private void enterChannel(String channelUrl) {
//            OpenChannel.getChannel(channelUrl, new OpenChannel.OpenChannelGetHandler() {
//                @Override
//                public void onResult(final OpenChannel openChannel, SendBirdException e) {
//                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
//                        @Override
//                        public void onResult(SendBirdException e) {
//                            if (e != null) {
//                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//
//                            mOpenChannel = openChannel;
////                            ((TextView) getActivity().findViewById(R.id.txt_channel_name)).setText(openChannel.getName());
//
//                            loadPrevMessages(true);
//                        }
//                    });
//                }
//            });
//        }
//
//        private void exitChannel() {
//            if (mOpenChannel != null) {
//                mOpenChannel.exit(null);
//            }
//        }
//
//        private void refreshChannel() {
//            if (mOpenChannel != null) {
//                mOpenChannel.refresh(null);
//            }
//        }
//
//        private void initUIComponents(View rootView) {
//            mAdapter = new SendBirdChatAdapter(getActivity());
//
//            mListView = (ListView) rootView.findViewById(R.id.list);
//            turnOffListViewDecoration(mListView);
//            mListView.setAdapter(mAdapter);
//
//            mBtnSend = (Button) rootView.findViewById(R.id.btn_send);
//            mBtnUpload = (ImageButton) rootView.findViewById(R.id.btn_upload);
//            mProgressBtnUpload = (ProgressBar) rootView.findViewById(R.id.progress_btn_upload);
//            mEtxtMessage = (EditText) rootView.findViewById(R.id.etxt_message);
//
//            mBtnSend.setEnabled(false);
//            mBtnSend.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    send();
//                }
//            });
//
//            mBtnUpload.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (Helper.requestReadWriteStoragePermissions(getActivity())) {
//                        mIsUploading = true;
//
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PICK_IMAGE);
//
//                        /**
//                         * Set this as false to maintain SendBird connection,
//                         * even when an external Activity is started.
//                         */
//                        SendBird.setAutoBackgroundDetection(false);
//                    }
//                }
//            });
//
//            mEtxtMessage.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                            send();
//                        }
//                        return true; // Do not hide keyboard.
//                    }
//                    return false;
//                }
//            });
//            mEtxtMessage.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
//            mEtxtMessage.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    mBtnSend.setEnabled(s.length() > 0);
//                }
//            });
//
//            mListView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Helper.hideKeyboard(getActivity());
//                    return false;
//                }
//            });
//            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                    if (scrollState == SCROLL_STATE_IDLE) {
//                        if (view.getFirstVisiblePosition() == 0 && view.getChildCount() > 0 && view.getChildAt(0).getTop() == 0) {
//                            loadPrevMessages(false);
//                        }
//                    }
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                }
//            });
//            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("Select")
//                            .setItems(new String[]{"Delete Message", "Block User"}, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    switch (which) {
//                                        case 0:
//                                            final BaseMessage msg0 = (BaseMessage) mAdapter.getItem(position);
//                                            mOpenChannel.deleteMessage(msg0, new BaseChannel.DeleteMessageHandler() {
//                                                @Override
//                                                public void onResult(SendBirdException e) {
//                                                    if (e != null) {
//                                                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        return;
//                                                    }
//
//                                                    Toast.makeText(getActivity(), "Message deleted.", Toast.LENGTH_SHORT).show();
//                                                    // Message will be deleted at ChannelHandler.
//                                                }
//                                            });
//                                            break;
//
//                                        case 1:
//                                            BaseMessage msg1 = (BaseMessage) mAdapter.getItem(position);
//                                            User target = null;
//
//                                            if (msg1 instanceof AdminMessage) {
//                                                Toast.makeText(getActivity(), "Admin message can not be deleted.", Toast.LENGTH_SHORT).show();
//                                                return;
//                                            } else if (msg1 instanceof UserMessage) {
//                                                target = ((UserMessage) msg1).getSender();
//                                            } else if (msg1 instanceof FileMessage) {
//                                                target = ((FileMessage) msg1).getSender();
//                                            }
//
//                                            SendBird.blockUser(target, new SendBird.UserBlockHandler() {
//                                                @Override
//                                                public void onBlocked(User user, SendBirdException e) {
//                                                    if (e != null) {
//                                                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        return;
//                                                    }
//
//                                                    Toast.makeText(getActivity(), user.getNickname() + " is blocked.", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                                            break;
//                                    }
//                                }
//                            })
//                            .setNegativeButton("Cancel", null).create().show();
//
//                    return true;
//                }
//            });
//        }
//
//        private void showUploadProgress(boolean tf) {
//            if (tf) {
//                mBtnUpload.setEnabled(false);
//                mBtnUpload.setVisibility(View.INVISIBLE);
//                mProgressBtnUpload.setVisibility(View.VISIBLE);
//            } else {
//                mBtnUpload.setEnabled(true);
//                mBtnUpload.setVisibility(View.VISIBLE);
//                mProgressBtnUpload.setVisibility(View.GONE);
//            }
//        }
//
//        private void turnOffListViewDecoration(ListView listView) {
//            listView.setDivider(null);
//            listView.setDividerHeight(0);
//            listView.setHorizontalFadingEdgeEnabled(false);
//            listView.setVerticalFadingEdgeEnabled(false);
//            listView.setHorizontalScrollBarEnabled(false);
//            listView.setVerticalScrollBarEnabled(true);
//            listView.setSelector(new ColorDrawable(0x00ffffff));
//            listView.setCacheColorHint(0x00000000); // For Gingerbread scrolling bug fix
//        }
//
//        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            if (resultCode == Activity.RESULT_OK) {
//                if (requestCode == REQUEST_PICK_IMAGE && data != null && data.getData() != null) {
//                    upload(data.getData());
//                }
//            }
//        }
//
//        private void send() {
//            mOpenChannel.sendUserMessage(mEtxtMessage.getText().toString(), new BaseChannel.SendUserMessageHandler() {
//                @Override
//                public void onSent(UserMessage userMessage, SendBirdException e) {
//                    if (e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    mAdapter.appendMessage(userMessage);
//                    mAdapter.notifyDataSetChanged();
//
//                    mEtxtMessage.setText("");
//                }
//            });
//
//            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                Helper.hideKeyboard(getActivity());
//            }
//        }
//
//        private void upload(Uri uri) {
//            Hashtable<String, Object> info = Helper.getFileInfo(getActivity(), uri);
//            final String path = (String) info.get("path");
//            final File file = new File(path);
//            final String name = file.getName();
//            final String mime = (String) info.get("mime");
//            final int size = (Integer) info.get("size");
//
//            if (path == null) {
//                Toast.makeText(getActivity(), "Uploading file must be located in local storage.", Toast.LENGTH_LONG).show();
//            } else {
//                showUploadProgress(true);
//                mOpenChannel.sendFileMessage(file, name, mime, size, "", new BaseChannel.SendFileMessageHandler() {
//                    @Override
//                    public void onSent(FileMessage fileMessage, SendBirdException e) {
//                        showUploadProgress(false);
//                        if (e != null) {
//                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//
//                        mAdapter.appendMessage(fileMessage);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//    }
//
//    public static class SendBirdChatAdapter extends BaseAdapter {
//        private static final int TYPE_UNSUPPORTED = 0;
//        private static final int TYPE_USER_MESSAGE = 1;
//        private static final int TYPE_FILE_MESSAGE = 2;
//        private static final int TYPE_ADMIN_MESSAGE = 3;
//
//        private final Context mContext;
//        private final LayoutInflater mInflater;
//        private final ArrayList<Object> mItemList;
//
//        public SendBirdChatAdapter(Context context) {
//            mContext = context;
//            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            mItemList = new ArrayList<>();
//        }
//
//        public void delete(Object message) {
//            mItemList.remove(message);
//        }
//
//        @Override
//        public int getCount() {
//            return mItemList.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return mItemList.get(position);
//        }
//
//        public void clear() {
//            mItemList.clear();
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        public void insertMessage(BaseMessage message) {
//            mItemList.add(0, message);
//        }
//
//        public void appendMessage(BaseMessage message) {
//            mItemList.add(message);
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            Object item = mItemList.get(position);
//            if (item instanceof UserMessage) {
//                return TYPE_USER_MESSAGE;
//            } else if (item instanceof FileMessage) {
//                return TYPE_FILE_MESSAGE;
//            } else if (item instanceof AdminMessage) {
//                return TYPE_ADMIN_MESSAGE;
//            }
//
//            return TYPE_UNSUPPORTED;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 4;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ViewHolder viewHolder;
//            final Object item = getItem(position);
//
//            if (convertView == null || ((ViewHolder) convertView.getTag()).getViewType() != getItemViewType(position)) {
//                viewHolder = new ViewHolder();
//                viewHolder.setViewType(getItemViewType(position));
//
//                switch (getItemViewType(position)) {
//                    case TYPE_UNSUPPORTED:
//                        convertView = new View(mInflater.getContext());
//                        convertView.setTag(viewHolder);
//                        break;
//                    case TYPE_USER_MESSAGE: {
//                        TextView tv;
//
//                        convertView = mInflater.inflate(R.layout.sendbird_view_open_user_message, parent, false);
//                        tv = (TextView) convertView.findViewById(R.id.txt_message);
//                        viewHolder.setView("message", tv);
//                        convertView.setTag(viewHolder);
//                        break;
//                    }
//                    case TYPE_ADMIN_MESSAGE: {
//                        convertView = mInflater.inflate(R.layout.sendbird_view_admin_message, parent, false);
//                        viewHolder.setView("message", convertView.findViewById(R.id.txt_message));
//                        convertView.setTag(viewHolder);
//                        break;
//                    }
//                    case TYPE_FILE_MESSAGE: {
//                        TextView tv;
//
//                        convertView = mInflater.inflate(R.layout.sendbird_view_open_file_message, parent, false);
//                        tv = (TextView) convertView.findViewById(R.id.txt_sender_name);
//                        viewHolder.setView("txt_sender_name", tv);
//
//                        viewHolder.setView("img_file_container", convertView.findViewById(R.id.img_file_container));
//
//                        viewHolder.setView("image_container", convertView.findViewById(R.id.image_container));
//                        viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
//                        viewHolder.setView("txt_image_name", convertView.findViewById(R.id.txt_image_name));
//                        viewHolder.setView("txt_image_size", convertView.findViewById(R.id.txt_image_size));
//
//                        viewHolder.setView("file_container", convertView.findViewById(R.id.file_container));
//                        viewHolder.setView("txt_file_name", convertView.findViewById(R.id.txt_file_name));
//                        viewHolder.setView("txt_file_size", convertView.findViewById(R.id.txt_file_size));
//
//                        convertView.setTag(viewHolder);
//
//                        break;
//                    }
//                }
//            }
//
//            viewHolder = (ViewHolder) convertView.getTag();
//            switch (getItemViewType(position)) {
//                case TYPE_UNSUPPORTED:
//                    break;
//                case TYPE_USER_MESSAGE:
//                    final UserMessage message = (UserMessage) item;
//                    viewHolder.getView("message", TextView.class).setText(Html.fromHtml("<font color='#f002ff'><b><i>" + message.getSender().getNickname() + "</i></b></font>: " + message.getMessage()));
//                    break;
//                case TYPE_ADMIN_MESSAGE:
//                    AdminMessage adminMessage = (AdminMessage) item;
//                    viewHolder.getView("message", TextView.class).setText(Html.fromHtml(adminMessage.getMessage()));
//                    break;
//                case TYPE_FILE_MESSAGE:
//                    final FileMessage fileLink = (FileMessage) item;
//
//                    viewHolder.getView("txt_sender_name", TextView.class).setText(Html.fromHtml("<font color='#f002ff'><b><i>" + fileLink.getSender().getNickname() + "</i></b></font>: "));
//                    if (fileLink.getType().toLowerCase().startsWith("image")) {
//                        viewHolder.getView("file_container").setVisibility(View.GONE);
//
//                        viewHolder.getView("image_container").setVisibility(View.VISIBLE);
//                        viewHolder.getView("txt_image_name", TextView.class).setText(fileLink.getName());
//                        viewHolder.getView("txt_image_size", TextView.class).setText(Helper.readableFileSize(fileLink.getSize()));
//                        Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), fileLink.getUrl());
//                    } else {
//                        viewHolder.getView("image_container").setVisibility(View.GONE);
//
//                        viewHolder.getView("file_container").setVisibility(View.VISIBLE);
//                        viewHolder.getView("txt_file_name", TextView.class).setText(fileLink.getName());
//                        viewHolder.getView("txt_file_size", TextView.class).setText(Helper.readableFileSize(fileLink.getSize()));
//                    }
//                    viewHolder.getView("img_file_container").setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            new AlertDialog.Builder(mContext)
//                                    .setTitle("SendBird")
//                                    .setMessage("Do you want to download this file?")
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            try {
//                                                Helper.downloadUrl(fileLink.getUrl(), fileLink.getName(), mContext);
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    })
//                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                        }
//                    });
//                    break;
//            }
//
//            return convertView;
//        }
//
//        private class ViewHolder {
//            private Hashtable<String, View> holder = new Hashtable<>();
//            private int type;
//
//            public int getViewType() {
//                return this.type;
//            }
//
//            public void setViewType(int type) {
//                this.type = type;
//            }
//
//            public void setView(String k, View v) {
//                holder.put(k, v);
//            }
//
//            public View getView(String k) {
//                return holder.get(k);
//            }
//
//            public <T> T getView(String k, Class<T> type) {
//                return type.cast(getView(k));
//            }
//        }
//    }
//
//    public static class SendBirdParticipantListFragment extends Fragment {
//        private ListView mListView;
//        private UserListQuery mUserListQuery;
//        private SendBirdUserAdapter mAdapter;
//        private String mChannelUrl;
//
//        public SendBirdParticipantListFragment() {}
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.sendbird_fragment_participant_list, container, false);
//            initUIComponents(rootView);
//
//            mChannelUrl = getArguments().getString("channel_url");
//
//            OpenChannel.getChannel(mChannelUrl, new OpenChannel.OpenChannelGetHandler() {
//                @Override
//                public void onResult(final OpenChannel openChannel, SendBirdException e) {
//                    if(e != null) {
//                        Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    openChannel.refresh(new OpenChannel.OpenChannelRefreshHandler() {
//                        @Override
//                        public void onResult(SendBirdException e) {
//                            if (e != null) {
//                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                    //        ((TextView)getActivity().findViewById(R.id.txt_channel_name)).setText("Participants (" + openChannel.getParticipantCount() + ")");
//                            mUserListQuery = openChannel.createParticipantListQuery();
//                            loadMoreUsers();
//                        }
//                    });
//                }
//            });
//
//            return rootView;
//        }
//
//        private void initUIComponents(View rootView) {
//            mListView = (ListView)rootView.findViewById(R.id.list);
//            mAdapter = new SendBirdUserAdapter(getActivity());
//
//            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("User Block")
//                            .setMessage("Do you want to block the user?")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    SendBird.blockUser(mAdapter.getItem(position), new SendBird.UserBlockHandler() {
//                                        @Override
//                                        public void onBlocked(User user, SendBirdException e) {
//                                            if (e != null) {
//                                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//
//                                            Toast.makeText(getActivity(), user.getNickname() + " is blocked.", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            })
//                            .setNegativeButton("Cancel", null)
//                            .create().show();
//
//                    return true;
//                }
//            });
//            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
//                        loadMoreUsers();
//                    }
//                }
//            });
//
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    Commons.userEntity=(UserEntity)Mapping.userMap.get(mAdapter.getItem(i).getUserId());
//                    Intent intent=new Intent(getActivity(), ActionProfileActivity.class);
//                    startActivity(intent);
//                }
//            });
//
//            mListView.setAdapter(mAdapter);
//        }
//
//        private void loadMoreUsers() {
//            if(mUserListQuery != null && mUserListQuery.hasNext() && !mUserListQuery.isLoading()) {
//                mUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
//                    @Override
//                    public void onResult(List<User> list, SendBirdException e) {
//                        if(e != null) {
//                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        mAdapter.addAll(list);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        public class SendBirdUserAdapter extends BaseAdapter {
//            private final Context mContext;
//            private final LayoutInflater mInflater;
//            private final ArrayList<User> mItemList;
//
//            public SendBirdUserAdapter(Context context) {
//                mContext = context;
//                mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                mItemList = new ArrayList<>();
//            }
//
//            @Override
//            public int getCount() {
//                return mItemList.size();
//            }
//
//            @Override
//            public User getItem(int position) {
//                return mItemList.get(position);
//            }
//
//            public void clear() {
//                mItemList.clear();
//            }
//
//            public User remove(int index) {
//                return mItemList.remove(index);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            public void addAll(Collection<User> users) {
//                mItemList.addAll(users);
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder viewHolder;
//
//                if(convertView == null) {
//                    viewHolder = new ViewHolder();
//
//                    convertView = mInflater.inflate(R.layout.sendbird_view_user, parent, false);
//                    viewHolder.setView("root_view", convertView);
//                    viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
//                    viewHolder.setView("txt_name", convertView.findViewById(R.id.txt_name));
//                    viewHolder.setView("txt_age", convertView.findViewById(R.id.txt_age));
//                    viewHolder.setView("txt_relationship", convertView.findViewById(R.id.txt_relationship));
//                    viewHolder.setView("txt_status", convertView.findViewById(R.id.txt_status));
//                    viewHolder.setView("chk_select", convertView.findViewById(R.id.chk_select));
//                    viewHolder.getView("chk_select", CheckBox.class).setVisibility(View.GONE);
//
//                    convertView.setTag(viewHolder);
//                }
//
//                final User item = getItem(position);
//                viewHolder = (ViewHolder) convertView.getTag();
//            //    Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getProfileUrl());
//                Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), ((UserEntity)Mapping.userMap.get(item.getUserId())).get_photoUrl());
//                viewHolder.getView("txt_name", TextView.class).setText(item.getNickname());
//                Log.d("ParticipantAge===>",((UserEntity)Mapping.userMap.get(item.getUserId())).get_age_range());
//                viewHolder.getView("txt_age", TextView.class).setText(((UserEntity)Mapping.userMap.get(item.getUserId())).get_age_range());
//                viewHolder.getView("txt_relationship", TextView.class).setText(((UserEntity)Mapping.userMap.get(item.getUserId())).get_relations());
//                viewHolder.getView("txt_status", TextView.class).setText(""); // Always online
//                return convertView;
//            }
//
//            private class ViewHolder {
//                private Hashtable<String, View> holder = new Hashtable<>();
//
//                public void setView(String k, View v) {
//                    holder.put(k, v);
//                }
//
//                public View getView(String k) {
//                    return holder.get(k);
//                }
//
//                public <T> T getView(String k, Class<T> type) {
//                    return type.cast(getView(k));
//                }
//            }
//        }
//    }
//
//    public static class SendBirdBlockedUserListFragment extends Fragment {
//        private ListView mListView;
//        private UserListQuery mUserListQuery;
//        private SendBirdUserAdapter mAdapter;
//
//        public SendBirdBlockedUserListFragment() {}
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.sendbird_fragment_user_list, container, false);
//            initUIComponents(rootView);
//
//            mUserListQuery = SendBird.createBlockedUserListQuery();
//            loadMoreUsers();
//
//            return rootView;
//        }
//
//        private void initUIComponents(View rootView) {
//            mListView = (ListView)rootView.findViewById(R.id.list);
//            mAdapter = new SendBirdUserAdapter(getActivity());
//
//            mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                    new AlertDialog.Builder(getActivity())
//                            .setTitle("User Unblock")
//                            .setMessage("Do you want to unblock the user?")
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    final User target = mAdapter.getItem(position);
//                                    SendBird.unblockUser(target, new SendBird.UserUnblockHandler() {
//                                        @Override
//                                        public void onUnblocked(SendBirdException e) {
//                                            if (e != null) {
//                                                Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                return;
//                                            }
//
//                                            Toast.makeText(getActivity(), target.getNickname() + " is unblocked.", Toast.LENGTH_SHORT).show();
//                                            mAdapter.remove(position);
//                                            mAdapter.notifyDataSetChanged();
//                                        }
//                                    });
//                                }
//                            })
//                            .setNegativeButton("Cancel", null)
//                            .create().show();
//
//                    return true;
//                }
//            });
//            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView view, int scrollState) {
//                }
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                    if (firstVisibleItem + visibleItemCount >= (int) (totalItemCount * 0.8f)) {
//                        loadMoreUsers();
//                    }
//                }
//            });
//            mListView.setAdapter(mAdapter);
//        }
//
//        private void loadMoreUsers() {
//            if(mUserListQuery != null && mUserListQuery.hasNext() && !mUserListQuery.isLoading()) {
//                mUserListQuery.next(new UserListQuery.UserListQueryResultHandler() {
//                    @Override
//                    public void onResult(List<User> list, SendBirdException e) {
//                        if(e != null) {
//                            Toast.makeText(getActivity(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        mAdapter.addAll(list);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        public class SendBirdUserAdapter extends BaseAdapter {
//            private final Context mContext;
//            private final LayoutInflater mInflater;
//            private final ArrayList<User> mItemList;
//
//            public SendBirdUserAdapter(Context context) {
//                mContext = context;
//                mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                mItemList = new ArrayList<>();
//            }
//
//            @Override
//            public int getCount() {
//                return mItemList.size();
//            }
//
//            @Override
//            public User getItem(int position) {
//                return mItemList.get(position);
//            }
//
//            public void clear() {
//                mItemList.clear();
//            }
//
//            public User remove(int index) {
//                return mItemList.remove(index);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            public void addAll(Collection<User> users) {
//                mItemList.addAll(users);
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ViewHolder viewHolder;
//
//                if(convertView == null) {
//                    viewHolder = new ViewHolder();
//
//                    convertView = mInflater.inflate(R.layout.sendbird_view_user, parent, false);
//                    viewHolder.setView("root_view", convertView);
//                    viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
//                    viewHolder.setView("txt_name", convertView.findViewById(R.id.txt_name));
//                    viewHolder.setView("txt_age", convertView.findViewById(R.id.txt_age));
//                    viewHolder.setView("txt_relationship", convertView.findViewById(R.id.txt_relationship));
//                    viewHolder.setView("chk_select", convertView.findViewById(R.id.chk_select));
//                    viewHolder.setView("txt_status", convertView.findViewById(R.id.txt_status));
//                    viewHolder.getView("chk_select", CheckBox.class).setVisibility(View.GONE);
//
//                    convertView.setTag(viewHolder);
//                }
//
//                final User item = getItem(position);
//                viewHolder = (ViewHolder) convertView.getTag();
////                Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getProfileUrl());
//                Helper.displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), ((UserEntity)Mapping.userMap.get(item.getUserId())).get_photoUrl());
//                viewHolder.getView("txt_name", TextView.class).setText(item.getNickname());
//                viewHolder.getView("txt_age", TextView.class).setText(((UserEntity)Mapping.userMap.get(item.getUserId())).get_age_range());
//                viewHolder.getView("txt_relationship", TextView.class).setText(((UserEntity)Mapping.userMap.get(item.getUserId())).get_relations());
//                if(item.getConnectionStatus() == User.ConnectionStatus.ONLINE) {
//                    viewHolder.getView("txt_status", TextView.class).setText("Online");
//                } else {
//                    viewHolder.getView("txt_status", TextView.class).setText("");
//                }
//                return convertView;
//            }
//
//            private class ViewHolder {
//                private Hashtable<String, View> holder = new Hashtable<>();
//
//                public void setView(String k, View v) {
//                    holder.put(k, v);
//                }
//
//                public View getView(String k) {
//                    return holder.get(k);
//                }
//
//                public <T> T getView(String k, Class<T> type) {
//                    return type.cast(getView(k));
//                }
//            }
//        }
//    }
}
