package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.meetfriends.ShowMessageActivity;
import com.mv.vacay.main.watercooler.CommentActivity;
import com.mv.vacay.main.watercooler.WatercoolerViewActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.models.WaterCoolerEntity;
import com.mv.vacay.preference.PrefConst;
import com.mv.vacay.preference.Preference;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by sonback123456 on 12/2/2017.
 */

public class WaterCoolerListAdapter extends BaseAdapter {
    Context _context;
    ArrayList<WaterCoolerEntity> _datas = new ArrayList<>();
    ArrayList<WaterCoolerEntity> _alldatas = new ArrayList<>();
    ImageLoader _imageLoader;

    public WaterCoolerListAdapter(Context context){
        super();
        this._context = context;
        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<WaterCoolerEntity> datas) {
        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public WaterCoolerEntity getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.watercooler_item, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.category = (TextView) convertView.findViewById(R.id.category);
            holder.content=(TextView)convertView.findViewById(R.id.content);
            holder.link= (TextView)convertView.findViewById(R.id.link);
            holder.photonet=(CircularNetworkImageView)convertView.findViewById(R.id.photo_net);
            holder.photo=(CircularImageView)convertView.findViewById(R.id.photo);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final WaterCoolerEntity entity = _datas.get(position);

        if(entity.getProfilePhotoUrl().length()<1000){
            holder.photonet.setVisibility(View.VISIBLE);
            holder.photonet.setImageUrl(entity.getProfilePhotoUrl(),_imageLoader);
        }else {
            holder.photonet.setVisibility(View.GONE);
            holder.photo.setImageBitmap(base64ToBitmap(entity.getProfilePhotoUrl()));
        }

        holder.name.setText(String.valueOf(entity.getUserName()));
        holder.content.setText(String.valueOf(entity.getContent()));
        holder.category.setText(String.valueOf(entity.getCategory()));
        if(entity.getArticle().length()>0) {
            holder.link.setVisibility(View.VISIBLE);
            holder.link.setText(String.valueOf(entity.getArticle()));
        }else holder.link.setVisibility(View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, CommentActivity.class);
                Commons.waterCoolerEntity=entity;
                _context.startActivity(intent);
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

            for (WaterCoolerEntity waterCoolerEntity : _alldatas){

                if (waterCoolerEntity instanceof WaterCoolerEntity) {

                    String value = ((WaterCoolerEntity) waterCoolerEntity).getUserName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(waterCoolerEntity);
                    }else {
                        String value1 = ((WaterCoolerEntity) waterCoolerEntity).getContent().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(waterCoolerEntity);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class CustomHolder {
        public TextView name;
        public TextView category;
        public TextView content;
        public TextView link;
        public CircularNetworkImageView photonet;
        public CircularImageView photo;
    }
}


