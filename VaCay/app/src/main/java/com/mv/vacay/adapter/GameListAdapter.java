package com.mv.vacay.adapter;

/**
 * Created by a on 2016.12.23.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
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
import com.mv.vacay.main.carpediem.BarCheckinViewActivity;
import com.mv.vacay.main.carpediem.GameDetailActivity;
import com.mv.vacay.main.carpediem.GameListActivity;
import com.mv.vacay.models.GameEntity;
import com.mv.vacay.models.RestaurantEntity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by a on 2016.11.06.
 */
public class GameListAdapter extends BaseAdapter {

    private static final int TYPE_INDEX = 0;
    private static final int TYPE_USER = 1;

    private Context _context;
    private ArrayList<GameEntity> _datas = new ArrayList<>();
    private ArrayList<GameEntity> _alldatas = new ArrayList<>();

    private boolean _openVideos=false;

    ImageLoader _imageLoader;

    public GameListAdapter(Context context){

        super();
        this._context = context;

        _imageLoader = VaCayApplication.getInstance().getImageLoader();
    }

    public void setGameDatas(ArrayList<GameEntity> datas) {

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
            convertView = inflater.inflate(R.layout.game_list_item, parent, false);

            holder.gameImage = (ImageView) convertView.findViewById(R.id.game_image);
            holder.gameImagenet = (NetworkImageView) convertView.findViewById(R.id.game_image_net);
            holder.barName = (TextView) convertView.findViewById(R.id.barname);
            holder.gameName = (TextView) convertView.findViewById(R.id.gamename);
            holder.duration = (TextView) convertView.findViewById(R.id.duration);
            holder.gameTeam=(TextView)convertView.findViewById(R.id.team_channel);
            holder.knownName=(TextView)convertView.findViewById(R.id.knownname);
            holder.gameList=(TextView)convertView.findViewById(R.id.gamelist);
            holder.map = (ImageView) convertView.findViewById(R.id.map);
            holder.barImage = (ImageView) convertView.findViewById(R.id.bar_image);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final GameEntity gameEntity = (GameEntity) _datas.get(position);

        holder.barName.setText(gameEntity.getBarName());
        if(gameEntity.getDuaration().length()>0){
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(gameEntity.getDuaration());
        }else holder.duration.setVisibility(View.GONE);
        holder.gameName.setText(gameEntity.getGameName());
        if(gameEntity.getTeamName().length()>0){
            holder.gameTeam.setText("Team: "+gameEntity.getTeamName()+"  "+"Channel: "+gameEntity.getChannel());
        }else holder.gameTeam.setText("Channel: "+gameEntity.getChannel());
        holder.knownName.setText(gameEntity.getKnownName());
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context,BarCheckinViewActivity.class);
                Commons.gameEntity=gameEntity;
                _context.startActivity(intent);
            }
        });
        holder.gameList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(_context,GameListActivity.class);
                Commons.gameEntity=gameEntity;
                _context.startActivity(intent);
            }
        });
        if(gameEntity.getBarType().trim().equals("Restaurant"))
            holder.barImage.setImageResource(R.drawable.reestaurant);
        else if(gameEntity.getBarType().trim().equals("Bar"))
            holder.barImage.setImageResource(R.drawable.beer);
        else if(gameEntity.getBarType().trim().equals("Park"))
            holder.barImage.setImageResource(R.drawable.ppark);

 //       Log.d("GameImageUrl===>",gameEntity.getGameThumbnailUrl());

        holder.gameImagenet.setVisibility(View.VISIBLE);
        holder.gameImage.setVisibility(View.GONE);
        holder.gameImagenet.setImageUrl(gameEntity.getGameThumbnailUrl(),_imageLoader);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(_context,GameDetailActivity.class);
                Commons.gameEntity=gameEntity;
                _context.startActivity(intent);
            }
        });



        return convertView;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    public void filterName(String charText) {

        charText = charText.toLowerCase();

        _datas.clear();

        if (charText.length() == 0) {
            _datas.addAll(_alldatas);
        } else {

            for (GameEntity gameEntity : _alldatas) {

                if (gameEntity instanceof GameEntity) {

                    String value = ((GameEntity) gameEntity).getGameName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(gameEntity);
                    } else {
                        String value1 = ((GameEntity) gameEntity).getTeamName().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(gameEntity);
                        } else {
                            String value2 = ((GameEntity) gameEntity).getChannel().toLowerCase();
                            if (value2.contains(charText)) {
                                _datas.add(gameEntity);
                            }else {
                                String value3 = ((GameEntity) gameEntity).getBarName().toLowerCase();
                                if (value3 .contains(charText)) {
                                    _datas.add(gameEntity);
                                }
                            }
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    public void filterNameOthers(String charText) {

        charText = charText.toLowerCase();

        _datas.clear();

        if (charText.length() == 0) {
            _datas.addAll(_alldatas);
        } else {

            for (GameEntity gameEntity : _alldatas) {

                if (gameEntity instanceof GameEntity) {

                    String value = ((GameEntity) gameEntity).getGameName().toLowerCase();
                    if (value.contains(charText)) {
                        _datas.add(gameEntity);
                    } else {
                        String value1 = ((GameEntity) gameEntity).getTeamName().toLowerCase();
                        if (value1.contains(charText)) {
                            _datas.add(gameEntity);
                        } else {
                            String value2 = ((GameEntity) gameEntity).getChannel().toLowerCase();
                            if (value2.contains(charText)) {
                                _datas.add(gameEntity);
                            }
                        }
                    }
                }
            }
            notifyDataSetChanged();
        }
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

        public ImageView gameImage;
        public NetworkImageView gameImagenet;
        public TextView barName;
        public TextView gameName;
        public TextView gameTeam;
        public TextView knownName;
        public TextView gameList;
        public TextView duration;
        public ImageView map;
        public ImageView barImage;
    }
}




