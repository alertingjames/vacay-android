package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.beautymen.BookingResponseActivity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.05.
 */
public class BookedMessageListAdapter extends BaseAdapter {
    Context _context;
    ArrayList<MessageEntity> _datas = new ArrayList<>();
    ArrayList<MessageEntity> _alldatas = new ArrayList<>();
    ImageLoader _imageLoader;

    public BookedMessageListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<MessageEntity> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

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
            convertView = inflater.inflate(R.layout.booking_message_list_item, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.date=(TextView)convertView.findViewById(R.id.request_date);
            holder.service= (TextView)convertView.findViewById(R.id.service);
            holder.accept=(TextView)convertView.findViewById(R.id.accept);
            holder.decline= (TextView)convertView.findViewById(R.id.decline);
            holder.senderphotoNet=(CircularNetworkImageView)convertView.findViewById(R.id.sender_photo_net);
            holder.senderphoto=(CircularImageView)convertView.findViewById(R.id.sender_photo);
            holder.buttonBar = (LinearLayout)convertView.findViewById(R.id.buttonBar);
            holder.status = (TextView)convertView.findViewById(R.id.status);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final MessageEntity entity = _datas.get(position);

        if(entity.get_photoUrl().length()<1000) {
            holder.senderphotoNet.setVisibility(View.VISIBLE);
            holder.senderphotoNet.setImageUrl(entity.get_photoUrl(), _imageLoader);
        }
        else {
            holder.senderphotoNet.setVisibility(View.GONE);
            holder.senderphoto.setImageBitmap(base64ToBitmap(entity.get_photoUrl()));
        }

        holder.name.setText(String.valueOf(entity.get_userfullname()));
        holder.email.setText(String.valueOf(entity.get_useremail()));
        holder.date.setText(String.valueOf(entity.get_request_date()));
        holder.service.setText(String.valueOf(entity.get_service()));

        if(entity.get_status().equals("accepted") || entity.get_status().equals("declined")) {
            holder.buttonBar.setVisibility(View.GONE);
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(String.valueOf(entity.get_status()));
        }
        else {
            holder.buttonBar.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
        }

        holder.accept.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        holder.accept.setBackgroundResource(R.drawable.blue_fill_white_stroke);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        holder.accept.setBackgroundResource(R.drawable.green_fillround);
                        Commons.messageEntity=entity;
                        Intent intent=new Intent(_context, BookingResponseActivity.class);
                        intent.putExtra("status","accept");
                        _context.startActivity(intent);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        holder.accept.getBackground().clearColorFilter();
                        holder.accept.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        holder.decline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        holder.decline.setBackgroundResource(R.drawable.green_fillround);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        holder.decline.setBackgroundResource(R.drawable.blue_thin_stroke);
                        Commons.messageEntity=entity;
                        Intent intent=new Intent(_context, BookingResponseActivity.class);
                        intent.putExtra("status","decline");
                        _context.startActivity(intent);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        holder.decline.getBackground().clearColorFilter();
                        holder.decline.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.messageEntity=entity;
                Intent intent=new Intent(_context, BookingResponseActivity.class);
                intent.putExtra("status","accept");
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

            for (MessageEntity messageEntity : _alldatas){

                if (messageEntity instanceof MessageEntity) {

                    String value = ((MessageEntity) messageEntity).get_userfullname().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(messageEntity);
                    }else {
                        value = ((MessageEntity) messageEntity).get_service().toLowerCase();
                        if (value.contains(charText)) {
                            _datas.add(messageEntity);
                        }else {
                            value = ((MessageEntity) messageEntity).get_request_date().toLowerCase();
                            if (value.contains(charText)) {
                                _datas.add(messageEntity);
                            }
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }


    public class CustomHolder {
        public TextView name;
        public TextView email;
        public TextView date;
        public TextView service;
        public TextView accept;
        public TextView decline;
        public TextView status;
        public CircularNetworkImageView senderphotoNet;
        public CircularImageView senderphoto;
        public LinearLayout buttonBar;
    }
}

