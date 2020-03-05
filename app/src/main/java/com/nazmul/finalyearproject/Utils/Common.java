package com.nazmul.finalyearproject.Utils;

import com.nazmul.finalyearproject.Model.User;
import com.nazmul.finalyearproject.Remote.IFCMService;
import com.nazmul.finalyearproject.Remote.RetrofitClient;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Common {

    public static final String USER_INFORMATION ="UserInformation";
    public static final String USER_UID_SAVE_KEY = "SaveUid" ;
    public static final String TOKEN = "tokens";
    public static final String FROM_NAME = "FromName";
    public static final String ACCEPT_LIST = "acceptList";
    public static final String FROM_UID = "FromUid";
    public static final String TO_UID = "ToUid";
    public static final String TO_NAME = "ToName";
    public static final String FRIEND_REQUEST = "FriendRequests";
    public static final String PUBLIC_LOCATION = "PublicLocation";


    public static User logedUser;
    public static User trackingUser;

    public static IFCMService getFCMService(){
        return RetrofitClient.getClient("https://fcm.googleapis.com/")
                .create(IFCMService.class);
    }

    public static Date convertTimeStampToDate(long time){
        return new Date(new Timestamp(time).getTime());

    }

    public static String getFormatted(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date).toString();

    }
}
