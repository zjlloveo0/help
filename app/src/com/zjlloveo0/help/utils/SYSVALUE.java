package com.zjlloveo0.help.utils;

import com.zjlloveo0.help.model.UserSchool;

/**
 * Created by zjlloveo0 on 2017/4/8.
 */

public class SYSVALUE {
    public static UserSchool currentUser;
    public final static String REGEX_PHONE = "^1(3[0-9]|4[57]|5[0-35-9]|7[0135678]|8[0-9])\\d{8}$";
    public final static String REGEX_PASSWORD = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{6,20}$";
    public final static String HOST = "http://192.168.191.1:8080/HelpService/";
//    public final static String HOST = "http://zjlloveo0.e2.luyouxia.net:33723/HelpService/";
//    public final static String HOST = "http://112.74.195.152/HelpService/";
}
