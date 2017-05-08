package com.zjlloveo0.help.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.activity.ServerDetailActivity;
import com.zjlloveo0.help.model.ServerUser;
import com.zjlloveo0.help.utils.SYSVALUE;
import com.zjlloveo0.help.utils.Request2Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerListFragment extends Fragment {
    private View mRootView;
    ListView lv_ServerList;
    List<ServerUser> serverUserList = new ArrayList<ServerUser>();
    List<ServerUser> serverUserList1 = new ArrayList<ServerUser>();
    myAdapter adapter = new myAdapter();
    String HOST = SYSVALUE.HOST;
    public final static String PAR_KEY = "com.zjlloveo0.help.model.ServerUser";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getContext(), "添加成功!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    serverUserList.clear();
                    serverUserList.addAll(serverUserList1);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        updateData();
        if (mRootView == null) {
            Log.e("666", "ServerListFragment");
            mRootView = inflater.inflate(R.layout.serverlist_fragment, container, false);
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }

        lv_ServerList = (ListView) mRootView.findViewById(R.id.lv_serverList);
        lv_ServerList.setAdapter(adapter);
        lv_ServerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                ServerUser su = viewHolder.serverUser;
                Intent intent = new Intent(getContext(), ServerDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable(PAR_KEY, su);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        return mRootView;
    }

    public class ViewHolder {
        SmartImageView iv_image;
        TextView tv_username;
        TextView tv_title;
        TextView tv_description;
        TextView tv_point;
        ServerUser serverUser;
    }

    public class myAdapter extends BaseAdapter {

        public myAdapter() {

        }

        @Override
        public int getCount() {
            return serverUserList.size();
        }

        @Override
        public Object getItem(int position) {
            return serverUserList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getContext(), R.layout.serverlist_items, null);
                holder = new ViewHolder();
                holder.iv_image = (SmartImageView) convertView.findViewById(R.id.iv_image);
                holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_description = (TextView) convertView.findViewById(R.id.tv_descript);
                holder.tv_point = (TextView) convertView.findViewById(R.id.tv_point);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.serverUser = serverUserList.get(position);
            holder.iv_image.setImageUrl(HOST + serverUserList.get(position).getImg());
            holder.tv_username.setText(serverUserList.get(position).getCreaterName());
            holder.tv_title.setText(serverUserList.get(position).getTitle());
            holder.tv_description.setText(serverUserList.get(position).getContent());
            holder.tv_point.setText(serverUserList.get(position).getExchangePoint() + "积分/次");
            return convertView;
        }
    }

    public List<ServerUser> updateData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverUserList1.clear();

                JSONObject result = null;
                try {
                    result = new JSONObject(Request2Server.getRequsetResult(HOST + "findServerUser?isEnable=1"));
                    if (result.getInt("code") > 0) {
                        JSONArray objs = result.getJSONArray("content");
                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject obj = (JSONObject) objs.get(i);
                            ServerUser serverUser = new ServerUser();
                            String id = obj.getString("id");
                            String createrId = obj.getString("createrId");
                            String exchangePoint = obj.getString("exchangePoint");
                            String createrPoint = obj.getString("createrPoint");
                            String isEnable = obj.getString("isEnable");

                            serverUser.setCreaterName(obj.getString("createrName"));
                            serverUser.setCreaterImg(obj.getString("createrImg"));
                            serverUser.setId(id.equals("null") ? null : Integer.valueOf(id));
                            serverUser.setCreaterId(createrId.equals("null") ? null : Integer.valueOf(createrId));
                            serverUser.setCreateTime(new Date(obj.getString("createTime")));
                            serverUser.setTitle(obj.getString("title"));
                            serverUser.setContent(obj.getString("content"));
                            serverUser.setExchangePoint(exchangePoint.equals("null") ? null : Integer.valueOf(exchangePoint));
                            serverUser.setCreaterPoint(createrPoint.equals("null") ? null : Integer.valueOf(createrPoint));
                            serverUser.setIsEnable(isEnable.equals("null") ? null : Integer.valueOf(isEnable));
                            serverUser.setUpdateTime(new Date(obj.getString("updateTime")));
                            serverUser.setImg(obj.getString("img"));

                            serverUserList1.add(serverUser);
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return serverUserList1;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.v("666", "Server onHiddenChanged" + hidden);
        updateData();
    }
}
