package com.mv.vacay.api;

/**
 * Created by a on 2017.01.03.
 */

import android.content.Context;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sodha on 8/3/16.
 */
public class MakeJsonObjectRequest {
    public static void call(Context context, int method, String url, String requestBody, final VolleyResponseListner listener)  {
        JSONObject jsonRequest = null;
//        try {
//            if (requestBody != null) {
//                jsonRequest = new JSONObject(requestBody);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });
        CustomVolleyRequest.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
}