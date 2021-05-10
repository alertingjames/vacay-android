package com.mv.vacay.commons;

import java.util.ArrayList;

/**
 * Created by a on 2016.10.22.
 */
public class Constants {
    public static final int PICK_FROM_CAMERA = 100;
    public static final int PICK_FROM_ALBUM = 101;
    public static final int CROP_FROM_CAMERA = 102;

    public static final int PROFILE_IMAGE_SIZE = 256;

    public static ArrayList<String>promotionurls=new ArrayList<>();
    public static ArrayList<String>promotiondetailurl=new ArrayList<>();
    public static int promotiondetailid=0;

    public static final int VOLLEY_TIME_OUT = 60000;

    public static final String CONTACT_NAME = "contact_name";
    public static final int CONVERSATION_LOADER = 321;
    public static final int ALL_SMS_LOADER = 123;
    public static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    public static final String FROM_SMS_RECIEVER = "from_sms_receiver";
    public static final CharSequence BACKUP = "backup";
    public static final int MY_PERMISSIONS_REQUEST_READ_SMS = 1;
    public static final String PREF_NAME = "sms_pref";
    public static final String SMS_JSON = "sms_json";
    public static final String LIST_STATE_KEY = "list_state";
    public static final String SMS_ID = "_id";
    public static final String COLOR = "color";
    public static final String READ = "read";
    public static final CharSequence RESTORE = "restore";
    public static final int TYPE_RESTORE = 1;
    public static final int TYPE_BACKUP = 0;

    public static final int SPLASH_TIME = 2000;

    public static final String USER = "user";
    public static final String CLASS = "class";
    public static final String KEY_ROOM = "room";

    public static final String XMPP_START = "xmpp";
    public static final int XMPP_FROMBROADCAST = 0;
    public static final int XMPP_FROMLOGIN = 1;

    public static final String KEY_SEPERATOR = "#";
    public static final String KEY_ROOM_MARKER = "ROOM#";

    public static final String KEY_LOGOUT = "logout";

    public static final int NORMAL_NOTI_ID = 1;

    public static final int RECENT_MESSAGE_COUNT = 20;

    public static final String  DEFAULT_CITY = "London";
    public static final String  DEFAULT_LAT = "51.5072";
    public static final String  DEFAULT_LON = "0.1275";

    public final static int NORMAL = 0x00;
    public final static int BIG_TEXT_STYLE = 0x01;
    public final static int BIG_PICTURE_STYLE = 0x02;
    public final static int INBOX_STYLE = 0x03;
    public final static int CUSTOM_VIEW = 0x04;

}
