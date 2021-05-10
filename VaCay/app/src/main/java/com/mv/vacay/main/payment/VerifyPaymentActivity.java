package com.mv.vacay.main.payment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.meetfriends.ChatActivity;
//import com.sendbird.android.shadow.com.google.gson.Gson;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import butterknife.Bind;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class VerifyPaymentActivity extends AppCompatActivity {

    public SQLiteDatabase db;

//    @Bind(R.id.checkTV)
//    TextView check;


    public String cardHolderName = "";
    public String proFirst = "";
    public String proLast = "";
    public String creditCardNumber = "";
    public String expiryDate = "";
    public String cvv = "";
    public int expmonth = 0;
    public int expyear = 0;
    public int price;
    String proId="";
    String proEmail="";
    String proPhoto="";
    private ProgressDialog _progressDlg;
    public static final String PUBLISHABLE_KEY = "pk_live_J4bQpu3jLQ7jUPfZKLAcs1WV";
    public static final String SECRET_KEY = "sk_live_bxyrJ9CkAhUhVDUw4Zvw7hGW";
    public static final String SECRET_TEST_KEY = "sk_test_OkINWhx1sAWgwxrtyP6RSf5K";
    String accountId="",accountStatus="";
    TextView firstName, lastName, email,updateButton,country;
    EditText  birthDate,birthMonth,birthYear,bankNumber,routingNumber,city,addressLine,postalCode, state,ssnLast4;
    RelativeLayout accountPage;
    TextView check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        check=(TextView)findViewById(R.id.checkTV);

        proId=getIntent().getStringExtra("proid");  Log.d("ProId===>",proId);
        proEmail=getIntent().getStringExtra("proemail");
        cardHolderName=getIntent().getStringExtra("proName");
        proFirst=getIntent().getStringExtra("proFirst");Log.d("ProFirst===>",proFirst);
        proLast=getIntent().getStringExtra("proLast");Log.d("ProLast===>",proLast);
        proPhoto=getIntent().getStringExtra("proPhotoUrl");

        accountPage=(RelativeLayout)findViewById(R.id.accountPage);

        firstName=(TextView)findViewById(R.id.firstName);
        firstName.setText(proFirst);
        lastName=(TextView)findViewById(R.id.lastName);
        lastName.setText(proLast);
        email=(TextView)findViewById(R.id.emailbox);
        email.setText(proEmail);
        updateButton=(TextView)findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(birthDate.getText().length()>0 && birthMonth.getText().length()>0 && birthYear.getText().length()>0 && bankNumber.getText().length()>0 &&
                        country.getText().length()>0 && routingNumber.getText().length()>0 && email.getText().length()>0 && firstName.getText().length()>0 &&
                        lastName.getText().length()>0) {
//                    updateAccount(proEmail);
                    try {
                        updateAccount2(accountId);
                    } catch (CardException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    } catch (APIException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                    }

                }
                else showToast("Please check the blank boxes and retry writing.");
            }
        });
        birthDate=(EditText) findViewById(R.id.day);
        birthMonth=(EditText) findViewById(R.id.month);
        birthYear=(EditText) findViewById(R.id.year);
        bankNumber=(EditText) findViewById(R.id.bankNumber);
        country=(TextView) findViewById(R.id.countryContainer);
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectCountry();
            }
        });
        routingNumber=(EditText) findViewById(R.id.routing);
        city=(EditText) findViewById(R.id.city);
        addressLine=(EditText) findViewById(R.id.addressLine);
        postalCode=(EditText) findViewById(R.id.postalCode);
        state=(EditText) findViewById(R.id.state);
        ssnLast4=(EditText) findViewById(R.id.ssnLastFour);

//        selectTalkOrCreateUpdateAccount();

        viewAccountStatus3(proEmail);
    }
    public void registerOldCard(){
        final int GET_NEW_CARD = 2;
        Intent intent = new Intent(this, CardEditActivity.class);
        startActivityForResult(intent, GET_NEW_CARD);
    }

    public void selectCreateUpdateAccount() {

        final String[] items = {"Create & Update Account","Just Update Account"
        //        "View Account Status"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Create/Update Your Account...");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    try {
                        create_Account(proEmail, createAccount2(proEmail), "US");
                    } catch (CardException e) {
                        e.printStackTrace();
                    } catch (APIException e) {
                        e.printStackTrace();
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    } catch (InvalidRequestException e) {
                        e.printStackTrace();
                    } catch (APIConnectionException e) {
                        e.printStackTrace();
                    }
                } else if (item == 1) {
                    viewAccountStatus2(proEmail);
                }
