package com.mv.vacay.main.activity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.graphics.ImageViewTouch;
import com.mv.vacay.graphics.ImageViewTouchBase;
import com.mv.vacay.utils.BitmapUtils1;

public class BroadmoorProductPictureViewActivity extends AppCompatActivity {

    private static final String LOG_TAG = "imageTouch";

    TextView zoomin,zoomout;
    ImageView cancel;
    ImageViewTouch image;
    float dX, dY;
    float initScaleX=0,initScaleY=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadmoor_product_picture_view);

//        image=(ImageViewTouch) findViewById(R.id.image) ;
//        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
//
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.caybelle);
//        image.setImageBitmap(bitmap, null, -1, -1);
//
//    //    image.setImageBitmap(Commons.broadmoorImage);
//
//        image.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction()) {
//
//                    case MotionEvent.ACTION_DOWN:
//
//                        dX = view.getX() - motionEvent.getRawX();
//                        dY = view.getY() - motionEvent.getRawY();
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        view.animate()
//                                .x(motionEvent.getRawX() + dX)
//                                .y(motionEvent.getRawY() + dY)
//                                .setDuration(0)
//                                .start();
//                        break;
//                    default:
//                        return false;
//                }
//                return true;
//            }
//        });
//
        cancel=(ImageView) findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        zoomin=(TextView) findViewById(R.id.zoomin);
        zoomin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initScaleX=image.getScaleX()+0.2f;
                initScaleY=image.getScaleY()+0.2f;
                image.setScaleX(initScaleX);
                image.setScaleY(initScaleY);
            }
        });

        zoomout=(TextView) findViewById(R.id.zoomout);
        zoomout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initScaleX=image.getScaleX()-0.2f;
                initScaleY=image.getScaleY()-0.2f;
                if(initScaleY<=0 || initScaleY<=0){
                    image.setScaleX(0.2f);
                    image.setScaleY(0.2f);
                    initScaleX=image.getScaleX();
                    initScaleY=image.getScaleY();
                }
                image.setScaleX(initScaleX);
                image.setScaleY(initScaleY);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        image = (ImageViewTouch) findViewById(R.id.image);

        // set the default image display type
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
//        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.caybelle);
//        image.setImageBitmap(bitmap, null, -1, -1);


        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        int size = (int) (Math.min(metrics.widthPixels, metrics.heightPixels) / 0.35);

        Bitmap bitmap = BitmapUtils1.resizeBitmap(Commons.broadmoorImage,size,size);

        if (null != bitmap) {
            Log.d(LOG_TAG, "screen size: " + metrics.widthPixels + "x" + metrics.heightPixels);
            Log.d(LOG_TAG, "bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            image.setOnDrawableChangedListener(
                    new ImageViewTouchBase.OnDrawableChangeListener() {
                        @Override
                        public void onDrawableChanged(final Drawable drawable) {
                            Log.v(LOG_TAG, "image scale: " + image.getScale() + "/" + image.getMinScale());
                            Log.v(LOG_TAG, "scale type: " + image.getDisplayType() + "/" + image.getScaleType());

                        }
                    }
            );
            image.setImageBitmap(bitmap, null, 5, 5);

        } else {
            Toast.makeText(this, "Failed to load the image", Toast.LENGTH_LONG).show();
        }


        image.setSingleTapListener(
                new ImageViewTouch.OnImageViewTouchSingleTapListener() {

                    @Override
                    public void onSingleTapConfirmed() {
                        Log.d(LOG_TAG, "onSingleTapConfirmed");
                    }
                }
        );

        image.setDoubleTapListener(
                new ImageViewTouch.OnImageViewTouchDoubleTapListener() {

                    @Override
                    public void onDoubleTap() {
                        Log.d(LOG_TAG, "onDoubleTap");
                    }
                }
        );

        image.setOnDrawableChangedListener(
                new ImageViewTouchBase.OnDrawableChangeListener() {

                    @Override
                    public void onDrawableChanged(Drawable drawable) {
                        Log.i(LOG_TAG, "onBitmapChanged: " + drawable);
                    }
                }
        );
    }
}
