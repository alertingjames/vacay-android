package com.mv.vacay.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mv.vacay.R;
import com.mv.vacay.commons.Commons;
import com.mv.vacay.main.activity.GolfActivity;
import com.mv.vacay.main.meetfriends.MeetFriendActivity;
import com.mv.vacay.main.activity.RunningActivity;
import com.mv.vacay.main.activity.SkiingSnowboardingActivity;
import com.mv.vacay.main.activity.TennisActivity;

/**
 * Created by a on 2016.11.02.
 */
public class FragmentC extends Fragment {

    FrameLayout view;
    TextView ok, text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.third_frag, container, false);
        view= (FrameLayout) v.findViewById(R.id.frame);
        text=(TextView)v.findViewById(R.id.text3);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ag-futura-58e274b5588ad.ttf");
        text.setTypeface(font);
        ok=(TextView)v.findViewById(R.id.okay);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Commons._golf_activity){
                    Intent intent=new Intent(getActivity(),GolfActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._run_activity){
                    Intent intent=new Intent(getActivity(),RunningActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._ski_activity){
                    Intent intent=new Intent(getActivity(),SkiingSnowboardingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(Commons._tennis_activity){
                    Intent intent=new Intent(getActivity(),TennisActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), MeetFriendActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        return v;
    }

    public static FragmentC newInstance(String text) {

        FragmentC f = new FragmentC();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        view.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.move);
        view.startAnimation(animation);
    }


}
