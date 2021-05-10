package com.mv.vacay.main.watercooler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.adapter.CommentListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.models.CommentEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends BaseActivity implements AppBarLayout.OnOffsetChangedListener {

    private static final int PERCENTAGE_TO_ANIMATE_AVATAR = 10;
    private boolean mIsAvatarShown = true;

    private CircularImageView image;
    private CircularNetworkImageView imageNet;
    private TextView category, category2, name, content;
    private int mMaxScrollSize;
    private TextView comment;
    private ImageView backdrop, arrow;
    private LinearLayout layout;
    private FloatingActionButton commentButton;
    private ListView list;
//    private RecyclerView list;
    ArrayList<CommentEntity> entities=new ArrayList<>();
    CommentListAdapter commentListAdapter=new CommentListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.materialup_appbar);

//        list=(ListView)findViewById(R.id.materialup_list);
        list=(ListView)findViewById(R.id.materialup_list);

        image=(CircularImageView) findViewById(R.id.image);
        imageNet=(CircularNetworkImageView) findViewById(R.id.imageNet);
        backdrop=(ImageView)findViewById(R.id.backdrop);
        arrow=(ImageView)findViewById(R.id.arrow);
        layout=(LinearLayout)findViewById(R.id.materialup_title_container);
        commentButton=(FloatingActionButton)findViewById(R.id.commentbutton);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),AddCommentActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        });
        if(Commons.waterCoolerEntity.getProfilePhotoUrl().length()>1000) {
            imageNet.setVisibility(View.GONE);
            image.setImageBitmap(base64ToBitmap(Commons.waterCoolerEntity.getProfilePhotoUrl()));
        }else {
            imageNet.setVisibility(View.VISIBLE);
            imageNet.setImageUrl(Commons.waterCoolerEntity.getProfilePhotoUrl(), VaCayApplication.getInstance()._imageLoader);
        }

        category=(TextView)findViewById(R.id.category);
        category2=(TextView)findViewById(R.id.category2);
        name=(TextView)findViewById(R.id.name);
        content=(TextView)findViewById(R.id.content);

        category.setText(Commons.waterCoolerEntity.getCategory());
        name.setText(Commons.waterCoolerEntity.getUserName());
        content.setText(Commons.waterCoolerEntity.getContent());

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        name.setTypeface(font);

        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        category.setTypeface(font);
        category2.setTypeface(font);

        comment=(TextView)findViewById(R.id.comment);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        comment.setTypeface(font);

        appbarLayout.addOnOffsetChangedListener(this);
        mMaxScrollSize = appbarLayout.getTotalScrollRange();

        getComments(Commons.waterCoolerEntity.getIdx());

    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        ImageView pen=(ImageView)findViewById(R.id.penicon);
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;

            backdrop.animate()
                    .alpha(0.3f)
                    .setDuration(10)
                    .start();
            category.setVisibility(View.INVISIBLE);
            image.animate()
                    .scaleY(0.6f).scaleX(0.6f)
                    .setDuration(200)
                    .start();
            imageNet.animate()
                    .scaleY(0.6f).scaleX(0.6f)
                    .setDuration(200)
                    .start();
            category2.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            category2.setText(Commons.waterCoolerEntity.getCategory());
            content.animate()
                    .alpha(0.9f)
                    .setDuration(200)
                    .start();
            pen.setBackgroundResource(R.drawable.pencilicon);
            pen.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            layout.setBackground(getDrawable(R.drawable.praiseimage));
            arrow.animate().rotation(180.0f).setDuration(300).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            image.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            imageNet.animate()
                    .scaleY(1).scaleX(1)
                    .start();
            backdrop.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            category.setVisibility(View.VISIBLE);
            category.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            category2.animate()
                    .alpha(0.0f)
                    .setDuration(200)
                    .start();
            content.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .start();
            pen.animate()
                    .alpha(0.0f)
                    .setDuration(200)
                    .start();
            layout.setBackground(null);
            layout.setBackgroundColor(Color.parseColor("#2d47dc"));
            arrow.animate().rotation(360.0f).setDuration(300).start();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getComments(final String info_id) {

        String url = ReqConst.SERVER_URL + "get_comment";

        showProgress();

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

                params.put("info_id", info_id);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseGetMessagesResponse(String json) {

        closeProgress();

        try {
            entities.clear();

            JSONObject response = new JSONObject(json);   Log.d("WCresponse====", response.toString());

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===", String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                JSONArray comments = response.getJSONArray("data");
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);

                Log.d("watercoolers===",comments.toString());

                for (int i = 0; i < comments.length(); i++) {

                    JSONObject comment = (JSONObject) comments.get(i);

                    CommentEntity commentEntity=new CommentEntity();

                    commentEntity.setIdx(comment.getString("id"));
                    commentEntity.setPhotoUrl(comment.getString("photoUrl"));
                    commentEntity.setInfo_id(comment.getString("info_id"));
                    commentEntity.setName(comment.getString("name"));
                    commentEntity.setText(comment.getString("text"));
                    commentEntity.setImageUrl(comment.getString("imageUrl"));

                    entities.add(commentEntity);
                }
                if(entities.isEmpty())
                    showToast("No Comments ...");
                commentListAdapter.setDatas(entities);
                commentListAdapter.notifyDataSetChanged();
                list.setAdapter(commentListAdapter);
            }
            else {

                showToast("Server Error...");
            }

        } catch (JSONException e) {

            showToast("Server Error...");

            e.printStackTrace();
        }
    }

}























