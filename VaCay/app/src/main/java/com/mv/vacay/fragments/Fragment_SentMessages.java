package com.mv.vacay.fragments;

/**
 * Created by a on 2016.11.05.
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import com.mv.vacay.adapter.MessageAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.meetfriends.ShowUserMessageActivity;
import com.mv.vacay.models.MessageEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a on 2016.11.05.
 */
public class Fragment_SentMessages extends Fragment{

    ListView listView;
    ArrayList<MessageEntity> _datas=new ArrayList<>();
    ArrayList<MessageEntity> _datas_temp=new ArrayList<>();
    List<String> titlesort=new ArrayList<>();
    boolean _arrange_flag=false;
    MessageAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_sentmessage, container, false);

        listView = (ListView) v.findViewById(R.id.listView_sentmessage);

        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                _datas.remove(i);
                adapter=new MessageAdapter(getContext(),_datas);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

                return false;
            }
        });

        return v;
    }

    public static Fragment_SentMessages newInstance(String text) {

        Fragment_SentMessages f = new Fragment_SentMessages();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new MessageAdapter(getContext(), _datas);
        listView.setAdapter(adapter);

        _datas.clear();
        getSentMessage(Commons.thisEntity.get_email());

//        _datas.addAll(Commons.thisEntity.get_messagesentList());
//        listView = (ListView) getActivity().findViewById(R.id.listView_sentmessage);
//        adapter=new MessageAdapter(getContext(),_datas);
//        adapter.resorting(_datas);
//        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Commons.messageEntity=_datas.get(i);
                Intent intent=new Intent(getActivity(), ShowUserMessageActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showAlertDialog(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();

        alertDialog.setTitle(getString(R.string.app_name));
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getContext().getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }


    public void getSentMessage(final String email) {

        String url = ReqConst.SERVER_URL + "allSentMail";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseGetSentMessagesResponse(response);


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

    public void parseGetSentMessagesResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSESENT===",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);

            if(result_code.equals("0")) {

                JSONArray messages = response.getJSONArray(ReqConst.RES_SENTMAILINFOS);
//                JSONArray users = response.getJSONArray(ReqConst.RES_USERINFO);
                Log.d("SENTMESSAGES===", messages.toString());

                for (int i = 0; i < messages.length(); i++) {

                    JSONObject jsonMessage = (JSONObject) messages.get(i);

                    MessageEntity messageEntity = new MessageEntity();

                    messageEntity.setMail_id(jsonMessage.getString("mail_id"));
                    messageEntity.setMail_id(jsonMessage.getString("mail_id"));
                    messageEntity.set_useremail(jsonMessage.getString("to_mail").replace("%20"," "));
                    messageEntity.setUserfullname(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_username(jsonMessage.getString("name").replace("-", "."));
                    messageEntity.set_usermessage(jsonMessage.getString("text_message"));
                    messageEntity.set_request_date(jsonMessage.getString("request_date"));
                    messageEntity.set_service(jsonMessage.getString("service"));
                    messageEntity.set_service_reqdate(jsonMessage.getString("service_reqdate"));
                    messageEntity.set_imageUrl(jsonMessage.getString("image_message_url"));
                    messageEntity.set_requestLatLng(new LatLng(jsonMessage.getDouble("lat_message"),jsonMessage.getDouble("lon_message")));

                    _datas.add(0,messageEntity);
                }
                adapter = new MessageAdapter(getContext(), _datas);
//                adapter.resorting(_datas);
                adapter.notifyDataSetChanged();
                listView.setAdapter(adapter);

            }else if(result_code.equals("110")){
                Toast.makeText(getContext(), "No messages by the user.", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(), getString(R.string.error), Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteSentMessage(final int index) {

        String url = ReqConst.SERVER_URL + "deleteSentMail";

        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                VolleyLog.v("Response:%n %s", response.toString());

                parseDeleteSentMessagesResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("mail_id", String.valueOf(index));

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);
    }

    public void parseDeleteSentMessagesResponse(String json) {

        try{

            JSONObject response = new JSONObject(json);
            Log.d("RESPONSESENT===",response.toString());

            String result_code = response.getString(ReqConst.RES_CODE);

            if(result_code.equals("0")) {
                showAlertDialog("Mail deleted!");
            }
            else {
                showAlertDialog(getString(R.string.error));
            }
        }catch (JSONException e){
            e.printStackTrace();
            showAlertDialog(getString(R.string.error));
        }
    }
}
