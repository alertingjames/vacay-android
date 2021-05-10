package com.mv.vacay.adapter;

/**
 * Created by a on 4/11/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.JobListActivity;
import com.mv.vacay.models.CompanyEntity;

import java.util.ArrayList;

/**
 * Created by a on 3/25/2017.
 */

public class CompanyListAdapter1 extends BaseAdapter {

    private JobListActivity _context;
    private ArrayList<CompanyEntity> _datas = new ArrayList<>();
    private ArrayList<CompanyEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public CompanyListAdapter1(JobListActivity context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<CompanyEntity> datas) {

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
            convertView = inflater.inflate(R.layout.company_list, parent, false);

            holder.brandImage = (NetworkImageView) convertView.findViewById(R.id.brandImage);
            holder.company=(TextView)convertView.findViewById(R.id.companyName);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final CompanyEntity companyEntity = (CompanyEntity) _datas.get(position);

        holder.company.setText(companyEntity.getCompany());

        if(companyEntity.getLogoUrl().length()>0) {
            holder.brandImage.setImageUrl(companyEntity.getLogoUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.companyEntity=companyEntity;
                Commons._is_select_job=true;
                _context.showJobList1();
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

            for (CompanyEntity companyEntity : _alldatas){

                if (companyEntity instanceof CompanyEntity) {

                    String value = ((CompanyEntity) companyEntity).getCompany().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(companyEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public NetworkImageView brandImage;
        public TextView company;
    }
}

