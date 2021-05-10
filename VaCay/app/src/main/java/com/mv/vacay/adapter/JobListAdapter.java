package com.mv.vacay.adapter;

/**
 * Created by a on 4/9/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.provider.JobDetailActivity;
import com.mv.vacay.models.JobsEntity;
import com.mv.vacay.models.UserEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by a on 2016.10.24.
 */
public class JobListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<JobsEntity> _datas = new ArrayList<>();
    private ArrayList<JobsEntity> _allDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public JobListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<JobsEntity> jobs) {

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
            convertView = inflater.inflate(R.layout.job_list_item, parent, false);

            holder.logo = (NetworkImageView) convertView.findViewById(R.id.logo);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.reqId = (TextView) convertView.findViewById(R.id.reqId);
            holder.department=(TextView)convertView.findViewById(R.id.department);
            holder.location=(TextView)convertView.findViewById(R.id.location);
            holder.description=(TextView)convertView.findViewById(R.id.description);
            holder.postDate=(TextView)convertView.findViewById(R.id.postDate);
            holder.empty=(TextView)convertView.findViewById(R.id.empty);
            holder.company=(TextView)convertView.findViewById(R.id.company);
            holder.surveynote=(LinearLayout)convertView.findViewById(R.id.surveynote);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final JobsEntity job = (JobsEntity) _datas.get(position);

        holder.name.setText(job.getJobName());
        holder.reqId.setText(job.getJobReqId());
        holder.department.setText(job.getDepartment());
        holder.location.setText(job.getLocation());
        holder.description.setText(job.getDescription());

        holder.postDate.setText(job.getPostingDate());
        holder.empty.setText(job.getEmptyField());
        holder.company.setText(job.getCompany());

        if (job.getSurvey().startsWith("http") && job.getSurvey().contains("?usp=sf_link"))
            holder.surveynote.setVisibility(View.VISIBLE);
        else holder.surveynote.setVisibility(View.GONE);

        Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/futura-md-bt-bold-58e2b41ab199c.ttf");
        holder.company.setTypeface(font);

        font = Typeface.createFromAsset(_context.getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        holder.name.setTypeface(font);

        font = Typeface.createFromAsset(_context.getAssets(), "fonts/monotype-corsiva-58e26af1803c5.ttf");
        holder.description.setTypeface(font);

        if (job.getLogoUrl().length() > 0) {
            holder.logo.setImageUrl(job.getLogoUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.job=job;
                Intent intent=new Intent(_context, JobDetailActivity.class);
                _context.startActivity(intent);
            }
        });



        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();

        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_allDatas);
        }else {

            for (JobsEntity job : _allDatas){

                if (job instanceof JobsEntity) {

                    String value = ((JobsEntity) job).getJobName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(job);
                    }else {
                        String value1 = ((JobsEntity) job).getJobName().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(job);
                        }
                        else {
                            String value2 = ((JobsEntity) job).getDescription().toLowerCase();
                            if (value2.contains(charText)) {
                                _datas.add(job);
                            }
                            else {
                                String value3 = ((JobsEntity) job).getJobReqId().toLowerCase();
                                if (value3.contains(charText)) {
                                    _datas.add(job);
                                }
                                else {
                                    String value4 = ((JobsEntity) job).getDepartment().toLowerCase();
                                    if (value4.contains(charText)) {
                                        _datas.add(job);
                                    }
                                }
                            }
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

        public NetworkImageView logo;
        public TextView name;
        public TextView reqId;
        public TextView department;
        public TextView location;
        public TextView description;
        public TextView postDate;
        public TextView empty;
        public TextView company;
        public LinearLayout surveynote;
    }
}


