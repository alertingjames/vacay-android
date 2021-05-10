package com.mv.vacay.main.beauty;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.manager.BeautyManagerPageActivity;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BeautyProviderPhotoActivity extends BaseActivity {
    ImageView back;
    Button upload;
    CircularImageView providerPhoto;
    String _photoPath = "",_photoPath_provider = "";
    private Uri _imageCaptureUri;
    Bitmap bitmap=null,bitmap_provider=null;
    boolean provider_Photo_flag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_provider_photo);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons._is_beautyProviderPage=true;
                Intent intent=new Intent(getApplicationContext(), BeautyManagerPageActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
            }
        });

        upload=(Button)findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadProviderImage();
            }
        });
        providerPhoto=(CircularImageView) findViewById(R.id.provider_profile);
        selectPhoto();


    }

    public void selectPhoto() {

        final String[] items = {"Take photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You should take the provider's photo.");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    takePhoto();
                } else {
                    fromGallery();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void fromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto() {

        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        _imageCaptureUri = this.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {

            case Constants.CROP_FROM_CAMERA: {

                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap_buf=null;
                        File saveFile = BitmapUtils.getOutputMediaFile(this);

                        InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                        BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                        bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                        String path=saveFile.getAbsolutePath();

                        in.close();


                        //set The bitmap data to image View

                            Commons.bitmap_provider=bitmap;
                            providerPhoto.setImageBitmap(Commons.bitmap_provider);
                            _photoPath_provider= path;

                        //           Constants.userphoto=ui_imvphoto.getDrawable();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.PICK_FROM_ALBUM:

                if (resultCode == RESULT_OK) {
                    _imageCaptureUri = data.getData();
                }

            case Constants.PICK_FROM_CAMERA: {
                try {
//                    _photoPath_provider=BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    _photoPath_provider = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    File saveFile = new File(_photoPath_provider);

                    InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                    BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                    String path=saveFile.getAbsolutePath();

                    in.close();


                    //set The bitmap data to image View

                    Commons.bitmap_provider=bitmap;
                    providerPhoto.setImageBitmap(Commons.bitmap_provider);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

                    intent.putExtra("crop", true);
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", Constants.PROFILE_IMAGE_SIZE);
                    intent.putExtra("outputY", Constants.PROFILE_IMAGE_SIZE);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("noFaceDetection", true);
                    //intent.putExtra("return-data", true);
                    intent.putExtra("output", Uri.fromFile(BitmapUtils.getOutputMediaFile(this)));

                    startActivityForResult(intent, Constants.CROP_FROM_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void uploadProviderImage() {

        try {

            File file = new File(_photoPath_provider);  Log.d("ProviderImage===",file.toString());

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(Commons.providerId));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(1));

            String url = ReqConst.SERVER_URL + ReqConst.REQ_UPLOADPHOTO;

            MultiPartRequest reqMultiPart = new MultiPartRequest(url, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

                    closeProgress();
                    showToast(getString(R.string.photo_upload_fail));
                }
            }, new Response.Listener<String>() {

                @Override
                public void onResponse(String json) {

                    ParseUploadImgResponse1(json);
                    Log.d("ProimageJson===",json.toString());
                    Log.d("params====",params.toString());
                }
            }, file, ReqConst.PARAM_FILE, params);

            reqMultiPart.setRetryPolicy(new DefaultRetryPolicy(
                    Constants.VOLLEY_TIME_OUT, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            VaCayApplication.getInstance().addToRequestQueue(reqMultiPart, url);

        } catch (Exception e) {

            e.printStackTrace();
            closeProgress();
            showToast(getString(R.string.photo_upload_fail));
        }
    }


    public void ParseUploadImgResponse1(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("resultPRO===",String.valueOf(result_code));

            if (result_code == 0) {
                showToast("Successfully your beauty info registered on server.");
                Intent intent=new Intent(getApplicationContext(), BeautyServiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);

            } else if(result_code==102){
                showToast(getString(R.string.unregistered_user));
            }else if(result_code==103){
                showToast("Upload file size error!");
            }
            else {
                showToast(getString(R.string.photo_upload_fail));
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(getString(R.string.photo_upload_fail));
        }
    }

}
