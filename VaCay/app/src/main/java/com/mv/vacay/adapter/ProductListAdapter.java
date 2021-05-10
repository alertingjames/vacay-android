package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.beautymen.ProductDetailActivity;
import com.mv.vacay.main.provider.ProviderProductDetailActivity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.RestaurantEntity;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.06.
 */
public class ProductListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<BeautyProductEntity> _datas = new ArrayList<>();
    private ArrayList<BeautyProductEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ProductListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<BeautyProductEntity> datas) {

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
            convertView = inflater.inflate(R.layout.product_list, parent, false);

            holder.imvPhoto = (ImageView) convertView.findViewById(R.id.product_image);
            holder.imvPhotonet = (NetworkImageView) convertView.findViewById(R.id.product_image_net);
            holder.product = (TextView) convertView.findViewById(R.id.product);
            holder.productName = (TextView) convertView.findViewById(R.id.productName);
            holder.company=(TextView)convertView.findViewById(R.id.company);
            holder.price=(TextView)convertView.findViewById(R.id.price);
            holder.size=(TextView)convertView.findViewById(R.id.size);
            holder.desc=(TextView)convertView.findViewById(R.id.description);
            holder.loc=(TextView)convertView.findViewById(R.id.location);
            holder.providerTakeHome=(TextView)convertView.findViewById(R.id.takeHomePay);
            holder.processingFee=(TextView)convertView.findViewById(R.id.processingFee);
            holder.totalPrice=(TextView)convertView.findViewById(R.id.totalPrice);
            holder.breakdownLayout=(LinearLayout)convertView.findViewById(R.id.breakdownLayout);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final BeautyProductEntity beautyProductEntity = (BeautyProductEntity) _datas.get(position);

        holder.product.setText(beautyProductEntity.getProduct());
        holder.productName.setText(beautyProductEntity.getProductName());
        holder.company.setText(beautyProductEntity.getCompanyName());
        holder.price.setText(beautyProductEntity.getPrice().replace("$",""));
        holder.size.setText(beautyProductEntity.getSize());
        holder.desc.setText(beautyProductEntity.getDescription());
        holder.loc.setText(beautyProductEntity.getLocation());

        if(Commons._is_provider)
            holder.breakdownLayout.setVisibility(View.VISIBLE);
        else
            holder.breakdownLayout.setVisibility(View.GONE);

        holder.providerTakeHome.setText("$" + beautyProductEntity.getProviderTakeHome());
        holder.processingFee.setText("20%");
        if(beautyProductEntity.getPrice().contains("$"))
            holder.totalPrice.setText(beautyProductEntity.getPrice());
        else holder.totalPrice.setText("$" + beautyProductEntity.getPrice());


        if (beautyProductEntity.getProductImageUrl().length() < 1000) {
            holder.imvPhotonet.setVisibility(View.VISIBLE);
            holder.imvPhotonet.setImageUrl(beautyProductEntity.getProductImageUrl(),_imageLoader);
        }
        else {
            holder.imvPhotonet.setVisibility(View.GONE);
            holder.imvPhoto.setImageBitmap(base64ToBitmap(beautyProductEntity.getProductImageUrl()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Commons._is_provider) {
                    Intent intent = new Intent(_context, ProductDetailActivity.class);
                    Commons.beautyProductEntity = beautyProductEntity;
                    _context.startActivity(intent);
                }else {
                    Intent intent = new Intent(_context, ProviderProductDetailActivity.class);
                    Commons.beautyProductEntity = beautyProductEntity;
                    _context.startActivity(intent);
                }
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

            for (BeautyProductEntity beautyProductEntity : _alldatas){

                if (beautyProductEntity instanceof BeautyProductEntity) {

                    String value = ((BeautyProductEntity) beautyProductEntity).getProduct().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(beautyProductEntity);
                    }
                    else {
                        String value2 = ((BeautyProductEntity) beautyProductEntity).getBrand().toLowerCase();
                        if (value2.contains(charText)) {
                            _datas.add(beautyProductEntity);
                        }
                        else {
                            String value3 = ((BeautyProductEntity) beautyProductEntity).getDescription().toLowerCase();
                            if (value3.contains(charText)) {
                                _datas.add(beautyProductEntity);
                            }
                            else {
                                String value4 = ((BeautyProductEntity) beautyProductEntity).getPrice().toLowerCase();
                                if (value4.contains(charText)) {
                                    _datas.add(beautyProductEntity);
                                }
                            }
                        }
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

    public Bitmap base64ToBitmap(String base64Str){
        Bitmap bitmap;
        final String pureBase64Encoded = base64Str.substring(base64Str.indexOf(",")  + 1);
        final byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
        bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        return bitmap;
    }


    class CustomHolder {

        public ImageView imvPhoto;
        public NetworkImageView imvPhotonet;
        public TextView product;
        public TextView productName;
        public TextView company;
        public TextView price;
        public TextView size;
        public TextView desc;
        public TextView loc;
        public TextView providerTakeHome;
        public TextView processingFee;
        public TextView totalPrice;
        public LinearLayout breakdownLayout;
    }
}



