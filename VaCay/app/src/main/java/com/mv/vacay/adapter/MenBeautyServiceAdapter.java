package com.mv.vacay.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.mv.vacay.main.MediaActivity;
import com.mv.vacay.main.beauty.ViewActivity;
import com.mv.vacay.main.beautymen.BeautyListActivity;
import com.mv.vacay.main.beautymen.NewBeautyServiceDetailActivity;
import com.mv.vacay.main.beautymen.ProductListActivity;
import com.mv.vacay.main.beautymen.RequestBeautyActivity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.RestaurantEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.11.06.
 */
public class MenBeautyServiceAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    BeautyListActivity _context = null;

    private ArrayList<BeautyServiceEntity> _datas = new ArrayList<>();
    private ArrayList<BeautyServiceEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public MenBeautyServiceAdapter(BeautyListActivity context){

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
    public View getView(int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.beauty_service_list, parent, false);

            holder.providerPhoto = (CircularImageView) convertView.findViewById(R.id.provider_profile);
            holder.providerPhoto0=(CircularImageView)convertView.findViewById(R.id.provider_profile0);
            holder.providerPhotoNet=(CircularNetworkImageView) convertView.findViewById(R.id.provider_profile_net);
            holder.providerEmail = (TextView) convertView.findViewById(R.id.providerEmail);
            holder.providerPassword = (TextView) convertView.findViewById(R.id.tempPwd);
            holder.providerName = (TextView) convertView.findViewById(R.id.provider_name);
            holder.providerLocation = (TextView) convertView.findViewById(R.id.location);
            holder.company=(TextView)convertView.findViewById(R.id.company_name);
            holder.gotoProductList=(TextView)convertView.findViewById(R.id.productList);
            holder.available=(TextView)convertView.findViewById(R.id.available);
            holder.availableicon=(ImageView) convertView.findViewById(R.id.availableicon);

            holder.linearLayout1=(LinearLayout) convertView.findViewById(R.id.beautyFrame1);
            holder.beautyImage11=(ImageView) convertView.findViewById(R.id.beauty_image0);
            holder.beautyImage1=(ImageView) convertView.findViewById(R.id.beauty_image);
            holder.beautyImageNet=(NetworkImageView) convertView.findViewById(R.id.beauty_image_net);
            holder.beautyPrice1=(TextView)convertView.findViewById(R.id.beauty_price);
            holder.beautyName1=(TextView)convertView.findViewById(R.id.beauty_category);
            holder.beautySubName1=(TextView)convertView.findViewById(R.id.beauty_name);
            holder.beautyDescription1=(TextView)convertView.findViewById(R.id.description);
            holder.accept1=(TextView)convertView.findViewById(R.id.accept_request);

            holder.mediaButton=(LinearLayout) convertView.findViewById(R.id.mediaButton);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final BeautyServiceEntity beautyEntity = (BeautyServiceEntity) _datas.get(position);

        holder.providerName.setText(beautyEntity.getFullName());
        holder.providerEmail.setText(beautyEntity.getEmail());
        holder.company.setText(beautyEntity.getCompanyName());
        holder.providerPassword.setText(beautyEntity.getPassword());
        holder.providerLocation.setText(beautyEntity.getLocation());
        holder.available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.newBeautyEntity=beautyEntity;
                _context.showSchedulePage();
            }
        });

        if (beautyEntity.getAvailable().equals("true") || beautyEntity.getAvailable().length()==0)
            holder.availableicon.setImageResource(R.drawable.bluecircle);
        else holder.availableicon.setImageResource(R.drawable.redcircle);


        if (beautyEntity.getProviderPhotoUrl().length() > 1000) {
            holder.providerPhotoNet.setVisibility(View.GONE);
            holder.providerPhoto.setImageBitmap(base64ToBitmap(beautyEntity.getProviderPhotoUrl()));
        }
        else {
            holder.providerPhotoNet.setVisibility(View.VISIBLE);
            holder.providerPhotoNet.setImageUrl(beautyEntity.getProviderPhotoUrl(),_imageLoader);
        }

        holder.providerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Commons.resId = beautyEntity.getProviderResPhoto();
                Commons.photoUrl = beautyEntity.getProviderPhotoUrl();
                Intent intent = new Intent(_context, ViewActivity.class);
                _context.startActivity(intent);
            }
        });

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        holder.gotoProductList.setTypeface(font);

        holder.gotoProductList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context,ProductListActivity.class);
                Commons.newBeautyEntity=beautyEntity;
                _context.startActivity(intent);
            }
        });

        holder.beautyPrice1.setTypeface(font);
        holder.beautySubName1.setTypeface(font);
        font = Typeface.createFromAsset(_context.getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        holder.beautyDescription1.setTypeface(font);

        try {
            if(!beautyEntity.getBeautyPrice().contains("$")){
                holder.beautyPrice1.setText("$ "+beautyEntity.getBeautyPrice());
            }
            else holder.beautyPrice1.setText(beautyEntity.getBeautyPrice());
            holder.beautyName1.setText(beautyEntity.getBeautyName());
            holder.beautySubName1.setText(beautyEntity.getBeautySubName());
            holder.beautyDescription1.setText(beautyEntity.getBeautyDescription());

            if (beautyEntity.getBeautyImageUrl().length() > 1000) {
                holder.beautyImageNet.setVisibility(View.GONE);
                holder.beautyImage1.setImageBitmap(base64ToBitmap(beautyEntity.getBeautyImageUrl()));
            }
            else {
                holder.beautyImageNet.setVisibility(View.VISIBLE);
                holder.beautyImageNet.setImageUrl(beautyEntity.getBeautyImageUrl(),_imageLoader);
            }

            holder.beautyImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Commons.resId = beautyEntity.getBeautyImageRes();
                    Commons.photoUrl = beautyEntity.getBeautyImageUrl();
                    Intent intent = new Intent(_context, ViewActivity.class);
                    _context.startActivity(intent);
                }
            });

            holder.accept1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            holder.accept1.setBackgroundResource(R.drawable.green_fillround);
                            holder.accept1.setTextColor(ColorStateList.valueOf(Color.WHITE));
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                            holder.accept1.setBackgroundResource(R.drawable.white_stroke);
                            holder.accept1.setTextColor(ColorStateList.valueOf(Color.WHITE));

                            Commons.newBeautyEntity=beautyEntity;
                            Intent intent=new Intent(_context,RequestBeautyActivity.class);
                            _context.startActivity(intent);

                        case MotionEvent.ACTION_CANCEL: {
                            //clear the overlay
                            holder.accept1.getBackground().clearColorFilter();
                            holder.accept1.invalidate();
                            break;
                        }
                    }

                    return true;
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

                            Commons.newBeautyEntity=beautyEntity;
                            _context.enterMedia(beautyEntity);

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

            holder.linearLayout1.setVisibility(View.VISIBLE);

        }catch (NullPointerException e){
            e.printStackTrace();
            TextView nobeauty=(TextView)convertView.findViewById(R.id.nobeauty);
            nobeauty.setVisibility(View.VISIBLE);
        }catch (IndexOutOfBoundsException e){
            TextView nobeauty=(TextView)convertView.findViewById(R.id.nobeauty);
            nobeauty.setVisibility(View.VISIBLE);
        }




        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                _context.gotoUserProfile(_context,user);
                Intent intent=new Intent(_context,NewBeautyServiceDetailActivity.class);
                Commons.newBeautyEntity=beautyEntity;
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
            for (BeautyServiceEntity beautyEntity : _alldatas){

                if (beautyEntity instanceof BeautyServiceEntity) {

                    String value = ((BeautyServiceEntity) beautyEntity).getFullName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(beautyEntity);
                        notifyDataSetChanged();
                    }else {
                        String value2 = ((BeautyServiceEntity) beautyEntity).getBeautySubName().toLowerCase();
                        if (value2.contains(charText)) {
                            _datas.add(beautyEntity);
                            notifyDataSetChanged();
                        }else {
                            String value3 = ((BeautyServiceEntity) beautyEntity).getBeautyPrice().toLowerCase();
                            if (value3.contains(charText)) {
                                _datas.add(beautyEntity);
                                notifyDataSetChanged();
                            }else {
                                String value4 = ((BeautyServiceEntity) beautyEntity).getBeautyDescription().toLowerCase();
                                if (value4.contains(charText)) {
                                    _datas.add(beautyEntity);
                                    notifyDataSetChanged();
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

        public CircularImageView providerPhoto0;
        public CircularImageView providerPhoto;
        public CircularNetworkImageView providerPhotoNet;
        public TextView providerName;
        public TextView providerEmail;
        public TextView providerPassword;
        public TextView providerLocation;
        public TextView gotoProductList;
        public TextView company;
        public TextView available;
        public ImageView availableicon;

        public LinearLayout linearLayout1;
        public ImageView beautyImage11;
        public ImageView beautyImage1;
        public NetworkImageView beautyImageNet;
        public TextView beautyName1;
        public TextView beautySubName1;
        public TextView beautyPrice1;
        public TextView beautyDescription1;
        public TextView accept1;

        public LinearLayout mediaButton;

    }
}



