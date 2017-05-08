package com.zjlloveo0.help.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.recent.RecentContactsFragment;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.MissionListFragment;
import com.zjlloveo0.help.fragment.ServerListFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button bt_add_service;
    private Button bt_add_mission;
    private Snackbar snackBar;

    //tab
    private FragmentTabHost mTabHost;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList;
    private Class mClass[] = {ServerListFragment.class, MissionListFragment.class, RecentContactsFragment.class, MineFragment.class};
    private Fragment mFragment[] = {new ServerListFragment(), new MissionListFragment(), new RecentContactsFragment(), new MineFragment()};
    private String mTitles[] = {"找服务", "去帮忙", "消息", "我的"};
    private int mImages[] = {
            R.drawable.tab_service,
            R.drawable.tab_mission,
            R.drawable.tab_message,
            R.drawable.tab_mine
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        bt_add_service=(Button)findViewById(R.id.bt_add_service);
        bt_add_mission=(Button)findViewById(R.id.bt_add_mission);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBar = Snackbar.make(view, "", Snackbar.LENGTH_SHORT);
                addViewToSnackBar(snackBar,R.layout.snackbar_new_mission,0);
                setSnackBarColor(snackBar, Color.alpha(0));
                snackBar.show();
            }
        });

        init();
        test();
    }

    public void test() {
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        Toast.makeText(getApplicationContext(), messages.get(0).getContent(), Toast.LENGTH_SHORT).show();
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }
    public void setSnackBarColor(Snackbar snackbar, int backgroundColor) {
        View view = snackbar.getView();
        if (view != null) {
            view.setBackgroundColor(backgroundColor);
        }
    }
    public void addViewToSnackBar(Snackbar snackbar, int layoutId, int index) {
        View snackBarView = snackbar.getView();
        Snackbar.SnackbarLayout snackBarLayout=(Snackbar.SnackbarLayout)snackBarView;
        View add_view = LayoutInflater.from(snackBarView.getContext()).inflate(layoutId,null);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        p.gravity= Gravity.CENTER_VERTICAL;
        snackBarLayout.addView(add_view,index,p);
    }
    public void addService(View v){
        snackBar.dismiss();
        Toast.makeText(getApplicationContext(),"1111",Toast.LENGTH_SHORT).show();
    }
    public void addMission(View v){
        snackBar.dismiss();
        Toast.makeText(getApplicationContext(),"2222",Toast.LENGTH_SHORT).show();
    }

    public void sendMsg(View v) {
        // 创建文本消息
        IMMessage message = MessageBuilder.createTextMessage(
                "zjlloveo0", // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                "你好！" // 文本内容
        );
        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(MsgService.class).sendMessage(message, false);
    }

    //tab TODO:tab
    private void init() {

        initView();

        initEvent();
    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOffscreenPageLimit(4);

        mFragmentList = new ArrayList<Fragment>();
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        mTabHost.getTabWidget().setDividerDrawable(null);

        for (int i = 0; i < mFragment.length; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTitles[i]).setIndicator(getTabView(i));
            mTabHost.addTab(tabSpec, mClass[i], null);
            mFragmentList.add(mFragment[i]);
            mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
        }

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
    }

    private View getTabView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView title = (TextView) view.findViewById(R.id.title);

        image.setImageResource(mImages[index]);
        title.setText(mTitles[index]);

        return view;
    }

    private void initEvent() {

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mViewPager.setCurrentItem(mTabHost.getCurrentTab());
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabHost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

    }

}
