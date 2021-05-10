package com.mv.vacay.adapter;

/**
 * Created by a on 2016.12.24.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mv.vacay.R;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.05.
 */
public class MenuListAdapter extends BaseAdapter {
    Context _context;
    ArrayList<String> _datas = new ArrayList<>();

    public MenuListAdapter(Context context,ArrayList<String> datas){
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
    public String getItem(int position){
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
            convertView = inflater.inflate(R.layout.menu_list_item, parent, false);

            holder.menuUrl = (TextView) convertView.findViewById(R.id.menuUrl);

            convertView.setTag(holder);

        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final String menu = _datas.get(position);

        holder.menuUrl.setText(menu);

        return convertView;
    }
    public void resorting(ArrayList<String> entities){
        ArrayList<String> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            String url="";
            url=entities.get(i);
            datas.add(url);
        }
        entities.clear();
        entities.addAll(datas);
    }

    public class CustomHolder {
        public TextView menuUrl;
    }
}


