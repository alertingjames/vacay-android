package com.mv.vacay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.main.provider.BroadmoorProductDetailActivity;
import com.mv.vacay.models.ProductSizeEntity;

import java.util.ArrayList;

/**
 * Created by a on 3/31/2017.
 */

public class ProductSizeListAdapter extends BaseAdapter {
    private BroadmoorProductDetailActivity _context;
    private ArrayList<ProductSizeEntity> _datas = new ArrayList<>();
    private ArrayList<ProductSizeEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ProductSizeListAdapter(BroadmoorProductDetailActivity context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<ProductSizeEntity> datas) {

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
            convertView = inflater.inflate(R.layout.product_size_list, parent, false);

            holder.size=(TextView)convertView.findViewById(R.id.size);
            holder.price=(TextView)convertView.findViewById(R.id.price);
            holder.quantity=(TextView)convertView.findViewById(R.id.quantity);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final ProductSizeEntity sizeEntity = (ProductSizeEntity) _datas.get(position);

        holder.size.setText(sizeEntity.getProductSize());
        holder.price.setText(sizeEntity.getProductPrice().replace("$",""));
        holder.quantity.setText(sizeEntity.getProductQuantity());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _context.showProductSizeDetail(sizeEntity);
            }
        });

        return convertView;
    }

    class CustomHolder {

        public TextView size;
        public TextView price;
        public TextView quantity;
    }


}

