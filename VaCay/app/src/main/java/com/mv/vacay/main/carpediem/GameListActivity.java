package com.mv.vacay.main.carpediem;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mv.vacay.R;
import com.mv.vacay.adapter.GameListAdapter;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.models.GameEntity;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {

    ListView listView;
    TextView barName;
    ImageView back;
    AdView mAdView;
    GameListAdapter gameListAdapter;
    ArrayList<GameEntity> _datas_videos=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        listView=(ListView)findViewById(R.id.list_videos);
        barName=(TextView)findViewById(R.id.barname);
        barName.setText(Commons.gameEntity.getBarName());
        gameListAdapter=new GameListAdapter(this);

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        barName.setTypeface(font);


        if(!Commons._is_all_videos_theBar){
            for(int i = 0; i< Commons.gameEntities.size(); i++){
                if(
//                    Commons.gameEntity.getBarName().equals(Commons.gameEntities.get(i).getBarName()) &&
                        !Commons.gameEntity.getGameName().equals(Commons.gameEntities.get(i).getGameName())) {

                    GameEntity gameEntity = new GameEntity();
                    gameEntity.setGameName(Commons.gameEntities.get(i).getGameName());
                    Log.d("GameName===>",gameEntity.getGameName());
                    gameEntity.setVideoId(Commons.gameEntities.get(i).getVideoId());
                    gameEntity.setTeamName(Commons.gameEntities.get(i).getTeamName());
                    gameEntity.setDuaration(Commons.gameEntities.get(i).getDuaration());
                    gameEntity.setSpecials(Commons.gameEntities.get(i).getSpecials());
                    gameEntity.setGameThumbnailUrl(Commons.gameEntities.get(i).getGameThumbnailUrl());
//            gameEntity.setResImageId(gameResIds[i]);
                    gameEntity.setChannel(Commons.gameEntities.get(i).getChannel());
                    if(Commons.gameEntities.get(i).getDuaration().length()>0){
                        gameEntity.setDuaration(Commons.gameEntities.get(i).getDuaration());
                    }
                    gameEntity.setBarName(Commons.gameEntities.get(i).getBarName());
                    gameEntity.setDuaration(Commons.gameEntities.get(i).getDuaration());
                    try{
                        gameEntity.setSalePrice(Commons.gameEntities.get(i).getSalePrice());
                        gameEntity.setRentPrice(Commons.gameEntities.get(i).getRentPrice());
                        gameEntity.setVideoTypeSecondary(Commons.gameEntities.get(i).getVideoTypeSecondary());
                        gameEntity.setWatchWithPrice(Commons.gameEntities.get(i).getWatchWithPrice());
                    }catch (NullPointerException e){}
                    gameEntity.setBarType(Commons.gameEntities.get(i).getBarType());
                    gameEntity.setKnownName(Commons.gameEntities.get(i).getKnownName());
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int k = 0; k < Commons.gameEntities.get(i).getResMenus().size(); k++)
                        arrayList.add(Commons.gameEntities.get(i).getResMenus().get(k));
                    gameEntity.setResMenus(arrayList);

                    _datas_videos.add(gameEntity);
                }
            }
        }else {

            Commons._is_all_videos_theBar=false;

            for(int i = 0; i< Commons.gameEntitiesAll.size(); i++){
                if(Commons.gameEntity.getBarName().equals(Commons.gameEntitiesAll.get(i).getBarName())) {

                    GameEntity gameEntity = new GameEntity();
                    gameEntity.setGameName(Commons.gameEntitiesAll.get(i).getGameName());
                    Log.d("GameName===>",gameEntity.getGameName());
                    gameEntity.setVideoId(Commons.gameEntitiesAll.get(i).getVideoId());
                    gameEntity.setTeamName(Commons.gameEntitiesAll.get(i).getTeamName());
                    gameEntity.setDuaration(Commons.gameEntitiesAll.get(i).getDuaration());
                    gameEntity.setSpecials(Commons.gameEntitiesAll.get(i).getSpecials());
                    gameEntity.setGameThumbnailUrl(Commons.gameEntitiesAll.get(i).getGameThumbnailUrl());
//            gameEntity.setResImageId(gameResIds[i]);
                    gameEntity.setChannel(Commons.gameEntitiesAll.get(i).getChannel());
                    gameEntity.setDuaration(Commons.gameEntitiesAll.get(i).getDuaration());
                    try{
                        gameEntity.setSalePrice(Commons.gameEntitiesAll.get(i).getSalePrice());
                        gameEntity.setRentPrice(Commons.gameEntitiesAll.get(i).getRentPrice());
                        gameEntity.setVideoTypeSecondary(Commons.gameEntitiesAll.get(i).getVideoTypeSecondary());
                        gameEntity.setWatchWithPrice(Commons.gameEntitiesAll.get(i).getWatchWithPrice());
                    }catch (NullPointerException e){}
                    gameEntity.setBarName(Commons.gameEntitiesAll.get(i).getBarName());
                    gameEntity.setBarType(Commons.gameEntitiesAll.get(i).getBarType());
                    gameEntity.setKnownName(Commons.gameEntitiesAll.get(i).getKnownName());
                    ArrayList<String> arrayList = new ArrayList<>();
                    for (int k = 0; k < Commons.gameEntitiesAll.get(i).getResMenus().size(); k++)
                        arrayList.add(Commons.gameEntitiesAll.get(i).getResMenus().get(k));
                    gameEntity.setResMenus(arrayList);

                    _datas_videos.add(gameEntity);
                }
            }
        }

        if(_datas_videos.isEmpty()) showToast("No videos");

        gameListAdapter.setGameDatas(_datas_videos);
        listView.setAdapter(gameListAdapter);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0,0);
            }
        });
        if (TextUtils.isEmpty(getString(R.string.banner_home_footer))) {
            Toast.makeText(getApplicationContext(), "Please mention your Banner Ad ID in strings.xml", Toast.LENGTH_LONG).show();
            return;
        }

        mAdView = (AdView) findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("C04B1BFFB0774708339BC273F8A43708")
                .build();

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mAdView.loadAd(adRequest);
    }

    public void showToast(String content){
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.toast_view, null);
        TextView textView=(TextView)dialogView.findViewById(R.id.text);
        textView.setText(content);
        Toast toast=new Toast(this);
        toast.setView(dialogView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }
}
