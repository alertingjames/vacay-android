package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.beauty.BeautyServiceRequestActivity;
import com.mv.vacay.main.beauty.ProviderLocationViewActivity;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.models.BeautyEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.10.
 */
public class BeautyServiceAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<BeautyEntity> _beautyDatas = new ArrayList<>();
    private ArrayList<BeautyEntity> _allBeautyDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public BeautyServiceAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setbeautyDatas(ArrayList<BeautyEntity> beauties) {

        _allBeautyDatas = beauties;
        _beautyDatas.clear();
        _beautyDatas.addAll(_allBeautyDatas);
    }

    @Override
    public int getCount(){
        return _beautyDatas.size();
    }

    @Override
    public Object getItem(int position){
        return _beautyDatas.get(position);
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
            convertView = inflater.inflate(R.layout.item_beauty_detail_list, parent, false);

            holder.providerImage = (CircularImageView) convertView.findViewById(R.id.provider_profile);
            holder.providerImagenet = (CircularNetworkImageView) convertView.findViewById(R.id.provider_profile_net);
            holder.providerName = (TextView) convertView.findViewById(R.id.provider_name);
            holder.beautyName = (TextView) convertView.findViewById(R.id.beauty_name);
            holder.beautyPrice=(TextView)convertView.findViewById(R.id.beauty_price);
            holder.beautyDescription=(TextView)convertView.findViewById(R.id.description);
            holder.beautyCategory=(TextView)convertView.findViewById(R.id.beauty_category);
            holder.accept=(TextView)convertView.findViewById(R.id.accept_request);
            holder.beautyImage=(ImageView) convertView.findViewById(R.id.beauty_image);
            holder.beautyImagenet=(NetworkImageView) convertView.findViewById(R.id.beauty_image_net);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final BeautyEntity beautyEntity = (BeautyEntity) _beautyDatas.get(position);

        holder.beautyName.setText("Beauty name: "+beautyEntity.get_beauty_name());
        holder.providerName.setText(beautyEntity.get_provider_name());
        holder.beautyPrice.setText("Price: $"+beautyEntity.get_beauty_price());
        holder.beautyDescription.setText(beautyEntity.get_description());
        holder.beautyCategory.setText("Beauty category: "+beautyEntity.get_beauty_category());
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context,BeautyServiceRequestActivity.class);
                Commons.beautyEntity=beautyEntity;
                _context.startActivity(intent);
            }
        });
        holder.providerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, ProviderLocationViewActivity.class);
                Commons.beautyEntity=beautyEntity;
                showToast("Please wait.............");
                _context.startActivity(intent);
            }
        });
        holder.providerImagenet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, ProviderLocationViewActivity.class);
                Commons.beautyEntity=beautyEntity;
                showToast("Please wait.............");
                _context.startActivity(intent);
            }
        });

        holder.beautyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, ViewActivity.class);
                Commons.resId=beautyEntity.get_beauty_resId();
                Commons.photoUrl=beautyEntity.get_beauty_imageURL();
                _context.startActivity(intent);
            }
        });
        holder.beautyImagenet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context, ViewActivity.class);
                Commons.resId=beautyEntity.get_beauty_resId();
                Commons.photoUrl=beautyEntity.get_beauty_imageURL();
                _context.startActivity(intent);
            }
        });

        if (beautyEntity.get_beauty_imageURL().length() > 0) {
            holder.beautyImage.setVisibility(View.GONE);
            holder.beautyImagenet.setVisibility(View.VISIBLE);
            holder.beautyImagenet.setImageUrl(beautyEntity.get_beauty_imageURL(),_imageLoader);
        }
        else
        if(beautyEntity.get_beauty_resId()!=0) {
            holder.beautyImagenet.setVisibility(View.GONE);
            holder.beautyImage.setVisibility(View.VISIBLE);
            holder.beautyImage.setImageResource(beautyEntity.get_beauty_resId());
        }
        else {
            holder.beautyImagenet.setVisibility(View.GONE);
            holder.beautyImage.setVisibility(View.VISIBLE);
            holder.beautyImage.setImageBitmap(beautyEntity.get_beautyBitmap());
        }


        if (beautyEntity.get_provider_imageURL().length() > 0) {
            holder.providerImage.setVisibility(View.GONE);
            holder.providerImagenet.setVisibility(View.VISIBLE);
            holder.providerImagenet.setImageUrl(beautyEntity.get_provider_imageURL(),_imageLoader);
        }
        else
        if(beautyEntity.get_provider_resId()!=0) {
            holder.providerImagenet.setVisibility(View.GONE);
            holder.providerImage.setVisibility(View.VISIBLE);
            holder.providerImage.setImageResource(beautyEntity.get_provider_resId());
        }

        else {
            holder.providerImagenet.setVisibility(View.GONE);
            holder.providerImage.setVisibility(View.VISIBLE);
            holder.providerImage.setImageBitmap(beautyEntity.get_providerBitmap());
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _context.gotoUserProfile(_context,user);
            }
        });



        return convertView;
    }

    public void showToast(String content){
        LayoutInflater inflater = LayoutInflater.from(_context);
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(_context);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public void filter(String charText){

        charText = charText.toLowerCase();

        _beautyDatas.clear();

        if(charText.length() == 0){
            _beautyDatas.addAll(_allBeautyDatas);
        }else {

            for (BeautyEntity beautyEntity : _allBeautyDatas){

                if (beautyEntity instanceof BeautyEntity) {

                    String value = ((BeautyEntity) beautyEntity).get_beauty_name().toLowerCase();
                    if (value.contains(charText)) {
                        _beautyDatas.add(beautyEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resorting(ArrayList<BeautyEntity> entities){
        ArrayList<BeautyEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            BeautyEntity beautyEntity=new BeautyEntity();
            beautyEntity=entities.get(i);
            datas.add(beautyEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    class CustomHolder {

        public CircularImageView providerImage;
        public CircularNetworkImageView providerImagenet;
        public ImageView beautyImage;
        public NetworkImageView beautyImagenet;
        public TextView beautyName;
        public TextView providerName;
        public TextView beautyPrice;
        public TextView beautyCategory;
        public TextView beautyDescription;
        public TextView accept;
    }
}

