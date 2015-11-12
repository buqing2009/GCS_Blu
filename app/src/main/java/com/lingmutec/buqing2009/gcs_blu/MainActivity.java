package com.lingmutec.buqing2009.gcs_blu;

import android.app.Activity;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;



/**
 * Created by buqing2009 on 15-11-11.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private MapView mMapView = null;
    private BaiduMap bdMap;
//    private MapController mMapController = null;
//    private Toast mToast=null;
//    private BMapManager mBMapManager=null;
    private boolean firstLocation;
    private BitmapDescriptor mCurrentMarker;
    private MyLocationConfiguration config;
    private LocationClient mLocationClient = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        bdMap = mMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15f);
        bdMap.setMapStatus(msu);

        // 定位初始化
        mLocationClient = new LocationClient(this);
        firstLocation =true;

        // 设置定位的相关配置
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setOpenGps(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);

        BitmapDescriptor myMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.download);
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, true, myMarker);

        Button btn_sel_map_type = (Button)findViewById(R.id.mtype_select_bottom);
        Button btn_locate_map = (Button)findViewById(R.id.mlocate_bottom);

        btn_sel_map_type.setOnClickListener(this);
        btn_locate_map.setOnClickListener(this);

        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation location) {
                // map view 销毁后不在处理新接收的位置
                if (location == null || mMapView == null)
                    return;
                // 构造定位数据
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                                // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(100).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                // 设置定位数据
                bdMap.setMyLocationData(locData);

                // 第一次定位时，将地图位置移动到当前位置
                if (firstLocation) {
                    firstLocation = false;
                    LatLng xy = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(xy);
                    bdMap.animateMapStatus(status);
                }
            }
        });
    }
    @Override
    protected void onStart()
    {
        // 如果要显示位置图标,必须先开启图层定位
        bdMap.setMyLocationEnabled(true);
        super.onStart();
    }
    @Override
    protected void onStop()
    {
        // 关闭图层定位
        bdMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    protected void selectMap(){
        if(bdMap.getMapType()==BaiduMap.MAP_TYPE_NORMAL){
            bdMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }else{
            bdMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }
    }

    protected void blu_locate(){
        if (!mLocationClient.isStarted())
        {
            mLocationClient.start();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mtype_select_bottom:
                selectMap();
                break;
            case R.id.mlocate_bottom:
                blu_locate();
                break;
        }
    }
}

