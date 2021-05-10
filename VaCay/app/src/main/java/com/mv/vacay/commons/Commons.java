package com.mv.vacay.commons;

import android.app.NotificationManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.mv.vacay.base.CommonActivity;
import com.mv.vacay.models.AnnouncementEntity;
import com.mv.vacay.models.BeautyEntity;
import com.mv.vacay.models.BeautyProductEntity;
import com.mv.vacay.models.BeautyServiceEntity;
import com.mv.vacay.models.BroadmoorEntity;
import com.mv.vacay.models.CompanyEntity;
import com.mv.vacay.models.GameEntity;
import com.mv.vacay.models.JobsEntity;
import com.mv.vacay.models.MediaEntity;
import com.mv.vacay.models.MessageEntity;
import com.mv.vacay.models.NewBeautyEntity;
import com.mv.vacay.models.ProviderScheduleEntity;
import com.mv.vacay.models.RestaurantEntity;
import com.mv.vacay.models.SProviderIntentEntity;
import com.mv.vacay.models.UserEntity;
import com.mv.vacay.models.WaterCoolerEntity;
import com.mv.vacay.nearby.PlaceBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by a on 2016.10.22.
 */
public class Commons {
    public static UserEntity userEntity=new UserEntity();
    public static String survey_quest="";
    public static String loc_url="";
    public static String requestMessage="";
    public static String speechMessage="";

    public static String thisuserGender="";
    public static String messageForDate="";

    public static String requestDescription="";
    public static String requestDateTime="";

    public static String payEmail="";
    public static String payPrice="";
    public static String payName="";

    public static String notiEmail="";

    public static Firebase firebase=null;

    public static Map mapping=null;

    public static LinearLayout imagePortion=null;

    public static ImageView mapImage=null;

    public static ArrayList<String> speeches=new ArrayList<>();

    public static ArrayList<PlaceBean> list=new ArrayList<>();

    public static UserEntity thisEntity=new UserEntity();
    public static UserEntity employee=new UserEntity();
    public static MessageEntity messageEntity=new MessageEntity();
    public static Bitmap bitmap=null;
    public static Bitmap map=null;
    public static Bitmap broadmoorImage=null;
    public static Bitmap bitmap_provider=null;
    public static String mailingAddr="";
    public static String providerimagepath="";
    public static Bitmap bitmap_activity=null;
    public static int resId=0;
    public static int _idx=0;
    public static int REQ_CODE_SPEECH_INPUT_CAR =200;
    public static File file=null;
    public static File destination=null;
    public static File tempFile=null;
    public static String comment="";
    public static String now_watching_message="";
    public static String photoUrl="";
    public static String imageUrl="";
    public static String videoUrl="";
    public static String compressedvideoUrl="";

    public static ImageView imageView=null;

    public static WaterCoolerEntity waterCoolerEntity=new WaterCoolerEntity();
    public static String proid="";
    public static String proemail="";

    public static LatLng requestLatlng=null;
    public static LatLng providerLatlng=null;
    public static LatLng latLng=null;
    public static double providerLat=0.0;
    public static double providerlng=0.0;
    public static int year=0;
    public static int month=0;
    public static int day=0;
    public static int hour=0;
    public static int min=0;
    public static String _datetime="";
    public static int _datetimeId=0;
    public static String _speechResult="";

    public static NotificationManager mNotificationManager=null;
    public static Vibrator _vibrator=null;

    public static int beautyCategoryId=0;
    public static int beautyAreaId=0;
    public static int providerId=0;

    public static int oldMessages=0;

    public static int _gender_flag=0;

    public static boolean _home_to_inbox=false;
    public static boolean _provider_to_inbox=false;
    public static boolean _proManager_to_inbox=false;
    public static boolean _broadmoor_to_inbox=false;
    public static boolean _company_to_inbox=false;
    public static boolean _customer_booking=false;
    public static boolean _provider_booking=false;

    public static boolean _golf_activity=false;
    public static boolean _run_activity=false;
    public static boolean _ski_activity=false;
    public static boolean _tennis_activity=false;

    public static boolean _biking_activity=false;
    public static boolean _fishing_activity=false;
    public static boolean _surfing_activity=false;
    public static boolean _exploring_activity=false;

    public static boolean _location_activity=false;
    public static boolean _calendar_set=false;   //request
    public static boolean _location_set=false;   //request
    public static boolean _request_set=false;    //request
    public static boolean _beauty_hair_set=false;
    public static boolean _beauty_blowout_set=false;
    public static boolean _beauty_hotshave_set=false;
    public static boolean _beauty_makeover_set=false;
    public static boolean _beauty_manicure_set=false;
    public static boolean _beauty_massage_set=false;
    public static boolean _beauty_wax_set=false;
    public static boolean _beauty_facial_set=false;
    public static boolean _is_beautyProviderPage=false;
    public static boolean _is_beautyBack=true;
    public static boolean _is_vendorSelect=false;
    public static boolean _is_employeeSelect=false;
    public static boolean _is_composeDateBack=false;
    public static boolean _is_all_videos_theBar=false;
    public static boolean _is_game=false;
    public static boolean _is_commentPhotoBack=false;
    public static boolean _video_activity=false;
    public static boolean _allow_sendMessage=false;
    public static boolean _inboxUserSearch=false;
    public static boolean _video_compressed=false;
    public static boolean _mappingGetFlag=false;
    public static boolean _isMyLocationVerified=false;

