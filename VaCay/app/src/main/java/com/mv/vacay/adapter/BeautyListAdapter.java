package com.mv.vacay.adapter;

/**
 * Created by a on 2/19/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.main.provider.SProviderHomeActivity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.RestaurantEntity;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.06.
 */
public class BeautyListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private SProviderHomeActivity _context;
    private ArrayList<BeautyServiceEntity> _datas = new ArrayList<>();
    private ArrayList<BeautyServiceEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public BeautyListAdapter(SProviderHomeActivity context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<BeautyServiceEntity> datas) {

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

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.beauty_list, parent, false);

            holder.imvPhotonet = (NetworkImageView) convertView.findViewById(R.id.beauty_image_net);
            holder.imvPhoto = (ImageView) convertView.findViewById(R.id.beauty_image);
            holder.beautyCategory = (TextView) convertView.findViewById(R.id.beauty_category);
            holder.beautyName = (TextView) convertView.findViewById(R.id.beauty_name);
            holder.beautyPrice=(TextView)convertView.findViewById(R.id.beauty_price);
            holder.beautyDescription=(TextView)convertView.findViewById(R.id.description);
            holder.providerTakeHome=(TextView)convertView.findViewById(R.id.takeHomePay);
            holder.processingFee=(TextView)convertView.findViewById(R.id.processingFee);
            holder.totalPrice=(TextView)convertView.findViewById(R.id.totalPrice);

            holder.mediaButton=(LinearLayout) convertView.findViewById(R.id.mediaButton);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        holder.beautyPrice.setTypeface(font);

        final BeautyServiceEntity beautyServiceEntity = (BeautyServiceEntity) _datas.get(position);

        holder.beautyCategory.setText(beautyServiceEntity.getBeautyName());
        holder.beautyName.setText(beautyServiceEntity.getBeautySubName());
        holder.beautyDescription.setText(beautyServiceEntity.getBeautyDescription());
        if(beautyServiceEntity.getBeautyPrice().contains("$"))
            holder.beautyPrice.setText(beautyServiceEntity.getBeautyPrice());
        else holder.beautyPrice.setText("$"+beautyServiceEntity.getBeautyPrice());
        holder.providerTakeHome.setText("$" + beautyServiceEntity.getProviderTakeHome());
        holder.processingFee.setText("20%");
        if(beautyServiceEntity.getBeautyPrice().contains("$"))
            holder.totalPrice.setText(beautyServiceEntity.getBeautyPrice());
        else holder.totalPrice.setText("$"+beautyServiceEntity.getBeautyPrice());

        if (beautyServiceEntity.getBeautyImageUrl().length() < 1000) {
            holder.imvPhotonet.setVisibility(View.VISIBLE);
            holder.imvPhotonet.setImageUrl(beautyServiceEntity.getBeautyImageUrl(),_imageLoader);
        }
        else {
            holder.imvPhotonet.setVisibility(View.GONE);
            holder.imvPhoto.setImageBitmap(base64ToBitmap(beautyServiceEntity.getBeautyImageUrl()));
        }

        holder.imvPhotonet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.resId = beautyServiceEntity.getBeautyImageRes();
                Commons.photoUrl = beautyServiceEntity.getBeautyImageUrl();
                Intent intent = new Intent(_context, ViewActivity.class);
                _context.startActivity(intent);
            }
        });

        holder.mediaButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        holder.mediaButton.setBackgroundResource(R.drawable.green_fillround);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                        holder.mediaButton.setBackgroundResource(R.drawable.login_roundrect);

                        Commons.newBeautyEntity=beautyServiceEntity;
                        _context.enterMedia(beautyServiceEntity);

                    case MotionEvent.ACTION_CANCEL: {
                        //clear the overlay
                        holder.mediaButton.getBackground().clearColorFilter();
                        holder.mediaButton.invalidate();
                        break;
                    }
                }

                return true;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _context.gotoUserProfile(_context,user);
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

            for (BeautyServiceEntity beautyServiceEntity : _alldatas){

                if (beautyServiceEntity instanceof BeautyServiceEntity) {

                    String value = ((BeautyServiceEntity) beautyServiceEntity).getBeautySubName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(beautyServiceEntity);
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

        public NetworkImageView imvPhotonet;
        public ImageView imvPhoto;
        public TextView beautyCategory;
        public TextView beautyName;
        public TextView beautyPrice;
        public TextView beautyDescription;
        public TextView providerTakeHome;
        public TextView processingFee;
        public TextView totalPrice;

        public LinearLayout mediaButton;
    }
}


























