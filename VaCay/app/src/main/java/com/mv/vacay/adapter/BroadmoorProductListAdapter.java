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
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.provider.BroadmoorProductDetailActivity;
import com.mv.vacay.models.BroadmoorEntity;

import java.util.ArrayList;

/**
 * Created by a on 3/28/2017.
 */

public class BroadmoorProductListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<BroadmoorEntity> _datas = new ArrayList<>();
    private ArrayList<BroadmoorEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public BroadmoorProductListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<BroadmoorEntity> datas) {

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
            convertView = inflater.inflate(R.layout.broadmoor_product_list, parent, false);

            holder.productImage = (ImageView) convertView.findViewById(R.id.product_image);
            holder.productImage_net = (NetworkImageView) convertView.findViewById(R.id.product_image_net);
            holder.productName = (TextView) convertView.findViewById(R.id.productName);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final BroadmoorEntity productEntity = (BroadmoorEntity) _datas.get(position);


        holder.productName.setText(productEntity.getProductName());


        if (productEntity.getProductImageUrl().length() > 1000) {
            holder.productImage.setVisibility(View.VISIBLE);
            holder.productImage.setImageBitmap(base64ToBitmap(productEntity.getProductImageUrl()));
        }else {
            holder.productImage.setVisibility(View.GONE);
            holder.productImage_net.setImageUrl(productEntity.getProductImageUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.broadmoorEntity=productEntity;
                Intent intent=new Intent(_context, BroadmoorProductDetailActivity.class);
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

            for (BroadmoorEntity broadmoorEntity : _alldatas){

                if (broadmoorEntity instanceof BroadmoorEntity) {

                    String value = ((BroadmoorEntity) broadmoorEntity).getProductName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(broadmoorEntity);
                    }
                    else {
                        String value2 = ((BroadmoorEntity) broadmoorEntity).getProductAdditional().toLowerCase();
                        if (value2.contains(charText)) {
                            _datas.add(broadmoorEntity);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public ImageView productImage;
        public NetworkImageView productImage_net;
        public TextView productName;
    }
}



