package com.mv.vacay.adapter;

/**
 * Created by a on 4/9/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.mv.vacay.main.provider.AnnouncementDetailActivity;
import com.mv.vacay.models.AnnouncementEntity;
import com.mv.vacay.models.UserEntity;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by a on 2016.10.24.
 */
public class AnnouncementListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<AnnouncementEntity> _datas = new ArrayList<>();
    private ArrayList<AnnouncementEntity> _allDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public AnnouncementListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<AnnouncementEntity> jobs) {

        _allDatas = jobs;
        _datas.clear();
        _datas.addAll(_allDatas);
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
            convertView = inflater.inflate(R.layout.announcement_list_item, parent, false);

            holder.logo = (NetworkImageView) convertView.findViewById(R.id.logo);
            holder.picture = (NetworkImageView) convertView.findViewById(R.id.picture);
            holder.picture2 = (ImageView) convertView.findViewById(R.id.picture2);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.audience = (TextView) convertView.findViewById(R.id.audience);
            holder.subject=(TextView)convertView.findViewById(R.id.subject);
            holder.postDate=(TextView)convertView.findViewById(R.id.postDate);
            holder.messageOwner=(TextView)convertView.findViewById(R.id.messageOwnerEmail);
            holder.description=(TextView)convertView.findViewById(R.id.description);
            holder.views=(TextView)convertView.findViewById(R.id.views);
            holder.responses=(TextView)convertView.findViewById(R.id.responses);
            holder.company=(TextView)convertView.findViewById(R.id.company);
            holder.surveynote=(LinearLayout)convertView.findViewById(R.id.surveynote);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final AnnouncementEntity announcementEntity = (AnnouncementEntity) _datas.get(position);

        holder.title.setText(announcementEntity.getTitle());
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        holder.title.setTypeface(font);

        holder.audience.setText(announcementEntity.getAudience());
        holder.subject.setText(announcementEntity.getSubject());
        holder.messageOwner.setText(announcementEntity.getMessageOwnerEmail());
        holder.description.setText(announcementEntity.getDescription());
        font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        holder.description.setTypeface(font);

        holder.postDate.setText(announcementEntity.getPostDate());
        holder.views.setText(announcementEntity.getViews());
        holder.responses.setText(announcementEntity.getResponses());
        holder.company.setText(announcementEntity.getCompany());

        font = Typeface.createFromAsset(_context.getAssets(), "fonts/futura-md-bt-bold-58e2b41ab199c.ttf");
        holder.company.setTypeface(font);

        if (announcementEntity.getSurvey().startsWith("http") && announcementEntity.getSurvey().contains("?usp=sf_link"))
            holder.surveynote.setVisibility(View.VISIBLE);
        else holder.surveynote.setVisibility(View.GONE);


        if (announcementEntity.getLogoUrl().length() > 0) {
            holder.logo.setImageUrl(announcementEntity.getLogoUrl(),_imageLoader);
        }

        if (announcementEntity.getPictureUrl().length() < 1000) {
            holder.picture.setImageUrl(announcementEntity.getPictureUrl(),_imageLoader);
        }else {
            holder.picture2.setVisibility(View.VISIBLE);
            holder.picture2.setImageBitmap(base64ToBitmap(announcementEntity.getPictureUrl()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.announcement=announcementEntity;
                Intent intent=new Intent(_context, AnnouncementDetailActivity.class);
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
            _datas.addAll(_allDatas);
        }else {

            for (AnnouncementEntity announcementEntity : _allDatas){

                if (announcementEntity instanceof AnnouncementEntity) {

                    String value = ((AnnouncementEntity) announcementEntity).getTitle().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(announcementEntity);
                    }else {
                        String value1 = ((AnnouncementEntity) announcementEntity).getAudience().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(announcementEntity);
                        }
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void resorting(ArrayList<UserEntity> entities){
        ArrayList<UserEntity> datas=new ArrayList<>();
        datas.clear();
        for(int i=entities.size()-1;i>-1;i--){
            UserEntity userEntity=new UserEntity();
            userEntity=entities.get(i);
            datas.add(userEntity);
        }
        entities.clear();
        entities.addAll(datas);
    }

    class CustomHolder {

        public NetworkImageView picture;
        public ImageView picture2;
        public NetworkImageView logo;
        public TextView title;
        public TextView audience;
        public TextView subject;
        public TextView postDate;
        public TextView description;
        public TextView messageOwner;
        public TextView views;
        public TextView responses;
        public TextView company;
        public LinearLayout surveynote;
    }
}


