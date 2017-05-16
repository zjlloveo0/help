package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.bean.MissionUser;
import com.zjlloveo0.help.bean.UserSchool;
import com.zjlloveo0.help.utils.InitApplication;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONObject;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class MissionOrdersDetailActivity extends Activity implements View.OnClickListener {
    private SmartImageView iv_detail_image;

    private LinearLayout ll_create;
    private CircleImageView iv_detail_create_head;
    private TextView tv_detail_create_name;
    private TextView tv_detail_create_point;

    private LinearLayout ll_used;
    private CircleImageView iv_detail_use_head;
    private TextView tv_detail_use_name;
    private TextView tv_detail_use_point;

    private TextView tv_order_id;
    private TextView tv_order_point;
    private TextView tv_order_finish_time;
    private TextView tv_order_create_time;
    private TextView tv_order_state;

    private TextView tv_server_detail_title;
    private TextView tv_server_detail_content;

    private Button bt_finish;
    private Button bt_evaluate;
    private Button bt_zhongzhi;
    private Button bt_querenzhongzhi;

    private String HOST = SYSVALUE.HOST;
    private String missionId = "";
    private MissionUser missionUser;
    Bitmap bitmap = null;
    Bitmap bitmap2 = null;
    private StringBuffer reqeust;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    setViewValue();
                    break;
                case 2:
                    reqeust.append(msg.obj.toString());
                    changeOrdersState();
                    break;
                case 3:
                    changeOrdersState();
                    break;
                case 4:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    getData();
                    break;
            }
        }
    };

    private void setViewValue() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (missionUser.getCreaterImg() != null && "".equals(missionUser.getCreaterImg()) && "null".equals(missionUser.getCreaterImg())) {
                    bitmap = Request2Server.getBitMapFromUrl(HOST + missionUser.getCreaterImg());
                }
                if (missionUser.getReceiverImg() != null && "".equals(missionUser.getReceiverImg()) && "null".equals(missionUser.getReceiverImg())) {
                    bitmap2 = Request2Server.getBitMapFromUrl(HOST + missionUser.getReceiverImg());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap != null) {
                            iv_detail_create_head.setImageBitmap(bitmap);
                        }
                        if (bitmap2 != null) {
                            iv_detail_use_head.setImageBitmap(bitmap2);
                        }
                    }
                });
            }
        }).start();
        iv_detail_image.setImageUrl(HOST + missionUser.getImg());
        //当前登录用户为任务接受者显示任务创建者信息
        if (SYSVALUE.currentUser.getId() == missionUser.getReceiverId()) {
            ll_create.setVisibility(View.VISIBLE);
            ll_used.setVisibility(View.GONE);
            tv_detail_create_name.setText(missionUser.getCreaterName());
            tv_detail_create_point.setText((missionUser.getCreaterPoint() == null ? 0 : missionUser.getCreaterPoint()) + "分");
            setButtonState(false, missionUser.getState());
            //当前登录用户为任务创建者显示任务接收者信息
        } else if (SYSVALUE.currentUser.getId() == missionUser.getCreaterId()) {
            ll_create.setVisibility(View.GONE);
            ll_used.setVisibility(View.VISIBLE);
            tv_detail_use_name.setText(missionUser.getReceiverName());
            tv_detail_use_point.setText((missionUser.getReceiverPoint() == null ? 0 : missionUser.getReceiverPoint()) + "分");
            setButtonState(true, missionUser.getState());
        }
        tv_order_id.setText(missionUser.getId() + "");
        tv_order_point.setText((missionUser.getExchangePoint() == null ? 0 : missionUser.getExchangePoint()) + "分");
        tv_order_create_time.setText(SystemUtil.formatDate(missionUser.getCreateTime()));
        tv_order_finish_time.setText(SystemUtil.formatDate(missionUser.getFinishTime()));
        tv_order_state.setText(SystemUtil.getMissionStateString(missionUser.getState()));

        tv_server_detail_title.setText(missionUser.getTitle());
        tv_server_detail_content.setText(missionUser.getContent());
    }

    private void setButtonState(boolean isOrderCreater, int state) {
        if (isOrderCreater) {
            //创建者
            String s = "";
            switch (state) {
                case 0:
                    s = "暂无人接受任务";
                    setButtonGone();
                    break;
                case 1:
                    s = "正在完成任务";
                    setButtonGone();
                    bt_zhongzhi.setVisibility(View.VISIBLE);
                    bt_zhongzhi.setClickable(true);
                    break;
                case 2:
                    s = "任务已完成";
                    setButtonGone();
                    bt_evaluate.setVisibility(View.VISIBLE);
                    bt_evaluate.setClickable(true);
                    break;
                case 3:
                    s = "任务已结束";
                    setButtonGone();
                    break;
                case 4:
                    s = "任务接受者终止订单";
                    setButtonGone();
                    bt_querenzhongzhi.setVisibility(View.VISIBLE);
                    bt_querenzhongzhi.setClickable(true);
                    break;
                case 5:
                    s = "任务发布者终止订单";
                    setButtonGone();
                    break;
                default:
                    s = "状态异常";
                    setButtonGone();
                    break;
            }
        } else {//接受者
            String s = "";
            switch (state) {
                case 0:
                    s = "暂无人接受任务";
                    setButtonGone();
                    break;
                case 1:
                    s = "正在完成任务";
                    setButtonGone();
                    bt_finish.setVisibility(View.VISIBLE);
                    bt_finish.setClickable(true);
                    bt_zhongzhi.setVisibility(View.VISIBLE);
                    bt_zhongzhi.setClickable(true);
                    break;
                case 2:
                    s = "任务已完成";
                    setButtonGone();
                    break;
                case 3:
                    s = "任务已结束";
                    setButtonGone();
                    break;
                case 4:
                    s = "任务接受者终止订单";
                    setButtonGone();
                    break;
                case 5:
                    s = "任务发布者终止订单";
                    setButtonGone();
                    bt_querenzhongzhi.setVisibility(View.VISIBLE);
                    bt_querenzhongzhi.setClickable(true);
                    break;
                default:
                    s = "状态异常";
                    setButtonGone();
                    break;
            }
        }
    }

    public void setButtonGone() {
        bt_zhongzhi.setVisibility(View.GONE);
        bt_finish.setVisibility(View.GONE);
        bt_querenzhongzhi.setVisibility(View.GONE);
        bt_evaluate.setVisibility(View.GONE);

        bt_zhongzhi.setClickable(false);
        bt_finish.setClickable(false);
        bt_querenzhongzhi.setClickable(false);
        bt_evaluate.setClickable(false);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_order_detail);
        InitApplication.setMissionOrdersDetailActivity(this);
        missionId = this.getIntent().getExtras().getString("missionId").trim();
        findView();
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = Request2Server.getRequsetResult(HOST + "findMissionUser?id=" + missionId);
                if (res.startsWith("{\"code\":1")) {
                    try {
                        JSONObject obj = (JSONObject) new JSONObject(res).getJSONArray("content").get(0);
                        MissionUser mUser = new MissionUser();

                        String id = obj.getString("id");
                        String state = obj.getString("state");
                        String createrId = obj.getString("createrId");
                        String createrPoint = obj.getString("createrPoint");
                        String receiverPoint = obj.getString("receiverPoint");
                        String receiverId = obj.getString("receiverId");
                        String exchangePoint = obj.getString("exchangePoint");
                        String isEnable = obj.getString("isEnable");
                        String finishTime = obj.getString("finishTime");

                        mUser.setCreaterName(obj.getString("createrName"));
                        mUser.setCreaterImg(obj.getString("createrImg"));
                        mUser.setReceiverName(obj.getString("receiverName"));
                        mUser.setReceiverImg(obj.getString("receiverImg"));
                        mUser.setId(id.equals("null") ? null : Integer.valueOf(id));
                        mUser.setCreaterPoint(createrPoint.equals("null") ? 0 : Integer.valueOf(createrPoint));
                        mUser.setReceiverPoint(receiverPoint.equals("null") ? 0 : Integer.valueOf(receiverPoint));
                        mUser.setId(id.equals("null") ? null : Integer.valueOf(id));
                        mUser.setState(state.equals("null") ? null : Integer.valueOf(state));
                        mUser.setCreaterId(createrId.equals("null") ? null : Integer.valueOf(createrId));
                        mUser.setReceiverId(receiverId.equals("null") ? null : Integer.valueOf(receiverId));
                        mUser.setCreateTime(SystemUtil.convert(obj.getString("createTime")));
                        mUser.setFinishTime(SystemUtil.convert(finishTime));
                        mUser.setTitle(obj.getString("title"));
                        mUser.setContent(obj.getString("content"));
                        mUser.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                        mUser.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                        mUser.setUpdateTime(SystemUtil.convert(obj.getString("updateTime")));
                        mUser.setImg(obj.getString("img"));
                        missionUser = mUser;

                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = "系统繁忙";
                        handler.sendMessage(msg);
                        e.printStackTrace();
                    }
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = 0;
                    msg.obj = "服务器繁忙";
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    public void findView() {
        iv_detail_image = (SmartImageView) findViewById(R.id.iv_detail_image);

        ll_create = (LinearLayout) findViewById(R.id.ll_create);
        iv_detail_create_head = (CircleImageView) findViewById(R.id.iv_detail_create_head);
        tv_detail_create_name = (TextView) findViewById(R.id.tv_detail_crate_name);
        tv_detail_create_point = (TextView) findViewById(R.id.tv_detail_create_point);

        ll_used = (LinearLayout) findViewById(R.id.ll_used);
        iv_detail_use_head = (CircleImageView) findViewById(R.id.iv_detail_use_head);
        tv_detail_use_name = (TextView) findViewById(R.id.tv_detail_use_name);
        tv_detail_use_point = (TextView) findViewById(R.id.tv_detail_use_point);

        tv_server_detail_title = (TextView) findViewById(R.id.tv_mission_detail_title);
        tv_server_detail_content = (TextView) findViewById(R.id.tv_mission_detail_content);

        tv_order_id = (TextView) findViewById(R.id.tv_order_id);
        tv_order_point = (TextView) findViewById(R.id.tv_order_point);
        tv_order_create_time = (TextView) findViewById(R.id.tv_order_create_time);
        tv_order_finish_time = (TextView) findViewById(R.id.tv_order_finish_time);
        tv_order_state = (TextView) findViewById(R.id.tv_order_state);

        bt_evaluate = (Button) findViewById(R.id.bt_evaluate);
        bt_finish = (Button) findViewById(R.id.bt_finish);
        bt_zhongzhi = (Button) findViewById(R.id.bt_zhongzhi);
        bt_querenzhongzhi = (Button) findViewById(R.id.bt_querenzhongzhi);
        bt_evaluate.setOnClickListener(this);
        bt_finish.setOnClickListener(this);
        bt_zhongzhi.setOnClickListener(this);
        bt_querenzhongzhi.setOnClickListener(this);
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        reqeust = new StringBuffer("updateMission?id=" + missionUser.getId());
        switch (v.getId()) {
            case R.id.bt_evaluate:
                reqeust.append("&state=3");
                dialogMsg("注意", "确认对方已完成任务?将把" + missionUser.getExchangePoint() + "积分转入对方账户");
                break;
            case R.id.bt_finish:
                reqeust.append("&state=2&finishTime=" + SystemUtil.formatDate(new Date()));
                dialogMsg("注意", "确认任务已完成?");
                break;
            case R.id.bt_zhongzhi:
                if (SYSVALUE.currentUser.getId() == missionUser.getCreaterId()) {
                    reqeust.append("&state=5");
                } else if (SYSVALUE.currentUser.getId() == missionUser.getReceiverId()) {
                    reqeust.append("&state=4");
                }
                dialogMsg("注意", "请求终止后,订单将回到任务列表（可由其他人继续完成），确认终止?");
                break;
            case R.id.bt_querenzhongzhi:
                reqeust.append("&state=0&receiverId=0");
                dialogMsg("注意", "请求终止后,订单将回到任务列表（可由其他人继续完成），确认终止?");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        missionId = "";
        InitApplication.setServerOrdersDetailActivity(null);
    }

    public String getMissionId() {
        return missionId;
    }

    public void createUserDetail(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", missionUser.getCreaterId() + "");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void useUserDetail(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", missionUser.getReceiverId() + "");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void dialogMsg(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setMessage(msg); //设置内容
        builder.setIcon(R.drawable.tab_service_a);//设置图标，图片id即可
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = handler.obtainMessage();
                msg.what = 3;
                handler.sendMessage(msg);
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    public void changeOrdersState() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = "";
                Message msg = handler.obtainMessage();
                try {
                    result = Request2Server.getRequsetResult(HOST + reqeust.toString());
                    if (!"".equals(result)) {
                        msg.what = 4;
                        msg.obj = (new JSONObject(result)).getString("content");
                        handler.sendMessage(msg);
                    } else {
                        msg.what = 0;
                        msg.obj = "网络异常";
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    msg.what = 0;
                    msg.obj = "系统异常";
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
