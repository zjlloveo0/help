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
import com.zjlloveo0.help.activity.ServerOrdersDetailActivity;
import com.zjlloveo0.help.bean.OrdersList;
import com.zjlloveo0.help.bean.ServerUser;
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

public class ServerOrdersUsedFragment extends Fragment {
    private View mRootView;
    private CustomRefreshListView lv_serverOrdersUsedList;
    private LinearLayout ll_no_data;
    private TextView tv_no_data_server;
    private List<OrdersList> oList = new ArrayList<OrdersList>();
    private List<OrdersList> oList1 = new ArrayList<OrdersList>();
    private myAdapter adapter = new myAdapter();
    private String HOST = SYSVALUE.HOST;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ll_no_data.setVisibility(View.GONE);
                    oList.clear();
                    oList.addAll(oList1);
                    adapter.notifyDataSetChanged();
                    break;
                case 1:
                    ll_no_data.setVisibility(View.GONE);
                    oList.clear();
                    oList.addAll(oList1);
                    adapter.notifyDataSetChanged();
                    lv_serverOrdersUsedList.completeRefresh();
                    break;
                case 2:
                    lv_serverOrdersUsedList.failRefresh();
                    break;
                case 3:
                    oList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    oList.clear();
                    adapter.notifyDataSetChanged();
                    ll_no_data.setVisibility(View.VISIBLE);
                    lv_serverOrdersUsedList.noResRefresh();
                    break;

            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.e("666", "ServerOrdersUsedFragment");
            updateData(true);
            mRootView = inflater.inflate(R.layout.tab_server_used, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        ll_no_data = (LinearLayout) mRootView.findViewById(R.id.ll_no_data);
        tv_no_data_server = (TextView) mRootView.findViewById(R.id.tv_no_data_server);
        tv_no_data_server.setText("还没有使用服务的订单，去用用合适的服务吧");
        ll_no_data.setVisibility(View.GONE);
        lv_serverOrdersUsedList = (CustomRefreshListView) mRootView.findViewById(R.id.lv_serverOrdersUsedList);
        lv_serverOrdersUsedList.setAdapter(adapter);
        lv_serverOrdersUsedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                OrdersList order = viewHolder.ordersList;
                Intent intent = new Intent(getContext(), ServerOrdersDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("orderId", order.getId() + "");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        lv_serverOrdersUsedList.setOnRefreshListener(new CustomRefreshListView.OnRefreshListener() {
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
        OrdersList ordersList;
    }

    public class myAdapter extends BaseAdapter {

        public myAdapter() {

        }

        @Override
        public int getCount() {
            return oList.size();
        }

        @Override
        public Object getItem(int position) {
            return oList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.tab_server_items, null);
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
            holder.ordersList = oList.get(position);
            holder.iv_used_image.setImageUrl(HOST + oList.get(position).getImg());
            holder.tv_used_title.setText(oList.get(position).getTitle());
            holder.tv_used_state.setText(SystemUtil.getOrderStateString(oList.get(position).getState()));
            holder.tv_used_point.setText("消耗积分：" + oList.get(position).getExchangePoint());
            holder.tv_used_time.setText(SystemUtil.formatDate(oList.get(position).getUpdateTime()));
            return convertView;
        }
    }

    public List<OrdersList> updateData(final boolean isFirst) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                oList1.clear();
                JSONObject result = null;
                try {
                    result = new JSONObject(Request2Server.getRequsetResult(HOST + "findOrders?isEnable=1&createId=" + SYSVALUE.currentUser.getId()));
                    if (result.getInt("code") > 0) {
                        JSONArray objs = result.getJSONArray("content");
                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject obj = (JSONObject) objs.get(i);
                            OrdersList orders = new OrdersList();
                            String id = obj.getString("id");
                            String img = obj.getString("img");
                            String title = obj.getString("title");
                            String reMsg = obj.getString("reMsg");
                            String serverId = obj.getString("serverId");
                            String message = obj.getString("message");
                            String createId = obj.getString("createId");
                            String uId = obj.getString("uId");
                            String state = obj.getString("state");
                            String exchangePoint = obj.getString("exchangePoint");
                            String isEnable = obj.getString("isEnable");
                            String updateTime = obj.getString("updateTime");

                            orders.setId(id.equals("null") ? null : Integer.valueOf(id));
                            orders.setImg(img);
                            orders.setTitle(title);
                            orders.setReMsg(reMsg);
                            orders.setServerId(serverId.equals("null") ? null : Integer.valueOf(serverId));
                            orders.setMessage(message);
                            orders.setCreateId(createId.equals("null") ? null : Integer.valueOf(createId));
                            orders.setUId(uId.equals("null") ? null : Integer.valueOf(uId));
                            orders.setState(state.equals("null") ? null : Integer.valueOf(state));
                            orders.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                            orders.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                            orders.setUpdateTime(SystemUtil.convert(updateTime));

                            oList1.add(orders);
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
        return oList1;
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        Log.v("666", "Server onHiddenChanged" + hidden);
//        updateData();
//    }
}
