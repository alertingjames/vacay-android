package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 4/27/2017.
 */

public class ChatUserListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<UserEntity> _datas = new ArrayList<>();
    private ArrayList<UserEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ChatUserListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<UserEntity> datas) {

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

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_user_list_item, parent, false);

            holder.photo = (CircularNetworkImageView) convertView.findViewById(R.id.photo);
            holder.photo2 = (CircularImageView) convertView.findViewById(R.id.photo2);
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.email=(TextView)convertView.findViewById(R.id.email);
            holder.num=(TextView)convertView.findViewById(R.id.num);
            holder.badge=(ImageView)convertView.findViewById(R.id.badge);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _datas.get(position);

        if(user.get_name().length()>0)
            holder.name.setText(user.get_name());
        else
            holder.name.setText(user.get_fullName());

        holder.email.setVisibility(View.VISIBLE);
        holder.email.setText(user.get_email());

        if(user.get_num().equals("1")) {
            holder.badge.setVisibility(View.VISIBLE);
        }
        else {
            holder.badge.setVisibility(View.GONE);
        }
        Log.d("Flag===>",String.valueOf(user.is_message_flag()));

        if(user.get_photoUrl().length()<1000) {
            holder.photo.setImageUrl(user.get_photoUrl(),_imageLoader);
        }else {
            holder.photo2.setVisibility(View.VISIBLE);
            holder.photo2.setImageBitmap(base64ToBitmap(user.get_photoUrl()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.userEntity=new UserEntity();
                Commons.userEntity=user;
                holder.badge.setVisibility(View.GONE);

                Intent intent = new Intent(_context, ChatActivity.class);
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

            for (UserEntity userEntity : _alldatas){

                if (userEntity instanceof UserEntity) {

                    String value = ((UserEntity) userEntity).get_fullName().toLowerCase();
                    String value1 = ((UserEntity) userEntity).get_name().toLowerCase();
                    if (value.contains(charText) || value1.contains(charText)) {
                        _datas.add(userEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public CircularNetworkImageView photo;
        public CircularImageView photo2;
        public TextView name;
        public TextView email;
        public TextView num;
        public ImageView badge;
    }
}




