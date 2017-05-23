package com.zjlloveo0.help.location.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.zjlloveo0.help.R;
import com.zjlloveo0.help.location.helper.NimGeocoder;
import com.zjlloveo0.help.location.model.NimLocation;
import com.netease.nim.uikit.LocationProvider;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.model.ToolBarOptions;

public class LocationAmapActivity extends UI implements OnCameraChangeListener, OnClickListener, AMapLocationListener {


    private TextView sendButton;
    private ImageView pinView;
    private View pinInfoPanel;
    private TextView pinInfoTextView;

    private double latitude; // 经度
    private double longitude; // 维度
    private String addressInfo; // 对应的地址信息

    private static LocationProvider.Callback callback;

    private double cacheLatitude = -1;
    private double cacheLongitude = -1;
    private String cacheAddressInfo;

    private boolean locating = true; // 正在定位的时候不用去查位置
    private NimGeocoder geocoder;

    AMap amap;
    private MapView mapView;
    private Button btnMyLocation;
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    public static void start(Context context, LocationProvider.Callback callback) {
        LocationAmapActivity.callback = callback;
        context.startActivity(new Intent(context, LocationAmapActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view_amap_layout);
        mapView = (MapView) findViewById(R.id.autonavi_mapView);
        mapView.onCreate(savedInstanceState);// 此方法必须重写

        ToolBarOptions options = new ToolBarOptions();
        setToolBar(R.id.toolbar, options);

        initView();
        initAmap();
        initLocation();
        updateSendStatus();

    }

    private void initView() {
        sendButton = findView(R.id.action_bar_right_clickable_textview);
        sendButton.setText(R.string.send);
        sendButton.setOnClickListener(this);
        sendButton.setVisibility(View.INVISIBLE);

        pinView = (ImageView) findViewById(R.id.location_pin);
        pinInfoPanel = findViewById(R.id.location_info);
        pinInfoTextView = (TextView) pinInfoPanel.findViewById(R.id.marker_address);

        pinView.setOnClickListener(this);
        pinInfoPanel.setOnClickListener(this);


        btnMyLocation = (Button) findViewById(R.id.my_location);
        btnMyLocation.setOnClickListener(this);
        btnMyLocation.setVisibility(View.GONE);
    }

    private void initAmap() {
        try {
            amap = mapView.getMap();
            amap.setOnCameraChangeListener(this);

            UiSettings uiSettings = amap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        locationClient.setLocationOption(locationOption);
        locationClient.setLocationListener(this);
        locationClient.startLocation();
        Location location = locationClient.getLastKnownLocation();
        Intent intent = getIntent();
        float zoomLevel = intent.getIntExtra(LocationExtras.ZOOM_LEVEL, LocationExtras.DEFAULT_ZOOM_LEVEL);

        LatLng latlng = null;
        if (location == null) {
            latlng = new LatLng(39.90923, 116.397428);
        } else {
            latlng = new LatLng(location.getLatitude(), location.getLongitude());
        }
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, zoomLevel, 0, 0));
        amap.moveCamera(camera);
        geocoder = new NimGeocoder(this, geocoderListener);
    }

    private void updateSendStatus() {
        if (isFinishing()) {
            return;
        }
        int titleResID = R.string.location_map;
        if (TextUtils.isEmpty(addressInfo)) {
            titleResID = R.string.location_loading;
            sendButton.setVisibility(View.GONE);
        } else {
            sendButton.setVisibility(View.VISIBLE);
        }
        if (btnMyLocation.getVisibility() == View.VISIBLE || Math.abs(-1 - cacheLatitude) < 0.1f) {
            setTitle(titleResID);
        } else {
            setTitle(R.string.my_location);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        callback = null;
    }

    private String getStaticMapUrl() {
        StringBuilder urlBuilder = new StringBuilder(LocationExtras.STATIC_MAP_URL_1);
        urlBuilder.append(latitude);
        urlBuilder.append(",");
        urlBuilder.append(longitude);
        urlBuilder.append(LocationExtras.STATIC_MAP_URL_2);
        return urlBuilder.toString();
    }

    private void sendLocation() {
        Intent intent = new Intent();
        intent.putExtra(LocationExtras.LATITUDE, latitude);
        intent.putExtra(LocationExtras.LONGITUDE, longitude);
        addressInfo = TextUtils.isEmpty(addressInfo) ? getString(R.string.location_address_unkown) : addressInfo;
        intent.putExtra(LocationExtras.ADDRESS, addressInfo);
        intent.putExtra(LocationExtras.ZOOM_LEVEL, amap.getCameraPosition().zoom);
        intent.putExtra(LocationExtras.IMG_URL, getStaticMapUrl());

        if (callback != null) {
            callback.onSuccess(longitude, latitude, addressInfo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bar_right_clickable_textview:
                sendLocation();
                finish();
                break;
            case R.id.location_pin:
                setPinInfoPanel(!isPinInfoPanelShow());
                break;
            case R.id.location_info:
                pinInfoPanel.setVisibility(View.GONE);
                break;
            case R.id.my_location:
                locationAddressInfo(cacheLatitude, cacheLongitude, cacheAddressInfo);
                break;
        }
    }

    private void locationAddressInfo(double lat, double lng, String address) {
        LatLng latlng = new LatLng(lat, lng);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, amap.getCameraPosition().zoom, 0, 0));
        amap.moveCamera(camera);
        addressInfo = address;
        latitude = lat;
        longitude = lng;

        setPinInfoPanel(true);
    }

