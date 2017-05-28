package com.zjlloveo0.help.utils;

import com.zjlloveo0.help.bean.UserSchool;

/**
 * Created by zjlloveo0 on 2017/4/8.
 */

public class SYSVALUE {
    public static UserSchool currentUser;
    //小米推送
    public final static String APP_ID = "2882303761517576078";
    public final static String APP_KEY = "5731757658078";
    // 此TAG在adb logcat中检索自己所需要的信息， 只需在命令行终端输入 adb logcat | grep
    public final static String TAG = "com.zjlloveo0.help";
    public final static int SERVER_ORDERS_DETAIL = 1001;
    public final static int MISSION_ORDERS_DETAIL = 1002;
    public final static int MINE_DETAIL = 1003;

    //时间格式化
    public final static String SDF = "yyyy/MM/dd-HH:mm:ss";

    public final static String REGEX_PHONE = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";
    public final static String REGEX_PASSWORD = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,20}$";
    public final static String HOST = "http://192.168.191.1:8080/HelpService/";
//    public final static String HOST = "http://zjlloveo0.e2.luyouxia.net:33723/HelpService/";
//    public final static String HOST = "http://112.74.195.152/HelpService/";
}
