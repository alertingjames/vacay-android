package com.mv.vacay.main.carpediem;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.utils.BitmapUtils;

import java.io.File;
import java.io.InputStream;

public class TakePhotoActivity extends AppCompatActivity {

    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        String val=getIntent().getStringExtra("key");
        if(val.equals("gallery"))fromGallery();
        else if(val.equals("camera"))takePhoto();
    }

    public void fromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constants.PICK_FROM_ALBUM);

    }

    public void takePhoto() {

        //  _takePhoto=true;
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

    //    public void takePhoto() {
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        String picturePath = BitmapUtils.getTempFolderPath() + "photo_temp.jpg";
//        _imageCaptureUri = Uri.fromFile(new File(picturePath));
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, _imageCaptureUri);
//        startActivityForResult(intent, Constants.PICK_FROM_CAMERA);//????????????????????????///
//
//    }

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

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap_activity=bitmap;
//                        imagemessage.setImageBitmap(Commons.bitmap_activity);
                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();
                        Commons.imageUrl=_photoPath;
                        back();

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

                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
                    //        Commons.imageUrl=_photoPath;

//                    this.grantUriPermission("com.android.camera",_imageCaptureUri,
//                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(_imageCaptureUri, "image/*");

                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

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

    private void back() {
        Commons._is_commentPhotoBack=true;
        Intent intent=new Intent(this,VideoDisplayActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0,0);
    }

}
