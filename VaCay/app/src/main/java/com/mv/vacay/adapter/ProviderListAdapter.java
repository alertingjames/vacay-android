package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.main.payment.VerifyPaymentActivity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.10.24.
 */
public class ProviderListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<BeautyServiceEntity> _userDatas = new ArrayList<>();
    private ArrayList<BeautyServiceEntity> _allUserDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ProviderListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<BeautyServiceEntity> users) {

        _allUserDatas = users;
        _userDatas.clear();
        _userDatas.addAll(_allUserDatas);
    }

    @Override
    public int getCount(){
        return _userDatas.size();
    }

    @Override
    public Object getItem(int position){
        return _userDatas.get(position);
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
            convertView = inflater.inflate(R.layout.providerlist, parent, false);

            holder.providerPhoto = (CircularNetworkImageView) convertView.findViewById(R.id.provider_profile);
            holder.providerPhoto0=(CircularImageView)convertView.findViewById(R.id.provider_profile0);
            holder.providerEmail = (TextView) convertView.findViewById(R.id.providerEmail);
            holder.providerName = (TextView) convertView.findViewById(R.id.provider_name);
            holder.token = (TextView) convertView.findViewById(R.id.token);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final BeautyServiceEntity beautyEntity = (BeautyServiceEntity) _userDatas.get(position);

        holder.providerName.setText(beautyEntity.getFullName());
        holder.providerEmail.setText(beautyEntity.getEmail());
        if(beautyEntity.getToken().length()>0){
            holder.token.setText("Verified Payment");
            holder.token.setTextColor(Color.WHITE);
        }
        else {
            holder.token.setTextColor(Color.RED);
            holder.token.setText("Unverified Payment");
        }

        if (beautyEntity.getProviderPhotoUrl().length() > 1000) {
            holder.providerPhoto.setVisibility(View.GONE);
            holder.providerPhoto0.setImageBitmap(base64ToBitmap(beautyEntity.getProviderPhotoUrl()));
        }
        else {
            holder.providerPhoto.setVisibility(View.VISIBLE);
            holder.providerPhoto.setImageUrl(beautyEntity.getProviderPhotoUrl(),_imageLoader);
        }
        holder.providerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.resId = beautyEntity.getProviderResPhoto();
                Commons.photoUrl = beautyEntity.getProviderPhotoUrl();
                Intent intent = new Intent(_context, ViewActivity.class);
                _context.startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(beautyEntity.getToken().length()==0){
                    Intent intent = new Intent(_context, VerifyPaymentActivity.class);
                    Log.d("ProID===>",String.valueOf(beautyEntity.get_proIdx()));
                    intent.putExtra("proid",String.valueOf(beautyEntity.get_proIdx()));
                    intent.putExtra("proemail",beautyEntity.getEmail());
                    intent.putExtra("proName",beautyEntity.getFullName());
                    intent.putExtra("proFirst",beautyEntity.getFirstName());
                    intent.putExtra("proLast",beautyEntity.getLastName());
                    intent.putExtra("proPhotoUrl",beautyEntity.getProviderPhotoUrl());

                    _context.startActivity(intent);
                }else Toast.makeText(_context,"This provider's payment already is verified",Toast.LENGTH_SHORT).show();

            }
        });



        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();

        _userDatas.clear();

        if(charText.length() == 0){
            _userDatas.addAll(_allUserDatas);
        }else {

            for (BeautyServiceEntity user : _allUserDatas){

                if (user instanceof BeautyServiceEntity) {

                    String value = ((BeautyServiceEntity) user).getFullName().toLowerCase();
                    if (value.contains(charText)) {
                        _userDatas.add(user);
                    }else {
                        String value1 = ((BeautyServiceEntity) user).getEmail().toLowerCase();
                        if (value1.contains(charText)) {
                            _userDatas.add(user);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    public void resorting(ArrayList<UserEntity> entities){
        ArrayList<UserEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            UserEntity userEntity=new UserEntity();
            userEntity=entities.get(i);
            datas.add(userEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    class CustomHolder {

        public CircularImageView providerPhoto0;
        public CircularNetworkImageView providerPhoto;
        public TextView providerName;
        public TextView providerEmail;
        public TextView token;

    }
}