//                else if (item == 2) {
//                    viewAccountStatus(proEmail);
//                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectTalkOrCreateUpdateAccount() {

        final String[] items = {"Talk to him/her","Create/Update Account"
                //        "View Account Status"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please select ...");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Commons.userEntity.set_firstName(proFirst);
                    Commons.userEntity.set_lastName(proLast);
                    Commons.userEntity.set_name(cardHolderName);
                    Commons.userEntity.set_photoUrl(proPhoto);
                    Commons.userEntity.set_email(proEmail);
                    Intent intent=new Intent(getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item == 1) {
                    selectCreateUpdateAccount();
                }
//                else if (item == 2) {
//                    viewAccountStatus(proEmail);
//                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectCountry() {

        final String[] items = {"US","CA","AU","GB","JP","SG","HK","NZ"
                //        "View Account Status"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select \nCountry...");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                country.setText(items[item]);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alert.getWindow().getAttributes());
        lp.width = 700;
//        lp.height = 700;
//        lp.x=80;
//        lp.y=-1200;
        alert.getWindow().setAttributes(lp);
    }

    public void showProgress() {
        closeProgress();
        _progressDlg = ProgressDialog.show(this, "", "Provider's profile updating...",true);
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

    public void updateProviderToken(final String acc_id) {

        String url = ReqConst.SERVER_URL + "updateProviderToken";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

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

                params.put("proid", proId);
                params.put("proToken", acc_id);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseRestUrlsResponse(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("proResponse=====> :",response.toString());  Log.d("proId=====> :",proId);

            String success = response.getString(ReqConst.RES_CODE);

            if (success.equals("0")) {

                showToast("Successfully provider's profile updated with the payment!");
                finish();
            }
            else {
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast("Provider's profile updating with the payment failed! \nTry again...");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }


    public void showAlertDialog(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Please Update Your Account");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        check.setVisibility(View.GONE);
                        accountPage.setVisibility(View.VISIBLE);
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, this.getString(R.string.cancel),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }

    public void showAlertDialog2(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Please Update Your Account Later Again");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }
    public void showAlertDialog1(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Please Update Your Profile");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("Status===>",accountStatus);

                        updateProviderToken(accountId);

//                        if(!accountStatus.equals("Pending verification"))
//                            updateProviderToken(accountId);
//                        else showAlertDialog2("Your Account is on Pending verification.");
                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public String createAccount2(String email) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        String accoutId="";

        Stripe.apiKey = SECRET_KEY;

        Map<String, Object> accountParams = new HashMap<String, Object>();
        accountParams.put("type", "custom");
        accountParams.put("country", "US");
        accountParams.put("email", email);

        Account account=Account.create(accountParams);
        accoutId = account.getId();    Log.d("AccountID===>", accoutId);

        return accoutId;
    }

    public void updateAccount2(String accountId) throws CardException, APIException, AuthenticationException, InvalidRequestException, APIConnectionException {

        String accoutId="";

        Stripe.apiKey = SECRET_KEY;

        Map<String, Object> accountParams = new HashMap<String, Object>();

        Map<String, Object> externalParams = new HashMap<String, Object>();
        externalParams.put("object", "bank_account");
        externalParams.put("account_number", bankNumber.getText().toString());
        externalParams.put("country", country.getText().toString());
        externalParams.put("currency", "USD");
        externalParams.put("routing_number", routingNumber.getText().toString());

        accountParams.put("external_account", externalParams);

        Map<String, Object> dobParams = new HashMap<String, Object>();
        dobParams.put("day", birthDate.getText().toString());
        dobParams.put("month", birthMonth.getText().toString());
        dobParams.put("year", birthYear.getText().toString());

        Map<String, Object> dob = new HashMap<String, Object>();
        dob.put("dob", dobParams);

        accountParams.put("legal_entity", dob);

        Map<String, Object> legalParams = new HashMap<String, Object>();
        legalParams.put("first_name", proFirst);
        legalParams.put("last_name", proLast);
        legalParams.put("type", "individual");

        accountParams.put("legal_entity", legalParams);

        Map<String, Object> addrParams = new HashMap<String, Object>();
        addrParams.put("city", city.getText().toString());
        addrParams.put("line1", addressLine.getText().toString());
        addrParams.put("postal_code", postalCode.getText().toString());
        addrParams.put("state", state.getText().toString());

        Map<String, Object> addressParams = new HashMap<String, Object>();
        addressParams.put("dob", addrParams);

        accountParams.put("legal_entity", addressParams);

        Map<String, Object> ssnParams = new HashMap<String, Object>();
        ssnParams.put("ssn_last_4", ssnLast4.getText().toString());

        accountParams.put("legal_entity", ssnParams);

        Map<String, Object> tosParams = new HashMap<String, Object>();
        tosParams.put("date", (long) System.currentTimeMillis() / 1000L);
        tosParams.put("ip", "75.70.234.51");

        accountParams.put("tos_acceptance", tosParams);

        Log.d("Stripe_Date===>", String.valueOf(System.currentTimeMillis() / 1000L) + "///" + String.valueOf(new Date().getTime()));
        Log.d("Name===>", proFirst + "///" + proLast);
        Log.d("Birthday===>", birthYear.getText().toString() + "///" + birthMonth.getText().toString() + "///" + birthDate.getText().toString());

        Account account = Account.retrieve(accountId, null);

        Account acc= account.update(accountParams);

        String acc_id=acc.getId();   Log.d("AccountId===>", acc_id);

        if(acc_id.length()>0){
            update_Account(proEmail);
        }
    }

    public void create_Account(final String email, final String stripe_id, final String country) {

        String url = ReqConst.SERVER_URL + "account_create";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseCreateAccountResponse2(response);

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

                params.put("stripe_id", stripe_id);
                params.put("email", email);
                params.put("country", country);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseCreateAccountResponse2(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("accountResponse=====> :",response.toString());

            String result = response.getString("result");

            if (result.equals("success")) {

                JSONObject userInfo = response.getJSONObject("account_data");  Log.d("accountData=====> :",userInfo.toString());

                accountId=userInfo.getString("accountid");
                accountStatus=userInfo.getString("status");
                showAlertDialog("Successfully Created.\n\nAccountId: "+accountId+"\n\n"+"Status: "+accountStatus+"\n");
            }
            else {
                showToast("Stripe account creation failed");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast("Stripe account creation failed");
        }
    }

    public void viewAccountStatus2(final String email) {

        String url = ReqConst.SERVER_URL + "account_details";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseViewAccountResponse2(response);

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

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseViewAccountResponse2(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("accountResponse=====> :",response.toString());

            String status = response.getString("status");

            if (status.equals("success")) {

                JSONObject userInfo = response.getJSONObject("account_data");  Log.d("accountData=====> :",userInfo.toString());

                accountId=userInfo.getString("accountid");
                accountStatus=userInfo.getString("status");
                showAlertDialog("This is your account status.\n\nAccountId: "+accountId+"\n\n"
                        +"Status: "+accountStatus+"\n"
                );
            }
            else if(status.equals("error")) showToast("This person's account isn't created yet.");
            else {

                String error = response.getString(ReqConst.RES_ERROR);
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast(error);
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void update_Account(final String email) {

        String url = ReqConst.SERVER_URL + "account_update_required"; Log.d("API===>",url);

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseUpdateAccountResponse2(response);

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

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseUpdateAccountResponse2(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);

            String status = response.getString("status");  Log.d("Status=====> :",status);

            if (status.equals("success")) {

                if(email.equals(Commons.thisEntity.get_email())){
                    showToast("Successfully Updated!");
                }
                else showAlertDialog1("Successfully Updated!");
            }
            else {
                showToast("Failed to update");
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void viewAccountStatus3(final String email) {

        String url = ReqConst.SERVER_URL + "account_details";

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Log.d("REST response========>", response);
                VolleyLog.v("Response:%n %s", response.toString());

                parseViewAccountResponse3(response);

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

                params.put("email", email);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);

    }

    public void parseViewAccountResponse3(String json) {

        closeProgress();
        try {

            JSONObject response = new JSONObject(json);   Log.d("accountResponse=====> :",response.toString());

            String status = response.getString("status");

            if (status.equals("success")) {

                JSONObject userInfo = response.getJSONObject("account_data");  Log.d("accountData=====> :",userInfo.toString());

                accountId=userInfo.getString("accountid");
                accountStatus=userInfo.getString("status");
                if(accountStatus.startsWith("Pending"))
                    showAlertDialog("This is your account status.\n\nAccountId: "+accountId+"\n\n"
                        +"Status: "+accountStatus+"\n"
                    );
                else if(accountStatus.equals("Approved")){
                    showToast("Already approved");
                    finish();
                }
            }
            else if(status.equals("error")) showAlertDialog3("This person's account isn't created yet. Please create account");
            else {

                String error = response.getString(ReqConst.RES_ERROR);
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast(error);
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void showAlertDialog3(String msg) {

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(this).create();

        alertDialog.setTitle("Info!");
        alertDialog.setMessage(msg);

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            create_Account(proEmail, createAccount2(proEmail), "US");
                        } catch (CardException e) {
                            e.printStackTrace();
                        } catch (APIException e) {
                            e.printStackTrace();
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                        } catch (InvalidRequestException e) {
                            e.printStackTrace();
                        } catch (APIConnectionException e) {
                            e.printStackTrace();
                        }
                    }
                });
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, this.getString(R.string.cancel),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
        alertDialog.show();
    }
}






































