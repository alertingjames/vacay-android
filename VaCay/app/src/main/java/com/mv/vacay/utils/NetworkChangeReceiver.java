package com.mv.vacay.utils;

/**
 * Created by a on 2017.01.03.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mv.vacay.main.youtubesearch.NoInternetActivity;


/**
 * Created by sodha on 10/3/16.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    static boolean flag = false;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = NetworkUtil.getConnectivityStatusString(context);
        if(status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
            Toast.makeText(context, "No Internet", Toast.LENGTH_LONG).show();
            Intent retry = new Intent(context, NoInternetActivity.class);
            flag = true;
            context.startActivity(retry);
        }
    }
}
