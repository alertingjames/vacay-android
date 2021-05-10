package com.mv.vacay.adapter;

import android.content.Context;
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
import com.mv.vacay.main.carpediem.VideoDisplayActivity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.05.
 */
public class TVWatchingAdapter extends BaseAdapter {
    VideoDisplayActivity _context;
    ArrayList<MessageEntity> _datas = new ArrayList<>();
    ImageLoader _imageLoader;

    public TVWatchingAdapter(VideoDisplayActivity context,ArrayList<MessageEntity> datas){
        super();
        this._context = context;
        this._datas=datas;
        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

//    public void setDatas(Entity entity) {
//        _datas.add(entity);
//    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public MessageEntity getItem(int position){
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
            convertView = inflater.inflate(R.layout.inboxmessage, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.email.setVisibility(View.GONE);
            holder.message=(TextView)convertView.findViewById(R.id.message);
            holder.delete= (TextView)convertView.findViewById(R.id.delete);
            holder.delete.setVisibility(View.INVISIBLE);
            holder.message1=(ImageView)convertView.findViewById(R.id.message_mark);
            holder.senderphoto=(CircularNetworkImageView)convertView.findViewById(R.id.sender_photo);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final MessageEntity entity = _datas.get(position);

        if(entity.get_photoUrl().length()>0)holder.senderphoto.setImageUrl(entity.get_photoUrl(),_imageLoader);
        holder.name.setText(String.valueOf(entity.get_userfullname()));
    //    holder.email.setText(String.valueOf(entity.get_useremail()));
        holder.message.setText(String.valueOf(entity.get_usermessage()));

        if(entity.is_message_flag()) holder.message1.setVisibility(View.VISIBLE);
        else holder.message1.setVisibility(View.INVISIBLE);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.messageEntity=entity;
                _context.showTVWatchingPersonComments();
            }
        });

        return convertView;
    }
    public void resorting(ArrayList<MessageEntity> entities){
        ArrayList<MessageEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            MessageEntity messageEntity=new MessageEntity();
            messageEntity=entities.get(i);
            datas.add(messageEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    public class CustomHolder {
        public TextView name;
        public TextView email;
        public TextView message;
        public TextView delete;
        public CircularNetworkImageView senderphoto;
        public ImageView message1;
    }
}

