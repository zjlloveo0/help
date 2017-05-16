package com.zjlloveo0.help.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.activity.MissionOrdersDetailActivity;
import com.zjlloveo0.help.bean.Mission;
import com.zjlloveo0.help.other.CustomRefreshListView;
import com.zjlloveo0.help.utils.Request2Server;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.SystemUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MissionOrdersUsedFragment extends Fragment {
    private View mRootView;
    private CustomRefreshListView lv_missionOrdersUsedList;
    private LinearLayout ll_no_data;
    private List<Mission> mList = new ArrayList<Mission>();
    private List<Mission> mList1 = new ArrayList<Mission>();
    private myAdapter adapter = new myAdapter();
    private String HOST = SYSVALUE.HOST;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ll_no_data.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(mList1);
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    ll_no_data.setVisibility(View.GONE);
                    mList.clear();
                    mList.addAll(mList1);
                    adapter.notifyDataSetChanged();
                    lv_missionOrdersUsedList.completeRefresh();
                    break;
                case 2:
                    lv_missionOrdersUsedList.failRefresh();
                    break;
                case 3:
                    mList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    mList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    lv_missionOrdersUsedList.noResRefresh();
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "MissionOrdersUsedFragment");
            updateData(true);
            mRootView = inflater.inflate(R.layout.tab_mission_used, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        ll_no_data = (LinearLayout) mRootView.findViewById(R.id.ll_no_data);
        TextView tv_no_data_server = (TextView) mRootView.findViewById(R.id.tv_no_data_mission);
        tv_no_data_server.setText("你还没有帮助别人的订单，去帮帮别人吧~");
        ll_no_data.setVisibility(View.GONE);
        lv_missionOrdersUsedList = (CustomRefreshListView) mRootView.findViewById(R.id.lv_missionOrdersUsedList);
        lv_missionOrdersUsedList.setAdapter(adapter);
        lv_missionOrdersUsedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                Mission mission = viewHolder.mission;
                Intent intent = new Intent(getContext(), MissionOrdersDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("missionId", mission.getId() + "");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        lv_missionOrdersUsedList.setOnRefreshListener(new CustomRefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                updateData(false);
            }

            @Override
            public void onLoadingMore() {
                Toast.makeText(getContext(), "加载", Toast.LENGTH_SHORT).show();
            }
        });
        return mRootView;
    }

    public class ViewHolder {
        SmartImageView iv_used_image;
        TextView tv_used_title;
        TextView tv_used_state;
        TextView tv_used_point;
        TextView tv_used_time;
        Mission mission;
    }

    public class myAdapter extends BaseAdapter {

        public myAdapter() {

        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.tab_mission_items, null);
                holder = new ViewHolder();
                holder.iv_used_image = (SmartImageView) convertView.findViewById(R.id.iv_used_image);
                holder.tv_used_title = (TextView) convertView.findViewById(R.id.tv_used_title);
                holder.tv_used_state = (TextView) convertView.findViewById(R.id.tv_used_state);
                holder.tv_used_point = (TextView) convertView.findViewById(R.id.tv_used_point);
                holder.tv_used_time = (TextView) convertView.findViewById(R.id.tv_used_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mission = mList.get(position);
            holder.iv_used_image.setImageUrl(HOST + mList.get(position).getImg());
            holder.tv_used_title.setText(mList.get(position).getTitle());
            holder.tv_used_state.setText(SystemUtil.getMissionStateString(mList.get(position).getState()));
            holder.tv_used_point.setText("奖励积分：" + mList.get(position).getExchangePoint());
            holder.tv_used_time.setText(SystemUtil.formatDate(mList.get(position).getUpdateTime()));
            return convertView;
        }
    }

    public List<Mission> updateData(final boolean isFirst) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mList1.clear();
                JSONObject result = null;
                try {
                    /*

                     */
                    result = new JSONObject(Request2Server.getRequsetResult(HOST + "findMission?isEnable=1&receiverId=" + SYSVALUE.currentUser.getId()));
                    if (result.getInt("code") > 0) {
                        JSONArray objs = result.getJSONArray("content");
                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject obj = (JSONObject) objs.get(i);
                            Mission mission = new Mission();
                            String id = obj.getString("id");
                            String img = obj.getString("img");
                            String title = obj.getString("title");
                            String content = obj.getString("content");
                            String createrId = obj.getString("createrId");
                            String receiverId = obj.getString("receiverId");
                            String state = obj.getString("state");
                            String exchangePoint = obj.getString("exchangePoint");
                            String isEnable = obj.getString("isEnable");
                            String createTime = obj.getString("createTime");
                            String finishTime = obj.getString("finishTime");
                            String updateTime = obj.getString("updateTime");

                            mission.setId(id.equals("null") ? null : Integer.valueOf(id));
                            mission.setImg(img);
                            mission.setTitle(title);
                            mission.setContent(content);
                            mission.setCreaterId(createrId.equals("null") ? null : Integer.valueOf(createrId));
                            mission.setReceiverId(receiverId.equals("null") ? null : Integer.valueOf(receiverId));
                            mission.setState(state.equals("null") ? null : Integer.valueOf(state));
                            mission.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                            mission.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                            mission.setCreateTime(SystemUtil.convert(createTime));
                            mission.setFinishTime(SystemUtil.convert(finishTime));
                            mission.setUpdateTime(SystemUtil.convert(updateTime));

                            mList1.add(mission);
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
        return mList1;
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        Log.v("666", "Server onHiddenChanged" + hidden);
//        updateData();
//    }
}
