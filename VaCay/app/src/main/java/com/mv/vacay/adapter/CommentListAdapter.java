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
import com.mv.vacay.models.CommentEntity;
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

public class CommentListAdapter extends BaseAdapter {
    Context _context;
    ArrayList<CommentEntity> _datas = new ArrayList<>();
    ArrayList<CommentEntity> _alldatas = new ArrayList<>();
    ImageLoader _imageLoader;

    public CommentListAdapter(Context context){
        super();
        this._context = context;
        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<CommentEntity> datas) {
        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public CommentEntity getItem(int position){
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
            convertView = inflater.inflate(R.layout.comment_list_item, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.content = (TextView) convertView.findViewById(R.id.commentText);
            holder.photonet=(CircularNetworkImageView)convertView.findViewById(R.id.imageNet);
            holder.photo=(CircularImageView)convertView.findViewById(R.id.image);
            holder.image=(ImageView)convertView.findViewById(R.id.commentImage);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final CommentEntity entity = _datas.get(position);

        if(entity.getPhotoUrl().length()<1000){
            holder.photonet.setVisibility(View.VISIBLE);
            holder.photonet.setImageUrl(entity.getPhotoUrl(),_imageLoader);
        }else {
            holder.photonet.setVisibility(View.GONE);
            holder.photo.setImageBitmap(base64ToBitmap(entity.getPhotoUrl()));
        }

        holder.name.setText(String.valueOf(entity.getName()));
        if(entity.getText().length()>0)
            holder.content.setText(String.valueOf(entity.getText()));
        else holder.content.setVisibility(View.GONE);
        if(entity.getImageUrl().length()>1000)
            holder.image.setImageBitmap(base64ToBitmap(entity.getImageUrl()));
        else holder.image.setVisibility(View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

            for (CommentEntity commentEntity : _alldatas){

                if (commentEntity instanceof CommentEntity) {

                    String value = ((CommentEntity) commentEntity).getName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(commentEntity);
                    }else {
                        String value1 = ((CommentEntity) commentEntity).getText().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(commentEntity);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public class CustomHolder {
        public TextView name;
        public TextView content;
        public CircularNetworkImageView photonet;
        public CircularImageView photo;
        public ImageView image;
    }
}


