package com.mv.vacay.base;

/**
 * Created by a on 2016.11.22.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mv.vacay.R;

/**
 * Created by a on 6/13/2016.
 */
public abstract class BaseActivity extends AppCompatActivity implements Handler.Callback {


    public Context _context = null;

    public Handler _handler = null;

    private ProgressDialog _progressDlg;

    private Vibrator _vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _context = this;

        _vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        _handler = new Handler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        closeProgress();

        try {
            if (_vibrator != null)
                _vibrator.cancel();
        } catch (Exception e) {
        }
        _vibrator = null;

        super.onDestroy();
    }


//    public void showProgress(boolean cancelable) {
//
//        closeProgress();
//
//        _progressDlg = ProgressDialog.show(getApplicationContext(), "", getApplicationContext().getString(R.string.loading),true);
////        _progressDlg
////                .setProgressStyle(android.R.style.Widget_ProgressBar_Large);
////        _progressDlg.setCancelable(cancelable);
////        _progressDlg.show();
//    }

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

//    public void showAlertDialog(String msg) {
//
//        AlertDialog alertDialog = new AlertDialog.Builder(_context).create();
//
//        alertDialog.setTitle(getString(R.string.app_name));
//        alertDialog.setMessage(msg);
//
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, _context.getString(R.string.ok),
//
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//        //alertDialog.setIcon(R.drawable.banner);
//        alertDialog.show();
//
//    }

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

    public void vibrate() {

        if (_vibrator != null)
            _vibrator.vibrate(1500);
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {

            default:
                break;
        }

        return false;
    }

}

