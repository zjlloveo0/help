package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.NimUIKit;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.ServerListFragment;
import com.zjlloveo0.help.model.ServerUser;
import com.zjlloveo0.help.model.UserSchool;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserMsgActivity extends Activity {
    private SmartImageView iv_user_msg_image;
    private CircleImageView iv_user_msg_head;
    private TextView tv_user_msg_name;
    private TextView tv_user_msg_school;
    private TextView tv_user_msg_num;
    private TextView tv_user_msg_point;
    private TextView tv_user_msg_phone;
    private String userID;
    private UserSchool userSchool;
    private Bitmap headImg;
    String HOST = SYSVALUE.HOST;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_msg);
        userID = this.getIntent().getExtras().getString("userID").trim();
        findView();
        initView();
    }

    public void user_msg_back(View view) {
        finish();
    }

    public void talkToHe(View view) {
        NimUIKit.startP2PSession(getApplicationContext(), userID, null);
    }

    public void findView() {
        iv_user_msg_image = (SmartImageView) findViewById(R.id.iv_user_msg_image);
        iv_user_msg_head = (CircleImageView) findViewById(R.id.iv_user_msg_head);
        tv_user_msg_name = (TextView) findViewById(R.id.tv_user_msg_name);
        tv_user_msg_school = (TextView) findViewById(R.id.tv_user_msg_school);
        tv_user_msg_num = (TextView) findViewById(R.id.tv_user_msg_num);
        tv_user_msg_point = (TextView) findViewById(R.id.tv_user_msg_point);
        tv_user_msg_phone = (TextView) findViewById(R.id.tv_user_msg_phone);
    }

    public void initView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String res = "";
                res = Request2Server.getRequsetResult(HOST + "findUserSchool?id=" + userID);
                try {
                    long startTime = System.currentTimeMillis();
                    while ("".equals(res)) {
                        Thread.sleep(1000);
                        if (System.currentTimeMillis() - startTime > 10000L) {
                            showToast(getString(R.string.internet_error));
                            break;
                        }
                    }
                    if (!"".equals(res) && res.startsWith("{\"code\":")) {
                        JSONObject jsonRes = new JSONObject(res);
                        int code = jsonRes.getInt("code");
                        /**
                         * {"id":57,"name":"米糕","phone":"13253370013","password":"11111q",
                         * "img":"static/img/server/server_default.png","stuNum":"1","point":100,
                         * "collegeId":1,"star":5,"isEnable":1,"updateTime":"Fri May 05 15:05:19 CST 2017",
                         * "schoolName":"河南工业大学","collegeName":"信息科学与工程学院","createMissionNum":0,"createServerNum":4}
                         */
                        if (code == 400) {
                            JSONArray array = jsonRes.getJSONArray("content");
                            JSONObject obj = array.getJSONObject(0);
                            String id = obj.getString("id");
                            id = ("".equals(id) || id == null) ? "0" : id;
                            String name = obj.getString("name");
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
                            updateTime = ("".equals(updateTime) || updateTime == null) ? "0" : updateTime;
                            String schoolName = obj.getString("schoolName");
                            String collegeName = obj.getString("collegeName");
                            String createMissionNum = obj.getString("createMissionNum");
                            createMissionNum = ("".equals(createMissionNum) || createMissionNum == null) ? "0" : createMissionNum;
                            String createServerNum = obj.getString("createServerNum");
                            createServerNum = ("".equals(createServerNum) || createServerNum == null) ? "0" : createServerNum;
                            userSchool = new UserSchool();
                            userSchool.setId(Integer.valueOf(id));
                            userSchool.setName(name);
                            userSchool.setPhone(phone);
                            userSchool.setPassword(password);
                            userSchool.setImg(img);
                            userSchool.setStuNum(stuNum);
                            userSchool.setPoint(Integer.valueOf(point));
                            userSchool.setCollegeId(Integer.valueOf(collegeId));
                            userSchool.setStar(Integer.valueOf(star));
                            userSchool.setIsEnable(Integer.valueOf(isEnable));
                            userSchool.setUpdateTime(new Date(updateTime));
                            userSchool.setSchoolName(schoolName);
                            userSchool.setCollegeName(collegeName);
                            userSchool.setCreateMissionNum(Integer.valueOf(createMissionNum));
                            userSchool.setCreateServerNum(Integer.valueOf(createServerNum));
                            headImg = Request2Server.getBitMapFromUrl(HOST + userSchool.getImg());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_user_msg_image.setImageUrl(HOST + userSchool.getImg());
                                    iv_user_msg_head.setImageBitmap(headImg);
                                    tv_user_msg_name.setText(userSchool.getName());
                                    tv_user_msg_school.setText(userSchool.getSchoolName() + "-" + userSchool.getCollegeName());
                                    tv_user_msg_num.setText(userSchool.getStuNum());
                                    tv_user_msg_point.setText(userSchool.getPoint() + "");
                                    tv_user_msg_phone.setText(userSchool.getPhone());
                                }
                            });
                        } else {
                            showToast(getString(R.string.system_error));
                        }
                    }
                } catch (Exception e) {
                    showToast(getString(R.string.system_error));
                }
            }
        }).start();
    }

    public void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void helpYou(View view) {
        Intent intent = new Intent(getApplicationContext(), UserServerListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", userID);
        bundle.putString("userName", userSchool.getName());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void helpOthers(View view) {
    }
}