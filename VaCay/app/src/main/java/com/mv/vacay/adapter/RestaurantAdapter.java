package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.restaurant.RestaurantMenuDetailActivity;
import com.mv.vacay.models.RestaurantEntity;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.06.
 */
public class RestaurantAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<RestaurantEntity> _datas = new ArrayList<>();
    private ArrayList<RestaurantEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public RestaurantAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<RestaurantEntity> datas) {

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
            convertView = inflater.inflate(R.layout.item_food_list, parent, false);

            holder.imvPhoto = (ImageView) convertView.findViewById(R.id.food_image);
            holder.imvPhotonet = (NetworkImageView) convertView.findViewById(R.id.food_image_net);
            holder.resname = (TextView) convertView.findViewById(R.id.restaurantname);
            holder.type = (TextView) convertView.findViewById(R.id.restauranttype);
            holder.resmenu=(TextView)convertView.findViewById(R.id.restaurantmenu);
            holder.opentable=(TextView)convertView.findViewById(R.id.opentable);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final RestaurantEntity restaurantEntity = (RestaurantEntity) _datas.get(position);

        holder.resname.setText(restaurantEntity.getRestaurant_name());
        holder.type.setText(restaurantEntity.getRestaurant_type());
        holder.resmenu.setText(restaurantEntity.getFood_menu_url());
        holder.opentable.setText(restaurantEntity.getOpentable_url());


        if (restaurantEntity.get_photoUrl().length() > 0) {
            holder.imvPhotonet.setVisibility(View.VISIBLE);
            holder.imvPhoto.setVisibility(View.GONE);
            holder.imvPhotonet.setImageUrl(restaurantEntity.get_photoUrl(),_imageLoader);
        }
        else if(restaurantEntity.getImageRes()!=0) {
            holder.imvPhotonet.setVisibility(View.GONE);
            holder.imvPhoto.setVisibility(View.VISIBLE);
            holder.imvPhoto.setImageResource(restaurantEntity.getImageRes());
        }
        else {
            holder.imvPhotonet.setVisibility(View.GONE);
            holder.imvPhoto.setVisibility(View.VISIBLE);
            holder.imvPhoto.setImageBitmap(restaurantEntity.getImageBitmap());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _context.gotoUserProfile(_context,user);
                Intent intent=new Intent(_context,RestaurantMenuDetailActivity.class);
                Commons.restaurantEntity=restaurantEntity;
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

            for (RestaurantEntity restaurantEntity : _alldatas){

                if (restaurantEntity instanceof RestaurantEntity) {

                    String value = ((RestaurantEntity) restaurantEntity).getRestaurant_name().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(restaurantEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resorting(ArrayList<RestaurantEntity> entities){
        ArrayList<RestaurantEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            RestaurantEntity restaurantEntity=new RestaurantEntity();
            restaurantEntity=entities.get(i);
            datas.add(restaurantEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    class CustomHolder {

        public ImageView imvPhoto;
        public NetworkImageView imvPhotonet;
        public TextView resname;
        public TextView type;
        public TextView resmenu;
        public TextView opentable;
        public TextView resloc;
    }
}



