package com.choose.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.zjlloveo0.help.R;
import com.choose.XmlParserHandler;
import com.choose.model.CityModel;
import com.choose.model.ProvinceModel;
import com.choose.view.adapter.ArrayWheelAdapter;
import com.choose.view.myinterface.OnWheelChangedListener;
import com.choose.view.myinterface.SelectAddressInterface;

public class SelectAddressDialog implements OnClickListener,
        OnWheelChangedListener {
    private boolean isMyDatas;//是否自定义数据

    public static final int STYLE_ONE = 1;//一级联动
    public static final int STYLE_TWO = 2;//二级联动
    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

    /**
     * 当前省的postion
     */
    protected int mCurrentProviceNamePosition;
    /**
     * 当前市的postion
     */
    protected int mCurrentCityNamePosition;
    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private Button mBtnConfirm, mBtnCancel;
    private Activity context;
    private Dialog overdialog;
    private SelectAddressInterface selectAdd;
    private int tmp1, tmp2;
    private int type;

    public SelectAddressDialog(Activity context,
                               SelectAddressInterface selectAdd, int type, String[] mProvinceDatas) {
        this.selectAdd = selectAdd;
        this.type = type;
        // TODO Auto-generated constructor stub
        this.context = context;
        View overdiaView = View.inflate(context,
                R.layout.dialog_select_address, null);

        mViewProvince = (WheelView) overdiaView.findViewById(R.id.id_province);
        mViewCity = (WheelView) overdiaView.findViewById(R.id.id_city);
        if (STYLE_ONE == type) {
            mViewCity.setVisibility(View.GONE);
        }
        mBtnConfirm = (Button) overdiaView.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) overdiaView.findViewById(R.id.btn_cancel);
        overdialog = new Dialog(context, R.style.dialog_lhp);
        Window window = overdialog.getWindow();
        window.setWindowAnimations(R.style.mystyle); // 添加动画
        overdialog.setContentView(overdiaView);
        overdialog.setCanceledOnTouchOutside(false);
        setUpListener();
        if (mProvinceDatas == null) {
            setUpData();
            isMyDatas = false;
        } else {
            isMyDatas = true;
            this.mProvinceDatas = mProvinceDatas;
            mCurrentProviceName = mProvinceDatas[0];
            mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context,
                    this.mProvinceDatas));
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewCity.setVisibleItems(7);
        }

    }

//	public void initDate(){
//		if(mViewProvince!=null)mViewProvince.setCurrentItem(mCurrentProviceNamePosition);
//		if(mViewCity!=null)mViewCity.setCurrentItem(mCurrentCityNamePosition);
//		if(mViewDistrict!=null)mViewDistrict.setCurrentItem(mCurrentDistrictNamePosition);
//	}

    public void showDialog() {

        if (overdialog != null) {
            if (mViewProvince != null) mViewProvince.setCurrentItem(mCurrentProviceNamePosition);
            if (mViewCity != null) mViewCity.setCurrentItem(mCurrentCityNamePosition);
            overdialog.show();
            Window win = overdialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setGravity(Gravity.BOTTOM);
            win.setAttributes(lp);
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[0];
        } else if (wheel == mViewCity) {
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[newValue];
        }
    }


    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        tmp1 = pCurrent;
        mCurrentProviceName = mProvinceDatas[pCurrent];
        if (!isMyDatas) {//不是自定义数据
            String[] cities = mCitisDatasMap.get(mCurrentProviceName);
            if (cities == null) {
                cities = new String[]{""};
            }
            mViewCity
                    .setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
            mViewCity.setCurrentItem(0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (type == STYLE_TWO) {
                    selectAdd.setAreaString(mCurrentProviceName + "-" + mCurrentCityName);
                } else if (type == STYLE_ONE) {
                    selectAdd.setAreaString(mCurrentProviceName);
                }
                mCurrentProviceNamePosition = tmp1;
                mCurrentCityNamePosition = tmp2;
//			ToastUtil.show(context,mCurrentCityNamePosition+":"+mCurrentCityNamePosition+":"+mCurrentDistrictNamePosition);
                overdialog.cancel();
                break;

            case R.id.btn_cancel:
                overdialog.cancel();
                break;
            default:
                break;
        }
    }

    private void showSelectedResult() {
        Toast.makeText(context, "当前选中:" + mCurrentProviceName + "," + mCurrentCityName, Toast.LENGTH_SHORT).show();
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context,
                mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        updateCities();
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
        // 添加onclick事件
        mBtnCancel.setOnClickListener(this);
    }

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            // */ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                }
            }
            // */
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }

}
