package com.mv.vacay.main.manager;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.beauty.BeautyProviderPhotoActivity;
import com.mv.vacay.main.beauty.BeautyServiceActivity;
import com.mv.vacay.main.beauty.BeautyServiceRequestActivity;
import com.mv.vacay.models.BeautyEntity;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BeautyManagerPageActivity extends BaseActivity {

    String _photoPath = "",_photoPath_provider = "";
    Bitmap bitmap=null,bitmap_provider=null;
    String categoryStr="",areas="",beautynm="";
    LinearLayout spinner,area,beauty;
    private Uri _imageCaptureUri;
    boolean provider_Photo_flag=false;
    int category_id=0,beauty_id=0;
    ImageView beautyImage,locButton,back;
    CircularImageView providerPhoto;
    TextView providerName,price,okButton,restaurant,description;
    int area_id=0;
    String locationUrl="",provideremail="";
    private int _idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_page);

        Commons.providerLatlng=null;

        beautyImage=(ImageView)findViewById(R.id.beauty_image);
        beautyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        providerPhoto=(CircularImageView) findViewById(R.id.provider_profile);
        if(Commons._is_beautyProviderPage) {
            providerPhoto.setImageBitmap(Commons.bitmap_provider);
            Commons._is_beautyProviderPage=false;
        }
        providerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  showToast("Please upload it later.");
            }
        });
        back=(ImageView)findViewById(R.id.back);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), BeautyServiceRequestActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
        restaurant=(TextView)findViewById(R.id.restaurant);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RestaurantManagerActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });

        okButton=(TextView)findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (providerName.getText().length() != 0 && price.getText().length() != 0
                            && beautynm!="" && description.getText().length() != 0
                            && categoryStr != "" && areas != "" && _photoPath!=""
//                            && !Commons.bitmap_provider.equals(null)
                            ) {
                           showChangeLangDialog_BeautyManager();

                    } else
                        showToast("Please input all items.");
                }catch (NullPointerException e){
                    showToast("Please input all items.");
                }
            }
        });

        providerName=(EditText)findViewById(R.id.edt_provider_name);
        price=(EditText)findViewById(R.id.edt_price);
