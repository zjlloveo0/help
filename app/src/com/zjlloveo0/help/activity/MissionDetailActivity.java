package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.NimUIKit;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.MissionListFragment;
import com.zjlloveo0.help.model.MissionUser;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

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
    private Button bt_use_this;
    String HOST = SYSVALUE.HOST;
    private MissionUser missionUser;

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
    }

    /**
     * isEnable
     * 是否可用
     * 0 不可用
     * 1 可用
     * 2 已接受未完成
     * 3 已完成未评价
     * 4 已完成已评价
     */
    public void initView() {
        missionUser = getIntent().getParcelableExtra(MissionListFragment.PAR_KEY);

        if (missionUser.getReceiverId() != null && missionUser.getReceiverId() != 0) {
            ll_receiver.setVisibility(View.VISIBLE);
            if (missionUser.getIsEnable() == 2) {
                bt_use_this.setText("我能更好的帮TA");
            } else if (missionUser.getIsEnable() > 2) {
                bt_use_this.setVisibility(View.GONE);
            }
        } else {
            ll_receiver.setVisibility(View.GONE);
            bt_use_this.setText("我要帮TA");
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
        bt_use_this = (Button) findViewById(R.id.bt_use_this);
    }

    public void back(View v) {
        finish();
    }

}
