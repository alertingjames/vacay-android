package com.mv.vacay.main.watercooler;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.annotation.RequiresApi;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;
import com.rm.freedrawview.FreeDrawSerializableState;
import com.rm.freedrawview.FreeDrawView;
import com.rm.freedrawview.PathDrawnListener;
import com.rm.freedrawview.PathRedoUndoCountChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddCommentActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        PathRedoUndoCountChangeListener, FreeDrawView.DrawCreatorListener, PathDrawnListener {

    TextView title, category, drawButton, content;
    FrameLayout drawingPage;
    ImageView back;
    private CircularImageView image;
    private CircularNetworkImageView imageNet;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private static final int THICKNESS_STEP = 2;
    private static final int THICKNESS_MAX = 80;
    private static final int THICKNESS_MIN = 15;

    private static final int ALPHA_STEP = 1;
    private static final int ALPHA_MAX = 255;
    private static final int ALPHA_MIN = 0;

    private LinearLayout mRoot;
    private FreeDrawView mFreeDrawView;
    private View mSideView;
    private Button mBtnRandomColor, mBtnUndo, mBtnRedo, mBtnClearAll, fullscreenButton, saveButton, removeButton;
    private SeekBar mThicknessBar, mAlphaBar;
    private TextView mTxtRedoCount, mTxtUndoCount, viewControl;
    private ProgressBar mProgressBar;
    private RelativeLayout sideView;

    private ImageView mImgScreen, bitmapImage;
    private String base64String="";
    private EditText comment;
    private TextView submitButton;
    private ImageView audioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        title=(TextView)findViewById(R.id.title);
        content=(TextView)findViewById(R.id.content);
        content.setText(Commons.waterCoolerEntity.getContent());
        category=(TextView)findViewById(R.id.category);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        category.setTypeface(font);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        image=(CircularImageView) findViewById(R.id.image);
        imageNet=(CircularNetworkImageView) findViewById(R.id.imageNet);
        if(Commons.waterCoolerEntity.getProfilePhotoUrl().length()>1000) {
            imageNet.setVisibility(View.GONE);
            image.setImageBitmap(base64ToBitmap(Commons.waterCoolerEntity.getProfilePhotoUrl()));
        }else {
            imageNet.setVisibility(View.VISIBLE);
            imageNet.setImageUrl(Commons.waterCoolerEntity.getProfilePhotoUrl(), VaCayApplication.getInstance()._imageLoader);
        }

        drawingPage=(FrameLayout) findViewById(R.id.drawingPage);

        drawButton=(TextView)findViewById(R.id.drawingButton);
        drawButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        drawButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        drawButton.setBackgroundColor(Color.parseColor("#ff9602"));
                        drawingPage.setVisibility(View.VISIBLE);
                        Animation animation= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
                        drawingPage.setAnimation(animation);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        drawButton.getBackground().clearColorFilter();
                        drawButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        comment=(EditText)findViewById(R.id.comment);
        submitButton=(TextView)findViewById(R.id.submitButton);
        submitButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        submitButton.setBackgroundColor(Color.BLUE);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        submitButton.setBackground(getDrawable(R.drawable.commentbutton_background));
                        if(base64String.length()>1000 || comment.getText().length()>0)
                            composeComment();
                        else {
                            showToast("Please check your comment...");
                        }

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        submitButton.getBackground().clearColorFilter();
                        submitButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        audioButton=(ImageView)findViewById(R.id.audioButton);
        audioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startVoiceRecognitionActivity();
            }
        });

        mRoot = (LinearLayout) findViewById(R.id.root);

        mImgScreen = (ImageView) findViewById(R.id.img_screen);
        sideView=(RelativeLayout)findViewById(R.id.side_view);
        bitmapImage=(ImageView)findViewById(R.id.bitmapImage);

        mTxtRedoCount = (TextView) findViewById(R.id.txt_redo_count);
        mTxtUndoCount = (TextView) findViewById(R.id.txt_undo_count);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        mFreeDrawView = (FreeDrawView) findViewById(R.id.free_draw_view);
        mFreeDrawView.setOnPathDrawnListener(this);
        mFreeDrawView.setPathRedoUndoCountChangeListener(this);

        mSideView = findViewById(R.id.side_view);
        mBtnRandomColor = (Button) findViewById(R.id.btn_color);
        mBtnUndo = (Button) findViewById(R.id.btn_undo);
        mBtnRedo = (Button) findViewById(R.id.btn_redo);
        mBtnClearAll = (Button) findViewById(R.id.btn_clear_all);
        removeButton=(Button)findViewById(R.id.btn_remove_all);
        mAlphaBar = (SeekBar) findViewById(R.id.slider_alpha);
        mThicknessBar = (SeekBar) findViewById(R.id.slider_thickness);

        fullscreenButton = (Button) findViewById(R.id.fullscreenbutton);
        saveButton = (Button) findViewById(R.id.saveButton);
        viewControl=(TextView)findViewById(R.id.viewcontrol);
        viewControl.setOnClickListener(this);

        fullscreenButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);

        mAlphaBar.setOnSeekBarChangeListener(null);
        mThicknessBar.setOnSeekBarChangeListener(null);

        mBtnRandomColor.setOnClickListener(this);
        mBtnUndo.setOnClickListener(this);
        mBtnRedo.setOnClickListener(this);
        mBtnClearAll.setOnClickListener(this);

        if (savedInstanceState == null) {

            showLoadingSpinner();

            // Restore the previous saved state
            FileHelper.getSavedStoreFromFile(this,
                    new FileHelper.StateExtractorInterface() {
                        @Override
                        public void onStateExtracted(FreeDrawSerializableState state) {
                            if (state != null) {
                                mFreeDrawView.restoreStateFromSerializable(state);
                            }

                            hideLoadingSpinner();
                        }

                        @Override
                        public void onStateExtractionError() {
                            hideLoadingSpinner();
                        }
                    });
        }

        mAlphaBar.setMax((ALPHA_MAX - ALPHA_MIN) / ALPHA_STEP);
        int alphaProgress = ((mFreeDrawView.getPaintAlpha() - ALPHA_MIN) / ALPHA_STEP);
        mAlphaBar.setProgress(alphaProgress);
        mAlphaBar.setOnSeekBarChangeListener(this);

        mThicknessBar.setMax((THICKNESS_MAX - THICKNESS_MIN) / THICKNESS_STEP);
        int thicknessProgress = (int)
                ((mFreeDrawView.getPaintWidth() - THICKNESS_MIN) / THICKNESS_STEP);
        mThicknessBar.setProgress(thicknessProgress);
        mThicknessBar.setOnSeekBarChangeListener(this);
        mSideView.setBackgroundColor(mFreeDrawView.getPaintColor());

    }

    public void composeComment() {

        String url = ReqConst.SERVER_URL + "upload_comment";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRegisterResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showToast(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("info_id", Commons.waterCoolerEntity.getIdx());
                params.put("photo", Commons.thisEntity.get_photoUrl());
                params.put("name", Commons.thisEntity.get_name());
                params.put("text", comment.getText().toString());
                params.put("image", base64String);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRegisterResponse(String json) {

        closeProgress();
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                Toast.makeText(getApplicationContext(),"Composed successfully!",Toast.LENGTH_SHORT).show();
                comment.setText("");
                base64String="";
                bitmapImage.setVisibility(View.GONE);

            }
            else {
                showToast("Uploading failed...");
            }

        } catch (JSONException e) {
            showToast("Uploading failed...");

            e.printStackTrace();
        }

    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }
    private void showLoadingSpinner() {

        TransitionManager.beginDelayedTransition(mRoot);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        FileHelper.saveStateIntoFile(this, mFreeDrawView.getCurrentViewStateAsSerializable(), null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mSideView.setBackgroundColor(mFreeDrawView.getPaintColor());
    }

    @Override
    public void onBackPressed() {

        if (mImgScreen.getVisibility() == View.VISIBLE) {
            mImgScreen.setImageBitmap(null);
            mImgScreen.setVisibility(View.GONE);

            mFreeDrawView.setVisibility(View.VISIBLE);
            mSideView.setVisibility(View.VISIBLE);
        }
        else{
            drawingPage.setVisibility(View.GONE);
            Animation animation=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
            drawingPage.setAnimation(animation);
        }
    }

    public void startVoiceRecognitionActivity() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"en","ko","de","ja","fr"});