//        beautyName=(EditText)findViewById(R.id.edt_beautyname);
        description=(EditText)findViewById(R.id.edt_desc);
        spinner=(LinearLayout)findViewById(R.id.lyt_spin);
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoiceDialog();
            }
        });

        area=(LinearLayout)findViewById(R.id.lyt_spin_area);
        area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoiceDialog_area();
            }
        });
        beauty=(LinearLayout)findViewById(R.id.lyt_spin_name);
        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoiceDialog_beauty();
            }
        });
    }

    public void showChangeLangDialog_BeautyManager() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_edit_dialog_beauty, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.customView);
        final TextView loc_button=(TextView)dialogView.findViewById(R.id.loc_button);
        loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SelectLocationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });

        dialogBuilder.setTitle("Additional items");
        dialogBuilder.setMessage("Please input following items additionally.");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {


                //do something with edt.getText().toString();
                try{
                    if(edt.getText().length()!=0
                            && !Commons.providerLatlng.equals(null)
                            ) {
                        BeautyEntity beautyEntity=new BeautyEntity();
                        try{
                            beautyEntity.set_providerLatlng(Commons.providerLatlng);
                            beautyEntity.set_providerlat(Commons.providerLat);
                            beautyEntity.set_providerlng(Commons.providerlng);
                            provideremail=edt.getText().toString();
                        }catch (NullPointerException e){

                        }
                        beautyEntity.set_beauty_category(categoryStr);
                        beautyEntity.set_area(areas);
                        beautyEntity.set_provider_email(edt.getText().toString());
                        beautyEntity.set_beauty_price(Float.parseFloat(price.getText().toString()));
                        beautyEntity.set_beauty_name(beautynm);
                        beautyEntity.set_provider_name(providerName.getText().toString());
                        beautyEntity.set_description(description.getText().toString());
                        beautyEntity.set_provider_resId(0);
                        beautyEntity.set_providerBitmap(bitmap_provider);
                        beautyEntity.set_beautyBitmap(Commons.bitmap);
                        beautyEntity.set_beauty_resId(0);
                        beautyEntity.set_provider_imageURL(_photoPath_provider);
                        beautyEntity.set_beauty_imageURL(_photoPath);

                        Commons.bufbeautyEntities.add(beautyEntity);

                        registerBeautyInfo1();

//                        showToast("Your new beauty service item uploaded successfully.");
//                        Intent intent=new Intent(getApplicationContext(), BeautyServiceActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        finish();
//                        overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    }
                    else if(edt.getText().length()==0)showToast("Please input the provider's email.");
                    else if(Commons.providerLatlng.equals(null))showToast("Please select the provider's location.");
                }catch (NullPointerException e){
                    showToast("Please select the provider's location.");
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    public void selectPhoto() {

        final String[] items = {"Take photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(provider_Photo_flag) provider_Photo_flag=false;
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

    public void showChoiceDialog() {

        final String[] items = {"Hair",
                "Blowout",
                "Manicure/Pedicure",
                "Massage",
                "Wax",
                "Facial"
        };

        final TextView category=(TextView) findViewById(R.id.edt_category);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Beauty service type");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                category.setText(items[item]);
                categoryStr=items[item];
                category_id=item+1;
                showToast("Please select an beauty name of "+categoryStr+" in Beauty name field.");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_area() {

        final String[] items = {"San Francisco",
                "New York",
                "Chicago",
                "Denver"
        };

        final TextView category_area=(TextView) findViewById(R.id.category_area);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Beauty service area.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                category_area.setText(items[item]);
                areas=items[item];
                area_id=item+1;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChoiceDialog_beauty() {

        final String[][] items_list={{"Haircut","Color","Brazilian Blowout","Keratin Treatment","Deep Conditioner"},
                {"Blowout"},{"Manicure","Manicure:Gel","Pedicure","Pedicure:Gel","Pink & White"},
                {"Deep Tissue Massage (50 Minutes)","Deep Tissue Massage (90 Minutes)","Swedish Massage (50 Minutes)","Swedish Massage (90 minutes)"},
                {"Eye Brow Wax","Lip Wax","Bikini Wax","Brazilian Wax"},{"Basic Facial","Premium Facial"}};

        final String[] items_list1={"Haircut","Color","Brazilian Blowout","Keratin Treatment","Deep Conditioner",
                "Blowout","Manicure","Manicure: Gel","Pedicure","Pedicure: Gel","Pink & White",
                "Deep Tissue Massage (50 Minutes)","Deep Tissue Massage (90 Minutes)","Swedish Massage (50 Minutes)","Swedish Massage (90 minutes)",
                "Eye Brow Wax","Lip Wax","Bikini Wax","Brazilian Wax","Basic Facial","Premium Facial"};

        final String[] items = items_list[category_id-1];

        final TextView  beautyName=(TextView) findViewById(R.id.beautyname);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Beauty service name.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setNegativeButton("More", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }

        });
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                beautyName.setText(items[item]);
                beautynm=items[item];
                for(int i=0;i<items_list1.length;i++){
                    if(items[item].equals(items_list1[i])){
                        beauty_id=i+1;
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
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

                        beautyImage.setImageBitmap(bitmap);
                        _photoPath=path;
                        Commons.bitmap=bitmap;

                        //set The bitmap data to image View

//                        if(provider_Photo_flag) {
//                            bitmap_provider=bitmap;
//                            providerPhoto.setImageBitmap(bitmap_provider);
//                            _photoPath_provider = path;  File file1=new File(_photoPath_provider); Log.d("PROVIDER_FILE",file1.toString());
//                            provider_Photo_flag=false;
//                        }else {
//                            beautyImage.setImageBitmap(bitmap);
//                            Commons.bitmap=bitmap;
//                            Commons.beautyimagepath = path;  File file2=new File(Commons.beautyimagepath); Log.d("BEAUTY_FILE",file2.toString());
//
//                        }



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
//                    if(provider_Photo_flag)_photoPath_provider=BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);
//                    else Commons.beautyimagepath=BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    _photoPath = BitmapUtils.getRealPathFromURI(this, _imageCaptureUri);

                    File saveFile = new File(_photoPath);

                    InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                    BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeStream(in, null, bitOpt);
                    String path=saveFile.getAbsolutePath();

                    in.close();

                    beautyImage.setImageBitmap(bitmap);

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

    public void registerBeautyInfo1() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_REGISTERPROVIDER;

        try {
            String providername = providerName.getText().toString().replace(" ","%20").replace(".","-");
            //       firstname = URLEncoder.encode(firstname, "utf-8");
            String provider_email =provideremail.replace(" ","");
            //       lastname = URLEncoder.encode(lastname, "utf-8");
            String service_area = String.valueOf(area_id).replace(" ","%20");
            //        email = URLEncoder.encode(email, "utf-8");
            String providerLat = String.valueOf(Commons.providerLat);
            String providerLng = String.valueOf(Commons.providerlng);
            String categoryId=String.valueOf(category_id);
            String beautyId=String.valueOf(beauty_id);
            String beautyPrice=price.getText().toString().replace(".","-");
            String beautyDescription=description.getText().toString().replace(" ","%20").replace(".","-");

            String params = String.format("/%s/%s/%s/%s/%s/%s/%s/%s/%s",providername,provider_email, service_area,providerLng,providerLat,
                    categoryId,beautyId,beautyPrice,beautyDescription);

            url += params;
            Log.d("API====",url);

        } catch (Exception ex) {
            return;
        }

        showProgress();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String json) {

                parseRegisterResponse(json);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                closeProgress();
                showToast(getString(R.string.error));
            }
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);

    }

    public void parseRegisterResponse(String json) {

        Log.d("JsonREST====",json);
        try {
            JSONObject response = new JSONObject(json);

            int result_code = response.getInt(ReqConst.RES_CODE);

            Log.d("result===",String.valueOf(result_code));

            if (result_code == ReqConst.CODE_SUCCESS) {

                _idx = response.getInt(ReqConst.RES_PROID);   Log.d("idx====",String.valueOf(_idx));
                Commons.providerId=_idx;

                uploadBeautyImage();

                //               showAlertDialog("Successfully registered on server.");

            } else if (result_code == 105) {

                closeProgress();
                showToast("Provider registered the beauty name in this area.");
            } else {
                closeProgress();
                showToast(getString(R.string.register_fail));
            }

        } catch (JSONException e) {
            closeProgress();
            showToast(getString(R.string.register_fail));

            e.printStackTrace();
        }

    }

    public void uploadBeautyImage() {

        try {

            File file = new File(_photoPath);    Log.d("BeautyImage===",file.toString());

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(3));

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

                    ParseUploadImgResponse(json);
                    Log.d("imageJsonBeauty===",json.toString());
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


    public void ParseUploadImgResponse(String json) {

        closeProgress();

        try {
            JSONObject response = new JSONObject(json);
            int result_code = response.getInt(ReqConst.RES_CODE);
            Log.d("resultBeauty===",String.valueOf(result_code));

            if (result_code == 0) {
//                showToast("Successfully registered on server.");

 //               uploadProviderImage();

//                showToast("Successfully your beauty info registered on server.");
//                Intent intent=new Intent(getApplicationContext(), BeautyServiceActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.left_in,R.anim.right_out);

                Intent intent=new Intent(getApplicationContext(), BeautyProviderPhotoActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);

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

    public void uploadProviderImage() {

        try {

            File file = new File(Commons.providerimagepath);  Log.d("ProviderImage===",file.toString());

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
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









































