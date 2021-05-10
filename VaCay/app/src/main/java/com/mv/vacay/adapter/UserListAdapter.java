package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.meetfriends.ChatActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 4/27/2017.
 */

public class UserListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<UserEntity> _datas = new ArrayList<>();
    private ArrayList<UserEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public UserListAdapter(Context context){

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

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_user_list_item, parent, false);

            holder.photo = (CircularNetworkImageView) convertView.findViewById(R.id.photo);
            holder.name=(TextView)convertView.findViewById(R.id.name);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _datas.get(position);

        holder.name.setText(user.get_fullName());

        if(user.get_photoUrl().length()>0) {
            holder.photo.setImageUrl(user.get_photoUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.userEntity=user;
                Intent intent=new Intent(_context, ChatActivity.class);
                _context.startActivity(intent);
            }
        });



        return convertView;
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
                    if (value.contains(charText)) {
                        _datas.add(userEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public CircularNetworkImageView photo;
        public TextView name;
    }
}



