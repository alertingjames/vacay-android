package com.mv.vacay.main.manager;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.mv.vacay.R;
import com.mv.vacay.adapter.MainActivityListAdapter;
import com.mv.vacay.api.MakeJsonObjectRequest;
import com.mv.vacay.api.VolleyResponseListner;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.config.EndPoints;
import com.mv.vacay.config.JsonKeys;
import com.mv.vacay.main.movies.VideoDetail;
import com.mv.vacay.models.VideoData;
import com.mv.vacay.provider.SuggestionProvider;
import com.mv.vacay.utils.EndlessRecyclerOnScrollListner;
import com.mv.vacay.utils.NetworkChangeReceiver;
import com.mv.vacay.utils.RecyclerItemClickListener;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VideoListActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    ProgressDialog progressDialog;
    private MainActivityListAdapter adapter;
    String nextPageToken = null;
    private String query = null;
    EditText ui_edtsearch;
    LinearLayout movies,news,books;
    static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    ListView listView;
    private int count = 0;
    ImageView back,search;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    Button retryButton;
    IntentFilter filter = new IntentFilter(ACTION);
    SwipyRefreshLayout ui_RefreshLayout;
    static NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private List<VideoData> videoList = new ArrayList<>();
    public final String TAG = com.mv.vacay.main.carpediem.EntertainmentEntryActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        adapter = new MainActivityListAdapter(getApplicationContext(), videoList);
        nextPageToken = null;

        this.registerReceiver(networkChangeReceiver, filter);

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

        recyclerView = (RecyclerView)findViewById(R.id.searchRecycleList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MainActivityListAdapter(getApplicationContext(), videoList);
        recyclerView.setAdapter(adapter);
        handleIntent(getIntent());

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                VideoData intentData = videoList.get(position);
                Intent videoDetailsIntent = new Intent(getApplicationContext(), VideoDetail.class);
                videoDetailsIntent.putExtra("ob", intentData);
                startActivity(videoDetailsIntent);
            }
        }));

        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListner(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "onLoadMore: " + current_page);
                getData();
            }
        });
        adapter = new MainActivityListAdapter(getApplicationContext(), videoList);
        recyclerView.setAdapter(adapter);
        getData();

        listView=(ListView)findViewById(R.id.list_entertainment);
//        listView.setAdapter((ListAdapter) adapter);
        back=(ImageView)findViewById(R.id.back) ;
        back.setOnClickListener(this);
//        search=(ImageView)findViewById(R.id.search_button);
//        search.setOnClickListener(this);
        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);