    private boolean isPinInfoPanelShow() {
        return pinInfoPanel.getVisibility() == View.VISIBLE;
    }

    private void setPinInfoPanel(boolean show) {
        if (show && !TextUtils.isEmpty(addressInfo)) {
            pinInfoPanel.setVisibility(View.VISIBLE);
            pinInfoTextView.setText(addressInfo);
        } else {
            pinInfoPanel.setVisibility(View.GONE);
        }
        updateSendStatus();
    }


    @Override
    public void onCameraChange(CameraPosition arg0) {
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (!locating) {
            queryLatLngAddress(cameraPosition.target);
        } else {
            latitude = cameraPosition.target.latitude;
            longitude = cameraPosition.target.longitude;
        }
        updateMyLocationStatus(cameraPosition);
    }

    private void updateMyLocationStatus(CameraPosition cameraPosition) {
        if (Math.abs(-1 - cacheLatitude) < 0.1f) {
            // 定位失败
            return;
        }
        LatLng source = new LatLng(cacheLatitude, cacheLongitude);
        LatLng target = cameraPosition.target;
        float distance = AMapUtils.calculateLineDistance(source, target);
        boolean showMyLocation = distance > 50;
        btnMyLocation.setVisibility(showMyLocation ? View.VISIBLE : View.GONE);
        updateSendStatus();
    }

    private void queryLatLngAddress(LatLng latlng) {
        if (!TextUtils.isEmpty(addressInfo) && latlng.latitude == latitude && latlng.longitude == longitude) {
            return;
        }

        Handler handler = getHandler();
        handler.removeCallbacks(runable);
        handler.postDelayed(runable, 20 * 1000);// 20s超时
        geocoder.queryAddressNow(latlng.latitude, latlng.longitude);

        latitude = latlng.latitude;
        longitude = latlng.longitude;

        this.addressInfo = null;
        setPinInfoPanel(false);
    }

    private void clearTimeoutHandler() {
        Handler handler = getHandler();
        handler.removeCallbacks(runable);
    }

    private NimGeocoder.NimGeocoderListener geocoderListener = new NimGeocoder.NimGeocoderListener() {
        @Override
        public void onGeoCoderResult(NimLocation location) {
            if (latitude == location.getLatitude() && longitude == location.getLongitude()) { // 响应的是当前查询经纬度
                if (location.hasAddress()) {
                    LocationAmapActivity.this.addressInfo = location.getFullAddr();
                } else {
                    addressInfo = getString(R.string.location_address_unkown);
                }
                setPinInfoPanel(true);
                clearTimeoutHandler();
            }
        }
    };


    private Runnable runable = new Runnable() {
        @Override
        public void run() {
            LocationAmapActivity.this.addressInfo = getString(R.string.location_address_unkown);
            setPinInfoPanel(true);
        }
    };

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (location != null) {
            cacheLatitude = location.getLatitude();
            cacheLongitude = location.getLongitude();
            cacheAddressInfo = location.getAddress();

            if (locating) {
                locating = false;
                locationAddressInfo(cacheLatitude, cacheLongitude, cacheAddressInfo);
            }
        }
        updateSendStatus();
        locationClient.stopLocation();
        if (null != location) {

            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition(latlng, 15, 0, 0));
                amap.moveCamera(camera);
                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + location.getTime() + "\n");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            //定位之后的回调时间
            sb.append("回调时间: " + System.currentTimeMillis() + "\n");

            //解析定位结果，
            String result = sb.toString();
            System.out.println(result);
        } else {
            System.out.println("定位失败，loc is null");
        }
    }
}