//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, languagePref);
//        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, languagePref);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,

                "AndroidBite Voice Recognition...");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),"Sorry! Your device doesn\'t support speech input",Toast.LENGTH_SHORT).show();
        }catch (NullPointerException a) {

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            comment.setText(matches.get(0));

        }
    }

    private void takeAndShowScreenshot() {

        mFreeDrawView.getDrawScreenshot(this);
        drawingPage.setVisibility(View.GONE);
    }

    private void changeColor() {
        int color = ColorHelper.getRandomMaterialColor(this);

        mFreeDrawView.setPaintColor(color);

        mSideView.setBackgroundColor(mFreeDrawView.getPaintColor());
    }

    public static String convertBitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == mBtnRandomColor.getId()) {
            changeColor();
        }

        if (id == mBtnUndo.getId()) {
            mFreeDrawView.undoLast();
        }

        if (id == mBtnRedo.getId()) {
            mFreeDrawView.redoLast();
        }

        if (id == mBtnClearAll.getId()) {
            mFreeDrawView.undoAll();
        }

        if (id == fullscreenButton.getId()) {
            sideView.setVisibility(View.GONE);
            viewControl.setVisibility(View.VISIBLE);
            Animation animation=AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
            viewControl.setAnimation(animation);
        }

        if (id == saveButton.getId()) {
            takeAndShowScreenshot();
        }

        if (id == removeButton.getId()) {
            mFreeDrawView.clearDrawAndHistory();
            FileHelper.deleteSavedStateFile(this);
        }

        if (id == viewControl.getId()) {
            sideView.setVisibility(View.VISIBLE);
            viewControl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        if (seekBar.getId() == mThicknessBar.getId()) {
            mFreeDrawView.setPaintWidthPx(THICKNESS_MIN + (progress * THICKNESS_STEP));
        } else {
            mFreeDrawView.setPaintAlpha(ALPHA_MIN + (progress * ALPHA_STEP));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onDrawCreated(Bitmap draw) {
        mSideView.setVisibility(View.GONE);
        mFreeDrawView.setVisibility(View.GONE);

        mImgScreen.setVisibility(View.VISIBLE);

        mImgScreen.setImageBitmap(draw);
        mImgScreen.setVisibility(View.GONE);
        bitmapImage.setImageBitmap(draw);
        bitmapImage.setVisibility(View.VISIBLE);
        base64String=convertBitmapToBase64(draw);
    //    Toast.makeText(getApplicationContext(),base64String,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrawCreationError() {
        Toast.makeText(this, "Error, cannot create bitmap", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPathStart() {

    }

    @Override
    public void onNewPathDrawn() {

    }

    @Override
    public void onUndoCountChanged(int undoCount) {
        mTxtUndoCount.setText(String.valueOf(undoCount));
    }

    @Override
    public void onRedoCountChanged(int redoCount) {
        mTxtRedoCount.setText(String.valueOf(redoCount));
    }
}
