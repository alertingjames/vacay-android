package com.mv.vacay.adapter;

/**
 * Created by a on 4/9/2017.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mv.vacay.R;
import com.mv.vacay.VaCayApplication;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.provider.CompanyManagerActivity;
import com.mv.vacay.main.provider.EmployeeSendMailActivity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.utils.CircularImageView;
import com.mv.vacay.utils.CircularNetworkImageView;

import java.util.ArrayList;

/**
 * Created by a on 2016.10.24.
 */
public class EmployeesListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private CompanyManagerActivity _context;
    private ArrayList<UserEntity> _userDatas = new ArrayList<>();
    private ArrayList<UserEntity> _allUserDatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public EmployeesListAdapter(CompanyManagerActivity context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setUserDatas(ArrayList<UserEntity> users) {

        _allUserDatas = users;
        _userDatas.clear();
        _userDatas.addAll(_allUserDatas);
    }

    @Override
    public int getCount(){
        return _userDatas.size();
    }

    @Override
    public Object getItem(int position){
        return _userDatas.get(position);
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
            convertView = inflater.inflate(R.layout.employee_list_item, parent, false);

            holder.photo = (CircularNetworkImageView) convertView.findViewById(R.id.photo);
            holder.photo2 = (CircularImageView) convertView.findViewById(R.id.photo2);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.gender = (TextView) convertView.findViewById(R.id.gender);
            holder.email=(TextView)convertView.findViewById(R.id.email);
            holder.pwd=(TextView)convertView.findViewById(R.id.pwd);
            holder.millennial=(TextView)convertView.findViewById(R.id.millennial);
            holder.given=(TextView)convertView.findViewById(R.id.givenbuck);
            holder.used=(TextView)convertView.findViewById(R.id.usedbuck);
            holder.interactions=(TextView)convertView.findViewById(R.id.interactions);
            holder.status=(TextView)convertView.findViewById(R.id.status);
            holder.sendMailButton=(TextView)convertView.findViewById(R.id.sendMailButton);

            Typeface font = Typeface.createFromAsset(_context.getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
            holder.status.setTypeface(font);
            holder.name.setTypeface(font);


            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _userDatas.get(position);

        holder.name.setText(user.get_name());
        holder.email.setText(user.get_email());
        String password="Pwd: "+user.get_password();
        holder.pwd.setText(password);
        holder.gender.setText(user.getGender());
        holder.millennial.setText(user.getMillennial());
        String given="Given VaCay bucks: "+user.getVacayBucksGiven();
        String used="Used VaCay bucks: "+user.getVacayBucksUsed();
        String interactions="Interactions: "+user.getInteractions();
        holder.given.setText(given);
        holder.used.setText(used);
        holder.interactions.setText(interactions);
        holder.status.setText(user.getStatus());
        if(user.getStatus().equals("Login Approved")){
            holder.sendMailButton.setBackgroundResource(R.drawable.gray_fillrect);
            holder.sendMailButton.setTextColor(Color.WHITE);
            holder.status.setTextColor(Color.RED);
        }else {
            holder.sendMailButton.setBackgroundResource(R.drawable.black_fill_rect);
            holder.status.setTextColor(Color.parseColor("#03c423"));
        }

        Commons.employee=user;

        holder.sendMailButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!user.getStatus().equals("Login Approved")){
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            holder.sendMailButton.setBackgroundResource(R.drawable.green_fillrect);
                            break;
                        }
                        case MotionEvent.ACTION_UP:
        //                    holder.sendMailButton.setBackgroundResource(R.drawable.black_fill_rect);
                            holder.sendMailButton.setBackgroundResource(R.drawable.gray_fillrect);
                            holder.sendMailButton.setTextColor(Color.WHITE);




                        case MotionEvent.ACTION_CANCEL: {
                            //clear the overlay
                            holder.sendMailButton.getBackground().clearColorFilter();
                            holder.sendMailButton.invalidate();
                            break;
                        }
                    }
                }
        //        else showAlertDialogSendMail("You have already emailed this employee. Do you want to resend this employee?");

                return true;
            }
        });

//        if(user.is_message_flag()) holder.message.setVisibility(View.VISIBLE);
//        else holder.message.setVisibility(View.INVISIBLE);

        Log.d("ImageUrl123===>",user.get_photoUrl());

        if (user.get_photoUrl().length() < 1000) {
            holder.photo2.setVisibility(View.GONE);
            holder.photo.setImageUrl(user.get_photoUrl(),_imageLoader);
        }else {
            holder.photo2.setVisibility(View.VISIBLE);
            holder.photo2.setImageBitmap(base64ToBitmap(user.get_photoUrl()));
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _context.selectThingsForEmployee(user);
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

        _userDatas.clear();

        if(charText.length() == 0){
            _userDatas.addAll(_allUserDatas);
        }else {

            for (UserEntity user : _allUserDatas){

                if (user instanceof UserEntity) {

                    String value = ((UserEntity) user).get_name().toLowerCase();
                    if (value.contains(charText)) {
                        _userDatas.add(user);
                    }else {
                        String value1 = ((UserEntity) user).get_email().toLowerCase();
                        if (value1.contains(charText)) {
                            _userDatas.add(user);
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

        public CircularNetworkImageView photo;
        public CircularImageView photo2;
        public TextView name;
        public TextView gender;
        public TextView email;
        public TextView millennial;
        public TextView given;
        public TextView used;
        public TextView interactions;
        public TextView pwd;
        public TextView status;
        public TextView sendMailButton;
    }

    public void showAlertDialogSendMail(String msg) {

        AlertDialog alertDialog = new AlertDialog.Builder(_context).create();

        alertDialog.setTitle("Hint!");
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent=new Intent(_context, EmployeeSendMailActivity.class);
                        _context.startActivity(intent);
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        //alertDialog.setIcon(R.drawable.banner);
        alertDialog.show();

    }
}


