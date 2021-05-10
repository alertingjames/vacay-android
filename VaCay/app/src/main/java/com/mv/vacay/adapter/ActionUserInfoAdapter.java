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
import com.mv.vacay.main.inbox.UserProfileActivity;
import com.mv.vacay.main.meetfriends.ActionProfileActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.10.24.
 */
public class ActionUserInfoAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<UserEntity> _userDatas = new ArrayList<>();
    private ArrayList<UserEntity> _allUserDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ActionUserInfoAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<UserEntity> users) {

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
            convertView = inflater.inflate(R.layout.run_list_item, parent, false);

            holder.imvPhotonet = (CircularNetworkImageView) convertView.findViewById(R.id.imv_photo_net);
            holder.imvPhoto = (CircularImageView) convertView.findViewById(R.id.imv_photo);
            holder.txvName = (TextView) convertView.findViewById(R.id.txv_name);
            holder.txvcity = (TextView) convertView.findViewById(R.id.txv_city);
            holder.txvjob=(TextView)convertView.findViewById(R.id.txv_job);
            holder.txveducation=(TextView)convertView.findViewById(R.id.txv_education);
            holder.txvinterest=(TextView)convertView.findViewById(R.id.txv_interest);
            holder.txvages=(TextView)convertView.findViewById(R.id.txv_age_range);
            holder.message=(ImageView)convertView.findViewById(R.id.message_mark);
            holder.txvrelations=(TextView)convertView.findViewById(R.id.txv_relationship);
            holder.millennial=(TextView)convertView.findViewById(R.id.txv_millennial);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _userDatas.get(position);

        holder.txvName.setText(user.get_firstName()+" "+user.get_lastName());
        holder.txvcity.setText(user.get_city());
        holder.txvjob.setText(user.get_job());
        holder.txveducation.setText(user.get_education());
        holder.txvinterest.setText(user.get_interest());
        holder.txvages.setText(String.valueOf(user.get_age_range()));

        if(user.get_relations().contains("common")) {
            user.set_relations("-"+user.get_relations().replace("\ncommon", "").replace("\n","\n-"));
        }

        holder.txvrelations.setText(user.get_relations());

//        if(user.is_message_flag()) holder.message.setVisibility(View.VISIBLE);
//        else holder.message.setVisibility(View.INVISIBLE);

        if (user.get_photoUrl().length() <1000) {
            holder.imvPhotonet.setVisibility(View.VISIBLE);
            holder.imvPhotonet.setImageUrl(user.get_photoUrl(),_imageLoader);
        }
        else {
            holder.imvPhotonet.setVisibility(View.GONE);
            holder.imvPhoto.setImageBitmap(base64ToBitmap(user.get_photoUrl()));
        }

        if (user.getMillennial().length() > 0) {
            holder.millennial.setVisibility(View.VISIBLE);
            holder.millennial.setText(user.getMillennial());
        }
        else {
            holder.millennial.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.userEntity=user;
                if(Commons._inboxUserSearch){
                    Intent intent=new Intent(_context,UserProfileActivity.class);
                    _context.startActivity(intent);

                }else {
                    Intent intent=new Intent(_context,ActionProfileActivity.class);
                    _context.startActivity(intent);
                }


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

        _userDatas.clear();

        if(charText.length() == 0){
            _userDatas.addAll(_allUserDatas);
        }else {

            for (UserEntity user : _allUserDatas){

                if (user instanceof UserEntity) {

                    String value = ((UserEntity) user).get_fullName().toLowerCase();
                    if (value.contains(charText)) {
                        _userDatas.add(user);
                    }else {
                        String value1 = ((UserEntity) user).get_email().toLowerCase();
                        if (value1.contains(charText)) {
                            _userDatas.add(user);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
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

        public CircularNetworkImageView imvPhotonet;
        public CircularImageView imvPhoto;
        public TextView txvName;
        public TextView txvcity;
        public TextView txvjob;
        public TextView txveducation;
        public TextView txvinterest;
        public TextView txvages;
        public TextView txvrelations;
        public TextView millennial;
        public ImageView message;
    }
}

