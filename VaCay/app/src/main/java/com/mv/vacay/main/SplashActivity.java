package com.mv.vacay.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.mv.vacay.R;

public class SplashActivity extends Activity {
    ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressStatus = 0;

                /*
                    A Thread is a concurrent unit of execution. It has its own call stack for
                    methods being invoked, their arguments and local variables. Each application
                    has at least one thread running when it is started, the main thread,
                    in the main ThreadGroup. The runtime keeps its own threads
                    in the system thread group.
                */
        // Start the lengthy operation in a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(progressStatus < 200){
                    // Update the progress status
                    progressStatus +=1;

                    // Try to sleep the thread for 20 milliseconds
                    try{
                        Thread.sleep(20);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }

                    // Update the progress bar
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            // Show the progress on TextView
                        }
                    });
                }
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        }).start(); // Start the operation
    }
}
