package com.zjlloveo0.help.utils;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.activity.MainActivity;
import com.zjlloveo0.help.activity.MissionOrdersDetailActivity;
import com.zjlloveo0.help.activity.ServerOrdersDetailActivity;
import com.zjlloveo0.help.location.NimDemoLocationProvider;

import java.util.List;


/**
 * Created by zjlloveo0 on 2017/3/6.
 */

public class InitApplication extends Application {
    //小米推送
    private static DemoHandler sHandler = null;
    private static MainActivity sMainActivity = null;
    private static ServerOrdersDetailActivity sServerOrdersDetailActivity = null;
    private static MissionOrdersDetailActivity mMissionOrdersDetailActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        DemoCache.setContext(this);
        //小米推送
        // 注册push服务，注册成功后会向DemoMessageReceiver发送广播
        // 可以从DemoMessageReceiver的onCommandResult方法中MiPushCommandMessage对象参数中获取注册信息
        if (shouldInit()) {
            MiPushClient.registerPush(this, SYSVALUE.APP_ID, SYSVALUE.APP_KEY);
        }

        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(SYSVALUE.TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(SYSVALUE.TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
        if (sHandler == null) {
            sHandler = new DemoHandler(getApplicationContext());
        }

        //网易云信
        NIMClient.init(this, loginInfo(), options());
        if (inMainProcess()) {
            // 在主进程中初始化UI组件，判断所属进程方法请参见demo源码。
            NimUIKit.init(this);
            NimUIKit.setLocationProvider(new NimDemoLocationProvider());
        }
    }

    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = SystemUtil.getProcessName(this);
        return packageName.equals(processName);
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = MainActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.drawable.help_b;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.zjlloveo0.help/raw/msg";
        options.statusBarNotificationConfig = config;

        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        String sdkPath = Environment.getExternalStorageDirectory() + "/" + getPackageName() + "/nim";
        options.sdkStorageRootPath = sdkPath;

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = 480 / 2;

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.drawable.help_a;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId,
                                                           SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        SharedPreferences sp = getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        LoginInfo logininfo;
        String account = "";
        String token = "";
        String password = "";
        if (sp != null) {
            account = sp.getString("account", "");
            token = sp.getString("token", "");
            password = sp.getString("password", "");
        }
        if (!("".equals(account) || "".equals(token) || "".equals(password))) {

        }
        return null;
    }


    //小米推送
    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static DemoHandler getHandler() {
        return sHandler;
    }

    public static void setMainActivity(MainActivity activity) {
        sMainActivity = activity;
    }

    public static void setServerOrdersDetailActivity(ServerOrdersDetailActivity serverOrdersDetailActivity) {
        sServerOrdersDetailActivity = serverOrdersDetailActivity;
    }

    public static void setMissionOrdersDetailActivity(MissionOrdersDetailActivity missionOrdersDetailActivity) {
        mMissionOrdersDetailActivity = missionOrdersDetailActivity;
    }

    public static class DemoHandler extends Handler {

        private Context context;

        public DemoHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            if (sMainActivity != null) {
                sMainActivity.refreshLogInfo();
            }
            if (msg.what == 0) {//跳转页面
                Intent intent = new Intent();
                switch (msg.arg1) {
                    case SYSVALUE.SERVER_ORDERS_DETAIL:
                        intent.setClass(context, ServerOrdersDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("orderId", msg.obj.toString());
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        break;
                }
            } else if (msg.what == 1) {//刷新数据
                switch (msg.arg1) {
                    case SYSVALUE.SERVER_ORDERS_DETAIL:
                        if (sServerOrdersDetailActivity != null && sServerOrdersDetailActivity.getOrderId().equals(msg.obj.toString())) {
                            sServerOrdersDetailActivity.getData();
                        }
                        break;
                }

            }
            String s = (String) msg.obj;
            if (!TextUtils.isEmpty(s)) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
