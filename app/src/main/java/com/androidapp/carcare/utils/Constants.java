package com.androidapp.carcare.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constants {

    public static final String SERVER_ADDRESS = "http://lingalah.com";
//    public static final String SERVER_ADDRESS = "http://192.168.43.210";

    public static final String GETSERVICELIST = "/getserviceslist";
    public static final String GETAUSERINFO = "/getauserinfo";
    public static final String REGISTERUSER = "/registeruser";
    public static final String STOREPROPIC = "/storepropic";
    public static final String GETCARSLIST = "/getcarslist";
    public static final String GETVENDORSFORSERVICE = "/vendorsforservice";

    public static final String UNDEFINED = "undef_strngvar";
    public static final String NULLDATA = "nullld";


    public static final String cUSERID  =  "userid";
    public static final String cUSERNAME = "username";
    public static final String cUSERMOBNUM = "usermobnum";
    public static final String cUSEREMAIL = "useremailid";
    public static final String cUSERIMGURL = "userimgurl";

    public static final String userCARBRAND = "usercarbrand";
    public static final String userCARNAME  = "usercarname";
    public static final String userCARFUELTYPE = "usercarfueltype";
    public static final String userCARYEAR = "usercaryear";

    public static final String userLOClat = "userloclat";
    public static final String userLOClong = "userloclong";
    public static final String userLOCcity = "userloccity";



    public static final String cSELECT= "Select";
    public static final String PREFS_NAME = "GCC_PREFS";


    public static final String db_servicename = "servicename";
    public static final String db_serviceid = "serviceid";
    public static final String db_serviceimgurl = "serviceimgurl";

    public static final String db_vendorid = "vendorid";
    public static final String db_vendorname = "vendorname";
    public static final String db_vendortitle = "vendortitle";

    public static final String db_vendorimgurl = "vendorimgurl";
    public static final String db_vendormobnum = "vendormobnum";
    public static final String db_vendoremailid = "vendoremailid";

    public static final String db_vendorloclat = "vendorloclat";
    public static final String db_vendorloclong = "vendorloclong";
    public static final String db_vendorcity = "vendorcity";



    

    // Determines if user has to log on | update when logins and logsout
    public static final String IsRegisteredUser = "isregistereduser";

    public static final int PERMISSION_ACCESS_COARSE_LOCATION = 2323;




    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected() && info.isAvailable());
    }



    public static final int SELECT_PHOTO = 40997;
}
