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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.NimUIKit;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.MissionListFragment;
import com.zjlloveo0.help.bean.MissionUser;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MissionDetailActivity extends Activity {
    private SmartImageView iv_mission_detail_image;
    private CircleImageView iv_create_detail_head;
    private TextView tv_create_detail_name;
    private TextView tv_create_detail_point;
    private LinearLayout ll_receiver;
    private CircleImageView iv_receiver_detail_head;
    private TextView tv_receiver_detail_name;
    private TextView tv_receiver_detail_point;
    private TextView tv_mission_detail_title;
    private TextView tv_mission_detail_exPoint;
    private TextView tv_mission_detail_content;
    String HOST = SYSVALUE.HOST;
    private StringBuffer reqeust;
    private MissionUser missionUser;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    changeOrdersState();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_detail);
        findView();
        initView();
    }

    public void receiverUserDetail(View view) {
        Intent intent = new Intent(MissionDetailActivity.this, UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", missionUser.getReceiverId().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void createUserDetail(View view) {
        Intent intent = new Intent(MissionDetailActivity.this, UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", missionUser.getCreaterId().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void talkToHeDetail(View view) {
        NimUIKit.startP2PSession(getApplicationContext(), missionUser.getCreaterId() + "", null);
    }

    public void applyMission(View view) {
        reqeust = new StringBuffer();
        reqeust.append("updateMission?id=" + missionUser.getId());
        reqeust.append("&receiverId=" + SYSVALUE.currentUser.getId());
        reqeust.append("&state=1");
        dialogMsg("注意", "确定要完成这个任务吗?");

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
                msg.what = 1;
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
                    msg.what = 0;
                    msg.obj = (new JSONObject(result)).getString("content");
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    msg.what = 0;
                    msg.obj = "系统异常";
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public void initView() {
        missionUser = getIntent().getParcelableExtra(MissionListFragment.PAR_KEY);

        if (missionUser.getReceiverId() != null && missionUser.getReceiverId() != 0) {
            ll_receiver.setVisibility(View.VISIBLE);
        } else {
            ll_receiver.setVisibility(View.GONE);
        }
        iv_mission_detail_image.setImageUrl(HOST + missionUser.getImg());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = Request2Server.getBitMapFromUrl(HOST + missionUser.getCreaterImg());
                final Bitmap bitmap2 = Request2Server.getBitMapFromUrl(HOST + missionUser.getReceiverImg());
                while (true) {
                    if (bitmap != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_create_detail_head.setImageBitmap(bitmap);
                                iv_receiver_detail_head.setImageBitmap(bitmap2);
                            }
                        });
                        break;
                    }
                }
            }
        }).start();
        tv_create_detail_name.setText(missionUser.getCreaterName());
        tv_create_detail_point.setText("积分：" + missionUser.getCreaterPoint());
        tv_receiver_detail_name.setText(missionUser.getReceiverName());
        tv_receiver_detail_point.setText("积分：" + missionUser.getReceiverPoint());
        tv_mission_detail_title.setText(missionUser.getTitle());
        tv_mission_detail_exPoint.setText("奖励" + missionUser.getExchangePoint() + "积分");
        tv_mission_detail_content.setText(missionUser.getContent());
    }

    public void findView() {
        iv_mission_detail_image = (SmartImageView) findViewById(R.id.iv_mission_detail_image);
        iv_create_detail_head = (CircleImageView) findViewById(R.id.iv_create_detail_head);
        tv_create_detail_name = (TextView) findViewById(R.id.tv_create_detail_name);
        tv_create_detail_point = (TextView) findViewById(R.id.tv_create_detail_point);
        ll_receiver = (LinearLayout) findViewById(R.id.ll_receiver);
        iv_receiver_detail_head = (CircleImageView) findViewById(R.id.iv_receiver_detail_head);
        tv_receiver_detail_name = (TextView) findViewById(R.id.tv_receiver_detail_name);
        tv_receiver_detail_point = (TextView) findViewById(R.id.tv_receiver_detail_point);
        tv_mission_detail_title = (TextView) findViewById(R.id.tv_mission_detail_title);
        tv_mission_detail_exPoint = (TextView) findViewById(R.id.tv_mission_detail_exPoint);
        tv_mission_detail_content = (TextView) findViewById(R.id.tv_mission_detail_content);
    }

    public void back(View v) {
        finish();
    }

}
