package com.mv.vacay.classes;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.config_mapping.Mapping;
import com.mv.vacay.main.inbox.InboxActivity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by a on 1/25/2017.
 */

public class Notification extends Activity{

    private final static String sample_url = "http://codeversed.com/androidifysteve.png";
    ArrayList<MessageEntity> _datas=new ArrayList<>();
    int _news=0;


    public Notification(Context context,String email){
        getMessage(context,email);
    }

    public class CreateNotification extends AsyncTask<Void, Void, Void> {

        int style = Constants.NORMAL;
        Context _context;

        public CreateNotification(Context context) {
            this.style = Constants.NORMAL;
            this._context = context;
        }

        @Override
        protected Void doInBackground(Void... params) {
            android.app.Notification noti = new android.app.Notification();

            switch (style) {
                case Constants.NORMAL:
                    noti = setInboxStyleNotification(_context);
                    break;
            }

            noti.defaults |= android.app.Notification.DEFAULT_LIGHTS;
            noti.defaults |= android.app.Notification.DEFAULT_VIBRATE;
            noti.defaults |= android.app.Notification.DEFAULT_SOUND;

            noti.flags |= android.app.Notification.FLAG_ONLY_ALERT_ONCE;

            Commons.mNotificationManager.notify(0, noti);

            return null;

        }

        /**
         * Inbox Style Notification
         *
         * @return Notification
         * @see CreateNotification
         */
        private android.app.Notification setInboxStyleNotification(Context context) {
            Bitmap remote_picture = null;


            // Create the style object with InboxStyle subclass.
            NotificationCompat.InboxStyle notiStyle = new NotificationCompat.InboxStyle();
            notiStyle.setBigContentTitle("Hello! Here "+_news+" message");

            // Add the multiple lines to the style.
            // This is strictly for providing an example of multiple lines.
            for (int i = 0; i < _news; i++) {
                notiStyle.addLine((i+1)+" :  "+_datas.get(i).get_useremail());    //    _datas.get(i).get_usermessage()
                notiStyle.addLine("   "+_datas.get(i).get_usermessage());
            }
            notiStyle.setSummaryText("Please enjoy the messages.");

            try{

                if(Commons.userEntity.get_photoUrl().length()<1000){
                    try {
                        remote_picture = BitmapFactory.decodeStream((InputStream) new URL(Commons.userEntity.get_photoUrl()).getContent());
                    } catch (IOException e) {
                        e.printStackTrace();
                        remote_picture=BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.sendbird_ic_launcher);
                    }
                }else {
                    remote_picture=base64ToBitmap(Commons.userEntity.get_photoUrl());
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }

                // Creates an explicit intent for an ResultActivity to receive.
            Intent resultIntent = new Intent(context, InboxActivity.class);

            // This ensures that the back button follows the recommended convention for the back key.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack for the Intent (but not the Intent itself).
            stackBuilder.addParentStack(InboxActivity.class);

            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            long[] v = {500,1000};
            long v[] = { 0, 300, 200, 300, 500};

            // Adds the Intent that starts the Activity to the top of the stack.
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            return new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.animationfile)
                    .setFullScreenIntent(resultPendingIntent,true)
                    .setAutoCancel(true)
                    .setLargeIcon(remote_picture)
                    .setContentIntent(resultPendingIntent)
//                    .addAction(R.drawable.ic_launcher, "One", resultPendingIntent)
//                    .addAction(R.drawable.ic_launcher, "Two", resultPendingIntent)
//                    .addAction(R.drawable.ic_launcher, "Three", resultPendingIntent)
                    .setContentTitle("Hello! Here "+_news+" Message(s) for you.")
                    .setContentText("").setSound(uri)
                    .setVibrate(v)
                    .setStyle(notiStyle).build();
        }
    }


    public void getMessage(final Context context, final String email) {

        _datas.clear();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_GETMAIL;

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetMessagesResponse(context,response);


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

    public void parseGetMessagesResponse(Context context,String json) {

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

                    //        messageEntity.set_idx(jsonMessage.getInt("mail_id"));
                    messageEntity.set_useremail(jsonMessage.getString("from_mail").replace("%20"," "));
                    messageEntity.setUserfullname(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_username(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_photoUrl(jsonMessage.getString("photo_url"));
                    messageEntity.set_request_date(jsonMessage.getString("request_date"));
                    messageEntity.set_service(jsonMessage.getString("service"));
                    messageEntity.set_usermessage(jsonMessage.getString("text_message"));
                    messageEntity.set_imageUrl(jsonMessage.getString("image_message_url"));
                    messageEntity.set_requestLatLng(new LatLng(jsonMessage.getDouble("lat_message"),jsonMessage.getDouble("lon_message")));

                    _datas.add(0,messageEntity);
                }

                int newMessages=_datas.size();  Log.d("NewMessages===>",String.valueOf(newMessages));

                int oldMessages = Preference.getInstance().getValue(context, PrefConst.PREFKEY_BADGES, 0); Log.d("OldMessages===>",String.valueOf(oldMessages));
                int news=newMessages-oldMessages;
                _news=news;
                if(oldMessages==0) {
                    Preference.getInstance().put(context,
                            PrefConst.PREFKEY_BADGES, newMessages);
                    return;
                }

        //        _news=1;

                if(_news>0){
                    Commons._newMessage=true;
                    long pattern[] = { 0, 300, 200, 300, 500};

                    Log.d("EEEMAIL===>",_datas.get(0).get_useremail());

                    UserEntity userEntity=new UserEntity();
                    userEntity.set_photoUrl(_datas.get(0).get_photoUrl());
                    userEntity.set_name(_datas.get(0).get_userfullname());
                    userEntity.set_email(_datas.get(0).get_useremail());

                    Commons.userEntity=userEntity;

                    new CreateNotification(context).execute();
                    //        _vibrator.vibrate(5000);
                    //                msgNum.setText(String.valueOf(news));

                }else {
                    Commons._newMessage=false;
                    if(Commons._vibrator!=null){
                        Commons._vibrator.cancel();
                        Commons._vibrator=null;
                    }
                }

            }else if(result_code.equals("109")){
        //        showAlertDialog(context,"No messages from others.");
            }
            else {
        //        showAlertDialog(context,"Connecting to the server failed.\nPlease try again.");
            }
        }catch (JSONException e){
            e.printStackTrace();
        //    showAlertDialog(context,"Connecting to the server failed.\nPlease try again.");
        }
    }

    public void showToast(Context context,String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }


    public void showAlertDialog(Context context,String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, this.getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

}

