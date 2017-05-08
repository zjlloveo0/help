package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.NimUIKit;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.ServerListFragment;
import com.zjlloveo0.help.model.ServerUser;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServerDetailActivity extends Activity {
    private SmartImageView iv_detail_image;
    private CircleImageView iv_detail_head;
    private TextView tv_detail_headTitle;
    private TextView tv_detail_name;
    private TextView tv_detail_point;
    private TextView tv_detail_title;
    private TextView tv_detail_exPoint;
    private TextView tv_detail_content;
    String HOST = SYSVALUE.HOST;
    private ServerUser serverUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        findView();
        initView();
    }

    public void userDetail(View view) {
        Intent intent = new Intent(ServerDetailActivity.this, UserMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userID", serverUser.getCreaterId().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void talkToHeDetail(View view) {
        NimUIKit.startP2PSession(getApplicationContext(), serverUser.getCreaterId() + "", null);
    }

    public void useServer(View view) {
    }


    public void initView() {
        serverUser = getIntent().getParcelableExtra(ServerListFragment.PAR_KEY);
        iv_detail_image.setImageUrl(HOST + serverUser.getImg());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = Request2Server.getBitMapFromUrl(HOST + serverUser.getCreaterImg());
                while (true) {
                    if (bitmap != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_detail_head.setImageBitmap(bitmap);
                            }
                        });
                        break;
                    }
                }
            }
        }).start();
        tv_detail_headTitle.setText("服务详情");
        tv_detail_name.setText(serverUser.getCreaterName());
        tv_detail_point.setText("积分：" + serverUser.getCreaterPoint());
        tv_detail_title.setText(serverUser.getTitle());
        tv_detail_exPoint.setText(serverUser.getExchangePoint() + "积分/次");
        tv_detail_content.setText(serverUser.getContent());
    }

    public void findView() {
        iv_detail_image = (SmartImageView) findViewById(R.id.iv_detail_image);
        iv_detail_head = (CircleImageView) findViewById(R.id.iv_detail_head);
        tv_detail_headTitle = (TextView) findViewById(R.id.tv_detail_headTitle);
        tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
        tv_detail_point = (TextView) findViewById(R.id.tv_detail_point);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_exPoint = (TextView) findViewById(R.id.tv_detail_exPoint);
        tv_detail_content = (TextView) findViewById(R.id.tv_detail_content);
    }

    public void back(View v) {
        finish();
    }
}