    public static int _is_beautyRequest=0;

    public static boolean _is_admin=false;
    public static boolean _is_provider=false;

    public static boolean _is_instagram=false;

    public static boolean _is_companyManager=false;
    public static boolean _is_broadmoor=false;

    public static ArrayList<ProviderScheduleEntity> scheduleInfo=new ArrayList<>();
    public static ProviderScheduleEntity dateTime=new ProviderScheduleEntity();
    public static BroadmoorEntity broadmoorEntity=new BroadmoorEntity();

    public static JobsEntity job=new JobsEntity();
    public static MediaEntity mediaEntity=new MediaEntity();
    public static AnnouncementEntity announcement=new AnnouncementEntity();
    public static CompanyEntity companyEntity=new CompanyEntity();

    public static boolean _is_select_job=false;

    public static boolean _search_selected=false;

    public static Uri videouri=null;
    public static Uri videouri_default=null;
    public static Bitmap thumb=null;
    public static boolean _video_post_flag=false;
    public static boolean _newMessage=false;
    public static boolean _appPermissions=false;

    public static RestaurantEntity restaurantEntity=new RestaurantEntity();
    public static GameEntity gameEntity=new GameEntity();
    public static GameEntity gameEntityUpload=new GameEntity();
    public static ArrayList<GameEntity> gameEntities=new ArrayList<>();
    public static ArrayList<GameEntity> gameEntitiesMovie=new ArrayList<>();
    public static ArrayList<GameEntity> gameEntitiesAll=new ArrayList<>();
    public static BeautyEntity beautyEntity=new BeautyEntity();
    public static ArrayList<BeautyEntity> subbeautyEntities=new ArrayList<>();
    public static ArrayList<BeautyEntity> beautyEntities=new ArrayList<>();

    public static BeautyServiceEntity newBeautyEntity=new BeautyServiceEntity();
    public static BeautyServiceEntity beautyServiceEntity=new BeautyServiceEntity();
    public static BeautyProductEntity beautyProductEntity=new BeautyProductEntity();
    public static ArrayList<BeautyProductEntity> productEntities=new ArrayList<>();
    public static ArrayList<NewBeautyEntity> newBeautyEntities=new ArrayList<>();
    public static ArrayList<BeautyServiceEntity> newBeautyEntitiesBuf=new ArrayList<>();
    public static ArrayList<BeautyServiceEntity> allNewBeautyEntities=new ArrayList<>();
    public static ArrayList<BeautyServiceEntity> subnewbeautyEntities=new ArrayList<>();
    public static ArrayList<BeautyServiceEntity> beautyEntities_net=new ArrayList<>();
    public static ArrayList<BeautyEntity> bufbeautyEntities=new ArrayList<>();
    public static ArrayList<RestaurantEntity> restaurantEntities=new ArrayList<>();
    public static ArrayList<BeautyEntity> beautyEntities1=new ArrayList<>();
    public static ArrayList<UserEntity> userEntities=new ArrayList<>();

    public static SProviderIntentEntity sProviderIntentEntity=new SProviderIntentEntity();

    public static boolean g_isAppRunning = false;
    public static boolean g_isAppPaused = false;

    public static Handler g_handler = null;
    public static String g_appVersion = "1.0";
    public static int g_badgCount = 0;

    public static UserEntity g_newUser = null;
    public static UserEntity g_user = null;

//    public static ConnectionMgrService g_xmppService = null;

    public static CommonActivity g_currentActivity = null;
//    public static ChattingActivity g_chattingActivity = null;

//    public static String idxToAddr(int idx) {
//        return idx + "@" + ReqConst.CHATTING_SERVER;
//    }

    public static int addrToIdx(String addr) {
        int pos = addr.indexOf("@");
        return Integer.valueOf(addr.substring(0, pos)).intValue();
    }

    public static String fileExtFromUrl(String url) {

        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }

        if (url.lastIndexOf(".") == -1) {
            return url;
        } else {
            String ext = url.substring(url.lastIndexOf(".") );
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    public static String fileNameWithExtFromUrl(String url) {

        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }

        if (url.lastIndexOf("/") == -1) {
            return url;
        } else {
            String name = url.substring(url.lastIndexOf("/")  + 1);
            return name;
        }
    }

    public static String fileNameWithoutExtFromUrl(String url) {

        String fullname = fileNameWithExtFromUrl(url);

        if (fullname.lastIndexOf(".") == -1) {
            return fullname;
        } else {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
    }

    public static String fileNameWithExtFromPath(String path) {

        if (path.lastIndexOf("/") > -1)
            return path.substring(path.lastIndexOf("/") + 1);

        return path;
    }

    public static String fileNameWithoutExtFromPath(String path) {

        String fullname = fileNameWithExtFromPath(path);

        if (fullname.lastIndexOf(".") == -1) {
            return fullname;
        } else {
            return fullname.substring(0, fullname.lastIndexOf("."));
        }
    }

}
