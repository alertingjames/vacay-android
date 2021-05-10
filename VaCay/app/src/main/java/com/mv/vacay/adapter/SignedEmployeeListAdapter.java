package com.mv.vacay.adapter;

/**
 * Created by a on 4/11/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.models.SignedEmployeeEntity;

import java.util.ArrayList;

/**
 * Created by a on 3/25/2017.
 */

public class SignedEmployeeListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<SignedEmployeeEntity> _datas = new ArrayList<>();
    private ArrayList<SignedEmployeeEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public SignedEmployeeListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<SignedEmployeeEntity> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.company_list, parent, false);

            holder.photo = (ImageView) convertView.findViewById(R.id.brandImage);
            holder.photoNet = (NetworkImageView) convertView.findViewById(R.id.brandImageNet);
            holder.name=(TextView)convertView.findViewById(R.id.companyName);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final SignedEmployeeEntity signedEmployeeEntity = (SignedEmployeeEntity) _datas.get(position);

        holder.name.setText(signedEmployeeEntity.getName());

        if(signedEmployeeEntity.getPhotoUrl().length()<1000) {
            holder.photoNet.setVisibility(View.VISIBLE);
            holder.photoNet.setImageUrl(signedEmployeeEntity.getPhotoUrl(),_imageLoader);
        }else {
            holder.photoNet.setVisibility(View.GONE);
            holder.photo.setImageBitmap(base64ToBitmap(signedEmployeeEntity.getPhotoUrl()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        return convertView;
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (SignedEmployeeEntity signedEmployeeEntity : _alldatas){

                if (signedEmployeeEntity instanceof SignedEmployeeEntity) {

                    String value = ((SignedEmployeeEntity) signedEmployeeEntity).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(signedEmployeeEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public NetworkImageView photoNet;
        public ImageView photo;
        public TextView name;
    }
}


