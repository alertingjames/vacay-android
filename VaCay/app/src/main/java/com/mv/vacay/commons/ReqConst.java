package com.mv.vacay.commons;

/**
 * Created by a on 2016.11.22.
 */
public class ReqConst {

    //    public static final String SERVER_ADDR = "http://52.34.222.14";

    public static final String SERVER_ADDR = "https://cayley.pythonanywhere.com";           //      Local Server Address      "http://192.168.1.140:8000"        52.14.191.245

    //    public static final String CHATTING_SERVER = "52.34.222.14";
//    public static final String CHATTING_SERVER = "192.168.1.80";


    public static final String ROOM_SERVICE = "@conference.";

    public static final String SERVER_URL = SERVER_ADDR + "/";                     //      Local Server URL        SERVER_ADDR + "/"

    public static final String FIREBASE_DATABASE_URL = "https://vacay-42bcd.firebaseio.com/";

    public static final String REQ_REGISTERUSER = "registerUser";
    public static final String REQ_RESTAURANT = "restaurant";
    public static final String REQ_SENDMAIL = "sendMail";
    public static final String REQ_MAKEMAIL = "makeMail";

    public static final String REQ_REGISTERPROVIDER = "registerProvider";

    public static final String REQ_UPDATEUSERPROFILE = "updateUserProfile";
    public static final String REQ_UPLOADPHOTO = "uploadPhoto";
    public static final String REQ_SURVEY = "survey";
    public static final String REQ_LOGIN = "login";
    public static final String REQ_UPDATEPROFILE = "updateProfile";
    public static final String REQ_GETALLUSERS = "getAllUsers";
    public static final String REQ_GETRESTAURANTINFO = "getRestaurantInfo";
    public static final String REQ_GETPROVIDERINFO = "getProviderInfo";
    public static final String REQ_GETUSERPROFILE = "getUserProfile";
    public static final String REQ_GETMAIL = "getMail";
    public static final String REQ_ALLSENTMAIL = "allSentMail";
    public static final String REQ_GETBEAUTYIMAGES = "getBeautyImages";
    public static final String REQ_ADDFRIEND = "addFriend";
    public static final String REQ_GETFRIENDS = "getFriends";
    public static final String REQ_GETCLASS = "getClass";
    public static final String REQ_GETCLASSMATE = "getClassmate";
    public static final String REQ_ADDCLASS = "addClass";
    public static final String REQ_SEARCHUSERS = "searchUsers";
    public static final String REQ_GETAUTHCODE = "getAuthCode";
    public static final String REQ_DELETECLASS = "deleteClass";
    public static final String REQ_DELETEFRIEND = "deleteFriend";
    public static final String REQ_RESETPWD = "resetPassword";
    public static final String REQ_GETUSERINFO = "getUserInfo";
    public static final String REQ_GETVIRIFYCODE = "getVerifyCode";
    public static final String REQ_EMAILCONFIRM = "emailConfirm";


    //response value
    public static final String RES_CODE = "result_code";
    public static final String RES_USERINFO = "user_info";
    public static final String RES_USERINFOS = "user_infos";
    public static final String RES_USERPROFILE = "user_profile";
    public static final String RES_RESTAURANTINFO = "restaurantInfo";
    public static final String RES_PROVIDERINFO = "providerInfo";
    public static final String RES_USERNAME = "name";
    public static final String RES_EMAIL = "email";

    public static final String RES_ID = "id";
    public static final String RES_USERID = "userid";
    public static final String RES_RESTID = "restid";
    public static final String RES_SENTMAIL = "sent mail";
    public static final String RES_MESSAGECONTENT = "mail_infos";
    public static final String RES_SENTMAILINFOS = "sent_mail_infos";
    public static final String RES_PROID = "proid";
    public static final String RES_FIRSTNAME = "first_name";
    public static final String RES_LASTNAME = "last_name";
    public static final String RES_RESTNAME = "rest_name";
    public static final String RES_PRONAME = "pro_name";
    public static final String RES_AGE = "age";
    public static final String RES_RESTTYPE = "rest_type";
    public static final String RES_PROPHOTO = "pro_photo";
    public static final String RES_ADDRESS = "address";
    public static final String RES_MENUURL = "menu_url";
    public static final String RES_PROLATITUDE = "pro_latitude";
    public static final String RES_PROLONGITUDE = "pro_longitude";
    public static final String RES_JOB = "job";
    public static final String RES_OPENTABLEURL = "opentable_url";
    public static final String RES_EDUCATION = "education";
    public static final String RES_LOCATIONURL = "location_url";
    public static final String RES_BEAUTYNAMEID = "beauti_name_id";
    public static final String RES_BEAUTYPRICE = "beauti_price";
    public static final String RES_BEAUTYIMAGE = "beauti_image";
    public static final String RES_BEAUTYDESCRIPTION = "beauti_description";
    public static final String RES_INTERESTS = "interests";
    public static final String RES_ABOUTME = "about_me";
    public static final String RES_PHOTOURL = "photo_url";

    public static final String RES_SURVEYONE = "survey_one";
    public static final String RES_SURVEYTWO = "survey_two";
    public static final String RES_SURVEYTHREE = "survey_three";
    public static final String RES_SURVEYFOUR = "survey_four";
    public static final String RES_SURVEYFIVE = "survey_five";

    public static final String RES_FOODIMAGE = "food_image";
    public static final String RES_PROEMAIL = "pro_email";
    public static final String RES_FILEURL = "file_url";
    public static final String RES_FRIENDCOUNT = "friend_count";
    public static final String RES_FRIENDLIST = "friend_list";
    public static final String RES_CLASSNAME = "class_name";
    public static final String RES_SEARCHRESULT = "search_result";

    public static final String PARAM_ID = "id";
    public static final String PARAM_IMAGETYPE = "image_type";
    public static final String PARAM_PHOTOURL = "photo_url";
    public static final String PARAM_RESTID = "restid";
    public static final String PARAM_MENUURL = "menu_url";
    public static final String PARAM_OPENTABLEURL = "opentable_url";
    public static final String PARAM_LOCATIONURL = "location_url";

    public static final String RES_ACCESS_TOKEN = "access_token";
    public static final String RES_ERROR = "error";
    public static final String PARAM_FILE = "file";

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_EXISTEMAIL = 101;
    public static final int CODE_UNREGUSER = 102;
    public static final int CODE_WRONGPWD = 104;
    public static final int CODE_NOTEMAIL = 107;
    public static final int CODE_WRONGAUTH = 108;

}

