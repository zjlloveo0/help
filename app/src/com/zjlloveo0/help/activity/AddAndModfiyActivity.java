package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.netease.nim.uikit.common.media.picker.PickImageHelper;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.bean.MissionUser;
import com.zjlloveo0.help.bean.ServerUser;
import com.zjlloveo0.help.fragment.MissionListFragment;
import com.zjlloveo0.help.utils.DemoCache;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddAndModfiyActivity extends Activity {
    private SmartImageView iv_upload_image;
    private TextView tv_head_title;
    private TextView tv_alert_msg;
    private EditText et_title;
    private EditText et_content;
    private EditText et_point;
    private Button bt_submit;
    private String image_local_url;
    private String addOrModify;
    private String type;
    private String request;
    private String id = "";
    private static final int PICK_AVATAR_REQUEST = 0x01;
    String HOST = SYSVALUE.HOST;
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    showToast(msg.obj.toString());
                    break;
                case 1:
                    saveMsg();
                    break;
                case 2:
                    break;
                case 3:
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
            String s = response.body().string();
            try {
                showToast(new JSONObject(s).getString("content"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            DialogMaker.dismissProgressDialog();
            finish();
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_or_modify_layout);
        addOrModify = this.getIntent().getExtras().getString("addOrModify").trim();
        type = this.getIntent().getExtras().getString("type").trim();
        findView();
        switch (addOrModify) {
            case "add":
                bt_submit.setText("立即发布");
                request = HOST + "addSth";
                tv_alert_msg.setVisibility(View.VISIBLE);
                switch (type) {
                    case "server":
                        tv_head_title.setText("新增服务");
                        break;
                    case "mission":
                        tv_head_title.setText("新增任务");
                        break;
                    default:
                        showToast("系统异常");
                        break;
                }
                break;
            case "modify":
                bt_submit.setText("保存");
                request = HOST + "modifySth";
                tv_alert_msg.setVisibility(View.GONE);
                switch (type) {
                    case "server":
                        tv_head_title.setText("编辑服务");
                        ServerUser serverUser = getIntent().getParcelableExtra(ServerDetailActivity.PAR_KEY);
                        id = String.valueOf(serverUser.getId());
                        et_title.setText(serverUser.getTitle());
                        et_content.setText(serverUser.getContent());
                        et_point.setText(String.valueOf(serverUser.getExchangePoint()));
                        iv_upload_image.setImageUrl(HOST + serverUser.getImg());
                        break;
                    case "mission":
                        tv_head_title.setText("编辑任务");
                        MissionUser missionUser = getIntent().getParcelableExtra(MissionDetailActivity.PAR_KEY);
                        id = String.valueOf(missionUser.getId());
                        et_title.setText(missionUser.getTitle());
                        et_content.setText(missionUser.getContent());
                        et_point.setText(String.valueOf(missionUser.getExchangePoint()));
                        iv_upload_image.setImageUrl(HOST + missionUser.getImg());
                        break;
                    default:
                        showToast("系统异常");
                        break;
                }
                break;
            default:
                showToast("系统异常");
                break;
        }
        addListener();
    }

    private boolean checkInfo() {
        if (TextUtils.isEmpty(image_local_url)) {
            showToast("请选择图片！");
            return false;
        }
        if (TextUtils.isEmpty(et_title.getText()) || et_title.getText().length() > 36) {
            showToast("标题不能超过36字，且不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_content.getText()) || et_content.getText().length() > 500) {
            showToast("内容不能超过500字，且不能为空");
            return false;
        }
        if (TextUtils.isEmpty(et_point.getText()) || Integer.valueOf(et_point.getText().toString()) < 0) {
            showToast("积分不能小于0，且不能为空");
            return false;
        }
        return true;
    }

    private void addListener() {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInfo()) {
                    return;
                }
                DialogMaker.showProgressDialog(AddAndModfiyActivity.this, null, null, true, null).setCanceledOnTouchOutside(false);
                if (TextUtils.isEmpty(image_local_url)) {
                    dialogMsg("确认不设置图片?", 1);
                } else {
                    saveMsg();
                }
            }
        });
    }

    private void saveMsg() {
        Map<String, Object> map = new HashMap<String, Object>();
        File file = null;
        map.put("type", type);
        if (addOrModify.equals("add")) {
            map.put("createrId", DemoCache.getAccount() + "");
        } else if (addOrModify.equals("modify")) {
            map.put("id", id);
        }
        map.put("title", et_title.getText());
        map.put("content", et_content.getText());
        map.put("exchangePoint", et_point.getText());
        file = new File(image_local_url);
        Request2Server.uploadFile(file, request, map, mCallback);
    }

    public void dialogMsg(String title, final int a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(R.drawable.tab_message_b);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Message msg = handler.obtainMessage();
                msg.what = a;
                handler.sendMessage(msg);
            }
        });
        builder.create().show();
    }

    public void findView() {
        iv_upload_image = (SmartImageView) findViewById(R.id.iv_upload_image);
        tv_head_title = (TextView) findViewById(R.id.tv_head_title);
        tv_alert_msg = (TextView) findViewById(R.id.tv_alert_msg);
        et_title = (EditText) findViewById(R.id.et_title);
        et_content = (EditText) findViewById(R.id.et_content);
        et_point = (EditText) findViewById(R.id.et_point);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    public void showToast(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void uploadImage(View v) {
        PickImageHelper.PickImageOption option = new PickImageHelper.PickImageOption();
        option.titleResId = R.string.choice_pic;
        option.crop = true;
        option.multiSelect = false;
        option.cropOutputImageWidth = 800;
        option.cropOutputImageHeight = 450;
        PickImageHelper.pickImage(this, PICK_AVATAR_REQUEST, option);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_AVATAR_REQUEST) {
            image_local_url = data.getStringExtra(com.netease.nim.uikit.session.constant.Extras.EXTRA_FILE_PATH);
            File file = new File(image_local_url);
            iv_upload_image.setImageURI(Uri.fromFile(file));
            iv_upload_image.setImageUrl(image_local_url);
            tv_alert_msg.setVisibility(View.GONE);
        }
    }

    public void back(View v) {
        finish();
    }
}
