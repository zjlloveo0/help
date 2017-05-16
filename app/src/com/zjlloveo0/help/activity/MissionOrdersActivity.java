package com.zjlloveo0.help.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.zjlloveo0.help.R;
import com.zjlloveo0.help.fragment.MissionOrdersCreateFragment;
import com.zjlloveo0.help.fragment.MissionOrdersUsedFragment;

import java.util.ArrayList;
import java.util.List;

public class MissionOrdersActivity extends FragmentActivity {
    private List<Fragment> mFragmentList;
    private Fragment mFragment[] = {new MissionOrdersUsedFragment(), new MissionOrdersCreateFragment()};
    private TextView tv_mission_used, tv_mission_create;
    private View v_line_used, v_line_create;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_orders);
        findView();
        initVPager();
    }

    private void findView() {
        viewPager = (ViewPager) findViewById(R.id.vp_mission);
        tv_mission_used = (TextView) findViewById(R.id.tv_mission_used);
        tv_mission_create = (TextView) findViewById(R.id.tv_mission_create);
        v_line_used = findViewById(R.id.v_line_used);
        v_line_create = findViewById(R.id.v_line_create);
    }

    public void onClickUsed(View view) {
        viewPager.setCurrentItem(0);
    }

    public void onClickBuy(View view) {
        viewPager.setCurrentItem(1);
    }

    private void initVPager() {
        mFragmentList = new ArrayList<Fragment>();
        for (int i = 0; i < mFragment.length; i++) {
            mFragmentList.add(mFragment[i]);
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        });
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_mission_used.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    v_line_used.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    tv_mission_create.setTextColor(Color.BLACK);
                    v_line_create.setBackgroundColor(Color.WHITE);
                } else if (position == 1) {
                    tv_mission_used.setTextColor(Color.BLACK);
                    v_line_used.setBackgroundColor(Color.WHITE);
                    tv_mission_create.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                    v_line_create.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void back(View view) {
        finish();
    }
}
