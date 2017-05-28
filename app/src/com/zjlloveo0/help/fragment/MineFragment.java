package com.zjlloveo0.help.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.choose.view.SelectAddressDialog;
import com.choose.view.myinterface.SelectAddressInterface;
import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nim.uikit.session.actions.PickImageAction;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.nos.NosService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.activity.LoginActivity;
import com.zjlloveo0.help.activity.MainActivity;
import com.zjlloveo0.help.activity.MissionOrdersActivity;
import com.zjlloveo0.help.activity.ServerOrdersActivity;
import com.zjlloveo0.help.bean.UserSchool;
import com.zjlloveo0.help.contact.helper.UserUpdateHelper;
import com.zjlloveo0.help.utils.DemoCache;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.zjlloveo0.help.R.id.et_content;


public class MineFragment extends Fragment implements View.OnClickListener, SelectAddressInterface {

    private View mRootView;
    private SmartImageView iv_user_msg_image;
    private CircleImageView iv_user_msg_head;
    private TextView tv_user_msg_name;
    private TextView tv_user_msg_school;
    private TextView tv_user_msg_num;
    private TextView tv_user_msg_point;
    private TextView tv_user_msg_phone;
    private LinearLayout ll_serverOrders;
    private LinearLayout ll_missionOrders;
    private LinearLayout ll_setSchool;
    private LinearLayout ll_setStuNum;
    private LinearLayout ll_setPassword;
    private Button bt_logout;
    private String userID;
    public static String path = "";
    private static final int PICK_AVATAR_REQUEST = 0x0E;
    private static final int AVATAR_TIME_OUT = 30000;
    private final String TAG = "MINE";
    AbortableFuture<String> uploadAvatarFuture;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private UserSchool userSchool;
    private Bitmap headImg;
    SelectAddressDialog dialog;
    String HOST = SYSVALUE.HOST;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    setValue();
                    break;
                case 2:
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("id", DemoCache.getAccount());
                    map2.put("stuNum", msg.obj.toString());
                    Request2Server.request(HOST + "updateUser", map2, mCallback);
                    break;
                case 3:
                    Map<String, Object> map3 = new HashMap<String, Object>();
                    map3.put("id", DemoCache.getAccount());
                    map3.put("password", msg.obj.toString());
                    Request2Server.request(HOST + "updateUser", map3, mCallback);
                    break;
            }
        }
    };
    private Callback mCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.i("help", "上传失败！");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "信息修改成功", Toast.LENGTH_SHORT).show();
                }
            });
            initView();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "MineFragment");
            mRootView = inflater.inflate(R.layout.activity_mine, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        userID = DemoCache.getAccount();//SYSVALUE.currentUser.getId()+"";
        findView();
        initView();
        return mRootView;
    }

    public void serverOrders() {
        startActivity(new Intent(getContext(), ServerOrdersActivity.class));
    }

    public void missionOrders() {
        startActivity(new Intent(getContext(), MissionOrdersActivity.class));
    }

    public void setSchool() {
        if (dialog == null) {
            dialog = new SelectAddressDialog(getActivity(),
                    this, SelectAddressDialog.STYLE_TWO, null);
        }
        dialog.showDialog();
    }

    public void setStuNum() {
        dialogInput("设置学号", "请输入学号", 2);
    }

    public void setPassword() {
        final View v = getActivity().getLayoutInflater().inflate(R.layout.reset_password, null);
        final EditText et_old = (EditText) v.findViewById(R.id.et_old_password);
        final EditText et_pass = (EditText) v.findViewById(R.id.et_new_password);
        final EditText et_pass2 = (EditText) v.findViewById(R.id.et_new_password2);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改密码");
        builder.setView(v);
        builder.setIcon(R.drawable.tab_mine_b);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String s = et_old.getText().toString();
                String s1 = et_pass.getText().toString();
                String s2 = et_pass2.getText().toString();
                if (s.equals(SYSVALUE.currentUser.getPassword())) {
                    boolean cancel = false;
                    String res = "";
                    if (TextUtils.isEmpty(s1)) {
                        cancel = true;
                        res = "新密码不能为空！";
                    } else if (!s1.matches(SYSVALUE.REGEX_PASSWORD)) {
                        cancel = true;
                        res = "密码至少6位，且应包含字母或者数字";
                    } else if (!s1.equals(s2)) {
                        cancel = true;
                        res = "两次密码不一致！";
                    }
                    final String ss = res;
                    if (cancel) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), ss, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        String result = s1;
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = result;
                        handler.sendMessage(msg);
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "旧密码不正确！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
            }
        });
        builder.create().show();
    }

    public void setHeadImg() {
        path = "";
        update();
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = R.string.set_head_image;
        option.crop = true;
        option.multiSelect = false;
        option.cropOutputImageWidth = 720;
        option.cropOutputImageHeight = 720;
        PickImageHelper.pickImage(getContext(), PICK_AVATAR_REQUEST, option);
    }

    public void logOut() {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("LoginInfo", Context.MODE_PRIVATE).edit();
        editor.putString("id", "");
        editor.putString("loginPhone", "");
        editor.putString("password", "");
        editor.commit();
        DemoCache.clear();
        getActivity().startActivity(new Intent(getContext(), LoginActivity.class));
        getActivity().finish();
    }

    public void setValue() {
        if (!(userSchool.getImg() == null || "".equals(userSchool.getImg()) || "null".equals(userSchool.getImg()))) {
            iv_user_msg_image.setImageUrl(HOST + userSchool.getImg());
        }
        if (headImg != null) {
            iv_user_msg_head.setImageBitmap(headImg);
        }
        tv_user_msg_name.setText(userSchool.getName());
        tv_user_msg_school.setText(userSchool.getSchoolInfo());
        tv_user_msg_num.setText(userSchool.getStuNum());
        tv_user_msg_point.setText(userSchool.getPoint() + "");
        tv_user_msg_phone.setText(userSchool.getPhone());
    }

    public void findView() {
        iv_user_msg_image = (SmartImageView) mRootView.findViewById(R.id.iv_user_msg_image);
        iv_user_msg_head = (CircleImageView) mRootView.findViewById(R.id.iv_user_msg_head);
        tv_user_msg_name = (TextView) mRootView.findViewById(R.id.tv_user_msg_name);
        tv_user_msg_school = (TextView) mRootView.findViewById(R.id.tv_user_msg_school);
        tv_user_msg_num = (TextView) mRootView.findViewById(R.id.tv_user_msg_num);
        tv_user_msg_point = (TextView) mRootView.findViewById(R.id.tv_user_msg_point);
        tv_user_msg_phone = (TextView) mRootView.findViewById(R.id.tv_user_msg_phone);
        ll_serverOrders = (LinearLayout) mRootView.findViewById(R.id.ll_serverOrders);
        ll_missionOrders = (LinearLayout) mRootView.findViewById(R.id.ll_missionOrders);
        ll_setSchool = (LinearLayout) mRootView.findViewById(R.id.ll_setSchool);
        ll_setStuNum = (LinearLayout) mRootView.findViewById(R.id.ll_setStuNum);
        ll_setPassword = (LinearLayout) mRootView.findViewById(R.id.ll_setPassword);
        bt_logout = (Button) mRootView.findViewById(R.id.bt_logout);
        ll_serverOrders.setOnClickListener(this);
        ll_missionOrders.setOnClickListener(this);
        ll_setSchool.setOnClickListener(this);
        ll_setStuNum.setOnClickListener(this);
        ll_setPassword.setOnClickListener(this);
        iv_user_msg_head.setOnClickListener(this);
        bt_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_serverOrders:
                serverOrders();
                break;
            case R.id.ll_missionOrders:
                missionOrders();
                break;
            case R.id.ll_setSchool:
                setSchool();
                break;
            case R.id.ll_setStuNum:
                setStuNum();
                break;
            case R.id.ll_setPassword:
                setPassword();
                break;
            case R.id.iv_user_msg_head:
                setHeadImg();
                break;
            case R.id.bt_logout:
                logOut();
                break;
        }
    }

    public void initView() {
        userSchool = new UserSchool();
        headImg = null;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = "";
                res = Request2Server.getRequsetResult(HOST + "findUserSchool?id=" + userID);
                try {
                    if (!"".equals(res) && res.startsWith("{\"code\":")) {
                        JSONObject jsonRes = new JSONObject(res);
                        int code = jsonRes.getInt("code");
                        if (code == 400) {
                            UserSchool us = null;
                            JSONArray array = jsonRes.getJSONArray("content");
                            JSONObject obj = array.getJSONObject(0);
                            String id = obj.getString("id");
                            id = ("".equals(id) || id == null) ? "0" : id;
                            String name = obj.getString("name");
                            String schoolInfo = obj.getString("schoolInfo");
                            String phone = obj.getString("phone");
                            String password = obj.getString("password");
                            String img = obj.getString("img");
                            String stuNum = obj.getString("stuNum");
                            String point = obj.getString("point");
                            point = ("".equals(point) || point == null) ? "0" : point;
                            String collegeId = obj.getString("collegeId");
                            collegeId = ("".equals(collegeId) || collegeId == null) ? "0" : collegeId;
                            String star = obj.getString("star");
                            star = ("".equals(star) || star == null) ? "0" : star;
                            String isEnable = obj.getString("isEnable");
                            isEnable = ("".equals(isEnable) || isEnable == null) ? "0" : isEnable;
                            String updateTime = obj.getString("updateTime");
                            String schoolName = obj.getString("schoolName");
                            String collegeName = obj.getString("collegeName");
                            String createMissionNum = obj.getString("createMissionNum");
                            createMissionNum = ("".equals(createMissionNum) || createMissionNum == null) ? "0" : createMissionNum;
                            String createServerNum = obj.getString("createServerNum");
                            createServerNum = ("".equals(createServerNum) || createServerNum == null) ? "0" : createServerNum;
                            us = new UserSchool();
                            us.setId(Integer.valueOf(id));
                            us.setName(name);
                            us.setSchoolInfo(schoolInfo);
                            us.setPhone(phone);
                            us.setPassword(password);
                            us.setImg(img);
                            us.setStuNum(stuNum);
                            us.setPoint((point == null || "".equals(point) || "null".equals(point)) ? 0 : Integer.valueOf(point));
                            us.setCollegeId((collegeId == null || "".equals(collegeId) || "null".equals(collegeId)) ? 0 : Integer.valueOf(collegeId));
                            us.setStar((star == null || "".equals(star) || "null".equals(star)) ? 0 : Integer.valueOf(star));
                            us.setIsEnable((isEnable == null || "".equals(isEnable) || "null".equals(isEnable)) ? 0 : Integer.valueOf(isEnable));
                            us.setUpdateTime(SystemUtil.convert(updateTime));
                            us.setSchoolName(schoolName);
                            us.setCollegeName(collegeName);
                            us.setCreateMissionNum((createMissionNum == null || "".equals(createMissionNum) || "null".equals(createMissionNum)) ? 0 : Integer.valueOf(createMissionNum));
                            us.setCreateServerNum((createServerNum == null || "".equals(createServerNum) || "null".equals(createServerNum)) ? 0 : Integer.valueOf(createServerNum));
                            if (us.getImg() != null && !"".equals(us.getImg()) && !"null".equals(us.getImg())) {
                                headImg = Request2Server.getBitMapFromUrl(HOST + us.getImg());
                            }
                            userSchool = us;
                            Message msg = handler.obtainMessage();
                            msg.what = 1;
                            handler.sendMessage(msg);
                        } else {
                            Message msg = handler.obtainMessage();
                            msg.what = 0;
                            msg.obj = getString(R.string.system_error);
                            handler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = getString(R.string.system_error);
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void update() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (path.equals("")) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateAvatar(path);
                    }
                });

            }
        }).start();
    }

    /**
     * 更新头像
     */
    public void updateAvatar(final String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }

        final File file = new File(path);
        if (file == null) {
            return;
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "head");
        map.put("uId", DemoCache.getAccount());
        Request2Server.uploadFile(file, HOST + "fileUpload", map, mCallback);
        DialogMaker.showProgressDialog(getContext(), null, null, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancelUpload(R.string.user_info_update_cancel);
            }
        }).setCanceledOnTouchOutside(false);


        LogUtil.i(TAG, "start upload avatar, local file path=" + file.getAbsolutePath());
        new Handler().postDelayed(outimeTask, AVATAR_TIME_OUT);
        uploadAvatarFuture = NIMClient.getService(NosService.class).upload(file, PickImageAction.MIME_JPEG);
        uploadAvatarFuture.setCallback(new RequestCallbackWrapper<String>() {
            @Override
            public void onResult(int code, String url, Throwable exception) {
                if (code == ResponseCode.RES_SUCCESS && !TextUtils.isEmpty(url)) {
                    LogUtil.i(TAG, "upload avatar success, url =" + url);

                    UserUpdateHelper.update(UserInfoFieldEnum.AVATAR, url, new RequestCallbackWrapper<Void>() {
                        @Override
                        public void onResult(int code, Void result, Throwable exception) {
                            if (code == ResponseCode.RES_SUCCESS) {
                                Toast.makeText(getContext(), R.string.head_update_success, Toast.LENGTH_SHORT).show();
                                onUpdateDone();
                            } else {
                                Toast.makeText(getContext(), R.string.head_update_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }); // 更新资料
                } else {
                    Toast.makeText(getContext(), R.string.user_info_update_failed, Toast
                            .LENGTH_SHORT).show();
                    onUpdateDone();
                }
            }
        });
    }

    private void cancelUpload(int resId) {
        if (uploadAvatarFuture != null) {
            uploadAvatarFuture.abort();
            Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
            onUpdateDone();
        }
    }

    private Runnable outimeTask = new Runnable() {
        @Override
        public void run() {
            cancelUpload(R.string.user_info_update_failed);
        }
    };

    private void onUpdateDone() {
        uploadAvatarFuture = null;
        DialogMaker.dismissProgressDialog();
    }

    public void dialogInput(String title, String hint, final int a) {
        final EditText et_content = new EditText(getContext());
        et_content.setHint(hint);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        builder.setIcon(R.drawable.tab_mine_b);
        builder.setView(et_content);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String result = et_content.getText().toString();
                Message msg = handler.obtainMessage();
                msg.what = a;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
        builder.create().show();
    }

    @Override
    public void setAreaString(String area) {
        tv_user_msg_school.setText(area);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", DemoCache.getAccount());
        map.put("schoolInfo", area);
        Request2Server.request(HOST + "updateUser", map, mCallback);
    }
}
