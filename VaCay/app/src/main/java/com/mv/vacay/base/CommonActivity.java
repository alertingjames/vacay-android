package com.mv.vacay.base;

/**
 * Created by a on 2016.11.22.
 */

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import com.mv.vacay.commons.Commons;

import java.util.List;
import java.util.Locale;

/**
 * Created by a on 6/13/2016.
 */
public abstract class CommonActivity extends BaseActivity{

    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

    }

    public void updateBadgeCount(int count) {

        Commons.g_badgCount = count;
        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName());
        intent.putExtra("badge_count", Commons.g_badgCount);
        sendBroadcast(intent);
    }

    private String getLauncherClassName() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(getPackageName());

        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(intent, 0);
        if(resolveInfoList != null && resolveInfoList.size() > 0) {
            return resolveInfoList.get(0).activityInfo.name;
        }

        return null;
    }

    public String getLanguage(){

        Locale systemLocale = getResources().getConfiguration().locale;
        String strLanguage = systemLocale.getLanguage();

        return strLanguage;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Commons.g_isAppPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Commons.g_isAppPaused = false;
        Commons.g_isAppRunning = true;
    }
}

