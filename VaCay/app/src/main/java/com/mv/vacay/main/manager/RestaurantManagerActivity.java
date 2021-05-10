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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.commons.Constants;
import com.mv.vacay.commons.ReqConst;
import com.mv.vacay.main.beauty.BeautyServiceRequestActivity;
import com.mv.vacay.main.restaurant.FoodEntryActivity;
import com.mv.vacay.models.RestaurantEntity;
import com.mv.vacay.utils.BitmapUtils;
import com.mv.vacay.utils.MultiPartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class RestaurantManagerActivity extends BaseActivity {

    ImageView foodImage,locButton,back;
    TextView resName,resmenu,tablemenu,okButton,restaurant, beauty;
    LinearLayout spinner,spinner_area;
    private Uri _imageCaptureUri;
    String _photoPath = "";
    Bitmap bitmap=null;
    String categoryStr="",area="";
    int area_id=0;
    String locationUrl="";
    private int _idx = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_manager);

        foodImage=(ImageView)findViewById(R.id.food_image);
        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });
        locButton=(ImageView)findViewById(R.id.loc_button);
        locButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSelectLocation();
            }
        });
        spinner=(LinearLayout)findViewById(R.id.lyt_spin);
        spinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoiceDialog();
            }
        });

        spinner_area=(LinearLayout)findViewById(R.id.lyt_spin_area);
        spinner_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChoiceDialog_area();
            }
        });

        resmenu=(EditText)findViewById(R.id.edt_menu);
        tablemenu=(EditText)findViewById(R.id.edt_opentable);
        resName=(EditText)findViewById(R.id.resname);
        okButton=(TextView)findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try{
                   if(resName.getText().length()!=0 && resmenu.getText().length()!=0 && tablemenu.getText().length()!=0 && categoryStr!="" && area!="" && !bitmap.equals(null)) {
                       showChangeLangDialog_RestaurantManager();
                   }else showToast("Please input all items.");
               }catch (NullPointerException e){
                   showToast("Please input all items.");
               }
            }
        });
        restaurant=(TextView)findViewById(R.id.restaurant);
        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        beauty=(TextView)findViewById(R.id.beauty);
        beauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),BeautyManagerPageActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0,0);
            }
        });
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

    }



    public void selectPhoto() {

        final String[] items = {"Take photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public void showChoiceDialog() {

        final String[] items = {"Asian",
                "English",
                "Farm and Table",
                "French",
                "Italian",
                "Mediterranean",
                "Mexican",
                "Modern Pub",
                "Modern Steakhouse",
                "Napa Style Food & Wine",
                "New American",
                "New Seafood",
                "Steakhouse",
                "Sushi"
        };

        final EditText category=(EditText) findViewById(R.id.edt_category);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select restaurant type");
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
        builder.setTitle("Select restaurant area");
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
                area_id=item+1;
                area=items[item];
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showChangeLangDialog_RestaurantManager() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_edit_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.customView);
        edt.setText(Commons.loc_url);

        dialogBuilder.setTitle("Restaurant location URL");
        dialogBuilder.setMessage("Please confirm this URL and write a new one unless it is correct");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
                if(edt.getText().length()!=0) {
                    RestaurantEntity restaurantEntity=new RestaurantEntity();
                    restaurantEntity.setRestaurant_location_url(edt.getText().toString());
                    restaurantEntity.setOpentable_url(tablemenu.getText().toString());
                    restaurantEntity.setFood_menu_url(resmenu.getText().toString());
                    restaurantEntity.setRestaurant_type(categoryStr);
                    restaurantEntity.set_area(area);
                    restaurantEntity.setImageBitmap(Commons.bitmap);
                    restaurantEntity.setImageRes(0);
                    restaurantEntity.setRestaurant_name(resName.getText().toString());
                    restaurantEntity.set_photoUrl(_photoPath);

                    Commons.restaurantEntities.add(restaurantEntity);

                    locationUrl=edt.getText().toString();
                    registerRestaurantInfo1();

//                    showToast("Your new restaurant item uploaded successfully.");
//                    Intent intent=new Intent(getApplicationContext(), FoodEntryActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(R.anim.left_in,R.anim.right_out);
                }
                else showToast("Input the restaurant location url.");
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

    private  void confirmSelectLocation() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to search & select this restaurant's location?");
        builder.setMessage("You can search or select the location on the map.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(getApplicationContext(),SelectLocationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in,R.anim.left_out);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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

                        in.close();

                        //set The bitmap data to image View
                        Commons.bitmap=bitmap;
                        foodImage.setImageBitmap(bitmap);

                        //           Constants.userphoto=ui_imvphoto.getDrawable();
                        _photoPath = saveFile.getAbsolutePath();   Log.d("ImagePath===>",_photoPath);

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

                 //   File saveFile = BitmapUtils.getOutputMediaFile(this);

                    File saveFile = new File(_photoPath);

                    InputStream in = getContentResolver().openInputStream(Uri.fromFile(saveFile));
                    BitmapFactory.Options bitOpt = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeStream(in, null, bitOpt);

                    in.close();

                    //set The bitmap data to image View
                    Commons.bitmap=bitmap;
                    foodImage.setImageBitmap(bitmap);

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

//    public void registerRestaurantInfo() {
//
//        String url = ReqConst.SERVER_URL + ReqConst.REQ_RESTAURANT;
//
//        try {
//            String restaurant_name = resName.getText().toString().replace("'","%26").replace(" ","%20");
//            //       firstname = URLEncoder.encode(firstname, "utf-8");
//            String restaurant_type = categoryStr.replace(" & ","%26").replace(" ","%20");
//            //       lastname = URLEncoder.encode(lastname, "utf-8");
//            String restaurant_area = String.valueOf(area_id).replace(" ","%20");
//            //        email = URLEncoder.encode(email, "utf-8");
//
//
//            String params = String.format("/%s/%s/%s",restaurant_name,restaurant_type, restaurant_area);
//
//            url += params;
//            Log.d("API====",url);
//
//        } catch (Exception ex) {
//            return;
//        }
//
//        showProgress();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String json) {
//
//                parseRegisterResponse(json);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//                closeProgress();
//                showAlertDialog(getString(R.string.error));
//            }
//        });
//
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
//                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//        VaCayApplication.getInstance().addToRequestQueue(stringRequest, url);
//
//    }
//
//    public void parseRegisterResponse(String json) {
//
//        Log.d("JsonREST====",json);
//        try {
//            JSONObject response = new JSONObject(json);
//
//            int result_code = response.getInt(ReqConst.RES_CODE);
//
//            Log.d("result===",String.valueOf(result_code));
//
//            if (result_code == ReqConst.CODE_SUCCESS) {
//
//                _idx = response.getInt(ReqConst.RES_RESTID);   Log.d("idx====",String.valueOf(_idx));
//
//                registerRestaurantInfo2();
//
// //               showAlertDialog("Successfully registered on server.");
//
//            } else if (result_code == ReqConst.CODE_EXISTEMAIL) {
//
//                closeProgress();
//                showAlertDialog(getString(R.string.email_exist));
//            } else {
//                closeProgress();
//                showAlertDialog(getString(R.string.register_fail));
//            }
//
//        } catch (JSONException e) {
//            closeProgress();
//            showAlertDialog(getString(R.string.register_fail));
//
//            e.printStackTrace();
//        }
//
//    }

    public void registerRestaurantInfo1() {

        String url = ReqConst.SERVER_URL + ReqConst.REQ_RESTAURANT;

        Log.d("request url :", url.toString());

        Map<String, String> params = new HashMap<>();

        params.put("rest_name", resName.getText().toString());
        params.put("rest_type", categoryStr);
        params.put("area_id", String.valueOf(area_id));
//        params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(0));
        params.put(ReqConst.PARAM_MENUURL, resmenu.getText().toString());
        params.put(ReqConst.PARAM_OPENTABLEURL, tablemenu.getText().toString());
        params.put(ReqConst.PARAM_LOCATIONURL, locationUrl);

        Log.d("REST params=====> :", params.toString());

        showProgress();


        StringRequest post = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                Log.d("REST response========>", response);

                VolleyLog.v("Response:%n %s", response.toString());

                parseRestUrlsResponse(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("debug", error.toString());
                closeProgress();
                showToast(getString(R.string.error));

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("rest_name", resName.getText().toString());
                params.put("rest_type", categoryStr);
                params.put("area_id", String.valueOf(area_id));
//        params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(0));
                params.put(ReqConst.PARAM_MENUURL, resmenu.getText().toString());
                params.put(ReqConst.PARAM_OPENTABLEURL, tablemenu.getText().toString());
                params.put(ReqConst.PARAM_LOCATIONURL, locationUrl);

                return params;
            }
        };

        post.setRetryPolicy(new DefaultRetryPolicy(Constants.VOLLEY_TIME_OUT,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VaCayApplication.getInstance().addToRequestQueue(post, url);


    }

    public void parseRestUrlsResponse(String json) {

        try {

            JSONObject response = new JSONObject(json);   Log.d("response=====> :",response.toString());

            String success = response.getString(ReqConst.RES_CODE);

            Log.d("resultcode=====> :",success);

            if (success==String.valueOf(0)) {

                _idx = response.getInt(ReqConst.RES_RESTID);
                uploadImage();
//                showAlertDialog("Successfully registered on server.");

            } else {

                String error = response.getString(ReqConst.RES_ERROR);
                closeProgress();
//                showAlertDialog(getString(R.string.error));
                showToast(error);
            }

        } catch (JSONException e) {
            closeProgress();
            e.printStackTrace();

            showToast(getString(R.string.error));
        }
    }

    public void uploadImage() {

        try {

            File file = new File(_photoPath);     //     /storage/emulated/0/DCIM/Camera/20170115_141323.mp4
    //        File file = new File("/storage/emulated/0/DCIM/Camera/20170115_141323.mp4");

            final Map<String, String> params = new HashMap<>();
            params.put(ReqConst.PARAM_ID, String.valueOf(_idx));
            params.put(ReqConst.PARAM_IMAGETYPE, String.valueOf(2));

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
                    Log.d("imageJson===",json.toString());
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
            Log.d("resultFF===",String.valueOf(result_code));

            if (result_code == 0) {
                showToast("Successfully your restaurant info registered on server.");

                Intent intent=new Intent(getApplicationContext(), FoodEntryActivity.class);
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


























