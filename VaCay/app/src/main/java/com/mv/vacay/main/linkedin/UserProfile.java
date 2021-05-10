package com.mv.vacay.main.linkedin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkedin.platform.LISessionManager;
import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.SurveyQuestActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Vinicius on 27/09/16.
 */
public class UserProfile extends AppCompatActivity {


/*
    private static final String host = "api.linkedin.com";
    //private static final String  = "https://" + host + "/v1/people/:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";
    ProgressDialog progressDialog;
    private static final  String topCardUrl2 = "https://" + host + "/v1/people/~:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    private TextView mTextName;
    private TextView mTextEmail;
    private Button mButtonLogout;
    private ImageView mImageProfilePic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mTextName = (TextView) findViewById(R.id.text_name);
        mTextEmail = (TextView) findViewById(R.id.text_email);
        mImageProfilePic = (ImageView) findViewById(R.id.image_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Retrieve data ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        getUserData();


    }

    public void logout(View v) {
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        Intent intent = new Intent(this, LoginLinkedin.class);
        startActivity(intent);
    }


    public void getUserData()
    {
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(UserProfile.this, topCardUrl2, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {


                try {
                    setUserProfile(apiResponse.getResponseDataAsJson());
                    progressDialog.dismiss();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError LIApiError) {


                Toast.makeText(UserProfile.this, "failed Two" + LIApiError.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void setUserProfile(JSONObject response)
    {
        try
        {
            mTextEmail.setText(response.get("emailAddress").toString());
            mTextName.setText(response.get("formattedName").toString());

            Picasso.with(this)
                    .load(response.getString("pictureUrl"))
                    .into(mImageProfilePic);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }*/


    private static final String PROFILE_URL = "https://api.linkedin.com/v1/people/~";
    private static final String OAUTH_ACCESS_TOKEN_PARAM ="oauth2_access_token";
    private static final String QUESTION_MARK = "?";
    private static final String EQUALS = "=";
    private static final String host = "api.linkedin.com";
    private static final  String topCardUrl2 = "https://" + host + "/v1/people/~:(first-name,last-name,email-address,formatted-name,phone-numbers,public-profile-url,picture-url,picture-urls::(original))";

    private ImageView mImage;

    private TextView welcomeText;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        welcomeText = (TextView) findViewById(R.id.activity_profile_welcome_text);

        //Request basic profile of the user
        SharedPreferences preferences = this.getSharedPreferences("user_info", 0);
        String accessToken = preferences.getString("accessToken", null);

    //    Toast.makeText(getApplicationContext(),"result: "+String.valueOf(accessToken),Toast.LENGTH_SHORT).show();

        if(accessToken!=null){
            String profileUrl = getProfileUrl(accessToken);
            new GetProfileRequestAsyncTask().execute(profileUrl);
        }else {
    //        Toast.makeText(getApplicationContext(),"result: "+String.valueOf((Object) null),Toast.LENGTH_SHORT).show();
        }


    }

    private static final String getProfileUrl(String accessToken){
        return  PROFILE_URL
                +QUESTION_MARK
                +OAUTH_ACCESS_TOKEN_PARAM+EQUALS+accessToken;
    }



    private class GetProfileRequestAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute(){
            pd = ProgressDialog.show(UserProfile.this, "", UserProfile.this.getString(R.string.loading),true);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            if(urls.length>0){
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                httpget.setHeader("x-li-format", "json");
                try{
                    HttpResponse response = httpClient.execute(httpget);
                    if(response!=null){
                        //If status is OK 200
                        if(response.getStatusLine().getStatusCode()==200){

                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object
                            return new JSONObject(result);
                        }
                    }
                }catch(IOException e){
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize","Error Http response "+e.getLocalizedMessage());
                }
            }
            return null;
        }



        @Override
        protected void onPostExecute(JSONObject data){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
        //        Toast.makeText(getApplicationContext(),"result: "+String.valueOf(data),Toast.LENGTH_SHORT).show();
            }
            if(data!=null){

                try {
                    String welcomeTextString = String.format("Welcome %1$s %2$s, You are a %3$s",data.getString("firstName"),data.getString("lastName"),data.getString("headline"));

                    welcomeText.setText(welcomeTextString);
        //            Toast.makeText(getApplicationContext(),"your job: "+String.valueOf(welcomeTextString),Toast.LENGTH_LONG).show();

                    Commons.thisEntity.set_job(data.getString("headline"));
                    gotoSurveyQuestActivity();

                } catch (JSONException e) {

                    Log.e("Authorize","Error Parsing json "+e.getLocalizedMessage());

                }
            }
        }
    }

    public void logout(View view) {
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        Intent intent = new Intent(UserProfile.this, LoginLinkedin.class);
        startActivity(intent);
        finish();
    }

    public void gotoSurveyQuestActivity() {
        Intent intent = new Intent(getApplicationContext(), SurveyQuestActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }


}

