package com.mv.vacay.adapter;

/**
 * Created by a on 3/27/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.models.ProviderScheduleEntity;

import java.util.ArrayList;
/**
 * Created by a on 3/25/2017.
 */

public class ProviderScheduleListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<ProviderScheduleEntity> _datas = new ArrayList<>();
    private ArrayList<ProviderScheduleEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ProviderScheduleListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<ProviderScheduleEntity> datas) {

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
    public View getView(final int position, View convertView, ViewGroup parent){

        CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.date_time_list, parent, false);

            holder.start=(TextView)convertView.findViewById(R.id.start);
            holder.end=(TextView)convertView.findViewById(R.id.end);
            holder.comment=(TextView)convertView.findViewById(R.id.comment);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final ProviderScheduleEntity dateTime = (ProviderScheduleEntity) _datas.get(position);

        holder.start.setText(dateTime.getScheduleStart());
        holder.end.setText(dateTime.getScheduleEnd());
        holder.comment.setText(dateTime.getScheduleComment());
        if(dateTime.getScheduleComment().length()==0)
            holder.comment.setVisibility(View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return convertView;
    }

    class CustomHolder {

        public TextView start;
        public TextView end;
        public TextView comment;
    }


}

