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
import com.zjlloveo0.help.bean.OrdersDetail;
import com.zjlloveo0.help.bean.UserSchool;
import com.zjlloveo0.help.utils.InitApplication;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONObject;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ServerOrdersDetailActivity extends Activity implements View.OnClickListener {
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
    private TextView tv_order_time;
    private TextView tv_order_state;

    private LinearLayout ll_re_msg;
    private TextView tv_server_re_msg;

    private LinearLayout ll_msg;
    private TextView tv_server_msg;

    private TextView tv_server_detail_title;
    private TextView tv_server_detail_content;

    private Button bt_start;
    private Button bt_finish;
    private Button bt_evaluate;
    private Button bt_jujue;
    private Button bt_zhongzhi;
    private Button bt_querenzhongzhi;
    //    private Button bt_delete;
    private Button bt_cancel;

    private String HOST = SYSVALUE.HOST;
    private String orderId = "";
    private OrdersDetail ordersDetail;
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
                if (ordersDetail.getCreateUser().getImg() != null && !"".equals(ordersDetail.getCreateUser().getImg()) && !"null".equals(ordersDetail.getCreateUser().getImg())) {
                    bitmap = Request2Server.getBitMapFromUrl(HOST + ordersDetail.getCreateUser().getImg());
                }
                if (ordersDetail.getOfUser().getImg() != null && !"".equals(ordersDetail.getOfUser().getImg()) && !"null".equals(ordersDetail.getOfUser().getImg())) {
                    bitmap2 = Request2Server.getBitMapFromUrl(HOST + ordersDetail.getOfUser().getImg());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (bitmap2 != null) {
                            iv_detail_create_head.setImageBitmap(bitmap2);
                        }
                        if (bitmap != null) {
                            iv_detail_use_head.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        }).start();
        iv_detail_image.setImageUrl(HOST + ordersDetail.getImg());
        //当前登录用户为服务使用者显示服务创建者信息
        if (SYSVALUE.currentUser.getId() == ordersDetail.getCreateId()) {
            ll_create.setVisibility(View.VISIBLE);
            ll_used.setVisibility(View.GONE);
            tv_detail_create_name.setText(ordersDetail.getOfUser().getName());
            tv_detail_create_point.setText(ordersDetail.getOfUser().getPoint() + "分");
            setButtonState(true, ordersDetail.getState());
            //当前登录用户为服务创建者显示服务使用者信息
        } else if (SYSVALUE.currentUser.getId() == ordersDetail.getUId()) {
            ll_create.setVisibility(View.GONE);
            ll_used.setVisibility(View.VISIBLE);
            tv_detail_use_name.setText(ordersDetail.getCreateUser().getName());
            tv_detail_use_point.setText(ordersDetail.getCreateUser().getPoint() + "分");
            setButtonState(false, ordersDetail.getState());
        }
        tv_order_id.setText(ordersDetail.getId() + "");
        tv_order_point.setText(ordersDetail.getExchangePoint() + "分");
        tv_order_time.setText(SystemUtil.formatDate(ordersDetail.getUpdateTime()));
        tv_order_state.setText(SystemUtil.getOrderStateString(ordersDetail.getState()));
        if (ordersDetail.getMessage() == null || "".equals(ordersDetail.getMessage()) || "null".equals(ordersDetail.getMessage())) {
            ll_msg.setVisibility(View.GONE);
        } else {
            ll_msg.setVisibility(View.VISIBLE);
            tv_server_msg.setText(ordersDetail.getMessage());
        }
        if (ordersDetail.getReMsg() == null || "".equals(ordersDetail.getReMsg()) || "null".equals(ordersDetail.getReMsg())) {
            ll_re_msg.setVisibility(View.GONE);
        } else {
            ll_re_msg.setVisibility(View.VISIBLE);
            tv_server_re_msg.setText(ordersDetail.getReMsg());
        }
        tv_server_detail_title.setText(ordersDetail.getTitle());
        tv_server_detail_content.setText(ordersDetail.getContent());
    }

    private void setButtonState(boolean isOrderCreater, int state) {
        if (isOrderCreater) {
            //使用者
            String s = "";
            switch (state) {
                case 0:
                    s = "服务已申请";
                    setButtonGone();
                    bt_cancel.setClickable(true);
                    bt_cancel.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    s = "订单已接受";
                    setButtonGone();
                    bt_zhongzhi.setVisibility(View.VISIBLE);
                    bt_zhongzhi.setClickable(true);
                    break;
                case 2:
                    s = "订单已完成";
                    setButtonGone();
                    bt_evaluate.setVisibility(View.VISIBLE);
                    bt_evaluate.setClickable(true);
                    break;
                case 3:
                    s = "订单已结束";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 4:
                    s = "订单被拒绝";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 5:
                    s = "服务使用者终止订单";
                    setButtonGone();
                    break;
                case 6:
                    s = "服务提供者终止订单";
                    setButtonGone();
                    bt_querenzhongzhi.setVisibility(View.VISIBLE);
                    bt_querenzhongzhi.setClickable(true);
                    break;
                case 7:
                    s = "订单终止";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 8:
                    s = "订单取消";
                    setButtonGone();
                    break;
            }
        } else {
            String s = "";
            switch (state) {
                case 0:
                    s = "服务已申请";
                    setButtonGone();
                    bt_jujue.setVisibility(View.VISIBLE);
                    bt_jujue.setClickable(true);
                    bt_start.setVisibility(View.VISIBLE);
                    bt_start.setClickable(true);
                    break;
                case 1:
                    s = "订单已接受";
                    setButtonGone();
                    bt_zhongzhi.setVisibility(View.VISIBLE);
                    bt_zhongzhi.setClickable(true);
                    bt_finish.setVisibility(View.VISIBLE);
                    bt_finish.setClickable(true);
                    break;
                case 2:
                    s = "订单已完成";
                    setButtonGone();
                    break;
                case 3:
                    s = "订单已结束";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 4:
                    s = "订单被拒绝";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 5:
                    s = "服务使用者终止订单";
                    setButtonGone();
                    break;
                case 6:
                    s = "服务提供者终止订单";
                    setButtonGone();
                    break;
                case 7:
                    s = "订单终止";
                    setButtonGone();
//                    bt_delete.setVisibility(View.VISIBLE);
//                    bt_delete.setClickable(true);
                    break;
                case 8:
                    s = "订单取消";
                    setButtonGone();
                    break;
            }
        }
    }

    public void setButtonGone() {
        bt_zhongzhi.setVisibility(View.GONE);
        bt_start.setVisibility(View.GONE);
        bt_finish.setVisibility(View.GONE);
        bt_evaluate.setVisibility(View.GONE);
        bt_jujue.setVisibility(View.GONE);
        bt_querenzhongzhi.setVisibility(View.GONE);
//        bt_delete.setVisibility(View.GONE);
        bt_cancel.setVisibility(View.GONE);

        bt_zhongzhi.setClickable(false);
        bt_start.setClickable(false);
        bt_finish.setClickable(false);
        bt_evaluate.setClickable(false);
        bt_jujue.setClickable(false);
        bt_querenzhongzhi.setClickable(false);
//        bt_delete.setClickable(false);
        bt_cancel.setClickable(false);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_order_detail);
        InitApplication.setServerOrdersDetailActivity(this);
        orderId = this.getIntent().getExtras().getString("orderId").trim();
        findView();
        getData();
    }

    public void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String res = Request2Server.getRequsetResult(HOST + "findOrdersDetail?id=" + orderId);
                if (res.startsWith("{\"code\":1")) {
                    try {
                        JSONObject obj = (JSONObject) new JSONObject(res).getJSONArray("content").get(0);
                        OrdersDetail orderDetail = new OrdersDetail();
                        String img = obj.getString("img");
                        String title = obj.getString("title");
                        String content = obj.getString("content");
                        String reMsg = obj.getString("reMsg");
                        String serverId = obj.getString("serverId");
                        String message = obj.getString("message");
                        String id = obj.getString("id");
                        String createId = obj.getString("createId");
                        String uId = obj.getString("uId");
                        String state = obj.getString("state");
                        String exchangePoint = obj.getString("exchangePoint");
                        String isEnable = obj.getString("isEnable");
                        String updateTime = obj.getString("updateTime");

                        orderDetail.setImg(img);
                        orderDetail.setTitle(title);
                        orderDetail.setContent(content);
                        orderDetail.setReMsg(reMsg);
                        orderDetail.setServerId(serverId.equals("null") ? null : Integer.valueOf(serverId));
                        orderDetail.setMessage(message);
                        orderDetail.setId(id.equals("null") ? null : Integer.valueOf(id));
                        orderDetail.setCreateId(createId.equals("null") ? null : Integer.valueOf(createId));
                        orderDetail.setUId(uId.equals("null") ? null : Integer.valueOf(uId));
                        orderDetail.setState(state.equals("null") ? null : Integer.valueOf(state));
                        orderDetail.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                        orderDetail.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                        orderDetail.setUpdateTime(SystemUtil.convert(updateTime));

                        JSONObject cu = obj.getJSONObject("createUser");
                        UserSchool cUser = getUserSchool(cu);
                        JSONObject ou = obj.getJSONObject("ofUser");
                        UserSchool uUser = getUserSchool(ou);
                        orderDetail.setCreateUser(cUser);
                        orderDetail.setOfUser(uUser);
                        ordersDetail = orderDetail;

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

    public UserSchool getUserSchool(JSONObject cu) throws Exception {
        UserSchool userSchool = new UserSchool();
        String cuId = cu.getString("id");
        String cuName = cu.getString("name");
        String cuPhone = cu.getString("phone");
        String cuPassword = cu.getString("password");
        String cuImg = cu.getString("img");
        String cuStuNum = cu.getString("stuNum");
        String cuPoint = cu.getString("point");
        String cuCollegeId = cu.getString("collegeId");
        String cuStar = cu.getString("star");
        String cuIsEnable = cu.getString("isEnable");
        String cuUpdateTime = cu.getString("updateTime");
        String cuSchoolName = cu.getString("schoolName");
        String cuCollegeName = cu.getString("collegeName");
        String cuCreateMissionNum = cu.getString("createMissionNum");
        String cuCreateServerNum = cu.getString("createServerNum");

        userSchool.setId(cuId.equals("null") ? null : Integer.valueOf(cuId));
        userSchool.setName(cuName);
        userSchool.setPhone(cuPhone);
        userSchool.setPassword(cuPassword);
        userSchool.setImg(cuImg);
        userSchool.setStuNum(cuStuNum);
        userSchool.setPoint(cuPoint.equals("null") ? null : Integer.valueOf(cuPoint));
        userSchool.setCollegeId(cuCollegeId.equals("null") ? null : Integer.valueOf(cuCollegeId));
        userSchool.setStar(cuStar.equals("null") ? null : Integer.valueOf(cuStar));
        userSchool.setIsEnable(cuIsEnable.equals("null") ? null : Integer.valueOf(cuIsEnable));
        userSchool.setUpdateTime(SystemUtil.convert(cuUpdateTime));
        userSchool.setSchoolName(cuSchoolName);
        userSchool.setCollegeName(cuCollegeName);
        userSchool.setCreateMissionNum(cuCreateMissionNum.equals("null") ? null : Integer.valueOf(cuCreateMissionNum));
        userSchool.setCreateServerNum(cuCreateServerNum.equals("null") ? null : Integer.valueOf(cuCreateServerNum));
        return userSchool;
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

        tv_server_detail_title = (TextView) findViewById(R.id.tv_server_detail_title);
        tv_server_detail_content = (TextView) findViewById(R.id.tv_server_detail_content);

        tv_order_id = (TextView) findViewById(R.id.tv_order_id);
        tv_order_point = (TextView) findViewById(R.id.tv_order_point);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        tv_order_state = (TextView) findViewById(R.id.tv_order_state);

        ll_re_msg = (LinearLayout) findViewById(R.id.ll_re_msg);
        tv_server_re_msg = (TextView) findViewById(R.id.tv_server_re_msg);

        ll_msg = (LinearLayout) findViewById(R.id.ll_msg);
        tv_server_msg = (TextView) findViewById(R.id.tv_server_msg);

        bt_evaluate = (Button) findViewById(R.id.bt_evaluate);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_finish = (Button) findViewById(R.id.bt_finish);
        bt_jujue = (Button) findViewById(R.id.bt_jujue);
        bt_zhongzhi = (Button) findViewById(R.id.bt_zhongzhi);
        bt_querenzhongzhi = (Button) findViewById(R.id.bt_querenzhongzhi);
//        bt_delete= (Button) findViewById(R.id.bt_delete);
        bt_cancel = (Button) findViewById(R.id.bt_cancel);
        bt_evaluate.setOnClickListener(this);
        bt_start.setOnClickListener(this);
        bt_finish.setOnClickListener(this);
        bt_jujue.setOnClickListener(this);
        bt_zhongzhi.setOnClickListener(this);
        bt_querenzhongzhi.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        reqeust = new StringBuffer("updateOrders?id=" + ordersDetail.getId());
        switch (v.getId()) {
            case R.id.bt_evaluate:
                reqeust.append("&state=3&reMsg=");
                dialogInput();
                break;
            case R.id.bt_start:
                reqeust.append("&state=1");
                dialogMsg("注意", "确认接受此订单?");
                break;
            case R.id.bt_finish:
                reqeust.append("&state=2");
                dialogMsg("注意", "确认订单已完成?");
                break;
            case R.id.bt_jujue:
                reqeust.append("&state=4");
                dialogMsg("注意", "确认拒绝此订单?");
                break;
            case R.id.bt_zhongzhi:
                if (SYSVALUE.currentUser.getId() == ordersDetail.getCreateId()) {
                    reqeust.append("&state=5");
                } else if (SYSVALUE.currentUser.getId() == ordersDetail.getUId()) {
                    reqeust.append("&state=6");
                }
                dialogMsg("注意", "请求终止后,订单将无效，确认终止?");
                break;
            case R.id.bt_querenzhongzhi:
                reqeust.append("&state=7");
                dialogMsg("注意", "请求终止后,订单将无效，确认终止?");
                break;
            case R.id.bt_cancel:
                reqeust.append("&state=8");
                dialogMsg("注意", "确认取消订单?");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderId = "";
        InitApplication.setServerOrdersDetailActivity(null);
    }

    public String getOrderId() {
        return orderId;
    }

    public void createUserDetail(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", ordersDetail.getUId() + "");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void useUserDetail(View view) {
        Intent intent = new Intent(getApplicationContext(), UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", ordersDetail.getCreateId() + "");
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

    public void dialogInput() {
        final View layout = getLayoutInflater().inflate(R.layout.alert_dialog_input_evaluate, null);
        final EditText et_user_server_re_msg = (EditText) layout.findViewById(R.id.et_user_server_re_msg);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("评价服务");
        builder.setIcon(R.drawable.tab_service_a);
        builder.setView(layout);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String result = et_user_server_re_msg.getText().toString();
                Message msg = handler.obtainMessage();
                msg.what = 2;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        });
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
