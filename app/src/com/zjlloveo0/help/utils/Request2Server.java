package com.zjlloveo0.help.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zjlloveo0 on 20.
 */
public class Request2Server {
    public static String getRequsetResult(String u) {
        String s = "";
        try {
            URL url = new URL(u);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(5000);
            if (urlConnection.getResponseCode() == 200) {
                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                s = response.toString().trim();
            } else {
                s = "网络错误！";
            }
        } catch (Exception e) {
            s = "系统异常！";
            e.printStackTrace();
        }
        System.out.println("得到数据：" + s);
        return s;
    }

    /*
    *  u="http://192.168.191.1:8080/BookStore/TestAndroidObj";
    * */
    public static String sendObj2Server(Object o, String u) {
        String s = "";
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        HttpURLConnection con = null;
        ObjectOutputStream oos = null;
        try {
            URL url = new URL(u);
            con = (HttpURLConnection) url.openConnection();
            // 设置允许输出，默认为false
            con.setDoOutput(true);
            con.setConnectTimeout(5 * 1000);
            con.setReadTimeout(10 * 1000);
            // 请求方式为POST请求
            con.setRequestMethod("POST");
            oos = new ObjectOutputStream(con.getOutputStream());
            // 向服务端写数据
            oos.writeObject(o);
            if (con.getResponseCode() == 200) {
                // 获得服务端的返回数据
                InputStreamReader read = new InputStreamReader(
                        con.getInputStream());
                reader = new BufferedReader(read);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                s = sb.toString();
            } else {
                s = "网络错误！";
            }
        } catch (Exception e) {
            s = "系统异常！";
            e.printStackTrace();
        }
        System.out.println("得到数据：" + s);
        return s;
    }
}
