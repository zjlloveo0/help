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

import com.zjlloveo0.help.R;

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
    private Class mClass[] = {HomeFragment.class, ReportFragment.class, MessageFragment.class, MineFragment.class};
    private Fragment mFragment[] = {new HomeFragment(), new ReportFragment(), new MessageFragment(), new MineFragment()};
    private String mTitles[] = {"首页", "报表", "消息", "我的"};
    private int mImages[] = {
            R.drawable.tab_home,
            R.drawable.tab_report,
            R.drawable.tab_message,
            R.drawable.tab_mine
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                snackBar= Snackbar.make(view,"", Snackbar.LENGTH_LONG);
                addViewToSnackBar(snackBar,R.layout.snackbar_new_mission,0);
                snackBar.show();
            }
        });

        init();
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


    //tab TODO:tab
    private void init() {

        initView();

        initEvent();
    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

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
