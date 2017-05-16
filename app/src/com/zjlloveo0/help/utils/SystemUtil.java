package com.zjlloveo0.help.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 系统工具箱
 */
public class SystemUtil {

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getOrderStateString(int i) {
        String s = "";
        switch (i) {
            case 0:
                s = "服务已申请";
                break;
            case 1:
                s = "订单已接受";
                break;
            case 2:
                s = "订单已完成";
                break;
            case 3:
                s = "订单已结束";
                break;
            case 4:
                s = "订单被拒绝";
                break;
            case 5:
                s = "服务使用者终止订单";
                break;
            case 6:
                s = "服务提供者终止订单";
                break;
            case 7:
                s = "订单终止";
                break;
            case 8:
                s = "订单取消";
                break;
            default:
                s = "状态异常";
                break;
        }
        return s;
    }

    public static String getMissionStateString(int i) {
        String s = "";
        switch (i) {
            case 0:
                s = "暂无人接任务";
                break;
            case 1:
                s = "正在完成任务";
                break;
            case 2:
                s = "任务已完成";
                break;
            case 3:
                s = "任务已结束";
                break;
            case 4:
                s = "任务接受者终止订单";
                break;
            case 5:
                s = "任务发布者终止订单";
                break;
            default:
                s = "状态异常";
                break;
        }
        return s;
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(SYSVALUE.SDF);
        if (date == null) {
            return "null";
        }
        return sdf.format(date);
    }

    public static Date convert(String stringDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SYSVALUE.SDF);
        try {
            if (!(stringDate == null || stringDate.equals("") || stringDate.equals("null"))) {
                return simpleDateFormat.parse(stringDate);
            } else {
                return null;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