//        handleIntent(getIntent());

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

                }else  {

                }

            }
        });

    }

    private void getData() {
        progressDialog = ProgressDialog.show(this, "Loading Awesomeness", "Please Wait" , true);
        String URL = EndPoints.POPULARVIDEO_URL;
        if(nextPageToken != null) {
            URL = URL + "&pageToken=" + nextPageToken;
        }
        MakeJsonObjectRequest.call(getApplicationContext(), Request.Method.GET, URL, null, new VolleyResponseListner() {
            @Override
            public void onError(String message) {
                Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = (JSONObject) response;
                    nextPageToken = jsonObject.getString(JsonKeys.NEXT_PAGE_TOKEN);
                    JSONArray items = jsonObject.getJSONArray(JsonKeys.ITEMS);
                    parseData(items);
                } catch (JSONException e) {
                    // TODO: 9/3/16 Handle Error
                    handleError();
                    Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void mainRetry(View view) {
        recyclerView.setVisibility(View.VISIBLE);
        getData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(networkChangeReceiver, filter);
//        count++;
//        Log.i(TAG, "onResume: " + count);
//        if(videoList.size() == 0 && count > 1) {
//            Toast.makeText(getApplicationContext(), "Resume", Toast.LENGTH_LONG).show();
//            getData(query, null);
//        }

        count++;
        Log.i(TAG, "onResume: " + count);
        if(videoList.size() == 0 && count > 1) {
            getData();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        adapter.removeAll();
        adapter.notifyDataSetChanged();
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            nextPageToken = null;
            query = intent.getStringExtra(SearchManager.QUERY); Log.d("QUERY==>",query);
            getSupportActionBar().setTitle(query);
            recyclerView.setOnScrollListener(null);
            setScrollListener();
            getData(query, nextPageToken);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    public void getData(String q, String nextToken) {
//        progressDialog = ProgressDialog.show(this, "Loading Awesomeness", "Please Wait" , true);
        String URL = EndPoints.SEARCH_VIDEO_URL;
        if(nextToken != null) {
            URL = URL + "&pageToken=" + nextToken;
        }
        URL = URL + "&q=" + q;  Log.d("URL===>",URL);

        MakeJsonObjectRequest.call(getApplicationContext(), Request.Method.GET, URL, null, new VolleyResponseListner() {
            @Override
            public void onError(String message) {
                Log.e(TAG, "onError: " + message);
                // TODO: 9/3/16 Handle Error
                Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = (JSONObject) response;
                    nextPageToken = jsonObject.getString(JsonKeys.NEXT_PAGE_TOKEN);
                    JSONArray items = jsonObject.getJSONArray(JsonKeys.ITEMS);
                    getVideoIds(items);
                } catch (JSONException e) {
                    handleError();
                    Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void getVideoIds(JSONArray items) {
        List<String> videoIdList= new ArrayList<String>();
        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject idObject = items.getJSONObject(i).getJSONObject(JsonKeys.ID);
                if(idObject.getString(JsonKeys.KIND).equals(JsonKeys.KIND_VIDEO)) {
                    String videoId = idObject.getString(JsonKeys.VIDEO_ID);
                    videoIdList.add(videoId);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                // TODO: 9/3/16 Handle Error
                Toast.makeText(getApplicationContext(), "Some Error", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }
        String videoIdsForDetail = TextUtils.join(",", videoIdList);
        getVideoDetails(videoIdsForDetail);
    }
    public void getVideoDetails(String Ids) {
        String  URL =  EndPoints.VIDEO_DETAILS_URL + "&id=" + Ids;

        MakeJsonObjectRequest.call(getApplicationContext(), Request.Method.GET, URL, null, new VolleyResponseListner() {
            @Override
            public void onError(String message) {
                Log.e(TAG, "onError: " + message);
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(Object response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = (JSONObject) response;
                    JSONArray itemsArray = jsonObject.getJSONArray(JsonKeys.ITEMS);
                    parseData(itemsArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void parseData(JSONArray items) {
//        videoList.clear();
        List<VideoData> tempVideoList = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            VideoData video = new VideoData();
            try {
                JSONObject itemObject = items.getJSONObject(i); Log.d("VideoList===>",itemObject.toString());
                video.setVideoId(itemObject.getString(JsonKeys.ID));
                JSONObject snippet = itemObject.getJSONObject(JsonKeys.SNIPPET);
                video.setChannelTitle(snippet.getString(JsonKeys.CHANNEL_TITLE));
                video.setPublishedAt(snippet.getString(JsonKeys.PUBLISHED_AT));
                video.setChannelId(snippet.getString(JsonKeys.CHANNL_ID));
                video.setVideoTitle(snippet.getString(JsonKeys.VIDEO_TITLE));
                video.setDescription(snippet.getString(JsonKeys.DESCRIPTION));
                JSONObject thumbnails = snippet.getJSONObject(JsonKeys.THUMBNAILS);
                video.setSmallThumbnail(thumbnails.getJSONObject(JsonKeys.DEFAULT_THUMBNAIL).getString(JsonKeys.URL));
                video.setMediumThumbnail(thumbnails.getJSONObject(JsonKeys.MEDIUM_THUMBNAIL).getString(JsonKeys.URL));
                video.setLargeThumbnail(thumbnails.getJSONObject(JsonKeys.HIGH_THUMBNAIL).getString(JsonKeys.URL));
                JSONObject contentDetails = itemObject.getJSONObject(JsonKeys.CONTENT_DETAILS);
                video.setDuration(contentDetails.getString(JsonKeys.DURATION));
                JSONObject statistics = itemObject.getJSONObject(JsonKeys.STATISTICS);
                video.setViewCount(statistics.getString(JsonKeys.VIEW_COUNT));
                video.setLikeCount(statistics.getString(JsonKeys.LIKE_COUNT));
                video.setDislikeCount(statistics.getString(JsonKeys.DISLIKE_COUNT));
                video.setFavouriteCount(statistics.getString(JsonKeys.FAVORITE_COUNT));
                video.setCommentCount(statistics.getString(JsonKeys.COMMENT_COUNT));
                videoList.add(video);
                tempVideoList.add(video);

            } catch (JSONException e) {
                // TODO: 9/3/16 Handle Error
                e.printStackTrace();
            }
        }
        adapter.addAll(tempVideoList);
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                Intent intent=new Intent(this,VendorUploadActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
                break;
            case R.id.search_button:

                break;
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {

    }

    public void setScrollListener() {
        recyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListner(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.i(TAG, "onLoadMore: " + current_page);
                getData(query, nextPageToken);
            }
        });
    }
    private void handleError() {
        recyclerView.setVisibility(View.GONE);
        retryButton.setVisibility(View.VISIBLE);
    }
    public void searchRetry(View view) {
        recyclerView.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);
        getData(query, null);
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
            videoList.clear();
            String text=ui_edtsearch.getText().toString().trim();
            getData(text,nextPageToken);
            adapter.notifyDataSetChanged();

        }
    }
}
