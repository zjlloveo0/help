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
import com.netease.nim.uikit.NimUIKit;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.ServerListFragment;
import com.zjlloveo0.help.bean.Orders;
import com.zjlloveo0.help.bean.ServerUser;
import com.zjlloveo0.help.other.LoadingDialog;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import org.json.JSONObject;

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
    private LoadingDialog dialog1;
    private LinearLayout ll_others_handle;
    private Button bt_edit;
    String HOST = SYSVALUE.HOST;
    public final static String PAR_KEY = "com.zjlloveo0.help.model.ServerUser";
    private ServerUser serverUser;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "系统异常", Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    dialog1.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_detail);
        dialog1 = new LoadingDialog(ServerDetailActivity.this);
        dialog1.setCanceledOnTouchOutside(false);
        findView();
        initView();
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServerDetailActivity.this, AddAndModfiyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("addOrModify", "modify");
                bundle.putString("type", "server");
                bundle.putParcelable(PAR_KEY, serverUser);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
        final View layout = getLayoutInflater().inflate(R.layout.alert_dialog_use_server, null);
        final EditText et_user_server_message = (EditText) layout.findViewById(R.id.et_user_server_message);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("使用服务");
        builder.setIcon(R.drawable.tab_service_a);
        builder.setView(layout);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog1.show();
                final Orders orders = new Orders();
                orders.setIsEnable(1);
                orders.setState(0);
                orders.setServerId(serverUser.getId());
                orders.setCreateId(SYSVALUE.currentUser.getId());
                orders.setUId(serverUser.getCreaterId());
                orders.setMessage(et_user_server_message.getText().toString());
                orders.setExchangePoint(serverUser.getExchangePoint());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = "";
                        Message msg = handler.obtainMessage();
                        try {
                            result = Request2Server.getRequsetResult(HOST + "createOrders" + Request2Server.getParamters(orders));
                            if (!"".equals(result)) {
                                msg.what = 1;
                                msg.obj = (new JSONObject(result)).getString("content");
                                handler.sendMessage(msg);
                            } else {
                                msg.what = 0;
                                handler.sendMessage(msg);
                            }
                        } catch (Exception e) {
                            msg.what = 0;
                            handler.sendMessage(msg);
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
        builder.create().show();
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
        if (SYSVALUE.currentUser.getId() == serverUser.getCreaterId()) {
            bt_edit.setVisibility(View.VISIBLE);
            ll_others_handle.setVisibility(View.GONE);
        } else {
            bt_edit.setVisibility(View.GONE);
            ll_others_handle.setVisibility(View.VISIBLE);
        }
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
        ll_others_handle = (LinearLayout) findViewById(R.id.ll_others_handle);
        bt_edit = (Button) findViewById(R.id.bt_edit);
    }

    public void back(View v) {
        finish();
    }
}
