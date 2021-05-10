package com.mv.vacay.main;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.R;
import com.mv.vacay.adapter.CheckinListAdapter;
import com.mv.vacay.base.BaseActivity;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.UserEntity;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.Locale;

public class CheckinlistActivity extends BaseActivity implements View.OnClickListener,SwipyRefreshLayout.OnRefreshListener {
    ListView listView;
    ImageView imvback;
    private AdView mAdView;
    EditText ui_edtsearch;
    SwipyRefreshLayout ui_RefreshLayout;
    com.mv.vacay.main.meetfriends.MeetFriendActivity context;
    ArrayList<UserEntity> _datas=new ArrayList<>(10000);
    CheckinListAdapter checkinListAdapter=new CheckinListAdapter(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkinlist);

        TextView title=(TextView)findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        title.setTypeface(font);


        imvback=(ImageView)findViewById(R.id.back);
        imvback.setOnClickListener(this);
        listView=(ListView)findViewById(R.id.list_friends);

        ui_RefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        ui_RefreshLayout.setOnRefreshListener(this);
        ui_RefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);



        int[] resIds={R.drawable.cayley3,R.drawable.images1,R.drawable.bob1,R.drawable.alianna,R.drawable.celina,R.drawable.michael,R.drawable.andreas};
        String[] firstnames={"Cayley","Taylor","Lindsey","Cici","Michelle","Blake","Maria"};
        String[] lastnames={"W.","S.","V.","M.","O.","L.","S."};
        String[] cities={"San Francisco","New York","Washington D.C.","Boston","Edison","Plano","New York"};
        String[] jobs={"Ecommerce Manager","Artist","Artist","Typist","Reporter","Publisher","Manager"};
        String[] educations={"Musics","Mathematics","Mobile","Computer Engineering","Information","Chemistry","Biology"};
        String[] interests={"Tennis,Reading","Typing,Reading","Skiing,Typing","Reading,Golf","Typing,Reading","Golf,Typing","Tennis,Reading"};
        String[] ageranges={"20-25","20-25","26-30","20-25","26-30","20-25","26-30"};
        String[] survey_quests={getString(R.string.questdd),getString(R.string.questc),getString(R.string.questa),getString(R.string.queste),getString(R.string.questb),getString(R.string.questdd),getString(R.string.questb)};
        boolean[] message_flags={true,false,false,true,false,false,false};
        String[] messages={getString(R.string.messagefromfriend),"","",getString(R.string.messagefromfriendB),"","",""};
        String[] emails={"cayleywetzig@gmail.com","tayor@gmail.com","lindsey@hotmail.com","cici@gmail.com","michelle@gmail.com","blake@gmail.com","maria@hotmail.com"};
        int[] friendmessage_resIds={R.drawable.match,0,0,R.drawable.loc,0,0,0};
        String[] requestLocInfo={getString(R.string.locinfosf),"","",getString(R.string.locinfoboston),"","",""};
        LatLng[] latLngs={new LatLng(37.7749295,-122.4194155),null,null,new LatLng(42.3600825,-71.0588801),null,null,null};

        for(int i=0;i<resIds.length;i++){
            UserEntity entity=new UserEntity();
            entity.set_imageRes(resIds[i]);
            entity.set_firstName(firstnames[i]);
            entity.set_lastName(lastnames[i]);
            entity.set_city(cities[i]);
            entity.set_job(jobs[i]);
            entity.set_education(educations[i]);
            entity.set_interest(interests[i]);
            entity.set_age_range(ageranges[i]);
            entity.set_survey_quest(survey_quests[i]);
            entity.set_email(emails[i]);
            entity.set_message_flag(message_flags[i]);
            MessageEntity messageEntity=new MessageEntity();
            messageEntity.setUserfullname(entity.get_fullName());
            messageEntity.set_useremail(entity.get_email());
            messageEntity.set_usermessage(messages[i]);
            messageEntity.set_resId(friendmessage_resIds[i]);
            messageEntity.setRequestLocation(requestLocInfo[i]);
            messageEntity.set_requestLatLng(latLngs[i]);
            ArrayList<MessageEntity> messageEntities=new ArrayList<>();
            messageEntities.add(messageEntity);
            entity.set_messageList(messageEntities);
            //           _datas.add(entity);
        }

        if(Commons.userEntities.isEmpty()) showToast("No people checked in");
        checkinListAdapter.setUserDatas(Commons.userEntities);
        checkinListAdapter.notifyDataSetChanged();
        listView.setAdapter(checkinListAdapter);

        ui_edtsearch = (EditText)findViewById(R.id.edt_search);
        ui_edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ui_edtsearch.getText().toString().toLowerCase(Locale.getDefault());
                if (text.length() != 0) {
                    checkinListAdapter.filter(text);
                    //   adapter.notifyDataSetChanged();
                }else  {
                    checkinListAdapter.setUserDatas(Commons.userEntities);
                    listView.setAdapter(checkinListAdapter);
                }

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                overridePendingTransition(R.anim.left_in,R.anim.right_out);
        }
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        //       getAllUsers();
    }
}


