package com.mv.vacay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.models.MessageEntity;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.05.
 */
public class MessageAdapter extends BaseAdapter {
    Context _context;
    ArrayList<MessageEntity> _datas = new ArrayList<>();

    public MessageAdapter(Context context,ArrayList<MessageEntity> datas){
        super();
        this._context = context;
        this._datas=datas;
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

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_message, parent, false);

            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.message=(TextView)convertView.findViewById(R.id.message);
            holder.delete= (TextView)convertView.findViewById(R.id.delete);
            holder.date= (TextView)convertView.findViewById(R.id.date);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final MessageEntity entity = _datas.get(position);

        holder.name.setText(String.valueOf(entity.get_userfullname()));
        holder.email.setText("To: "+String.valueOf(entity.get_useremail()));
        holder.message.setText(String.valueOf(entity.get_usermessage()));
        holder.date.setText(String.valueOf(entity.get_request_date()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        public TextView date;
    }
}

