package com.zjlloveo0.help.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.bean.MissionUser;
import com.zjlloveo0.help.other.CustomRefreshListView;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserMissionListActivity extends Activity {
    private CustomRefreshListView lv_MissionList;
    private TextView tv_detail_headTitle;
    private TextView tv_no_data_mission;
    private LinearLayout ll_no_data;
    private List<MissionUser> missionUserList = new ArrayList<MissionUser>();
    private List<MissionUser> missionUserList1 = new ArrayList<MissionUser>();
    private myAdapter adapter = new myAdapter();
    private String userID;
    public final static String PAR_KEY = "com.zjlloveo0.help.model.MissionUser";
    String HOST = SYSVALUE.HOST;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ll_no_data.setVisibility(View.GONE);
                    missionUserList.clear();
                    missionUserList.addAll(missionUserList1);
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    ll_no_data.setVisibility(View.GONE);
                    missionUserList.clear();
                    missionUserList.addAll(missionUserList1);
                    adapter.notifyDataSetChanged();
                    lv_MissionList.completeRefresh();
                    break;
                case 2:
                    lv_MissionList.failRefresh();
                    break;
                case 3:
                    missionUserList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    missionUserList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    lv_MissionList.noResRefresh();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userID = this.getIntent().getExtras().getString("userID").trim();
        String userName = this.getIntent().getExtras().getString("userName").trim();
        updateData(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.missionlist_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.measure(0, 0);
        tv_no_data_mission = (TextView) findViewById(R.id.tv_no_data_mission);
        tv_no_data_mission.setText("TA还没有完成任何任务，去别处看看吧");
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        ll_no_data.setVisibility(View.GONE);
        tv_detail_headTitle = (TextView) findViewById(R.id.tv_detail_headTitle);
        tv_detail_headTitle.setText(userName + "的任务");
        lv_MissionList = (CustomRefreshListView) findViewById(R.id.lv_missionList);
        lv_MissionList.setPadding(0, toolbar.getMeasuredHeight(), 0, 0);
        lv_MissionList.setAdapter(adapter);
        lv_MissionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                MissionUser mu = viewHolder.missionUser;
                Intent intent = new Intent(getApplicationContext(), MissionDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PAR_KEY, mu);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        lv_MissionList.setOnRefreshListener(new CustomRefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                updateData(false);
            }

            @Override
            public void onLoadingMore() {
                Toast.makeText(getApplicationContext(), "加载", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void back(View view) {
        finish();
    }

    public class ViewHolder {
        SmartImageView iv_mission_image;
        TextView tv_mission_username;
        TextView tv_mission_title;
        TextView tv_mission_description;
        TextView tv_mission_point;
        MissionUser missionUser;
    }

    public class myAdapter extends BaseAdapter {

        public myAdapter() {

        }

        @Override
        public int getCount() {
            return missionUserList.size();
        }

        @Override
        public Object getItem(int position) {
            return missionUserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.missionlist_items, null);
                holder = new ViewHolder();
                holder.iv_mission_image = (SmartImageView) convertView.findViewById(R.id.iv_mission_image);
                holder.tv_mission_username = (TextView) convertView.findViewById(R.id.tv_mission_username);
                holder.tv_mission_title = (TextView) convertView.findViewById(R.id.tv_mission_title);
                holder.tv_mission_description = (TextView) convertView.findViewById(R.id.tv_mission_description);
                holder.tv_mission_point = (TextView) convertView.findViewById(R.id.tv_mission_point);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.missionUser = missionUserList.get(position);
            holder.iv_mission_image.setImageUrl(HOST + missionUserList.get(position).getImg());
            holder.tv_mission_username.setText(missionUserList.get(position).getCreaterName());
            holder.tv_mission_title.setText(missionUserList.get(position).getTitle());
            holder.tv_mission_description.setText(missionUserList.get(position).getContent());
            holder.tv_mission_point.setText("奖励" + String.valueOf(missionUserList.get(position).getExchangePoint()) + "积分");
            return convertView;
        }
    }

    public List<MissionUser> updateData(final boolean isFirst) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                missionUserList1.clear();

                JSONObject result = null;
                try {
                    result = new JSONObject(Request2Server.getRequsetResult(HOST + "findMissionUser?isEnable=3&receiverId=" + userID));
                    if (result.getInt("code") > 0) {
                        JSONArray objs = result.getJSONArray("content");
                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject obj = (JSONObject) objs.get(i);
                            MissionUser missionUser = new MissionUser();

                            String id = obj.getString("id");
                            String createrId = obj.getString("createrId");
                            String receiverId = obj.getString("receiverId");
                            String exchangePoint = obj.getString("exchangePoint");
                            String isEnable = obj.getString("isEnable");
                            String finishTime = obj.getString("finishTime");

                            missionUser.setCreaterName(obj.getString("createrName"));
                            missionUser.setCreaterImg(obj.getString("createrImg"));
                            missionUser.setReceiverName(obj.getString("receiverName"));
                            missionUser.setReceiverImg(obj.getString("receiverImg"));
                            missionUser.setId(id.equals("null") ? null : Integer.valueOf(id));
                            missionUser.setCreaterId(createrId.equals("null") ? null : Integer.valueOf(createrId));
                            missionUser.setReceiverId(receiverId.equals("null") ? null : Integer.valueOf(receiverId));
                            missionUser.setCreateTime(SystemUtil.convert(obj.getString("createTime")));
                            missionUser.setFinishTime(SystemUtil.convert(finishTime));
                            missionUser.setTitle(obj.getString("title"));
                            missionUser.setContent(obj.getString("content"));
                            missionUser.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                            missionUser.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                            missionUser.setUpdateTime(SystemUtil.convert(obj.getString("updateTime")));
                            missionUser.setImg(obj.getString("img"));

                            missionUserList1.add(missionUser);
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = isFirst ? 0 : 1;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = isFirst ? 3 : 4;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    Message msg = handler.obtainMessage();
                    msg.what = isFirst ? 3 : 2;
                    handler.sendMessage(msg);
                    e.printStackTrace();
                }
            }
        }).start();
        return missionUserList1;
    }
}
